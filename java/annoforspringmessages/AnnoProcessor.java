package blz.server.utils.anno;

import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;
import org.springframework.context.ApplicationContext;
import org.springframework.validation.Validator;
import org.springframework.web.servlet.mvc.AbstractController;
import org.springframework.web.servlet.mvc.BaseCommandController;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.*;
import java.util.logging.Logger;

/**
 * ����� ����������� ������������ �������� spring, ���� � ��� ��� ����
 * ���������� ��� ���������� ��� ���� ����������� ��������
 * �� ������ ������ �� ����������� �������� �� ������ ����������� ����� BaseCommandController,
 * �.�. ����� �� ��� ����� ����������� � ��� ����������
 * ����� � �������� ����� ������ ������ ���������� �������� � �������� �����������
 * � ��� ������ �� ����� �������� �������� �� ���� ������ ��������� ������ ���������
 */
public class AnnoProcessor {

    private static Logger logger = Logger.getLogger(AnnoProcessor.class.getName());


    private static HashMap<String, HashSet<String>> declaredResources = new HashMap<String, HashSet<String>>();
    private static List<Troika> rezBads = new ArrayList<Troika>();
    private static List<Troika> rezGoods = new ArrayList<Troika>();


    /**
     * ������� ������� ������. ��������� ��� �������� �� ������������ ��������� spring.
     * ����� ����������� html-�����
     *
     * @param context                ������ �� �������� spring
     * @param saveToFileName         ��� �����, ���� ����� ��������� ��������� �������
     * @param useContext             ������� ���� ����� �� ������������ �������� �������� �� ���������
     * @param pathToBaseResourcesDir � ������ ���� �������� �������� ����������� �� �� ���������,
     *                               � ��������������, �� ���� �������� ��������� ���� � �������� ������������ �������� ������ �������������
     *                               ��� ������ �������
     * @return ������� ����, ������� �� ��� ������������ ����� � ������������ �������
     */
    public synchronized static boolean runScanner(ApplicationContext context, File saveToFileName, Boolean useContext, File pathToBaseResourcesDir) {
        logger.info("AnnoProcessor.runScanner preparing");
        prepareScanner();

        // ����� �������� ��������, ����� ���� � ��� �� ��������� ������������ ����������� �������������
        HashSet<Class> usedValidators = new HashSet<Class>();
        // ���� ��� ���� ������� ��� controller
        final Map mapControllers = context.getBeansOfType(AbstractController.class);
        logger.info("AnnoProcessor.runScanner got mapControllers = " + mapControllers);

        // ������ ���������� �� ��� ����, ��� ������� ���������� ��������� ��� ���
        final Set setNamesOfControllers = mapControllers.keySet();
        for (Object nameController : setNamesOfControllers) {
            AbstractController absController = (AbstractController) mapControllers.get(nameController);
            // � ���� ���������� ����� ��� BaseCommandController ��� ����������� �� ����
            if (!(absController instanceof BaseCommandController)) continue;
            BaseCommandController comController = (BaseCommandController) absController;
            // ������ ������ ������ ����������� �������� � ������� ���������� ��� �������
            if (comController.getValidators() != null)
                for (Validator validator : comController.getValidators()) {
                    // ����� ������ ��������� � ��������� ������������
                    // ������ ������� �������� �� �������
                    if (usedValidators.contains(validator.getClass())) continue;
                    findAnnotations(validator, context, useContext, pathToBaseResourcesDir);
                    usedValidators.add(validator.getClass());
                }
        }

        // ������ ��������� ���������� � ���� ��������� bean-��
        final Map mapValidators = context.getBeansOfType(Validator.class);
        logger.info("AnnoProcessor.runScanner got mapValidators = " + mapValidators);
        for (Object nameValidator : mapValidators.keySet()) {
            Validator validator = (Validator) mapValidators.get(nameValidator);
            // ����� �� �������� ������� �������� �� �������
            if (usedValidators.contains(validator.getClass())) continue;
            findAnnotations(validator, context, useContext, pathToBaseResourcesDir);
            usedValidators.add(validator.getClass());
        }

        // ����� ���������� ������������ �������� � ������������ ��������, ����� ����� ������� html-�����
        try {
            logger.info("AnnoProcessor.runScanner try generate HTML report");
            generateHTMLFile(saveToFileName);
            return rezBads.size() == 0;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }


    /**
     * ��������������� �������. ������ ��� ��������� ������� Locale �� �����
     *
     * @param strNamesForLocale ��� ������, �������� ������� �� �����, ����, ���� ���������
     * @return ������ Locale
     */
    private static synchronized Locale getLocaleByName(String strNamesForLocale) {
        String lang = strNamesForLocale;
        String country = null;
        String variant = null;
        int p_1 = strNamesForLocale.indexOf("_");
        int p_2 = -1;
        if (p_1 != -1) {
            lang = strNamesForLocale.substring(0, p_1);
            p_2 = strNamesForLocale.indexOf("_", p_1 + 1);
            if (p_2 != -1) {
                country = strNamesForLocale.substring(p_1 + 1, p_2);
                variant = strNamesForLocale.substring(p_2 + 1);
            } else
                country = strNamesForLocale.substring(p_1 + 1);
        }

        if (variant != null)
            return new Locale(lang, country, variant);
        if (country != null)
            return new Locale(lang, country);
        return new Locale(lang);
    }

    /**
     * ��������������� �������. �� ���������� ��� ������������ html-�����.
     * ��������� ������ ����������� ���������� velocity
     *
     * @param target ��� ����� ���� ����� ��������� �����
     * @throws Exception
     */
    private synchronized static void generateHTMLFile(File target) throws Exception {
        Properties props = new Properties();

        // ������ ����� ��� ������ velocity ���������� ������� ����� ��������� ��� �������� �������� �������
        // ������ ����� ����������� �� classpath-����������
        props.setProperty("resource.loader", "classpath");
        props.setProperty("classpath.resource.loader.description", "Velocity Classpath Resource Loader");
        props.setProperty("classpath.resource.loader.class",
                "org.apache.velocity.runtime.resource.loader.ClasspathResourceLoader");
        //props.setProperty("classpath.resource.loader.path", "c:/templates");
        props.setProperty("classpath.resource.loader.cache", "false");
        props.setProperty("classpath.resource.loader.modificationCheckInterval", "0");


        VelocityEngine ve = new VelocityEngine(props);
        VelocityContext context = new VelocityContext();
        // �������� ������ ��������� ������ �� �������������� ������� ������
        context.put("good", rezGoods);
        context.put("bad", rezBads);
        context.put("resources", declaredResources);

        // ��������� ��� ���������� ������ � ���� html
        BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(target), "windows-1251"));
        String annoName = AnnoProcessor.class.getName();
        //��������� ��� ����� �������. ������ ������ ���������� "templatereport.vm.html"
        // � ���������� � ����� �������� � ���� �������
        annoName = annoName.replaceAll("\\.", "/");
        annoName = annoName.replace("AnnoProcessor", "templatereport.vm.html");

        final Template template = ve.getTemplate(annoName, "windows-1251");
        template.merge(context, bw);
        bw.flush();
        bw.close();

    }

    /**
     * ��������������� �������. �� ���������� ��������� ������� �������� ��� ������������ ���������� �� ���������� ������������
     */
    private synchronized static void prepareScanner() {
        declaredResources.clear();
        rezBads.clear();
        rezGoods.clear();
    }

    /**
     * ��������������� �������. �� ���������� �������������� ������ ������ Validator,
     * ����� � ��� ��� ���������� ���� ������ � ��������� ����� ������ spring-��������� ������� ������ ��������� ��� �����������
     *
     * @param validator  ������ ��������� ���������� ������������
     * @param context    �������� spring ������ �������� � ����� ��������� ������������
     * @param useContext ������� ���� ��������� �� ���������� �������� ����� �������� spring ��� ����� resourceBundle
     */
    private synchronized static void findAnnotations(Validator validator, ApplicationContext context, Boolean useContext, File pathToBaseResourcesDir) {

        final Class aClass = validator.getClass();
        final String validatorClassName = aClass.getName();

        final ValidationBundle validationBundle = (ValidationBundle) aClass.getAnnotation(ValidationBundle.class);
        // ��������� ���������� �� ������ ������ ���������� ���������� ValidationBundle
        if (validationBundle == null) return;

        final Locale aDefault = Locale.getDefault();
        HashSet<Locale> locales = new HashSet<Locale>();
        locales.add(aDefault);

        final String[] strNamesForLocales = validationBundle.locales();
        if (strNamesForLocales != null) {
            for (String strNamesForLocale : strNamesForLocales) {
                locales.add(getLocaleByName(strNamesForLocale));
            }
        }
        String enc = validationBundle.encoding();
        if (enc == null || enc.length() == 0)
            enc = "ISO8859-1";

        String s = validationBundle.value();
        if ((s == null || "".equals(s)) && useContext == false) return;

        if (declaredResources.containsKey(s))
            declaredResources.get(s).add(validatorClassName);
        else {
            declaredResources.put(s, new HashSet<String>(Arrays.asList(validatorClassName)));
        }
        String dir = "";
        int posSep = s.lastIndexOf("/");
        if (posSep != -1) {
            dir = dir + s.substring(0, posSep);
            s = s.substring(posSep + 1);
        }
        URLClassLoader urlLoader = null;
        if (useContext == false)
            try {
                final URL url = new File(pathToBaseResourcesDir, dir).toURI().toURL();
                urlLoader = new URLClassLoader(new URL[]{url});
            } catch (MalformedURLException e) {
                e.printStackTrace();
                return;
            }

        for (Locale locale : locales) {
            ResourceBundle resourceBundle = null;

            if (useContext == false)
                try {
                    resourceBundle = ResourceBundle.getBundle(s, locale, urlLoader);
                } catch (Exception e) {
                    e.printStackTrace();
                    return;
                }
            final Object[] EMPTY_OBJECTS = {};
            final Field[] declaredFields = aClass.getDeclaredFields();
            for (Field declaredField : declaredFields) {
                final String fieldName = declaredField.getName();

                final Annotation annotationMarker = declaredField.getAnnotation(ValidationBundle.Marker.class);
                if (annotationMarker == null) continue;
                declaredField.setAccessible(true);
                String keyName = null;
                try {
                    keyName = String.valueOf(declaredField.get(validator));
                } catch (IllegalAccessException e) {
                    rezBads.add(new Troika(locale.toString(), null, fieldName, validatorClassName, e.getMessage()));
                    e.printStackTrace();
                    continue;
                }
                try {
                    if (useContext == false) {
                        String keyValue = resourceBundle.getString(keyName);
                        keyValue = new String( keyValue.getBytes("ISO8859-1"), enc);
                        rezGoods.add(
                                new Troika(locale.toString(), keyName, fieldName, validatorClassName, keyValue)
                        );
                    } else {
                        final String keyValue = context.getMessage(keyName, EMPTY_OBJECTS, locale);
                        rezGoods.add(new Troika(locale.toString(), keyName, fieldName, validatorClassName, keyValue));
                    }
                } catch (Exception e) {
                    rezBads.add(new Troika(locale.toString(), keyName, fieldName, validatorClassName, e.getMessage()));
                }
            }
        }

    }
}

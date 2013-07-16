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
 * Класс выполняющий сканирование контекст spring, ищет в нем все бины
 * помеченные как валидаторы или бины контроллеры запросов
 * во втором случае мы анализируем являются ли данные контроллеры типом BaseCommandController,
 * т.е. могут ли они иметь привязанные к ним валидаторы
 * затем я выполняю поиск внутри класса валидатора привязок к ресурсам локализации
 * и для каждой из такой привязок проверяю то есть внутри контекста нужное сообщение
 */
public class AnnoProcessor {

    private static Logger logger = Logger.getLogger(AnnoProcessor.class.getName());


    private static HashMap<String, HashSet<String>> declaredResources = new HashMap<String, HashSet<String>>();
    private static List<Troika> rezBads = new ArrayList<Troika>();
    private static List<Troika> rezGoods = new ArrayList<Troika>();


    /**
     * Главная функция класса. Выполняет все действия по сканированию контекста spring.
     * Затем формируется html-отчет
     *
     * @param context                Ссылка на контекст spring
     * @param saveToFileName         имя файла, куда нужно сохранить результат анализа
     * @param useContext             признак того нужно ли использовать загрузку ресурсов из контекста
     * @param pathToBaseResourcesDir в случае если загрузка ресурсов выполняется не из контекста,
     *                               а самостоятельно, то этот параметр указывает путь к каталогу относительно которого должны располагаться
     *                               все нужные ресурсы
     * @return признак того, успешно ли был сгенерирован отчет с результатами анализа
     */
    public synchronized static boolean runScanner(ApplicationContext context, File saveToFileName, Boolean useContext, File pathToBaseResourcesDir) {
        logger.info("AnnoProcessor.runScanner preparing");
        prepareScanner();

        // нужно избежать дубляжей, вдруг один и тот же валидатор используется несколькими контроллерами
        HashSet<Class> usedValidators = new HashSet<Class>();
        // ищем все бины имеющие тип controller
        final Map mapControllers = context.getBeansOfType(AbstractController.class);
        logger.info("AnnoProcessor.runScanner got mapControllers = " + mapControllers);

        // теперь организуем по ним цикл, для каждого контролера проверяем его тип
        final Set setNamesOfControllers = mapControllers.keySet();
        for (Object nameController : setNamesOfControllers) {
            AbstractController absController = (AbstractController) mapControllers.get(nameController);
            // и если контроллер имеет тип BaseCommandController или производный от него
            if (!(absController instanceof BaseCommandController)) continue;
            BaseCommandController comController = (BaseCommandController) absController;
            // значит внутри такого контроллера найдутся и объекты валидаторы для команды
            if (comController.getValidators() != null)
                for (Validator validator : comController.getValidators()) {
                    // берем объект валидатор и запускаем сканирование
                    // однако сначала проверка на дубляжи
                    if (usedValidators.contains(validator.getClass())) continue;
                    findAnnotations(validator, context, useContext, pathToBaseResourcesDir);
                    usedValidators.add(validator.getClass());
                }
        }

        // теперь проверяем валидаторы в виде отдельный bean-ов
        final Map mapValidators = context.getBeansOfType(Validator.class);
        logger.info("AnnoProcessor.runScanner got mapValidators = " + mapValidators);
        for (Object nameValidator : mapValidators.keySet()) {
            Validator validator = (Validator) mapValidators.get(nameValidator);
            // также не забываем сделать проверку на дубляжи
            if (usedValidators.contains(validator.getClass())) continue;
            findAnnotations(validator, context, useContext, pathToBaseResourcesDir);
            usedValidators.add(validator.getClass());
        }

        // после завершения формирования массивов с результатами проверок, самое время создать html-отчет
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
     * Вспомогательная функция. Служит для получения объекта Locale по имени
     *
     * @param strNamesForLocale имя локали, возможно состоит из одной, двух, трех компонент
     * @return объект Locale
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
     * Вспомогательная фукнция. Ее назначение это сформировать html-отчет.
     * Генерация отчета выполняется средствами velocity
     *
     * @param target Имя файла куда нужно поместить отчет
     * @throws Exception
     */
    private synchronized static void generateHTMLFile(File target) throws Exception {
        Properties props = new Properties();

        // прежде всего для работы velocity необходимо указать такие параметры как источник загрузки шаблона
        // шаблон будет загружаться из classpath-приложения
        props.setProperty("resource.loader", "classpath");
        props.setProperty("classpath.resource.loader.description", "Velocity Classpath Resource Loader");
        props.setProperty("classpath.resource.loader.class",
                "org.apache.velocity.runtime.resource.loader.ClasspathResourceLoader");
        //props.setProperty("classpath.resource.loader.path", "c:/templates");
        props.setProperty("classpath.resource.loader.cache", "false");
        props.setProperty("classpath.resource.loader.modificationCheckInterval", "0");


        VelocityEngine ve = new VelocityEngine(props);
        VelocityContext context = new VelocityContext();
        // помещаем внутрь контекста ссылки на сформированные массивы данных
        context.put("good", rezGoods);
        context.put("bad", rezBads);
        context.put("resources", declaredResources);

        // последний шаг сохранение отчета в виде html
        BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(target), "windows-1251"));
        String annoName = AnnoProcessor.class.getName();
        //формируем имя файла шаблона. шаблон должен называться "templatereport.vm.html"
        // и находиться в одном каталоге с этим классом
        annoName = annoName.replaceAll("\\.", "/");
        annoName = annoName.replace("AnnoProcessor", "templatereport.vm.html");

        final Template template = ve.getTemplate(annoName, "windows-1251");
        template.merge(context, bw);
        bw.flush();
        bw.close();

    }

    /**
     * Вспомогательная функция. Ее назначение выполнить очистку массивов для последующего наполнения их результами сканирования
     */
    private synchronized static void prepareScanner() {
        declaredResources.clear();
        rezBads.clear();
        rezGoods.clear();
    }

    /**
     * Вспомогательная функция. Ее назначение просканировать данный объект Validator,
     * найти в нем все помеченные поля класса и проверить чтобы внутри spring-контекста нашлись нужные сообщения для локализации
     *
     * @param validator  Объект валидатор подлежащий сканированию
     * @param context    контекст spring внутри которого и нужно выполнить сканирование
     * @param useContext признак того выполнить ли разрешение ресурсов через контекст spring или через resourceBundle
     */
    private synchronized static void findAnnotations(Validator validator, ApplicationContext context, Boolean useContext, File pathToBaseResourcesDir) {

        final Class aClass = validator.getClass();
        final String validatorClassName = aClass.getName();

        final ValidationBundle validationBundle = (ValidationBundle) aClass.getAnnotation(ValidationBundle.class);
        // проверяем маркирован ли данный объект валидатора аннотацией ValidationBundle
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

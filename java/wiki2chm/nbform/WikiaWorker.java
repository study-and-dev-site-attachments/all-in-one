/*
 * WikiaWorker.java
 *
 * Created on 13 Май 2007 г., 1:35
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package nbform;

import com.sun.org.apache.xerces.internal.parsers.DOMParser;
import com.sun.org.apache.xpath.internal.XPathAPI;
import com.sun.org.apache.xpath.internal.objects.XObject;
import org.ccil.cowan.tagsoup.CommandLine;
import org.w3c.dom.DOMException;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.traversal.NodeIterator;
import org.xml.sax.EntityResolver;
import org.xml.sax.InputSource;

import javax.swing.*;
import javax.xml.transform.TransformerException;
import java.io.*;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.Set;


/**
 * Класс  выполняющий загрузку ресурсов и их помещение в список задач
 */
public class WikiaWorker {


    /**
     * Объект хранящий состояние выполняемой задачи (все нужные для работы поля с данными)
     */
    WikiaWorkerStateObject state = new WikiaWorkerStateObject();

    /**
     * Модель списка для вывода журнала работы
     */
    DefaultListModel model;


    /**
     * Функция остановки проекта
     */
    public void makeStopProject() {
        state.notTerminated = false;
        state.status_of = WikiaWorkerStateObject.STATUS_OF_WORK.STAT_NO_LAUNCHED_YET;
    }

     /**
     * Функция получения информации о том выполняется ли сейчас проект или он приостановлен проекта
     * @return Признак того запущен ли сайчас данный проект
     */
    public Boolean getProjectIsRunning() {
        return state.notPaused && state.notTerminated;
    }
    /**
     * Функция приостановки проекта
     */
    public void makeSuspendProject() {
        state.notPaused = false;
    }

    /**
     * Функция возобновления работы проекта
     */
    public void makeResumeProject() {
        state.notPaused = true;
    }

    /**
     * Функция выполняющая изменение состояния потока разбора ресурсов
     *
     * @param lstLogger объект logger (фактически это модель данных привязанная к GUI JList формы) в него следует поместить информацию об происходящих событиях
     */
    public void makeResumeOrSuspendProject(final DefaultListModel lstLogger) {
        state.notPaused = !state.notPaused;
        model = lstLogger;
        if (state.notPaused) {
            new Thread() {
                public void run() {
                    try {
                        work(lstLogger);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    finally {
                    }
                }
            }.start();
        }

    }

    /**
     * Конструктор класса выполняет инициализацию начальных значений, но запуск сервиса не выполняется
     *
     * @param wikiName
     * @param wikiSpecialPageName
     * @param saveToCatalog
     * @param encodingNameIn
     * @param wikiCatalogOnServerName
     * @param tagStart
     * @param dropByXPath
     * @param templateHTML
     * @param disabledExts
     * @param skipSpecialURLs
     */
    public WikiaWorker(String wikiName, String wikiSpecialPageName, String saveToCatalog, String encodingNameIn,String encodingNameOut,
                       String wikiCatalogOnServerName, String tagStart, String dropByXPath, String templateHTML,
                       String disabledExts, String skipSpecialURLs, Integer NamespacesPolicy, String allowedNameSpaces) {
        state.wikiName = wikiName;
        state.wikiSpecialPageName = wikiSpecialPageName;
        state.saveToCatalog = saveToCatalog;
        state.encodingNameIn = encodingNameIn;
        state.encodingNameOut = encodingNameOut;
        state.wikiCatalogOnServerName = wikiCatalogOnServerName;
        state.tagStart = tagStart;
        state.dropByXPath = dropByXPath;
        state.templateHTML = templateHTML;
        state.NamespacesPolicy = NamespacesPolicy;


        state.allowedNameSpaces = allowedNameSpaces.split(",");
        for (int i = 0; i < state.allowedNameSpaces.length; i++) {
            try {
                state.allowedNameSpaces[i] =  URLEncoder.encode(state.allowedNameSpaces[i].trim(), "utf-8");
            } catch (UnsupportedEncodingException e) {
                JOptionPane.showMessageDialog (null , "Ошибка: не возможно выполнить закодировать значения пространства имен из кодировки utf-8");
                e.printStackTrace();
            }
        }



        state.disabledExts = disabledExts.split(",");
        for (int i = 0; i < state.disabledExts.length; i++) {
            state.disabledExts[i] = state.disabledExts[i].trim();
        }

        state.skipSpecialURLs = skipSpecialURLs.split("\n");
        for (int i = 0; i < state.skipSpecialURLs.length; i++) {
            state.skipSpecialURLs[i] = state.skipSpecialURLs[i].trim();
        }


        state.status_of = WikiaWorkerStateObject.STATUS_OF_WORK.STAT_NO_LAUNCHED_YET;
        state.failedUrls = new ArrayList<StatusOfFailedItemsDialog.FailedResourceItem>();
    }

    /**
     * Запуск процесса загрузки mediawiki страниц
     *
     * @param model - модель списка для вывода журнала работы
     * @throws IOException
     */
    void work(DefaultListModel model) throws IOException {
        this.model = model;
        // проверяем текущее состояние потока выполняющего обработку задания
        if (WikiaWorkerStateObject.STATUS_OF_WORK.STAT_NO_LAUNCHED_YET == state.status_of) {
            // если это первый запуск
            state.globCounter = 1;
            model.removeAllElements();

            model.addElement("Начало обработки задания ");
            // надо удалить все файлы в каталоге местоназначения
            File[] flist = new File(state.saveToCatalog).listFiles();
            if (flist != null) {
                for (File file : flist) {
                    file.delete();
                }
            }
            model.addElement("Удалено все содержимое каталога местоназначения ");
            state.mapURLToFileName.clear();
            state.mustSkipThesePages.clear();

            state.webTasks.add(new WebTask(state.wikiName + state.wikiSpecialPageName, "index.html", "Перечень всех страниц"));
            model.addElement("Добавлено задание для главной страницы ");
            state.status_of = WikiaWorkerStateObject.STATUS_OF_WORK.STAT_IN_PROGRESS;
        }
        // вне зависисимости от того первый это запуск или нет необходимо обработать очередь заданий
        Node allPageContent = null;
        while ((state.webTasks.size() != 0) && state.notTerminated && state.notPaused) {
            WebTask task = state.webTasks.get(0);
            state.webTasks.remove(0);
            state.mapURLToFileName.put(task.getFileName(), getShortUrl(task));
            state.mustSkipThesePages.add(task.getPageURL());
            try {
                allPageContent = getPageContent(new URL(task.getPageURL()), state.tagStart);
            } catch (Exception e) {
                e.printStackTrace();
                continue;
            }
            if (allPageContent == null) {
                return;
            }
            adaptLinksAndImages(allPageContent);
            dropBadTagsFromDocument(allPageContent);
            saveAsFile(allPageContent, task.getFileName(), task.getPageTitle());
            model.addElement("Страница сохранена " + task.getPageTitle() + " как " + task.getFileName());
        }
        // после окончания обработки всех заданий необходимо выполнить синхронизацию загруженных страниц - изменить ссылки с
        // internet-ресурсов на локальные файлы
        model.addElement("Цикл загрузки ресурсов завершен (количество необработанных заданий = " + state.webTasks.size() + ")");
        // но делать это нужно только в том случае если обработка еще не завершена т.е. мы не в режиме паузы
        if (state.notPaused) {
            // теперь надо пробежаться по всем эти файлам и выполнить локализацию ссылок
            model.addElement("Модифицирую ссылки ");
            Iterator I = state.mapURLToFileName.keySet().iterator();
            while (I.hasNext()) {
                String fname = (String) I.next();
                String text = readFile(state.saveToCatalog + File.separator + fname, state.encodingNameOut);

                Set ks = state.mapURLToFileName.keySet();
                for (Object k : ks) {
                    text = text.replace("href=\"" + state.mapURLToFileName.get(k) + "\"", "href=\"" + k + "\"");
                }
                text = text.replace("\""+state.wikiCatalogOnServerName, "\"");
                
                writeFile(text, state.saveToCatalog + File.separator + fname, state.encodingNameOut);
            }
            model.addElement("Работа завершена ");
            state.status_of = WikiaWorkerStateObject.STATUS_OF_WORK.STAT_COMPLETED;

            new StatusOfFailedItemsDialog(state.failedUrls).showDialog();
        }

        state.status_of = WikiaWorkerStateObject.STATUS_OF_WORK.STAT_COMPLETED;
    }

    /**
     * Преобразование адреса загружаемой страницы в короткую форму
     * (только имя страницы без имени сервера и имени каталога в нем)
     * @param task объект задания, адрес которого и нужно представить в виде короткой строки 
     * @return Короткая форма адреса страницы
     */
    public String getShortUrl(WebTask task) {
        return task.getPageURL().substring(state.wikiName.length() - state.wikiCatalogOnServerName.length());
    }


    /**
     * Необходимо найти все ссылки в документе и добавить из них те, которые не исключены по какому-либо из правил, в список
     * для последующей загрузки
     *
     * @param allPageContent - узел с которого нужно обработать документ
     * @throws MalformedURLException
     */
    private void adaptLinksAndImages(Node allPageContent) throws MalformedURLException {
        try {
            // список ссылок на странице которые нужно удалить (специальные ссылки, ссылки на другие ресурсы)
            ArrayList<Node> mustBeDeleted = new ArrayList<Node>();

            XObject xob = XPathAPI.eval(allPageContent, "//*[name() = 'a' ]");
            NodeIterator it = xob.nodeset();
            Node cur = null;
            if (it != null) {
                bigcycle:
                while (true) {

                    try {
                        cur = it.nextNode();
                        if (cur == null) break bigcycle;
                    }
                    catch (Exception ee) {
                        // нет больше узлов для обработки
                        break bigcycle;
                    }

                    String href = XPathSupport.getAttribute(cur, "href");
                    if (href == null) continue;

                    if (href.indexOf("#") != -1) {
                        continue bigcycle;
                    }

                    XObject xob_img = XPathAPI.eval(cur, "*[name() = 'img' ]");
                    NodeIterator it_img = xob_img.nodeset();

                    Node node_img_in_a = it_img.nextNode();
                    if (node_img_in_a != null){
                        continue bigcycle;
                    }


                    URL pageUrl = null;
                    try {
                        pageUrl = new URL(new URL(state.wikiName), href);
                        if (state.mustSkipThesePages.contains(pageUrl.toExternalForm())) {
                            continue;
                        }
                        state.mustSkipThesePages.add(pageUrl.toExternalForm());

                        if (state.getNamespacesPolicy() == 0){
                            if ((("" + pageUrl.getPath()).indexOf(":") != -1)) {
                                mustBeDeleted.add(cur);
                                continue bigcycle;
                            }
                        }
                        if (state.getNamespacesPolicy() == 1){
                            if ((("" + pageUrl.getPath()).indexOf(":") == -1)) {
                                mustBeDeleted.add(cur);
                                continue bigcycle;
                            }
                        }

                        if (state.getNamespacesPolicy() == 2){
                            String all [] = state.getAllowedNameSpacesList();
                            Boolean isFoundAnyNamespace = false;
                            for (String s : all) {
                                if (("" + pageUrl.getPath()).indexOf(s+":") != -1)
                                    isFoundAnyNamespace = true;
                            }
                            if (isFoundAnyNamespace){
                                mustBeDeleted.add(cur);
                                continue bigcycle;
                            }
                        }
                        if (state.getNamespacesPolicy() == 3){
                            String all [] = state.getAllowedNameSpacesList();
                            Boolean isFoundAnyNamespace = false;
                            for (String s : all) {
                                if (("" + pageUrl.getPath()).indexOf(s+":") != -1)
                                    isFoundAnyNamespace = true;
                            }
                            if (! isFoundAnyNamespace){
                                mustBeDeleted.add(cur);
                                continue bigcycle;
                            }
                        }

                         if (state.getNamespacesPolicy() == 4){
                            String all [] = state.getAllowedNameSpacesList();
                            Boolean isFoundAnyNamespace = false;
                            for (String s : all) {
                                if (("" + pageUrl.getPath()).indexOf(s+":") != -1)
                                    isFoundAnyNamespace = true;
                            }
                            if (isFoundAnyNamespace || (("" + pageUrl.getPath()).indexOf(":") == -1)){
                                mustBeDeleted.add(cur);
                                continue bigcycle;
                            }
                        }
                        if (state.getNamespacesPolicy() == 5){
                            String all [] = state.getAllowedNameSpacesList();
                            Boolean isFoundAnyNamespace = false;
                            for (String s : all) {
                                if (("" + pageUrl.getPath()).indexOf(s+":") != -1)
                                    isFoundAnyNamespace = true;
                            }
                            if ((!isFoundAnyNamespace) && (("" + pageUrl.getPath()).indexOf(":") != -1)){
                                mustBeDeleted.add(cur);
                                continue bigcycle;
                            }
                        }

                        /*
                         Запрещены все, кроме основного пространства имен
                         Разрешены все, кроме основного пространства имен
                         Запрещены все перечисленные пространства имен
                         Разрешены все перечисленные пространства имен
                         Запрещены все перечисленные пространства имен + основное
                         Разрешены все перечисленные пространства имен + основное
                         */


                        if (pageUrl.toExternalForm().indexOf(state.wikiName) == -1) {
                            continue bigcycle;
                        }
                    } catch (MalformedURLException e) {
                        continue bigcycle;
                    }

                    String title = XPathSupport.getAttribute(cur, "title");

                    if (href.indexOf("#") != -1) {
                        continue bigcycle;
                    }
                    String className = XPathSupport.getAttribute(cur, "class");
                    if ("image".equals(className)) {
                        cur.getAttributes().removeNamedItem("href");
                        continue;
                    }

                    // список страниц которые нельзя загружать (служебные)
                    String man_mustDeleteFrags[] = new String[]{
                            "action=", "printable=yes", "action=history", "diff=", "oldid=", "oldid=",
                            "%D0%A1%D0%BB%D1%83%D0%B6%D0%B5%D0%B1%D0%BD%D0%B0%D1%8F:", "Special:",
                    };
                    ArrayList<String> mustDeleteFrags = new ArrayList<String>();
                    Collections.addAll(mustDeleteFrags, man_mustDeleteFrags);
                    Collections.addAll(mustDeleteFrags, state.disabledExts);
                    Collections.addAll(mustDeleteFrags, state.skipSpecialURLs);

                    for (String i_mustDeleteFrag : mustDeleteFrags)
                        if (href.indexOf(i_mustDeleteFrag) != -1) {
                            mustBeDeleted.add(cur);
                            continue bigcycle;
                        }
                    // для каждой страницы нужно определить имя файла в котором она будет храниться и ее заголовок

                    try {
                        title = URLDecoder.decode(title, state.encodingNameIn);
                    } catch (Exception e) {
                        title = "Page_at_" + href;
                    }

                    try {
                        String fname = "page_" + state.globCounter++ + ".html";
                        // добавляем задание к обработке
                        state.webTasks.add(new WebTask(pageUrl.toExternalForm(), fname, title));
                        model.addElement("Добавлено новое задание:  " + title + " / " + href);
                    } catch (Exception e) {
                        e.printStackTrace();
                        continue bigcycle;
                    }
                }// while
            }//if has any a on page
            // теперь выкинем те дибильные ссылки
            for (Node node : mustBeDeleted) {
                node.getParentNode().removeChild(node);
            }
        } catch (TransformerException e) {
            e.printStackTrace();
        }

        // --- теперь картинки ---

        try {
            XObject xob = XPathAPI.eval(allPageContent, "//*[name() = 'img' ]");
            NodeIterator it = xob.nodeset();
            Node cur = null;
            while ((cur = it.nextNode()) != null) {
                if (cur.getAttributes().getNamedItem("longdesc") != null) {
                    cur.getAttributes().removeNamedItem("longdesc");
                }
                String src = XPathSupport.getAttribute(cur, "src");
                if (src == null) continue;
                try {
                    URL imgUrl = new URL(new URL(state.wikiName), src);
                    if (state.mustSkipThesePages.contains(imgUrl)) {
                        continue;
                    }
                    state.mustSkipThesePages.add(imgUrl.toExternalForm());
                    XPathSupport.setAttribute(cur, "src", loadImage(imgUrl));
                } catch (Exception e) {
                    e.printStackTrace();
                    continue;
                }
            }// while
        } catch (TransformerException e) {
            e.printStackTrace();
        }
    } // end of adaptLinksAndImages


    /**
     * Адаптация содержимого страницы, найти все ссылки и изменить их так чтобы они ссылались на реальные адреса (файлы)
     *
     * @param allPageContent узел страницы
     * @param fileToSave     файл, куда нужно сохранить данный узел
     * @param title          заголовок страницы (вставляется в шаблон страницы в нужное место)
     */
    private void saveAsFile(Node allPageContent, String fileToSave, String title) {
        try {
            String content = XPathSupport.getNodeAsString(allPageContent, state.encodingNameOut, false);
            String template = readFile(state.templateHTML, state.encodingNameOut);

            template = template.replace("#CONTENT#", content);
            template = template.replace("#TITLE#", title);

            OutputStreamWriter fw = new OutputStreamWriter(new FileOutputStream(new File(state.saveToCatalog, fileToSave)), state.encodingNameOut);
            fw.write(template);
            fw.flush();
            fw.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void dropBadTagsFromDocument(Node allPageContent) {
        String xpaths[] = state.dropByXPath.split("\n");
        // в ходе сохранения нужно удалить из узла все ненужные части (теги указанные пользователем)
        for (String xpath : xpaths) {
            String sclear = xpath.trim();
            if (sclear.length() == 0) continue;
            ArrayList<Node> mustbeDel = new ArrayList<Node>();
            try {
                XObject xob = XPathAPI.eval(allPageContent, sclear);
                NodeIterator it = xob.nodeset();
                Node cur;
                while ((cur = it.nextNode()) != null) {
                    mustbeDel.add(cur);
                }
            } catch (TransformerException e) {
                e.printStackTrace();
                continue;
            }

            for (Node node : mustbeDel) {
                try {
                    node.getParentNode().removeChild(node);
                } catch (DOMException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * Прочитать содержимое файла
     *
     * @param fName         имя фала
     * @param encondingName имя кодировки для чтения файла
     * @return строка со всем содержимым файла
     * @throws IOException
     */
    private String readFile(String fName, String encondingName) throws IOException {
        BufferedReader brin = new BufferedReader(new InputStreamReader(new FileInputStream(fName), encondingName));
        StringBuilder bu = new StringBuilder();
        String line;
        while ((line = brin.readLine()) != null) {
            bu.append(line).append("\n");
        }
        return bu.toString();
    }

    /**
     * Сохранить строку в файл
     *
     * @param fileContent   строка которую нужно записать в файл
     * @param fileName      имя файла
     * @param encondingName имя кодировки
     * @throws IOException
     */
    private void writeFile(String fileContent, String fileName, String encondingName) throws IOException {
        OutputStreamWriter ow = new OutputStreamWriter(new FileOutputStream(fileName), encondingName);
        ow.write(fileContent);
        ow.flush();
        ow.close();
    }

    /**
     * Функция на основании URL-страницы получает ее содержимое и извлекает из нее тот узел который содержит информацию
     *
     * @param ula      адрес страницы для загрузки
     * @param tagStart xpath-выражения для тега содержащего основную информацию
     * @return узел с содержимым документа
     * @throws Exception
     */
    private Node getPageContent(URL ula, String tagStart) throws Exception {
        ByteArrayOutputStream bout = new ByteArrayOutputStream();
        try {
            File ftmp = File.createTempFile("zzzz", "ffff");
            BufferedReader brin = new BufferedReader(new InputStreamReader (ula.openStream(), state.encodingNameIn));
            StringBuffer buf = new StringBuffer();
            String line = null;
            while ((line = brin.readLine()) != null)
                buf.append(line).append("\n");
            try {
                brin.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            String text = buf.toString();

            String tags [] = new String [] {
                    "a", "div", "span", "p", "h1", "h2",  "h3", "h4", "h5", "h6", "h7"
            };
            for (String tag : tags) {
                String regexp = "(?i)<"+tag+"([^>]*?)></"+tag+">";
                text = text.replaceAll(regexp, "<"+tag+"$1>&nbsp;<!-- comment --></"+tag+">");

                regexp = "(?i)<"+tag+"([^/>]*?)/>";
                text = text.replaceAll(regexp, "<"+tag+"$1>&nbsp;<!-- comment --></"+tag+">");
            }

            OutputStreamWriter fw = new OutputStreamWriter(new FileOutputStream(ftmp), state.encodingNameIn);
            fw.write(text);
            fw.flush();
            fw.close();


            CommandLine.process(ftmp.toURL().toExternalForm(), bout);

            ftmp.delete();
            
        } catch (Exception e) {
            state.failedUrls.add(new StatusOfFailedItemsDialog.FailedResourceItem(ula,
                    StatusOfFailedItemsDialog.FailedResourceItem.FailedResourceKind.HTML));
            throw e;
        }
        if (tagStart != null) {
            DOMParser dp = new DOMParser();

            //dp.setEntityResolver(new MyEntityResolver());
            try {
                final InputSource inputSource = new InputSource(new ByteArrayInputStream(bout.toByteArray()));
                inputSource.setSystemId(ula.toExternalForm());
                dp.parse(inputSource);
            } catch (IOException e) {
                // а тут хитрее вообще то ошибка чтени¤ данных могла вознинуть из за несовпадени¤ кодировок --
                //  например возвращают документ где кодировка windows-1251 а она ¤вно не указана
                dp.parse(new InputSource(new InputStreamReader(new ByteArrayInputStream(bout.toByteArray()), state.encodingNameIn)));
                return null;
            }
            Document doc = dp.getDocument();

            XObject xob = XPathAPI.eval(doc, tagStart);
            NodeIterator it = xob.nodeset();
            Node cur = it.nextNode();

            if ((cur == null)) {
                JOptionPane.showMessageDialog(null, "Не возможно найти тег: " + tagStart);
                return null;
            }
            return cur;
        }
        // parsing
        return null;
    }

    /**
     * Функция выполняющая загрузку картинки с указанного адреса
     *
     * @param ula адрес картинки для загрузки
     * @return строка с именем картинки на диске куда она была сохранена
     * @throws IOException
     */
    private String loadImage(URL ula) throws IOException {
        String name = getOnlyImageName(ula);
        File tanem = new File(state.saveToCatalog + File.separator + name);
        tanem.getParentFile().mkdirs();
        if (tanem.exists()) return name;

        InputStream sin = null;
        try {
            sin = ula.openStream();
        } catch (IOException e) {
            state.failedUrls.add(new StatusOfFailedItemsDialog.FailedResourceItem(ula,
                    StatusOfFailedItemsDialog.FailedResourceItem.FailedResourceKind.IMAGE));
            throw e;
        }
        ByteArrayOutputStream bout = new ByteArrayOutputStream();
        byte[] buf = new byte[4096];
        int real;
        while ((real = sin.read(buf)) > 0) {
            bout.write(buf, 0, real);
        }
        sin.close();

        FileOutputStream fw = new FileOutputStream(tanem);
        fw.write(bout.toByteArray());
        fw.close();
        return name;
    }

    /**
     * Получить только имя картинки (без предшествующего пути сервера и префикса каталога)
     *
     * @param ula
     * @return
     */
    private String getOnlyImageName(URL ula) {
        String s = ula.getPath().replace(state.wikiCatalogOnServerName, "");
        if (s.startsWith("/"))
            s = s.substring(1);
        return s;
    }

    /**
     * Класс выполняющий разрешение нужных для работы схем
     */
    private static class MyEntityResolver implements EntityResolver {
        public InputSource resolveEntity(String publicId, String systemId) {
            try {
                File fGot = new File(systemId);
                if (fGot.exists()) {
                    System.out.println("DirectCall: " + systemId);
                    return new InputSource(new FileInputStream(fGot));
                }
                URL ula = new URL(systemId);
                String fname = ula.getFile();
                int pp = fname.lastIndexOf("/");
                if (pp > -1)
                    fname = fname.substring(pp + 1);
                File frepl = new File("./schemas", fname);
                System.out.println("Resolve: " + frepl);
                return new InputSource(new FileInputStream(frepl));

            } catch (Exception e) {
                e.printStackTrace();
                JOptionPane.showMessageDialog(null, "Ошибка настадии разрешения сущности и ее ссылки \npublicId: " + publicId + "\nsystemId: " + systemId);
                return null;
            }
        }
    }
}

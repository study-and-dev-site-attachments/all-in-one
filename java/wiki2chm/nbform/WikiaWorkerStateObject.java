package nbform;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Hashtable;

/**
 * ¬спомогательный класс хранит сведени€ об состо€нии рабочего потока загрузки (WikiaWorker)
 */
public class WikiaWorkerStateObject implements Serializable {
    /**
     * здесь хранитс€ информаци€ об состо€нии выполн€емой работы
     */
    public static enum STATUS_OF_WORK {
        STAT_NO_LAUNCHED_YET,
        STAT_COMPLETED,
        STAT_IN_PROGRESS
    }


    /**
     * —четчик количества загружаемых ресурсов
     */
    public int globCounter = 0;
    /**
     * признак завершени€ работы потока
     */
    public Boolean notTerminated = true;
    /**
     * признак временной оставноки потока
     */
    public Boolean notPaused = true;
    /**
     * “екущее состо€ние выполн€емой работы
     */
    public STATUS_OF_WORK status_of = STATUS_OF_WORK.STAT_NO_LAUNCHED_YET;
    /**
     * —писок тех ресурсов при загрузке которых возникли какие-либо проблемы
     */
    public ArrayList<StatusOfFailedItemsDialog.FailedResourceItem> failedUrls = new ArrayList<StatusOfFailedItemsDialog.FailedResourceItem>();
    /**
     * —писок расширений документов на mediawiki-сервере которые не следует загружать
     */
    public String[] disabledExts;
    /**
     * јдрес вики-сервера
     */
    public String wikiName;
    /**
     * јдрес страницы "—пециальна€-все страницы"
     */
    public String wikiSpecialPageName;
    /**
     *  аталог, куда нужно сохранить временные файлы (отдельные странички и картинки)
     */
    public String saveToCatalog;
    /**
     * »м€ каталога на сервере где находитс€ wiki
     */
    public String wikiCatalogOnServerName;
    /**
     * »м€ кодировки в которой загружаютс€ с сервера страницы
     */
    public String encodingNameIn;
    /**
     * »м€ кодировки в которой будут сохран€тьс€ файлы после загрузки их с сервера
     */
    public String encodingNameOut;

    /**
     * —трока xpath-выражени€ поиска тега содержащего все содержимое wiki-документа (основна€ колонка)
     */
    public String tagStart;
    /**
     * ”даление всех xpath-узлов
     */
    public String dropByXPath;
    /**
     * ѕуть к файлу шаблона страницы (внутрь этого файла вставл€етс€ загруженное из mediawiki-страницы)
     */
    public String templateHTML;
    /**
     * јдреса страницы которые следует пропускать
     */
    public String[] skipSpecialURLs;

    /**
     *  арта отображени€ адреса страницы которую нужно загрузить в файл (дл€ всех заданий, в том числе и выполненных) в который страница будет сохранена
     */
    public Hashtable mapURLToFileName = new Hashtable();
    /**
     * —писок адресов тех страниц которые уже были загружены
     */
    public ArrayList<String> mustSkipThesePages = new ArrayList<String>();
    /**
     * —писок заданий на загруку документов
     */
    public ArrayList<WebTask> webTasks = new ArrayList<WebTask>();


    /**
     * ѕризнак того разрешены ли при загрузке страниц имена содержащие символ ":"
     */
    public Integer NamespacesPolicy = 0;

    /**
     * —писок тех пространств имен которые разрешены дл€ загрузки
     */
    public String[] allowedNameSpaces  = null;

    public WikiaWorkerStateObject() {
    }

    public int getGlobCounter() {
        return globCounter;
    }

    public void setGlobCounter(int globCounter) {
        this.globCounter = globCounter;
    }

    public Boolean getNotTerminated() {
        return notTerminated;
    }

    public void setNotTerminated(Boolean notTerminated) {
        this.notTerminated = notTerminated;
    }

    public Boolean getNotPaused() {
        return notPaused;
    }

    public void setNotPaused(Boolean notPaused) {
        this.notPaused = notPaused;
    }

    public STATUS_OF_WORK getStatus_of() {
        return status_of;
    }

    public void setStatus_of(STATUS_OF_WORK status_of) {
        this.status_of = status_of;
    }

    public ArrayList<StatusOfFailedItemsDialog.FailedResourceItem> getFailedUrls() {
        return failedUrls;
    }

    public void setFailedUrls(ArrayList<StatusOfFailedItemsDialog.FailedResourceItem> failedUrls) {
        this.failedUrls = failedUrls;
    }

    public String[] getDisabledExts() {
        return disabledExts;
    }

    public void setDisabledExts(String[] disabledExts) {
        this.disabledExts = disabledExts;
    }

    public String getWikiName() {
        return wikiName;
    }

    public void setWikiName(String wikiName) {
        this.wikiName = wikiName;
    }

    public String getWikiSpecialPageName() {
        return wikiSpecialPageName;
    }

    public void setWikiSpecialPageName(String wikiSpecialPageName) {
        this.wikiSpecialPageName = wikiSpecialPageName;
    }

    public String getSaveToCatalog() {
        return saveToCatalog;
    }

    public void setSaveToCatalog(String saveToCatalog) {
        this.saveToCatalog = saveToCatalog;
    }

    public String getWikiCatalogOnServerName() {
        return wikiCatalogOnServerName;
    }

    public void setWikiCatalogOnServerName(String wikiCatalogOnServerName) {
        this.wikiCatalogOnServerName = wikiCatalogOnServerName;
    }

    public String getEncodingNameIn() {
        return encodingNameIn;
    }

    public void setEncodingNameIn(String encodingNameIn) {
        this.encodingNameIn = encodingNameIn;
    }

    public String getTagStart() {
        return tagStart;
    }

    public void setTagStart(String tagStart) {
        this.tagStart = tagStart;
    }

    public String getDropByXPath() {
        return dropByXPath;
    }

    public void setDropByXPath(String dropByXPath) {
        this.dropByXPath = dropByXPath;
    }

    public String getTemplateHTML() {
        return templateHTML;
    }

    public void setTemplateHTML(String templateHTML) {
        this.templateHTML = templateHTML;
    }

    public String[] getSkipSpecialURLs() {
        return skipSpecialURLs;
    }

    public void setSkipSpecialURLs(String[] skipSpecialURLs) {
        this.skipSpecialURLs = skipSpecialURLs;
    }

    public Hashtable getMapURLToFileName() {
        return mapURLToFileName;
    }

    public void setMapURLToFileName(Hashtable mapURLToFileName) {
        this.mapURLToFileName = mapURLToFileName;
    }

    public ArrayList<String> getMustSkipThesePages() {
        return mustSkipThesePages;
    }

    public void setMustSkipThesePages(ArrayList<String> mustSkipThesePages) {
        this.mustSkipThesePages = mustSkipThesePages;
    }

    public ArrayList<WebTask> getWebTasks() {
        return webTasks;
    }

    public void setWebTasks(ArrayList<WebTask> webTasks) {
        this.webTasks = webTasks;
    }

    public String getEncodingNameOut() {
        return encodingNameOut;
    }

    public void setEncodingNameOut(String encodingNameOut) {
        this.encodingNameOut = encodingNameOut;
    }


    public String[] getAllowedNameSpacesList() {
        return allowedNameSpaces;
    }

    public void setAllowedNameSpaces(String[] allowedNameSpaces) {
        this.allowedNameSpaces = allowedNameSpaces;
    }

    public Integer getNamespacesPolicy() {
        return NamespacesPolicy;
    }

    public void setNamespacesPolicy(Integer namespacesPolicy) {
        NamespacesPolicy = namespacesPolicy;
    }
}

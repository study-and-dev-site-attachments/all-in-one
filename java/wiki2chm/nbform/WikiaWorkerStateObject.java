package nbform;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Hashtable;

/**
 * ��������������� ����� ������ �������� �� ��������� �������� ������ �������� (WikiaWorker)
 */
public class WikiaWorkerStateObject implements Serializable {
    /**
     * ����� �������� ���������� �� ��������� ����������� ������
     */
    public static enum STATUS_OF_WORK {
        STAT_NO_LAUNCHED_YET,
        STAT_COMPLETED,
        STAT_IN_PROGRESS
    }


    /**
     * ������� ���������� ����������� ��������
     */
    public int globCounter = 0;
    /**
     * ������� ���������� ������ ������
     */
    public Boolean notTerminated = true;
    /**
     * ������� ��������� ��������� ������
     */
    public Boolean notPaused = true;
    /**
     * ������� ��������� ����������� ������
     */
    public STATUS_OF_WORK status_of = STATUS_OF_WORK.STAT_NO_LAUNCHED_YET;
    /**
     * ������ ��� �������� ��� �������� ������� �������� �����-���� ��������
     */
    public ArrayList<StatusOfFailedItemsDialog.FailedResourceItem> failedUrls = new ArrayList<StatusOfFailedItemsDialog.FailedResourceItem>();
    /**
     * ������ ���������� ���������� �� mediawiki-������� ������� �� ������� ���������
     */
    public String[] disabledExts;
    /**
     * ����� ����-�������
     */
    public String wikiName;
    /**
     * ����� �������� "�����������-��� ��������"
     */
    public String wikiSpecialPageName;
    /**
     * �������, ���� ����� ��������� ��������� ����� (��������� ��������� � ��������)
     */
    public String saveToCatalog;
    /**
     * ��� �������� �� ������� ��� ��������� wiki
     */
    public String wikiCatalogOnServerName;
    /**
     * ��� ��������� � ������� ����������� � ������� ��������
     */
    public String encodingNameIn;
    /**
     * ��� ��������� � ������� ����� ����������� ����� ����� �������� �� � �������
     */
    public String encodingNameOut;

    /**
     * ������ xpath-��������� ������ ���� ����������� ��� ���������� wiki-��������� (�������� �������)
     */
    public String tagStart;
    /**
     * �������� ���� xpath-�����
     */
    public String dropByXPath;
    /**
     * ���� � ����� ������� �������� (������ ����� ����� ����������� ����������� �� mediawiki-��������)
     */
    public String templateHTML;
    /**
     * ������ �������� ������� ������� ����������
     */
    public String[] skipSpecialURLs;

    /**
     * ����� ����������� ������ �������� ������� ����� ��������� � ���� (��� ���� �������, � ��� ����� � �����������) � ������� �������� ����� ���������
     */
    public Hashtable mapURLToFileName = new Hashtable();
    /**
     * ������ ������� ��� ������� ������� ��� ���� ���������
     */
    public ArrayList<String> mustSkipThesePages = new ArrayList<String>();
    /**
     * ������ ������� �� ������� ����������
     */
    public ArrayList<WebTask> webTasks = new ArrayList<WebTask>();


    /**
     * ������� ���� ��������� �� ��� �������� ������� ����� ���������� ������ ":"
     */
    public Integer NamespacesPolicy = 0;

    /**
     * ������ ��� ����������� ���� ������� ��������� ��� ��������
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

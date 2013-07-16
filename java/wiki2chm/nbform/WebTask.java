package nbform;

import java.io.Serializable;

/**
     * ����� �������� �������� �� ����������� �������
 */
public class WebTask implements Serializable {
    /**
     * ����� �������� ������� ���������� ���������
     */
    String pageURL;
    /**
     * ��� ����� � ������� ����� ��������� ���������� mediawiki-��������
     */
    String fileName;
    /**
     * ��������� mediawiki-��������
     */
    String pageTitle;

    public WebTask(String pageURL, String fileName, String t) {
        this.pageURL = pageURL;
        this.fileName = fileName;
        this.pageTitle = t;
    }


    public String getPageTitle() {
        return pageTitle;
    }

    public String getFileName() {
        return fileName;
    }

    public String getPageURL() {
        return pageURL;
    }


    public void setPageURL(String pageURL) {
        this.pageURL = pageURL;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public void setPageTitle(String pageTitle) {
        this.pageTitle = pageTitle;
    }

    public WebTask() {
    }
}

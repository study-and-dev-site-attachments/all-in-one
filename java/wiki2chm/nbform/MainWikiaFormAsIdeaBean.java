package nbform;

import java.io.Serializable;

/**
 * ����� �������� �������� �� ���������� �������, ������ ���� ����� ����������� ��� XML-�������� � ����
 */
public class MainWikiaFormAsIdeaBean implements Serializable {
    /**
     * ��� �������
     */
    private String txtServerName;
    /**
     * ����� ����������� �������� "��� ��������"
     */
    private String txtAllPagesWikiName;
    /**
     * ��� �������� ���� ����� ��������� ��� ����������� �� internet �����
     */
    private String txtOutputDir;
    /**
     * ��� �������� �� ������� (���� wiki ����������� �� � ����� �������)
     */
    private String txtCatalogWikiName;
    /**
     * XPath-��������� ��� ������ ��� ����� html-��������� wiki-��������, ��� �������� �������� ������ ��� ����������� �������������� (��� ������ ������� � �����)
     */
    private String txtXPathForContent;
    /**
     * ������ ��� ����� ������ (�������� ����� XPath-���������) ������� ������� ������� �� ��������� ���� ��������
     */
    private String txtDropXPathTags;
    /**
     * ���� � ����� �������
     */
    private String txtTemplateName;
    /**
     * ��� ����� �������
     */
    private String txtFileOfProject;
    /**
     * ����� ��������� ��������� ��� ������� ������� wiki (������-�� ��� ������ utf-8, �� ...)
     */
    private Integer comboSelectedCharSetInIndex;
    /**
     * ����� ��������� ��������� ��� ��� ���������� ������� � ����
     */
    private Integer comboSelectedCharSetOutIndex;

    /**
     * ����� ���� ���������� (�� ��������� �� ���������� ������� ��������� �� ������� �� ��������)
     */
    private String txtSkipByExtension;
    /**
     * �������� ��� ����� ������� ������� ����� ���������� �������� �� ������� �� ��������
     */
    private String txtSkipSpecialURLs;

    /**
     * ������� ���� ��������� �� ��� �������� ������� ����� ���������� ������ ":"
     */
    public Integer NamespacesPolicy = 0;


    /**
     * ������ ��� ����������� ���� ������� ��������� ��� ��������
     */
    public String allowedNameSpaces  = "";


    Boolean buttonStartEnabled;
    Boolean buttonStopEnabled;
    Boolean buttonButtonSuspendResume;

    public MainWikiaFormAsIdeaBean() {
    }

    public Boolean getButtonStartEnabled() {
        return buttonStartEnabled;
    }

    public void setButtonStartEnabled(Boolean buttonStartEnabled) {
        this.buttonStartEnabled = buttonStartEnabled;
    }

    public Boolean getButtonStopEnabled() {
        return buttonStopEnabled;
    }

    public void setButtonStopEnabled(Boolean buttonStoptEnabled) {
        this.buttonStopEnabled = buttonStoptEnabled;
    }

    public Boolean getButtonButtonSuspendResume() {
        return buttonButtonSuspendResume;
    }

    public void setButtonButtonSuspendResume(Boolean buttonButtonSuspendResume) {
        this.buttonButtonSuspendResume = buttonButtonSuspendResume;
    }

    public String getTxtServerName() {
        return txtServerName;
    }

    public void setTxtServerName(final String txtServerName) {
        this.txtServerName = txtServerName;
    }

    public String getTxtAllPagesWikiName() {
        return txtAllPagesWikiName;
    }

    public void setTxtAllPagesWikiName(final String txtAllPagesWikiName) {
        this.txtAllPagesWikiName = txtAllPagesWikiName;
    }

    public String getTxtOutputDir() {
        return txtOutputDir;
    }

    public void setTxtOutputDir(final String txtOutputDir) {
        this.txtOutputDir = txtOutputDir;
    }

    public String getTxtCatalogWikiName() {
        return txtCatalogWikiName;
    }

    public void setTxtCatalogWikiName(final String txtCatalogWikiName) {
        this.txtCatalogWikiName = txtCatalogWikiName;
    }

    public String getTxtXPathForContent() {
        return txtXPathForContent;
    }

    public void setTxtXPathForContent(final String txtXPathForContent) {
        this.txtXPathForContent = txtXPathForContent;
    }

    public String getTxtDropXPathTags() {
        return txtDropXPathTags;
    }

    public void setTxtDropXPathTags(final String txtDropXPathTags) {
        this.txtDropXPathTags = txtDropXPathTags;
    }

    public String getTxtTemplateName() {
        return txtTemplateName;
    }

    public void setTxtTemplateName(final String txtTemplateName) {
        this.txtTemplateName = txtTemplateName;
    }

    public String getTxtFileOfProject() {
        return txtFileOfProject;
    }

    public void setTxtFileOfProject(final String txtFileOfProject) {
        this.txtFileOfProject = txtFileOfProject;
    }

    public Integer getComboSelectedCharSetInIndex() {
        return comboSelectedCharSetInIndex;
    }

    public void setComboSelectedCharSetInIndex(Integer comboSelectedCharSetInIndex) {
        this.comboSelectedCharSetInIndex = comboSelectedCharSetInIndex;
    }

    public Integer getComboSelectedCharSetOutIndex() {
        return comboSelectedCharSetOutIndex;
    }

    public void setComboSelectedCharSetOutIndex(Integer comboSelectedCharSetOutIndex) {
        this.comboSelectedCharSetOutIndex = comboSelectedCharSetOutIndex;
    }

    public String getTxtSkipByExtension() {
        return txtSkipByExtension;
    }

    public void setTxtSkipByExtension(String txtSkipByExtension) {
        this.txtSkipByExtension = txtSkipByExtension;
    }

    public String getTxtSkipSpecialURLs() {
        return txtSkipSpecialURLs;
    }

    public void setTxtSkipSpecialURLs(String txtSkipSpecialURLs) {
        this.txtSkipSpecialURLs = txtSkipSpecialURLs;
    }


    public String getAllowedNameSpacesList() {
        return allowedNameSpaces;
    }

    public void setAllowedNameSpaces(String allowedNameSpaces) {
        this.allowedNameSpaces = allowedNameSpaces;
    }

    public Integer getNamespacesPolicy() {
        return NamespacesPolicy;
    }

    public void setNamespacesPolicy(Integer namespacesPolicy) {
        NamespacesPolicy = namespacesPolicy;
    }
}
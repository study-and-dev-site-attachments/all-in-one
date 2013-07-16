package nbform;

import java.io.Serializable;

/**
 *  ласс хран€щий сведени€ об параметрах проекта, именно этот класс сохран€етс€ как XML-документ в файл
 */
public class MainWikiaFormAsIdeaBean implements Serializable {
    /**
     * »м€ сервера
     */
    private String txtServerName;
    /**
     * јдрес специальной страницы "¬се страницы"
     */
    private String txtAllPagesWikiName;
    /**
     * »м€ каталога куда нужно поместить все загруженные из internet файлы
     */
    private String txtOutputDir;
    /**
     * »м€ каталога на сервере (если wiki расположена не в корне сервера)
     */
    private String txtCatalogWikiName;
    /**
     * XPath-выражение дл€ поиска той части html-документа wiki-страницы, что содержит реальные данные без избыточного форматировани€ (без лишних колонок и тегов)
     */
    private String txtXPathForContent;
    /**
     * —писок тех узлов дерева (заданных через XPath-выражение) которые следует удалить из исходного тела страницы
     */
    private String txtDropXPathTags;
    /**
     * ѕуть к файлу шаблона
     */
    private String txtTemplateName;
    /**
     * »м€ файла проекта
     */
    private String txtFileOfProject;
    /**
     * Ќомер выбранной кодировки дл€ входных страниц wiki (вообще-то это всегда utf-8, но ...)
     */
    private Integer comboSelectedCharSetInIndex;
    /**
     * Ќомер выбранной кодировки дл€ при сохранении страниц в файл
     */
    private Integer comboSelectedCharSetOutIndex;

    /**
     *  акие типы документов (на основании их расширени€ следует выбросить из очереди на загрузку)
     */
    private String txtSkipByExtension;
    /**
     * ¬озможно что часть адресов страниц также необходимо выкинуть из очереди на загрузку
     */
    private String txtSkipSpecialURLs;

    /**
     * ѕризнак того разрешены ли при загрузке страниц имена содержащие символ ":"
     */
    public Integer NamespacesPolicy = 0;


    /**
     * —писок тех пространств имен которые разрешены дл€ загрузки
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
package nbform;

import com.intellij.uiDesigner.core.Spacer;
import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;
import org.ccil.cowan.tagsoup.CommandLine;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.XMLDecoder;
import java.beans.XMLEncoder;
import java.io.*;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.Charset;
import java.util.Set;
import java.util.SortedMap;

/**
 * Created by IntelliJ IDEA.
 * User: Programmer
 * Date: 28.08.2007
 * Time: 23:06:25
 * To change this template use File | Settings | File Templates.
 */
public class MainWikiaFormAsIdea {
    private JPanel megapane;
    private JTabbedPane pages_main;
    private JList lstLog;
    private JPanel tab_config;
    private JTextField txtServerName;
    private JButton btnSelectOutputDir;
    private JTextField txtOutputDir;
    private JTextField txtAllPagesWikiName;
    private JComboBox comboCharSetIn;
    private JTextField txtCatalogWikiName;
    private JTextField txtXPathForContent;
    private JTextArea txtDropXPathTags;
    private JTextField txtTemplateName;
    private JButton btnSelectTemplateFile;
    private JProgressBar pBar;
    private JButton btnStart;
    private JButton btnStop;
    private JPanel paneBtnsZoneGoStop;
    private JButton btnTagSoupSettings;
    private JButton saveProjectAs;
    private JTextField txtFileOfProject;
    private JButton loadProjectAs;
    private JButton btnTestServerPath;
    private JButton btnSuspendResume;
    private JTextArea txtSkipByExtension;
    private JTextArea txtSkipSpecialURLs;
    private JPanel tab_log;
    private JComboBox comboCharSetOut;
    private JTextField txtAllowedNameSpacesList;
    private JComboBox comboNameSpacesPolicy;

    /**
     * Точка входа приложения. Основное назначение main создать форму и показать ее на экране
     *
     * @param args
     */
    public static void main(String[] args) {
        final JFrame jf = new JFrame("MediaWiki2Chm project (by black zorro --- black-zorro@tut.by, http://black-zorro.com)");
        jf.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        MainWikiaFormAsIdea form = new MainWikiaFormAsIdea();
        jf.getContentPane().add(form.megapane);
        form.invokeAfterCreation();
        //jf.setSize(860, 720);
        jf.pack();
        SwingUtilities.invokeLater(
                new Runnable() {
                    public void run() {
                        jf.setVisible(true);
                    }
                }
        );
    }// end of MAIN

    /**
     * Метод вызываемый после создания формы, но до ее показа, его назначение выполнить донастройку элементов управления.
     * заполнить их данными и установить начальное состояние элементов управления.
     */
    private void invokeAfterCreation() {
        CommandLine.options.put("--encoding=", "utf-8");
        // заполн¤ем список кодировок
        SortedMap<String, Charset> map = Charset.availableCharsets();
        Set<String> ks = map.keySet();
        String objUtf8 = null;
        String objCp1251 = null;
        for (String k : ks) {
            comboCharSetIn.addItem(k);
            comboCharSetOut.addItem(k);
            if (k.equalsIgnoreCase("utf-8"))
                objUtf8 = k;
            if (k.equalsIgnoreCase("windows-1251"))
                objCp1251 = k;
        }// ---- for ----

        comboCharSetIn.setSelectedItem(objUtf8);
        comboCharSetOut.setSelectedItem(objCp1251);

        String curPath = new File(".").getAbsolutePath();
        txtTemplateName.setText(curPath + File.separator + "template.html");
        txtOutputDir.setText(curPath + File.separator + "output" + File.separator);

        btnStop.setEnabled(false);
        btnSuspendResume.setEnabled(false);
        pBar.setIndeterminate(false);

        lstLog.setModel(new DefaultListModel());

        wia = new WikiaWorker(
                txtServerName.getText(),
                txtAllPagesWikiName.getText(),
                txtOutputDir.getText(),
                "" + comboCharSetIn.getSelectedItem(),
                "" + comboCharSetOut.getSelectedItem(),
                txtCatalogWikiName.getText(),
                txtXPathForContent.getText(),
                txtDropXPathTags.getText(),
                txtTemplateName.getText(),
                txtSkipByExtension.getText(),
                txtSkipSpecialURLs.getText(),
                comboNameSpacesPolicy.getSelectedIndex(),
                txtAllowedNameSpacesList.getText()
        );
    }

    /**
     * Ссылка на объект "рабочий"
     */
    WikiaWorker wia = null;

    /**
     * Конструктор класса. В нем назначаются обработчики событий
     */
    public MainWikiaFormAsIdea() {
        // выбор каталога, куда поместить загруженные из internet файлы
        btnSelectOutputDir.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                // выбор каталога места назначения
                JFileChooser jfc = new JFileChooser();
                jfc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
                if (jfc.showOpenDialog(megapane) == JFileChooser.APPROVE_OPTION) {
                    txtOutputDir.setText(jfc.getSelectedFile().getAbsolutePath());
                }
            }
        });
        // выбор файла шаблона
        btnSelectTemplateFile.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                JFileChooser jfc = new JFileChooser();
                jfc.setFileSelectionMode(JFileChooser.FILES_ONLY);
                if (jfc.showOpenDialog(megapane) == JFileChooser.APPROVE_OPTION) {
                    txtTemplateName.setText(jfc.getSelectedFile().getAbsolutePath());
                }
            }
        });
        // отображаем диалоговое окно настроек TagSoap
        btnTagSoupSettings.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                GUITagSoapOptions.getParams((JFrame) SwingUtilities.getRoot(megapane));
            }
        }
        );

        // кнопка запуска работы приложения
        // сначала контроль за правильностью заполнения полей в форме
        btnStart.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                if (!testFieldNotEmpty(txtServerName.getText(), "Имя сервера где размещена wiki")) return;
                if (!testFieldNotEmpty(txtAllPagesWikiName.getText(), "Адрес специальной страницы wiki - список всех страниц"))
                    return;
                if (!testFieldNotEmpty(txtOutputDir.getText(), "Каталог, куда будут сохранены результаты работы"))
                    return;

                wia = new WikiaWorker(
                        txtServerName.getText(),
                        txtAllPagesWikiName.getText(),
                        txtOutputDir.getText(),
                        "" + comboCharSetIn.getSelectedItem(),
                        "" + comboCharSetOut.getSelectedItem(),
                        txtCatalogWikiName.getText(),
                        txtXPathForContent.getText(),
                        txtDropXPathTags.getText(),
                        txtTemplateName.getText(),
                        txtSkipByExtension.getText(),
                        txtSkipSpecialURLs.getText(),
                        comboNameSpacesPolicy.getSelectedIndex(),
                        txtAllowedNameSpacesList.getText()
                );

                btnStop.setEnabled(true);
                btnSuspendResume.setEnabled(true);
                pages_main.setSelectedIndex(1);
                new Thread() {
                    public void run() {
                        try {
                            btnStart.setEnabled(false);
                            btnStop.setEnabled(true);
                            pBar.setIndeterminate(true);

                            wia.work((DefaultListModel) lstLog.getModel());
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        finally {
                            btnStart.setEnabled(true);
                            btnStop.setEnabled(false);
                            pBar.setIndeterminate(false);
                        }
                    }
                }.start();
            }// end of actionPreformed
        });

        // кнопка проверки валидность http-сервера на котором размещена mediawiki
        btnTestServerPath.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                Boolean testOk = false;
                try {
                    URL ula = new URL(txtServerName.getText());
                    URLConnection coco = ula.openConnection();
                    coco.connect();
                    coco.getContent();
                    testOk = true;
                }
                catch (Exception eee) {
                    eee.printStackTrace();
                }
                if (testOk)
                    JOptionPane.showMessageDialog(megapane, "Порядок, указанный вами путь к вики серверу валиден и может быть использован");
                else
                    JOptionPane.showMessageDialog(megapane, "Ошибка, указанный вами путь к вики серверу не может быть найден");
            }
        });

        // обработка события для кнопки остановить загрузку файлов
        btnStop.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                wia.makeStopProject();
                btnSuspendResume.setEnabled(false);
                btnStart.setEnabled(true);
                btnStop.setEnabled(false);
                pBar.setIndeterminate(false);
            }
        });

        // кнопка загрузки проекта из файла
        loadProjectAs.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                JFileChooser jfc = new JFileChooser();
                jfc.setFileFilter(new MyFileFilter());
                jfc.setFileSelectionMode(JFileChooser.FILES_ONLY);
                jfc.setMultiSelectionEnabled(false);

                if (jfc.showOpenDialog(megapane) == JFileChooser.APPROVE_OPTION) {
                    String fname = jfc.getSelectedFile().getAbsolutePath();
                    if (fname.indexOf(MyFileFilter.WIKI2CHM) == -1)
                        fname += MyFileFilter.WIKI2CHM;
                    if (!new File(fname).exists()) {
                        JOptionPane.showMessageDialog(MainWikiaFormAsIdea.this.pages_main, "Ошибка, указанный вами файл не существует");
                        return;
                    }
                    txtFileOfProject.setText(fname);
                    XMLDecoder dec = null;
                    try {
                        dec = new XMLDecoder(new FileInputStream(fname));

                        MainWikiaFormAsIdeaBean bean = (MainWikiaFormAsIdeaBean) dec.readObject();
                        wia.state = (WikiaWorkerStateObject) dec.readObject();
                        setData(bean);
                    } catch (FileNotFoundException e1) {
                        e1.printStackTrace();
                    }
                }
            }
        });

        // кнопка сохранения проекта
        saveProjectAs.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                JFileChooser jfc = new JFileChooser();
                jfc.setFileFilter(new MyFileFilter());
                jfc.setFileSelectionMode(JFileChooser.FILES_ONLY);
                if (jfc.showSaveDialog(megapane) == JFileChooser.APPROVE_OPTION) {
                    String fname = jfc.getSelectedFile().getAbsolutePath();
                    if (fname.indexOf(MyFileFilter.WIKI2CHM) == -1)
                        fname += MyFileFilter.WIKI2CHM;

                    txtFileOfProject.setText(fname);
                    XMLEncoder enc = null;
                    try {
                        MainWikiaFormAsIdeaBean bean = new MainWikiaFormAsIdeaBean();
                        getData(bean);
                        enc = new XMLEncoder(new FileOutputStream(fname));
                        enc.writeObject(bean);
                        enc.writeObject(wia.state);
                        enc.flush();
                        enc.close();
                    } catch (FileNotFoundException e1) {
                        e1.printStackTrace();
                    }
                }
            }
        });

        // кнопка продолжить работу проекта или приостановить его
        btnSuspendResume.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                if (wia.getProjectIsRunning()) {
                    btnStart.setEnabled(false);
                    btnStop.setEnabled(true);
                    pBar.setIndeterminate(false);
                } else {
                    btnStart.setEnabled(false);
                    btnStop.setEnabled(true);
                    pBar.setIndeterminate(true);
                }
                wia.makeResumeOrSuspendProject((DefaultListModel) lstLog.getModel());

            }
        });
    }

    /**
     * Вспомогательная функция тестирования валидности некоторого значения текстового поля
     *
     * @param strForTest
     * @param strMessageIfError
     * @return
     */
    private boolean testFieldNotEmpty(String strForTest, String strMessageIfError) {
        if (strForTest == null || strForTest.length() == 0) {
            JOptionPane.showMessageDialog(megapane, "Для продолжения работы необходимо указать \"" + strMessageIfError + "\"");
            return false;
        }
        return true;
    }


    public JPanel getMegapane() {
        return megapane;
    }

    private void createUIComponents() {

    }

    /**
     * Наполнение информацией элементов управления на основании модели данных
     *
     * @param data Объект содержащий сведения обо всех полях, падающих списков ... формы
     */
    public void setData(MainWikiaFormAsIdeaBean data) {
        txtServerName.setText(data.getTxtServerName());
        txtAllPagesWikiName.setText(data.getTxtAllPagesWikiName());
        txtOutputDir.setText(data.getTxtOutputDir());
        txtCatalogWikiName.setText(data.getTxtCatalogWikiName());
        txtXPathForContent.setText(data.getTxtXPathForContent());
        txtDropXPathTags.setText(data.getTxtDropXPathTags());
        txtTemplateName.setText(data.getTxtTemplateName());
        txtFileOfProject.setText(data.getTxtFileOfProject());
        comboCharSetIn.setSelectedIndex(data.getComboSelectedCharSetInIndex());
        comboCharSetOut.setSelectedIndex(data.getComboSelectedCharSetOutIndex());
        txtSkipByExtension.setText(data.getTxtSkipByExtension());
        txtSkipSpecialURLs.setText(data.getTxtSkipSpecialURLs());

        txtAllowedNameSpacesList.setText(data.getAllowedNameSpacesList());
        comboNameSpacesPolicy.setSelectedIndex(data.getNamespacesPolicy());

        btnStart.setEnabled(data.getButtonStartEnabled());
        btnStop.setEnabled(data.getButtonStopEnabled());
        btnSuspendResume.setEnabled(data.getButtonButtonSuspendResume());
    }

    /**
     * Получение данных из элементов управления и упаковка их в виде объекта MainWikiaFormAsIdeaBean
     *
     * @param data объект, куда нужно поместить информацию взятую из текстовых полей
     */
    public void getData(MainWikiaFormAsIdeaBean data) {
        data.setTxtServerName(txtServerName.getText());
        data.setTxtAllPagesWikiName(txtAllPagesWikiName.getText());
        data.setTxtOutputDir(txtOutputDir.getText());
        data.setTxtCatalogWikiName(txtCatalogWikiName.getText());
        data.setTxtXPathForContent(txtXPathForContent.getText());
        data.setTxtDropXPathTags(txtDropXPathTags.getText());
        data.setTxtTemplateName(txtTemplateName.getText());
        data.setTxtFileOfProject(txtFileOfProject.getText());
        data.setComboSelectedCharSetInIndex(comboCharSetIn.getSelectedIndex());
        data.setComboSelectedCharSetOutIndex(comboCharSetOut.getSelectedIndex());
        data.setTxtSkipByExtension(txtSkipByExtension.getText());
        data.setTxtSkipSpecialURLs(txtSkipSpecialURLs.getText());

        data.setAllowedNameSpaces(txtAllowedNameSpacesList.getText());
        data.setNamespacesPolicy(comboNameSpacesPolicy.getSelectedIndex());

        data.setButtonStartEnabled(btnStart.isEnabled());
        data.setButtonStopEnabled(btnStop.isEnabled());
        data.setButtonButtonSuspendResume(btnSuspendResume.isEnabled());
    }

    /**
     * ПРоверка того синхронизированы ли элементы управления с моделью данных
     *
     * @param data модель данных
     * @return совпадают ли значения всех элементов управления с соответствующими значениями в модели данных
     */
    public boolean isModified(MainWikiaFormAsIdeaBean data) {
        if (txtServerName.getText() != null ? !txtServerName.getText().equals(data.getTxtServerName()) : data.getTxtServerName() != null)
            return true;
        if (txtAllPagesWikiName.getText() != null ? !txtAllPagesWikiName.getText().equals(data.getTxtAllPagesWikiName()) : data.getTxtAllPagesWikiName() != null)
            return true;
        if (txtOutputDir.getText() != null ? !txtOutputDir.getText().equals(data.getTxtOutputDir()) : data.getTxtOutputDir() != null)
            return true;
        if (txtCatalogWikiName.getText() != null ? !txtCatalogWikiName.getText().equals(data.getTxtCatalogWikiName()) : data.getTxtCatalogWikiName() != null)
            return true;
        if (txtXPathForContent.getText() != null ? !txtXPathForContent.getText().equals(data.getTxtXPathForContent()) : data.getTxtXPathForContent() != null)
            return true;
        if (txtDropXPathTags.getText() != null ? !txtDropXPathTags.getText().equals(data.getTxtDropXPathTags()) : data.getTxtDropXPathTags() != null)
            return true;
        if (txtTemplateName.getText() != null ? !txtTemplateName.getText().equals(data.getTxtTemplateName()) : data.getTxtTemplateName() != null)
            return true;
        if (txtFileOfProject.getText() != null ? !txtFileOfProject.getText().equals(data.getTxtFileOfProject()) : data.getTxtFileOfProject() != null)
            return true;
        if (txtAllowedNameSpacesList.getText() != null ? !txtAllowedNameSpacesList.getText().equals(data.getAllowedNameSpacesList()) : data.getAllowedNameSpacesList() != null)
            return true;

        if (comboNameSpacesPolicy.getSelectedIndex() != data.getNamespacesPolicy())
            return true;


        return false;
    }

    {
// GUI initializer generated by IntelliJ IDEA GUI Designer
// >>> IMPORTANT!! <<<
// DO NOT EDIT OR ADD ANY CODE HERE!
        $$$setupUI$$$();
    }

    /**
     * Method generated by IntelliJ IDEA GUI Designer
     * >>> IMPORTANT!! <<<
     * DO NOT edit this method OR call it in your code!
     *
     * @noinspection ALL
     */
    private void $$$setupUI$$$() {
        megapane = new JPanel();
        megapane.setLayout(new GridBagLayout());
        megapane.setPreferredSize(new Dimension(860, 640));
        pages_main = new JTabbedPane();
        GridBagConstraints gbc;
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        gbc.fill = GridBagConstraints.BOTH;
        megapane.add(pages_main, gbc);
        tab_config = new JPanel();
        tab_config.setLayout(new FormLayout("fill:10px:noGrow,left:4dlu:noGrow,fill:max(d;4px):noGrow,left:4dlu:noGrow,fill:d:grow,left:4dlu:noGrow,fill:160px:noGrow,left:4dlu:noGrow,fill:max(d;4px):noGrow,left:4dlu:noGrow,fill:max(d;4px):noGrow", "center:max(d;4px):noGrow,top:4dlu:noGrow,center:max(d;4px):noGrow,top:4dlu:noGrow,center:max(d;4px):noGrow,top:4dlu:noGrow,center:max(d;4px):noGrow,top:4dlu:noGrow,center:max(d;4px):noGrow,top:4dlu:noGrow,center:max(d;4px):noGrow,top:4dlu:noGrow,top:100px:noGrow,top:4dlu:noGrow,top:p:noGrow,top:4dlu:noGrow,top:max(d;4px):noGrow,top:4dlu:noGrow,center:max(d;4px):noGrow,top:4dlu:noGrow,top:40px:noGrow,top:5dlu:noGrow,center:max(d;4px):noGrow,top:4dlu:noGrow,center:max(d;4px):noGrow,top:4dlu:noGrow,top:p:noGrow,top:4dlu:noGrow,center:max(d;4px):noGrow,top:4dlu:noGrow,center:30px:noGrow,top:32px:noGrow,center:max(d;4px):noGrow"));
        pages_main.addTab("Настройки", new ImageIcon(getClass().getResource("/nbform/assets/attach24.png")), tab_config);
        txtServerName = new JTextField();
        txtServerName.setText("http://center:89/mediawiki/");
        CellConstraints cc = new CellConstraints();
        tab_config.add(txtServerName, cc.xy(5, 1, CellConstraints.FILL, CellConstraints.DEFAULT));
        final JLabel label1 = new JLabel();
        label1.setText("Имя сервера Wiki");
        tab_config.add(label1, cc.xy(3, 1));
        final JLabel label2 = new JLabel();
        label2.setText("Имя страницы \"Список всех страниц\"");
        tab_config.add(label2, cc.xy(3, 3));
        txtAllPagesWikiName = new JTextField();
        txtAllPagesWikiName.setText("index.php/%D0%A1%D0%BB%D1%83%D0%B6%D0%B5%D0%B1%D0%BD%D0%B0%D1%8F:Allpages");
        tab_config.add(txtAllPagesWikiName, cc.xyw(5, 3, 3, CellConstraints.FILL, CellConstraints.DEFAULT));
        final JLabel label3 = new JLabel();
        label3.setText("Сохранить результаты куда?");
        tab_config.add(label3, cc.xy(3, 5));
        txtOutputDir = new JTextField();
        txtOutputDir.setText("output/");
        tab_config.add(txtOutputDir, cc.xy(5, 5, CellConstraints.FILL, CellConstraints.DEFAULT));
        btnSelectOutputDir = new JButton();
        btnSelectOutputDir.setText("Выбрать каталог");
        tab_config.add(btnSelectOutputDir, cc.xy(7, 5));
        final JLabel label4 = new JLabel();
        label4.setText("Кодировка (входная и выходная)");
        tab_config.add(label4, cc.xy(3, 7));
        comboCharSetIn = new JComboBox();
        tab_config.add(comboCharSetIn, cc.xy(5, 7));
        final JLabel label5 = new JLabel();
        label5.setText("Имя каталога в котором на сервера размещена wiki");
        tab_config.add(label5, cc.xy(3, 9));
        txtCatalogWikiName = new JTextField();
        txtCatalogWikiName.setText("/mediawiki/");
        tab_config.add(txtCatalogWikiName, cc.xyw(5, 9, 3, CellConstraints.FILL, CellConstraints.DEFAULT));
        final JLabel label6 = new JLabel();
        label6.setText("XPath-выражение отбора информационного узла");
        tab_config.add(label6, cc.xy(3, 11));
        txtXPathForContent = new JTextField();
        txtXPathForContent.setText("//*[name() = 'div' and @id= 'content' ]");
        tab_config.add(txtXPathForContent, cc.xyw(5, 11, 3, CellConstraints.FILL, CellConstraints.DEFAULT));
        final JLabel label7 = new JLabel();
        label7.setText("Удалить из исходного документа узлы по XPath");
        tab_config.add(label7, cc.xy(3, 13));
        txtDropXPathTags = new JTextArea();
        txtDropXPathTags.setText("//* [name () = 'form' ] \n//* [name () = 'div' and @class='printfooter']\n//* [name () = 'span' and @class='editsection']\n//* [name () = 'div' and @id='jump-to-nav']\n//* [name () = 'script']\n//* [name () = 'style']\n//* [@id = 'ca-nstab-main']\n//* [@id = 'ca-talk']\n//* [@id = 'ca-edit']\n//* [@id = 'ca-history']\n//* [@id = 'pt-login']\n");
        tab_config.add(txtDropXPathTags, cc.xyw(5, 13, 3, CellConstraints.FILL, CellConstraints.FILL));
        final JLabel label8 = new JLabel();
        label8.setText("Укажите шаблон страницы");
        tab_config.add(label8, cc.xy(3, 23));
        txtTemplateName = new JTextField();
        txtTemplateName.setText("template.html");
        tab_config.add(txtTemplateName, cc.xy(5, 23, CellConstraints.FILL, CellConstraints.DEFAULT));
        btnSelectTemplateFile = new JButton();
        btnSelectTemplateFile.setText("Выбрать файл");
        tab_config.add(btnSelectTemplateFile, cc.xy(7, 23));
        paneBtnsZoneGoStop = new JPanel();
        paneBtnsZoneGoStop.setLayout(new FormLayout("fill:max(d;4px):noGrow,left:4dlu:noGrow,fill:max(d;4px):noGrow,left:4dlu:noGrow,fill:max(d;4px):noGrow,left:4dlu:noGrow,fill:d:grow,left:4dlu:noGrow,fill:max(d;4px):noGrow", "fill:max(d;4px):noGrow"));
        tab_config.add(paneBtnsZoneGoStop, cc.xyw(3, 29, 5));
        btnStart = new JButton();
        btnStart.setText("Запуск");
        paneBtnsZoneGoStop.add(btnStart, cc.xy(1, 1));
        final Spacer spacer1 = new Spacer();
        paneBtnsZoneGoStop.add(spacer1, cc.xy(7, 1, CellConstraints.FILL, CellConstraints.DEFAULT));
        btnTagSoupSettings = new JButton();
        btnTagSoupSettings.setText("Настройки TagSoup");
        paneBtnsZoneGoStop.add(btnTagSoupSettings, cc.xy(9, 1));
        btnStop = new JButton();
        btnStop.setText("Остановить");
        paneBtnsZoneGoStop.add(btnStop, cc.xy(3, 1));
        btnSuspendResume = new JButton();
        btnSuspendResume.setText("Приостановить - Продолжить");
        paneBtnsZoneGoStop.add(btnSuspendResume, cc.xy(5, 1));
        final JPanel panel1 = new JPanel();
        panel1.setLayout(new FormLayout("fill:max(d;4px):noGrow,left:4dlu:noGrow,fill:84px:grow,left:5dlu:noGrow,fill:200px:noGrow,left:4dlu:noGrow,fill:max(d;4px):noGrow,left:4dlu:noGrow,fill:d:grow,left:4dlu:noGrow,fill:max(d;4px):noGrow,left:4dlu:noGrow,fill:d:grow", "fill:28px:noGrow"));
        tab_config.add(panel1, cc.xyw(3, 31, 5));
        saveProjectAs = new JButton();
        saveProjectAs.setHorizontalAlignment(0);
        saveProjectAs.setIcon(new ImageIcon(getClass().getResource("/nbform/assets/save24.png")));
        saveProjectAs.setText("Сохранить");
        panel1.add(saveProjectAs, cc.xy(13, 1));
        txtFileOfProject = new JTextField();
        panel1.add(txtFileOfProject, cc.xyw(4, 1, 6, CellConstraints.FILL, CellConstraints.DEFAULT));
        final JLabel label9 = new JLabel();
        label9.setText("Файл проекта");
        panel1.add(label9, cc.xy(3, 1));
        loadProjectAs = new JButton();
        loadProjectAs.setIcon(new ImageIcon(getClass().getResource("/nbform/assets/copy_to_folder24.png")));
        loadProjectAs.setText("Загрузить");
        panel1.add(loadProjectAs, cc.xy(11, 1));
        btnTestServerPath = new JButton();
        btnTestServerPath.setText("Проверить путь");
        tab_config.add(btnTestServerPath, cc.xy(7, 1));
        final JLabel label10 = new JLabel();
        label10.setText("Не выполнять запрос файлов по расширениям");
        tab_config.add(label10, cc.xy(3, 15));
        txtSkipByExtension = new JTextArea();
        txtSkipByExtension.setText("rar,zip,pdf,exe,jar");
        tab_config.add(txtSkipByExtension, cc.xywh(5, 15, 3, 3, CellConstraints.FILL, CellConstraints.FILL));
        final JLabel label11 = new JLabel();
        label11.setText("(через запятую)");
        tab_config.add(label11, cc.xy(3, 17));
        final JLabel label12 = new JLabel();
        label12.setText("Исключить страницы в адресе которых присуствует");
        tab_config.add(label12, cc.xywh(3, 21, 1, 2, CellConstraints.DEFAULT, CellConstraints.TOP));
        txtSkipSpecialURLs = new JTextArea();
        txtSkipSpecialURLs.setMinimumSize(new Dimension(49, 12));
        txtSkipSpecialURLs.setPreferredSize(new Dimension(49, 12));
        txtSkipSpecialURLs.setText("?index=\n?file=");
        tab_config.add(txtSkipSpecialURLs, cc.xywh(5, 19, 3, 3, CellConstraints.FILL, CellConstraints.FILL));
        comboCharSetOut = new JComboBox();
        tab_config.add(comboCharSetOut, cc.xy(7, 7));
        final JLabel label13 = new JLabel();
        label13.setText("Список разрешенных пространств имен (через запятую)");
        tab_config.add(label13, cc.xy(3, 27));
        pBar = new JProgressBar();
        tab_config.add(pBar, cc.xywh(3, 32, 5, 2, CellConstraints.FILL, CellConstraints.DEFAULT));
        final JLabel label14 = new JLabel();
        label14.setText("Разрешены ли обращения к страницам через \":\"");
        tab_config.add(label14, cc.xy(3, 25));
        comboNameSpacesPolicy = new JComboBox();
        final DefaultComboBoxModel defaultComboBoxModel1 = new DefaultComboBoxModel();
        defaultComboBoxModel1.addElement("Запрещены все, кроме основного пространства имен");
        defaultComboBoxModel1.addElement("Разрешены все, кроме основного пространства имен");
        defaultComboBoxModel1.addElement("Запрещены все перечисленные пространства имен");
        defaultComboBoxModel1.addElement("Разрешены все перечисленные пространства имен");
        defaultComboBoxModel1.addElement("Запрещены все перечисленные пространства имен + основное");
        defaultComboBoxModel1.addElement("Разрешены все перечисленные пространства имен + основное");
        comboNameSpacesPolicy.setModel(defaultComboBoxModel1);
        comboNameSpacesPolicy.setSelectedIndex(5);
        tab_config.add(comboNameSpacesPolicy, cc.xyw(5, 25, 3));
        txtAllowedNameSpacesList = new JTextField();
        txtAllowedNameSpacesList.setText("");
        tab_config.add(txtAllowedNameSpacesList, cc.xyw(5, 27, 3, CellConstraints.FILL, CellConstraints.DEFAULT));
        tab_log = new JPanel();
        tab_log.setLayout(new GridBagLayout());
        pages_main.addTab("Журнал выполнения запрошенной операции", new ImageIcon(getClass().getResource("/nbform/assets/download_headers24.png")), tab_log);
        final JScrollPane scrollPane1 = new JScrollPane();
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        gbc.fill = GridBagConstraints.BOTH;
        tab_log.add(scrollPane1, gbc);
        lstLog = new JList();
        scrollPane1.setViewportView(lstLog);
        final JPanel spacer2 = new JPanel();
        gbc = new GridBagConstraints();
        gbc.gridx = 1;
        gbc.gridy = 0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        megapane.add(spacer2, gbc);
        final JPanel spacer3 = new JPanel();
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.fill = GridBagConstraints.VERTICAL;
        megapane.add(spacer3, gbc);
        label1.setLabelFor(txtServerName);
        label2.setLabelFor(txtAllPagesWikiName);
        label3.setLabelFor(txtOutputDir);
        label4.setLabelFor(comboCharSetIn);
        label5.setLabelFor(txtCatalogWikiName);
        label6.setLabelFor(txtXPathForContent);
        label7.setLabelFor(txtDropXPathTags);
        label8.setLabelFor(txtTemplateName);
        label9.setLabelFor(txtFileOfProject);
        label10.setLabelFor(txtDropXPathTags);
    }

    /**
     * @noinspection ALL
     */
    public JComponent $$$getRootComponent$$$() {
        return megapane;
    }

    /**
     * Вспомогательный класс фильтра используется для отображения фильтра файлов при сохранении или чтении файла проекта
     * это файлы с расширением wiki2chm
     */
    private static class MyFileFilter extends javax.swing.filechooser.FileFilter {
        private static final String WIKI2CHM = ".wiki2chm";

        public boolean accept(File f) {
            return f.getName().endsWith(WIKI2CHM) || f.isDirectory();
        }

        public String getDescription() {
            return "Wiki2Chm Project File (" + WIKI2CHM + ")";
        }
    }
}

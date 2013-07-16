package paxeq;

import org.apache.xerces.parsers.DOMParser;
import org.apache.xml.serialize.OutputFormat;
import org.apache.xml.serialize.XMLSerializer;
import org.ccil.cowan.tagsoup.CommandLine;
import org.syntax.jedit.JEditTextArea;
import org.w3c.dom.*;
import org.w3c.dom.traversal.NodeFilter;
import org.xml.sax.EntityResolver;
import org.xml.sax.InputSource;

import javax.swing.*;
import javax.swing.event.TreeModelListener;
import javax.swing.filechooser.FileFilter;
import javax.swing.tree.TreeModel;
import javax.swing.tree.TreePath;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.io.*;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.*;


public class XCmpFrame extends JFrame {
    AboutDialog about = new AboutDialog();
    PaneDiffResults paneDiffsResultsImpl = new PaneDiffResults();
    JSplitPane splitDiffsAndAll = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
    ConfigColorsClass configColors = new ConfigColorsClass(this);
    SimpleDateFormat common_df = new SimpleDateFormat("dd-mmmm-yyyy hh:MM:ss");
    NodeFilter filter = new NodeFilter() {
        public short acceptNode(Node n) {
            if (n instanceof Attr)
                return checkAttrs.isSelected() ? NodeFilter.FILTER_ACCEPT : NodeFilter.FILTER_SKIP;
            if (n instanceof Comment)
                return checkComments.isSelected() ? NodeFilter.FILTER_ACCEPT : NodeFilter.FILTER_SKIP;
            if (n instanceof ProcessingInstruction)
                return checkPI.isSelected() ? NodeFilter.FILTER_ACCEPT : NodeFilter.FILTER_SKIP;
            if (n instanceof CharacterData)
                return checkText.isSelected() ? NodeFilter.FILTER_ACCEPT : NodeFilter.FILTER_SKIP;
            if (n instanceof EntityReference)
                return checkEntities.isSelected() ? NodeFilter.FILTER_ACCEPT : NodeFilter.FILTER_SKIP;
            return NodeFilter.FILTER_ACCEPT;
        }
    };
    /*����� ������� ����� ������ ��� �������� ����������*/

    XMLSerializer domSer = new XMLSerializer();

    Document original_LeftDocument;
    Document original_RightDocument;


    JMenu mainMenuFile = new JMenu("����");
    JMenu mainMenuXML = new JMenu("XML");

    JMenu recentLeft = new JMenu("����������� �������� ����� >>>");

    JMenu recentRight = new JMenu("����������� �������� ������ >>>");

    JTextField fldDefaultEncoding = new JTextField("cp1251");

    ArrayList<URL> precachedLeft = new ArrayList<URL>();
    ArrayList<URL> precachedRight = new ArrayList<URL>();


    JMenuBar mainMenuBar = new JMenuBar();

    JMenu menuShowHide = new JMenu("�������� | ��������");

    File LastLeftDir = new File(".");
    File LastRightDir = new File(".");

    String LastLeftWWW = "http://";
    String LastRightWWW = "http://";

    JSplitPane masterSplit = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
    JTabbedPane masterTabLeft = new JTabbedPane();
    JTabbedPane masterTabRight = new JTabbedPane();


    JEditTextArea masterTxtLeft = new JEditTextArea();
    JEditTextArea masterTxtRight = new JEditTextArea();


    JTextArea masterOriginalLeft = new JTextArea();
    JTextArea masterOriginalRight = new JTextArea();


    JXmlTree masterTreeLeft = new JXmlTree();
    JXmlTree masterTreeRight = new JXmlTree();

    XPathPane masterXPathLeft = new XPathPane(masterTreeLeft);
    XPathPane masterXPathRight = new XPathPane(masterTreeRight);


    JCheckBoxMenuItem checkText = new JCheckBoxMenuItem("��������� ����", new ImageIcon(XCmpFrame.class.getResource("img_cdata.gif")), true);
    JCheckBoxMenuItem checkComments = new JCheckBoxMenuItem("���� ������������", new ImageIcon(XCmpFrame.class.getResource("img_comment.gif")), true);
    JCheckBoxMenuItem checkPI = new JCheckBoxMenuItem("���� PI", new ImageIcon(XCmpFrame.class.getResource("img_pi.gif")), true);
    JCheckBoxMenuItem checkAttrs = new JCheckBoxMenuItem("��������", new ImageIcon(XCmpFrame.class.getResource("img_attribute.gif")), true);
    JCheckBoxMenuItem checkEntities = new JCheckBoxMenuItem("��������", new ImageIcon(XCmpFrame.class.getResource("img_entity.gif")), true);


    public XCmpFrame() throws HeadlessException {
        super("XComparator -- ����� ��� ��������� XML|XHTML|HTML ���������� -- by black zorro (x15@tut.by)");

        ToolTipManager.sharedInstance().registerComponent(masterTreeLeft);
        ToolTipManager.sharedInstance().registerComponent(masterTreeRight);


        mainMenuBar.add(mainMenuFile);
        mainMenuBar.add(mainMenuXML);
        setJMenuBar(mainMenuBar);

        masterSplit.setLeftComponent(masterTabLeft);
        masterSplit.setRightComponent(masterTabRight);


        final JScrollPane scrollLeftOriginalPane = new JScrollPane(masterOriginalLeft);
        masterTabLeft.add("�������� ��������", scrollLeftOriginalPane);


        final JScrollPane scrollLeftTxtPane = new JScrollPane(masterTxtLeft);
        masterTabLeft.add("�����������������", scrollLeftTxtPane);



        final JScrollPane scrollRightOriginalPane = new JScrollPane(masterOriginalRight);
        masterTabRight.add("�������� ��������)", scrollRightOriginalPane);


        final JScrollPane scrollRightTxtPane = new JScrollPane(masterTxtRight);
        masterTabRight.add("�����������������", scrollRightTxtPane);

        //****************************************************************************
        masterTxtLeft.setDocument(new org.syntax.jedit.SyntaxDocument());
        masterTxtLeft.setTokenMarker(new org.syntax.jedit.tokenmarker.XMLTokenMarker());
        masterTxtLeft.getPainter().setLineHighlightEnabled(false);
        configColors.ApplySettingsToJEditTextArea(masterTxtLeft);

        masterTxtRight.setDocument(new org.syntax.jedit.SyntaxDocument());
        masterTxtRight.setTokenMarker(new org.syntax.jedit.tokenmarker.XMLTokenMarker());
        masterTxtRight.getPainter().setLineHighlightEnabled(false);
        configColors.ApplySettingsToJEditTextArea(masterTxtRight);

        //*****************************************************************************

        JPanel leftTreeDock = new JPanel(new BorderLayout());
        JSplitPane splitterXPathLeft = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
        leftTreeDock.add(masterXPathLeft, BorderLayout.NORTH);
        splitterXPathLeft.setBottomComponent(new JScrollPane(masterTreeLeft));
        splitterXPathLeft.setTopComponent(masterXPathLeft.getTableResults());
        splitterXPathLeft.setOneTouchExpandable(true);
        leftTreeDock.add(splitterXPathLeft, BorderLayout.CENTER);

        JPanel rightTreeDock = new JPanel(new BorderLayout());
        rightTreeDock.add(masterXPathRight, BorderLayout.NORTH);
        JSplitPane splitterXPathRight = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
        splitterXPathRight.setBottomComponent(new JScrollPane(masterTreeRight));
        splitterXPathRight.setTopComponent(masterXPathRight.getTableResults());
        splitterXPathRight.setOneTouchExpandable(true);
        rightTreeDock.add(splitterXPathRight, BorderLayout.CENTER);

        masterTabLeft.add("������", leftTreeDock);
        masterTabRight.add("������", rightTreeDock);

        recentLeft.add(new AbstractAction("*** �������� ������� ����� ***") {
            public void actionPerformed(ActionEvent e) {
                for (int i = 0; i < precachedLeft.size(); i++)
                    recentLeft.remove(1);
                precachedLeft.clear();
            }
        });

        recentRight.add(new AbstractAction("*** �������� ������� ������� ***") {
            public void actionPerformed(ActionEvent e) {
                for (int i = 0; i < precachedRight.size(); i++)
                    recentRight.remove(1);
                precachedRight.clear();
            }
        });


        mainMenuFile.add(new AbstractAction("������� �������� ����� ��� **����**", new ImageIcon(XCmpFrame.class.getResource("mbtn_file.jpg"))) {
            public void actionPerformed(ActionEvent e) {
                JFileChooser jfc = new JFileChooser();
                jfc.setCurrentDirectory(LastLeftDir);
                jfc.setDialogType(JFileChooser.OPEN_DIALOG);
                jfc.setFileFilter(new XmlFilter());

                int returnVal = jfc.showOpenDialog(XCmpFrame.this);
                if (returnVal == JFileChooser.APPROVE_OPTION) {
                    File cur = jfc.getSelectedFile();
                    LastLeftDir = cur;
                    try {
                        AddRecentValue(cur.toURL(), true);
                        LoadXmlDocument(cur.toURL(), true);
                        masterTabLeft.setToolTipText(cur.toURL().toString() + "\n������ " + common_df.format(new Date()));
                    } catch (MalformedURLException e1) {
                        e1.printStackTrace();
                        JOptionPane.showMessageDialog(XCmpFrame.this, e1, "������, ��� ������� �������", JOptionPane.ERROR_MESSAGE);
                    }
                }
            }
        });

        mainMenuFile.add(new AbstractAction("������� �������� ������ ��� **����**", new ImageIcon(XCmpFrame.class.getResource("mbtn_file.jpg"))) {
            public void actionPerformed(ActionEvent e) {
                JFileChooser jfc = new JFileChooser();
                jfc.setCurrentDirectory(LastRightDir);
                jfc.setDialogType(JFileChooser.OPEN_DIALOG);
                jfc.setFileFilter(new XmlFilter());

                int returnVal = jfc.showOpenDialog(XCmpFrame.this);
                if (returnVal == JFileChooser.APPROVE_OPTION) {
                    File cur = jfc.getSelectedFile();
                    LastRightDir = cur;
                    try {
                        AddRecentValue(cur.toURL(), false);
                        LoadXmlDocument(cur.toURL(), false);
                        masterTabRight.setToolTipText(cur.toURL().toString() + "\n������ " + common_df.format(new Date()));
                    } catch (MalformedURLException e1) {
                        e1.printStackTrace();
                        JOptionPane.showMessageDialog(XCmpFrame.this, e1, "������ ��� �������� �������", JOptionPane.ERROR_MESSAGE);
                    }
                }
            }
        });

        mainMenuFile.add(new AbstractAction("������� �������� ����� ��� **���-��������**", new ImageIcon(XCmpFrame.class.getResource("mbtn_web.jpg"))) {
            public void actionPerformed(ActionEvent e) {
                Object val = JOptionPane.showInputDialog(XCmpFrame.this, LastLeftWWW);
                if (val == null) return;
                String tmp = (String) val;
                if (tmp.indexOf("://") == -1)
                    tmp = "http://" + tmp;
                LastLeftWWW = tmp;
                try {
                    final URL from = new URL(tmp);
                    AddRecentValue(from, true);
                    LoadXmlDocument(from, true);
                    masterTabLeft.setToolTipText(from.toString() + "\nopened at " + common_df.format(new Date()));
                } catch (MalformedURLException e1) {
                    e1.printStackTrace();
                    JOptionPane.showMessageDialog(XCmpFrame.this, e1, "������ ��� �������� �������", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        mainMenuFile.add(new AbstractAction("������� �������� ������ ��� **���-��������**", new ImageIcon(XCmpFrame.class.getResource("mbtn_web.jpg"))) {
            public void actionPerformed(ActionEvent e) {
                Object val = JOptionPane.showInputDialog(XCmpFrame.this, LastRightWWW);
                if (val == null) return;
                String tmp = (String) val;
                if (tmp.indexOf("://") == -1)
                    tmp = "http://" + tmp;
                LastRightWWW = tmp;
                try {
                    final URL from = new URL(tmp);
                    AddRecentValue(from, false);
                    LoadXmlDocument(from, false);
                    masterTabRight.setToolTipText(from.toString() + "\nopened at " + common_df.format(new Date()));
                } catch (MalformedURLException e1) {
                    e1.printStackTrace();
                    JOptionPane.showMessageDialog(XCmpFrame.this, e1, "������ ��� �������� �������", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        mainMenuFile.add(new JSeparator());
        JPanel paneEncoding = new JPanel(new BorderLayout());
        paneEncoding.add(new JLabel("��������� ��-���������: ", new ImageIcon(XCmpFrame.class.getResource("fbtn_encoding.jpg")), JLabel.LEFT), BorderLayout.WEST);
        paneEncoding.add(fldDefaultEncoding, BorderLayout.CENTER);
        mainMenuFile.add(paneEncoding);
        mainMenuFile.add(new JSeparator());


        mainMenuXML.add(
                new AbstractAction("��������� ������� TagSoap") {
                    public void actionPerformed(ActionEvent e) {
                        GUITagSoapOptions.getParams(XCmpFrame.this,
                                new ActionListener() {
                                    public void actionPerformed(ActionEvent e) {
                                        GUITagSoapOptions f = (GUITagSoapOptions) e.getSource();
                                        Hashtable h = new Hashtable();
                                        for (String key_name : f.model.key_names) {
                                            h.put(key_name, f.model.optName.get(key_name));
                                        }
                                        Set keys = h.keySet();
                                        for (Object key : keys) {
                                            Object value = h.get(key);
                                            if ("true".equalsIgnoreCase("" + value)) {
                                                value = Boolean.TRUE;
                                            } else {
                                                if ("false".equalsIgnoreCase("" + value)) {

                                                    value = Boolean.FALSE;
                                                }
                                            }
                                            CommandLine.options.put(key, value);
                                        }
                                    }// action performed -- action object as param
                                }// class ActionListener
                        );
                    }// actionPerformed
                }// AbstractAction
        );


        mainMenuXML.add(new AbstractAction("��������� �����", new ImageIcon(XCmpFrame.class.getResource("fbtn_colors.jpg"))) {
            public void actionPerformed(ActionEvent e) {
                configColors.setVisible(true);
                if (configColors.getModalResult()) {
                    configColors.ApplySettingsToJEditTextArea(masterTxtLeft);
                    configColors.ApplySettingsToJEditTextArea(masterTxtRight);
                }
            }
        }

        );

        recentLeft.setIcon(new ImageIcon(XCmpFrame.class.getResource("fbtn_reopen.jpg")));
        mainMenuFile.add(recentLeft);
        recentRight.setIcon(new ImageIcon(XCmpFrame.class.getResource("fbtn_reopen.jpg")));
        mainMenuFile.add(recentRight);

        mainMenuXML.add(menuShowHide);

        mainMenuXML.add(new AbstractAction("�������� ������� � ����� ����������", new ImageIcon(XCmpFrame.class.getResource("xcl_root.jpg"))) {
            public void actionPerformed(ActionEvent e) {
                try {
                    Node l = original_LeftDocument;
                    Node r = original_RightDocument;
                    CompareNodeSettings defset = new CompareNodeSettings(false);
                    paneDiffsResultsImpl.setCompareNodesInfo(l, r, defset);
                    paneDiffsResultsImpl.startComparission();
                } catch (Exception e1) {
                    JOptionPane.showMessageDialog(XCmpFrame.this, "��������� �� �������:\n" + e1);
                }
            }
        }

        );

        mainMenuXML.add(new AbstractAction("�������� ������� � ������� �����", new ImageIcon(XCmpFrame.class.getResource("xcl_root.jpg"))) {
            public void actionPerformed(ActionEvent e) {
                try {
                    Node l = (Node) masterTreeLeft.getSelectionPath().getLastPathComponent();
                    Node r = (Node) masterTreeRight.getSelectionPath().getLastPathComponent();
                    CompareNodeSettings defset = new CompareNodeSettings(false);
                    paneDiffsResultsImpl.setCompareNodesInfo(l, r, defset);
                    paneDiffsResultsImpl.startComparission();
                } catch (Exception e1) {
                    JOptionPane.showMessageDialog(XCmpFrame.this, "��������� �� �������:\n" + e1);
                }

            }
        }

        );

        menuShowHide.add(checkComments);
        menuShowHide.add(checkText);
        menuShowHide.add(checkPI);
        menuShowHide.add(checkAttrs);
        menuShowHide.add(checkEntities);

        final ActionListener adofilter = new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                ApplyCustomFilter();
            }
        };
        checkComments.addActionListener(adofilter);
        checkText.addActionListener(adofilter);
        checkPI.addActionListener(adofilter);
        checkAttrs.addActionListener(adofilter);
        checkEntities.addActionListener(adofilter);

        masterSplit.setOneTouchExpandable(true);
        masterSplit.setDividerLocation(400);


        splitDiffsAndAll.setOneTouchExpandable(true);
        splitDiffsAndAll.setTopComponent(masterSplit);
        splitDiffsAndAll.setBottomComponent(paneDiffsResultsImpl);
        splitDiffsAndAll.setDividerLocation(380);        

        getContentPane().add(splitDiffsAndAll);

        JMenu menuAbout = new JMenu("�������");
        menuAbout.add(new AbstractAction("� ���������") {
            public void actionPerformed(ActionEvent e) {
                about.setSize(400, 400);
                SwingUtilities.invokeLater(
                        new Runnable() {
                            public void run() {
                                about.setVisible(true);
                            }
                        }
                );
            }
        }

        );
        mainMenuBar.add(menuAbout);

        initJeditMouseSupport();
    }

    private void initJeditMouseSupport() {
        final int h = masterTxtLeft.getFontMetrics(masterTxtLeft.getFont()).getHeight();
        final MouseWheelListener l = new MouseWheelListener() {
            public void mouseWheelMoved(MouseWheelEvent e) {
                int units = e.getUnitsToScroll();
                int posFirstLine = masterTxtLeft.getFirstLine();
                int colOnScreen = masterTxtLeft.getBounds().height / h - 4;
                posFirstLine += units;
                if (posFirstLine < 0) posFirstLine = 0;
                if (posFirstLine >= (masterTxtLeft.getLineCount() - colOnScreen))
                    posFirstLine = masterTxtLeft.getLineCount() - 1 - colOnScreen;
                masterTxtLeft.setFirstLine(posFirstLine);
            }
        };
        final MouseWheelListener r = new MouseWheelListener() {
            public void mouseWheelMoved(MouseWheelEvent e) {
                int units = e.getUnitsToScroll();
                int posFirstLine = masterTxtRight.getFirstLine();
                int colOnScreen = masterTxtRight.getBounds().height / h - 4;
                posFirstLine += units;
                if (posFirstLine < 0) posFirstLine = 0;
                if (posFirstLine >= (masterTxtRight.getLineCount() - colOnScreen))
                    posFirstLine = masterTxtRight.getLineCount() - 1 - colOnScreen;
                masterTxtRight.setFirstLine(posFirstLine);
            }
        };

        masterTxtLeft.addMouseWheelListener(l);
        masterTxtRight.addMouseWheelListener(r);
    }

    private void ApplyCustomFilter() {
        try {
            if (original_LeftDocument != null) {
                Document filtered_doc = (Document) original_LeftDocument.cloneNode(true);
                filtered_doc = (Document) ClearNeedlessNodes(filtered_doc, filter);
                FillUIFromFilteredNode(filtered_doc, true);
            }
            if (original_RightDocument != null) {
                Document filtered_doc = (Document) original_RightDocument.cloneNode(true);
                filtered_doc = (Document) ClearNeedlessNodes(filtered_doc, filter);
                FillUIFromFilteredNode(filtered_doc, false);
            }

            invalidate();
        } catch (IOException e) {
            System.err.println("Exception While ApplyCustomFilter:\n exception = {" + e + "} ");
        }
    }

    private void AddRecentValue(final URL url, boolean isLeft) {
        if (isLeft) {
            for (Iterator<URL> iterator = precachedLeft.iterator(); iterator.hasNext();) {
                URL url1 = iterator.next();
                if (url1.sameFile(url)) return;
            }
            precachedLeft.add(url);
            recentLeft.add(new AbstractAction(url.toString()) {
                public void actionPerformed(ActionEvent e) {
                    LoadXmlDocument(url, true);
                }
            });
            return;
        }
        for (Iterator<URL> iterator = precachedRight.iterator(); iterator.hasNext();) {
            URL url1 = iterator.next();
            if (url1.sameFile(url)) return;
        }
        precachedRight.add(url);
        recentRight.add(new AbstractAction(url.toString()) {
            public void actionPerformed(ActionEvent e) {
                LoadXmlDocument(url, false);
            }
        });
    }


    public void LoadXmlDocument(URL from, boolean toLeft) {
        try {

            /*
            XMLParserConfiguration conf  = new StandardParserConfiguration();
            Object xb = conf.getProperty("validate-if-schema");
            conf.setProperty("validate-if-schema" , new Boolean (false));
            conf.setProperty("validate" , new Boolean (false));
            conf.setProperty("element-content-whitespace" , new Boolean (false));
            */

            BufferedReader brin = new BufferedReader(new InputStreamReader (from.openStream(), fldDefaultEncoding.getText()) );
            StringBuilder buf = new StringBuilder();
            String line;
            while ((line = brin.readLine()) != null){
                buf.append(line);
                buf.append("\n");
            }// -- while --
            brin.close();

            if (toLeft)
                masterOriginalLeft.setText(buf.toString());
            else
                masterOriginalRight.setText(buf.toString());            


            // �������� ��������� ������� ������������ ����������
            ByteArrayOutputStream bout = new ByteArrayOutputStream();
            CommandLine.process(from.toExternalForm(), bout  );
            

            DOMParser dp = new DOMParser();


            dp.setEntityResolver(new EntityResolver() {
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
                        JOptionPane.showMessageDialog(XCmpFrame.this, e, "������ �������� ���������� �������� � �� ������ \npublicId: " + publicId + "\nsystemId: " + systemId, JOptionPane.ERROR_MESSAGE);
                        return null;
                    }
                }
            });
            try {
                final InputSource inputSource = new InputSource(new ByteArrayInputStream (bout.toByteArray()) );
                inputSource.setSystemId(from.toExternalForm());
                dp.parse(inputSource);
            } catch (IOException e) {
                // � ��� ������ ������ �� ������ ������ ������ ����� ��������� �� �� ������������ ��������� --
                //  �������� ���������� �������� ��� ��������� windows-1251 � ��� ���� �� �������
                String defEnc = fldDefaultEncoding.getText().trim();
                dp.parse(new InputSource(new InputStreamReader(from.openStream(), defEnc)));
            }
            Document doc = dp.getDocument();
            String enc = null;
            try {
                enc = doc.getInputEncoding();
            } catch (Throwable e) {
                enc = "utf-8";
            }


            if (toLeft)// ��������� ������������ ������
                original_LeftDocument = doc;
            else
                original_RightDocument = doc;

            Document filtered_doc = (Document) doc.cloneNode(true);
            filtered_doc = (Document) ClearNeedlessNodes(filtered_doc, filter);


            FillUIFromFilteredNode(filtered_doc, toLeft);

            invalidate();
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(XCmpFrame.this, e, "������ �� ������ �������������� � �������� ��������� ���������", JOptionPane.ERROR_MESSAGE);
        }
    }

    protected void FillUIFromFilteredNode(Document filtered_doc, boolean toLeft) throws IOException {
        ByteArrayOutputStream chaw = new ByteArrayOutputStream(4096);
        OutputStreamWriter outw = new OutputStreamWriter(chaw, "cp1251");

        domSer.setOutputFormat(new OutputFormat("xml", "windows-1251", true));
        domSer.setOutputCharStream(outw);
        domSer.serialize(filtered_doc);
        outw.flush();
        String t = chaw.toString();
        if (toLeft) {
            masterTxtLeft.setText(t);
            masterTreeLeft.setModel(new XmlTreeModel(filtered_doc));
            original_LeftDocument = filtered_doc;
        } else {
            masterTxtRight.setText(t);
            masterTreeRight.setModel(new XmlTreeModel(filtered_doc));
            original_RightDocument = filtered_doc;
        }
    }

    private Node ClearNeedlessNodes(Node filtered_doc, NodeFilter filter) {
        if (filtered_doc == null) return null;
        if (filter.acceptNode(filtered_doc) != NodeFilter.FILTER_ACCEPT)
            return null;
        NodeList nli = filtered_doc.getChildNodes();
        ArrayList<Node> toBeDeleted = new ArrayList<Node>();
        for (int i = 0; i < nli.getLength(); i++) {
            Node now = nli.item(i);
            Node future = ClearNeedlessNodes(now, filter);
            if (future == null)
                toBeDeleted.add(now);
        }
        for (Iterator<Node> iterator = toBeDeleted.iterator(); iterator.hasNext();) {
            Node node = iterator.next();
            try {
                filtered_doc.removeChild(node);
            } catch (DOMException e) {
                System.err.println("Cannot Deleted Node: exception ={" + e + "} node = {" + node + "}");
            }
        }
        return filtered_doc;
    }


}//class

package paxeq;

import org.apache.xml.serialize.OutputFormat;
import org.apache.xml.serialize.XMLSerializer;
import org.w3c.dom.Attr;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

import javax.swing.*;
import javax.swing.table.AbstractTableModel;
import java.awt.*;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileWriter;
import java.io.OutputStreamWriter;


public class XmlInfoNodeDialog extends JDialog {

    static XMLSerializer domSer = new XMLSerializer();

    Node masterNode;

    JTabbedPane tabsInfo = new JTabbedPane();
    JTable tabAttrs = new JTable();

    class XmlAttrsTableModel extends AbstractTableModel {
        public int getRowCount() {
            if (masterNode == null) return 0;
            if (masterNode.getAttributes() == null) return 0;
            return masterNode.getAttributes().getLength();
        }

        public int getColumnCount() {
            return 4;
        }

        public Object getValueAt(int rowIndex, int columnIndex) {
            Attr at = (Attr) masterNode.getAttributes().item(rowIndex);
            if (columnIndex == 0) return at.getPrefix();
            if (columnIndex == 1) return at.getNamespaceURI();
            if (columnIndex == 2) return at.getName();
            return at.getValue();
        }

        public String getColumnName(int column) {
            if (column == 0) return "Attr: NS_URI";
            if (column == 1) return "Attr: NS_Prefix";
            if (column == 2) return "Attr: Name";
            return "Attr: Value";
        }
    }

    JTextPane txtPane = new JTextPane();
    JTree treeTailOfTree = new JTree();
    JTextPane txtPaneAsIs = new JTextPane();
    JTextPane txtPaneAsHtml = new JTextPane();

    public XmlInfoNodeDialog(Node masterNode) throws HeadlessException {
        this.masterNode = masterNode;
        setModal(true);
        getContentPane().add(tabsInfo);

        tabAttrs.setModel(new XmlAttrsTableModel());// устанавливаем модель данных для таблицы
        txtPane.setText(masterNode.getNodeValue());
        String tt = masterNode.getTextContent();
        txtPane.setText(tt);
        txtPane.setEditable(false);
        treeTailOfTree.setModel(new XmlTreeModel(masterNode));


        try {
            ByteArrayOutputStream chaw = new ByteArrayOutputStream(4096);
            OutputStreamWriter outw = new OutputStreamWriter(chaw, "cp1251");

            domSer.setOutputFormat(new OutputFormat("xml", "windows-1251", true));
            domSer.setOutputCharStream(outw);
            domSer.serialize((Element) masterNode);
            outw.flush();
            txtPaneAsIs.setEditable(false);
            String t = chaw.toString();
            txtPaneAsIs.setText(t);
            tabsInfo.add("As XML", new JScrollPane(txtPaneAsIs));

            chaw.reset();
            // заново сохраняем но уже в формате html
            OutputFormat outputFormat = new OutputFormat("html", "windows-1251", true);
            outputFormat.setOmitXMLDeclaration(true);
            domSer.setOutputFormat(outputFormat);

            domSer.setOutputCharStream(outw);
            domSer.serialize((Element) masterNode);
            outw.flush();
            txtPaneAsIs.setEditable(false);
            if (t.indexOf("<html") != 0)
                t = "<HTML>" + t;
            t = chaw.toString();

            File ftmp = File.createTempFile("dom_x", "cmp_x");
            ftmp.deleteOnExit();
            FileWriter fw = new FileWriter(ftmp);
            fw.write(t);
            fw.flush();
            fw.close();
            fw = null;
            txtPaneAsHtml.setEditable(false);
            txtPaneAsHtml.setPage(ftmp.toURL());
            tabsInfo.add("As HTML", new JScrollPane(txtPaneAsHtml));

        } catch (Exception e) {
            e.printStackTrace();
        }

        tabsInfo.add("Attributes", new JScrollPane(tabAttrs));
        tabsInfo.add("SubTree", new JScrollPane(treeTailOfTree));
        tabsInfo.add("Content", new JScrollPane(txtPane));

        setTitle(masterNode.getNamespaceURI() + " : " + masterNode.getPrefix() + " : " + masterNode.getNodeName());
        pack();


        Rectangle virtualBounds = new Rectangle();
        GraphicsEnvironment ge =
                GraphicsEnvironment.getLocalGraphicsEnvironment();
        GraphicsDevice[] gs = ge.getScreenDevices();
        for (int j = 0; j < gs.length; j++) {
            GraphicsDevice gd = gs[j];
            GraphicsConfiguration[] gc = gd.getConfigurations();
            for (int i = 0; i < gc.length; i++) {
                virtualBounds = virtualBounds.union(gc[i].getBounds());
            }
        }
        int w = virtualBounds.width / 2;
        int h = virtualBounds.height / 2;
        setLocation(w, 0);
        setSize(w, h);
    }
}

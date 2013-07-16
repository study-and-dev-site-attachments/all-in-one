package paxeq;

import org.w3c.dom.*;

import javax.swing.*;
import javax.swing.event.CellEditorListener;
import javax.swing.tree.TreeCellEditor;
import javax.swing.tree.TreeCellRenderer;
import javax.swing.tree.TreeModel;
import javax.swing.tree.TreePath;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.util.EventObject;


class JXmlButton extends JPanel {

    protected static ImageIcon ico_element = new ImageIcon(JXmlButton.class.getResource("img_element.gif"));
    protected static ImageIcon ico_attribute = new ImageIcon(JXmlButton.class.getResource("img_attribute.gif"));

    protected static ImageIcon ico_text = new ImageIcon(JXmlButton.class.getResource("img_cdata.gif"));
    protected static ImageIcon ico_pi = new ImageIcon(JXmlButton.class.getResource("img_pi.gif"));

    protected static ImageIcon ico_document = new ImageIcon(JXmlButton.class.getResource("img_root.gif"));
    protected static ImageIcon ico_unknown = new ImageIcon(JXmlButton.class.getResource("img_unknown.gif"));

    protected static ImageIcon ico_comment = new ImageIcon(JXmlButton.class.getResource("img_comment.gif"));
    protected static ImageIcon ico_entity = new ImageIcon(JXmlButton.class.getResource("img_entity.gif"));

    public static Icon getIconForNodeType(Node n) {
        if (n instanceof Document)
            return ico_document;

        if (n instanceof Element)
            return ico_element;
        if (n instanceof Attr)
            return ico_attribute;
        if (n instanceof Text)
            return ico_text;
        if (n instanceof ProcessingInstruction)
            return ico_element;
        if (n instanceof Comment)
            return ico_comment;
        if (n instanceof Entity)
            return ico_entity;
        if (n instanceof EntityReference)
            return ico_entity;

        return ico_unknown;
    }


    protected Node masterNode;
    JLabel LabNode = new JLabel();
    JButton BtnNode = new JButton();

    public JXmlButton(Node masterNode) {
        super(new BorderLayout());

        String fullText = masterNode.getNodeName();
        NamedNodeMap attrs = masterNode.getAttributes();
        String as = " { ";
        if (attrs != null && attrs.getLength() > 0) {
            final int length = attrs.getLength();
            for (int i = 0; i < length; i++) {
                Node attr = attrs.item(i);
                as += (attr.getNodeName() + "=\"" + attr.getNodeValue() + "\"");
                if (i != (length - 1))
                    as += ", ";
            }
            as += " } ";
            fullText += as;
        }
        int w = SwingUtilities.computeStringWidth(getFontMetrics(getFont()), fullText + "   ");
        w = (120 * w) / 100;
        this.masterNode = masterNode;
        add(LabNode, BorderLayout.CENTER);
        add(BtnNode, BorderLayout.WEST);
        BtnNode.setPreferredSize(new Dimension(18, 18));
        LabNode.setPreferredSize(new Dimension(w, 18));
        BtnNode.setIcon(getIconForNodeType(masterNode));

        LabNode.setText(fullText);
        short t = masterNode.getNodeType();
        String st = "";

        st = JXmlTree.getNodeTypeName(t, st);


        BtnNode.setToolTipText("<HTML>" + masterNode.getNodeName() + "<BR>" + st);
        BtnNode.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                // нажали на кнопку для узла дерева -- значит надо показать окно с информацией об узле
                new XmlInfoNodeDialog(JXmlButton.this.masterNode).setVisible(true);
            }
        });
    }


}


public class JXmlTree extends JTree {
    public static String getNodeTypeName(short t, String st) {
        if (t == Node.ATTRIBUTE_NODE)
            st = "The node is an Attr.";
        if (t == Node.CDATA_SECTION_NODE)
            st = "The node is a CDATASection";
        if (t == Node.COMMENT_NODE)
            st = "The node is a Comment";
        if (t == Node.DOCUMENT_FRAGMENT_NODE)
            st = "The node is a DocumentFragment";
        if (t == Node.DOCUMENT_NODE)
            st = "The node is a Document.";
        if (t == Node.DOCUMENT_POSITION_CONTAINED_BY)
            st = "The node is contained by the reference node.";
        if (t == Node.DOCUMENT_POSITION_CONTAINS)
            st = "The node contains the reference node.";
        if (t == Node.DOCUMENT_POSITION_DISCONNECTED)
            st = "The two nodes are disconnected.";
        if (t == Node.DOCUMENT_POSITION_FOLLOWING)
            st = "The node follows the reference node";
        if (t == Node.DOCUMENT_POSITION_IMPLEMENTATION_SPECIFIC)
            st = "The determination of preceding versus following is implementation-specific.";
        if (t == Node.DOCUMENT_POSITION_PRECEDING)
            st = "The node precedes the reference node.";
        if (t == Node.DOCUMENT_TYPE_NODE)
            st = "The node is a DocumentType.";
        if (t == Node.ELEMENT_NODE)
            st = "The node is an Element";
        if (t == Node.ENTITY_NODE)
            st = "The node is an Entity.";
        if (t == Node.ENTITY_REFERENCE_NODE)
            st = "The node is an EntityReference.";
        if (t == Node.PROCESSING_INSTRUCTION_NODE)
            st = "The node is a ProcessingInstruction.";
        if (t == Node.TEXT_NODE)
            st = "The node is a Text node.";
        return st;
    }


    public JXmlTree(TreeModel newModel) {
        super(newModel);
    }

    public JXmlTree() {
        setCellEditor(new TreeCellEditor() {
            public Component getTreeCellEditorComponent(JTree tree, Object value, boolean isSelected, boolean expanded, boolean leaf, int row) {
                if (!(value instanceof Node)) return new JLabel(value.toString());
                return new JXmlButton((Node) value);
            }

            public Object getCellEditorValue() {
                return null;
            }

            public boolean isCellEditable(EventObject anEvent) {
                if (anEvent instanceof MouseEvent) {
                    MouseEvent ev = (MouseEvent) anEvent;
                    return ev.getClickCount() == 2;
                }
                return false;
            }

            public boolean shouldSelectCell(EventObject anEvent) {
                return false;
            }

            public boolean stopCellEditing() {
                return false;
            }

            public void cancelCellEditing() {
            }

            public void addCellEditorListener(CellEditorListener l) {
            }

            public void removeCellEditorListener(CellEditorListener l) {
            }
        });

        setCellRenderer(new TreeCellRenderer() {
            public Component getTreeCellRendererComponent(JTree tree, Object value, boolean selected, boolean expanded, boolean leaf, int row, boolean hasFocus) {
                if (!(value instanceof Node)) {
                    JLabel jLabel = new JLabel(value.toString());
                    if (hasFocus || selected)
                        jLabel.setBorder(BorderFactory.createLineBorder(Color.RED, 1));
                    else
                        jLabel.setBorder(BorderFactory.createLineBorder(Color.WHITE, 1));
                    return jLabel;
                }
                JXmlButton jXmlButton = new JXmlButton((Node) value);
                if (hasFocus || selected)
                    jXmlButton.setBorder(BorderFactory.createLineBorder(Color.RED, 1));
                else
                    jXmlButton.setBorder(BorderFactory.createLineBorder(Color.WHITE, 1));

                return jXmlButton;
            }
        });
        setEditable(true);
    }

    public String getToolTipText(MouseEvent e) {
        int selRow = getRowForLocation(e.getX(), e.getY());
        TreePath selPath = getPathForLocation(e.getX(), e.getY());
        if (selRow != -1) {

            Node org_node = (Node) selPath.getLastPathComponent();

            short t = org_node.getNodeType();
            String st = "";
            st = getNodeTypeName(t, st);

            st = "<HTML>" + org_node.getNodeName() + "<BR>" + st + "<BR>";

            if (org_node instanceof Text)
                return st + "\"" + createWrap(((Text) org_node).getTextContent()) + "\"";

            if (org_node instanceof Comment)
                return st + "<!--" + createWrap(((Comment) org_node).getNodeValue()) + "-->";

            if (org_node instanceof Element) {
                String h = "";
                Element element = (Element) org_node;
                NamedNodeMap atrs = element.getAttributes();
                for (int i = 0; i < atrs.getLength(); i++)
                    h += atrs.item(i).getNodeName() + " = \"" + createWrap(atrs.item(i).getNodeValue()) + "\"<BR>";
                h += "<TABLE BORDER=\"1\"><TR><TD align=\"CENTER\"><B>Value:</B></TD></TR><TR><TD>" + element.getTextContent() + "</TD></TR></TABLE>";
                return st + h;
            }
        }
        return null;
    }

    private String createWrap(String what) {
        StringBuffer buf = new StringBuffer();
        for (int i = 0; i < what.length(); i++) {
            if (i > 0 && i % 60 == 0)
                buf.append("<BR>");
            buf.append(what.charAt(i));
        }
        return buf.toString();
    }

    public static String getNodeTypeName(Node l_attr, String st) {
        return getNodeTypeName(l_attr.getNodeType(), st);
    }

    public static String getNodeTypeName(Node l_attr) {
        return getNodeTypeName(l_attr.getNodeType(), "");
    }

}

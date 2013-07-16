package paxeq;

import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.swing.*;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableCellRenderer;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.io.CharArrayWriter;
import java.io.PrintWriter;
import java.util.ArrayList;

class DiffsInfo {
    String msg;
    Node left, right;

    public DiffsInfo(String msg, Node left, Node right) {
        this.msg = msg;
        this.left = left;
        this.right = right;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public Node getLeft() {
        return left;
    }

    public void setLeft(Node left) {
        this.left = left;
    }

    public Node getRight() {
        return right;
    }

    public void setRight(Node right) {
        this.right = right;
    }
}


public class PaneDiffResults extends JPanel {
    ArrayList<DiffsInfo> arr_diffs = new ArrayList<DiffsInfo>();

    public void startComparission() {
        arr_diffs.clear();
        String exc = "";
        boolean b = false;
        try {
            b = IsEqualTrees(left, right);
        } catch (Exception e) {
            CharArrayWriter chaw = new CharArrayWriter();
            PrintWriter pw = new PrintWriter(chaw);
            e.printStackTrace(pw);
            e.printStackTrace(System.err);
            pw.flush();
            chaw.flush();
            exc = e.toString() + "\n" + chaw.toString();
        }
        arr_diffs.add(new DiffsInfo((b ? "Порядок " + exc : "Провалено: " + exc), left, right));
        tabDiffsModel.fireTableDataChanged();
    }

    class TableDiffsResults extends AbstractTableModel {
        public int getRowCount() {
            return arr_diffs.size();
        }

        public int getColumnCount() {
            return 3;
        }

        public Object getValueAt(int rowIndex, int columnIndex) {
            DiffsInfo di = arr_diffs.get(rowIndex);
            if (columnIndex == 0) return di.getLeft();
            if (columnIndex == 2) return di.getRight();
            return di.getMsg();
        }

        public String getColumnName(int column) {
            if (column == 0) return "Левый узел";
            if (column == 2) return "Правый узел";
            return "Отличия";
        }

        public Class<?> getColumnClass(int columnIndex) {
            if (columnIndex == 0 || columnIndex == 2) return Node.class;
            return String.class;
        }
    }

    TableDiffsResults tabDiffsModel = new TableDiffsResults();

    JTable tabDiffs = new JTable(tabDiffsModel) {
        public String getToolTipText(MouseEvent event) {
            String tip = null;
            try {
                Point p = event.getPoint();
                int rowIndex = rowAtPoint(p);
                int colIndex = columnAtPoint(p);
                int realColumnIndex = convertColumnIndexToModel(colIndex);
                tip = (String) tabDiffsModel.getValueAt(rowIndex, 1);
                tip = tip.replaceAll("\n", "<BR>");
                tip = tip.replaceAll("\t", "&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;");
                tip = "<HTML>" + tip;
            } catch (Exception e) {
            }
            return tip;
        }
    };

    Node left, right;
    CompareNodeSettings settings;

    public PaneDiffResults() {
        super(new BorderLayout());
        tabDiffs.setDefaultRenderer(Node.class,
                new TableCellRenderer() {
                    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                        if (value == null) return new JLabel("");
                        if (!(value instanceof Node)) return new JLabel("" + value);
                        Node n = (Node) value;
                        final JLabel jLabel = new JLabel(n.getNodeName(), JXmlButton.getIconForNodeType(n), JLabel.LEFT);
                        if (tabDiffs.getSelectionModel().isSelectedIndex(row))
                            jLabel.setBorder(BorderFactory.createLineBorder(Color.RED, 1));
                        else
                            jLabel.setBorder(BorderFactory.createLineBorder(Color.WHITE, 1));
                        return jLabel;
                    }
                });
        add(new JScrollPane(tabDiffs), BorderLayout.CENTER);
        int w = 800;
        int cc = tabDiffs.getColumnCount();
        tabDiffs.getColumnModel().getColumn(0).setWidth(w / 6);
        tabDiffs.getColumnModel().getColumn(1).setWidth(w / 4);
        tabDiffs.getColumnModel().getColumn(2).setWidth(w / 6);
        ToolTipManager.sharedInstance().registerComponent(tabDiffs);

        setPreferredSize(new Dimension(0, 300));
        setMaximumSize(new Dimension(0, 300));
    }

    public void setCompareNodesInfo(Node left, Node right, CompareNodeSettings settings) {
        this.settings = settings;
        this.left = left;
        this.right = right;
    }

    public static final String msg_DifferentTypes = "Не совпадают типы узлов:\n\t{Left: (#LEFT_NAME#) #LEFT_TYPE#},\nтогда как второй нет:\n\t(#RIGHT_NAME#) #RIGHT_TYPE#";
    public static final String msg_NotFoundSomething = "У Узла\n\t{#LEFT_SIDE_NAME#:(#LEFT_NAME#) #LEFT_TYPE#}\nОбнаружен\n\t{#SOMETHING_TYPE# #SOMETHING_NAME#}=>#SOMETHING_VALUE# \n\tОтсутсвующий у\n{#RIGHT_SIDE_NAME#: (#RIGHT_NAME#) #RIGHT_TYPE#}";


    public String getMsg_DifferentTypes(Node l_attr, Node r_not) {
        String tmp = msg_DifferentTypes;
        tmp = tmp.replaceAll("#LEFT_NAME#", (l_attr.getPrefix() == null ? "" : l_attr.getPrefix()) + l_attr.getNodeName());
        tmp = tmp.replaceAll("#RIGHT_NAME#", (r_not.getPrefix() == null ? "" : r_not.getPrefix()) + r_not.getNodeName());

        tmp = tmp.replaceAll("#LEFT_TYPE#", JXmlTree.getNodeTypeName(l_attr));
        tmp = tmp.replaceAll("#RIGHT_TYPE#", JXmlTree.getNodeTypeName(r_not));
        return tmp;
    }

    public String getMsg_NotFoundAttr(boolean leftRule, Node l_attr, Node r_not, Node not_found) {
        String tmp = msg_NotFoundSomething;

        tmp = tmp.replaceAll("#SOMETHING_NAME#", (not_found.getPrefix() == null ? "" : not_found.getPrefix()) + not_found.getNodeName());
        tmp = tmp.replaceAll("#SOMETHING_VALUE#", not_found.getNodeValue());
        tmp = tmp.replaceAll("#SOMETHING_TYPE#", JXmlTree.getNodeTypeName(not_found));


        if (!leftRule) {
            Node tmp_t;
            tmp_t = l_attr;
            l_attr = r_not;
            r_not = tmp_t;
        }
        tmp = tmp.replaceAll("#LEFT_SIDE_NAME#", "Left");
        tmp = tmp.replaceAll("#RIGHT_SIDE_NAME#", "Right");

        tmp = tmp.replaceAll("#LEFT_NAME#", (l_attr.getPrefix() == null ? "" : l_attr.getPrefix()) + l_attr.getNodeName());
        tmp = tmp.replaceAll("#RIGHT_NAME#", (r_not.getPrefix() == null ? "" : r_not.getPrefix()) + r_not.getNodeName());

        tmp = tmp.replaceAll("#LEFT_TYPE#", JXmlTree.getNodeTypeName(l_attr));
        tmp = tmp.replaceAll("#RIGHT_TYPE#", JXmlTree.getNodeTypeName(r_not));


        return tmp;
    }


    private String getMsg_DifferentNames(Node l, Node r) {
        return "Несовпадают имена узлов:\nLeft:\n" +
                (l.getPrefix() == null ? "" : l.getPrefix()) + l.getNodeName() +
                " Right:" +
                (r.getPrefix() == null ? "" : r.getPrefix()) + r.getNodeName();
    }

    private String getMsg_DifferentChildNodesCount(Node l, Node r) {
        return "Несовпадают количество дочерних узлов:\nLeft:\n" +
                (l.getPrefix() == null ? "" : l.getPrefix()) + l.getNodeName() + " {" + l.getChildNodes().getLength() + "} " +
                "\nRight:\n" +
                (r.getPrefix() == null ? "" : r.getPrefix()) + r.getNodeName() + " {" + r.getChildNodes().getLength() + "} ";
    }

    private String getMsg_DifferentNamespaces(Node l, Node r) {
        return "Несовпадают пространства имен узлов:\nLeft:\n" +
                (l.getPrefix() == null ? "" : l.getPrefix()) + "{" +
                (l.getNamespaceURI() == null ? "" : l.getNamespaceURI()) + "}" +
                l.getNodeName() +
                "\nRight:\n" +
                (r.getPrefix() == null ? "" : r.getPrefix()) + "{" +
                (r.getNamespaceURI() == null ? "" : r.getNamespaceURI()) + "}" +
                r.getNodeName();
    }

    public boolean IsEqualTrees(Node l, Node r) {
        if (l == null && r != null) return false;
        if (l != null && r == null) return false;
        if (l == null && r == null) return true;

        if (l.getNodeType() != r.getNodeType()) {
            arr_diffs.add(new DiffsInfo(getMsg_DifferentTypes(l, r), l, r));
            return false;
        }
        String name_l = l.getNodeName();
        String name_r = r.getNodeName();
        String uri_l = l.getNamespaceURI();
        String uri_r = r.getNamespaceURI();

        if (!name_l.equals(name_r)) {
            arr_diffs.add(new DiffsInfo(getMsg_DifferentNames(l, r), l, r));
            return false;
        }

        if ((uri_l != null && uri_r != null) && (!uri_l.equals(uri_r))) {
            arr_diffs.add(new DiffsInfo(getMsg_DifferentNamespaces(l, r), l, r));
            return false;
        }
        if (uri_l != null && uri_r == null) {
            arr_diffs.add(new DiffsInfo(getMsg_DifferentNamespaces(l, r), l, r));
            return false;
        }
        if (uri_l == null && uri_r != null) {
            arr_diffs.add(new DiffsInfo(getMsg_DifferentNamespaces(l, r), l, r));
            return false;
        }


        NodeList list_left = l.getChildNodes();
        NodeList list_right = r.getChildNodes();

        if (list_left.getLength() != list_right.getLength()) {
            arr_diffs.add(new DiffsInfo(getMsg_DifferentChildNodesCount(l, r), l, r));
            return false;
        }

        boolean all_ok = true;
        for (int i = 0; i < list_left.getLength(); i++) {
            Node left_candi = list_left.item(i);
            Node right_candi = list_right.item(i);
            if (!IsEqualTrees(left_candi, right_candi))
                all_ok = false;
        }
        return all_ok;
    }


}

package paxeq;

import org.apache.xpath.XPathAPI;
import org.apache.xpath.objects.XObject;
import org.w3c.dom.Attr;
import org.w3c.dom.Node;
import org.w3c.dom.traversal.NodeIterator;

import javax.swing.*;
import javax.swing.event.MouseInputAdapter;
import javax.swing.plaf.basic.BasicComboBoxEditor;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableCellRenderer;
import javax.swing.tree.TreePath;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.Vector;

public class XPathPane extends JPanel {
    public Component getTableResults() {
        // здесь мы возвращаем таблицу в которой видны результаты вычисления ряда Xpath выражений
        return new JScrollPane(xpathResults);
    }

    ArrayList<Node> evaluatedNodes = new ArrayList<Node>();

    class SimpleXPathResultsModel extends AbstractTableModel {
        public int getRowCount() {
            return evaluatedNodes.size();
        }

        public int getColumnCount() {
            return 1;
        }

        public Object getValueAt(int rowIndex, int columnIndex) {
            return evaluatedNodes.get(rowIndex);
        }

        public String getColumnName(int column) {
            return "Node";
        }

        public Class<?> getColumnClass(int columnIndex) {
            return Node.class;
        }
    }

    SimpleXPathResultsModel modelTableResults = new SimpleXPathResultsModel();
    JTable xpathResults = new JTable(modelTableResults);

    JButton btnFirst = new JButton(new ImageIcon(this.getClass().getResource("btn_first.jpg")));
    JButton btnLast = new JButton(new ImageIcon(this.getClass().getResource("btn_last.jpg")));
    JButton btnPrev = new JButton(new ImageIcon(this.getClass().getResource("btn_prev.jpg")));
    JButton btnNext = new JButton(new ImageIcon(this.getClass().getResource("btn_next.jpg")));
    Vector<String> vCommands = new Vector<String>();
    JComboBox comboText = new JComboBox(new DefaultComboBoxModel(vCommands));

    JXmlTree masterTree;
    JLabel labStatusAboutXPathCall  = new JLabel(" ");

    public XPathPane(JXmlTree masterTree) {
        super(new BorderLayout());

        this.masterTree = masterTree;
        comboText.setEditable(true);
        comboText.setEditor(new BasicComboBoxEditor() {
            public Component getEditorComponent() {
                final JTextField fld = (JTextField) super.getEditorComponent();
                fld.addActionListener(new ActionListener() {
                    public void actionPerformed(ActionEvent e) {
                        String txt = fld.getText();
                        ExecuteXPath(txt);
                        if (vCommands.indexOf(txt) == -1)
                            vCommands.add(txt);
                        ExecuteXPath(txt);
                    }
                });
                return fld;
            }
        });


        add(comboText, BorderLayout.CENTER);
        JPanel navi = new JPanel(new GridLayout(1, 4));
        navi.add(btnFirst);
        navi.add(btnPrev);
        navi.add(btnNext);
        navi.add(btnLast);

        btnFirst.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                doFirst();
            }
        });

        btnPrev.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                doPrev();
            }
        });

        btnNext.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                doNext();
            }
        });

        btnLast.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                doLast();
            }
        });

        add(navi, BorderLayout.EAST);
        add(labStatusAboutXPathCall, BorderLayout.SOUTH);


        int w = 22;
        int h = 22;
        Dimension prf = new Dimension(w, h);
        btnFirst.setPreferredSize(prf);
        btnPrev.setPreferredSize(prf);
        btnNext.setPreferredSize(prf);
        btnLast.setPreferredSize(prf);

        xpathResults.addMouseListener(new MouseInputAdapter() {
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    // тут был выбран иной элемент узла и следовательно надо переместить в дереве фокус и подсветку на выбранный элемент в пути
                    doSynchronizeTableXPathResultsAndTree();
                }
            }
        });

        xpathResults.setDefaultRenderer(Node.class,
                new TableCellRenderer() {
                    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                        if (value == null)
                            return new JLabel("<<NULL>>");
                        if (!(value instanceof Node)) return new JLabel("" + value);
                        String type = JXmlTree.getNodeTypeName((Node) value);
                        String name = ((Node) value).getNodeName();
                        Icon ico = JXmlButton.getIconForNodeType((Node) value);
                        JLabel labWhat = new JLabel(name, ico, JLabel.LEFT);
                        if (isSelected || hasFocus)
                            labWhat.setBorder(BorderFactory.createLineBorder(Color.RED, 1));
                        else
                            labWhat.setBorder(BorderFactory.createLineBorder(Color.WHITE, 1));
                        return labWhat;
                    }
                });
    }

    private void doSynchronizeTableXPathResultsAndTree() {
        try {
            if (modelTableResults.getRowCount() == 0) return;
            int rowS = xpathResults.getSelectedRow();
            masterTree.clearSelection();
            if (rowS < 0 || rowS > modelTableResults.getRowCount()) return;
            Node selectedXPathNode = (Node) modelTableResults.getValueAt(rowS, 0);

            if (selectedXPathNode instanceof Attr)
                selectedXPathNode = selectedXPathNode.getParentNode();

            ArrayList<Node> nodesInPath = new ArrayList<Node>();
            while (selectedXPathNode != null) {
                nodesInPath.add(selectedXPathNode);

                selectedXPathNode = selectedXPathNode.getParentNode();
            }
            ArrayList<Node> nodesInPathRev = new ArrayList<Node>();
            for (int i = nodesInPath.size() - 1; i >= 0; i--)
                nodesInPathRev.add(nodesInPath.get(i));
            TreePath path = new TreePath(nodesInPathRev.toArray());
            masterTree.setExpandsSelectedPaths(true);
            masterTree.setSelectionPath(path);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void doLast() {
        xpathResults.getSelectionModel().setSelectionInterval(xpathResults.getRowCount() - 1, xpathResults.getRowCount() - 1);
        doSynchronizeTableXPathResultsAndTree();
    }

    private void doNext() {
        int cur_row = xpathResults.getSelectionModel().getMinSelectionIndex();
        cur_row++;
        if (cur_row < xpathResults.getRowCount())
            xpathResults.getSelectionModel().setSelectionInterval(cur_row, cur_row);
        doSynchronizeTableXPathResultsAndTree();
    }

    private void doPrev() {
        int cur_row = xpathResults.getSelectionModel().getMinSelectionIndex();
        cur_row--;
        if (cur_row >= 0)
            xpathResults.getSelectionModel().setSelectionInterval(cur_row, cur_row);
        doSynchronizeTableXPathResultsAndTree();
    }

    private void doFirst() {
        xpathResults.getSelectionModel().setSelectionInterval(0, 0);
        doSynchronizeTableXPathResultsAndTree();
    }

    String prevPath = "nothing";

    public void ExecuteXPath(String xpathExprStr) {
        if (xpathExprStr.equals(prevPath)) return;
        prevPath = xpathExprStr;
        Node master = null;
        if (masterTree.getSelectionPath() == null)
            master = (Node) masterTree.getModel().getRoot();
        else
            master = (Node) masterTree.getSelectionPath().getLastPathComponent();
        try {
            XObject xob = XPathAPI.eval(master, xpathExprStr);

            NodeIterator it = xob.nodeset();
            Node cur;
            evaluatedNodes.clear();
            while ((cur = it.nextNode()) != null) {
                evaluatedNodes.add(cur);
            }
            labStatusAboutXPathCall.setText("Found: " + evaluatedNodes.size() + " nodes");
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Exception while Evaluate XPath: " + e);
            e.printStackTrace();
        }
        modelTableResults.fireTableDataChanged();
        doSynchronizeTableXPathResultsAndTree();
    }
}

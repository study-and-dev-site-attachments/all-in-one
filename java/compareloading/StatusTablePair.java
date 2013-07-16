package compareloading;

import javax.swing.*;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.table.AbstractTableModel;
import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.util.List;
import java.util.ArrayList;
import java.util.Set;
import java.util.HashSet;
import java.util.regex.Pattern;

/**
 * GUI class which presents pairs ort troika's vaues
 */
public class StatusTablePair extends JPanel {

    /**
     * inner class presents data model for table (2 values)
     */
    public static class StatusTableModelPair extends AbstractTableModel {
        private List<CompareUtils.ComparePair> data;
        private List<CompareUtils.ComparePair> filteredData;

        StatusTableModelPair(List<CompareUtils.ComparePair> data) {
            this.data = data;
            applyFilter(null);
        }

        public int getRowCount() {
            return filteredData.size();
        }

        public int getColumnCount() {
            return 2;
        }

        public Object getValueAt(int rowIndex, int columnIndex) {
            CompareUtils.ComparePair pair = filteredData.get(rowIndex);
            if (columnIndex == 0) return pair.clazz;
            if (columnIndex == 1) return pair.file;
            return null;
        }

        public String getColumnName(int column) {
            if (column == 0) return "clazz";
            if (column == 1) return "file";
            return null;
        }

        /**
         * applies filter to full set of pairs (clazz & file)
         *
         * @param s    filter expression (*List*)
         */
        private void applyFilter(String s) {
            List<CompareUtils.ComparePair> rezz = new ArrayList<CompareUtils.ComparePair>();

            // convert like expression to regexp style expression
            if (s == null || s.equals("")) {
                // copy all list content without filtering
                rezz.addAll(data);
            } else {
                s = s.replace(".", "\\.");
                s = s.replace("*", ".*");
                // search everywhere
                s = ".*" + s + ".*";
                Pattern p = Pattern.compile(s);
                for (CompareUtils.ComparePair comparePair : data) {
                    if (p.matcher(comparePair.clazz).matches())
                        rezz.add(comparePair);
                }
            }
            this.filteredData = rezz;
            fireTableDataChanged();
        }
    }//end of inner class


    /**
     * table component
     */
    private JTable tab;
    /**
     * tree component
     */
    private JTree tree;
    /*
    * data model
    */
    private List<CompareUtils.ComparePair> dataPairs;
    /**
     * table model
     */
    private StatusTableModelPair modelTable;
    /**
     * tree model
     */
    private DefaultMutableTreeNode modelTree;

    public StatusTablePair() {
        super(new BorderLayout());

        List<CompareUtils.ComparePair> data = new ArrayList<CompareUtils.ComparePair>();
        JScrollPane scrollTable = new JScrollPane(tab = new JTable(modelTable = new StatusTableModelPair(data)));
        JScrollPane scrollTree = new JScrollPane(tree = new JTree(modelTree = new DefaultMutableTreeNode("Classes")));
        tree.setRootVisible(true);
        JTabbedPane rootPane = new JTabbedPane(JTabbedPane.BOTTOM);


        JPanel tableWrapper = new JPanel(new BorderLayout());
        tableWrapper.add(scrollTable, BorderLayout.CENTER);

        final JTextField txtFilter = new JTextField();
        txtFilter.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                // apply new filter
               modelTable.applyFilter(txtFilter.getText());
            }
        });
        JPanel paneFilter = new JPanel(new BorderLayout());
        paneFilter.add(new JLabel("Filter expression: "), BorderLayout.WEST);
        paneFilter.add(txtFilter, BorderLayout.CENTER);
        tableWrapper.add(paneFilter, BorderLayout.NORTH);

        rootPane.add("Table", tableWrapper);
        rootPane.add("Tree", scrollTree);

        add(rootPane);
        this.dataPairs = data;
    }

    /**
     * sets new data for UI control
     *
     * @param data
     */
    public void fillData(List<CompareUtils.ComparePair> data) {
        this.dataPairs = data;
        this.modelTable.data = data;
        this.modelTable.applyFilter (null);

        // building tree
        // at first select all file resources (without duplicates)
        Set<String> files = new HashSet<String>();
        for (CompareUtils.ComparePair dataPair : dataPairs) {
            files.add(dataPair.file);
        }
        // remove all tree content
        modelTree.removeAllChildren();
        // build new tree        
        for (String file : files) {
            DefaultMutableTreeNode nfile = new DefaultMutableTreeNode(file);
            //looks for all classes from current file
            for (CompareUtils.ComparePair dataPair : dataPairs) {
                if (dataPair.file.equals(file)) {
                    nfile.add(new DefaultMutableTreeNode(dataPair.clazz));
                }
            }
            // add qty of classes loaded from this resource
            nfile.setUserObject(file + " ["+nfile.getChildCount()+"] ");
            modelTree.add(nfile);
        }
        tree.expandRow(0);
    }

}

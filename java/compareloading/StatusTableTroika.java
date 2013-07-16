package compareloading;

import javax.swing.*;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.table.AbstractTableModel;
import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.util.List;
import java.util.ArrayList;
import java.util.regex.Pattern;

/**
 * GUI class which presents pairs ort troika's vaues
 */
public class StatusTableTroika extends JPanel {


    /**
     * inner class presents data model for table (3 values)
     */

    class StatusTableModelTroika extends AbstractTableModel {
        private List<CompareUtils.CompareTroika> data;
        // filtered data list
        private List<CompareUtils.CompareTroika> filteredData;

        StatusTableModelTroika(List<CompareUtils.CompareTroika> data) {
            this.data = data;
            applyFilter(null);
        }

        public int getRowCount() {
            return filteredData.size();
        }

        public int getColumnCount() {
            return 3;
        }

        public Object getValueAt(int rowIndex, int columnIndex) {
            CompareUtils.CompareTroika troika = filteredData.get(rowIndex);
            if (columnIndex == 0) return troika.file1;
            if (columnIndex == 1) return troika.clazz;
            if (columnIndex == 2) return troika.file2;
            return null;
        }

        public String getColumnName(int column) {
            if (column == 0) return "file 1";
            if (column == 1) return "clazz";
            if (column == 2) return "file 2";
            return null;
        }

        /**
         * applies filter to full set of pairs (clazz & file)
         *
         * @param s filter expression (*List*)
         */
        private void applyFilter(String s) {
            List<CompareUtils.CompareTroika> rezz = new ArrayList<CompareUtils.CompareTroika>();

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
                for (CompareUtils.CompareTroika comparePair : data) {
                    if (p.matcher(comparePair.clazz).matches())
                        rezz.add(comparePair);
                }
            }
            this.filteredData = rezz;
            fireTableDataChanged();
        }
    }

    /**
     * table component
     */
    private JTable tab;

    /*
     * data model
     */
    private List<CompareUtils.CompareTroika> dataTroikas;
    /**
     * table model
     */
    private StatusTableModelTroika modelTable;
    /**
     * tree model
     */
    private DefaultMutableTreeNode modelTree;

    public StatusTableTroika() {
        super(new BorderLayout());

        List<CompareUtils.CompareTroika> data = new ArrayList<CompareUtils.CompareTroika>();
        JScrollPane scrollTable = new JScrollPane(tab = new JTable(modelTable = new StatusTableModelTroika(data)));

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


        add(tableWrapper);
        this.dataTroikas = data;
    }

    public void fillData(List<CompareUtils.CompareTroika> data) {
        this.dataTroikas = data;
        this.modelTable.data = data;
        this.modelTable.applyFilter (null);
    }

}
package nbform;

import org.ccil.cowan.tagsoup.CommandLine;

import javax.swing.*;
import javax.swing.table.AbstractTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Set;
import java.util.TreeMap;


/**
 * ������ ������ ������� ������������ ��� ������������� ������� � ����������� ��� ������� TagSoup
 */
class OptionsModel extends AbstractTableModel {

    TreeMap<String, String> optName = new TreeMap<String, String>();
    ArrayList<String> key_names = new ArrayList<String>();

    /**
     * ��� �������� ������ ������ ����������� �� ���������� �� ������� CommandLine
     */
    public OptionsModel() {
        Set<String> k = (Set<String>) CommandLine.options.keySet();
        for (String s : k) {
            optName.put(s, "" + CommandLine.options.get(s));
            key_names.add(s);
        }
    }

    public int getRowCount() {
        return optName.size();
    }

    public int getColumnCount() {
        return 2;
    }

    public Object getValueAt(int rowIndex, int columnIndex) {
        if (columnIndex < 0 || columnIndex > 1 || rowIndex < 0 || rowIndex >= getRowCount())
            return null;
        if (columnIndex == 0)
            return key_names.get(rowIndex);

        return optName.get(key_names.get(rowIndex));
    }

    public static String field_nams[] = {"Param", "Value"};

    public String getColumnName(int column) {
        return field_nams[column];
    }

    public Class<?> getColumnClass(int columnIndex) {
        return String.class;
    }


    public boolean isCellEditable(int rowIndex, int columnIndex) {
        return columnIndex == 1;
    }


    public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
        optName.put(key_names.get(rowIndex), "" + aValue);
    }
}

/**
 * ����� �������������� ���������� ���� � ����������� ��� ���������� ����������� �������� HTML-����������
 * � �������������� DOM-������ � ����������� ��� ��������
 */
public class GUITagSoapOptions extends JDialog {

    /**
     * ������ �� ������ ������ ������� ������������  � �������
     */
    OptionsModel model = new OptionsModel();
    /**
     * ���������� GUI �������
     */
    JTable tab = new JTable(model);

    public GUITagSoapOptions(JFrame jf) {
        super(jf, "��������� ������� TagSoap / ����� �������� ���� � '������' HTML", true);

        Container cp = getContentPane();
        cp.setLayout(new BorderLayout());


        cp.add(new JScrollPane(tab), BorderLayout.CENTER);

        JPanel btns_pane = new JPanel(new FlowLayout(FlowLayout.CENTER));
        JButton btn_OK = new JButton("�������");
        JButton btn_CANCEL = new JButton("����������");

        btns_pane.add(btn_OK);
        btns_pane.add(btn_CANCEL);

        cp.add(btns_pane, BorderLayout.SOUTH);

        btn_CANCEL.addActionListener(
                new ActionListener() {
                    public void actionPerformed(ActionEvent e) {
                        setVisible(false);
                    }
                }
        );

        btn_OK.addActionListener(
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
                        setVisible(false);
                    }
                }
        );
        setSize(640, 480);
    }

    /**
     * ������� � �������� ���������� ���� �������� TagSoap
     *
     * @param jf ����������� ���� JFrame
     */
    public static void getParams(JFrame jf) {
        final GUITagSoapOptions f = new GUITagSoapOptions(jf);
        SwingUtilities.invokeLater(
                new Runnable() {
                    public void run() {
                        f.setVisible(true);
                    }
                }
        );


    }
}

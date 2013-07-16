package tunnel;

import javax.swing.*;
import javax.swing.table.AbstractTableModel;
import java.util.ArrayList;


class SmallBeesTableModel extends AbstractTableModel {
    TunnelInfo info;

    public int getRowCount() {
        if (info != null)
            return info.getBee().getListSmallBees().size();
        else
            return 0;
    }

    public int getColumnCount() {
        return 5;
    }

    public Object getValueAt(int rowIndex, int columnIndex) {
        if (info != null) {
            ArrayList<SmallBeePair> listSmallBees = info.getBee().getListSmallBees();
            if (listSmallBees.size() <= rowIndex) return null;

            SmallBeePair pan = listSmallBees.get(rowIndex);

            if (columnIndex == 0) return pan.getLifePresentattion();

            if (columnIndex == 1) return pan.getBeeIn().getTunnel_RemotePort();
            if (columnIndex == 2) return pan.getBeeOut().getTunnel_LocalPort();
            if (columnIndex == 3) return pan.getBeeIn().getTunnel_BytesAmount();
            if (columnIndex == 4) return pan.getBeeOut().getTunnel_BytesAmount();
        }
        return null;
    }

    public Class<?> getColumnClass(int columnIndex) {
        if (columnIndex == 0) return ImageIcon.class;
        return Integer.class;
    }

    String[] names = new String[]{
            "Статус",
            "Удаленный порт",
            "Локальный порт",
            "Передано байт",
            "Принято байт"
    };

    public String getColumnName(int column) {

        return names[column];
    }


    public SmallBeePair getRowObject(int rowIndex) {
        if (rowIndex < 0 || rowIndex >= info.getBee().getListSmallBees().size())
            return null;
        return info.getBee().getListSmallBees().get(rowIndex);
    }


    public TunnelInfo getInfo() {
        return info;
    }

    public void setInfo(TunnelInfo info) {
        this.info = info;
        fireTableStructureChanged();
    }

    public int getSmallBeeRowNumber(SmallBee smallBee) {
        ArrayList<SmallBeePair> listSmallBees = info.getBee().getListSmallBees();
        int i = 0;
        for (SmallBeePair listSmallBee : listSmallBees) {
            if (smallBee == listSmallBee.beeIn || smallBee == listSmallBee.beeOut)
                return i;
            i++;
        }
        return -1;
    }

    public ArrayList<SmallBeePair> getSmallBeeList() {
        return info.getBee().getListSmallBees();
    }
}

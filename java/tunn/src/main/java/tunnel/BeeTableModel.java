package tunnel;

import javax.swing.table.AbstractTableModel;
import java.text.SimpleDateFormat;
import java.util.ArrayList;


class BeeTableModel extends AbstractTableModel {
    ArrayList<TunnelInfo> data = new ArrayList<TunnelInfo>();
    SimpleDateFormat dateFormat = new SimpleDateFormat("H:m:s");

    public int getRowCount() {
        return data.size();
    }

    public int getColumnCount() {
        return 3;
    }

    public Object getValueAt(int rowIndex, int columnIndex) {
        TunnelInfo info = data.get(rowIndex);
        if (columnIndex == 0)
            return info.getM_ListenPort();
        if (columnIndex == 1)
            return info.getM_TargetUrl() + ":" + info.getM_TargetPort();
        if (columnIndex == 2) {
            return dateFormat.format(info.getBeeStartLife());
        }
        return null;
    }

    String[] strings = new String[]{"Откуда", "Куда", "Когда"};

    public String getColumnName(int column) {
        return strings[column];
    }


    public Class<?> getColumnClass(int columnIndex) {
        return String.class;
    }

    public void addNewBee(TunnelInfo tun) throws Exception {
        tun.setBee(new Bee(tun));
        data.add(tun);
    }

    public TunnelInfo getRowObject(int r) {
        return data.get(r);
    }

    public void removeBee(Bee bee) {
        data.remove(bee);
        fireTableDataChanged();
    }
}

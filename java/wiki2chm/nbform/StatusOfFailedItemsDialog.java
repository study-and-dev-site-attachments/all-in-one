package nbform;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.Iterator;
import java.net.URL;

/**
*���������� ����� �������� ��� ����������� ���������� � ��� ����� ������� �� ����� ���� ��������� ��-�� ������
*/

public class StatusOfFailedItemsDialog extends JDialog {

    /**
     * ������ �������� �������� � ��� �������� ��� �������� ������� �� ������ ���������
     */

    public static class FailedResourceItem {
        /**
         * ����� �������
         */
        private URL url;

        /**
         * �������� ���� ������� (�������� ��� html-��������)
         */
        public enum FailedResourceKind {
            IMAGE, HTML
        }

        private FailedResourceKind kind;

        public FailedResourceItem(URL url, FailedResourceKind kind) {
            this.url = url;
            this.kind = kind;
        }

        public URL getUrl() {
            return url;
        }

        public FailedResourceKind getKind() {
            return kind;
        }

        ImageIcon ico_image;
        ImageIcon ico_html;

        /**
         * ����� ������ ��� ������������ ����������� ������������� �������� �� �������� ����������� ��������
         * @return ������ Icon � ����������� �� ���� ���������
         */
        public Icon getImageForKind() {
            try {
                if (ico_image == null) {
                    ico_image = new ImageIcon(ClassLoader.getSystemResource("nbform/assets/account_actions24.jpg"));
                }
                if (ico_html == null) {
                    ico_html = new ImageIcon(ClassLoader.getSystemResource("nbform/assets/account_ledgers24.jpg"));
                }
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }

            if (kind == FailedResourceKind.HTML)
                return ico_html;
            if (kind == FailedResourceKind.IMAGE)
                return ico_image;
            return null;
        }
    } // end of Class


    /**
     * ������ ������� ������� �� ����� ���� ���������
     */
    ArrayList<FailedResourceItem> failedUrls;

    /**
     * ����������� ��������������� ���������� ����� � ������������ �������� wikimedia (�������� �� ��������� ����������� ������)
     * @param failedUrls
     */
    public StatusOfFailedItemsDialog(ArrayList<FailedResourceItem> failedUrls) {
        this.failedUrls = failedUrls;
        setModal(true);
        setTitle("������ �� ������ �������� offline ����� ����� mediawiki");
        Container cp = getContentPane();
        JList jl = new JList(failedUrls.toArray());
        cp.add (new JLabel ("���������� ��������, ������� �� ����� ���� ���������: "+ failedUrls.size()), BorderLayout.NORTH);
        cp.add (new JScrollPane(jl), BorderLayout.CENTER);
        jl.setCellRenderer(
                new ListCellRenderer() {
                    JLabel lab = new JLabel();
                    public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
                        if (value == null){
                            lab.setText("");
                            lab.setIcon(null);
                        }
                        else{
                           FailedResourceItem fa  = (FailedResourceItem) value;
                           lab.setText(fa.getUrl().toString());
                           lab.setIcon(fa.getImageForKind());
                        }
                        return lab;
                    }
                }
        );
    }

    /**
     * ������� ������ ��������� ����� ����������� ���� � ������������ �������� ����������� mediawiki, �� ������ � ��� ������ ���� ������� ������ ���� ���������
     */
    public void showDialog() {
        if (failedUrls.size() > 0) {
            setSize(800, 640);
            SwingUtilities.invokeLater(
                    new Runnable() {
                        public void run() {
                            setVisible(true);
                        }
                    }
            );
        }
    }
}

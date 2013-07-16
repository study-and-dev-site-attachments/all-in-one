package nbform;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.Iterator;
import java.net.URL;

/**
*диалоговая форма служащая для отображения информации о том какие ресурсы не могли быть загружены из-за ошибок
*/

public class StatusOfFailedItemsDialog extends JDialog {

    /**
     * Объект содержит сведения о той странице или картинке которую не смогли загрузить
     */

    public static class FailedResourceItem {
        /**
         * Адрес ресурса
         */
        private URL url;

        /**
         * Описание типа ресурса (картинка или html-документ)
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
         * Метод служит для формирования визуального представления сведений об неудачно загруженном элементе
         * @return Объект Icon в зависимости от типа документа
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
     * Список адресов которые не могли быть загружены
     */
    ArrayList<FailedResourceItem> failedUrls;

    /**
     * Выполняется конструирование диалоговой формы с результатами загрузки wikimedia (сведения об неудачных загруженных файлах)
     * @param failedUrls
     */
    public StatusOfFailedItemsDialog(ArrayList<FailedResourceItem> failedUrls) {
        this.failedUrls = failedUrls;
        setModal(true);
        setTitle("Ошибки на стадии создания offline копии вашей mediawiki");
        Container cp = getContentPane();
        JList jl = new JList(failedUrls.toArray());
        cp.add (new JLabel ("Количество ресурсов, которые не могли быть загружены: "+ failedUrls.size()), BorderLayout.NORTH);
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
     * Функция должна выполнить показ диалогового окна с результатами загрузки содержимого mediawiki, но только в том случае если таковые ошибки были встречены
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

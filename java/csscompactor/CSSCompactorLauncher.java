import javax.swing.*;

/**
 * Created by IntelliJ IDEA.
 * User: Programmer
 * Date: 07.12.2007
 * Time: 12:30:38
 */

/**
 * Назначение класса - выполнить создание GUI формы приложения и запустить ее
 */
public class CSSCompactorLauncher {
    /**
     * Главная функция приложения, создает форму и отображает ее.
     * @param args Не используются
     */
    public static void main(String[] args) {
        final JFrame jf = new JFrame("CSS Compactor project (by black zorro --- black-zorro@tut.by)");
        jf.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        CSSCompactorForm form = new CSSCompactorForm();
        jf.getContentPane().add(form.getPaneroot());
        form.postCreateSteps();
        jf.pack();
        SwingUtilities.invokeLater(
                new Runnable() {
                    public void run() {
                        jf.setVisible(true);
                    }
                }
        );

    }
}

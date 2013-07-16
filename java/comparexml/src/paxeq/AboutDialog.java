package paxeq;

import javax.swing.*;


public class AboutDialog extends JDialog {
    public AboutDialog() {
        setTitle("About: idea and programming by black zorro");
        setModal(true);
        getContentPane().add(new JLabel(new ImageIcon(AboutDialog.class.getResource("mangusta.jpg"))));
        pack();
    }
}

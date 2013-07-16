package paxeq;

import org.ccil.cowan.tagsoup.CommandLine;

import javax.swing.*;
import java.awt.*;


public class frstart {
    public static void main(String[] args) {

        CommandLine.options.put("--encoding=" , "cp1251");
        //CommandLine.options.put("--output-encoding=" , "cp1251");

        final XCmpFrame xfr = new XCmpFrame();
        xfr.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        xfr.setMinimumSize(new Dimension(800, 600));
        SwingUtilities.invokeLater(
                new Runnable(){
                    public void run() {
                        xfr.setVisible(true);
                    }
                }
        );
    }
}

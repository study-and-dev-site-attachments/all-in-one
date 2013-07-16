package tunnel;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;


public class DialogHTMLView extends JDialog {

    public DialogHTMLView(Frame owner, byte[] data, String enc) throws IOException {
        super(owner, true);

        Container cp = getContentPane();
        cp.setLayout(new BorderLayout());
        JTextPane tpane = new JTextPane();
        tpane.setEditable(false);
        cp.add(new JScrollPane(tpane));

        String str = new String(data, enc);
        String[] lines = str.split("\n");
        StringBuilder html = new StringBuilder();
        for (int i = 0; i < lines.length; i++) {
            if (lines[i].trim().isEmpty()) {
                for (int j = 1 + i; j < lines.length; j++) {
                    html.append(lines[j]);
                }
                break;
            }
        }


        File f = File.createTempFile("htama", "aadml");
        System.out.println(f.toURI().toString());
        FileWriter fw = new FileWriter(f);
        fw.write(html.toString());
        fw.flush();
        fw.close();

        File tfhtml = new File(f.getAbsolutePath() + ".html");
        f.renameTo(tfhtml);
        tpane.setPage(tfhtml.toURI().toURL());
        setSize(640, 480);
    }
}

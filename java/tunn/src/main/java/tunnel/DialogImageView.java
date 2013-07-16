package tunnel;

import javax.swing.*;
import java.awt.*;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;


public class DialogImageView extends JDialog {

    public DialogImageView(Frame owner, byte[] data, String enc) throws IOException {
        super(owner, true);

        Container cp = getContentPane();
        cp.setLayout(new BorderLayout());

        Boolean trigger = false;
        ByteArrayOutputStream bout = new ByteArrayOutputStream();

        String ext = "gif";

        String str = new String(data, enc);
        String[] lines = str.split("\n");
        int savedI = 0;
        StringBuilder html = new StringBuilder();
        for (int i = 0; i < lines.length; i++) {
            if (lines[i].trim().isEmpty()) {
                int delta  = 0;
                do{
                    delta++;
                }
                while (data [savedI+delta] == 13 || data [savedI+delta] == 10);
                
                for (int j = delta + savedI; j < data.length; j++)
                    bout.write(data[j]);
                String headers = new String(data, 0, savedI);
                if (headers.indexOf("image/gif") != -1)
                    ext = "gif";
                if (headers.indexOf("image/png") != -1)
                    ext = "png";
                if (headers.indexOf("image/jpg") != -1)
                    ext = "jpg";
                if (headers.indexOf("image/jpeg") != -1)
                    ext = "jpg";
                break;
            }
            savedI += lines [i].length() + 1;
        }


        File f = File.createTempFile("htama", "aadml");
        System.out.println(f.toURI().toString());
        FileOutputStream fw = new FileOutputStream(f);
        fw.write(bout.toByteArray());
        fw.flush();
        fw.close();

        File tfhtml = new File(f.getAbsolutePath() + "." + ext);
        f.renameTo(tfhtml);
        Image I = Toolkit.getDefaultToolkit().getImage(tfhtml.toURI().toURL());

        cp.add(new JScrollPane(new JLabel(new ImageIcon(I))), BorderLayout.CENTER);
        setSize(640, 480);
    }
}

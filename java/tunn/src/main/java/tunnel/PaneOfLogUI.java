package tunnel;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.util.Set;
import java.util.SortedMap;


public class PaneOfLogUI extends JPanel {

    JTextArea txta_FromClient = new JTextArea("Wait Data ...");
    JTextArea txta_FromServer = new JTextArea("Wait Data ...");

    BigTunnelPane ui;

    JComboBox m_BoxEncodingInput = new JComboBox();
    JComboBox m_BoxEncodingOutput = new JComboBox();


    public PaneOfLogUI(BigTunnelPane ui) {
        this.ui = ui;

        JSplitPane splitter = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
        splitter.setOneTouchExpandable(true);
        JButton btn_ShowAsHTMLInput = new JButton("HTML");
        JButton btn_ShowAsHTMLOutput = new JButton("HTML");


        JButton btn_ShowAsAMFInput = new JButton("AMF");
        JButton btn_ShowAsAMFOutput = new JButton("AMF");


        JButton btn_ShowAsImageInput = new JButton("IMG");
        JButton btn_ShowAsImageOutput = new JButton("IMG");


        txta_FromClient.setEditable(false);
        txta_FromClient.setEditable(true);

        setLayout(new BorderLayout());
        add(splitter, BorderLayout.CENTER);

        JPanel navi_in = new JPanel();
        JPanel navi_out = new JPanel();

        //navi_in.add(new JLabel("Просмотр как"));
        navi_in.add(btn_ShowAsHTMLInput);

        //navi_out.add(new JLabel("Просмотр как"));
        navi_out.add(btn_ShowAsHTMLOutput);

        navi_in.add(btn_ShowAsAMFInput);
        navi_out.add(btn_ShowAsAMFOutput);

        navi_in.add(btn_ShowAsImageInput);
        navi_out.add(btn_ShowAsImageOutput);

        //navi_in.add(new JLabel("Кодировка"));
        navi_in.add(m_BoxEncodingInput);
        //navi_out.add(new JLabel("Кодировка"));
        navi_out.add(m_BoxEncodingOutput);


        txta_FromClient.setWrapStyleWord(true);
        txta_FromServer.setWrapStyleWord(true);

        JPanel pane_left = new JPanel(new BorderLayout());
        JPanel pane_right = new JPanel(new BorderLayout());

        //txta_FromClient.setBorder(BorderFactory.createLineBorder(Color.RED, 5));
        //txta_FromServer.setBorder(BorderFactory.createLineBorder(Color.BLUE, 5));

        pane_left.add(new JScrollPane(txta_FromClient), BorderLayout.CENTER);
        pane_right.add(new JScrollPane(txta_FromServer), BorderLayout.CENTER);


        pane_left.add(navi_in, BorderLayout.NORTH);
        pane_right.add(navi_out, BorderLayout.NORTH);

        splitter.setLeftComponent(pane_left);
        splitter.setRightComponent(pane_right);

        // заполняем список кодировок
        SortedMap<String, Charset> map = Charset.availableCharsets();
        Set<String> ks = map.keySet();
        for (String k : ks) {
            m_BoxEncodingInput.addItem(k);
            m_BoxEncodingOutput.addItem(k);
        }// ---- for ----


        btn_ShowAsHTMLInput.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                showHTMLPane(pair.beeIn.getDataAsText(), (String) m_BoxEncodingInput.getSelectedItem());
            }
        });
        btn_ShowAsHTMLOutput.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                showHTMLPane(pair.beeOut.getDataAsText(), (String) m_BoxEncodingOutput.getSelectedItem());
            }
        });

        btn_ShowAsAMFInput.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                //showHTMLPane(pair.beeIn.getDataAsText(), (String) m_BoxEncodingInput.getSelectedItem());
            }
        });
        btn_ShowAsAMFOutput.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                //showHTMLPane(pair.beeOut.getDataAsText(), (String) m_BoxEncodingOutput.getSelectedItem());
            }
        });


        btn_ShowAsImageInput.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                showImagePane(pair.beeIn.getDataAsText(), (String) m_BoxEncodingInput.getSelectedItem());
            }
        });
        btn_ShowAsImageOutput.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                showImagePane(pair.beeOut.getDataAsText(), (String) m_BoxEncodingOutput.getSelectedItem());
            }
        });

        splitter.setDividerLocation(0.5);
    }


    SmallBeePair pair;

    public void initParams(SmallBeePair pair) {
        this.pair = pair;
        byte[] saved_text_in = pair.getBeeIn().getDataAsText();
        byte[] saved_text_out = pair.getBeeOut().getDataAsText();
        try {
            txta_FromClient.setText(new String(saved_text_in, (String) m_BoxEncodingInput.getSelectedItem()));
            txta_FromServer.setText(new String(saved_text_out, (String) m_BoxEncodingOutput.getSelectedItem()));
        } catch (UnsupportedEncodingException e) {
            JOptionPane.showMessageDialog(this, "Ошибка, не поддерживаемая кодировка:\n" + e);
        }
    }

    public void showHTMLPane(byte[] data, String encoding) {
        try {
            final DialogHTMLView dialogHTMLView = new DialogHTMLView(UIForm.getFrameInstance(), data, encoding);
            SwingUtilities.invokeLater(
                    new Runnable() {
                        public void run() {
                            dialogHTMLView.setVisible(true);
                        }
                    }
            );
        } catch (IOException e) {
            JOptionPane.showMessageDialog(this, "Ошибка, не возможно отобразить окно HTML:\n" + e);
        }
    }

    private void showImagePane(byte[] dataAsText, String encoding) {
        try {
            final DialogImageView dialogImageView = new DialogImageView(UIForm.getFrameInstance(), dataAsText, encoding);
            SwingUtilities.invokeLater(
                    new Runnable() {
                        public void run() {
                            dialogImageView.setVisible(true);
                        }
                    }
            );
        } catch (IOException e) {
            JOptionPane.showMessageDialog(this, "Ошибка, не возможно отобразить окно ИЗОБРАЖЕНИЯ:\n" + e);
        }

    }

}


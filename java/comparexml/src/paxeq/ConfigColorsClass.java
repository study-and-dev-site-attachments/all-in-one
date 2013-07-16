package paxeq;

import org.syntax.jedit.JEditTextArea;
import org.syntax.jedit.SyntaxStyle;
import org.syntax.jedit.tokenmarker.Token;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.XMLDecoder;
import java.beans.XMLEncoder;
import java.io.*;


public class ConfigColorsClass extends JDialog {

    ConfigColorsDataStorage storage = new ConfigColorsDataStorage();

    private JFormattedTextField COMMENT1 = new JFormattedTextField();
    private JFormattedTextField COMMENT2 = new JFormattedTextField();
    private JFormattedTextField KEYWORD1 = new JFormattedTextField();
    private JFormattedTextField KEYWORD2 = new JFormattedTextField();
    private JFormattedTextField KEYWORD3 = new JFormattedTextField();
    private JFormattedTextField LITERAL1 = new JFormattedTextField();
    private JFormattedTextField LITERAL2 = new JFormattedTextField();
    private JFormattedTextField LABEL = new JFormattedTextField();
    private JFormattedTextField OPERATOR = new JFormattedTextField();
    private JFormattedTextField INVALID = new JFormattedTextField();


    private JLabel labCOMMENT1 = new JLabel("COMMENT1");
    private JLabel labCOMMENT2 = new JLabel("COMMENT2");
    private JLabel labKEYWORD1 = new JLabel("KEYWORD1");
    private JLabel labKEYWORD2 = new JLabel("KEYWORD2");
    private JLabel labKEYWORD3 = new JLabel("KEYWORD3");
    private JLabel labLITERAL1 = new JLabel("LITERAL1");
    private JLabel labLITERAL2 = new JLabel("LITERAL2");
    private JLabel labLABEL = new JLabel("LABEL");
    private JLabel labOPERATOR = new JLabel("OPERATOR");
    private JLabel labINVALID = new JLabel("INVALID");


    public String getLABEL() {
        return LABEL.getText();
    }

    public void setLABEL(String LABEL) {
        this.LABEL.setText(LABEL);
        this.LABEL.setForeground(ConfigColorsDataStorage.RGB(LABEL));
    }

    public String getOPERATOR() {
        return OPERATOR.getText();
    }

    public void setOPERATOR(String OPERATOR) {
        this.OPERATOR.setText(OPERATOR);
        this.OPERATOR.setForeground(ConfigColorsDataStorage.RGB(OPERATOR));
    }

    private JButton btnCOMMENT1 = new JButton("...");
    private JButton btnCOMMENT2 = new JButton("...");
    private JButton btnKEYWORD1 = new JButton("...");
    private JButton btnKEYWORD2 = new JButton("...");
    private JButton btnKEYWORD3 = new JButton("...");
    private JButton btnLITERAL1 = new JButton("...");
    private JButton btnLITERAL2 = new JButton("...");
    private JButton btnLABEL = new JButton("...");
    private JButton btnOPERATOR = new JButton("...");
    private JButton btnINVALID = new JButton("...");


    public String getCOMMENT1() {
        return COMMENT1.getText();
    }

    public void setCOMMENT1(String COMMENT1) {
        this.COMMENT1.setText(COMMENT1);
        this.COMMENT1.setForeground(ConfigColorsDataStorage.RGB(COMMENT1));
    }

    public String getCOMMENT2() {
        return COMMENT2.getText();
    }

    public void setCOMMENT2(String COMMENT2) {
        this.COMMENT2.setText(COMMENT2);
        this.COMMENT2.setForeground(ConfigColorsDataStorage.RGB(COMMENT2));
    }

    public String getKEYWORD1() {
        return KEYWORD1.getText();
    }

    public void setKEYWORD1(String KEYWORD1) {
        this.KEYWORD1.setText(KEYWORD1);
        this.KEYWORD1.setForeground(ConfigColorsDataStorage.RGB(KEYWORD1));
    }

    public String getKEYWORD2() {
        return KEYWORD2.getText();
    }

    public void setKEYWORD2(String KEYWORD2) {
        this.KEYWORD2.setText(KEYWORD2);
        this.KEYWORD2.setForeground(ConfigColorsDataStorage.RGB(KEYWORD2));
    }

    public String getKEYWORD3() {
        return KEYWORD3.getText();
    }

    public void setKEYWORD3(String KEYWORD3) {
        this.KEYWORD3.setText(KEYWORD3);
        this.KEYWORD3.setForeground(ConfigColorsDataStorage.RGB(KEYWORD3));
    }

    public String getLITERAL1() {
        return LITERAL1.getText();
    }

    public void setLITERAL1(String LITERAL1) {
        this.LITERAL1.setText(LITERAL1);
        this.LITERAL1.setForeground(ConfigColorsDataStorage.RGB(LITERAL1));
    }

    public String getLITERAL2() {
        return LITERAL2.getText();
    }

    public void setLITERAL2(String LITERAL2) {
        this.LITERAL2.setText(LITERAL2);
        this.LITERAL2.setForeground(ConfigColorsDataStorage.RGB(LITERAL2));
    }

    public String getINVALID() {
        return INVALID.getText();
    }

    public void setINVALID(String INVALID) {
        this.INVALID.setText(INVALID);
        this.INVALID.setForeground(ConfigColorsDataStorage.RGB(INVALID));
    }


    public ConfigColorsClass(XCmpFrame xCmpFrame) {
        super(xCmpFrame);
        setModal(true);
        setTitle("Color XML Settings");
        btnCOMMENT1.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                setCOMMENT1(getColor(getCOMMENT1()));
            }
        });
        btnCOMMENT2.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                setCOMMENT2((getColor((getCOMMENT2()))));
            }
        });
        btnKEYWORD1.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                setKEYWORD1((getColor((getKEYWORD1()))));
            }
        });
        btnKEYWORD2.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                setKEYWORD2((getColor((getKEYWORD2()))));
            }
        });
        btnKEYWORD3.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                setKEYWORD3((getColor((getKEYWORD3()))));
            }
        });
        btnLITERAL1.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                setLITERAL1((getColor((getLITERAL1()))));
            }
        });
        btnLITERAL2.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                setLITERAL2((getColor((getLITERAL2()))));
            }
        });
        btnLABEL.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                setLABEL((getColor((getLABEL()))));
            }
        });
        btnINVALID.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                setINVALID((getColor((getINVALID()))));
            }
        });
        btnOPERATOR.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                setOPERATOR((getColor((getOPERATOR()))));
            }
        });

        Container coco = getContentPane();
        coco.setLayout(new GridLayout(0, 3));
        coco.add(labCOMMENT1);
        coco.add(COMMENT1);
        coco.add(btnCOMMENT1);
        coco.add(labCOMMENT2);
        coco.add(COMMENT2);
        coco.add(btnCOMMENT2);
        coco.add(labKEYWORD1);
        coco.add(KEYWORD1);
        coco.add(btnKEYWORD1);
        coco.add(labKEYWORD2);
        coco.add(KEYWORD2);
        coco.add(btnKEYWORD2);
        coco.add(labKEYWORD3);
        coco.add(KEYWORD3);
        coco.add(btnKEYWORD3);
        coco.add(labLITERAL1);
        coco.add(LITERAL1);
        coco.add(btnLITERAL1);
        coco.add(labLITERAL2);
        coco.add(LITERAL2);
        coco.add(btnLITERAL2);
        coco.add(labLABEL);
        coco.add(LABEL);
        coco.add(btnLABEL);
        coco.add(labINVALID);
        coco.add(INVALID);
        coco.add(btnINVALID);
        coco.add(labOPERATOR);
        coco.add(OPERATOR);
        coco.add(btnOPERATOR);


        JButton btnOkey = new JButton("Ok");
        JButton btnCancel = new JButton("Cancel");
        btnOkey.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                String h = System.getProperty("user.home", ".");

                storage.setCOMMENT1((getCOMMENT1()));
                storage.setCOMMENT2((getCOMMENT2()));
                storage.setINVALID((getINVALID()));
                storage.setKEYWORD1((getCOMMENT1()));
                storage.setKEYWORD2((getKEYWORD2()));
                storage.setKEYWORD3((getKEYWORD3()));
                storage.setLABEL((getLABEL()));
                storage.setLITERAL1((getLITERAL1()));
                storage.setLITERAL2((getLITERAL2()));
                storage.setOPERATOR((getOPERATOR()));

                try {
                    XMLEncoder enc = new XMLEncoder(new BufferedOutputStream(new FileOutputStream(new File(h, ".comparexml.config.xml"))));
                    enc.writeObject(storage);
                    enc.close();
                } catch (Exception e1) {
                }
                m_ModalResult = true;
                setVisible(false);
            }
        });

        btnCancel.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                m_ModalResult = true;
                setVisible(false);
            }
        });
        coco.add(btnOkey);
        coco.add(new JLabel());
        coco.add(btnCancel);
        prealodColorsIntoUI();
        pack();
    }

    private void prealodColorsIntoUI() {
        String h = System.getProperty("user.home", ".");
        try {
            XMLDecoder dec = new XMLDecoder(new BufferedInputStream(new FileInputStream(new File(h, ".comparexml.config.xml"))));
            storage = (ConfigColorsDataStorage) dec.readObject();
            dec.close();
        } catch (Exception e) {
        }
        setCOMMENT1(storage.getCOMMENT1());
        setCOMMENT2(storage.getCOMMENT2());
        setINVALID(storage.getINVALID());
        setKEYWORD1(storage.getKEYWORD1());
        setKEYWORD2(storage.getKEYWORD2());
        setKEYWORD3(storage.getKEYWORD3());
        setLABEL(storage.getLABEL());
        setLITERAL1(storage.getLITERAL1());
        setLITERAL2(storage.getLITERAL2());
        setOPERATOR(storage.getOPERATOR());
    }

    public ConfigColorsClass() throws HeadlessException {
    }

    public String getColor(String c) {
        Color cc = Color.BLACK;
        try {
            cc = Color.decode(c);
        } catch (NumberFormatException e) {
        }
        return ConfigColorsDataStorage._RGB(JColorChooser.showDialog(null, "Choose Color", cc));
    }

    public void ApplySettingsToJEditTextArea(JEditTextArea jta) {

        String h = System.getProperty("user.home", ".");
        try {
            XMLDecoder dec = new XMLDecoder(new BufferedInputStream(new FileInputStream(new File(h, ".comparexml.config.xml"))));
            storage = (ConfigColorsDataStorage) dec.readObject();
            dec.close();
        } catch (Exception e) {
        }

        SyntaxStyle[] styles = new SyntaxStyle[Token.ID_COUNT];
        styles[Token.COMMENT1] = new SyntaxStyle(storage.xgetCOMMENT1(), true, false);
        styles[Token.COMMENT2] = new SyntaxStyle(storage.xgetCOMMENT2(), false, true);
        styles[Token.KEYWORD1] = new SyntaxStyle(storage.xgetKEYWORD1(), false, true);
        styles[Token.KEYWORD2] = new SyntaxStyle(storage.xgetKEYWORD2(), false, false);
        styles[Token.KEYWORD3] = new SyntaxStyle(storage.xgetKEYWORD3(), false, true);
        styles[Token.LITERAL1] = new SyntaxStyle(storage.xgetLITERAL1(), false, true);
        styles[Token.LITERAL2] = new SyntaxStyle(storage.xgetLITERAL2(), false, true);
        styles[Token.LABEL] = new SyntaxStyle(storage.xgetLABEL(), false, false);
        styles[Token.OPERATOR] = new SyntaxStyle(storage.xgetOPERATOR(), false, true);
        styles[Token.INVALID] = new SyntaxStyle(storage.xgetINVALID(), false, true);

        jta.getPainter().setStyles(styles);
    }

    boolean m_ModalResult = false;

    public boolean getModalResult() {
        return m_ModalResult;
    }

    public void setVisible(boolean b) {
        if (b)
            m_ModalResult = false;
        super.setVisible(b);
    }
}

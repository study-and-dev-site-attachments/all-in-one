package paxeq;

import java.awt.*;


public class ConfigColorsDataStorage {
    private Color COMMENT1 = Color.green;
    private Color COMMENT2 = Color.black;
    private Color KEYWORD1 = Color.blue;
    private Color KEYWORD2 = Color.black;
    private Color KEYWORD3 = Color.black;
    private Color LITERAL1 = new Color(0xa3a1a1);
    private Color LITERAL2 = new Color(0xa3a1a1);
    private Color LABEL = Color.black;
    private Color OPERATOR = Color.black;
    private Color INVALID = Color.red;


    public static String _RGB(Color c) {
        return "#" + toHex(c.getRed()) + toHex(c.getGreen()) + toHex(c.getBlue());
    }

    private static String toHex(int dec) {
        int f = dec / 16;
        int l = dec % 16;
        String sf = to16(f);
        String sl = to16(l);
        return sf + sl;
    }

    private static String to16(int f) {
        if (f < 10) return "" + f;
        if (f == 10) return "A";
        if (f == 11) return "B";
        if (f == 12) return "C";
        if (f == 13) return "D";
        if (f == 14) return "E";
        if (f == 15) return "F";
        return "0";
    }

    public static Color RGB(String color) {
        Color c = Color.GREEN;
        try {
            c = Color.decode(color);
        } catch (NumberFormatException e) {
        }
        return c;
    }


    public String getCOMMENT1() {
        return _RGB(COMMENT1);
    }

    public void setCOMMENT1(String COMMENT1) {
        this.COMMENT1 = RGB(COMMENT1);
    }

    public String getCOMMENT2() {
        return _RGB(COMMENT2);
    }

    public void setCOMMENT2(String COMMENT2) {
        this.COMMENT2 = RGB(COMMENT2);
    }

    public String getKEYWORD1() {
        return _RGB(KEYWORD1);
    }

    public void setKEYWORD1(String KEYWORD1) {
        this.KEYWORD1 = RGB(KEYWORD1);
    }

    public String getKEYWORD2() {
        return _RGB(KEYWORD2);
    }

    public void setKEYWORD2(String KEYWORD2) {
        this.KEYWORD2 = RGB(KEYWORD2);
    }

    public String getKEYWORD3() {
        return _RGB(KEYWORD3);
    }

    public void setKEYWORD3(String KEYWORD3) {
        this.KEYWORD3 = RGB(KEYWORD3);
    }

    public String getLITERAL1() {
        return _RGB(LITERAL1);
    }

    public void setLITERAL1(String LITERAL1) {
        this.LITERAL1 = RGB(LITERAL1);
    }

    public String getLITERAL2() {
        return _RGB(LITERAL2);
    }

    public void setLITERAL2(String LITERAL2) {
        this.LITERAL2 = RGB(LITERAL2);
    }

    public String getLABEL() {
        return _RGB(LABEL);
    }

    public void setLABEL(String LABEL) {
        this.LABEL = RGB(LABEL);
    }

    public String getOPERATOR() {
        return _RGB(OPERATOR);
    }

    public void setOPERATOR(String OPERATOR) {
        this.OPERATOR = RGB(OPERATOR);
    }

    public String getINVALID() {
        return _RGB(INVALID);
    }

    public void setINVALID(String INVALID) {
        this.INVALID = RGB(INVALID);
    }

    public Color xgetCOMMENT1() {
        return COMMENT1;
    }

    public void xsetCOMMENT1(Color COMMENT1) {
        this.COMMENT1 = COMMENT1;
    }

    public Color xgetCOMMENT2() {
        return COMMENT2;
    }

    public void xsetCOMMENT2(Color COMMENT2) {
        this.COMMENT2 = COMMENT2;
    }

    public Color xgetKEYWORD1() {
        return KEYWORD1;
    }

    public void xsetKEYWORD1(Color KEYWORD1) {
        this.KEYWORD1 = KEYWORD1;
    }

    public Color xgetKEYWORD2() {
        return KEYWORD2;
    }

    public void xsetKEYWORD2(Color KEYWORD2) {
        this.KEYWORD2 = KEYWORD2;
    }

    public Color xgetKEYWORD3() {
        return KEYWORD3;
    }

    public void xsetKEYWORD3(Color KEYWORD3) {
        this.KEYWORD3 = KEYWORD3;
    }

    public Color xgetLITERAL1() {
        return LITERAL1;
    }

    public void xsetLITERAL1(Color LITERAL1) {
        this.LITERAL1 = LITERAL1;
    }

    public Color xgetLITERAL2() {
        return LITERAL2;
    }

    public void xsetLITERAL2(Color LITERAL2) {
        this.LITERAL2 = LITERAL2;
    }

    public Color xgetLABEL() {
        return LABEL;
    }

    public void xsetLABEL(Color LABEL) {
        this.LABEL = LABEL;
    }

    public Color xgetOPERATOR() {
        return OPERATOR;
    }

    public void xsetOPERATOR(Color OPERATOR) {
        this.OPERATOR = OPERATOR;
    }

    public Color xgetINVALID() {
        return INVALID;
    }

    public void xsetINVALID(Color INVALID) {
        this.INVALID = INVALID;
    }
}

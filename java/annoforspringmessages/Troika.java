package blz.server.utils.anno;


/**
 * ��������������� �����. ������ ��� ����� ������������ html-������ �� �����������
 * ������������ ��������� spring �� ������� ������� ���������� ����� ���������
 */
public class Troika {
    /**
     * ������
     */
    String locale;
    /**
     * ��� ���������
     */
    String keyCode;
    /**
     * ��� ���� ������
     */
    String fieldName;
    /**
     * ��� ������ ���������� ������������� ������ ���������
     */
    String validatorClassName;
    /**
     * �������� ��������� spring-�� �� ����
     */
    String value;

    public Troika(String locale, String keyCode, String fieldName, String validatorClassName, String value) {
        this.locale = locale;
        this.keyCode = keyCode;
        this.fieldName = fieldName;
        this.validatorClassName = validatorClassName;
        this.value = value;
    }

    public String getLocale() {
        return locale;
    }

    public String getKeyCode() {
        return keyCode;
    }

    public String getFieldName() {
        return fieldName;
    }

    public String getValidatorClassName() {
        int p = validatorClassName.lastIndexOf(".");
        return validatorClassName.substring(0, p - 1) + " " + validatorClassName.substring(p + 1);
    }

    public String getValue() {
        return value;
    }
}

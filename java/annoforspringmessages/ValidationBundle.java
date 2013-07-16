package blz.server.utils.anno;

import java.io.File;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * ��������� ��� ���������� ��� �������, ������� ����� ������������
 * ������ �������� �� �� ������� ���������� ����� ���������
 */

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface ValidationBundle {
    /**
     * ������� ��� ResourceBundle
     * @return
     */
    String value();

    /**
     * ������ �������, ��� ������� ����� ��������� ��������
     * @return
     */
    String[] locales () default {};


    /**
     * ��������� ����� � ���������, �� ��� ����������, ��� ������ ...
     * @return
     */
    String encoding () default "ISO8859-1";



    @Retention(RetentionPolicy.RUNTIME)
    @Target({ElementType.FIELD} )

    /**
     * ��� ���� ������, �� ��� �� ��� ������, � ��� ���������� ��� ����� 
     */
    public static @interface Marker {}
}

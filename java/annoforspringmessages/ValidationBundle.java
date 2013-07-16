package blz.server.utils.anno;

import java.io.File;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * јннотаци€ дл€ маркировки тех классов, которые хот€т использовать
 * услуги проверки их на наличие корректных кодов сообщений
 */

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface ValidationBundle {
    /**
     * Ѕазовое им€ ResourceBundle
     * @return
     */
    String value();

    /**
     * —писок локалей, дл€ которых нужно выполнить проверку
     * @return
     */
    String[] locales () default {};


    /**
     *  одировка файла с ресурсами, ну так получилось, что делать ...
     * @return
     */
    String encoding () default "ISO8859-1";



    @Retention(RetentionPolicy.RUNTIME)
    @Target({ElementType.FIELD} )

    /**
     * ≈ще один маркер, но уже не дл€ класса, а дл€ конкретных его полей 
     */
    public static @interface Marker {}
}

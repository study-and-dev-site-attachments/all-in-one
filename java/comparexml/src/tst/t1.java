package tst;

import org.ccil.cowan.tagsoup.CommandLine;
import org.xml.sax.SAXException;

import java.io.IOException;
import java.io.File;

/**
 * Created by IntelliJ IDEA.
 * User: Администратор
 * Date: 28.08.2005
 * Time: 19:05:15
 * To change this template use File | Settings | File Templates.
 */
public class t1 {
    public static void main(String[] args) throws IOException, SAXException {
        CommandLine.process(
                new File("H:\\Docs_java\\jsf_ru\\1index.html").toURI().toURL().toExternalForm()
                , System.out);
       
    }
}

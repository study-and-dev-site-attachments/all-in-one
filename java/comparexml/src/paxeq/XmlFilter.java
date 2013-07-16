package paxeq;

import javax.swing.filechooser.FileFilter;
import java.io.File;

/**
 * Created by IntelliJ IDEA.
 * User: Programmer
 * Date: 10.05.2007
 * Time: 0:05:08
 * To change this template use File | Settings | File Templates.
 */
class XmlFilter extends FileFilter {
    public boolean accept(File f) {
        if (f.isDirectory()) return true;
        String s = f.getName().toLowerCase();
        return s.endsWith("xml") || s.endsWith("html") || s.endsWith("xhtml") || s.endsWith("htm");
    }

    public String getDescription() {
        return "XML & XHTML & HTML Документы (*.xml; *.html; *.htm; *.xhtml)";
    }
}

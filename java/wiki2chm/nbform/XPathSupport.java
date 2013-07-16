package nbform;

import com.sun.org.apache.xml.internal.serialize.OutputFormat;
import com.sun.org.apache.xml.internal.serialize.XMLSerializer;
import com.sun.org.apache.xpath.internal.XPathAPI;
import com.sun.org.apache.xpath.internal.objects.XObject;
import org.jaxen.JaxenException;
import org.jaxen.dom.DOMXPath;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.traversal.NodeIterator;

import javax.xml.transform.TransformerException;
import java.io.ByteArrayOutputStream;
import java.io.OutputStreamWriter;
import java.io.IOException;
import java.util.*;

/**
 * Created by IntelliJ IDEA.
 * User: Programmer
 * Date: 30.08.2007
 * Time: 0:09:06
 * To change this template use File | Settings | File Templates.
 */

/**
 * Вспомогательный класс для работы с выражениями XPath
 * В основе работы приложения используется Jaxen
 * Все функции класса являются статическими
 */
public class XPathSupport {

    /**
     * Функция выполняет поиск узла дерева на основании некоторого Xpath-выражения и возвращает его содержимое
     *
     * @param doc - узел с которого следует начать поиск
     * @param s   - строка xpath-выражения (что нужно  искать)
     * @return - строку содержащую представление содержимого найденного узла
     */
    public static String getByXPath(Node doc, String s) {
        NodeIterator it = null;
        try {
            XObject xob = XPathAPI.eval(doc, s);
            it = xob.nodeset();
        } catch (TransformerException e) {
            e.printStackTrace();
            return null;
        }
        Node cur = it.nextNode();
        if (cur == null) {
            return null;
        }
        return cur.getNodeValue();
    }

    /**
     * На основании xpath-выражения выполняется поиск всех узлов в дереве
     *
     * @param doc - узел с которого нужно начать поиск
     * @param s   - строка XPath-выражения для начала поиска
     * @return - список найденных узлов (но не объект Node а его строковое представление)
     */
    public static ArrayList<String> getByAllByXPath(Node doc, String s) {
        ArrayList<String> strs = new ArrayList<String>();

        NodeIterator it = null;
        try {
            XObject xob = XPathAPI.eval(doc, s);
            it = xob.nodeset();
        } catch (TransformerException e) {
            e.printStackTrace();
            return null;
        }
        Node cur = null;
        while ((cur = it.nextNode()) != null) {
            strs.add(cur.getNodeValue());
        }
        return strs;
    }

    /**
     * Сведения об отображении узлов документа на их namespaces
     */
    private static HashMap<Node, Set> mapDocToNamesSpaces = new HashMap<Node, Set>();

    /**
     * Поиск узла на основании xpath-выражения
     *
     * @param doc - узел с которого нужно начать поиск
     * @param s   - строка xpath-выражения для начала поиска
     * @return - строковое представление содержимого найденного узла
     */
    public static String get_By_Jaxen_String(Node doc, String s) {
        try {
            List result = helper_SelectNodes(s, doc);
            if (result.size() == 0)
                return null;
            else {
                Node node_item = (Node) result.get(0);
                return node_item.getNodeValue();
            }
        } catch (JaxenException e) {
            e.printStackTrace();
            return null;
        }

    }

    /**
     * На основании xpath-выражения выполняется поиск всех узлов в дереве
     *
     * @param doc - узел с которого нужно начать поиск
     * @param s   - строка XPath-выражения для начала поиска
     * @return - список найденных узлов (но не объект Node а его строковое представление)
     */
    public static ArrayList<String> get_By_Jaxen_Strings(Node doc, String s) {
        ArrayList<String> rez = new ArrayList<String>();
        try {
            List result = helper_SelectNodes(s, doc);
            if (result.size() == 0)
                return null;
            else {
                for (int i = 0; i < result.size(); i++) {
                    Node node_item = (Node) result.get(i);
                    rez.add(node_item.getNodeValue());
                }
                return rez;
            }
        } catch (JaxenException e) {
            e.printStackTrace();
            return null;
        }

    }

    /**
     * Вспомогательная функция вызывается из  get_By_Jaxen_Strings
     *
     * @param xpath_expr Строка xpath-выражения для начала поиска
     * @param curNode    - узел с которого нужно начать поиск
     * @return список найденных узлов (именно узлов)
     * @throws JaxenException
     */
    private static ArrayList<Node> helper_SelectNodes(String xpath_expr, Node curNode) throws JaxenException {
        DOMXPath xpath = new DOMXPath(xpath_expr);
        Set namespaces = null;
        Node document = curNode.getOwnerDocument();
        if (document == null)
            document = curNode;

        if (!mapDocToNamesSpaces.containsKey(document)) {
            try {
                namespaces = new TreeSet();
                namespaces.add(new XPE_Namespace("default", "http://www.w3.org/1999/xhtml"));
            } catch (Exception e) {
                e.printStackTrace();
                namespaces = new TreeSet();
            }
            mapDocToNamesSpaces.put(document, namespaces);
        }
        namespaces = mapDocToNamesSpaces.get(document);
        for (Iterator i = namespaces.iterator(); i.hasNext();) {
            XPE_Namespace namespace = (XPE_Namespace) i.next();
            xpath.addNamespace(namespace.prefix, namespace.uri);
        }
        ArrayList<Node> rez = new ArrayList<Node>();
        rez.addAll(xpath.selectNodes(curNode));
        return rez;
    }

    /**
     * Содержиео узла необхоимо вернуть в виде корректной строки xml
     *
     * @param n Узел содержимое которого и нужно представить в виде строки
     * @param encodingName Кодировка строки в которую будет преобразован данный узел как строка
     * @param dropSpaces Следует ли удалять лишние знаки ввода, символы табуляции и пробелы
     * @return собственно строковое представление узла
     */
    public static String getNodeAsString(Node n, String encodingName, Boolean dropSpaces ) {
        try {
            ByteArrayOutputStream chaw = new ByteArrayOutputStream(4096);
            OutputStreamWriter outw = new OutputStreamWriter(chaw, encodingName);

            XMLSerializer domSer = new XMLSerializer();

            OutputFormat outputFormat = new OutputFormat("xml", encodingName, true);
            outputFormat.setOmitXMLDeclaration(true);
            outputFormat.setIndenting(false);
            outputFormat.setPreserveSpace(false);
            domSer.setOutputFormat(outputFormat);
            domSer.setOutputCharStream(outw);
            domSer.serialize((Element) n);
            outw.flush();
            String outs = chaw.toString(encodingName);
            if (dropSpaces){
                //outs  = outs.replaceAll("(?is)<style type=\"text\\/css\">.*?</style>", "");
                //outs = outs.replaceAll("\t", "");
                //outs = outs.replaceAll("\n", "");
                outs = outs.replaceAll(">\\s+<", "><");
            }
            return outs;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }


    /**
     * Метод ищет в указанном узле дерева атрибут с некоторым именем, в случае если такой атрибут не найден то возвращается null
     *
     * @param node     Узел где нужно найти атрибут
     * @param attrName имя искомого атрибута
     * @return значение искомого атрибута
     */
    public static String getAttribute(Node node, String attrName) {
        node = node.getAttributes().getNamedItem(attrName);
        if (node != null)
            return node.getNodeValue();
        return null;
    }

    /**
     * Метод ищет в узле дерева атрибут с заданным именем и в случае если он найден то изменяет значение атрибута на новое
     * @param node узел где нужно искать атрибут
     * @param attrName имя атрибута для поиска
     * @param attrValue новое значение атрибута
     */
    public static void setAttribute(Node node, String attrName, String attrValue) {
        node = node.getAttributes().getNamedItem(attrName);
        if (node != null)
            node.setNodeValue(attrValue);
    }
}

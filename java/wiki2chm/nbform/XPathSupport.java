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
 * ��������������� ����� ��� ������ � ����������� XPath
 * � ������ ������ ���������� ������������ Jaxen
 * ��� ������� ������ �������� ������������
 */
public class XPathSupport {

    /**
     * ������� ��������� ����� ���� ������ �� ��������� ���������� Xpath-��������� � ���������� ��� ����������
     *
     * @param doc - ���� � �������� ������� ������ �����
     * @param s   - ������ xpath-��������� (��� �����  ������)
     * @return - ������ ���������� ������������� ����������� ���������� ����
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
     * �� ��������� xpath-��������� ����������� ����� ���� ����� � ������
     *
     * @param doc - ���� � �������� ����� ������ �����
     * @param s   - ������ XPath-��������� ��� ������ ������
     * @return - ������ ��������� ����� (�� �� ������ Node � ��� ��������� �������������)
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
     * �������� �� ����������� ����� ��������� �� �� namespaces
     */
    private static HashMap<Node, Set> mapDocToNamesSpaces = new HashMap<Node, Set>();

    /**
     * ����� ���� �� ��������� xpath-���������
     *
     * @param doc - ���� � �������� ����� ������ �����
     * @param s   - ������ xpath-��������� ��� ������ ������
     * @return - ��������� ������������� ����������� ���������� ����
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
     * �� ��������� xpath-��������� ����������� ����� ���� ����� � ������
     *
     * @param doc - ���� � �������� ����� ������ �����
     * @param s   - ������ XPath-��������� ��� ������ ������
     * @return - ������ ��������� ����� (�� �� ������ Node � ��� ��������� �������������)
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
     * ��������������� ������� ���������� ��  get_By_Jaxen_Strings
     *
     * @param xpath_expr ������ xpath-��������� ��� ������ ������
     * @param curNode    - ���� � �������� ����� ������ �����
     * @return ������ ��������� ����� (������ �����)
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
     * ��������� ���� ��������� ������� � ���� ���������� ������ xml
     *
     * @param n ���� ���������� �������� � ����� ����������� � ���� ������
     * @param encodingName ��������� ������ � ������� ����� ������������ ������ ���� ��� ������
     * @param dropSpaces ������� �� ������� ������ ����� �����, ������� ��������� � �������
     * @return ���������� ��������� ������������� ����
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
     * ����� ���� � ��������� ���� ������ ������� � ��������� ������, � ������ ���� ����� ������� �� ������ �� ������������ null
     *
     * @param node     ���� ��� ����� ����� �������
     * @param attrName ��� �������� ��������
     * @return �������� �������� ��������
     */
    public static String getAttribute(Node node, String attrName) {
        node = node.getAttributes().getNamedItem(attrName);
        if (node != null)
            return node.getNodeValue();
        return null;
    }

    /**
     * ����� ���� � ���� ������ ������� � �������� ������ � � ������ ���� �� ������ �� �������� �������� �������� �� �����
     * @param node ���� ��� ����� ������ �������
     * @param attrName ��� �������� ��� ������
     * @param attrValue ����� �������� ��������
     */
    public static void setAttribute(Node node, String attrName, String attrValue) {
        node = node.getAttributes().getNamedItem(attrName);
        if (node != null)
            node.setNodeValue(attrValue);
    }
}

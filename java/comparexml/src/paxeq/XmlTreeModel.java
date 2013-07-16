package paxeq;

import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.swing.tree.TreeModel;
import javax.swing.tree.TreePath;
import javax.swing.event.TreeModelListener;

/**
 * Created by IntelliJ IDEA.
 * User: Programmer
 * Date: 10.05.2007
 * Time: 0:05:12
 * To change this template use File | Settings | File Templates.
 */
class XmlTreeModel implements TreeModel {
    Node root;

    public XmlTreeModel(Node root) {
        this.root = root;
    }

    public Object getRoot() {
        return root;
    }

    public Object getChild(Object parent, int index) {
        Node nparent = (Node) parent;
        return nparent.getChildNodes().item(index);
    }

    public int getChildCount(Object parent) {
        Node nparent = (Node) parent;
        return nparent.getChildNodes().getLength();
    }

    public boolean isLeaf(Object node) {
        Node nparent = (Node) node;
        return nparent.getChildNodes().getLength() == 0;
    }

    public void valueForPathChanged(TreePath path, Object newValue) {
    }

    public int getIndexOfChild(Object parent, Object child) {
        Node nparent = (Node) parent;
        NodeList nli = nparent.getChildNodes();
        for (int i = 0; i < nli.getLength(); i++)
            if (nli.item(i) == child)
                return i;
        return -1;
    }

    public void addTreeModelListener(TreeModelListener l) {
    }

    public void removeTreeModelListener(TreeModelListener l) {
    }
}

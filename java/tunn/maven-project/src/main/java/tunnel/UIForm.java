package tunnel;

import javax.swing.*;
import javax.swing.event.MouseInputAdapter;
import javax.swing.tree.DefaultMutableTreeNode;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.MouseEvent;
import java.net.InetAddress;


public class UIForm extends JFrame {

    DefaultMutableTreeNode m_Root = new DefaultMutableTreeNode("*");


    BeeTableModel tabBeeModel = new BeeTableModel();
    JTable tabBee = new JTable(tabBeeModel);

    BigTunnelPane paneCurBee;

    InetAddress ia_tmp = InetAddress.getLocalHost();
    String name_tmp = ia_tmp.getHostName() + "(" + ia_tmp.getHostAddress() + ")";

    public UIForm() throws Exception {
        super();
        setTitle("Tunneller Project: by Zhyshkevich Mikalai: " + name_tmp);
        setMinimumSize(new Dimension(640, 480));
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setDefaultLookAndFeelDecorated(true);

        JToolBar tools = new JToolBar(JToolBar.VERTICAL);
        tools.add(new AbstractAction("Новый туннель") {
            public void actionPerformed(ActionEvent e) {
                TunnelInfo info = UINewTunnel.createNewTunnel(UIForm.this);
                if (info == null) return;
                try {
                    tabBeeModel.addNewBee(info);
                    tabBeeModel.fireTableDataChanged();
                    validateTree();
                } catch (Exception e1) {
                    e1.printStackTrace();
                    JOptionPane.showMessageDialog(UIForm.this, e1, "Не возможно создать туннель", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        paneCurBee = new BigTunnelPane(this);

        final JSplitPane splitter = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
        splitter.setOneTouchExpandable(true);
        splitter.setLeftComponent(new JScrollPane(tabBee));
        splitter.setRightComponent(paneCurBee);
        splitter.setDividerLocation(200);


        tabBee.addMouseListener(
                new MouseInputAdapter() {
                    public void mouseClicked(MouseEvent e) {
                        int r = tabBee.getSelectedRow();
                        TunnelInfo f = tabBeeModel.getRowObject(r);
                        paneCurBee.initParams(f);
                        splitter.validate();
                        splitter.invalidate();
                    }
                }
        );

        getContentPane().add(tools, BorderLayout.PAGE_START);
        getContentPane().add(splitter, BorderLayout.CENTER);

        setSize(new Dimension(1000, 740));
        //setExtendedState(JFrame.MAXIMIZED_BOTH);
    }

    static UIForm uiForm = null;

    public static void main(String[] args) throws Exception {
        uiForm = new UIForm();
        SwingUtilities.invokeLater(
                new Runnable() {
                    public void run() {
                        uiForm.setVisible(true);
                    }
                }
        );
    }

    public static void fireAnythingFromBeeChanged(Bee bee, SmallBee smallBee) {
        uiForm.paneCurBee.fireAnythingFromBeeChanged(bee, smallBee);
    }

    public void finishDeleteBee(Bee bee) {
        tabBeeModel.removeBee(bee);
    }

    public static Frame getFrameInstance() {
        return uiForm;
    }

    public static void fireSmallBeesCountChanged(Bee bee) {
        uiForm.paneCurBee.fireSmallBeesCountChanged(bee);
    }
}


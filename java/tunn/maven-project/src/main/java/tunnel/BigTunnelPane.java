package tunnel;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;

public class BigTunnelPane extends JPanel {

    SmallBeesTableModel model = new SmallBeesTableModel();
    JTable table = new JTable(model);

    PaneOfLogUI panelog = new PaneOfLogUI(this);
    TunnelInfo info;
    Bee bee;

    JLabel labelHead = new JLabel();

    public void initParams(final TunnelInfo info) {
        this.info = info;
        this.bee = info.getBee();

        labelHead.setText("Listen: " + info.getM_ListenPort() + " Redirect to: " +
                info.getM_TargetUrl() + ":" + info.getM_TargetPort());
        model.setInfo(info);
    }

    public BigTunnelPane(final UIForm form) throws Exception {
        super(new BorderLayout());
        JSplitPane iam = new JSplitPane();
        JPanel pnavi = new JPanel(new FlowLayout());
        JPanel ptop = new JPanel(new BorderLayout());

        JButton btn_StopBee = new JButton("Закрыть туннель");
        JButton btn_StopSmallBee = new JButton("Разорвать соединение");
        JButton btn_DeleteOldSmallBee = new JButton("Очистить закрытие соединения");

        pnavi.add(btn_StopBee);
        pnavi.add(btn_StopSmallBee);
        pnavi.add(btn_DeleteOldSmallBee);

        ptop.add(pnavi, BorderLayout.SOUTH);
        ptop.add(new JScrollPane(table), BorderLayout.CENTER);

        add(labelHead, BorderLayout.NORTH);
        iam.setOrientation(JSplitPane.VERTICAL_SPLIT);
        iam.setOneTouchExpandable(true);
        iam.setTopComponent(ptop);
        iam.setBottomComponent(panelog);
        panelog.setMinimumSize(new Dimension(400, 400));
        panelog.setPreferredSize(new Dimension(400, 400));

        btn_StopBee.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                if (bee == null) return;
                bee.setTerminated(Boolean.TRUE);
                form.finishDeleteBee(bee);
            }
        });

        btn_StopSmallBee.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                if (bee == null) return;
                int selrow = table.getSelectedRow();
                SmallBeePair pa = model.getRowObject(selrow);
                if (pa != null) {
                    pa.beeIn.TerminateSmallBee();
                    pa.beeOut.TerminateSmallBee();
                } else {
                    JOptionPane.showMessageDialog(BigTunnelPane.this, "Для того чтобы остановить соединение сначала выберите его в таблице");
                    return;
                }
            }
        });

        btn_DeleteOldSmallBee.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                if (bee == null) return;

                ArrayList<SmallBeePair> list = model.getSmallBeeList();
                ArrayList<SmallBeePair> listForDel = new ArrayList<SmallBeePair>();
                for (SmallBeePair smallBeePair : list) {
                    if (smallBeePair.getBeeIn().isTerminated() && smallBeePair.getBeeOut().isTerminated()) {
                        listForDel.add(smallBeePair);
                    }
                }

                for (SmallBeePair smallBeePair : listForDel) {
                    list.remove(smallBeePair);
                }

                UIForm.fireSmallBeesCountChanged(bee);
            }
        });


        table.addMouseListener(
                new MouseAdapter() {
                    public void mouseClicked(MouseEvent e) {
                        java.awt.Point p = e.getPoint();
                        int rowIndex = table.rowAtPoint(p);
                        SmallBeePair pair = model.getRowObject(rowIndex);
                        if (pair == null) return;
                        panelog.initParams(pair);
                    }
                }
        );
        iam.setDividerLocation(200);
        add(iam, BorderLayout.CENTER);
    }


    public void fireAnythingFromBeeChanged(Bee bee, SmallBee smallBee) {
        if (this.bee == bee) {
            int smallberow = model.getSmallBeeRowNumber(smallBee);
            model.fireTableRowsUpdated(smallberow, smallberow);
        }
    }

    public void fireSmallBeesCountChanged(Bee bee) {
        if (this.bee == bee) {
            model.fireTableDataChanged();
        }

    }
}

package tunnel;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.net.ServerSocket;
import java.net.Socket;

@SuppressWarnings({"EmptyCatchBlock"})
public class UINewTunnel extends JDialog {

    JTextField m_ListenPort = new JTextField("");
    JTextField m_TargetPort = new JTextField("");
    JTextField m_TargetHost = new JTextField("");
    JCheckBox m_IsProxy = new JCheckBox("работать как прокси", true);

    JTextField m_ProxyUser = new JTextField("");
    JTextField m_ProxyPassword = new JTextField("");
    JTextField m_ParamsForProxyHost = new JTextField("");
    JTextField m_ParamsForProxyPort = new JTextField("");
    JPanel m_PaneParamsForProxy = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));

    JLabel m_StatusBar = new JLabel("");

    JComboBox m_ComboProxyTypes = new JComboBox(
            new Proxy.Type[]{
                    Proxy.Type.DIRECT,
                    Proxy.Type.HTTP,
                    Proxy.Type.SOCKS
            }
    );


    JButton m_TEST = new JButton("Тест");

    public UINewTunnel(final JFrame frame) throws HeadlessException {
        super(frame);
        setTitle("Новый туннель -- укажите настройки туннеля");
        setModal(true);

        JPanel pane = new JPanel(new GridLayout(0, 2));
        pane.add(new JLabel("Слушать порт"));
        pane.add(m_ListenPort);

        pane.add(new JLabel("Целевой адрес"));
        pane.add(m_TargetHost);

        pane.add(new JLabel("Целевой порт"));
        pane.add(m_TargetPort);

        pane.add(new JLabel("Параметры PROXY"));
        pane.add(new JLabel(""));

        m_PaneParamsForProxy.add(m_ParamsForProxyHost);
        m_PaneParamsForProxy.add(new JLabel(":"));
        m_PaneParamsForProxy.add(m_ParamsForProxyPort);
        pane.add(m_ComboProxyTypes);
        pane.add(m_PaneParamsForProxy);

        pane.add(new JLabel("Логин для Proxy"));
        pane.add(m_ProxyUser);

        pane.add(new JLabel("Пароль для Proxy"));
        pane.add(m_ProxyPassword);

        m_ParamsForProxyHost.setPreferredSize(new Dimension(180, 24));
        m_ParamsForProxyPort.setPreferredSize(new Dimension(40, 24));


        JButton m_OK = new JButton("Принять");
        JButton m_CANCEL = new JButton("Отказаться");
        JPanel m_navigator = new JPanel();
        m_navigator.add(m_OK);
        m_navigator.add(m_CANCEL);
        m_navigator.add(m_TEST);


        m_TEST.addActionListener(
                new ActionListener() {
                    public void actionPerformed(ActionEvent e) {
                        String s = m_ListenPort.getText();
                        m_StatusBar.setText("Начинаю проверку введенных данных");
                        int iport = 0;
                        try {
                            iport = Integer.parseInt(s);
                        } catch (NumberFormatException e1) {
                            JOptionPane.showMessageDialog(frame, "Ошибка, указанный вами номер порта не является корректной формой записи числа");
                            return;
                        }
                        ServerSocket sso = null;
                        try {
                            sso = new ServerSocket(iport);
                        } catch (IOException e1) {
                            JOptionPane.showMessageDialog(frame, "Ошибка, невозможно выполнить привязку к указанному вами порту, возможно он уже занят, или нет прав доступа");
                            return;
                        }
                        try {
                            sso.close();
                        } catch (IOException e1) {
                        }
                        m_StatusBar.setText("Порядок, указанный номер порта может быть использован");
                        Socket clis = null;
                        try {
                            Object o = m_ComboProxyTypes.getSelectedItem();
                            Proxy proxy = Proxy.NO_PROXY;
                            if (o != Proxy.Type.DIRECT)
                                proxy = new Proxy((Proxy.Type) o, new InetSocketAddress(
                                        m_ParamsForProxyHost.getText(), Integer.parseInt(m_ParamsForProxyPort.getText())
                                ));
                            clis = new Socket(proxy);

                            clis.connect(new InetSocketAddress(m_TargetHost.getText(), Integer.parseInt(m_TargetPort.getText())));

                        } catch (NumberFormatException e01) {
                            JOptionPane.showMessageDialog(frame, "Ошибка, указанный вами номер целевого порта не является корректной формой записи числа");
                            return;
                        }
                        catch (java.lang.IllegalArgumentException e10) {
                            JOptionPane.showMessageDialog(frame, "Ошибка, указанные вами параметры прокси-сервера не являются некорректными");
                            return;
                        }
                        catch (IOException e1) {
                            JOptionPane.showMessageDialog(frame, "Ошибка, указанные вами параметры адреса места-назначения являются некорректными");
                            return;
                        }
                        try {
                            clis.close();
                        } catch (IOException e1) {
                        }


                    }
                }
        );

        m_OK.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                try {
                    Integer portTarget = 0;
                    Integer portListen = 0;
                    Integer portProxy = 0;

                    try {
                        portTarget = Integer.valueOf(m_TargetPort.getText());
                    } catch (NumberFormatException e1) {
                    }
                    try {
                        portListen = Integer.valueOf(m_ListenPort.getText());
                    } catch (NumberFormatException e1) {
                    }
                    try {
                        portProxy = Integer.valueOf(m_ParamsForProxyPort.getText());
                    } catch (NumberFormatException e1) {
                    }


                    info = new TunnelInfo(m_IsProxy.isSelected(),
                            portListen,
                            m_ProxyPassword.getText(),
                            m_ProxyUser.getText(),
                            portTarget,
                            m_TargetHost.getText(),
                            (Proxy.Type) m_ComboProxyTypes.getSelectedItem(),
                            m_ParamsForProxyHost.getText(),
                            portProxy
                    );
                    setVisible(false);
                } catch (NumberFormatException e1) {
                    e1.printStackTrace();
                    JOptionPane.showMessageDialog(UINewTunnel.this, "Ошибка введены неправильно данные настроек:\n" + e1, "Ошибка создания нового туннеля", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        m_CANCEL.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                info = null;
                setVisible(false);
            }
        });

        getContentPane().add(pane, BorderLayout.CENTER);
        getContentPane().add(m_StatusBar, BorderLayout.NORTH);
        getContentPane().add(m_navigator, BorderLayout.SOUTH);

        pack();
        Point pp = frame.getLocation();
        pp.x += 100;
        pp.y += 100;
        setLocation(pp);
    }

    public static TunnelInfo createNewTunnel(JFrame frame) {
        UINewTunnel ui = new UINewTunnel(frame);
        ui.setVisible(true);
        return ui.getTunnelInfo();
    }

    TunnelInfo info = null;

    private TunnelInfo getTunnelInfo() {
        return info;
    }
}

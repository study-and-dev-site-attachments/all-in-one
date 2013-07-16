package tunnel;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.ArrayList;


public class Bee extends Thread {

    Boolean m_Terminated = Boolean.FALSE;
    ArrayList<SmallBeePair> listSmallBees = new ArrayList<SmallBeePair>();


    public Boolean getTerminated() {
        return m_Terminated;
    }

    public void setTerminated(Boolean m_Terminated) {
        this.m_Terminated = m_Terminated;
        try {
            if (m_Terminated)
                server.setSoTimeout(1);
            else
                server.setSoTimeout(0);
        } catch (SocketException e) {
            e.printStackTrace(System.out);
        }
    }

    TunnelInfo m_Tunnel;

    public Bee(TunnelInfo tunn) throws Exception {
        m_Tunnel = tunn;
        start();
    }

    ServerSocket server = null;

    public void run() {

        try {
            server = new ServerSocket(m_Tunnel.getM_ListenPort());
            System.out.println("TimeOut(Server): " + server.getSoTimeout());
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }
        while (!m_Terminated) {
            Socket client_connect = null;
            try {
                client_connect = server.accept();


                Socket to_server = m_Tunnel.getTargetSocket();

                System.out.println("Got New Connection: " + client_connect.getLocalAddress() + ":" + client_connect.getLocalPort());
                System.out.println("Has SmallBees Query Size = " + listSmallBees.size());

                InputStream in_from_client = client_connect.getInputStream();
                InputStream in_from_server = to_server.getInputStream();

                OutputStream out_from_client = client_connect.getOutputStream();
                OutputStream out_from_server = to_server.getOutputStream();

                final SmallBee smallBee1 = new SmallBee(this, client_connect, in_from_client, out_from_server);
                final SmallBee smallBee2 = new SmallBee(this, client_connect, in_from_server, out_from_client);

                smallBee1.setFriend(smallBee2);
                smallBee2.setFriend(smallBee1);

                listSmallBees.add(
                        new SmallBeePair(smallBee1, smallBee2)
                );
                smallBee1.start();
                smallBee2.start();

                UIForm.fireSmallBeesCountChanged(this);

            } catch (IOException e) {
                e.printStackTrace();
            }

        }
    }

    public void finishKillSmallBee(SmallBee smallBee) {
        for (SmallBeePair listSmallBee : listSmallBees) {
            if (listSmallBee.beeIn == smallBee || listSmallBee.beeOut == smallBee) {
                //listSmallBees.remove(listSmallBee);
                UIForm.fireSmallBeesCountChanged(this);
                return;
            }
        }

    }


    public ArrayList<SmallBeePair> getListSmallBees() {
        return listSmallBees;
    }

    public void updateBytesAmountValue(SmallBee smallBee) {
        UIForm.fireAnythingFromBeeChanged(this, smallBee);
    }
}

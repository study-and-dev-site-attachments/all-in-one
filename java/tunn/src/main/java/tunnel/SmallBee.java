package tunnel;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;


public class SmallBee extends Thread {
    InputStream in;
    OutputStream out;
    boolean m_IsTerminated = false;
    ByteArrayOutputStream bout = new ByteArrayOutputStream();

    public boolean isTerminated() {
        return m_IsTerminated;
    }

    Socket soc;
    Bee papa;
    SmallBee friend;

    public SmallBee(Bee papa, Socket soc, InputStream in, OutputStream out) {
        setDaemon(true);
        this.soc = soc;
        this.in = in;
        this.out = out;
        this.papa = papa;
    }

    public void TerminateSmallBee() {
        m_IsTerminated = true;
        if (!friend.isTerminated()) return;


        try {
            out.flush();
        } catch (IOException e) {
        }
        try {
            friend.out.flush();
        } catch (IOException e) {
        }

        try {
            out.close();
        } catch (IOException e) {
        }
        try {
            friend.out.close();
        } catch (IOException e) {
        }


        try {
            in.close();
        } catch (IOException e) {
        }


        try {
            friend.in.close();
        } catch (IOException e) {
        }

        papa.finishKillSmallBee(this);
    }

    public void run() {
        //byte buf [] = new byte [4096];
        while (!m_IsTerminated) {
            try {
                int bin = in.read();
                //int bin = in.read(buf);
                if (bin < 0) throw new IOException("end of data thread");
                out.write(bin);
                out.flush();

                //bout.write(buf, 0, bin);
                bout.write(bin);

                papa.updateBytesAmountValue(this);
            } catch (IOException e) {
                TerminateSmallBee();
            }
        }
    }


    public int getTunnel_RemotePort() {
        return soc.getPort();
    }

    public int getTunnel_LocalPort() {
        return soc.getLocalPort();
    }

    public int getTunnel_BytesAmount() {
        return bout.size();
    }

    public byte[] getDataAsText() {
        return bout.toByteArray();
    }

    public void setFriend(SmallBee smallBee2) {
        friend = smallBee2;
    }
}

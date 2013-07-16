package tunnel;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.net.Socket;
import java.util.Date;


public class TunnelInfo {
    Integer m_ListenPort;
    Integer m_TargetPort;
    String m_TargetUrl;
    Boolean isProxyConnection;

    String m_ProxyAuthUser;
    String m_ProxyAuthPassword;

    Proxy.Type m_ProxyType;
    String m_ProxyParamsHost;
    Integer m_ProxyParamsPort;
    Date m_beeStartLife;


    public TunnelInfo(Boolean proxyConnection,
                      Integer m_ListenPort, String m_ProxyAuthPassword,
                      String m_ProxyAuthUser, Integer m_TargetPort, String m_TargetUrl,
                      Proxy.Type m_ProxyType, String m_ProxyParamsHost, Integer m_ProxyParamsPort
    ) {
        isProxyConnection = proxyConnection;
        this.m_ListenPort = m_ListenPort;
        this.m_ProxyAuthPassword = m_ProxyAuthPassword;
        this.m_ProxyAuthUser = m_ProxyAuthUser;
        this.m_TargetPort = m_TargetPort;
        this.m_TargetUrl = m_TargetUrl;

        this.m_ProxyType = m_ProxyType;
        this.m_ProxyParamsHost = m_ProxyParamsHost;
        this.m_ProxyParamsPort = m_ProxyParamsPort;

        this.m_beeStartLife = new Date();
    }

    public Boolean getProxyConnection() {
        return isProxyConnection;
    }

    public void setProxyConnection(Boolean proxyConnection) {
        isProxyConnection = proxyConnection;
    }


    public Integer getM_ListenPort() {
        return m_ListenPort;
    }

    public void setM_ListenPort(Integer m_ListenPort) {
        this.m_ListenPort = m_ListenPort;
    }

    public Integer getM_TargetPort() {
        return m_TargetPort;
    }

    public void setM_TargetPort(Integer m_TargetPort) {
        this.m_TargetPort = m_TargetPort;
    }

    public String getM_TargetUrl() {
        return m_TargetUrl;
    }

    public void setM_TargetUrl(String m_TargetUrl) {
        this.m_TargetUrl = m_TargetUrl;
    }

    public String getM_ProxyAuthUser() {
        return m_ProxyAuthUser;
    }

    public void setM_ProxyAuthUser(String m_ProxyAuthUser) {
        this.m_ProxyAuthUser = m_ProxyAuthUser;
    }

    public String getM_ProxyAuthPassword() {
        return m_ProxyAuthPassword;
    }

    public void setM_ProxyAuthPassword(String m_ProxyAuthPassword) {
        this.m_ProxyAuthPassword = m_ProxyAuthPassword;
    }

    public Proxy.Type getM_ProxyType() {
        return m_ProxyType;
    }

    public void setM_ProxyType(Proxy.Type m_ProxyType) {
        this.m_ProxyType = m_ProxyType;
    }

    public String getM_ProxyParamsHost() {
        return m_ProxyParamsHost;
    }

    public void setM_ProxyParamsHost(String m_ProxyParamsHost) {
        this.m_ProxyParamsHost = m_ProxyParamsHost;
    }

    public Integer getM_ProxyParamsPort() {
        return m_ProxyParamsPort;
    }

    public void setM_ProxyParamsPort(Integer m_ProxyParamsPort) {
        this.m_ProxyParamsPort = m_ProxyParamsPort;
    }

    public Date getBeeStartLife() {
        return m_beeStartLife;
    }

    Bee bee;

    public Bee getBee() {
        return bee;
    }

    public void setBee(Bee bee) {
        this.bee = bee;
    }

    public Socket getTargetSocket() throws IOException {
        Proxy proxy = Proxy.NO_PROXY;
        if (getM_ProxyType() != Proxy.Type.DIRECT)
            proxy = new Proxy(getM_ProxyType(), new InetSocketAddress(
                    getM_ProxyParamsHost(), getM_ProxyParamsPort())
            );
        Socket clis = new Socket(proxy);
        clis.connect(new InetSocketAddress(getM_TargetUrl(), getM_TargetPort()));
        return clis;
    }
}

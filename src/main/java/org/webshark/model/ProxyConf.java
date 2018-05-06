package org.webshark.model;

public class ProxyConf {
    private String proxyAddr;
    private int proxyPort;
    private String targetAddr;
    private int targetPort;

    public String getProxyAddr() {
        return proxyAddr;
    }

    public void setProxyAddr(String proxyAddr) {
        this.proxyAddr = proxyAddr;
    }

    public int getProxyPort() {
        return proxyPort;
    }

    public void setProxyPort(int proxyPort) {
        this.proxyPort = proxyPort;
    }

    public String getTargetAddr() {
        return targetAddr;
    }

    public void setTargetAddr(String targetAddr) {
        this.targetAddr = targetAddr;
    }

    public int getTargetPort() {
        return targetPort;
    }

    public void setTargetPort(int targetPort) {
        this.targetPort = targetPort;
    }

    @Override
    public String toString() {
        return "ProxyConf{" +
            "proxyAddr='" + proxyAddr + '\'' +
            ", proxyPort=" + proxyPort +
            ", targetAddr='" + targetAddr + '\'' +
            ", targetPort=" + targetPort +
            '}';
    }
}

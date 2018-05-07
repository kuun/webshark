package org.webshark.model;

public class ProxyConf {
    private String proxyAddr;
    private String targetAddr;

    public String getProxyAddr() {
        return proxyAddr;
    }

    public void setProxyAddr(String proxyAddr) {
        this.proxyAddr = proxyAddr;
    }

    public String getTargetAddr() {
        return targetAddr;
    }

    public void setTargetAddr(String targetAddr) {
        this.targetAddr = targetAddr;
    }

    @Override
    public String toString() {
        return "ProxyConf{" +
            "proxyAddr='" + proxyAddr + '\'' +
            ", targetAddr='" + targetAddr + '\'' +
            '}';
    }
}

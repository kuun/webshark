package org.webshark.model;

import java.util.Objects;

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
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ProxyConf proxyConf = (ProxyConf) o;
        return Objects.equals(proxyAddr, proxyConf.proxyAddr) &&
            Objects.equals(targetAddr, proxyConf.targetAddr);
    }

    @Override
    public int hashCode() {
        return Objects.hash(proxyAddr, targetAddr);
    }

    @Override
    public String toString() {
        return "ProxyConf{" +
            "proxyAddr='" + proxyAddr + '\'' +
            ", targetAddr='" + targetAddr + '\'' +
            '}';
    }
}

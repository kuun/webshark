package org.webshark.service.proxy;

import com.google.inject.ImplementedBy;
import org.webshark.model.ProxyConf;

import java.net.MalformedURLException;

@ImplementedBy(ProxyServiceImpl.class)
public interface IProxyService {
    void startProxy(ProxyConf proxyConf) throws MalformedURLException;
    void stopPorxy(ProxyConf proxyConf);
}

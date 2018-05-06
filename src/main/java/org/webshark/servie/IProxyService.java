package org.webshark.servie;

import org.webshark.model.ProxyConf;

public interface IProxyService extends IService {
    void startProxy(ProxyConf proxyConf);
}

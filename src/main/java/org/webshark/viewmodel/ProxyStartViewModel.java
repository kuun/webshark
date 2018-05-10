package org.webshark.viewmodel;

import com.google.inject.Inject;
import de.saxsys.mvvmfx.ViewModel;
import org.webshark.model.ProxyConf;
import org.webshark.servie.proxy.IProxyService;

import java.net.MalformedURLException;

public class ProxyStartViewModel implements ViewModel {
    private ProxyConf conf = new ProxyConf();
    @Inject
    private IProxyService proxyService;

    public ProxyStartViewModel() {
    }

    public ProxyConf getConf() {
        return conf;
    }

    public void startProxy() throws MalformedURLException {
        // todo: valid proxy conf
        proxyService.startProxy(conf);
    }
}

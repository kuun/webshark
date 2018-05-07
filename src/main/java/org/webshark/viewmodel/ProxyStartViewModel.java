package org.webshark.viewmodel;

import com.google.inject.Inject;
import de.saxsys.mvvmfx.ViewModel;
import de.saxsys.mvvmfx.utils.mapping.ModelWrapper;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import org.webshark.model.ProxyConf;
import org.webshark.servie.IProxyService;

public class ProxyStartViewModel implements ViewModel {
    private ModelWrapper<ProxyConf> wrapper = new ModelWrapper<>();
    @Inject
    private IProxyService proxyService;

    public ProxyStartViewModel() {
        wrapper.set(new ProxyConf());
    }

    public StringProperty proxyAddrProperty() {
        return wrapper.field("proxyAddr", ProxyConf::getProxyAddr, ProxyConf::setProxyAddr, "localhost");
    }

    public StringProperty targetAddrProperty() {
        return wrapper.field("targetAddr", ProxyConf::getTargetAddr, ProxyConf::setTargetAddr, "");
    }

    public void startProxy() {
        wrapper.commit();
        // todo: valid proxy conf
        // handle exception
        proxyService.startProxy(wrapper.get());
    }
}

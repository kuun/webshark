package org.webshark.service.record;

import com.google.inject.ImplementedBy;
import io.netty.handler.codec.http.HttpContent;
import io.netty.handler.codec.http.HttpRequest;
import io.netty.handler.codec.http.HttpResponse;
import javafx.collections.ObservableList;
import org.webshark.model.HttpRecord;
import org.webshark.model.ProxyConf;

@ImplementedBy(RecordServiceImpl.class)
public interface IRecordService {
    int recordRequest(ProxyConf proxyConf, HttpRequest req);

    void recordRequestContent(int recordId, HttpContent content);

    void recordResponse(int recordId, HttpResponse res);

    void recordResponseContent(int recordId, HttpContent content);

    ObservableList<HttpRecord> getRecords();
}

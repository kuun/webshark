package org.webshark.servie.record;

import com.google.inject.ImplementedBy;
import io.netty.handler.codec.http.HttpContent;
import io.netty.handler.codec.http.HttpRequest;
import io.netty.handler.codec.http.HttpResponse;
import org.webshark.model.ProxyConf;

import java.util.List;

@ImplementedBy(RecordServiceImpl.class)
public interface IRecordService {
    int recordRequest(ProxyConf proxyConf, HttpRequest req);

    void recordRequestContent(int recordId, HttpContent content);

    void recordResponse(int recordId, HttpResponse res);

    void recordResponseContent(int recordId, HttpContent content);

    List<HttpRecord> getRecords();
}

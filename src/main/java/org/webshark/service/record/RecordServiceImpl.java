package org.webshark.service.record;

import com.google.inject.Singleton;
import io.netty.handler.codec.http.HttpContent;
import io.netty.handler.codec.http.HttpRequest;
import io.netty.handler.codec.http.HttpResponse;
import io.netty.handler.codec.http.LastHttpContent;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.webshark.model.HttpRecord;
import org.webshark.model.ProxyConf;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

@Singleton
class RecordServiceImpl implements IRecordService {
    private static final Logger log = LoggerFactory.getLogger(RecordServiceImpl.class);
    private ObservableList<HttpRecord> records = FXCollections.observableArrayList();
    private Map<Integer, HttpRecord> incompleteRecordMap = new HashMap<>();
    private AtomicInteger nextId = new AtomicInteger(1);

    @Override
    public int recordRequest(ProxyConf proxyConf, HttpRequest req) {
        var id = nextId.getAndAdd(1);
        var record = new HttpRecord();
        record.setId(id);
        record.setReq(req);
        record.setProxyConf(proxyConf);
        records.add(record);
        incompleteRecordMap.put(id, record);
        log.debug("create record: {}", id);
        return id;
    }

    @Override
    public void recordRequestContent(int recordId, HttpContent content) {
        var record = incompleteRecordMap.get(recordId);
        if (record == null) {
            log.error("can't find incompleted record, id: {}", recordId);
            return;
        }
        record.addReqContent(content);
    }

    @Override
    public void recordResponse(int recordId, HttpResponse res) {
        var record = incompleteRecordMap.get(recordId);
        if (record == null) {
            log.error("can't find incompleted record, id: {}", recordId);
            return;
        }
        record.setRes(res);
    }

    @Override
    public void recordResponseContent(int recordId, HttpContent content) {
        var record = incompleteRecordMap.get(recordId);
        if (record == null) {
            log.error("can't find incompleted record, id: {}", recordId);
            return;
        }
        record.addResContent(content);
        if (content == LastHttpContent.EMPTY_LAST_CONTENT) {
            log.debug("record is completed, record: {}", recordId);
            record.setCompleted(true);
            incompleteRecordMap.remove(recordId);
        }
    }

    @Override
    public ObservableList<HttpRecord> getRecords() {
        return records;
    }
}

package org.webshark.service.record;

import com.google.inject.Singleton;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.handler.codec.http.*;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.webshark.model.HttpRecord;
import org.webshark.model.ProxyConf;
import org.webshark.model.Request;
import org.webshark.model.Response;

import java.util.Date;
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
        var reqInfo = new Request(req);
        record.setReq(reqInfo);
        record.setProxyConf(proxyConf);
        record.setBeginTimestamp(new Date().getTime());
        incompleteRecordMap.put(id, record);
        Platform.runLater(() -> {
            records.add(record);
        });
        log.debug("create record: {}", id);
        return id;
    }

    @Override
    public void recordRequestContent(int recordId, HttpContent content) {
        final var record = incompleteRecordMap.get(recordId);
        if (record == null) {
            log.error("can't find incompleted record, id: {}", recordId);
            return;
        }
        var buf = getContentBuffer(content);
        if (buf != null) {
            Platform.runLater(() -> record.addReqContentBuffer(buf));
        }
    }

    @Override
    public void recordResponse(int recordId, HttpResponse res) {
        var record = incompleteRecordMap.get(recordId);
        if (record == null) {
            log.error("can't find incompleted record, id: {}", recordId);
            return;
        }
        var resInfo = new Response(res);
        Platform.runLater(() -> record.setRes(resInfo));
    }

    @Override
    public void recordResponseContent(int recordId, HttpContent content) {
        var record = incompleteRecordMap.get(recordId);
        if (record == null) {
            log.error("can't find incompleted record, id: {}", recordId);
            return;
        }
        var buf = getContentBuffer(content);
        if (buf != null) {
            Platform.runLater(() -> record.addResContentBuffer(buf));
        }
        if (content instanceof LastHttpContent) {
            log.debug("record is completed, record: {}", recordId);
            record.setEndTimestamp(new Date().getTime());
            record.setCompleted(true);
            incompleteRecordMap.remove(recordId);
        }
    }

    @Override
    public ObservableList<HttpRecord> getRecords() {
        return records;
    }

    private ByteBuf getContentBuffer(HttpContent content) {
        ByteBuf srcBuf = null;
        if (content instanceof DefaultHttpContent) {
            var tmp = (DefaultHttpContent)content;
            srcBuf = tmp.content();
        } else if (content instanceof LastHttpContent) {
            var tmp = (LastHttpContent)content;
            if (tmp != LastHttpContent.EMPTY_LAST_CONTENT) {
                srcBuf = tmp.content();
            }
        }
        if (srcBuf != null) {
            return Unpooled.copiedBuffer(srcBuf);
        } else {
            return null;
        }
    }
}

package org.webshark.viewmodel;

import de.saxsys.mvvmfx.InjectScope;
import de.saxsys.mvvmfx.ViewModel;
import io.netty.buffer.ByteBuf;
import javafx.beans.property.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import org.webshark.model.Content;
import org.webshark.model.HttpRecord;
import org.webshark.scope.RecordPageScope;

public class BodyViewModel implements ViewModel {
    @InjectScope
    private RecordPageScope scope;
    private ListProperty<ByteBuf> contentBufs = new SimpleListProperty<>(FXCollections.observableArrayList());
    private HttpRecord record;
    private boolean isRequest = true;
    private StringProperty contentType = new SimpleStringProperty();

    public void initialize() {
        scope.subscribe(RecordPageScope.Notification.FOCUSED_RECORD_CHANGED.name(), (key, payload) -> {
            var newRecord = (HttpRecord)payload[1];
            record = newRecord;
            Content content;
            if (isRequest) {
                content = record.getReq().getContent();
                contentType.setValue(record.getReq().getContentType());
            } else {
                content = record.getRes().getContent();
                contentType.setValue(record.getRes().getContentType());
            }
            if (content != null) {
                contentBufs.bind(content.bufsProperty());
            }
        });
    }

    public ObservableList<ByteBuf> getContentBufs() {
        return contentBufs.get();
    }

    public ListProperty<ByteBuf> contentBufsProperty() {
        return contentBufs;
    }

    public void setContentBufs(ObservableList<ByteBuf> contentBufs) {
        this.contentBufs.set(contentBufs);
    }

    public HttpRecord getRecord() {
        return record;
    }

    public BodyViewModel setRequest(boolean request) {
        isRequest = request;
        return this;
    }
}

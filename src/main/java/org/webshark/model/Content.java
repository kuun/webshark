package org.webshark.model;

import io.netty.buffer.ByteBuf;
import javafx.beans.property.ListProperty;
import javafx.beans.property.LongProperty;
import javafx.beans.property.SimpleListProperty;
import javafx.beans.property.SimpleLongProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class Content {
    private ListProperty<ByteBuf> bufs = new SimpleListProperty<>(FXCollections.observableArrayList());
    private LongProperty length = new SimpleLongProperty();

    public ObservableList<ByteBuf> getBufs() {
        return bufs.get();
    }

    public ListProperty<ByteBuf> bufsProperty() {
        return bufs;
    }

    public void setBufs(ObservableList<ByteBuf> bufs) {
        this.bufs.set(bufs);
    }

    public void addBuffer(ByteBuf buf) {
        bufs.add(buf);
        length.add(buf.readableBytes());
    }

    public long getLength() {
        return length.get();
    }

    public LongProperty lengthProperty() {
        return length;
    }

    public void setLength(long length) {
        this.length.set(length);
    }
}

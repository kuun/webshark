package org.webshark.control;

import io.netty.buffer.ByteBuf;
import javafx.beans.property.ListProperty;
import javafx.beans.property.SimpleListProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.TextArea;
import javafx.scene.text.Font;

public class HexTextArea extends TextArea {
    private ListProperty<ByteBuf> contentBufs = new SimpleListProperty<>(FXCollections.observableArrayList());

    public HexTextArea() {
        setWrapText(false);
        setFont(Font.font("Monospaced"));

        contentBufs.addListener((observable, oldValue, newValue) -> {
            var text = buildHexText(newValue);
            setText(text);
        });
    }

    public void setContentBufs(ObservableList<ByteBuf> contentBufs) {
        this.contentBufs.set(contentBufs);
    }

    private String buildHexText(ObservableList<ByteBuf> bufs) {
        StringBuilder sb = new StringBuilder();
        int offset = 0;
        for (ByteBuf buf : bufs) {
            offset = convertBufToHex(buf, sb, offset);
        }
        return sb.toString();
    }

    private int convertBufToHex(ByteBuf buf, StringBuilder sb, int offset) {
        buf.markReaderIndex();
        try {
        while (buf.readableBytes() > 0) {
            byte tmp = buf.readByte();
            // convert byte to hex char
            int c = 0xff & tmp;
            int high = c >>> 4;
            try {
                sb.append(dec2Hex(high));
            } catch (Exception e) {
                System.out.println("c: " + c);
            }
            int low = c & 0x0f;
            sb.append(dec2Hex(low));
            offset++;
            if (offset % 16 == 0) {
                sb.append('\n');
            } else if (offset % 8 == 0) {
                sb.append("  ");
            } else {
                sb.append(' ');
            }
        }
        } finally {
            buf.resetReaderIndex();
        }
        return offset;
    }

    private char dec2Hex(int i) {
        if (i < 10) {
            return (char) (i + 48);
        } else if (i < 16) {
            return (char) (i + 55);
        } else {
            throw new RuntimeException("too big int: " + i);
        }
    }

}

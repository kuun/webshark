package org.webshark.view;

import de.saxsys.mvvmfx.FxmlView;
import de.saxsys.mvvmfx.InjectViewModel;
import io.netty.buffer.ByteBuf;
import io.netty.util.internal.ThrowableUtil;
import javafx.beans.property.ListProperty;
import javafx.beans.property.SimpleListProperty;
import javafx.collections.ListChangeListener;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextArea;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.StackPane;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.webshark.viewmodel.BodyViewModel;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.ResourceBundle;

public class BodyView implements FxmlView<BodyViewModel>, Initializable {
    private static final Logger log = LoggerFactory.getLogger(BodyView.class);
    @FXML
    private RadioButton btnPreviewMode;
    @FXML
    private RadioButton btnTextMode;
    private ToggleGroup toggleGroup = new ToggleGroup();
    @FXML
    private StackPane stackPane;
    private boolean isRequest = true;
    @InjectViewModel
    private BodyViewModel viewModel;
    private ByteArrayOutputStream outputStream = null;
    private TextArea textArea;


    private static final String DEFAULT_CHARSET = "UTF-8";

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        btnPreviewMode.setToggleGroup(toggleGroup);
        ;
        btnTextMode.setToggleGroup(toggleGroup);
        btnTextMode.setSelected(true);

        var contentBufs = viewModel.contentBufsProperty();
        contentBufs.addListener((observable, oldValue, newValue) -> {
            var selected = toggleGroup.getSelectedToggle();
            if (newValue != oldValue) {
                if (outputStream != null) {
                    try {
                        outputStream.close();
                    } catch (IOException e) {
                        log.error("error: {}", ThrowableUtil.stackTraceToString(e));
                    }
                }
                outputStream = new ByteArrayOutputStream();
                if (selected == btnTextMode) {
                    stackPane.getChildren().clear();
                    stackPane.getChildren().add(textArea);
                }
            }
        });
        contentBufs.addListener(new ListChangeListener<ByteBuf>() {
            @Override
            public void onChanged(Change<? extends ByteBuf> c) {
                while (c.next()) {
                    if (c.wasAdded()) {
                        for (var buf : c.getAddedSubList()) {
                            buf.markReaderIndex();
                            try {
                                buf.readBytes(outputStream, buf.readableBytes());
                            } catch (IOException e) {
                                log.error("error: {}", ThrowableUtil.stackTraceToString(e));
                            } finally {
                                buf.resetReaderIndex();
                            }
                        }
                    }
                }
                var charset = getCharset();
                textArea.clear();
                try {
                    var text = outputStream.toString(charset);
                    textArea.setText(text);
                } catch (UnsupportedEncodingException e) {
                    log.error("error: {}", ThrowableUtil.stackTraceToString(e));
                }

            }
        });

        toggleGroup.selectedToggleProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue == btnPreviewMode) {
                log.debug("preview mode");
            } else if (newValue == btnTextMode) {
                stackPane.getChildren().clear();
                ;
                stackPane.getChildren().add(textArea);
            }
        });

        textArea = new TextArea();
        textArea.setEditable(false);
        textArea.setWrapText(true);
    }

    public BodyView setRequest(boolean request) {
        isRequest = request;
        viewModel.setRequest(request);
        return this;
    }

    private String getCharset() {
        var record = viewModel.getRecord();
        String contentType = null;
        if (isRequest) {
            var req = record.getReq();
            contentType = req.getContentType();
        } else {
            var res = record.getRes();
            contentType = res.getContentType();
        }
        if (contentType == null) {
            return DEFAULT_CHARSET;
        }
        int startPos = contentType.indexOf("charset");
        if (startPos == -1) {
            return DEFAULT_CHARSET;
        }
        int endPos = contentType.indexOf(";", startPos);
        if (endPos == -1) {
            endPos = contentType.length();
        }
        return contentType.substring(startPos + 8, endPos);
    }

    private String buildText() {
        var bufs = viewModel.contentBufsProperty();
        var outputStream = new ByteArrayOutputStream();
        for (ByteBuf buf : bufs) {
            buf.markReaderIndex();
            try {
                buf.readBytes(outputStream, buf.readableBytes());
            } catch (IOException e) {
                log.error("error: {}", ThrowableUtil.stackTraceToString(e));
            } finally {
                buf.resetReaderIndex();
            }
        }
        var charset = Charset.forName(getCharset());
        var text = outputStream.toString(charset);
        try {
            outputStream.close();
        } catch (IOException e) {
            log.error("error: {}", ThrowableUtil.stackTraceToString(e));
        }
        return text;
    }
}

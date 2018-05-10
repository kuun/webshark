package org.webshark.viewmodel;

import com.google.inject.Inject;
import de.saxsys.mvvmfx.ViewModel;
import javafx.beans.property.Property;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.ObservableList;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.webshark.model.HttpRecord;
import org.webshark.servie.record.IRecordService;

public class RecordViewModel implements ViewModel {
    private static final Logger log = LoggerFactory.getLogger(RecordViewModel.class);
    @Inject
    private IRecordService recordService;
    private Property<HttpRecord> selectedRecord = new SimpleObjectProperty<>();


    public ObservableList<HttpRecord> getRecords() {
        return recordService.getRecords();
    }

    public HttpRecord getSelectedRecord() {
        return selectedRecord.getValue();
    }

    public Property<HttpRecord> selectedRecordProperty() {
        return selectedRecord;
    }

    public void setSelectedRecord(HttpRecord selectedRecord) {
        this.selectedRecord.setValue(selectedRecord);
    }
}

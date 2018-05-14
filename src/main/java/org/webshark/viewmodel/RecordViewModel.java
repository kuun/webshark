package org.webshark.viewmodel;

import com.google.inject.Inject;
import de.saxsys.mvvmfx.InjectScope;
import de.saxsys.mvvmfx.ScopeProvider;
import de.saxsys.mvvmfx.ViewModel;
import javafx.collections.ObservableList;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.webshark.model.HttpRecord;
import org.webshark.scope.RecordPageScope;
import org.webshark.service.record.IRecordService;

@ScopeProvider(scopes = {RecordPageScope.class})
public class RecordViewModel implements ViewModel {
    private static final Logger log = LoggerFactory.getLogger(RecordViewModel.class);
    @InjectScope
    private RecordPageScope scope;
    @Inject
    private IRecordService recordService;


    public ObservableList<HttpRecord> getRecords() {
        return recordService.getRecords();
    }

    public void onFocusRecord(HttpRecord record) {
        scope.publish(RecordPageScope.Notification.FOCUSED_RECORD_CHANGED.name(), record);
    }
}

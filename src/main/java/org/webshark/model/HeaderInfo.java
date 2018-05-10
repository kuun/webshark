package org.webshark.model;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class HeaderInfo {
    private StringProperty fieldName = new SimpleStringProperty();
    private StringProperty fieldValue = new SimpleStringProperty();

    public String getFieldName() {
        return fieldName.get();
    }

    public StringProperty fieldNameProperty() {
        return fieldName;
    }

    public void setFieldName(String fieldName) {
        this.fieldName.set(fieldName);
    }

    public String getFieldValue() {
        return fieldValue.get();
    }

    public StringProperty fieldValueProperty() {
        return fieldValue;
    }

    public void setFieldValue(String fieldValue) {
        this.fieldValue.set(fieldValue);
    }
}

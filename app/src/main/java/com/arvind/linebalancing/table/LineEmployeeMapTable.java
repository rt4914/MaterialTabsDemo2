package com.arvind.linebalancing.table;

import android.arch.persistence.room.Entity;
import android.support.annotation.NonNull;

@Entity(primaryKeys = {"lineId","employeeId"})
public class LineEmployeeMapTable {

    private String id;
    @NonNull
    private String lineId;
    @NonNull
    private String employeeId;
    private long updatedAt;

    public LineEmployeeMapTable() {
    }

    @NonNull
    public String getLineId() {
        return lineId;
    }

    public void setLineId(@NonNull String lineId) {
        this.lineId = lineId;
    }

    @NonNull
    public String getEmployeeId() {
        return employeeId;
    }

    public void setEmployeeId(@NonNull String employeeId) {
        this.employeeId = employeeId;
    }

    @NonNull
    public String getId() {
        return id;
    }

    public void setId(@NonNull String id) {
        this.id = id;
    }

    public long getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(long updatedAt) {
        this.updatedAt = updatedAt;
    }
}

package com.arvind.linebalancing.table;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;
import android.support.annotation.NonNull;

@Entity(primaryKeys = {"employeeId","operationId"})
public class EmployeeOperationMapTable {

    private String id;
    @NonNull
    private String employeeId;
    @NonNull
    private String operationId;
    private long updatedAt;

    public EmployeeOperationMapTable() {
    }

    @NonNull
    public String getEmployeeId() {
        return employeeId;
    }

    public void setEmployeeId(@NonNull String employeeId) {
        this.employeeId = employeeId;
    }

    @NonNull
    public String getOperationId() {
        return operationId;
    }

    public void setOperationId(@NonNull String operationId) {
        this.operationId = operationId;
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

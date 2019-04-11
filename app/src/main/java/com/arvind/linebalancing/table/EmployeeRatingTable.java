package com.arvind.linebalancing.table;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;
import android.support.annotation.NonNull;

@Entity
public class EmployeeRatingTable {

    @PrimaryKey
    @NonNull
    private String id;
    private String employeeId;
    private String operationId;
    private double samValue;
    private long ratingTimestamp;
    private long updatedAt;

    public EmployeeRatingTable() {
    }

    @NonNull
    public String getId() {
        return id;
    }

    public void setId(@NonNull String id) {
        this.id = id;
    }

    public String getEmployeeId() {
        return employeeId;
    }

    public void setEmployeeId(String employeeId) {
        this.employeeId = employeeId;
    }

    public String getOperationId() {
        return operationId;
    }

    public void setOperationId(String operationId) {
        this.operationId = operationId;
    }

    public double getSamValue() {
        return samValue;
    }

    public void setSamValue(double samValue) {
        this.samValue = samValue;
    }

    public long getRatingTimestamp() {
        return ratingTimestamp;
    }

    public void setRatingTimestamp(long ratingTimestamp) {
        this.ratingTimestamp = ratingTimestamp;
    }

    public long getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(long updatedAt) {
        this.updatedAt = updatedAt;
    }
}

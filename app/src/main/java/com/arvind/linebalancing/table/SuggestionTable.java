package com.arvind.linebalancing.table;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;
import android.support.annotation.NonNull;

@Entity
public class SuggestionTable {

    @PrimaryKey
    @NonNull
    private String id;
    private double lineEfficiency;
    private String absentEmployeeId;
    private String assignedEmployeeId;
    private String lineId;
    private String machineId;
    private String operationId;
    private long timestamp;
    private long updatedAt;

    public SuggestionTable() {
    }

    public String getLineId() {
        return lineId;
    }

    public void setLineId(String lineId) {
        this.lineId = lineId;
    }

    public String getMachineId() {
        return machineId;
    }

    public void setMachineId(String machineId) {
        this.machineId = machineId;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    @NonNull
    public String getId() {
        return id;
    }

    public void setId(@NonNull String id) {
        this.id = id;
    }

    public String getOperationId() {
        return operationId;
    }

    public void setOperationId(String operationId) {
        this.operationId = operationId;
    }

    public long getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(long updatedAt) {
        this.updatedAt = updatedAt;
    }

    public double getLineEfficiency() {
        return lineEfficiency;
    }

    public void setLineEfficiency(double lineEfficiency) {
        this.lineEfficiency = lineEfficiency;
    }

    public String getAbsentEmployeeId() {
        return absentEmployeeId;
    }

    public void setAbsentEmployeeId(String absentEmployeeId) {
        this.absentEmployeeId = absentEmployeeId;
    }

    public String getAssignedEmployeeId() {
        return assignedEmployeeId;
    }

    public void setAssignedEmployeeId(String assignedEmployeeId) {
        this.assignedEmployeeId = assignedEmployeeId;
    }
}

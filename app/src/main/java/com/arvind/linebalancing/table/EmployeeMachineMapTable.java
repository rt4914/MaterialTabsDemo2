package com.arvind.linebalancing.table;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;
import android.support.annotation.NonNull;

@Entity(primaryKeys = {"employeeId","machineId"})
public class EmployeeMachineMapTable {

    private String id;
    @NonNull
    private String employeeId;
    @NonNull
    private String machineId;
    private long updatedAt;

    public EmployeeMachineMapTable() {
    }

    @NonNull
    public String getEmployeeId() {
        return employeeId;
    }

    public void setEmployeeId(@NonNull String employeeId) {
        this.employeeId = employeeId;
    }

    @NonNull
    public String getMachineId() {
        return machineId;
    }

    public void setMachineId(@NonNull String machineId) {
        this.machineId = machineId;
    }

    public long getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(long updatedAt) {
        this.updatedAt = updatedAt;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}

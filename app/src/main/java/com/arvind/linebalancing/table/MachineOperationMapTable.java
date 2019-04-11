package com.arvind.linebalancing.table;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;
import android.support.annotation.NonNull;

@Entity(primaryKeys = {"machineId","operationId"})
public class MachineOperationMapTable {

    private String id;
    @NonNull
    private String machineId;
    @NonNull
    private String operationId;
    private long updatedAt;

    public MachineOperationMapTable() {
    }

    @NonNull
    public String getMachineId() {
        return machineId;
    }

    public void setMachineId(@NonNull String machineId) {
        this.machineId = machineId;
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

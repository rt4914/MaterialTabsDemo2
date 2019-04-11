package com.arvind.linebalancing.table;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;
import android.support.annotation.NonNull;

@Entity
public class LineEfficiencyTable {

    private String id;

    @PrimaryKey @NonNull
    private String lineId;
    private double lineEfficiency;
    private long updatedAt;

    public LineEfficiencyTable() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public long getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(long updatedAt) {
        this.updatedAt = updatedAt;
    }

    @NonNull
    public String getLineId() {
        return lineId;
    }

    public void setLineId(@NonNull String lineId) {
        this.lineId = lineId;
    }

    public double getLineEfficiency() {
        return lineEfficiency;
    }

    public void setLineEfficiency(double lineEfficiency) {
        this.lineEfficiency = lineEfficiency;
    }
}

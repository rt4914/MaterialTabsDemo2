package com.arvind.linebalancing.table;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;
import android.support.annotation.NonNull;

@Entity
public class StitchTable {

    @PrimaryKey @NonNull
    private String id;
    private String stitchName;
    private long updatedAt;

    public StitchTable() {
    }

    @NonNull
    public String getId() {
        return id;
    }

    public void setId(@NonNull String id) {
        this.id = id;
    }

    public String getStitchName() {
        return stitchName;
    }

    public void setStitchName(String stitchName) {
        this.stitchName = stitchName;
    }

    public long getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(long updatedAt) {
        this.updatedAt = updatedAt;
    }
}

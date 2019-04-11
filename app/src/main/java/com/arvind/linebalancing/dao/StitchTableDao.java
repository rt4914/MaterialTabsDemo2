package com.arvind.linebalancing.dao;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import com.arvind.linebalancing.table.StitchTable;

import java.util.List;

import static android.arch.persistence.room.OnConflictStrategy.REPLACE;

@Dao
public interface StitchTableDao {

    @Insert(onConflict = REPLACE)
    void insert(StitchTable stitchTable);

    @Delete
    void delete(StitchTable stitchTable);

    @Query("Delete from StitchTable where id = :sStitchId")
    void deleteStitchById(String sStitchId);

    @Update
    void update(StitchTable stitchTable);

    @Query("Delete from StitchTable")
    void deleteAllStitches();

    @Query("Select * from StitchTable order by updatedAt DESC")
    List<StitchTable> getAllStitches();

    @Query("Select * from stitchtable where id = :sStitchId")
    StitchTable getStitchById(String sStitchId);
}

package com.arvind.linebalancing.dao;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import com.arvind.linebalancing.table.LineTable;

import java.util.List;

import static android.arch.persistence.room.OnConflictStrategy.REPLACE;

@Dao
public interface LineTableDao {

    @Insert(onConflict = REPLACE)
    void insert(LineTable lineTable);

    @Delete
    void delete(LineTable lineTable);

    @Update
    void update(LineTable lineTable);

    @Query("Delete from LineTable")
    void deleteAllLines();

    @Query("Select * from LineTable order by updatedAt DESC")
    List<LineTable> getAllLines();

    @Query("Select * from LineTable where id = :sLineId")
    LineTable getLineById(String sLineId);

    @Query("Delete from LineTable where id = :sLineId")
    void deleteLineById(String sLineId);
}

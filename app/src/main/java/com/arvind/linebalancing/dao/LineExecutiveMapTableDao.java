package com.arvind.linebalancing.dao;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import com.arvind.linebalancing.table.LineExecutiveMapTable;

import java.util.List;

import static android.arch.persistence.room.OnConflictStrategy.REPLACE;

@Dao
public interface LineExecutiveMapTableDao {

    @Insert(onConflict = REPLACE)
    void insert(LineExecutiveMapTable lineExecutiveMapTable);

    @Delete
    void delete(LineExecutiveMapTable lineExecutiveMapTable);

    @Update
    void update(LineExecutiveMapTable lineExecutiveMapTable);

    @Query("Delete from LineExecutiveMapTable")
    void deleteAllLineExecutiveTables();

    @Query("Select * from LineExecutiveMapTable where id = :sLineExecutiveMapTableId")
    LineExecutiveMapTable getLineExecutiveTableById(String sLineExecutiveMapTableId);

    @Query("Select * from LineExecutiveMapTable order by updatedAt DESC")
    List<LineExecutiveMapTable> getAllLineExecutiveTables();

    @Query("Select * from LineExecutiveMapTable where lineId = :sLineId order by updatedAt DESC")
    List<LineExecutiveMapTable> getAllLineExecutiveTablesByLineId(String sLineId);

    @Query("Select lineId from LineExecutiveMapTable where employeeId = :sExecutiveId order by updatedAt DESC")
    List<String> getAllLineByExecutiveId(String sExecutiveId);

    @Query("Select employeeId from LineExecutiveMapTable where lineId = :sLineId order by updatedAt DESC")
    List<String> getAllExecutiveByLineId(String sLineId);

    @Query("Delete from LineExecutiveMapTable where id = :sLineExecutiveTableId")
    void deleteLineExecutiveTableById(String sLineExecutiveTableId);

    @Query("Select * from LineExecutiveMapTable where lineId = :sLineId and employeeId = :sExecutiveId")
    LineExecutiveMapTable getLineExecutiveTableByBothId(String sLineId, String sExecutiveId);

}

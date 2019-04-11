package com.arvind.linebalancing.dao;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import com.arvind.linebalancing.table.LineSupervisorMapTable;

import java.util.List;

import static android.arch.persistence.room.OnConflictStrategy.REPLACE;

@Dao
public interface LineSupervisorMapTableDao {

    @Insert(onConflict = REPLACE)
    void insert(LineSupervisorMapTable lineSupervisorMapTable);

    @Delete
    void delete(LineSupervisorMapTable lineSupervisorMapTable);

    @Update
    void update(LineSupervisorMapTable lineSupervisorMapTable);

    @Query("Delete from LineSupervisorMapTable")
    void deleteAllLineSupervisorTables();

    @Query("Select * from LineSupervisorMapTable where id = :sLineSupervisorMapTableId")
    LineSupervisorMapTable getLineSupervisorTableById(String sLineSupervisorMapTableId);

    @Query("Select * from LineSupervisorMapTable order by updatedAt DESC")
    List<LineSupervisorMapTable> getAllLineSupervisorTables();

    @Query("Select * from LineSupervisorMapTable where lineId = :sLineId order by updatedAt DESC")
    List<LineSupervisorMapTable> getAllLineSupervisorTablesByLineId(String sLineId);

    @Query("Select lineId from LineSupervisorMapTable where employeeId = :sSupervisorId order by updatedAt DESC")
    List<String> getAllLineBySupervisorId(String sSupervisorId);

    @Query("Select employeeId from LineSupervisorMapTable where lineId = :sLineId order by updatedAt DESC")
    List<String> getAllSupervisorByLineId(String sLineId);

    @Query("Delete from LineSupervisorMapTable where id = :sLineSupervisorTableId ")
    void deleteLineSupervisorTableById(String sLineSupervisorTableId);

    @Query("Select * from LineSupervisorMapTable where lineId = :sLineId and employeeId = :sSupervisorId")
    LineSupervisorMapTable getLineSupervisorTableByBothId(String sLineId, String sSupervisorId);

}

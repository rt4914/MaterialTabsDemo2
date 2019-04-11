package com.arvind.linebalancing.dao;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import com.arvind.linebalancing.table.LineEmployeeMapTable;

import java.util.List;

import static android.arch.persistence.room.OnConflictStrategy.REPLACE;

@Dao
public interface LineEmployeeMapTableDao {

    @Insert(onConflict = REPLACE)
    void insert(LineEmployeeMapTable lineEmployeeMapTable);

    @Delete
    void delete(LineEmployeeMapTable lineEmployeeMapTable);

    @Update
    void update(LineEmployeeMapTable lineEmployeeMapTable);

    @Query("Delete from LineEmployeeMapTable")
    void deleteAllLineEmployeeTables();

    @Query("Select * from LineEmployeeMapTable where id = :sLineEmployeeMapTableId")
    LineEmployeeMapTable getLineEmployeeTableById(String sLineEmployeeMapTableId);

    @Query("Select * from LineEmployeeMapTable order by updatedAt DESC")
    List<LineEmployeeMapTable> getAllLineEmployeeTables();

    @Query("Select * from LineEmployeeMapTable where lineId = :sLineId order by updatedAt DESC")
    List<LineEmployeeMapTable> getAllLineEmployeeTablesByLineId(String sLineId);

    @Query("Select lineId from LineEmployeeMapTable where employeeId = :sEmployeeId order by updatedAt DESC")
    List<String> getAllLineByEmployeeId(String sEmployeeId);

    @Query("Select employeeId from LineEmployeeMapTable where lineId = :sLineId order by updatedAt DESC")
    List<String> getAllEmployeeByLineId(String sLineId);

    @Query("Delete from LineEmployeeMapTable where id = :sLineEmployeeTableId")
    void deleteLineEmployeeTableById(String sLineEmployeeTableId);

    @Query("Select * from LineEmployeeMapTable where lineId = :sLineId and employeeId = :sEmployeeId")
    LineEmployeeMapTable getLineEmployeeTableByBothId(String sLineId, String sEmployeeId);

}

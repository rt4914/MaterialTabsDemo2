package com.arvind.linebalancing.dao;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import com.arvind.linebalancing.table.EmployeeOperationMapTable;

import java.util.List;

import static android.arch.persistence.room.OnConflictStrategy.REPLACE;

@Dao
public interface EmployeeOperationMapTableDao {

    @Insert(onConflict = REPLACE)
    void insert(EmployeeOperationMapTable employeeOperationMapTable);

    @Delete
    void delete(EmployeeOperationMapTable employeeOperationMapTable);

    @Update
    void update(EmployeeOperationMapTable employeeOperationMapTable);

    @Query("Delete from EmployeeOperationMapTable")
    void deleteAllEmployeeOperationTables();

    @Query("Select * from EmployeeOperationMapTable where id = :sEmployeeOperationMapTableId")
    EmployeeOperationMapTable getEmployeeOperationTableById(String sEmployeeOperationMapTableId);

    @Query("Select * from EmployeeOperationMapTable order by updatedAt DESC")
    List<EmployeeOperationMapTable> getAllEmployeeOperationTables();

    @Query("Select * from EmployeeOperationMapTable where employeeId = :sEmployeeId order by updatedAt DESC")
    List<EmployeeOperationMapTable> getAllEmployeeOperationTablesByEmployeeId(String sEmployeeId);

    @Query("Select employeeId from EmployeeOperationMapTable where operationId = :sOperationId order by updatedAt DESC")
    List<String> getAllEmployeeByOperationId(String sOperationId);

    @Query("Select operationId from EmployeeOperationMapTable where employeeId = :sEmployeeId order by updatedAt DESC")
    List<String> getAllOperationByEmployeeId(String sEmployeeId);

    @Query("Delete from EmployeeOperationMapTable where id = :sEmployeeOperationTableId")
    void deleteEmployeeOperationTableById(String sEmployeeOperationTableId);

    @Query("Select * from EmployeeOperationMapTable where employeeId = :sEmployeeId and operationId = :sOperationId")
    EmployeeOperationMapTable getEmployeeOperationTableByBothId(String sEmployeeId, String sOperationId);

}

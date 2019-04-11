package com.arvind.linebalancing.dao;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import com.arvind.linebalancing.table.EmployeeMachineMapTable;

import java.util.List;

import static android.arch.persistence.room.OnConflictStrategy.REPLACE;

@Dao
public interface EmployeeMachineMapTableDao {

    @Insert(onConflict = REPLACE)
    void insert(EmployeeMachineMapTable employeeMachineMapTable);

    @Delete
    void delete(EmployeeMachineMapTable employeeMachineMapTable);

    @Update
    void update(EmployeeMachineMapTable employeeMachineMapTable);

    @Query("Delete from EmployeeMachineMapTable")
    void deleteAllEmployeeMachineTables();

    @Query("Select * from EmployeeMachineMapTable where id = :sEmployeeMachineMapTableId")
    EmployeeMachineMapTable getEmployeeMachineTableById(String sEmployeeMachineMapTableId);

    @Query("Select * from EmployeeMachineMapTable order by updatedAt DESC")
    List<EmployeeMachineMapTable> getAllEmployeeMachineTables();

    @Query("Select * from EmployeeMachineMapTable where employeeId = :sEmployeeId order by updatedAt DESC")
    List<EmployeeMachineMapTable> getAllEmployeeMachineTablesByEmployeeId(String sEmployeeId);

    @Query("Select employeeId from EmployeeMachineMapTable where machineId = :sMachineId order by updatedAt DESC")
    List<String> getAllEmployeeByMachineId(String sMachineId);

    @Query("Select machineId from EmployeeMachineMapTable where employeeId = :sEmployeeId order by updatedAt DESC")
    List<String> getAllMachineByEmployeeId(String sEmployeeId);

    @Query("Delete from EmployeeMachineMapTable where id = :sEmployeeMachineTableId")
    void deleteEmployeeMachineTableById(String sEmployeeMachineTableId);

    @Query("Select * from EmployeeMachineMapTable where employeeId = :sEmployeeId and machineId = :sMachineId")
    EmployeeMachineMapTable getEmployeeMachineTableByBothId(String sEmployeeId, String sMachineId);

}

package com.arvind.linebalancing.dao;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import com.arvind.linebalancing.table.EmployeeTable;

import java.util.List;

import static android.arch.persistence.room.OnConflictStrategy.REPLACE;

@Dao
public interface EmployeeTableDao {

    @Query("Select * from employeetable where id = :sId")
    EmployeeTable getEmployeeById(String sId);

    @Insert(onConflict = REPLACE)
    void insert(EmployeeTable employeeTable);

    @Delete
    void delete(EmployeeTable employeeTable);

    @Update
    void update(EmployeeTable employeeTable);

    @Query("Delete from EmployeeTable")
    void deleteAllEmployees();

    @Query("Select * from EmployeeTable order by updatedAt DESC")
    List<EmployeeTable> getAllEmployees();

    @Query("Delete from EmployeeTable where id = :sEmployeeId")
    void deleteEmployeeById(String sEmployeeId);
}

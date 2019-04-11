package com.arvind.linebalancing.dao;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import com.arvind.linebalancing.table.EmployeeRatingTable;

import java.util.List;

import static android.arch.persistence.room.OnConflictStrategy.REPLACE;

@Dao
public interface EmployeeRatingTableDao {

    @Insert(onConflict = REPLACE)
    void insert(EmployeeRatingTable employeeRatingTable);

    @Delete
    void delete(EmployeeRatingTable employeeRatingTable);

    @Update
    void update(EmployeeRatingTable employeeRatingTable);

    @Query("Delete from EmployeeRatingTable")
    void deleteAllEmployeeRatings();

    @Query("Select * from EmployeeRatingTable where id = :sEmployeeRatingId")
    EmployeeRatingTable getEmployeeRatingById(String sEmployeeRatingId);

    @Query("Select * from EmployeeRatingTable order by updatedAt DESC")
    List<EmployeeRatingTable> getAllEmployeeRatings();

    @Query("Select * from EmployeeRatingTable where employeeId = :sEmployeeId order by updatedAt DESC")
    List<EmployeeRatingTable> getAllEmployeeRatingsByEmployeeId(String sEmployeeId);

    @Query("Delete from EmployeeRatingTable where id = :sEmployeeRatingId")
    void deleteEmployeeRatingById(String sEmployeeRatingId);
}

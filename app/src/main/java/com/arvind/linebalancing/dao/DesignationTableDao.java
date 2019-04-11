package com.arvind.linebalancing.dao;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import com.arvind.linebalancing.table.DesignationTable;
import com.arvind.linebalancing.table.EmployeeRatingTable;

import java.util.List;

import static android.arch.persistence.room.OnConflictStrategy.REPLACE;

@Dao
public interface DesignationTableDao {

    @Insert(onConflict = REPLACE)
    void insert(DesignationTable designationTable);

     @Delete
    void delete(DesignationTable designationTable);

     @Update
    void update(DesignationTable designationTable);

    @Query("Delete from DesignationTable")
    void deleteAllDesignations();

    @Query("Select * from DesignationTable where id = :sAdminDesignationId")
    DesignationTable getDesignationById(String sAdminDesignationId);

    @Query("Select * from DesignationTable order by updatedAt DESC")
    List<DesignationTable> getAllDesignations();

    @Query("Delete from DesignationTable where id = :sDesignationId")
    void deleteDesignationById(String sDesignationId);
}

package com.arvind.linebalancing.dao;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import com.arvind.linebalancing.table.OperationTable;

import java.util.List;

import static android.arch.persistence.room.OnConflictStrategy.REPLACE;

@Dao
public interface OperationTableDao {

    @Insert(onConflict = REPLACE)
    void insert(OperationTable operationTable);

    @Delete
    void delete(OperationTable operationTable);

    @Update
    void update(OperationTable operationTable);

    @Query("Delete from OperationTable")
    void deleteAllOperations();

    @Query("Select * from OperationTable order by updatedAt DESC")
    List<OperationTable> getAllOperations();

    @Query("Select * from OperationTable where id = :sOperationId")
    OperationTable getOperationById(String sOperationId);

    @Query("Select operationName from OperationTable where id = :sOperationId")
    String getOperationNameById(String sOperationId);

    @Query("Delete from OperationTable where id = :sOperationId")
    void deleteOperationById(String sOperationId);
}

package com.arvind.linebalancing.dao;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import com.arvind.linebalancing.table.ConnectionTable;

import java.util.List;

import static android.arch.persistence.room.OnConflictStrategy.REPLACE;

@Dao
public interface ConnectionTableDao {

    @Insert(onConflict = REPLACE)
    void insert(ConnectionTable connectionTable);

    @Delete
    void delete(ConnectionTable connectionTable);

    @Update
    void update(ConnectionTable connectionTable);

    @Query("Delete from ConnectionTable")
    void deleteAllConnections();

    @Query("Select * from ConnectionTable where id = :sConnectionId")
    ConnectionTable getConnectionById(String sConnectionId);

    @Query("Select * from ConnectionTable order by updatedAt DESC")
    List<ConnectionTable> getAllConnections();

    @Query("Select * from ConnectionTable where employeeId = :sEmployeeId order by updatedAt DESC")
    List<ConnectionTable> getAllConnectionsByEmployeeId(String sEmployeeId);

    @Query("Delete from ConnectionTable where id = :sConnectionId")
    void deleteConnectionById(String sConnectionId);
}

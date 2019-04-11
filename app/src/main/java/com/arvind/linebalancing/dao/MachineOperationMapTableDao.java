package com.arvind.linebalancing.dao;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import com.arvind.linebalancing.table.MachineOperationMapTable;

import java.util.List;

import static android.arch.persistence.room.OnConflictStrategy.REPLACE;

@Dao
public interface MachineOperationMapTableDao {

    @Insert(onConflict = REPLACE)
    void insert(MachineOperationMapTable employeeOperationMapTable);

    @Delete
    void delete(MachineOperationMapTable employeeOperationMapTable);

    @Update
    void update(MachineOperationMapTable employeeOperationMapTable);

    @Query("Delete from MachineOperationMapTable")
    void deleteAllMachineOperationTables();

    @Query("Select * from MachineOperationMapTable where id = :sMachineOperationMapTableId")
    MachineOperationMapTable getMachineOperationTableById(String sMachineOperationMapTableId);

    @Query("Select * from MachineOperationMapTable where machineId = :sMachineId AND operationId = :sOperationId")
    MachineOperationMapTable getMachineOperationTableByBothId(String sMachineId, String sOperationId);

    @Query("Select * from MachineOperationMapTable order by updatedAt DESC")
    List<MachineOperationMapTable> getAllMachineOperationTables();

    @Query("Select * from MachineOperationMapTable where machineId = :sMachineId order by updatedAt DESC")
    List<MachineOperationMapTable> getAllMachineOperationTablesByMachineId(String sMachineId);

    @Query("Select machineId from MachineOperationMapTable where operationId = :sOperationId order by updatedAt DESC")
    List<String> getAllMachineByOperationId(String sOperationId);

    @Query("Select operationId from MachineOperationMapTable where machineId = :sMachineId order by updatedAt DESC")
    List<String> getAllOperationByMachineId(String sMachineId);

    @Query("Delete from MachineOperationMapTable where id = :sMachineOperationTableId")
    void deleteMachineOperationTableById(String sMachineOperationTableId);
}

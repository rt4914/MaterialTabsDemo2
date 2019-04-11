package com.arvind.linebalancing.dao;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import com.arvind.linebalancing.table.MachineTable;

import java.util.List;

import static android.arch.persistence.room.OnConflictStrategy.REPLACE;

@Dao
public interface MachineTableDao {

    @Insert(onConflict = REPLACE)
    void insert(MachineTable machineTable);

    @Delete
    void delete(MachineTable machineTable);

    @Update
    void update(MachineTable machineTable);

    @Query("Delete from machineTable")
    void deleteAllMachines();

    @Query("Select * from machinetable order by updatedAt DESC")
    List<MachineTable> getAllMachines();

    @Query("Select * from machinetable where id = :sMachineId")
    MachineTable getMachineById(String sMachineId);

    @Query("Select machineName from machinetable where id = :sMachineId")
    String getMachineNameById(String sMachineId);

    @Query("delete from machinetable where id = :sMachineId")
    void deleteMachineById(String sMachineId);
}

package com.arvind.linebalancing.dao;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import com.arvind.linebalancing.table.LineEfficiencyTable;

import java.util.List;

import static android.arch.persistence.room.OnConflictStrategy.REPLACE;

@Dao
public interface LineEfficiencyTableDao {

    @Insert(onConflict = REPLACE)
    void insert(LineEfficiencyTable lineEfficiencyTable);

    @Delete
    void delete(LineEfficiencyTable lineEfficiencyTable);

    @Update
    void update(LineEfficiencyTable lineEfficiencyTable);

    @Query("Delete from LineEfficiencyTable")
    void deleteAllLineEfficiencies();

    @Query("Select * from LineEfficiencyTable order by updatedAt DESC")
    List<LineEfficiencyTable> getAllLineEfficiencies();

    @Query("Select lineEfficiency from LineEfficiencyTable where lineId = :sLineId")
    double getLineEfficiencyById(String sLineId);

    @Query("Select * from LineEfficiencyTable where lineId = :sLineId")
    LineEfficiencyTable getLineEfficiencyTableById(String sLineId);

    @Query("Select * from LineEfficiencyTable where id = :sId")
    LineEfficiencyTable getLineEfficiencyTableByLineEfficiencyId(String sId);

    @Query("Delete from LineEfficiencyTable where id = :sLineEfficiencyId")
    void deleteLineEfficiencyTableById(String sLineEfficiencyId);
}

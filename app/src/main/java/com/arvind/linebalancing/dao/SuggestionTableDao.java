package com.arvind.linebalancing.dao;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import com.arvind.linebalancing.table.SuggestionTable;

import java.util.List;

import static android.arch.persistence.room.OnConflictStrategy.REPLACE;

@Dao
public interface SuggestionTableDao {

    @Insert(onConflict = REPLACE)
    void insert(SuggestionTable suggestionTable);

    @Delete
    void delete(SuggestionTable suggestionTable);

    @Update
    void update(SuggestionTable suggestionTable);

    @Query("Delete from SuggestionTable")
    void deleteAllSuggestions();

    @Query("Select * from SuggestionTable where id = :sSuggestionId")
    SuggestionTable getSuggestionById(String sSuggestionId);

    @Query("Select * from SuggestionTable order by timestamp DESC")
    List<SuggestionTable> getAllSuggestions();

    @Query("Select * from SuggestionTable where assignedEmployeeId = :sEmployeeId order by updatedAt DESC")
    List<SuggestionTable> getAllSuggestionsByEmployeeId(String sEmployeeId);

    @Query("Delete from SuggestionTable where id = :sSuggestionId")
    void deleteSuggestionById(String sSuggestionId);
}

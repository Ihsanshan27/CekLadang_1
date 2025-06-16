package com.dicoding.cekladang.data.local.room

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.dicoding.cekladang.data.local.entity.History

@Dao
interface HistoryDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(historyEntity: History)

    @Update
    fun update(historyEntity: History)

    @Delete
    fun delete(historyEntity: History)

    @Query("SELECT * FROM history WHERE id = :id LIMIT 1")
    fun getHistoryById(id: String): LiveData<History>

    @Query("SELECT * from History ORDER BY timestamp DESC")
    fun getAllHistoryUser(): LiveData<List<History>>
}
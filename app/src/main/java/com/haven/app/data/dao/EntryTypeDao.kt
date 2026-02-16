package com.haven.app.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.haven.app.data.entity.EntryTypeEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface EntryTypeDao {
    @Query("SELECT * FROM entry_type WHERE is_enabled = 1 ORDER BY sort_order")
    fun getEnabled(): Flow<List<EntryTypeEntity>>

    @Query("SELECT * FROM entry_type WHERE id = :id")
    suspend fun getById(id: Long): EntryTypeEntity?

    @Query("SELECT * FROM entry_type WHERE name = :name")
    suspend fun getByName(name: String): EntryTypeEntity?

    @Insert
    suspend fun insertAll(entryTypes: List<EntryTypeEntity>)
}

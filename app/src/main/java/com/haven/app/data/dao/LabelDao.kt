package com.haven.app.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.haven.app.data.entity.Label
import kotlinx.coroutines.flow.Flow

@Dao
interface LabelDao {
    @Query("SELECT * FROM label WHERE entry_type_id = :entryTypeId AND is_enabled = 1 ORDER BY sort_order")
    fun getByEntryType(entryTypeId: Long): Flow<List<Label>>

    /** Returns only root-level labels (those with no parent) for the given entry type. */
    @Query("SELECT * FROM label WHERE entry_type_id = :entryTypeId AND parent_id IS NULL AND is_enabled = 1 ORDER BY sort_order")
    fun getTopLevel(entryTypeId: Long): Flow<List<Label>>

    @Query("SELECT * FROM label WHERE parent_id = :parentId AND is_enabled = 1 ORDER BY sort_order")
    fun getChildren(parentId: Long): Flow<List<Label>>

    @Insert
    suspend fun insertAll(labels: List<Label>)

    @Insert
    suspend fun insert(label: Label): Long
}

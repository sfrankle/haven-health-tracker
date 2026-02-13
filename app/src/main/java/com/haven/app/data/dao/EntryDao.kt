package com.haven.app.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Transaction
import com.haven.app.data.entity.Entry
import com.haven.app.data.entity.EntryLabel
import com.haven.app.data.model.EntryWithDetails
import kotlinx.coroutines.flow.Flow

@Dao
interface EntryDao {
    @Insert
    suspend fun insert(entry: Entry): Long

    @Insert
    suspend fun insertEntryLabels(entryLabels: List<EntryLabel>)

    @Transaction
    suspend fun insertWithLabels(entry: Entry, labelIds: List<Long>): Long {
        val entryId = insert(entry)
        if (labelIds.isNotEmpty()) {
            insertEntryLabels(labelIds.map { EntryLabel(entryId, it) })
        }
        return entryId
    }

    @Query("""
        SELECT e.id, e.entry_type_id AS entryTypeId, et.name AS entryTypeName, et.icon AS entryTypeIcon,
               e.source_type AS sourceType, e.timestamp, e.created_at AS createdAt,
               e.numeric_value AS numericValue, e.notes,
               GROUP_CONCAT(l.name, ', ') AS labelNames
        FROM entry e
        JOIN entry_type et ON e.entry_type_id = et.id
        LEFT JOIN entry_label el ON e.id = el.entry_id
        LEFT JOIN label l ON el.label_id = l.id
        GROUP BY e.id
        ORDER BY e.timestamp DESC
    """)
    fun getAllWithDetails(): Flow<List<EntryWithDetails>>

    @Query("""
        SELECT e.id, e.entry_type_id AS entryTypeId, et.name AS entryTypeName, et.icon AS entryTypeIcon,
               e.source_type AS sourceType, e.timestamp, e.created_at AS createdAt,
               e.numeric_value AS numericValue, e.notes,
               GROUP_CONCAT(l.name, ', ') AS labelNames
        FROM entry e
        JOIN entry_type et ON e.entry_type_id = et.id
        LEFT JOIN entry_label el ON e.id = el.entry_id
        LEFT JOIN label l ON el.label_id = l.id
        WHERE e.entry_type_id = :entryTypeId
        GROUP BY e.id
        ORDER BY e.timestamp DESC
    """)
    fun getByTypeWithDetails(entryTypeId: Long): Flow<List<EntryWithDetails>>

    @Query("""
        SELECT e.id, e.entry_type_id AS entryTypeId, et.name AS entryTypeName, et.icon AS entryTypeIcon,
               e.source_type AS sourceType, e.timestamp, e.created_at AS createdAt,
               e.numeric_value AS numericValue, e.notes,
               GROUP_CONCAT(l.name, ', ') AS labelNames
        FROM entry e
        JOIN entry_type et ON e.entry_type_id = et.id
        LEFT JOIN entry_label el ON e.id = el.entry_id
        LEFT JOIN label l ON el.label_id = l.id
        WHERE e.timestamp BETWEEN :startDate AND :endDate
        GROUP BY e.id
        ORDER BY e.timestamp DESC
    """)
    fun getByDateRangeWithDetails(startDate: String, endDate: String): Flow<List<EntryWithDetails>>

    @Query("""
        SELECT COALESCE(SUM(e.numeric_value), 0)
        FROM entry e
        WHERE e.entry_type_id = :entryTypeId
        AND e.timestamp BETWEEN :dayStart AND :dayEnd
    """)
    fun getDailyTotal(entryTypeId: Long, dayStart: String, dayEnd: String): Flow<Double>
}

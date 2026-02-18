package com.haven.app.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Transaction
import com.haven.app.data.entity.Entry
import com.haven.app.data.entity.EntryLabel
import com.haven.app.data.model.EntryWithDetails
import com.haven.app.data.model.LabelFrequency
import kotlinx.coroutines.flow.Flow

@Dao
interface EntryDao {
    @Insert
    suspend fun insert(entry: Entry): Long

    @Insert
    suspend fun insertEntryLabels(entryLabels: List<EntryLabel>)

    /**
     * Atomically inserts an entry and its label associations in a single transaction.
     * If [labelIds] is empty, only the entry is inserted (no label rows are written).
     */
    @Transaction
    suspend fun insertWithLabels(entry: Entry, labelIds: List<Long>): Long {
        val entryId = insert(entry)
        if (labelIds.isNotEmpty()) {
            insertEntryLabels(labelIds.map { EntryLabel(entryId, it) })
        }
        return entryId
    }

    @Query("""
        SELECT e.id, e.entry_type_id AS entryTypeId, et.icon AS entryType,
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
        SELECT e.id, e.entry_type_id AS entryTypeId, et.icon AS entryType,
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

    /**
     * One-shot paginated fetch — returns a snapshot, not a live [Flow].
     * Use [getAllWithDetails] if you need reactive updates.
     */
    @Query("""
        SELECT e.id, e.entry_type_id AS entryTypeId, et.icon AS entryType,
               e.source_type AS sourceType, e.timestamp, e.created_at AS createdAt,
               e.numeric_value AS numericValue, e.notes,
               GROUP_CONCAT(l.name, ', ') AS labelNames
        FROM entry e
        JOIN entry_type et ON e.entry_type_id = et.id
        LEFT JOIN entry_label el ON e.id = el.entry_id
        LEFT JOIN label l ON el.label_id = l.id
        GROUP BY e.id
        ORDER BY e.timestamp DESC
        LIMIT :limit OFFSET :offset
    """)
    suspend fun getAllWithDetailsPaged(limit: Int, offset: Int): List<EntryWithDetails>

    /**
     * One-shot paginated fetch filtered by entry type — returns a snapshot, not a live [Flow].
     * Use [getByTypeWithDetails] if you need reactive updates.
     */
    @Query("""
        SELECT e.id, e.entry_type_id AS entryTypeId, et.icon AS entryType,
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
        LIMIT :limit OFFSET :offset
    """)
    suspend fun getByTypeWithDetailsPaged(entryTypeId: Long, limit: Int, offset: Int): List<EntryWithDetails>

    /**
     * Returns all entries whose timestamp falls within [[startDate], [endDate]] inclusive.
     * Both bounds must be ISO 8601 strings (e.g. "2024-01-15 00:00:00") — the query uses
     * SQLite's lexicographic string comparison on the timestamp column.
     */
    @Query("""
        SELECT e.id, e.entry_type_id AS entryTypeId, et.icon AS entryType,
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

    /**
     * Returns the top [limit] labels by usage frequency for entries of [entryTypeId] recorded
     * within the hour window [[hourStart], [hourEnd]).
     *
     * Hour extraction uses `substr(timestamp, 12, 2)` — requires timestamps stored as
     * ISO 8601 strings ("YYYY-MM-DD HH:mm:ss"). [hourStart] and [hourEnd] are 0–23 (24-hour).
     * This DAO method does NOT handle windows that cross midnight; see [EntryRepository] for that.
     */
    @Query("""
        SELECT el.label_id AS labelId, COUNT(*) AS count
        FROM entry e
        JOIN entry_label el ON e.id = el.entry_id
        WHERE e.entry_type_id = :entryTypeId
        AND CAST(substr(e.timestamp, 12, 2) AS INTEGER) >= :hourStart
        AND CAST(substr(e.timestamp, 12, 2) AS INTEGER) < :hourEnd
        GROUP BY el.label_id
        ORDER BY count DESC
        LIMIT :limit
    """)
    suspend fun getLabelFrequencyByTimeWindow(
        entryTypeId: Long,
        hourStart: Int,
        hourEnd: Int,
        limit: Int
    ): List<LabelFrequency>

    /**
     * Sums [Entry.numericValue] for all entries of [entryTypeId] within [[dayStart], [dayEnd]].
     * Returns 0.0 when no entries exist in the range (via COALESCE).
     * Bounds must be ISO 8601 strings (e.g. "2024-01-15 00:00:00" / "2024-01-15 23:59:59").
     */
    @Query("""
        SELECT COALESCE(SUM(e.numeric_value), 0)
        FROM entry e
        WHERE e.entry_type_id = :entryTypeId
        AND e.timestamp BETWEEN :dayStart AND :dayEnd
    """)
    fun getDailyTotal(entryTypeId: Long, dayStart: String, dayEnd: String): Flow<Double>
}

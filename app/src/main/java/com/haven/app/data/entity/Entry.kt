package com.haven.app.data.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "entry",
    foreignKeys = [
        ForeignKey(entity = EntryTypeEntity::class, parentColumns = ["id"], childColumns = ["entry_type_id"])
    ],
    indices = [Index("entry_type_id"), Index("timestamp"), Index("source_type")]
)
data class Entry(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    @ColumnInfo(name = "entry_type_id")
    val entryTypeId: Long,
    @ColumnInfo(name = "source_type", defaultValue = "'log'")
    val sourceType: String = "log",
    /** ISO 8601 string, e.g. "2024-01-15 14:30:00". Used in string-comparison range queries. */
    val timestamp: String,
    /** ISO 8601 string recording when the row was created; distinct from [timestamp] which the user controls. */
    @ColumnInfo(name = "created_at")
    val createdAt: String,
    /** Null for entry types that don't record a numeric value (e.g. food). */
    @ColumnInfo(name = "numeric_value")
    val numericValue: Double? = null,
    val notes: String? = null
)

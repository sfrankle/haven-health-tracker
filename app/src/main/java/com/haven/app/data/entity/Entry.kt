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
    val timestamp: String,
    @ColumnInfo(name = "created_at")
    val createdAt: String,
    @ColumnInfo(name = "numeric_value")
    val numericValue: Double? = null,
    val notes: String? = null
)

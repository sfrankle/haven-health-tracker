package com.haven.app.data.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "entry_type",
    foreignKeys = [
        ForeignKey(entity = MeasurementType::class, parentColumns = ["id"], childColumns = ["measurement_type_id"])
    ],
    indices = [Index(value = ["name"], unique = true), Index("measurement_type_id")]
)
data class EntryTypeEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val name: String,
    @ColumnInfo(name = "measurement_type_id")
    val measurementTypeId: Long,
    val prompt: String? = null,
    val icon: EntryType? = null,
    @ColumnInfo(name = "is_enabled", defaultValue = "1")
    val isEnabled: Boolean = true,
    @ColumnInfo(name = "is_default", defaultValue = "1")
    val isDefault: Boolean = true,
    @ColumnInfo(name = "sort_order", defaultValue = "0")
    val sortOrder: Int = 0
)

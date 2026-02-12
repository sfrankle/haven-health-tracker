package com.haven.app.data.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "anchor_activity",
    foreignKeys = [
        ForeignKey(entity = Label::class, parentColumns = ["id"], childColumns = ["label_id"])
    ],
    indices = [Index("label_id")]
)
data class AnchorActivity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    @ColumnInfo(name = "label_id")
    val labelId: Long,
    val title: String,
    val icon: String? = null,
    @ColumnInfo(name = "default_effort")
    val defaultEffort: Int,
    @ColumnInfo(name = "user_effort")
    val userEffort: Int? = null,
    @ColumnInfo(name = "is_enabled", defaultValue = "1")
    val isEnabled: Boolean = true,
    @ColumnInfo(name = "is_default", defaultValue = "1")
    val isDefault: Boolean = true,
    @ColumnInfo(name = "seed_version", defaultValue = "1")
    val seedVersion: Int = 1
)

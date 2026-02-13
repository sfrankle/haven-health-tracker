package com.haven.app.data.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "tag",
    indices = [Index(value = ["name", "tag_group"], unique = true)]
)
data class Tag(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val name: String,
    @ColumnInfo(name = "tag_group")
    val tagGroup: String,
    @ColumnInfo(name = "seed_version", defaultValue = "1")
    val seedVersion: Int = 1
)

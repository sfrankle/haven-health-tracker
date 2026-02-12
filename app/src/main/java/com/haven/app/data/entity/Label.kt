package com.haven.app.data.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "label",
    foreignKeys = [
        ForeignKey(entity = EntryType::class, parentColumns = ["id"], childColumns = ["entry_type_id"]),
        ForeignKey(entity = Label::class, parentColumns = ["id"], childColumns = ["parent_id"]),
        ForeignKey(entity = Category::class, parentColumns = ["id"], childColumns = ["category_id"])
    ],
    indices = [Index("entry_type_id"), Index("parent_id"), Index("category_id")]
)
data class Label(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    @ColumnInfo(name = "entry_type_id")
    val entryTypeId: Long,
    val name: String,
    @ColumnInfo(name = "parent_id")
    val parentId: Long? = null,
    @ColumnInfo(name = "category_id")
    val categoryId: Long? = null,
    @ColumnInfo(name = "is_default", defaultValue = "1")
    val isDefault: Boolean = true,
    @ColumnInfo(name = "is_enabled", defaultValue = "1")
    val isEnabled: Boolean = true,
    @ColumnInfo(name = "sort_order", defaultValue = "0")
    val sortOrder: Int = 0,
    @ColumnInfo(name = "seed_version", defaultValue = "1")
    val seedVersion: Int = 1
)

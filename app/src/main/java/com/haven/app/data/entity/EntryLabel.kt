package com.haven.app.data.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index

@Entity(
    tableName = "entry_label",
    primaryKeys = ["entry_id", "label_id"],
    foreignKeys = [
        ForeignKey(entity = Entry::class, parentColumns = ["id"], childColumns = ["entry_id"], onDelete = ForeignKey.CASCADE),
        ForeignKey(entity = Label::class, parentColumns = ["id"], childColumns = ["label_id"])
    ],
    indices = [Index("entry_id"), Index("label_id")]
)
data class EntryLabel(
    @ColumnInfo(name = "entry_id")
    val entryId: Long,
    @ColumnInfo(name = "label_id")
    val labelId: Long
)

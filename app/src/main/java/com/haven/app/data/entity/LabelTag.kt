package com.haven.app.data.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index

@Entity(
    tableName = "label_tag",
    primaryKeys = ["label_id", "tag_id"],
    foreignKeys = [
        ForeignKey(entity = Label::class, parentColumns = ["id"], childColumns = ["label_id"]),
        ForeignKey(entity = Tag::class, parentColumns = ["id"], childColumns = ["tag_id"])
    ],
    indices = [Index("label_id"), Index("tag_id")]
)
data class LabelTag(
    @ColumnInfo(name = "label_id")
    val labelId: Long,
    @ColumnInfo(name = "tag_id")
    val tagId: Long
)

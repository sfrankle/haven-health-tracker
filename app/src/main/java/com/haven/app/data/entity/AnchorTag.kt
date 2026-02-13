package com.haven.app.data.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index

@Entity(
    tableName = "anchor_tag",
    primaryKeys = ["anchor_activity_id", "tag_id"],
    foreignKeys = [
        ForeignKey(entity = AnchorActivity::class, parentColumns = ["id"], childColumns = ["anchor_activity_id"]),
        ForeignKey(entity = Tag::class, parentColumns = ["id"], childColumns = ["tag_id"])
    ],
    indices = [Index("anchor_activity_id"), Index("tag_id")]
)
data class AnchorTag(
    @ColumnInfo(name = "anchor_activity_id")
    val anchorActivityId: Long,
    @ColumnInfo(name = "tag_id")
    val tagId: Long
)

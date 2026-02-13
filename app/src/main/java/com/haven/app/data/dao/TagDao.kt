package com.haven.app.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.haven.app.data.entity.LabelTag
import com.haven.app.data.entity.Tag

@Dao
interface TagDao {
    @Query("SELECT * FROM tag WHERE tag_group = :group")
    suspend fun getByGroup(group: String): List<Tag>

    @Insert
    suspend fun insertAll(tags: List<Tag>)

    @Insert
    suspend fun insertLabelTags(labelTags: List<LabelTag>)
}

package com.haven.app.data.dao

import androidx.room.Dao
import androidx.room.Insert
import com.haven.app.data.entity.Category

@Dao
interface CategoryDao {
    @Insert
    suspend fun insertAll(categories: List<Category>)
}

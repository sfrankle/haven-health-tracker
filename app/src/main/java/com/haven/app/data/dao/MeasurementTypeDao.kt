package com.haven.app.data.dao

import androidx.room.Dao
import androidx.room.Insert
import com.haven.app.data.entity.MeasurementType

@Dao
interface MeasurementTypeDao {
    @Insert
    suspend fun insertAll(types: List<MeasurementType>)
}

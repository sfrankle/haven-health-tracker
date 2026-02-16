package com.haven.app.data

import androidx.room.TypeConverter
import com.haven.app.data.entity.EntryType

class Converters {
    @TypeConverter
    fun fromEntryType(value: EntryType?): String? = value?.dbKey

    @TypeConverter
    fun toEntryType(value: String?): EntryType? = value?.let { EntryType.fromDbKey(it) }
}

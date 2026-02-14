package com.haven.app.data

import androidx.room.TypeConverter
import com.haven.app.data.entity.EntryTypeIcon

class Converters {
    @TypeConverter
    fun fromEntryTypeIcon(value: EntryTypeIcon?): String? = value?.key

    @TypeConverter
    fun toEntryTypeIcon(value: String?): EntryTypeIcon? = value?.let { EntryTypeIcon.fromKey(it) }
}

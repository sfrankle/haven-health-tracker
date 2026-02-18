package com.haven.app.data

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.haven.app.data.dao.CategoryDao
import com.haven.app.data.dao.EntryDao
import com.haven.app.data.dao.EntryTypeDao
import com.haven.app.data.dao.LabelDao
import com.haven.app.data.dao.MeasurementTypeDao
import com.haven.app.data.dao.TagDao
import com.haven.app.data.entity.AnchorActivity
import com.haven.app.data.entity.AnchorTag
import com.haven.app.data.entity.Category
import com.haven.app.data.entity.Entry
import com.haven.app.data.entity.EntryLabel
import com.haven.app.data.entity.EntryTypeEntity
import com.haven.app.data.entity.Label
import com.haven.app.data.entity.LabelTag
import com.haven.app.data.entity.MeasurementType
import com.haven.app.data.entity.Tag

@TypeConverters(Converters::class)
@Database(
    entities = [
        MeasurementType::class,
        Category::class,
        EntryTypeEntity::class,
        Label::class,
        Tag::class,
        LabelTag::class,
        Entry::class,
        EntryLabel::class,
        AnchorActivity::class,
        AnchorTag::class,
    ],
    version = 2,
    exportSchema = true
)
abstract class HavenDatabase : RoomDatabase() {
    abstract fun measurementTypeDao(): MeasurementTypeDao
    abstract fun categoryDao(): CategoryDao
    abstract fun entryTypeDao(): EntryTypeDao
    abstract fun labelDao(): LabelDao
    abstract fun tagDao(): TagDao
    abstract fun entryDao(): EntryDao

    companion object {
        const val NAME = "haven_db"
    }
}

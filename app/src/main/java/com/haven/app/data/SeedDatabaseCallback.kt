package com.haven.app.data

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase

class SeedDatabaseCallback(
    private val context: Context
) : RoomDatabase.Callback() {

    override fun onOpen(db: SupportSQLiteDatabase) {
        super.onOpen(db)
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val appliedVersion = prefs.getInt(KEY_SEED_VERSION, 0)
        if (appliedVersion >= SeedData.VERSION) return

        db.beginTransaction()
        try {
            seedMeasurementTypes(db)
            seedCategories(db)
            seedEntryTypes(db)
            seedLabels(db)
            seedTags(db)
            seedLabelTags(db)
            db.setTransactionSuccessful()
        } finally {
            db.endTransaction()
        }
        prefs.edit().putInt(KEY_SEED_VERSION, SeedData.VERSION).apply()
    }

    private fun seedMeasurementTypes(db: SupportSQLiteDatabase) {
        SeedData.measurementTypes.forEach { mt ->
            db.insert("measurement_type", SQLiteDatabase.CONFLICT_IGNORE, ContentValues().apply {
                put("id", mt.id)
                put("name", mt.name)
                put("display_name", mt.displayName)
            })
        }
    }

    private fun seedCategories(db: SupportSQLiteDatabase) {
        SeedData.categories.forEach { cat ->
            db.insert("category", SQLiteDatabase.CONFLICT_IGNORE, ContentValues().apply {
                put("id", cat.id)
                put("name", cat.name)
            })
        }
    }

    private fun seedEntryTypes(db: SupportSQLiteDatabase) {
        SeedData.entryTypes.forEach { et ->
            db.insert("entry_type", SQLiteDatabase.CONFLICT_IGNORE, ContentValues().apply {
                put("id", et.id)
                put("name", et.name)
                put("measurement_type_id", et.measurementTypeId)
                put("prompt", et.prompt)
                put("icon", et.icon?.dbKey)
                put("is_enabled", if (et.isEnabled) 1 else 0)
                put("is_default", if (et.isDefault) 1 else 0)
                put("sort_order", et.sortOrder)
            })
        }
    }

    private fun seedLabels(db: SupportSQLiteDatabase) {
        (SeedData.foodLabels + listOf(SeedData.mealSourceLabel) + SeedData.mealSourceChildren).forEach { label ->
            db.insert("label", SQLiteDatabase.CONFLICT_IGNORE, ContentValues().apply {
                put("id", label.id)
                put("entry_type_id", label.entryTypeId)
                put("name", label.name)
                put("parent_id", label.parentId)
                put("category_id", label.categoryId)
                put("is_default", if (label.isDefault) 1 else 0)
                put("is_enabled", if (label.isEnabled) 1 else 0)
                put("sort_order", label.sortOrder)
                put("seed_version", label.seedVersion)
            })
        }
    }

    private fun seedTags(db: SupportSQLiteDatabase) {
        SeedData.foodTags.forEach { tag ->
            db.insert("tag", SQLiteDatabase.CONFLICT_IGNORE, ContentValues().apply {
                put("id", tag.id)
                put("name", tag.name)
                put("tag_group", tag.tagGroup)
                put("seed_version", tag.seedVersion)
            })
        }
    }

    private fun seedLabelTags(db: SupportSQLiteDatabase) {
        SeedData.foodLabelTags.forEach { lt ->
            db.insert("label_tag", SQLiteDatabase.CONFLICT_IGNORE, ContentValues().apply {
                put("label_id", lt.labelId)
                put("tag_id", lt.tagId)
            })
        }
    }

    companion object {
        private const val PREFS_NAME = "haven_seed"
        private const val KEY_SEED_VERSION = "seed_version"
    }
}

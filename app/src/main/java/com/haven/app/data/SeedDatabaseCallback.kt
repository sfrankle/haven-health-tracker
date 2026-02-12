package com.haven.app.data

import android.util.Log
import androidx.room.RoomDatabase
import androidx.room.withTransaction
import androidx.sqlite.db.SupportSQLiteDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Provider

class SeedDatabaseCallback(
    private val databaseProvider: Provider<HavenDatabase>
) : RoomDatabase.Callback() {

    override fun onCreate(db: SupportSQLiteDatabase) {
        super.onCreate(db)
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val database = databaseProvider.get()
                database.withTransaction {
                    database.measurementTypeDao().insertAll(SeedData.measurementTypes)
                    database.categoryDao().insertAll(SeedData.categories)
                    database.entryTypeDao().insertAll(SeedData.entryTypes)
                    database.labelDao().insertAll(SeedData.foodLabels)
                    database.tagDao().insertAll(SeedData.foodTags)
                    database.tagDao().insertLabelTags(SeedData.foodLabelTags)
                }
            } catch (e: Exception) {
                Log.e("SeedDatabaseCallback", "Failed to seed database", e)
            }
        }
    }
}

package com.haven.app.data

import androidx.room.RoomDatabase
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
            val database = databaseProvider.get()
            database.measurementTypeDao().insertAll(SeedData.measurementTypes)
            database.categoryDao().insertAll(SeedData.categories)
            database.entryTypeDao().insertAll(SeedData.entryTypes)
            database.labelDao().insertAll(SeedData.foodLabels)
            database.tagDao().insertAll(SeedData.foodTags)
            database.tagDao().insertLabelTags(SeedData.foodLabelTags)
        }
    }
}

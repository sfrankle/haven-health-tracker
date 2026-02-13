package com.haven.app.data.dao

import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import com.haven.app.data.HavenDatabase
import com.haven.app.data.SeedData
import org.junit.After
import org.junit.Before

abstract class BaseDaoTest {
    protected lateinit var db: HavenDatabase

    @Before
    fun createDb() {
        db = Room.inMemoryDatabaseBuilder(
            ApplicationProvider.getApplicationContext(),
            HavenDatabase::class.java
        ).allowMainThreadQueries().build()
    }

    @After
    fun closeDb() {
        db.close()
    }

    protected suspend fun seedPhase1Data() {
        db.measurementTypeDao().insertAll(SeedData.measurementTypes)
        db.categoryDao().insertAll(SeedData.categories)
        db.entryTypeDao().insertAll(SeedData.entryTypes)
        db.labelDao().insertAll(SeedData.foodLabels)
        db.tagDao().insertAll(SeedData.foodTags)
        db.tagDao().insertLabelTags(SeedData.foodLabelTags)
    }
}

package com.haven.app.data.dao

import android.database.Cursor
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import app.cash.turbine.test
import com.haven.app.data.HavenDatabase
import com.haven.app.data.SeedData
import com.haven.app.data.SeedDatabaseCallback
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import javax.inject.Provider

class SeedCallbackTest {
    private lateinit var db: HavenDatabase

    @Before
    fun createDb() {
        val dbProvider = Provider { db }
        db = Room.inMemoryDatabaseBuilder(
            ApplicationProvider.getApplicationContext(),
            HavenDatabase::class.java
        )
            .addCallback(SeedDatabaseCallback(dbProvider))
            .allowMainThreadQueries()
            .build()

        // Trigger onCreate by accessing the database
        db.openHelper.writableDatabase
    }

    @After
    fun closeDb() {
        db.close()
    }

    private fun queryCount(table: String): Int {
        val cursor: Cursor = db.openHelper.readableDatabase.query("SELECT COUNT(*) FROM $table")
        cursor.moveToFirst()
        val count = cursor.getInt(0)
        cursor.close()
        return count
    }

    @Test
    fun seedCallback_insertsMeasurementTypes() {
        assertEquals(SeedData.measurementTypes.size, queryCount("measurement_type"))
    }

    @Test
    fun seedCallback_insertsCategories() {
        assertEquals(SeedData.categories.size, queryCount("category"))
    }

    @Test
    fun seedCallback_insertsEntryTypes() = runTest {
        db.entryTypeDao().getEnabled().test {
            val types = awaitItem()
            assertEquals(SeedData.entryTypes.size, types.size)
            assertTrue(types.any { it.name == "Food" })
            assertTrue(types.any { it.name == "Activity" })
        }
    }

    @Test
    fun seedCallback_insertsFoodLabels() = runTest {
        db.labelDao().getByEntryType(1).test {
            val labels = awaitItem()
            assertEquals(SeedData.foodLabels.size, labels.size)
            assertTrue(labels.any { it.name == "Cheese" })
            assertTrue(labels.any { it.name == "Oats" })
        }
    }

    @Test
    fun seedCallback_insertsFoodTags() = runTest {
        val tags = db.tagDao().getByGroup("food")
        assertEquals(SeedData.foodTags.size, tags.size)
        assertTrue(tags.any { it.name == "dairy" })
        assertTrue(tags.any { it.name == "gluten" })
    }

    @Test
    fun seedCallback_insertsLabelTags() {
        assertEquals(SeedData.foodLabelTags.size, queryCount("label_tag"))
    }
}

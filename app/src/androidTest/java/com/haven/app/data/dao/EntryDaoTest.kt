package com.haven.app.data.dao

import app.cash.turbine.test
import com.haven.app.data.entity.Entry
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class EntryDaoTest : BaseDaoTest() {

    @Test
    fun insertWithLabels_createsEntryAndLinks() = runTest {
        seedPhase1Data()
        val entry = Entry(
            entryTypeId = 1,
            timestamp = "2026-02-11T12:00:00Z",
            createdAt = "2026-02-11T12:00:05Z"
        )
        db.entryDao().insertWithLabels(entry, listOf(1L, 2L))

        db.entryDao().getAllWithDetails().test {
            val entries = awaitItem()
            assertEquals(1, entries.size)
            assertEquals("Food", entries[0].entryTypeName)
            assertTrue(entries[0].labelNames?.contains("Cheese") == true)
            assertTrue(entries[0].labelNames?.contains("Bread") == true)
            cancelAndConsumeRemainingEvents()
        }
    }

    @Test
    fun insertNumericEntry_forSleep() = runTest {
        seedPhase1Data()
        val entry = Entry(
            entryTypeId = 4,
            timestamp = "2026-02-11T07:00:00Z",
            createdAt = "2026-02-11T07:00:00Z",
            numericValue = 7.5
        )
        db.entryDao().insert(entry)

        db.entryDao().getAllWithDetails().test {
            val entries = awaitItem()
            assertEquals(1, entries.size)
            assertEquals("Sleep", entries[0].entryTypeName)
            assertEquals(7.5, entries[0].numericValue!!, 0.01)
            cancelAndConsumeRemainingEvents()
        }
    }

    @Test
    fun getDailyTotal_sumsHydrationEntries() = runTest {
        seedPhase1Data()
        db.entryDao().insert(Entry(
            entryTypeId = 3, timestamp = "2026-02-11T08:00:00Z",
            createdAt = "2026-02-11T08:00:00Z", numericValue = 8.0
        ))
        db.entryDao().insert(Entry(
            entryTypeId = 3, timestamp = "2026-02-11T12:00:00Z",
            createdAt = "2026-02-11T12:00:00Z", numericValue = 16.0
        ))

        db.entryDao().getDailyTotal(3, "2026-02-11T00:00:00Z", "2026-02-11T23:59:59Z").test {
            val total = awaitItem()
            assertEquals(24.0, total, 0.01)
            cancelAndConsumeRemainingEvents()
        }
    }
}

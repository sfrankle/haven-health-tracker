package com.haven.app.data.dao

import app.cash.turbine.test
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Test

class EntryTypeDaoTest : BaseDaoTest() {

    @Test
    fun getEnabled_returnsAllSixEntryTypes_orderedBySortOrder() = runTest {
        seedPhase1Data()
        db.entryTypeDao().getEnabled().test {
            val types = awaitItem()
            assertEquals(6, types.size)
            assertEquals("Food", types[0].name)
            assertEquals("Activity", types[5].name)
            cancelAndConsumeRemainingEvents()
        }
    }

    @Test
    fun getById_returnsCorrectEntryType() = runTest {
        seedPhase1Data()
        val food = db.entryTypeDao().getById(1)
        assertEquals("Food", food?.name)
        assertEquals("What did you eat?", food?.prompt)
    }
}

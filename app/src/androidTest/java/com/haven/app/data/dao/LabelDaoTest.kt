package com.haven.app.data.dao

import app.cash.turbine.test
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Test

class LabelDaoTest : BaseDaoTest() {

    @Test
    fun getByEntryType_returnsFoodLabels() = runTest {
        seedPhase1Data()
        db.labelDao().getByEntryType(1).test {
            val labels = awaitItem()
            assertEquals(25, labels.size)
            assertEquals("Cheese", labels[0].name)
            cancelAndConsumeRemainingEvents()
        }
    }

    @Test
    fun getByEntryType_returnsEmpty_forTypeWithNoLabelsYet() = runTest {
        seedPhase1Data()
        db.labelDao().getByEntryType(4).test {
            val labels = awaitItem()
            assertEquals(0, labels.size)
            cancelAndConsumeRemainingEvents()
        }
    }
}

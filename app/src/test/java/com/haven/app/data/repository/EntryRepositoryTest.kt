package com.haven.app.data.repository

import com.haven.app.data.dao.EntryDao
import com.haven.app.data.model.LabelFrequency
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.mockito.kotlin.mock
import org.mockito.kotlin.whenever

class EntryRepositoryTest {

    private lateinit var entryDao: EntryDao
    private lateinit var repository: EntryRepository

    @Before
    fun setup() {
        entryDao = mock()
        repository = EntryRepository(entryDao)
    }

    @Test
    fun `getLabelFrequencyByTimeWindow delegates directly when no midnight wrap`() = runTest {
        val expected = listOf(LabelFrequency(1L, 5), LabelFrequency(2L, 3))
        whenever(entryDao.getLabelFrequencyByTimeWindow(1L, 6, 12, 6)).thenReturn(expected)

        val result = repository.getLabelFrequencyByTimeWindow(1L, 6, 12, 6)
        assertEquals(expected, result)
    }

    @Test
    fun `getLabelFrequencyByTimeWindow merges two ranges when wrapping midnight`() = runTest {
        // Evening range 21-24
        whenever(entryDao.getLabelFrequencyByTimeWindow(1L, 21, 24, 6))
            .thenReturn(listOf(LabelFrequency(1L, 3), LabelFrequency(2L, 2)))
        // Morning range 0-6
        whenever(entryDao.getLabelFrequencyByTimeWindow(1L, 0, 6, 6))
            .thenReturn(listOf(LabelFrequency(1L, 4), LabelFrequency(3L, 5)))

        val result = repository.getLabelFrequencyByTimeWindow(1L, 21, 6, 6)

        // Label 1 should have merged count 3+4=7
        // Label 3 stays at 5
        // Label 2 stays at 2
        // Sorted by count descending
        assertEquals(3, result.size)
        assertEquals(LabelFrequency(1L, 7), result[0])
        assertEquals(LabelFrequency(3L, 5), result[1])
        assertEquals(LabelFrequency(2L, 2), result[2])
    }

    @Test
    fun `getLabelFrequencyByTimeWindow midnight wrap respects limit`() = runTest {
        whenever(entryDao.getLabelFrequencyByTimeWindow(1L, 21, 24, 2))
            .thenReturn(listOf(LabelFrequency(1L, 10), LabelFrequency(2L, 8)))
        whenever(entryDao.getLabelFrequencyByTimeWindow(1L, 0, 6, 2))
            .thenReturn(listOf(LabelFrequency(3L, 6), LabelFrequency(4L, 4)))

        val result = repository.getLabelFrequencyByTimeWindow(1L, 21, 6, 2)

        assertEquals(2, result.size)
        assertEquals(1L, result[0].labelId)
        assertEquals(2L, result[1].labelId)
    }
}

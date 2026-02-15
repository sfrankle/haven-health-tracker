package com.haven.app.ui.trace

import com.haven.app.data.entity.EntryType
import com.haven.app.data.entity.EntryTypeIcon
import com.haven.app.data.model.EntryWithDetails
import com.haven.app.data.repository.EntryRepository
import com.haven.app.data.repository.EntryTypeRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.mockito.kotlin.any
import org.mockito.kotlin.eq
import org.mockito.kotlin.mock
import org.mockito.kotlin.times
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever

@OptIn(ExperimentalCoroutinesApi::class)
class TraceViewModelTest {

    private val testDispatcher = StandardTestDispatcher()
    private lateinit var entryRepository: EntryRepository
    private lateinit var entryTypeRepository: EntryTypeRepository

    private val sampleEntryTypes = listOf(
        EntryType(id = 1, name = "Food", measurementTypeId = 2, icon = EntryTypeIcon.FOOD, sortOrder = 1),
        EntryType(id = 3, name = "Hydration", measurementTypeId = 1, icon = EntryTypeIcon.HYDRATION, sortOrder = 3),
        EntryType(id = 4, name = "Sleep", measurementTypeId = 1, icon = EntryTypeIcon.SLEEP, sortOrder = 4),
    )

    private val sampleEntries = listOf(
        EntryWithDetails(
            id = 1, entryTypeId = 4, entryTypeName = "Sleep", entryTypeIcon = EntryTypeIcon.SLEEP,
            sourceType = "log", timestamp = "2026-02-14T08:00:00-05:00",
            createdAt = "2026-02-14T08:00:00-05:00", numericValue = 7.5, notes = null, labelNames = null
        ),
        EntryWithDetails(
            id = 2, entryTypeId = 3, entryTypeName = "Hydration", entryTypeIcon = EntryTypeIcon.HYDRATION,
            sourceType = "log", timestamp = "2026-02-14T07:30:00-05:00",
            createdAt = "2026-02-14T07:30:00-05:00", numericValue = 8.0, notes = null, labelNames = null
        ),
        EntryWithDetails(
            id = 3, entryTypeId = 1, entryTypeName = "Food", entryTypeIcon = EntryTypeIcon.FOOD,
            sourceType = "log", timestamp = "2026-02-13T12:00:00-05:00",
            createdAt = "2026-02-13T12:00:00-05:00", numericValue = null, notes = null, labelNames = "Eggs, Toast"
        ),
    )

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        entryRepository = mock()
        entryTypeRepository = mock()
        whenever(entryTypeRepository.getEnabled()).thenReturn(flowOf(sampleEntryTypes))
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `initial load groups entries by day with labels`() = runTest {
        whenever(entryRepository.getAllWithDetailsPaged(any(), eq(0))).thenReturn(sampleEntries)

        val viewModel = TraceViewModel(entryRepository, entryTypeRepository)
        testDispatcher.scheduler.advanceUntilIdle()

        val state = viewModel.uiState.value
        assertEquals(2, state.dayGroups.size)
        assertEquals(2, state.dayGroups[0].entries.size) // Feb 14 has 2 entries
        assertEquals(1, state.dayGroups[1].entries.size) // Feb 13 has 1 entry
        // Each day group has a non-empty label
        assertTrue(state.dayGroups.all { it.label.isNotEmpty() })
    }

    @Test
    fun `entries within a day are sorted chronologically`() = runTest {
        whenever(entryRepository.getAllWithDetailsPaged(any(), eq(0))).thenReturn(sampleEntries)

        val viewModel = TraceViewModel(entryRepository, entryTypeRepository)
        testDispatcher.scheduler.advanceUntilIdle()

        val feb14Entries = viewModel.uiState.value.dayGroups[0].entries
        // 7:30 AM should come before 8:00 AM
        assertTrue(feb14Entries[0].timestamp < feb14Entries[1].timestamp)
    }

    @Test
    fun `selectFilter with entry type filters entries`() = runTest {
        val sleepOnly = sampleEntries.filter { it.entryTypeName == "Sleep" }
        whenever(entryRepository.getAllWithDetailsPaged(any(), eq(0))).thenReturn(sampleEntries)
        whenever(entryRepository.getByTypeWithDetailsPaged(eq(4L), any(), eq(0))).thenReturn(sleepOnly)

        val viewModel = TraceViewModel(entryRepository, entryTypeRepository)
        testDispatcher.scheduler.advanceUntilIdle()

        viewModel.selectFilter(4L)
        testDispatcher.scheduler.advanceUntilIdle()

        val state = viewModel.uiState.value
        assertEquals(4L, state.selectedEntryTypeId)
        assertEquals(1, state.dayGroups.size)
        assertEquals(1, state.dayGroups[0].entries.size)
    }

    @Test
    fun `selectFilter with null shows all entries`() = runTest {
        val sleepOnly = sampleEntries.filter { it.entryTypeName == "Sleep" }
        whenever(entryRepository.getAllWithDetailsPaged(any(), eq(0))).thenReturn(sampleEntries)
        whenever(entryRepository.getByTypeWithDetailsPaged(eq(4L), any(), eq(0))).thenReturn(sleepOnly)

        val viewModel = TraceViewModel(entryRepository, entryTypeRepository)
        testDispatcher.scheduler.advanceUntilIdle()

        viewModel.selectFilter(4L)
        testDispatcher.scheduler.advanceUntilIdle()

        viewModel.selectFilter(null)
        testDispatcher.scheduler.advanceUntilIdle()

        val state = viewModel.uiState.value
        assertEquals(null, state.selectedEntryTypeId)
        assertEquals(2, state.dayGroups.size)
    }

    @Test
    fun `loadMore appends next page of entries`() = runTest {
        // First page must have PAGE_SIZE entries so hasMore stays true
        val fullPage = (1..50).map { i ->
            EntryWithDetails(
                id = i.toLong(), entryTypeId = 4, entryTypeName = "Sleep", entryTypeIcon = EntryTypeIcon.SLEEP,
                sourceType = "log", timestamp = "2026-02-14T08:00:00-05:00",
                createdAt = "2026-02-14T08:00:00-05:00", numericValue = 7.0, notes = null, labelNames = null
            )
        }
        whenever(entryRepository.getAllWithDetailsPaged(any(), eq(0))).thenReturn(fullPage)
        whenever(entryRepository.getAllWithDetailsPaged(any(), eq(50))).thenReturn(
            listOf(
                EntryWithDetails(
                    id = 51, entryTypeId = 4, entryTypeName = "Sleep", entryTypeIcon = EntryTypeIcon.SLEEP,
                    sourceType = "log", timestamp = "2026-02-12T09:00:00-05:00",
                    createdAt = "2026-02-12T09:00:00-05:00", numericValue = 6.0, notes = null, labelNames = null
                )
            )
        )

        val viewModel = TraceViewModel(entryRepository, entryTypeRepository)
        testDispatcher.scheduler.advanceUntilIdle()

        viewModel.loadMore()
        testDispatcher.scheduler.advanceUntilIdle()

        val state = viewModel.uiState.value
        assertEquals(2, state.dayGroups.size) // Feb 14 and Feb 12
    }

    @Test
    fun `loadMore sets hasMore to false when no results returned`() = runTest {
        val fullPage = (1..50).map { i ->
            EntryWithDetails(
                id = i.toLong(), entryTypeId = 4, entryTypeName = "Sleep", entryTypeIcon = EntryTypeIcon.SLEEP,
                sourceType = "log", timestamp = "2026-02-14T08:00:00-05:00",
                createdAt = "2026-02-14T08:00:00-05:00", numericValue = 7.0, notes = null, labelNames = null
            )
        }
        whenever(entryRepository.getAllWithDetailsPaged(any(), eq(0))).thenReturn(fullPage)
        whenever(entryRepository.getAllWithDetailsPaged(any(), eq(50))).thenReturn(emptyList())

        val viewModel = TraceViewModel(entryRepository, entryTypeRepository)
        testDispatcher.scheduler.advanceUntilIdle()

        viewModel.loadMore()
        testDispatcher.scheduler.advanceUntilIdle()

        assertFalse(viewModel.uiState.value.hasMore)
    }

    @Test
    fun `selectFilter with same value is a no-op`() = runTest {
        whenever(entryRepository.getAllWithDetailsPaged(any(), eq(0))).thenReturn(sampleEntries)

        val viewModel = TraceViewModel(entryRepository, entryTypeRepository)
        testDispatcher.scheduler.advanceUntilIdle()

        // Initial load calls getAllWithDetailsPaged once; selecting null again should not re-fetch
        viewModel.selectFilter(null)
        testDispatcher.scheduler.advanceUntilIdle()

        verify(entryRepository, times(1)).getAllWithDetailsPaged(any(), eq(0))
    }

    @Test
    fun `entryTypes emits enabled types for filter chips`() = runTest {
        whenever(entryRepository.getAllWithDetailsPaged(any(), eq(0))).thenReturn(emptyList())

        val viewModel = TraceViewModel(entryRepository, entryTypeRepository)
        testDispatcher.scheduler.advanceUntilIdle()

        assertEquals(sampleEntryTypes, viewModel.uiState.value.entryTypes)
    }
}

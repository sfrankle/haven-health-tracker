package com.haven.app.ui.logging

import com.haven.app.data.entity.Entry
import com.haven.app.data.repository.EntryRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
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
import org.mockito.kotlin.argumentCaptor
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever

@OptIn(ExperimentalCoroutinesApi::class)
class SleepLoggingViewModelTest {

    private val testDispatcher = StandardTestDispatcher()
    private lateinit var entryRepository: EntryRepository
    private lateinit var viewModel: SleepLoggingViewModel

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        entryRepository = mock()
        viewModel = SleepLoggingViewModel(entryRepository)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `updateHours sets hours value`() {
        viewModel.updateHours("7.5")
        assertEquals("7.5", viewModel.uiState.value.hours)
    }

    @Test
    fun `updateHours rejects non-numeric input`() {
        viewModel.updateHours("abc")
        assertEquals("", viewModel.uiState.value.hours)
    }

    @Test
    fun `updateNotes sets notes value`() {
        viewModel.updateNotes("Slept well")
        assertEquals("Slept well", viewModel.uiState.value.notes)
    }

    @Test
    fun `canSave is true when hours is valid`() {
        viewModel.updateHours("7")
        assertTrue(viewModel.uiState.value.canSave)
    }

    @Test
    fun `canSave is false when hours is empty`() {
        assertFalse(viewModel.uiState.value.canSave)
    }

    @Test
    fun `save creates entry with correct values`() = runTest {
        val entryTypeId = 4L
        viewModel.updateHours("7.5")
        viewModel.updateNotes("Slept well")

        whenever(entryRepository.insert(org.mockito.kotlin.any())).thenReturn(1L)

        viewModel.save(entryTypeId)
        testDispatcher.scheduler.advanceUntilIdle()

        val captor = argumentCaptor<Entry>()
        verify(entryRepository).insert(captor.capture())

        val entry = captor.firstValue
        assertEquals(entryTypeId, entry.entryTypeId)
        assertEquals(7.5, entry.numericValue!!, 0.01)
        assertEquals("Slept well", entry.notes)
        assertEquals("log", entry.sourceType)
    }
}

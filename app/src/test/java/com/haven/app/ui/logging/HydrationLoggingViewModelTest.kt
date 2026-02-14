package com.haven.app.ui.logging

import com.haven.app.data.entity.Entry
import com.haven.app.data.repository.EntryRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.mockito.kotlin.any
import org.mockito.kotlin.argumentCaptor
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever

@OptIn(ExperimentalCoroutinesApi::class)
class HydrationLoggingViewModelTest {

    private val testDispatcher = StandardTestDispatcher()
    private lateinit var entryRepository: EntryRepository
    private val entryTypeId = 3L

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        entryRepository = mock()
        whenever(entryRepository.getDailyTotal(any(), any(), any())).thenReturn(flowOf(0.0))
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `quickAdd creates entry with correct amount`() = runTest {
        whenever(entryRepository.insert(any())).thenReturn(1L)

        val viewModel = HydrationLoggingViewModel(entryRepository)
        viewModel.quickAdd(entryTypeId, 8.0)
        testDispatcher.scheduler.advanceUntilIdle()

        val captor = argumentCaptor<Entry>()
        verify(entryRepository).insert(captor.capture())

        val entry = captor.firstValue
        assertEquals(entryTypeId, entry.entryTypeId)
        assertEquals(8.0, entry.numericValue!!, 0.01)
        assertEquals("log", entry.sourceType)
    }

    @Test
    fun `updateCustomAmount sets custom amount`() = runTest {
        val viewModel = HydrationLoggingViewModel(entryRepository)
        viewModel.updateCustomAmount("12")
        assertEquals("12", viewModel.uiState.value.customAmount)
    }

    @Test
    fun `updateCustomAmount rejects non-numeric input`() = runTest {
        val viewModel = HydrationLoggingViewModel(entryRepository)
        viewModel.updateCustomAmount("abc")
        assertEquals("", viewModel.uiState.value.customAmount)
    }

    @Test
    fun `saveCustom creates entry with custom amount`() = runTest {
        whenever(entryRepository.insert(any())).thenReturn(1L)

        val viewModel = HydrationLoggingViewModel(entryRepository)
        viewModel.updateCustomAmount("12")
        viewModel.saveCustom(entryTypeId)
        testDispatcher.scheduler.advanceUntilIdle()

        val captor = argumentCaptor<Entry>()
        verify(entryRepository).insert(captor.capture())
        assertEquals(12.0, captor.firstValue.numericValue!!, 0.01)
    }

    @Test
    fun `saveCustom clears custom amount after save`() = runTest {
        whenever(entryRepository.insert(any())).thenReturn(1L)

        val viewModel = HydrationLoggingViewModel(entryRepository)
        viewModel.updateCustomAmount("12")
        viewModel.saveCustom(entryTypeId)
        testDispatcher.scheduler.advanceUntilIdle()

        assertEquals("", viewModel.uiState.value.customAmount)
    }
}

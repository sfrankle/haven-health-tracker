package com.haven.app.ui.logging

import com.haven.app.data.entity.Entry
import com.haven.app.data.entity.Label
import com.haven.app.data.model.LabelFrequency
import com.haven.app.data.repository.EntryRepository
import com.haven.app.data.repository.LabelRepository
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
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.mockito.kotlin.any
import org.mockito.kotlin.argumentCaptor
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever

@OptIn(ExperimentalCoroutinesApi::class)
class FoodLoggingViewModelTest {

    private val testDispatcher = StandardTestDispatcher()
    private lateinit var entryRepository: EntryRepository
    private lateinit var labelRepository: LabelRepository
    private val foodEntryTypeId = 1L

    private val mealSourceLabel = Label(id = 26, entryTypeId = 1, name = "Meal Source", sortOrder = 100)
    private val homeCookedLabel = Label(id = 27, entryTypeId = 1, name = "Home Cooked", parentId = 26, sortOrder = 1)
    private val eatingOutLabel = Label(id = 28, entryTypeId = 1, name = "Eating Out", parentId = 26, sortOrder = 2)
    private val cheeseLabel = Label(id = 1, entryTypeId = 1, name = "Cheese", sortOrder = 1)
    private val breadLabel = Label(id = 2, entryTypeId = 1, name = "Bread", sortOrder = 2)
    private val riceLabel = Label(id = 3, entryTypeId = 1, name = "Rice", sortOrder = 3)

    private val allLabels = listOf(cheeseLabel, breadLabel, riceLabel, mealSourceLabel, homeCookedLabel, eatingOutLabel)

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        entryRepository = mock()
        labelRepository = mock()
        whenever(labelRepository.getByEntryType(foodEntryTypeId)).thenReturn(flowOf(allLabels))
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    private fun createViewModel(): FoodLoggingViewModel {
        return FoodLoggingViewModel(entryRepository, labelRepository)
    }

    @Test
    fun `partitions labels into food labels and meal source options`() = runTest {
        whenever(entryRepository.getLabelFrequencyByTimeWindow(any(), any(), any(), any()))
            .thenReturn(emptyList())

        val viewModel = createViewModel()
        viewModel.loadLabels(foodEntryTypeId)
        testDispatcher.scheduler.advanceUntilIdle()

        val state = viewModel.uiState.value
        assertEquals(3, state.foodLabels.size)
        assertEquals("Cheese", state.foodLabels[0].name)
        assertEquals(2, state.mealSourceOptions.size)
        assertEquals("Home Cooked", state.mealSourceOptions[0].name)
        assertEquals("Eating Out", state.mealSourceOptions[1].name)
    }

    @Test
    fun `toggleLabel adds and removes label from selection`() = runTest {
        whenever(entryRepository.getLabelFrequencyByTimeWindow(any(), any(), any(), any()))
            .thenReturn(emptyList())

        val viewModel = createViewModel()
        viewModel.loadLabels(foodEntryTypeId)
        testDispatcher.scheduler.advanceUntilIdle()

        viewModel.toggleLabel(1L)
        assertTrue(viewModel.uiState.value.selectedLabelIds.contains(1L))

        viewModel.toggleLabel(1L)
        assertFalse(viewModel.uiState.value.selectedLabelIds.contains(1L))
    }

    @Test
    fun `search filters food labels by name`() = runTest {
        whenever(entryRepository.getLabelFrequencyByTimeWindow(any(), any(), any(), any()))
            .thenReturn(emptyList())

        val viewModel = createViewModel()
        viewModel.loadLabels(foodEntryTypeId)
        testDispatcher.scheduler.advanceUntilIdle()

        viewModel.updateSearch("che")
        val state = viewModel.uiState.value
        assertEquals(1, state.filteredLabels.size)
        assertEquals("Cheese", state.filteredLabels[0].name)
    }

    @Test
    fun `search is case insensitive`() = runTest {
        whenever(entryRepository.getLabelFrequencyByTimeWindow(any(), any(), any(), any()))
            .thenReturn(emptyList())

        val viewModel = createViewModel()
        viewModel.loadLabels(foodEntryTypeId)
        testDispatcher.scheduler.advanceUntilIdle()

        viewModel.updateSearch("BREAD")
        assertEquals(1, viewModel.uiState.value.filteredLabels.size)
    }

    @Test
    fun `setMealSource sets and clears meal source`() = runTest {
        whenever(entryRepository.getLabelFrequencyByTimeWindow(any(), any(), any(), any()))
            .thenReturn(emptyList())

        val viewModel = createViewModel()
        viewModel.loadLabels(foodEntryTypeId)
        testDispatcher.scheduler.advanceUntilIdle()

        viewModel.setMealSource(27L)
        assertEquals(27L, viewModel.uiState.value.selectedMealSourceId)

        // Tapping same again clears it
        viewModel.setMealSource(27L)
        assertNull(viewModel.uiState.value.selectedMealSourceId)
    }

    @Test
    fun `canSave is true when at least one food label selected`() = runTest {
        whenever(entryRepository.getLabelFrequencyByTimeWindow(any(), any(), any(), any()))
            .thenReturn(emptyList())

        val viewModel = createViewModel()
        viewModel.loadLabels(foodEntryTypeId)
        testDispatcher.scheduler.advanceUntilIdle()

        assertFalse(viewModel.uiState.value.canSave)
        viewModel.toggleLabel(1L)
        assertTrue(viewModel.uiState.value.canSave)
    }

    @Test
    fun `suggestions come from frequency query with fallback to first 6`() = runTest {
        whenever(entryRepository.getLabelFrequencyByTimeWindow(any(), any(), any(), any()))
            .thenReturn(emptyList())

        val viewModel = createViewModel()
        viewModel.loadLabels(foodEntryTypeId)
        testDispatcher.scheduler.advanceUntilIdle()

        // No history — fallback to first 6 by sort order
        val state = viewModel.uiState.value
        assertEquals(3, state.suggestions.size) // Only 3 food labels in test data
        assertEquals("Cheese", state.suggestions[0].name)
    }

    @Test
    fun `suggestions use frequency data when available`() = runTest {
        whenever(entryRepository.getLabelFrequencyByTimeWindow(any(), any(), any(), any()))
            .thenReturn(listOf(LabelFrequency(3L, 10), LabelFrequency(1L, 5)))

        val viewModel = createViewModel()
        viewModel.loadLabels(foodEntryTypeId)
        testDispatcher.scheduler.advanceUntilIdle()

        val state = viewModel.uiState.value
        assertEquals(2, state.suggestions.size)
        assertEquals("Rice", state.suggestions[0].name)    // id=3, highest frequency
        assertEquals("Cheese", state.suggestions[1].name)  // id=1, second
    }

    @Test
    fun `save creates entry with selected labels and meal source`() = runTest {
        whenever(entryRepository.getLabelFrequencyByTimeWindow(any(), any(), any(), any()))
            .thenReturn(emptyList())
        whenever(entryRepository.insertWithLabels(any(), any())).thenReturn(1L)

        val viewModel = createViewModel()
        viewModel.loadLabels(foodEntryTypeId)
        testDispatcher.scheduler.advanceUntilIdle()

        viewModel.toggleLabel(1L) // Cheese
        viewModel.toggleLabel(2L) // Bread
        viewModel.setMealSource(27L) // Home Cooked
        viewModel.updateNotes("Lunch")

        viewModel.save(foodEntryTypeId)
        testDispatcher.scheduler.advanceUntilIdle()

        val entryCaptor = argumentCaptor<Entry>()
        val labelsCaptor = argumentCaptor<List<Long>>()
        verify(entryRepository).insertWithLabels(entryCaptor.capture(), labelsCaptor.capture())

        val entry = entryCaptor.firstValue
        assertEquals(foodEntryTypeId, entry.entryTypeId)
        assertEquals("log", entry.sourceType)
        assertEquals("Lunch", entry.notes)
        assertNull(entry.numericValue)

        val labelIds = labelsCaptor.firstValue
        assertTrue(labelIds.contains(1L))  // Cheese
        assertTrue(labelIds.contains(2L))  // Bread
        assertTrue(labelIds.contains(27L)) // Home Cooked
        assertEquals(3, labelIds.size)
    }

    @Test
    fun `save without meal source does not include meal source label`() = runTest {
        whenever(entryRepository.getLabelFrequencyByTimeWindow(any(), any(), any(), any()))
            .thenReturn(emptyList())
        whenever(entryRepository.insertWithLabels(any(), any())).thenReturn(1L)

        val viewModel = createViewModel()
        viewModel.loadLabels(foodEntryTypeId)
        testDispatcher.scheduler.advanceUntilIdle()

        viewModel.toggleLabel(1L)
        viewModel.save(foodEntryTypeId)
        testDispatcher.scheduler.advanceUntilIdle()

        val labelsCaptor = argumentCaptor<List<Long>>()
        verify(entryRepository).insertWithLabels(any(), labelsCaptor.capture())
        assertEquals(listOf(1L), labelsCaptor.firstValue)
    }

    @Test
    fun `updateNotes sets notes value`() {
        val viewModel = createViewModel()
        viewModel.updateNotes("Delicious meal")
        assertEquals("Delicious meal", viewModel.uiState.value.notes)
    }
}

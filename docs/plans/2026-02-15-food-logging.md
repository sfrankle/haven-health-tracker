# Food Logging — Implementation Plan

> **For Claude:** REQUIRED SUB-SKILL: Use superpowers:executing-plans to implement this plan task-by-task.

**Goal:** Add food logging to the Tend page — search bar, label chip grid, time-of-day suggestions, meal source toggle, notes, and save.

**Architecture:** FoodLoggingScreen is routed from LoggingRoute via entry type name dispatch (same pattern as Sleep/Hydration). FoodLoggingViewModel loads food labels from LabelRepository, partitions them into regular food labels vs meal-source labels, queries EntryDao for time-of-day frequency suggestions, and saves via EntryRepository.insertWithLabels. Meal source (Home Cooked / Eating Out) is modeled as child labels under a "Meal Source" parent label with a "meal_source" tag for correlation.

**Tech Stack:** Kotlin, Jetpack Compose, Room, Hilt, Coroutines/Flow, Mockito

---

### Task 1: Seed Data — Meal Source Labels and Tag

Add "Meal Source" parent label, "Home Cooked" and "Eating Out" child labels, a "meal_source" tag, and label-tag mappings to the seed data. Bump seed version.

**Files:**
- Modify: `app/src/main/java/com/haven/app/data/SeedData.kt`

**Step 1: Add meal source labels, tag, and mappings**

Add after the existing `foodLabels` list in `SeedData.kt`:

```kotlin
// Meal source labels (entryTypeId = 1) — parent + children
val mealSourceLabel = Label(id = 26, entryTypeId = 1, name = "Meal Source", sortOrder = 100)
val mealSourceChildren = listOf(
    Label(id = 27, entryTypeId = 1, name = "Home Cooked", parentId = 26, sortOrder = 1),
    Label(id = 28, entryTypeId = 1, name = "Eating Out", parentId = 26, sortOrder = 2),
)
```

Add to `foodTags` list:

```kotlin
Tag(id = 9, name = "meal_source", tagGroup = "food"),
```

Add to `foodLabelTags` list:

```kotlin
LabelTag(labelId = 27, tagId = 9), // Home Cooked -> meal_source
LabelTag(labelId = 28, tagId = 9), // Eating Out -> meal_source
```

Update `SeedDatabaseCallback.seedLabels()` to also seed `mealSourceLabel` and `mealSourceChildren`:

In `SeedDatabaseCallback.kt`, update `seedLabels` to iterate over `SeedData.foodLabels + listOf(SeedData.mealSourceLabel) + SeedData.mealSourceChildren`.

Bump `SeedData.VERSION` to `2`.

**Step 2: Verify it compiles**

Run: `./gradlew assembleDebug`
Expected: BUILD SUCCESSFUL

**Step 3: Run existing tests**

Run: `./gradlew test`
Expected: All tests PASS (seed data changes don't affect unit tests)

**Step 4: Commit**

```
feat: add meal source seed labels and tag for food logging
```

---

### Task 2: DAO — Label Frequency by Time Window

Add a query to EntryDao that returns the most frequently used label IDs for a given entry type within an hour range. This powers time-of-day suggestions.

**Files:**
- Modify: `app/src/main/java/com/haven/app/data/dao/EntryDao.kt`
- Modify: `app/src/main/java/com/haven/app/data/repository/EntryRepository.kt`
- Create: `app/src/main/java/com/haven/app/data/model/LabelFrequency.kt`

**Step 1: Create the LabelFrequency data class**

```kotlin
package com.haven.app.data.model

data class LabelFrequency(
    val labelId: Long,
    val count: Int
)
```

**Step 2: Add DAO query**

Add to `EntryDao`:

```kotlin
@Query("""
    SELECT el.label_id AS labelId, COUNT(*) AS count
    FROM entry e
    JOIN entry_label el ON e.id = el.entry_id
    WHERE e.entry_type_id = :entryTypeId
    AND CAST(strftime('%H', e.timestamp) AS INTEGER) >= :hourStart
    AND CAST(strftime('%H', e.timestamp) AS INTEGER) < :hourEnd
    GROUP BY el.label_id
    ORDER BY count DESC
    LIMIT :limit
""")
suspend fun getLabelFrequencyByTimeWindow(
    entryTypeId: Long,
    hourStart: Int,
    hourEnd: Int,
    limit: Int
): List<LabelFrequency>
```

Note: For the late-night window (21:00–5:59) where hourStart > hourEnd, we need a separate query or handle it in the repository. We'll handle it in the repository by making two calls and merging.

**Step 3: Add repository method**

Add to `EntryRepository`:

```kotlin
import com.haven.app.data.model.LabelFrequency

suspend fun getLabelFrequencyByTimeWindow(
    entryTypeId: Long,
    hourStart: Int,
    hourEnd: Int,
    limit: Int
): List<LabelFrequency> {
    return if (hourStart < hourEnd) {
        entryDao.getLabelFrequencyByTimeWindow(entryTypeId, hourStart, hourEnd, limit)
    } else {
        // Wraps midnight (e.g., 21-6): query both ranges and merge
        val evening = entryDao.getLabelFrequencyByTimeWindow(entryTypeId, hourStart, 24, limit)
        val morning = entryDao.getLabelFrequencyByTimeWindow(entryTypeId, 0, hourEnd, limit)
        (evening + morning)
            .groupBy { it.labelId }
            .map { (labelId, freqs) -> LabelFrequency(labelId, freqs.sumOf { it.count }) }
            .sortedByDescending { it.count }
            .take(limit)
    }
}
```

**Step 4: Verify it compiles**

Run: `./gradlew assembleDebug`
Expected: BUILD SUCCESSFUL

**Step 5: Commit**

```
feat: add label frequency by time window query for food suggestions
```

---

### Task 3: FoodLoggingViewModel — Tests

Write tests for FoodLoggingViewModel covering: label loading and partitioning, search filtering, label selection toggle, meal source toggle, suggestion loading, and save.

**Files:**
- Create: `app/src/test/java/com/haven/app/ui/logging/FoodLoggingViewModelTest.kt`

**Step 1: Write the tests**

```kotlin
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
import org.mockito.kotlin.eq
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
```

**Step 2: Run tests to verify they fail**

Run: `./gradlew test --tests "com.haven.app.ui.logging.FoodLoggingViewModelTest"`
Expected: FAIL — `FoodLoggingViewModel` not defined

**Step 3: Commit**

```
test: add FoodLoggingViewModel tests
```

---

### Task 4: FoodLoggingViewModel — Implementation

Implement the ViewModel to make all tests pass.

**Files:**
- Create: `app/src/main/java/com/haven/app/ui/logging/FoodLoggingViewModel.kt`

**Step 1: Write the implementation**

```kotlin
package com.haven.app.ui.logging

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.haven.app.data.entity.Entry
import com.haven.app.data.entity.Label
import com.haven.app.data.repository.EntryRepository
import com.haven.app.data.repository.LabelRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.Instant
import java.time.LocalTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import javax.inject.Inject

data class FoodUiState(
    val foodLabels: List<Label> = emptyList(),
    val mealSourceOptions: List<Label> = emptyList(),
    val selectedLabelIds: Set<Long> = emptySet(),
    val selectedMealSourceId: Long? = null,
    val searchQuery: String = "",
    val notes: String = "",
    val suggestions: List<Label> = emptyList(),
    val saved: Boolean = false
) {
    val canSave: Boolean get() = selectedLabelIds.isNotEmpty()

    val filteredLabels: List<Label>
        get() = if (searchQuery.isBlank()) foodLabels
        else foodLabels.filter { it.name.contains(searchQuery, ignoreCase = true) }
}

@HiltViewModel
class FoodLoggingViewModel @Inject constructor(
    private val entryRepository: EntryRepository,
    private val labelRepository: LabelRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(FoodUiState())
    val uiState: StateFlow<FoodUiState> = _uiState.asStateFlow()

    fun loadLabels(entryTypeId: Long) {
        viewModelScope.launch {
            val allLabels = labelRepository.getByEntryType(entryTypeId).first()

            // Find parent IDs that have children
            val parentIds = allLabels.mapNotNull { it.parentId }.toSet()

            // Partition: group parents (have children), their children, and regular labels
            val groupParents = allLabels.filter { it.id in parentIds }
            val mealSourceParent = groupParents.firstOrNull()
            val mealSourceOptions = if (mealSourceParent != null) {
                allLabels.filter { it.parentId == mealSourceParent.id }
            } else emptyList()

            val foodLabels = allLabels.filter { it.parentId == null && it.id !in parentIds }

            // Load time-of-day suggestions
            val (hourStart, hourEnd) = currentMealWindow()
            val frequencies = entryRepository.getLabelFrequencyByTimeWindow(
                entryTypeId, hourStart, hourEnd, 6
            )

            val suggestions = if (frequencies.isNotEmpty()) {
                val freqLabelIds = frequencies.map { it.labelId }
                freqLabelIds.mapNotNull { id -> foodLabels.find { it.id == id } }
            } else {
                foodLabels.take(6)
            }

            _uiState.update {
                it.copy(
                    foodLabels = foodLabels,
                    mealSourceOptions = mealSourceOptions,
                    suggestions = suggestions
                )
            }
        }
    }

    fun toggleLabel(labelId: Long) {
        _uiState.update {
            val newSelection = if (labelId in it.selectedLabelIds) {
                it.selectedLabelIds - labelId
            } else {
                it.selectedLabelIds + labelId
            }
            it.copy(selectedLabelIds = newSelection)
        }
    }

    fun setMealSource(labelId: Long) {
        _uiState.update {
            it.copy(selectedMealSourceId = if (it.selectedMealSourceId == labelId) null else labelId)
        }
    }

    fun updateSearch(query: String) {
        _uiState.update { it.copy(searchQuery = query) }
    }

    fun updateNotes(value: String) {
        _uiState.update { it.copy(notes = value) }
    }

    fun save(entryTypeId: Long) {
        val state = _uiState.value
        if (!state.canSave) return

        viewModelScope.launch {
            val now = Instant.now().atZone(ZoneId.systemDefault())
            val timestamp = DateTimeFormatter.ISO_OFFSET_DATE_TIME.format(now)

            val allLabelIds = state.selectedLabelIds.toMutableList()
            state.selectedMealSourceId?.let { allLabelIds.add(it) }

            entryRepository.insertWithLabels(
                Entry(
                    entryTypeId = entryTypeId,
                    sourceType = "log",
                    timestamp = timestamp,
                    createdAt = timestamp,
                    notes = state.notes.ifBlank { null }
                ),
                allLabelIds
            )
            _uiState.update { it.copy(saved = true) }
        }
    }

    private fun currentMealWindow(): Pair<Int, Int> {
        val hour = LocalTime.now().hour
        return when (hour) {
            in 6..11 -> 6 to 12
            in 12..16 -> 12 to 17
            in 17..20 -> 17 to 21
            else -> 21 to 6 // wraps midnight
        }
    }
}
```

**Step 2: Run tests to verify they pass**

Run: `./gradlew test --tests "com.haven.app.ui.logging.FoodLoggingViewModelTest"`
Expected: All tests PASS

**Step 3: Commit**

```
feat: add FoodLoggingViewModel with search, suggestions, and meal source
```

---

### Task 5: Wire Food into LoggingRoute

Add the `"Food"` case to LoggingRoute so tapping Food on the Tend page navigates to the food logging screen.

**Files:**
- Modify: `app/src/main/java/com/haven/app/ui/logging/LoggingRoute.kt`

**Step 1: Add Food case**

In `LoggingRoute.kt`, update the `when (type.name)` block to add:

```kotlin
"Food" -> FoodLoggingScreen(entryTypeId = type.id, onSaved = onSaved, onBack = onBack)
```

Add the import:

```kotlin
import com.haven.app.ui.logging.FoodLoggingScreen
```

**Step 2: Create placeholder FoodLoggingScreen so it compiles**

Create `app/src/main/java/com/haven/app/ui/logging/FoodLoggingScreen.kt`:

```kotlin
package com.haven.app.ui.logging

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier

@Composable
fun FoodLoggingScreen(
    entryTypeId: Long,
    onSaved: () -> Unit,
    onBack: () -> Unit
) {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Text("Food logging placeholder")
    }
}
```

**Step 3: Verify it compiles**

Run: `./gradlew assembleDebug`
Expected: BUILD SUCCESSFUL

**Step 4: Commit**

```
feat: wire Food entry type into LoggingRoute
```

---

### Task 6: FoodLoggingScreen UI

Replace the placeholder with the full food logging screen: search bar, suggestion chips, label chip grid, selected labels, meal source toggle, notes, and save.

**Files:**
- Modify: `app/src/main/java/com/haven/app/ui/logging/FoodLoggingScreen.kt`

**Step 1: Implement the screen**

```kotlin
package com.haven.app.ui.logging

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.rounded.Search
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.haven.app.data.entity.Label

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun FoodLoggingScreen(
    entryTypeId: Long,
    onSaved: () -> Unit,
    onBack: () -> Unit,
    viewModel: FoodLoggingViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(entryTypeId) {
        viewModel.loadLabels(entryTypeId)
    }

    LaunchedEffect(uiState.saved) {
        if (uiState.saved) onSaved()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Log Food") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Rounded.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 16.dp)
                .verticalScroll(rememberScrollState())
        ) {
            // Search bar
            OutlinedTextField(
                value = uiState.searchQuery,
                onValueChange = viewModel::updateSearch,
                placeholder = { Text("Search foods") },
                leadingIcon = { Icon(Icons.Rounded.Search, contentDescription = null) },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Suggestions (when no search) or search results
            if (uiState.searchQuery.isBlank()) {
                if (uiState.suggestions.isNotEmpty()) {
                    Text("Suggestions", style = MaterialTheme.typography.titleSmall)
                    Spacer(modifier = Modifier.height(8.dp))
                    LabelChipGrid(
                        labels = uiState.suggestions,
                        selectedIds = uiState.selectedLabelIds,
                        onToggle = viewModel::toggleLabel
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                }

                // Full label grid
                Text("All foods", style = MaterialTheme.typography.titleSmall)
                Spacer(modifier = Modifier.height(8.dp))
                LabelChipGrid(
                    labels = uiState.foodLabels,
                    selectedIds = uiState.selectedLabelIds,
                    onToggle = viewModel::toggleLabel
                )
            } else {
                // Search results
                LabelChipGrid(
                    labels = uiState.filteredLabels,
                    selectedIds = uiState.selectedLabelIds,
                    onToggle = viewModel::toggleLabel
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Meal source toggle
            if (uiState.mealSourceOptions.isNotEmpty()) {
                Text("Where", style = MaterialTheme.typography.titleSmall)
                Spacer(modifier = Modifier.height(8.dp))
                MealSourceToggle(
                    options = uiState.mealSourceOptions,
                    selectedId = uiState.selectedMealSourceId,
                    onSelect = viewModel::setMealSource
                )
                Spacer(modifier = Modifier.height(16.dp))
            }

            // Notes
            OutlinedTextField(
                value = uiState.notes,
                onValueChange = viewModel::updateNotes,
                label = { Text("Notes (optional)") },
                modifier = Modifier.fillMaxWidth(),
                minLines = 2
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Save button
            Button(
                onClick = { viewModel.save(entryTypeId) },
                enabled = uiState.canSave,
                shape = MaterialTheme.shapes.large,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Save")
            }

            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun LabelChipGrid(
    labels: List<Label>,
    selectedIds: Set<Long>,
    onToggle: (Long) -> Unit
) {
    FlowRow(
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        labels.forEach { label ->
            FilterChip(
                selected = label.id in selectedIds,
                onClick = { onToggle(label.id) },
                label = { Text(label.name) },
                colors = FilterChipDefaults.filterChipColors(
                    selectedContainerColor = MaterialTheme.colorScheme.primaryContainer,
                    selectedLabelColor = MaterialTheme.colorScheme.onPrimaryContainer
                )
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun MealSourceToggle(
    options: List<Label>,
    selectedId: Long?,
    onSelect: (Long) -> Unit
) {
    SingleChoiceSegmentedButtonRow(modifier = Modifier.fillMaxWidth()) {
        options.forEachIndexed { index, option ->
            SegmentedButton(
                selected = selectedId == option.id,
                onClick = { onSelect(option.id) },
                shape = SegmentedButtonDefaults.itemShape(index = index, count = options.size)
            ) {
                Text(option.name)
            }
        }
    }
}
```

**Step 2: Verify it compiles**

Run: `./gradlew assembleDebug`
Expected: BUILD SUCCESSFUL

**Step 3: Commit**

```
feat: implement FoodLoggingScreen with search, chips, meal source toggle
```

---

### Task 7: Final Verification and Docs

Run all tests, update changelog and roadmap.

**Step 1: Run all tests**

Run: `./gradlew test`
Expected: All tests PASS

Run: `./gradlew lint`
Expected: No errors

**Step 2: Update changelog**

Add to `docs/changelog.md` under Phase 1:

```markdown
### PR 3: Food Logging
- Food logging screen with search bar, label chip grid, and multi-select
- Time-of-day meal suggestions (morning/afternoon/evening/late-night frequency)
- Meal source toggle (Home Cooked / Eating Out) stored as labels for correlation
- Optional notes field
- First label-based entry type proving insertWithLabels path end-to-end
```

**Step 3: Update roadmap**

Mark `Food logging` as done in `docs/roadmap.md`:

```markdown
### ~~Food logging~~ ✓ PR 3
```

**Step 4: Commit**

```
docs: update changelog and roadmap for food logging
```

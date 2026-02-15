# Trace Page — Implementation Plan

> **For Claude:** REQUIRED SUB-SKILL: Use superpowers:executing-plans to implement this plan task-by-task.

**Goal:** Build the Trace page — a scrollable, filterable journal of all logged entries grouped by day, with entry type filter chips and lazy loading.

**Architecture:** TraceViewModel collects entries from EntryRepository, groups them by date, and exposes a list of day-grouped display items. Filter chips swap which Flow the ViewModel observes. Pagination uses LIMIT/OFFSET DAO queries. An `entrySummary()` utility maps each EntryWithDetails to journal-style text ("I slept 7.5 hours").

**Tech Stack:** Kotlin, Jetpack Compose, Room, Hilt, Coroutines/Flow, Turbine + Mockito for tests

---

### Task 1: Entry Summary Utility

Maps `EntryWithDetails` to journal-style text. Pure function, no dependencies — ideal TDD target.

**Files:**
- Create: `app/src/test/java/com/haven/app/ui/trace/EntrySummaryTest.kt`
- Create: `app/src/main/java/com/haven/app/ui/trace/EntrySummary.kt`

**Step 1: Write the test**

```kotlin
package com.haven.app.ui.trace

import com.haven.app.data.entity.EntryTypeIcon
import com.haven.app.data.model.EntryWithDetails
import org.junit.Assert.assertEquals
import org.junit.Test

class EntrySummaryTest {

    private fun entry(
        entryTypeName: String,
        entryTypeIcon: EntryTypeIcon? = null,
        numericValue: Double? = null,
        labelNames: String? = null
    ) = EntryWithDetails(
        id = 1,
        entryTypeId = 1,
        entryTypeName = entryTypeName,
        entryTypeIcon = entryTypeIcon,
        sourceType = "log",
        timestamp = "2026-02-14T08:00:00-05:00",
        createdAt = "2026-02-14T08:00:00-05:00",
        numericValue = numericValue,
        notes = null,
        labelNames = labelNames
    )

    @Test
    fun `sleep entry shows hours`() {
        assertEquals(
            "I slept 7.5 hours",
            entrySummary(entry("Sleep", numericValue = 7.5))
        )
    }

    @Test
    fun `sleep entry with whole number omits decimal`() {
        assertEquals(
            "I slept 8 hours",
            entrySummary(entry("Sleep", numericValue = 8.0))
        )
    }

    @Test
    fun `hydration entry shows oz`() {
        assertEquals(
            "I drank 16 oz",
            entrySummary(entry("Hydration", numericValue = 16.0))
        )
    }

    @Test
    fun `food entry shows labels`() {
        assertEquals(
            "I ate Eggs, Toast",
            entrySummary(entry("Food", labelNames = "Eggs, Toast"))
        )
    }

    @Test
    fun `emotion entry shows labels`() {
        assertEquals(
            "I felt Content, Calm",
            entrySummary(entry("Emotion", labelNames = "Content, Calm"))
        )
    }

    @Test
    fun `symptom entry shows labels`() {
        assertEquals(
            "I experienced Headache",
            entrySummary(entry("Symptom", labelNames = "Headache"))
        )
    }

    @Test
    fun `activity entry shows label as verb`() {
        assertEquals(
            "I hiked",
            entrySummary(entry("Activity", labelNames = "Hiked"))
        )
    }

    @Test
    fun `activity entry with multiple labels`() {
        assertEquals(
            "I did Yoga, Stretching",
            entrySummary(entry("Activity", labelNames = "Yoga, Stretching"))
        )
    }

    @Test
    fun `unknown entry type shows generic summary`() {
        assertEquals(
            "Logged Custom",
            entrySummary(entry("Custom"))
        )
    }
}
```

**Step 2: Run test to verify it fails**

Run: `./gradlew test --tests "com.haven.app.ui.trace.EntrySummaryTest"`
Expected: FAIL — `entrySummary` not defined

**Step 3: Write the implementation**

```kotlin
package com.haven.app.ui.trace

import com.haven.app.data.model.EntryWithDetails

fun entrySummary(entry: EntryWithDetails): String {
    val value = entry.numericValue
    val labels = entry.labelNames

    return when (entry.entryTypeName) {
        "Sleep" -> "I slept ${formatNumber(value)} hours"
        "Hydration" -> "I drank ${formatNumber(value)} oz"
        "Food" -> "I ate ${labels ?: "something"}"
        "Emotion" -> "I felt ${labels ?: "something"}"
        "Symptom" -> "I experienced ${labels ?: "something"}"
        "Activity" -> formatActivity(labels)
        else -> "Logged ${entry.entryTypeName}"
    }
}

private fun formatNumber(value: Double?): String {
    if (value == null) return "0"
    return if (value == value.toLong().toDouble()) {
        value.toLong().toString()
    } else {
        value.toString()
    }
}

private fun formatActivity(labels: String?): String {
    if (labels == null) return "I did something"
    // Single label reads as a verb: "I hiked"
    // Multiple labels use "did": "I did Yoga, Stretching"
    return if (',' in labels) {
        "I did $labels"
    } else {
        "I ${labels.lowercase()}"
    }
}
```

**Step 4: Run test to verify it passes**

Run: `./gradlew test --tests "com.haven.app.ui.trace.EntrySummaryTest"`
Expected: PASS

**Step 5: Commit**

```
feat: add entrySummary utility for journal-style entry text
```

---

### Task 2: Paginated DAO Query and Repository Method

Add LIMIT/OFFSET queries to EntryDao and expose via EntryRepository for lazy loading.

**Files:**
- Modify: `app/src/main/java/com/haven/app/data/dao/EntryDao.kt`
- Modify: `app/src/main/java/com/haven/app/data/repository/EntryRepository.kt`

**Step 1: Add paginated queries to EntryDao**

Add after the existing `getAllWithDetails()` method:

```kotlin
@Query("""
    SELECT e.id, e.entry_type_id AS entryTypeId, et.name AS entryTypeName, et.icon AS entryTypeIcon,
           e.source_type AS sourceType, e.timestamp, e.created_at AS createdAt,
           e.numeric_value AS numericValue, e.notes,
           GROUP_CONCAT(l.name, ', ') AS labelNames
    FROM entry e
    JOIN entry_type et ON e.entry_type_id = et.id
    LEFT JOIN entry_label el ON e.id = el.entry_id
    LEFT JOIN label l ON el.label_id = l.id
    GROUP BY e.id
    ORDER BY e.timestamp DESC
    LIMIT :limit OFFSET :offset
""")
suspend fun getAllWithDetailsPaged(limit: Int, offset: Int): List<EntryWithDetails>

@Query("""
    SELECT e.id, e.entry_type_id AS entryTypeId, et.name AS entryTypeName, et.icon AS entryTypeIcon,
           e.source_type AS sourceType, e.timestamp, e.created_at AS createdAt,
           e.numeric_value AS numericValue, e.notes,
           GROUP_CONCAT(l.name, ', ') AS labelNames
    FROM entry e
    JOIN entry_type et ON e.entry_type_id = et.id
    LEFT JOIN entry_label el ON e.id = el.entry_id
    LEFT JOIN label l ON el.label_id = l.id
    WHERE e.entry_type_id = :entryTypeId
    GROUP BY e.id
    ORDER BY e.timestamp DESC
    LIMIT :limit OFFSET :offset
""")
suspend fun getByTypeWithDetailsPaged(entryTypeId: Long, limit: Int, offset: Int): List<EntryWithDetails>
```

**Step 2: Add repository methods**

Add to `EntryRepository`:

```kotlin
suspend fun getAllWithDetailsPaged(limit: Int, offset: Int): List<EntryWithDetails> =
    entryDao.getAllWithDetailsPaged(limit, offset)

suspend fun getByTypeWithDetailsPaged(entryTypeId: Long, limit: Int, offset: Int): List<EntryWithDetails> =
    entryDao.getByTypeWithDetailsPaged(entryTypeId, limit, offset)
```

**Step 3: Verify it compiles**

Run: `./gradlew assembleDebug`
Expected: BUILD SUCCESSFUL

**Step 4: Commit**

```
feat: add paginated entry queries to EntryDao and EntryRepository
```

---

### Task 3: TraceViewModel

Loads entries page-by-page, groups by day, supports filter chip selection.

**Files:**
- Create: `app/src/test/java/com/haven/app/ui/trace/TraceViewModelTest.kt`
- Create: `app/src/main/java/com/haven/app/ui/trace/TraceViewModel.kt`

**Step 1: Write the test**

```kotlin
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
    fun `initial load groups entries by day`() = runTest {
        whenever(entryRepository.getAllWithDetailsPaged(any(), eq(0))).thenReturn(sampleEntries)

        val viewModel = TraceViewModel(entryRepository, entryTypeRepository)
        testDispatcher.scheduler.advanceUntilIdle()

        val state = viewModel.uiState.value
        assertEquals(2, state.dayGroups.size)
        assertEquals(2, state.dayGroups[0].entries.size) // Feb 14 has 2 entries
        assertEquals(1, state.dayGroups[1].entries.size) // Feb 13 has 1 entry
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
        whenever(entryRepository.getAllWithDetailsPaged(any(), eq(0))).thenReturn(sampleEntries)

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
        whenever(entryRepository.getAllWithDetailsPaged(any(), eq(0))).thenReturn(sampleEntries)
        whenever(entryRepository.getAllWithDetailsPaged(any(), eq(50))).thenReturn(
            listOf(
                EntryWithDetails(
                    id = 4, entryTypeId = 4, entryTypeName = "Sleep", entryTypeIcon = EntryTypeIcon.SLEEP,
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
        assertEquals(3, state.dayGroups.size) // Feb 14, Feb 13, Feb 12
    }

    @Test
    fun `loadMore sets hasMore to false when no results returned`() = runTest {
        whenever(entryRepository.getAllWithDetailsPaged(any(), eq(0))).thenReturn(sampleEntries)
        whenever(entryRepository.getAllWithDetailsPaged(any(), eq(50))).thenReturn(emptyList())

        val viewModel = TraceViewModel(entryRepository, entryTypeRepository)
        testDispatcher.scheduler.advanceUntilIdle()

        viewModel.loadMore()
        testDispatcher.scheduler.advanceUntilIdle()

        assertFalse(viewModel.uiState.value.hasMore)
    }

    @Test
    fun `entryTypes emits enabled types for filter chips`() = runTest {
        whenever(entryRepository.getAllWithDetailsPaged(any(), eq(0))).thenReturn(emptyList())

        val viewModel = TraceViewModel(entryRepository, entryTypeRepository)
        testDispatcher.scheduler.advanceUntilIdle()

        assertEquals(sampleEntryTypes, viewModel.uiState.value.entryTypes)
    }
}
```

**Step 2: Run test to verify it fails**

Run: `./gradlew test --tests "com.haven.app.ui.trace.TraceViewModelTest"`
Expected: FAIL — `TraceViewModel` not defined

**Step 3: Write the implementation**

```kotlin
package com.haven.app.ui.trace

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.haven.app.data.entity.EntryType
import com.haven.app.data.model.EntryWithDetails
import com.haven.app.data.repository.EntryRepository
import com.haven.app.data.repository.EntryTypeRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.OffsetDateTime
import java.time.format.DateTimeFormatter
import javax.inject.Inject

data class DayGroup(
    val date: LocalDate,
    val entries: List<EntryWithDetails>
)

data class TraceUiState(
    val dayGroups: List<DayGroup> = emptyList(),
    val entryTypes: List<EntryType> = emptyList(),
    val selectedEntryTypeId: Long? = null,
    val isLoading: Boolean = false,
    val hasMore: Boolean = true
)

@HiltViewModel
class TraceViewModel @Inject constructor(
    private val entryRepository: EntryRepository,
    private val entryTypeRepository: EntryTypeRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(TraceUiState())
    val uiState: StateFlow<TraceUiState> = _uiState.asStateFlow()

    private var allEntries = mutableListOf<EntryWithDetails>()
    private var offset = 0

    companion object {
        const val PAGE_SIZE = 50
    }

    init {
        viewModelScope.launch {
            entryTypeRepository.getEnabled().collect { types ->
                _uiState.update { it.copy(entryTypes = types) }
            }
        }
        loadInitial()
    }

    fun selectFilter(entryTypeId: Long?) {
        _uiState.update { it.copy(selectedEntryTypeId = entryTypeId) }
        allEntries.clear()
        offset = 0
        loadInitial()
    }

    fun loadMore() {
        if (_uiState.value.isLoading || !_uiState.value.hasMore) return
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            val page = fetchPage(offset)
            if (page.isEmpty()) {
                _uiState.update { it.copy(isLoading = false, hasMore = false) }
            } else {
                allEntries.addAll(page)
                offset += PAGE_SIZE
                _uiState.update {
                    it.copy(
                        dayGroups = groupByDay(allEntries),
                        isLoading = false,
                        hasMore = page.size >= PAGE_SIZE
                    )
                }
            }
        }
    }

    private fun loadInitial() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            val page = fetchPage(0)
            allEntries.addAll(page)
            offset = PAGE_SIZE
            _uiState.update {
                it.copy(
                    dayGroups = groupByDay(allEntries),
                    isLoading = false,
                    hasMore = page.size >= PAGE_SIZE
                )
            }
        }
    }

    private suspend fun fetchPage(pageOffset: Int): List<EntryWithDetails> {
        val typeId = _uiState.value.selectedEntryTypeId
        return if (typeId == null) {
            entryRepository.getAllWithDetailsPaged(PAGE_SIZE, pageOffset)
        } else {
            entryRepository.getByTypeWithDetailsPaged(typeId, PAGE_SIZE, pageOffset)
        }
    }

    private fun groupByDay(entries: List<EntryWithDetails>): List<DayGroup> {
        return entries
            .groupBy { entry ->
                OffsetDateTime.parse(entry.timestamp).toLocalDate()
            }
            .map { (date, dayEntries) ->
                DayGroup(
                    date = date,
                    entries = dayEntries.sortedBy { it.timestamp }
                )
            }
            .sortedByDescending { it.date }
    }
}
```

**Step 4: Run test to verify it passes**

Run: `./gradlew test --tests "com.haven.app.ui.trace.TraceViewModelTest"`
Expected: PASS

**Step 5: Commit**

```
feat: add TraceViewModel with pagination, day grouping, and filtering
```

---

### Task 4: TraceScreen UI

Replace the stub TraceScreen with the full layout: filter chips, sticky day headers, entry rows.

**Files:**
- Modify: `app/src/main/java/com/haven/app/ui/trace/TraceScreen.kt`

**Step 1: Implement TraceScreen**

```kotlin
package com.haven.app.ui.trace

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.haven.app.data.model.EntryWithDetails
import com.haven.app.ui.common.entryTypeIcon
import java.time.LocalDate
import java.time.OffsetDateTime
import java.time.format.DateTimeFormatter

@Composable
fun TraceScreen(
    viewModel: TraceViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val listState = rememberLazyListState()

    // Load more when near the bottom
    val shouldLoadMore by remember {
        derivedStateOf {
            val lastVisibleItem = listState.layoutInfo.visibleItemsInfo.lastOrNull()?.index ?: 0
            val totalItems = listState.layoutInfo.totalItemsCount
            lastVisibleItem >= totalItems - 5 && uiState.hasMore && !uiState.isLoading
        }
    }

    LaunchedEffect(shouldLoadMore) {
        if (shouldLoadMore) {
            viewModel.loadMore()
        }
    }

    Column(modifier = Modifier.fillMaxSize()) {
        // Filter chips
        LazyRow(
            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            item {
                FilterChip(
                    selected = uiState.selectedEntryTypeId == null,
                    onClick = { viewModel.selectFilter(null) },
                    label = { Text("All") }
                )
            }
            items(uiState.entryTypes) { entryType ->
                FilterChip(
                    selected = uiState.selectedEntryTypeId == entryType.id,
                    onClick = { viewModel.selectFilter(entryType.id) },
                    label = { Text(entryType.name) },
                    leadingIcon = {
                        Icon(
                            imageVector = entryTypeIcon(entryType.icon),
                            contentDescription = null,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                )
            }
        }

        if (uiState.dayGroups.isEmpty() && !uiState.isLoading) {
            // Empty state
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "No entries yet — head to Tend to start logging",
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        } else {
            LazyColumn(
                state = listState,
                contentPadding = PaddingValues(bottom = 16.dp),
                modifier = Modifier.fillMaxSize()
            ) {
                uiState.dayGroups.forEach { dayGroup ->
                    // Sticky day header
                    stickyHeader(key = dayGroup.date.toString()) {
                        DayHeader(date = dayGroup.date)
                    }
                    items(
                        items = dayGroup.entries,
                        key = { it.id }
                    ) { entry ->
                        EntryRow(entry = entry)
                    }
                }

                if (uiState.isLoading) {
                    item {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            CircularProgressIndicator(modifier = Modifier.size(24.dp))
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun DayHeader(date: LocalDate) {
    val today = LocalDate.now()
    val label = when (date) {
        today -> "Today"
        today.minusDays(1) -> "Yesterday"
        else -> date.format(DateTimeFormatter.ofPattern("EEE, MMM d"))
    }
    Text(
        text = label,
        style = MaterialTheme.typography.titleSmall,
        color = MaterialTheme.colorScheme.primary,
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
    )
}

@Composable
private fun EntryRow(entry: EntryWithDetails) {
    val time = OffsetDateTime.parse(entry.timestamp)
        .format(DateTimeFormatter.ofPattern("h:mm a"))

    val summary = entrySummary(entry)
    // Bold the value portion (everything after the verb)
    val parts = splitSummaryForBold(entry)

    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 6.dp)
    ) {
        Text(
            text = time,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.width(72.dp)
        )
        Icon(
            imageVector = entryTypeIcon(entry.entryTypeIcon),
            contentDescription = entry.entryTypeName,
            modifier = Modifier.size(20.dp),
            tint = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text = buildAnnotatedString {
                append(parts.first)
                withStyle(SpanStyle(fontWeight = FontWeight.Bold)) {
                    append(parts.second)
                }
            },
            style = MaterialTheme.typography.bodyMedium
        )
    }
}

/**
 * Splits a summary into (prefix, boldPart) for display.
 * E.g., "I slept 7.5 hours" → ("I slept ", "7.5 hours")
 */
private fun splitSummaryForBold(entry: EntryWithDetails): Pair<String, String> {
    val value = entry.numericValue
    val labels = entry.labelNames

    return when (entry.entryTypeName) {
        "Sleep" -> "I slept " to "${formatNumberForDisplay(value)} hours"
        "Hydration" -> "I drank " to "${formatNumberForDisplay(value)} oz"
        "Food" -> "I ate " to (labels ?: "something")
        "Emotion" -> "I felt " to (labels ?: "something")
        "Symptom" -> "I experienced " to (labels ?: "something")
        "Activity" -> {
            if (labels != null && ',' !in labels) {
                "I " to labels.lowercase()
            } else {
                "I did " to (labels ?: "something")
            }
        }
        else -> "" to "Logged ${entry.entryTypeName}"
    }
}

private fun formatNumberForDisplay(value: Double?): String {
    if (value == null) return "0"
    return if (value == value.toLong().toDouble()) {
        value.toLong().toString()
    } else {
        value.toString()
    }
}
```

**Step 2: Verify it compiles**

Run: `./gradlew assembleDebug`
Expected: BUILD SUCCESSFUL

**Step 3: Commit**

```
feat: implement TraceScreen with filter chips, day groups, and entry rows
```

---

### Task 5: Final Verification and Docs

Run all tests, update changelog and roadmap.

**Step 1: Run all tests**

Run: `./gradlew test`
Expected: All tests PASS

Run: `./gradlew lint`
Expected: No errors

**Step 2: Update changelog**

Add to `docs/changelog.md` under Phase 1:

```markdown
### PR 4: Trace Page
- Trace page with entries grouped by day, most recent first
- Journal-style entry summaries ("I slept 7.5 hours", "I ate Eggs, Toast")
- Entry type filter chips (All, Food, Sleep, etc.)
- Paginated lazy loading (50 entries at a time)
- Sticky day headers (Today, Yesterday, then date format)
```

**Step 3: Update roadmap**

In `docs/roadmap.md`, mark the Trace page item as done:

```markdown
### ~~Trace page~~ ✓ PR 4
```

**Step 4: Commit**

```
docs: update changelog and roadmap for Trace page
```

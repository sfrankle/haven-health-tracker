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

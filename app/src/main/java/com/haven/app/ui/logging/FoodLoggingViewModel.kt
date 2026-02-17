package com.haven.app.ui.logging

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.haven.app.data.entity.Entry
import com.haven.app.data.entity.Label
import com.haven.app.data.repository.EntryRepository
import com.haven.app.data.repository.LabelRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.receiveAsFlow
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
    val error: String? = null
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

    private val _savedEvent = Channel<Unit>(Channel.BUFFERED)
    val savedEvent = _savedEvent.receiveAsFlow()

    private var loadJob: Job? = null

    fun loadLabels(entryTypeId: Long) {
        if (loadJob?.isActive == true) return

        loadJob = viewModelScope.launch {
            try {
                val allLabels = labelRepository.getByEntryType(entryTypeId).first()

                // Find meal source parent by name
                val mealSourceParent = allLabels.find { it.name == "Meal Source" && it.parentId == null }
                val mealSourceOptions = if (mealSourceParent != null) {
                    allLabels.filter { it.parentId == mealSourceParent.id }
                } else emptyList()

                // Exclude meal source parent and its children from food labels
                val mealSourceIds = setOfNotNull(mealSourceParent?.id) +
                    mealSourceOptions.map { it.id }.toSet()
                val foodLabels = allLabels.filter { it.id !in mealSourceIds }

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
                        suggestions = suggestions,
                        error = null
                    )
                }
            } catch (e: Exception) {
                _uiState.update { it.copy(error = "Failed to load food labels") }
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
            try {
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
                _savedEvent.send(Unit)
            } catch (e: Exception) {
                _uiState.update { it.copy(error = "Failed to save entry") }
            }
        }
    }

    fun clearError() {
        _uiState.update { it.copy(error = null) }
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

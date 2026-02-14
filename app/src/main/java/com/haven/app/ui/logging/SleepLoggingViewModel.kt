package com.haven.app.ui.logging

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.haven.app.data.entity.Entry
import com.haven.app.data.repository.EntryRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import javax.inject.Inject

data class SleepUiState(
    val hours: String = "",
    val notes: String = "",
    val saved: Boolean = false
) {
    val canSave: Boolean get() = hours.toDoubleOrNull() != null && hours.toDouble() > 0
}

@HiltViewModel
class SleepLoggingViewModel @Inject constructor(
    private val entryRepository: EntryRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(SleepUiState())
    val uiState: StateFlow<SleepUiState> = _uiState.asStateFlow()

    fun updateHours(value: String) {
        if (value.isEmpty() || value.toDoubleOrNull() != null) {
            _uiState.update { it.copy(hours = value) }
        }
    }

    fun updateNotes(value: String) {
        _uiState.update { it.copy(notes = value) }
    }

    fun save(entryTypeId: Long) {
        val state = _uiState.value
        val hours = state.hours.toDoubleOrNull() ?: return

        viewModelScope.launch {
            val now = Instant.now().atZone(ZoneId.systemDefault())
            val timestamp = DateTimeFormatter.ISO_OFFSET_DATE_TIME.format(now)

            entryRepository.insert(
                Entry(
                    entryTypeId = entryTypeId,
                    sourceType = "log",
                    timestamp = timestamp,
                    createdAt = timestamp,
                    numericValue = hours,
                    notes = state.notes.ifBlank { null }
                )
            )
            _uiState.update { it.copy(saved = true) }
        }
    }
}

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
import java.time.LocalDate
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import javax.inject.Inject

data class HydrationUiState(
    val customAmount: String = "",
    val dailyTotal: Double = 0.0,
) {
    val canSaveCustom: Boolean get() = customAmount.toDoubleOrNull() != null && customAmount.toDouble() > 0
}

@HiltViewModel
class HydrationLoggingViewModel @Inject constructor(
    private val entryRepository: EntryRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(HydrationUiState())
    val uiState: StateFlow<HydrationUiState> = _uiState.asStateFlow()

    fun startObservingDailyTotal(entryTypeId: Long) {
        viewModelScope.launch {
            val zone = ZoneId.systemDefault()
            val today = LocalDate.now(zone)
            val dayStart = today.atStartOfDay(zone).format(DateTimeFormatter.ISO_OFFSET_DATE_TIME)
            val dayEnd = today.plusDays(1).atStartOfDay(zone).format(DateTimeFormatter.ISO_OFFSET_DATE_TIME)

            entryRepository.getDailyTotal(entryTypeId, dayStart, dayEnd).collect { total ->
                _uiState.update { it.copy(dailyTotal = total) }
            }
        }
    }

    fun updateCustomAmount(value: String) {
        if (value.isEmpty() || value.toDoubleOrNull() != null) {
            _uiState.update { it.copy(customAmount = value) }
        }
    }

    fun quickAdd(entryTypeId: Long, amount: Double) {
        viewModelScope.launch {
            insertEntry(entryTypeId, amount)
        }
    }

    fun saveCustom(entryTypeId: Long) {
        val amount = _uiState.value.customAmount.toDoubleOrNull() ?: return
        viewModelScope.launch {
            insertEntry(entryTypeId, amount)
            _uiState.update { it.copy(customAmount = "") }
        }
    }

    private suspend fun insertEntry(entryTypeId: Long, amount: Double) {
        val now = Instant.now().atZone(ZoneId.systemDefault())
        val timestamp = DateTimeFormatter.ISO_OFFSET_DATE_TIME.format(now)

        entryRepository.insert(
            Entry(
                entryTypeId = entryTypeId,
                sourceType = "log",
                timestamp = timestamp,
                createdAt = timestamp,
                numericValue = amount
            )
        )
    }
}

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

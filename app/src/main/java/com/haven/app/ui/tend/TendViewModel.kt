package com.haven.app.ui.tend

import androidx.lifecycle.ViewModel
import com.haven.app.data.entity.EntryTypeEntity
import com.haven.app.data.repository.EntryTypeRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

@HiltViewModel
class TendViewModel @Inject constructor(
    private val entryTypeRepository: EntryTypeRepository
) : ViewModel() {

    val entryTypes: Flow<List<EntryTypeEntity>> = entryTypeRepository.getEnabled()
}

package com.haven.app.ui.logging

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import com.haven.app.data.entity.EntryType
import com.haven.app.data.repository.EntryTypeRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

@HiltViewModel
class LoggingRouteViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val entryTypeRepository: EntryTypeRepository
) : ViewModel() {
    private val entryTypeId: Long = savedStateHandle.get<Long>("entryTypeId") ?: 0L

    val entryType: Flow<EntryType?> = flow {
        emit(entryTypeRepository.getById(entryTypeId))
    }
}

@Composable
fun LoggingRoute(
    entryTypeId: Long,
    onSaved: () -> Unit,
    onBack: () -> Unit,
    viewModel: LoggingRouteViewModel = hiltViewModel()
) {
    val entryType by viewModel.entryType.collectAsState(initial = null)

    when (val type = entryType) {
        null -> {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        }
        else -> when (type.name) {
            "Sleep" -> SleepLoggingScreen(entryTypeId = type.id, onSaved = onSaved, onBack = onBack)
            "Hydration" -> HydrationLoggingScreen(entryTypeId = type.id, onSaved = onSaved, onBack = onBack)
            "Food" -> FoodLoggingScreen(entryTypeId = type.id, onSaved = onSaved, onBack = onBack)
            else -> {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("${type.name} logging coming soon")
                }
            }
        }
    }
}

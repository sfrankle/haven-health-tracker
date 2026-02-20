package com.haven.app.ui.tend

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.haven.app.data.entity.EntryTypeEntity
import com.haven.app.ui.common.GradientScaffold
import com.haven.app.ui.common.entryTypeIcon
import com.haven.app.ui.navigation.HavenDestination
import com.haven.app.ui.theme.entryTypeTint
import com.haven.app.ui.theme.tabGradient

@Composable
fun TendScreen(
    viewModel: TendViewModel = hiltViewModel(),
    onEntryTypeClick: (EntryTypeEntity) -> Unit = {}
) {
    val entryTypes by viewModel.entryTypes.collectAsState(initial = emptyList())

    GradientScaffold(gradient = tabGradient(HavenDestination.Tend)) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            Text(
                text = "Log",
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f)
            )
            Text(
                text = "What would you like to log?",
                style = MaterialTheme.typography.headlineMedium,
                color = MaterialTheme.colorScheme.onBackground
            )
            Spacer(modifier = Modifier.height(24.dp))
            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                contentPadding = PaddingValues(4.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(entryTypes, key = { it.id }) { entryType ->
                    EntryTypeButton(
                        entryType = entryType,
                        onClick = { onEntryTypeClick(entryType) }
                    )
                }
            }
        }
    }
}

@Composable
private fun EntryTypeButton(
    entryType: EntryTypeEntity,
    onClick: () -> Unit
) {
    Button(
        onClick = onClick,
        shape = MaterialTheme.shapes.large,
        colors = ButtonDefaults.buttonColors(
            containerColor = entryTypeTint(entryType.icon),
            contentColor = MaterialTheme.colorScheme.onBackground
        ),
        modifier = Modifier
            .fillMaxWidth()
            .height(100.dp)
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Icon(
                imageVector = entryTypeIcon(entryType.icon),
                contentDescription = entryType.name,
                modifier = Modifier.size(32.dp)
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = entryType.name,
                style = MaterialTheme.typography.titleSmall
            )
        }
    }
}

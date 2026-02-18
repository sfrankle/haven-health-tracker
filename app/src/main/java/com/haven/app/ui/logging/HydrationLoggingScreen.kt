package com.haven.app.ui.logging

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.haven.app.ui.common.PillButton

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HydrationLoggingScreen(
    entryTypeId: Long,
    onSaved: () -> Unit,
    onBack: () -> Unit,
    viewModel: HydrationLoggingViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(entryTypeId) {
        viewModel.startObservingDailyTotal(entryTypeId)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Log Hydration") },
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
                .padding(16.dp)
        ) {
            Text(
                text = "Today's total: ${uiState.dailyTotal.toInt()} oz",
                style = MaterialTheme.typography.headlineMedium,
                color = MaterialTheme.colorScheme.primary
            )
            Spacer(modifier = Modifier.height(24.dp))
            Text(
                text = "Quick add",
                style = MaterialTheme.typography.titleMedium
            )
            Spacer(modifier = Modifier.height(12.dp))
            Row(
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                PillButton(
                    onClick = { viewModel.quickAdd(entryTypeId, 8.0) },
                    modifier = Modifier.weight(1f)
                ) {
                    Text("+8 oz")
                }
                PillButton(
                    onClick = { viewModel.quickAdd(entryTypeId, 16.0) },
                    modifier = Modifier.weight(1f)
                ) {
                    Text("+16 oz")
                }
            }
            Spacer(modifier = Modifier.height(24.dp))
            Text(
                text = "Custom amount",
                style = MaterialTheme.typography.titleMedium
            )
            Spacer(modifier = Modifier.height(12.dp))
            Row(
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                OutlinedTextField(
                    value = uiState.customAmount,
                    onValueChange = viewModel::updateCustomAmount,
                    label = { Text("oz") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    singleLine = true,
                    modifier = Modifier.weight(1f)
                )
                OutlinedButton(
                    onClick = { viewModel.saveCustom(entryTypeId) },
                    enabled = uiState.canSaveCustom,
                    shape = MaterialTheme.shapes.large
                ) {
                    Text("Add")
                }
            }
            Spacer(modifier = Modifier.height(24.dp))
            PillButton(
                onClick = onBack,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Done")
            }
        }
    }
}

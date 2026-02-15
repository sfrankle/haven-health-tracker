package com.haven.app.ui.trace

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
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

@OptIn(ExperimentalFoundationApi::class)
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
                    text = "No entries yet \u2014 head to Tend to start logging",
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
 * E.g., "I slept 7.5 hours" -> ("I slept ", "7.5 hours")
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

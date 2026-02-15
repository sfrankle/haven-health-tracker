package com.haven.app.ui.trace

import com.haven.app.data.model.EntryWithDetails

fun entrySummary(entry: EntryWithDetails): String {
    val value = entry.numericValue
    val labels = entry.labelNames

    return when (entry.entryTypeName) {
        "Sleep" -> "I slept ${formatNumber(value)} hours"
        "Hydration" -> "I drank ${formatNumber(value)} oz"
        "Food" -> "I ate ${labels ?: "something"}"
        "Emotion" -> "I felt ${labels ?: "something"}"
        "Symptom" -> "I experienced ${labels ?: "something"}"
        "Activity" -> formatActivity(labels)
        else -> "Logged ${entry.entryTypeName}"
    }
}

private fun formatNumber(value: Double?): String {
    if (value == null) return "0"
    return if (value == value.toLong().toDouble()) {
        value.toLong().toString()
    } else {
        value.toString()
    }
}

private fun formatActivity(labels: String?): String {
    if (labels == null) return "I did something"
    return if (',' in labels) {
        "I did $labels"
    } else {
        "I ${labels.lowercase()}"
    }
}

package com.haven.app.ui.trace

import com.haven.app.data.model.EntryWithDetails

data class EntrySummaryParts(val prefix: String, val bold: String) {
    val full: String get() = "$prefix$bold"
}

fun entrySummaryParts(entry: EntryWithDetails): EntrySummaryParts {
    val value = entry.numericValue
    val labels = entry.labelNames

    return when (entry.entryTypeName) {
        "Sleep" -> EntrySummaryParts("I slept ", "${formatNumber(value)} hours")
        "Hydration" -> EntrySummaryParts("I drank ", "${formatNumber(value)} oz")
        "Food" -> EntrySummaryParts("I ate ", labels ?: "something")
        "Emotion" -> EntrySummaryParts("I felt ", labels ?: "something")
        "Symptom" -> EntrySummaryParts("I experienced ", labels ?: "something")
        "Activity" -> formatActivity(labels)
        else -> EntrySummaryParts("", "Logged ${entry.entryTypeName}")
    }
}

fun entrySummary(entry: EntryWithDetails): String = entrySummaryParts(entry).full

private fun formatNumber(value: Double?): String {
    if (value == null) return "\u2014"
    return if (value == value.toLong().toDouble()) {
        value.toLong().toString()
    } else {
        value.toString()
    }
}

private fun formatActivity(labels: String?): EntrySummaryParts {
    if (labels == null) return EntrySummaryParts("I did ", "something")
    return if (", " in labels) {
        EntrySummaryParts("I did ", labels)
    } else {
        EntrySummaryParts("I ", labels.lowercase())
    }
}

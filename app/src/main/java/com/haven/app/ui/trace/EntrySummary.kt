package com.haven.app.ui.trace

import com.haven.app.data.entity.EntryType
import com.haven.app.data.model.EntryWithDetails

data class EntrySummaryParts(val prefix: String, val bold: String) {
    val full: String get() = "$prefix$bold"
}

fun entrySummaryParts(entry: EntryWithDetails): EntrySummaryParts {
    val value = entry.numericValue
    val labels = entry.labelNames

    return when (entry.entryType) {
        EntryType.SLEEP -> EntrySummaryParts("I slept ", "${formatNumber(value)} hours")
        EntryType.HYDRATION -> EntrySummaryParts("I drank ", "${formatNumber(value)} oz")
        EntryType.FOOD -> EntrySummaryParts("I ate ", labels ?: "something")
        EntryType.EMOTION -> EntrySummaryParts("I felt ", labels ?: "something")
        EntryType.SYMPTOM -> EntrySummaryParts("I experienced ", labels ?: "something")
        EntryType.ACTIVITY -> formatActivity(labels)
        null -> EntrySummaryParts("", "Logged entry")
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

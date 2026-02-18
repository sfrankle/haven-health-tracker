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
        EntryType.FOOD -> formatFood(labels)
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

private val MEAL_SOURCE_LABELS = setOf("Home Cooked", "Eating Out")

private fun formatFood(labels: String?): EntrySummaryParts {
    if (labels == null) return EntrySummaryParts("I ate ", "something")
    val parts = labels.split(", ")
    val foodItems = parts.filter { it !in MEAL_SOURCE_LABELS }
    val mealSource = parts.firstOrNull { it in MEAL_SOURCE_LABELS }
    val foodText = if (foodItems.isNotEmpty()) foodItems.joinToString(", ") { it.lowercase() } else "something"
    val suffix = when (mealSource) {
        "Eating Out" -> " while eating out"
        "Home Cooked" -> " at home"
        else -> ""
    }
    return EntrySummaryParts("I ate ", "$foodText$suffix")
}

private fun formatActivity(labels: String?): EntrySummaryParts {
    if (labels == null) return EntrySummaryParts("I did ", "something")
    return if (", " in labels) {
        EntrySummaryParts("I did ", labels)
    } else {
        EntrySummaryParts("I ", labels.lowercase())
    }
}

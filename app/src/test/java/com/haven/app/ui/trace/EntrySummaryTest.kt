package com.haven.app.ui.trace

import com.haven.app.data.entity.EntryTypeIcon
import com.haven.app.data.model.EntryWithDetails
import org.junit.Assert.assertEquals
import org.junit.Test

class EntrySummaryTest {

    private fun entry(
        entryTypeName: String,
        entryTypeIcon: EntryTypeIcon? = null,
        numericValue: Double? = null,
        labelNames: String? = null
    ) = EntryWithDetails(
        id = 1,
        entryTypeId = 1,
        entryTypeName = entryTypeName,
        entryTypeIcon = entryTypeIcon,
        sourceType = "log",
        timestamp = "2026-02-14T08:00:00-05:00",
        createdAt = "2026-02-14T08:00:00-05:00",
        numericValue = numericValue,
        notes = null,
        labelNames = labelNames
    )

    @Test
    fun `sleep entry shows hours`() {
        assertEquals(
            "I slept 7.5 hours",
            entrySummary(entry("Sleep", numericValue = 7.5))
        )
    }

    @Test
    fun `sleep entry with whole number omits decimal`() {
        assertEquals(
            "I slept 8 hours",
            entrySummary(entry("Sleep", numericValue = 8.0))
        )
    }

    @Test
    fun `hydration entry shows oz`() {
        assertEquals(
            "I drank 16 oz",
            entrySummary(entry("Hydration", numericValue = 16.0))
        )
    }

    @Test
    fun `food entry shows labels`() {
        assertEquals(
            "I ate Eggs, Toast",
            entrySummary(entry("Food", labelNames = "Eggs, Toast"))
        )
    }

    @Test
    fun `emotion entry shows labels`() {
        assertEquals(
            "I felt Content, Calm",
            entrySummary(entry("Emotion", labelNames = "Content, Calm"))
        )
    }

    @Test
    fun `symptom entry shows labels`() {
        assertEquals(
            "I experienced Headache",
            entrySummary(entry("Symptom", labelNames = "Headache"))
        )
    }

    @Test
    fun `activity entry with single label shows as verb`() {
        assertEquals(
            "I hiked",
            entrySummary(entry("Activity", labelNames = "Hiked"))
        )
    }

    @Test
    fun `activity entry with multiple labels`() {
        assertEquals(
            "I did Yoga, Stretching",
            entrySummary(entry("Activity", labelNames = "Yoga, Stretching"))
        )
    }

    @Test
    fun `unknown entry type shows generic summary`() {
        assertEquals(
            "Logged Custom",
            entrySummary(entry("Custom"))
        )
    }
}

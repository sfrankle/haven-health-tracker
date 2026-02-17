package com.haven.app.ui.trace

import com.haven.app.data.entity.EntryType
import com.haven.app.data.model.EntryWithDetails
import org.junit.Assert.assertEquals
import org.junit.Test

class EntrySummaryTest {

    private fun entry(
        entryType: EntryType? = null,
        numericValue: Double? = null,
        labelNames: String? = null
    ) = EntryWithDetails(
        id = 1,
        entryTypeId = 1,
        entryType = entryType,
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
            entrySummary(entry(EntryType.SLEEP, numericValue = 7.5))
        )
    }

    @Test
    fun `sleep entry with whole number omits decimal`() {
        assertEquals(
            "I slept 8 hours",
            entrySummary(entry(EntryType.SLEEP, numericValue = 8.0))
        )
    }

    @Test
    fun `hydration entry shows oz`() {
        assertEquals(
            "I drank 16 oz",
            entrySummary(entry(EntryType.HYDRATION, numericValue = 16.0))
        )
    }

    @Test
    fun `food entry shows labels`() {
        assertEquals(
            "I ate Eggs, Toast",
            entrySummary(entry(EntryType.FOOD, labelNames = "Eggs, Toast"))
        )
    }

    @Test
    fun `emotion entry shows labels`() {
        assertEquals(
            "I felt Content, Calm",
            entrySummary(entry(EntryType.EMOTION, labelNames = "Content, Calm"))
        )
    }

    @Test
    fun `symptom entry shows labels`() {
        assertEquals(
            "I experienced Headache",
            entrySummary(entry(EntryType.SYMPTOM, labelNames = "Headache"))
        )
    }

    @Test
    fun `activity entry with single label shows as verb`() {
        assertEquals(
            "I hiked",
            entrySummary(entry(EntryType.ACTIVITY, labelNames = "Hiked"))
        )
    }

    @Test
    fun `activity entry with multiple labels`() {
        assertEquals(
            "I did Yoga, Stretching",
            entrySummary(entry(EntryType.ACTIVITY, labelNames = "Yoga, Stretching"))
        )
    }

    @Test
    fun `unknown entry type shows generic summary`() {
        assertEquals(
            "Logged entry",
            entrySummary(entry(null))
        )
    }

    @Test
    fun `sleep entry with null value shows em dash`() {
        assertEquals(
            "I slept \u2014 hours",
            entrySummary(entry(EntryType.SLEEP))
        )
    }

    @Test
    fun `hydration entry with null value shows em dash`() {
        assertEquals(
            "I drank \u2014 oz",
            entrySummary(entry(EntryType.HYDRATION))
        )
    }

    @Test
    fun `food entry with null labels shows something`() {
        assertEquals(
            "I ate something",
            entrySummary(entry(EntryType.FOOD))
        )
    }

    @Test
    fun `emotion entry with null labels shows something`() {
        assertEquals(
            "I felt something",
            entrySummary(entry(EntryType.EMOTION))
        )
    }

    @Test
    fun `activity entry with null labels shows something`() {
        assertEquals(
            "I did something",
            entrySummary(entry(EntryType.ACTIVITY))
        )
    }

    @Test
    fun `entrySummaryParts returns correct prefix and bold`() {
        val parts = entrySummaryParts(entry(EntryType.SLEEP, numericValue = 7.5))
        assertEquals("I slept ", parts.prefix)
        assertEquals("7.5 hours", parts.bold)
        assertEquals("I slept 7.5 hours", parts.full)
    }
}

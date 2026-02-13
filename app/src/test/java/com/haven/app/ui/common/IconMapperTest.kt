package com.haven.app.ui.common

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Bedtime
import androidx.compose.material.icons.rounded.FitnessCenter
import androidx.compose.material.icons.rounded.MonitorHeart
import androidx.compose.material.icons.rounded.Mood
import androidx.compose.material.icons.rounded.Restaurant
import androidx.compose.material.icons.rounded.WaterDrop
import org.junit.Assert.assertEquals
import org.junit.Test

class IconMapperTest {
    @Test
    fun `maps known icon keys to correct icons`() {
        assertEquals(Icons.Rounded.Restaurant, entryTypeIcon("food"))
        assertEquals(Icons.Rounded.Mood, entryTypeIcon("emotion"))
        assertEquals(Icons.Rounded.WaterDrop, entryTypeIcon("hydration"))
        assertEquals(Icons.Rounded.Bedtime, entryTypeIcon("sleep"))
        assertEquals(Icons.Rounded.MonitorHeart, entryTypeIcon("symptom"))
        assertEquals(Icons.Rounded.FitnessCenter, entryTypeIcon("activity"))
    }

    @Test
    fun `returns default icon for unknown key`() {
        val icon = entryTypeIcon("unknown")
        assertEquals(Icons.Rounded.FitnessCenter, icon)
    }

    @Test
    fun `returns default icon for null key`() {
        val icon = entryTypeIcon(null)
        assertEquals(Icons.Rounded.FitnessCenter, icon)
    }
}

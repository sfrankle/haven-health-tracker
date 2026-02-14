package com.haven.app.ui.common

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Bedtime
import androidx.compose.material.icons.rounded.FitnessCenter
import androidx.compose.material.icons.rounded.MonitorHeart
import androidx.compose.material.icons.rounded.Mood
import androidx.compose.material.icons.rounded.Restaurant
import androidx.compose.material.icons.rounded.WaterDrop
import com.haven.app.data.entity.EntryTypeIcon
import org.junit.Assert.assertEquals
import org.junit.Test

class IconMapperTest {
    @Test
    fun `maps known icon keys to correct icons`() {
        assertEquals(Icons.Rounded.Restaurant, entryTypeIcon(EntryTypeIcon.FOOD))
        assertEquals(Icons.Rounded.Mood, entryTypeIcon(EntryTypeIcon.EMOTION))
        assertEquals(Icons.Rounded.WaterDrop, entryTypeIcon(EntryTypeIcon.HYDRATION))
        assertEquals(Icons.Rounded.Bedtime, entryTypeIcon(EntryTypeIcon.SLEEP))
        assertEquals(Icons.Rounded.MonitorHeart, entryTypeIcon(EntryTypeIcon.SYMPTOM))
        assertEquals(Icons.Rounded.FitnessCenter, entryTypeIcon(EntryTypeIcon.ACTIVITY))
    }

    @Test
    fun `returns default icon for null key`() {
        val icon = entryTypeIcon(null)
        assertEquals(Icons.Rounded.FitnessCenter, icon)
    }
}

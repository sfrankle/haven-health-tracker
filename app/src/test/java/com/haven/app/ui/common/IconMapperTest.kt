package com.haven.app.ui.common

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Bedtime
import androidx.compose.material.icons.rounded.Interests
import androidx.compose.material.icons.rounded.KebabDining
import androidx.compose.material.icons.rounded.Mood
import androidx.compose.material.icons.rounded.Piano
import androidx.compose.material.icons.rounded.Sick
import androidx.compose.material.icons.rounded.WaterDrop
import com.haven.app.data.entity.EntryType
import org.junit.Assert.assertEquals
import org.junit.Test

class IconMapperTest {
    @Test
    fun `maps known icon keys to correct icons`() {
        assertEquals(Icons.Rounded.KebabDining, entryTypeIcon(EntryType.FOOD))
        assertEquals(Icons.Rounded.Mood, entryTypeIcon(EntryType.EMOTION))
        assertEquals(Icons.Rounded.WaterDrop, entryTypeIcon(EntryType.HYDRATION))
        assertEquals(Icons.Rounded.Bedtime, entryTypeIcon(EntryType.SLEEP))
        assertEquals(Icons.Rounded.Sick, entryTypeIcon(EntryType.SYMPTOM))
        assertEquals(Icons.Rounded.Interests, entryTypeIcon(EntryType.ACTIVITY))
    }

    @Test
    fun `returns default icon for null key`() {
        val icon = entryTypeIcon(null)
        assertEquals(Icons.Rounded.Piano, icon)
    }
}

package com.haven.app.ui.common

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Bedtime
import androidx.compose.material.icons.rounded.Interests
import androidx.compose.material.icons.rounded.KebabDining
import androidx.compose.material.icons.rounded.Mood
import androidx.compose.material.icons.rounded.Piano
import androidx.compose.material.icons.rounded.Sick
import androidx.compose.material.icons.rounded.WaterDrop
import com.haven.app.data.entity.EntryTypeIcon
import org.junit.Assert.assertEquals
import org.junit.Test

class IconMapperTest {
    @Test
    fun `maps known icon keys to correct icons`() {
        assertEquals(Icons.Rounded.KebabDining, entryTypeIcon(EntryTypeIcon.FOOD))
        assertEquals(Icons.Rounded.Mood, entryTypeIcon(EntryTypeIcon.EMOTION))
        assertEquals(Icons.Rounded.WaterDrop, entryTypeIcon(EntryTypeIcon.HYDRATION))
        assertEquals(Icons.Rounded.Bedtime, entryTypeIcon(EntryTypeIcon.SLEEP))
        assertEquals(Icons.Rounded.Sick, entryTypeIcon(EntryTypeIcon.SYMPTOM))
        assertEquals(Icons.Rounded.Interests, entryTypeIcon(EntryTypeIcon.ACTIVITY))
    }

    @Test
    fun `returns default icon for null key`() {
        val icon = entryTypeIcon(null)
        assertEquals(Icons.Rounded.Piano, icon)
    }
}

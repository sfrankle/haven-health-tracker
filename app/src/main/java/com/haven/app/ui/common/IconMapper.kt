package com.haven.app.ui.common

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Bedtime
import androidx.compose.material.icons.rounded.Interests
import androidx.compose.material.icons.rounded.LocalDining
import androidx.compose.material.icons.rounded.Mood
import androidx.compose.material.icons.rounded.Piano
import androidx.compose.material.icons.rounded.Sick
import androidx.compose.material.icons.rounded.WaterDrop
import androidx.compose.ui.graphics.vector.ImageVector
import com.haven.app.data.entity.EntryType

fun entryTypeIcon(icon: EntryType?): ImageVector = when (icon) {
    EntryType.FOOD -> Icons.Rounded.LocalDining
    EntryType.EMOTION -> Icons.Rounded.Mood
    EntryType.HYDRATION -> Icons.Rounded.WaterDrop
    EntryType.SLEEP -> Icons.Rounded.Bedtime
    EntryType.SYMPTOM -> Icons.Rounded.Sick
    EntryType.ACTIVITY -> Icons.Rounded.Interests
    null -> Icons.Rounded.Piano
}

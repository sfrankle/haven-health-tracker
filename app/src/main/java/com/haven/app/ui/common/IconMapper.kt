package com.haven.app.ui.common

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Bedtime
import androidx.compose.material.icons.rounded.FitnessCenter
import androidx.compose.material.icons.rounded.MonitorHeart
import androidx.compose.material.icons.rounded.Mood
import androidx.compose.material.icons.rounded.Restaurant
import androidx.compose.material.icons.rounded.WaterDrop
import androidx.compose.ui.graphics.vector.ImageVector
import com.haven.app.data.entity.EntryTypeIcon

fun entryTypeIcon(icon: EntryTypeIcon?): ImageVector = when (icon) {
    EntryTypeIcon.FOOD -> Icons.Rounded.Restaurant
    EntryTypeIcon.EMOTION -> Icons.Rounded.Mood
    EntryTypeIcon.HYDRATION -> Icons.Rounded.WaterDrop
    EntryTypeIcon.SLEEP -> Icons.Rounded.Bedtime
    EntryTypeIcon.SYMPTOM -> Icons.Rounded.MonitorHeart
    EntryTypeIcon.ACTIVITY -> Icons.Rounded.FitnessCenter
    null -> Icons.Rounded.FitnessCenter
}

package com.haven.app.ui.common

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Bedtime
import androidx.compose.material.icons.rounded.FitnessCenter
import androidx.compose.material.icons.rounded.HeartBroken
import androidx.compose.material.icons.rounded.Interests
import androidx.compose.material.icons.rounded.KebabDining
import androidx.compose.material.icons.rounded.MonitorHeart
import androidx.compose.material.icons.rounded.Mood
import androidx.compose.material.icons.rounded.Piano
import androidx.compose.material.icons.rounded.Restaurant
import androidx.compose.material.icons.rounded.SelfImprovement
import androidx.compose.material.icons.rounded.Sick
import androidx.compose.material.icons.rounded.WaterDrop
import androidx.compose.ui.graphics.vector.ImageVector
import com.haven.app.data.entity.EntryTypeIcon

fun entryTypeIcon(icon: EntryTypeIcon?): ImageVector = when (icon) {
    EntryTypeIcon.FOOD -> Icons.Rounded.KebabDining
    EntryTypeIcon.EMOTION -> Icons.Rounded.Mood
    EntryTypeIcon.HYDRATION -> Icons.Rounded.WaterDrop
    EntryTypeIcon.SLEEP -> Icons.Rounded.Bedtime
    EntryTypeIcon.SYMPTOM -> Icons.Rounded.Sick
    EntryTypeIcon.ACTIVITY -> Icons.Rounded.Interests
    null -> Icons.Rounded.Piano
}

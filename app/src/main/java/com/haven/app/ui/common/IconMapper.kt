package com.haven.app.ui.common

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Bedtime
import androidx.compose.material.icons.rounded.FitnessCenter
import androidx.compose.material.icons.rounded.MonitorHeart
import androidx.compose.material.icons.rounded.Mood
import androidx.compose.material.icons.rounded.Restaurant
import androidx.compose.material.icons.rounded.WaterDrop
import androidx.compose.ui.graphics.vector.ImageVector

fun entryTypeIcon(iconKey: String?): ImageVector = when (iconKey) {
    "food" -> Icons.Rounded.Restaurant
    "emotion" -> Icons.Rounded.Mood
    "hydration" -> Icons.Rounded.WaterDrop
    "sleep" -> Icons.Rounded.Bedtime
    "symptom" -> Icons.Rounded.MonitorHeart
    "activity" -> Icons.Rounded.FitnessCenter
    else -> Icons.Rounded.FitnessCenter
}

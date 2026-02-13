package com.haven.app.ui.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Anchor
import androidx.compose.material.icons.rounded.Favorite
import androidx.compose.material.icons.rounded.History
import androidx.compose.material.icons.rounded.Insights
import androidx.compose.material.icons.rounded.Settings
import androidx.compose.ui.graphics.vector.ImageVector

enum class HavenDestination(
    val label: String,
    val icon: ImageVector,
    val route: String
) {
    Tend("Tend", Icons.Rounded.Favorite, "tend"),
    Trace("Trace", Icons.Rounded.History, "trace"),
    Weave("Weave", Icons.Rounded.Insights, "weave"),
    Anchor("Anchor", Icons.Rounded.Anchor, "anchor"),
    Settings("Settings", Icons.Rounded.Settings, "settings"),
}

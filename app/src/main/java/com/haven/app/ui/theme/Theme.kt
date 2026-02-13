package com.haven.app.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

private val HavenColorScheme = lightColorScheme(
    primary = SageGreen,
    onPrimary = OffWhite,
    primaryContainer = SageGreenLight,
    onPrimaryContainer = SageGreenDark,
    secondary = Lavender,
    onSecondary = SoftBlack,
    secondaryContainer = LavenderLight,
    onSecondaryContainer = LavenderDark,
    background = OffWhite,
    onBackground = SoftBlack,
    surface = OffWhite,
    onSurface = SoftBlack,
    surfaceVariant = LavenderLight,
    onSurfaceVariant = WarmGray,
    outline = SageGreenLight
)

@Composable
fun HavenTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = HavenColorScheme,
        typography = HavenTypography,
        shapes = HavenShapes,
        content = content
    )
}

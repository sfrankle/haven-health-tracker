package com.haven.app.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

private val HavenColorScheme = lightColorScheme(
    primary = FoodSage,
    onPrimary = OffWhite,
    primaryContainer = FoodGreen,
    onPrimaryContainer = SoftBlack,
    secondary = SleepLavender,
    onSecondary = SoftBlack,
    secondaryContainer = SleepDustyBlue,
    onSecondaryContainer = SleepIndigo,
    background = OffWhite,
    onBackground = SoftBlack,
    surface = OffWhite,
    onSurface = SoftBlack,
    surfaceVariant = SleepDustyBlue,
    onSurfaceVariant = WarmGray,
    outline = FoodGreen
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

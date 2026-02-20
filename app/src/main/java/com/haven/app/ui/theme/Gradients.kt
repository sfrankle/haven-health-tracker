package com.haven.app.ui.theme

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import com.haven.app.data.entity.EntryType
import com.haven.app.ui.navigation.HavenDestination

/** Full-bleed diagonal gradient for each entry type's logging screen. */
fun entryTypeGradient(entryType: EntryType?): Brush = when (entryType) {
    EntryType.HYDRATION -> Brush.linearGradient(
        colors = listOf(HydrationBlue, HydrationTeal, HydrationWhite),
        start = Offset(0f, 0f), end = Offset(Float.POSITIVE_INFINITY, Float.POSITIVE_INFINITY)
    )
    EntryType.FOOD -> Brush.linearGradient(
        colors = listOf(FoodGreen, FoodSage, FoodMint),
        start = Offset(0f, 0f), end = Offset(Float.POSITIVE_INFINITY, Float.POSITIVE_INFINITY)
    )
    EntryType.SLEEP -> Brush.linearGradient(
        colors = listOf(SleepLavender, SleepIndigo, SleepDustyBlue),
        start = Offset(0f, 0f), end = Offset(Float.POSITIVE_INFINITY, Float.POSITIVE_INFINITY)
    )
    EntryType.EMOTION -> Brush.linearGradient(
        colors = listOf(EmotionBlush, EmotionPeach, EmotionCream),
        start = Offset(0f, 0f), end = Offset(Float.POSITIVE_INFINITY, Float.POSITIVE_INFINITY)
    )
    EntryType.SYMPTOM -> Brush.linearGradient(
        colors = listOf(SymptomSand, SymptomAmber, SymptomGold),
        start = Offset(0f, 0f), end = Offset(Float.POSITIVE_INFINITY, Float.POSITIVE_INFINITY)
    )
    EntryType.ACTIVITY -> Brush.linearGradient(
        colors = listOf(ActivityCoral, ActivityRose, ActivityViolet),
        start = Offset(0f, 0f), end = Offset(Float.POSITIVE_INFINITY, Float.POSITIVE_INFINITY)
    )
    null -> tabGradient(HavenDestination.Tend)
}

/** Full-bleed diagonal gradient for each top-level tab. */
fun tabGradient(destination: HavenDestination): Brush = when (destination) {
    HavenDestination.Tend -> Brush.linearGradient(
        colors = listOf(TendCream, TendPeach),
        start = Offset(0f, 0f), end = Offset(Float.POSITIVE_INFINITY, Float.POSITIVE_INFINITY)
    )
    HavenDestination.Trace -> Brush.linearGradient(
        colors = listOf(TraceSlate, TraceBluGrey, TraceNearWhite),
        start = Offset(0f, 0f), end = Offset(Float.POSITIVE_INFINITY, Float.POSITIVE_INFINITY)
    )
    HavenDestination.Weave -> Brush.linearGradient(
        colors = listOf(WeaveAmber, WeaveHoney, WeaveGold),
        start = Offset(0f, 0f), end = Offset(Float.POSITIVE_INFINITY, Float.POSITIVE_INFINITY)
    )
    HavenDestination.Anchor -> Brush.linearGradient(
        colors = listOf(AnchorBlush, AnchorMauve, AnchorWarmWhite),
        start = Offset(0f, 0f), end = Offset(Float.POSITIVE_INFINITY, Float.POSITIVE_INFINITY)
    )
    HavenDestination.Settings -> Brush.linearGradient(
        colors = listOf(OffWhite, PaleGrey),
        start = Offset(0f, 0f), end = Offset(Float.POSITIVE_INFINITY, Float.POSITIVE_INFINITY)
    )
}

/** Pale tint color (first palette color at low alpha) for card/surface backgrounds. */
fun entryTypeTint(entryType: EntryType?): androidx.compose.ui.graphics.Color = when (entryType) {
    EntryType.HYDRATION -> HydrationBlue.copy(alpha = 0.15f)
    EntryType.FOOD -> FoodGreen.copy(alpha = 0.15f)
    EntryType.SLEEP -> SleepLavender.copy(alpha = 0.15f)
    EntryType.EMOTION -> EmotionBlush.copy(alpha = 0.15f)
    EntryType.SYMPTOM -> SymptomSand.copy(alpha = 0.15f)
    EntryType.ACTIVITY -> ActivityCoral.copy(alpha = 0.15f)
    null -> TendPeach.copy(alpha = 0.15f)
}

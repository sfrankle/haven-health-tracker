package com.haven.app.ui.theme

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import com.haven.app.data.entity.EntryType
import com.haven.app.ui.navigation.HavenDestination

// Pre-allocated Brush instances — shared across recompositions.
private val diagonal = Offset(0f, 0f) to Offset(Float.POSITIVE_INFINITY, Float.POSITIVE_INFINITY)

private val HydrationGradient = Brush.linearGradient(
    colors = listOf(HydrationBlue, HydrationTeal, HydrationWhite),
    start = diagonal.first, end = diagonal.second
)
private val FoodGradient = Brush.linearGradient(
    colors = listOf(FoodGreen, FoodSage, FoodMint),
    start = diagonal.first, end = diagonal.second
)
private val SleepGradient = Brush.linearGradient(
    colors = listOf(SleepLavender, SleepIndigo, SleepDustyBlue),
    start = diagonal.first, end = diagonal.second
)
private val EmotionGradient = Brush.linearGradient(
    colors = listOf(EmotionBlush, EmotionPeach, EmotionCream),
    start = diagonal.first, end = diagonal.second
)
private val SymptomGradient = Brush.linearGradient(
    colors = listOf(SymptomSand, SymptomAmber, SymptomGold),
    start = diagonal.first, end = diagonal.second
)
private val ActivityGradient = Brush.linearGradient(
    colors = listOf(ActivityCoral, ActivityRose, ActivityViolet),
    start = diagonal.first, end = diagonal.second
)
private val TendGradient = Brush.linearGradient(
    colors = listOf(TendCream, TendPeach),
    start = diagonal.first, end = diagonal.second
)
private val TraceGradient = Brush.linearGradient(
    colors = listOf(TraceSlate, TraceBlueGrey, TraceNearWhite),
    start = diagonal.first, end = diagonal.second
)
private val WeaveGradient = Brush.linearGradient(
    colors = listOf(WeaveAmber, WeaveHoney, WeaveGold),
    start = diagonal.first, end = diagonal.second
)
private val AnchorGradient = Brush.linearGradient(
    colors = listOf(AnchorBlush, AnchorMauve, AnchorWarmWhite),
    start = diagonal.first, end = diagonal.second
)
private val SettingsGradient = Brush.linearGradient(
    colors = listOf(OffWhite, PaleGrey),
    start = diagonal.first, end = diagonal.second
)

/** Full-bleed diagonal gradient for each entry type's logging screen. */
fun entryTypeGradient(entryType: EntryType?): Brush = when (entryType) {
    EntryType.HYDRATION -> HydrationGradient
    EntryType.FOOD      -> FoodGradient
    EntryType.SLEEP     -> SleepGradient
    EntryType.EMOTION   -> EmotionGradient
    EntryType.SYMPTOM   -> SymptomGradient
    EntryType.ACTIVITY  -> ActivityGradient
    null                -> TendGradient // fallback for custom entry types with no icon mapping
}

/** Full-bleed diagonal gradient for each top-level tab. */
fun tabGradient(destination: HavenDestination): Brush = when (destination) {
    HavenDestination.Tend     -> TendGradient
    HavenDestination.Trace    -> TraceGradient
    HavenDestination.Weave    -> WeaveGradient
    HavenDestination.Anchor   -> AnchorGradient
    HavenDestination.Settings -> SettingsGradient
}

/** Pale tint color (first palette color at low alpha) for card/surface backgrounds. */
fun entryTypeTint(entryType: EntryType?): Color = when (entryType) {
    EntryType.HYDRATION -> HydrationBlue.copy(alpha = 0.15f)
    EntryType.FOOD      -> FoodGreen.copy(alpha = 0.15f)
    EntryType.SLEEP     -> SleepLavender.copy(alpha = 0.15f)
    EntryType.EMOTION   -> EmotionBlush.copy(alpha = 0.15f)
    EntryType.SYMPTOM   -> SymptomSand.copy(alpha = 0.15f)
    EntryType.ACTIVITY  -> ActivityCoral.copy(alpha = 0.15f)
    null                -> TendPeach.copy(alpha = 0.15f) // fallback for custom entry types with no icon mapping
}

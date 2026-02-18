# PillButton Design

**Date:** 2026-02-18
**Issue:** #4 — Add design system (colors, fonts, pill button)

## Context

The remaining item from issue #4 is a `PillButton` composable. The color scheme, typography, and `HavenTheme` wrapper are already in place. The existing screens use `Button` with `shape = MaterialTheme.shapes.large` (24dp radius) — visually close but not a true pill shape.

## Design

### Component

`ui/common/PillButton.kt` — a single composable wrapping Material3 `Button` with pill shape and Haven primary colors baked in.

```kotlin
@Composable
fun PillButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    content: @Composable RowScope.() -> Unit
)
```

### Shape & Styling

- **Shape:** `CircleShape` (`RoundedCornerShape(50%)`) — fully rounded ends regardless of button height
- **Colors:** `MaterialTheme.colorScheme.primary` container (sage green) / `MaterialTheme.colorScheme.onPrimary` text (off-white) — no hardcoded values, inherits from `HavenTheme`
- **Elevation:** `ButtonDefaults.buttonElevation()` default — produces the soft shadow from the design reference

### Call Sites Updated

Replace `Button(shape = MaterialTheme.shapes.large)` with `PillButton` in:

- `FoodLoggingScreen` — Save button
- `SleepLoggingScreen` — Save button
- `HydrationLoggingScreen` — Save button

### Out of Scope

`TendScreen`'s `EntryTypeButton` is a tall grid tile (icon + label stacked, 100dp) — a distinct pattern, not a pill button. Left unchanged.

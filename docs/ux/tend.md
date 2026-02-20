# UX Spec: Tend Screen (Logging v2)

Version: 1.0
Status: Target spec for MVP UX refactor

## Goal
Enable users to start and complete logging with minimal cognitive load.

Primary success metric:
- User can begin a log from Tend in <= 3 taps.

## Emotional Target
- Airy
- Neutral
- Grounding
- Safe

## Screen Purpose
Tend is the primary quick-entry screen.

Tend is not:
- An analytics dashboard
- A history browser
- A coaching screen

## Layout Specification
### Global
- Bottom navigation is always visible.
- No top app bar.
- Screen uses a vertical column with calm spacing rhythm.

### Spacing Rhythm
- Horizontal page padding: 16dp
- Section gap (major): 24dp
- Element gap (minor): 12dp
- Bottom content padding above nav: at least 16dp

### Zone 1: Orientation (Top)
Content:
- Date label, e.g. "Today, Feb 20"
- Optional one-line neutral helper text

Rules:
- Keep text brief
- No metrics, streaks, or warnings

### Zone 2: Primary Actions (Middle)
Content:
- Grid for entry types (2 columns)
- Each tile includes icon + label

Rules:
- Tile minimum height: 96dp
- Full-width grid with consistent row/column spacing
- Most-used categories should appear first when ranking data is available

Interaction:
- Tap entry type -> open logging flow for that type
- Preferred pattern: modal bottom sheet or full-screen sheet depending on complexity
- Avoid deep multi-step flows for basic entries

## Interaction Rules
- Default path should be tap-first, not keyboard-first
- Back navigation from logging returns user to Tend
- Preserve Tend scroll/state when returning

## Motion
- Soft fade between entry states
- No bounce, no celebratory animations
- Motion should not delay interaction readiness

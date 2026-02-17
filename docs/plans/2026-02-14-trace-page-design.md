# Trace Page — Design

## Goal

Trace is Haven's history page — a scrollable, filterable journal of all logged entries, grouped by day. Primary use case: quickly review entry history, including at a doctor's office ("How long have I had this symptom? Is it getting worse?").

## Data Flow & Architecture

- **TraceViewModel** collects entries from `EntryRepository`, groups by day, sorts days most-recent-first, entries within a day chronologically
- **Filter chips** swap the source — "All" uses `getAllWithDetails()`, specific type uses `getByTypeWithDetails(id)`
- **Lazy loading**: bounded initial load (~50 entries), load more on scroll via `LIMIT/OFFSET` DAO query
- **No aggregation** — every entry is its own row (including hydration) to preserve temporal data for correlation insights
- Display logic (journal text, value formatting) lives in the UI layer

## Entry Display Format

Each entry is one compact row: `[time]  [icon]  [journal summary]`

| Type | Summary | Example |
|------|---------|---------|
| Sleep | I slept **{hours} hours** | I slept **7.5 hours** |
| Hydration | I drank **{oz} oz** | I drank **8 oz** |
| Food | I ate **{labels}** | I ate **Eggs, Toast** |
| Emotion | I felt **{labels}** | I felt **Content, Calm** |
| Symptom | I experienced **{labels}** | I experienced **Headache** |
| Activity | I **{label}** | I **hiked** |

Time shown as `h:mm a` (e.g., "7:30 AM"). Verb and unit keyed off `entryTypeName`. Value only — no notes shown inline.

## Screen Layout

```
┌─────────────────────────────┐
│  [All] [Food] [Sleep] ...   │  ← horizontal scrollable filter chips
├─────────────────────────────┤
│  Today                      │  ← sticky day header
│  7:30 AM  💧 I drank 8 oz   │
│  8:15 AM  🍽️ I ate Eggs     │
│  ...                        │
├─────────────────────────────┤
│  Yesterday                  │
│  ...                        │
├─────────────────────────────┤
│  Wed, Feb 12                │
│  ...                        │
└─────────────────────────────┘
```

- **Day headers**: "Today", "Yesterday", then "EEE, MMM d" format. Sticky headers.
- **Filter chips**: horizontal scroll row, entry type icon + name, highlighted when active
- **Empty state**: "No entries yet" with nudge to Tend page
- **View-only**: no tap action on entries in this PR
- **Lazy loading**: load next batch as user scrolls near bottom

## Files

| Component | File |
|-----------|------|
| TraceViewModel | `ui/trace/TraceViewModel.kt` (new) |
| TraceScreen | `ui/trace/TraceScreen.kt` (replace stub) |
| entrySummary() | `ui/trace/EntrySummary.kt` (new) |
| Paginated DAO query | `data/dao/EntryDao.kt` (modify) |
| Repo method | `data/repository/EntryRepository.kt` (modify) |
| Tests | `TraceViewModelTest.kt`, `EntrySummaryTest.kt` (new) |

# Food Logging — Design

**Date:** 2026-02-15
**Goal:** Add food logging to the Tend page — search bar, label chip grid, time-of-day suggestions, meal source toggle, and notes.

---

## Screen Layout

**FoodLoggingScreen** — single scrollable screen:

1. **Top bar** — "Log Food" title + back arrow
2. **Search bar** — filters food labels by name as user types
3. **Suggestion / search results area** (up to 6 chips):
   - No search text → time-of-day frequency suggestions (fallback: first 6 labels by sort order when no history)
   - Typing → filtered label matches replace suggestions
4. **Full label chip grid** — all food labels as toggleable chips in a wrapping flow layout (visible when no search text)
5. **Selected labels summary** — filled chips showing current selection
6. **Meal source toggle** — 3-state: Home Cooked | *(unset)* | Eating Out
7. **Notes field** — optional multiline text
8. **Save button** — enabled when at least 1 food label is selected

## Time-of-Day Suggestions

Meal periods:
- Morning: 6:00–11:59
- Afternoon: 12:00–16:59
- Evening: 17:00–20:59
- Late night: 21:00–5:59

Query counts how often each food label was logged during the current time period across all history. Top 6 shown as suggestion chips. Falls back to first 6 labels by `sort_order` when no usage data exists.

## Meal Source Toggle

Uses the existing Label hierarchy pattern:
- Parent label: "Meal Source" (`parentId = null`, food entry type, hidden from chip grid)
- Child labels: "Home Cooked" and "Eating Out" (`parentId = Meal Source`)
- Both tagged with "meal_source" tag for correlation queries
- UI: 3-state toggle (Home Cooked | unset | Eating Out)
- On save: selected meal source label included in `insertWithLabels` alongside food labels
- Enables correlation queries like "Do I feel worse when eating out?"

Default toggle position configurable in Settings (deferred).

## Data Flow

1. `FoodLoggingViewModel` loads all food labels via `LabelRepository.getByEntryType(foodEntryTypeId)`
2. Partitions in memory: labels with `parentId = null` and no children → food chips; labels whose parent is "Meal Source" → toggle options; "Meal Source" parent → hidden
3. Loads time-of-day suggestions via new DAO query
4. On save: `EntryRepository.insertWithLabels(entry, selectedLabelIds)` — includes meal source label ID if toggled

## New DAO/Repository Methods

- `EntryDao.getLabelFrequencyByTimeWindow(entryTypeId, hourStart, hourEnd, limit)` — returns list of label IDs ordered by frequency within the given hour range
- Corresponding repository method in `EntryRepository`

## Seed Data Additions

- Label: "Meal Source" (entryTypeId=1, parentId=null, isEnabled=true)
- Label: "Home Cooked" (entryTypeId=1, parentId=Meal Source)
- Label: "Eating Out" (entryTypeId=1, parentId=Meal Source)
- Tag: "meal_source" (tagGroup="food")
- LabelTag: Home Cooked → meal_source, Eating Out → meal_source

## Proves

This is the first label-based entry type, proving:
- Multi-select label logging path (`insertWithLabels`)
- Label → Tag correlation data model works end-to-end
- Search + suggestion UX pattern reusable for Emotion/Symptom/Activity

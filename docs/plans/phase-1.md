# Haven — Phase 1 Implementation Plan

## Context

Haven is a gentle, private-first health tracking app (Android, Kotlin + Jetpack Compose + Room). Phase 0 planning docs and spec are complete. This plan covers project scaffolding through the end of Phase 1: "I can log and see my logs" — covering Sleep, Hydration, and Food entry types with a Tend page for logging and a Trace page for history.

---

## Step 1: Project Scaffolding

Create the Android project structure with Kotlin, Jetpack Compose, and Room.

- Initialize Android project with `com.haven.app` package
- Configure `build.gradle.kts` with dependencies:
  - Jetpack Compose (BOM, Material 3)
  - Room (runtime, compiler, KTX)
  - Navigation Compose
  - Kotlin coroutines
  - Hilt for dependency injection
- Set up project structure:
  ```
  app/src/main/java/com/haven/app/
  ├── HavenApp.kt              (Application class)
  ├── MainActivity.kt          (single activity, Compose host)
  ├── navigation/
  │   └── HavenNavGraph.kt     (bottom nav: Tend, Trace, + placeholders)
  ├── data/
  │   ├── db/
  │   │   ├── HavenDatabase.kt (Room database, seed data callback)
  │   │   └── Converters.kt    (type converters if needed)
  │   ├── entity/              (Room @Entity classes)
  │   ├── dao/                 (Room @Dao interfaces)
  │   └── repository/          (repository classes)
  ├── ui/
  │   ├── theme/               (colors, typography, shapes)
  │   ├── tend/                (Tend page + logging forms)
  │   └── trace/               (Trace page)
  └── di/                      (Hilt modules)
  ```
- Create placeholder pages for all 5 nav destinations (Anchor, Tend, Trace, Weave, Settings)
- Bottom navigation bar with Tend selected by default

**Verification:** App builds, launches, and shows bottom nav with placeholder screens.

---

## Step 2: Database — Room Entities and DAOs

Translate `schema.sql` into Room entities and set up the database with seed data.

### Entities (in `data/entity/`)
- `MeasurementType` — id, name, displayName
- `Category` — id, name
- `EntryType` — id, name, measurementTypeId, prompt, icon, isEnabled, isDefault, sortOrder
- `Label` — id, entryTypeId, name, parentId, categoryId, isDefault, isEnabled, sortOrder, seedVersion
- `Tag` — id, name, tagGroup, seedVersion
- `LabelTag` — labelId, tagId (composite PK)
- `Entry` — id, entryTypeId, sourceType, timestamp, createdAt, numericValue, notes
- `EntryLabel` — entryId, labelId (composite PK)
- `AnchorActivity` — id, labelId, title, icon, defaultEffort, userEffort, isEnabled, isDefault, seedVersion
- `AnchorTag` — anchorActivityId, tagId (composite PK)

### DAOs (in `data/dao/`)
- `EntryTypeDao` — getAll, getEnabled, getById
- `LabelDao` — getByEntryType, getByParent, search by name, insert user label
- `TagDao` — getByGroup
- `EntryDao` — insert, getByDateRange, getByEntryType, getByDate, delete
- `EntryLabelDao` — insert, getLabelsForEntry

### Seed Data (in `HavenDatabase.kt` callback)
- 4 MeasurementTypes: numeric, label_select, label_select_severity, label_category_select
- 8 Categories: Connect, Move, Reflect, Breathe, Nourish, Create, Ground, Structure
- 6 EntryTypes: Food, Emotion, Hydration, Sleep, Symptom, Activity (only Food, Sleep, Hydration enabled for Phase 1)
- Food labels: ~20-30 common foods (cheese, bread, rice, chicken, eggs, milk, pasta, salad, fruit, coffee, tea, etc.)
- Food tags: dairy, gluten, FODMAP, caffeine, sugar, processed, whole food
- LabelTag associations for food labels

### Seed data strategy
- Delivered via Room's `RoomDatabase.Callback.onCreate`
- Each seed row has `seedVersion = 1`
- Future versions use `addMigrations()` to insert new seed rows where `seedVersion > lastSeen`

**Verification:** Database inspector shows all tables populated. Query for enabled EntryTypes returns Food, Sleep, Hydration.

---

## Step 3: Design System / Theme

Set up the Haven visual identity in Compose.

- **Colors:** Soft sage green (primary), lavender (secondary), off-white (background)
- **Typography:** Philosopher for headers, Quicksand for body (bundle .ttf files)
- **Shapes:** Rounded corners (pill-shaped buttons, rounded cards)
- **Components:** Reusable pill button composable with soft shadow

**Verification:** Theme applied across placeholder screens, fonts rendering correctly.

---

## Step 4: Tend Page — Entry Type Grid

Build the main logging page.

- Grid of pill-shaped buttons, one per enabled EntryType
- Each button shows icon + name from EntryType table
- Tap a button → navigate to the appropriate logging form (Step 5-7)
- ViewModel: `TendViewModel` queries enabled EntryTypes via repository
- State: loading → grid displayed

**Verification:** Tend page shows 3 buttons (Food, Sleep, Hydration) from seed data.

---

## Step 5: Sleep Logging (Tier 1 — Numeric)

Simplest entry type: enter hours slept.

- Logging form: numeric input (hours), timestamp (defaults to now, editable), optional notes
- Save creates an `Entry` with entryTypeId=Sleep, numericValue=hours, sourceType="log"
- Success feedback → return to Tend page
- ViewModel: `SleepLogViewModel`

**Verification:** Log sleep entry, confirm it appears in database and on Trace page (Step 8).

---

## Step 6: Hydration Logging (Tier 1 — Numeric, Quick-Add)

Multiple entries per day with quick-add.

- Logging form: numeric input (oz/ml), quick-add buttons (+8oz, +16oz, custom), timestamp
- Default increment could be configurable later (hardcode 8oz for now)
- Each tap creates a new `Entry` with numericValue=amount
- Show today's total at the top of the form
- ViewModel: `HydrationLogViewModel`

**Verification:** Log multiple hydration entries, see running total update, entries appear on Trace.

---

## Step 7: Food Logging (Tier 2 — Label Select)

Proves the label+tag system works end-to-end.

- Logging form: search bar + scrollable grid/list of food labels
- Multi-select: tap labels to toggle selection (visual highlight)
- Recently used labels shown at top (query recent EntryLabels)
- Submit creates: 1 `Entry` (type=Food) + N `EntryLabel` rows
- Timestamp defaults to now, editable
- ViewModel: `FoodLogViewModel`

**Verification:** Log a food entry with multiple labels, confirm Entry + EntryLabel rows in DB. Labels with dairy tag queryable for future correlation.

---

## Step 8: Trace Page — History View

Basic history/timeline for reviewing past logs.

- Entries grouped by day, most recent day first
- Each day section shows date header + chronological list of entries
- Each entry card shows: EntryType icon + name, time, value summary
  - Sleep: "8 hours"
  - Hydration: "16 oz"
  - Food: "Cheese, Bread, Tomato" (label names)
- Scrollable, lazy-loaded
- Filter by entry type (chip row at top, optional)
- ViewModel: `TraceViewModel` queries entries with labels joined

**Verification:** All logged entries from Steps 5-7 appear correctly grouped by day on the Trace page.

---

## Implementation Order

Each step builds on the previous:

1. **Scaffolding** — buildable app with nav shell
2. **Database** — Room entities, DAOs, seed data
3. **Theme** — visual identity applied
4. **Tend page** — entry type grid (reads from DB)
5. **Sleep logging** — simplest form, proves Entry creation works
6. **Hydration logging** — multi-entry + quick-add pattern
7. **Food logging** — proves label select + EntryLabel join
8. **Trace page** — proves read path, entries display correctly

Steps 1-3 are foundational. Steps 4-7 are incremental feature adds. Step 8 ties it all together.

---

## Key Files Summary

| File | Purpose |
|---|---|
| `app/build.gradle.kts` | Dependencies (Compose, Room, Hilt, Nav) |
| `HavenDatabase.kt` | Room DB definition + seed data callback |
| `data/entity/*.kt` | All 10 Room entity classes |
| `data/dao/*.kt` | DAO interfaces for queries |
| `data/repository/*.kt` | Repository layer between DAOs and ViewModels |
| `ui/theme/Theme.kt` | Colors, typography, shapes |
| `navigation/HavenNavGraph.kt` | Bottom nav + routes |
| `ui/tend/TendScreen.kt` | Entry type grid |
| `ui/tend/SleepLogScreen.kt` | Sleep logging form |
| `ui/tend/HydrationLogScreen.kt` | Hydration logging form |
| `ui/tend/FoodLogScreen.kt` | Food label picker + submit |
| `ui/trace/TraceScreen.kt` | History grouped by day |

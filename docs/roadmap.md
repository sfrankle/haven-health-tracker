# Roadmap

## Phase 1 — "I can log and see my logs"

Scaffolding + 3 entry types + 2 pages. Proves the data model end-to-end.

### Project scaffolding
- Android project with Kotlin + Compose + Room + Hilt
- Bottom navigation shell with 5 destinations (Tend, Trace active; Weave, Anchor, Settings as placeholders)
- Design system: sage green / lavender / off-white palette, Philosopher font, pill-shaped button component

### Database
- All 10 tables from schema.sql implemented as Room entities
- Seed data for Phase 1 entry types: Food (with ~20-30 labels and tags), Sleep, Hydration
- Seed data for MeasurementTypes, Categories
- DAOs for entry creation, querying by date/type, label lookup

### Tend page
- Grid of pill buttons, one per enabled EntryType (reads from DB)
- Tap → navigates to the entry type's logging form

### Sleep logging
- Numeric input for hours slept
- Timestamp (defaults to now, editable)
- Optional notes
- Creates an Entry with numericValue

### Hydration logging
- Numeric input with quick-add buttons (+8oz, +16oz, custom)
- Shows running daily total
- Each tap creates a separate Entry

### Food logging
- Search bar + scrollable label grid
- Multi-select labels (visual toggle)
- Recently used labels shown first
- Creates 1 Entry + N EntryLabel rows
- **This proves the Label → Tag correlation path works**

### Trace page
- Entries grouped by day, most recent first
- Each entry shows: icon, type name, time, value summary (e.g., "8 hours", "Cheese, Bread")
- Lazy-loaded scrolling
- Optional: filter chips by entry type

### Done when
- Can log sleep, hydration, and food entries from the Tend page
- All entries appear correctly on the Trace page grouped by day
- Food entries have labels, and those labels have tags queryable for future correlation

---

## Phase 2 — Remaining Entry Types

- **Emotion** — nested valence picker (Pleasant / Neutral / Unpleasant → specific emotions), multi-select, tags for correlation
- **Symptom** — body part → symptom labels, multi-select, severity slider (1-5), stored as numericValue
- **Activity** — labels organized by Category, proves the label_category_select measurement type

### Done when
- All 6 entry types are loggable from Tend and visible on Trace

---

## Phase 3 — Reflect, Anchor, Settings

- **Reflect mode** — end-of-day logging flow via Tend, entries with sourceType "reflect" (date-associated, not timestamped)
- **Anchor page** — shows 3 activity suggestions based on effort rating, shuffle to see more, tap to log
- **Settings page** — toggle entry types and individual labels on/off

---

## Phase 4 — Insights + Polish

- **Weave page** — correlation engine (e.g., "7/10 times you ate dairy, you felt bloated"), tag-based analysis
- **Notifications** — food reminders (configurable meal times), hydration reminders (hourly), symptom follow-up, end-of-day reflection prompt

---

## Deferred / Maybe

- Google Drive backup
- Health Connect integration
- Data export for healthcare providers
- Local ML for correlation detection

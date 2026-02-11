# Brainstorm Decisions

## Tech Stack
- **Platform:** Android first
- **Language/Framework:** Kotlin + Jetpack Compose (confirmed)
- **iOS later:** KMP upgrade path if needed, not now
- **Database:** SQLite via Room (local-only, Google Drive backup later)
- **Body font:** Quicksand
- **Header font:** Philosopher

## Pages
- Anchor — capacity-based activity suggestions (suggestions ARE Activity labels)
- Tend — logging and reflection
- Trace — history/timeline
- Weave — trends and correlations
- Settings

## Entry Types (v1)

Three tiers of complexity:

### Tier 1 — Simple numeric
- **Sleep** — hours slept (one entry per day)
- **Hydration** — oz/ml per drink (multiple entries per day)

### Tier 2 — Label-based
- **Food** — search/select multiple food labels per entry; labels have tags (dairy, FODMAP...)
- **Emotion** — nested: select valence (Pleasant/Neutral/Unpleasant) → select emotion labels; labels have tags (nervous system, hormone...). Inspired by Flinch.
- **Symptom** — select body part → select symptom labels (multi-select) → severity slider; body parts are parent labels

### Tier 3 — Label + category
- **Activity** — things you DO (paint, yoga, walk, call a friend); organized by Category (Exercise, Creative, Social, Grounding, Breathe...)

## Data Model

### Key tables
- **EntryType** — top-level types: Food, Emotion, Hydration, Sleep, Symptom, Activity
- **Label** — specific items within each entry type (cheese, overwhelmed, gassy, yoga...)
  - has parentId for hierarchy (symptom body parts → symptom labels)
  - has isDefault/isEnabled for progressive disclosure
- **Tag** — classification on labels (dairy, FODMAP, nervous system, cardio...)
- **LabelTag** — join table: label ↔ tag (enables retroactive tagging)
- **Category** — groups for Activity labels: Connect, Move, Reflect, Breathe, Nourish, Create, Ground, Structure
- **MeasurementType** — drives form rendering (yes/no, scale 1-5, multi-select, etc.)
- **Entry** — a logged event (activityId→EntryType, sourceType, timestamp, numericValue, notes)
- **EntryLabel** — join table: entry ↔ labels selected by user
- **AnchorActivity** — suggestion layer; links to Activity labels via FK; tap → creates Entry

### Key design decisions
- Tags go on **Labels**, not on Entries — retroactive tagging works automatically
- Correlation path: Entry → EntryLabel → Label → LabelTag → Tag
- Entry has queryable numericValue column (hours, oz, severity) — not just JSON
- SourceType: "log" (in-the-moment) vs "reflect" (end-of-day)
- Preloaded DB with sensible defaults
- **Safe migrations:** new app versions can add seed data without disrupting existing user data. All local data is sacred.
- **Label hierarchy (parentId)** used by multiple entry types:
  - Symptom: body part → symptom labels (stomach → gassy, cramps...)
  - Emotion: valence → emotion labels (Pleasant → Optimistic, Content... / Unpleasant → Anxious, Overwhelmed... / Neutral → ...)
  - Inspired by Finch app's emotion picker

### Anchor → Entry flow
- Anchor suggestions are Activity labels (e.g., "Take 5 deep breaths" = Breathe category)
- Tap suggestion → creates an Entry of type Activity with that label
- AnchorActivity adds: effort rating, icon, enabled/disabled

## Design
- Color palette: soft sage green, lavender, off-white
- Pill-shaped buttons, soft shadows, rounded grid
- No scores, streaks, or judgment
- Offline-first

## Lessons from Previous Iterations (AirmedTracking, EirCompanion)

### Kept
- Two-layer model (EntryType + Label) — was right, just had confusing names
- MeasurementType as form driver
- SourceType distinction (log vs reflect)
- isEnabled/isDefault for progressive disclosure
- Preloaded DB with sensible defaults
- Decoupled data-UI contract

### Changed
- Clearer naming: EntryType (top-level), Label (items within), Tag (classification on labels)
- Tags on Labels (not Entries) for retroactive association
- Join tables for tags and entry-labels (clean correlation queries)
- Exercise is a Category within Activity entry type, not its own entry type
- Kotlin/Compose + Room instead of Flutter

## Phases

### Phase 0 — Project setup
- Finalize spec and plan docs
- Create GitHub repo

### Phase 1 — "I can log and see my logs"
- Project scaffolding (Kotlin + Compose, Room, navigation shell)
- Database + Room setup with seed data (EntryType, Label, Tag, LabelTag, Category, MeasurementType)
- Tend page — entry type grid, tap → logging form
- Sleep logging (tier 1 — hours, one per day)
- Hydration logging (tier 1 — oz/ml, multiple per day)
- Food logging (tier 2 — search/select labels, proving label+tag system)
- Basic Trace page — logs grouped by day, chronological

### Phase 2+ (deferred)
- Emotion, Symptom, Activity entry types
- Reflect mode (end-of-day)
- Anchor page, Weave page, Settings page
- Notifications
- Correlation engine

## Open Questions
- ~~App name~~ **Haven** (confirmed)
- Exact correlation engine approach (open to local ML)
- Notification system details
- Reflect mode form design

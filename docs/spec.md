# Haven — Specification

## Overview

Haven is a gentle, private-first mobile app for tracking food, feelings, habits, symptoms, and moments of reflection. It supports nervous system regulation and everyday happiness through correlation discovery — not rigid metrics, streaks, or judgment.

**Platform:** Android (Kotlin + Jetpack Compose)
**Database:** SQLite via Room (local-only, offline-first)
**Target user:** Neurodivergent individuals, people managing chronic symptoms, anyone seeking non-judgmental self-awareness

## Non-goals
- Not a calorie tracker or behavior enforcement tool
- No cloud sync (Google Drive backup deferred to later)
- No monetization
- No iOS (KMP upgrade path available if needed later)

---

## Pages

| Page | Purpose |
|---|---|
| **Tend** | Logging and reflection — the primary interaction |
| **Trace** | History and timeline review |
| **Weave** | Trends and correlation insights |
| **Anchor** | Capacity-based activity suggestions |
| **Settings** | Preferences, toggles, customization |

### Tend (Logging)
- Grid of rounded buttons, one per enabled entry type
- Tap a button → opens the logging form for that entry type
- Two modes: "log" (in-the-moment, timestamped) and "reflect" (end-of-day, date-associated)
- Low-friction: minimal taps, no overwhelming forms

### Trace (History)
- View past logs organized by day
- Entries grouped chronologically
- Filters by entry type, date range
- Date jumping, data export (for sharing with healthcare providers)

### Weave (Insights)
- Correlation analysis across entry types
- Example insights:
  - "7 out of 10 times you ate something tagged dairy, you felt bloated"
  - "On days with >6 cups of water, you reported feeling calmer"
- Supports: food tags, time-of-day patterns, boolean and scale-based analysis
- No medical claims — gentle pattern recognition only
- Analysis triggered on page load or manually

### Anchor (Grounding)
- Shows 3 calming activity suggestions
- Shuffle to see different options
- Each suggestion includes: title, optional icon, tags (regulation, happiness, etc.), effort rating (1-5)
- Tap a suggestion → opens a log form to record doing it (creates an Entry of type Activity)
- Anchor suggestions are Activity labels from the database

### Settings
- Toggle entry types on/off
- Toggle individual labels on/off
- App preferences and customization
- Start simple, expand over time

---

## Entry Types

### Tier 1 — Simple numeric

**Sleep**
- Input: number of hours
- One entry per day typical
- Source type: usually "reflect"

**Hydration**
- Input: oz or ml per drink
- Multiple entries per day (tap + button for quick add)
- Default increment configurable

### Tier 2 — Label-based

**Food**
- Flow: tap Food → search bar + label grid → select multiple food labels → submit
- Each entry records multiple labels (e.g., cheese, bread, tomato)
- Labels have tags (dairy, FODMAP, gluten, etc.) for correlation
- Recommendations / recently used shown for quick access

**Emotion**
- Flow: tap Emotion → select valence (Pleasant / Neutral / Unpleasant) → select specific emotions → submit
- Nested labels using parentId (valence is parent, specific emotions are children)
- Inspired by Finch app's emotion picker
- Labels have tags (nervous system, hormone, etc.)
- Multi-select: can feel multiple things at once

**Symptom**
- Flow: tap Symptom → select body part → select symptom labels (multi-select) → optional severity slider (1-5) → submit
- Nested labels using parentId (body part is parent, symptoms are children)
- Body parts: stomach, head, chest, joints, skin, etc.
- Severity stored as numericValue on Entry

### Tier 3 — Label + category

**Activity**
- Things you DO: paint, cuddle a cat, sketch, stretch, go for a walk, call a friend, yoga, running
- Labels organized by Category (Exercise, Creative, Social, Grounding, Breathe, etc.)
- Categories reuse the Category table: Connect, Move, Reflect, Breathe, Nourish, Create, Ground, Structure
- Labels have tags for correlation (regulation, cardio, social, etc.)
- Anchor page suggestions link directly to Activity labels

---

## Data Model

### Entity Relationship Summary

```
EntryType ──< Label ──< LabelTag >── Tag
                │
                └── parentId (self-referencing for hierarchy)

Entry ──< EntryLabel >── Label
  │
  └── FK → EntryType

Category ──< Label (for Activity entry type)

MeasurementType ──< EntryType

AnchorActivity ── FK → Label (Activity labels only)
AnchorActivity ──< AnchorTag >── Tag
```

### Tables

#### EntryType
| Column | Type | Notes |
|---|---|---|
| id | INTEGER PK | auto-increment |
| name | TEXT NOT NULL UNIQUE | Food, Emotion, Hydration, Sleep, Symptom, Activity |
| measurementTypeId | INTEGER FK | → MeasurementType |
| prompt | TEXT | "What did you eat?", "How are you feeling?" |
| icon | TEXT | icon reference for the Tend grid |
| isEnabled | INTEGER (bool) | user can toggle in Settings |
| isDefault | INTEGER (bool) | shipped with app vs user-created |
| sortOrder | INTEGER | controls display order in Tend grid |

#### MeasurementType
| Column | Type | Notes |
|---|---|---|
| id | INTEGER PK | auto-increment |
| name | TEXT NOT NULL UNIQUE | numeric, label_select, label_select_severity, label_category_select |
| displayName | TEXT | human-readable name |

#### Category
| Column | Type | Notes |
|---|---|---|
| id | INTEGER PK | auto-increment |
| name | TEXT NOT NULL UNIQUE | Connect, Move, Reflect, Breathe, Nourish, Create, Ground, Structure |

#### Label
| Column | Type | Notes |
|---|---|---|
| id | INTEGER PK | auto-increment |
| entryTypeId | INTEGER FK | → EntryType (which entry type this belongs to) |
| name | TEXT NOT NULL | "cheese", "overwhelmed", "gassy", "yoga" |
| parentId | INTEGER FK? | → Label (self-ref: body part→symptoms, valence→emotions) |
| categoryId | INTEGER FK? | → Category (for Activity labels only) |
| isDefault | INTEGER (bool) | shipped with app |
| isEnabled | INTEGER (bool) | user can hide |
| sortOrder | INTEGER | display order within parent/group |
| seedVersion | INTEGER | which app version introduced this seed row |

#### Tag
| Column | Type | Notes |
|---|---|---|
| id | INTEGER PK | auto-increment |
| name | TEXT NOT NULL | "dairy", "FODMAP", "nervous system", "cardio" |
| group | TEXT NOT NULL | "food", "symptom", "emotion", "activity", "anchor" |
| seedVersion | INTEGER | for safe migration |

#### LabelTag (join: Label ↔ Tag)
| Column | Type | Notes |
|---|---|---|
| labelId | INTEGER FK | → Label |
| tagId | INTEGER FK | → Tag |
| PRIMARY KEY | (labelId, tagId) | composite |

#### Entry
| Column | Type | Notes |
|---|---|---|
| id | INTEGER PK | auto-increment |
| entryTypeId | INTEGER FK | → EntryType |
| sourceType | TEXT NOT NULL | "log" or "reflect" |
| timestamp | TEXT NOT NULL | ISO 8601 — when the event happened |
| createdAt | TEXT NOT NULL | ISO 8601 — when the entry was recorded |
| numericValue | REAL? | hours (sleep), oz/ml (hydration), severity 1-5 (symptom) |
| notes | TEXT? | optional user notes |

#### EntryLabel (join: Entry ↔ Label)
| Column | Type | Notes |
|---|---|---|
| entryId | INTEGER FK | → Entry |
| labelId | INTEGER FK | → Label |
| PRIMARY KEY | (entryId, labelId) | composite |

#### AnchorActivity
| Column | Type | Notes |
|---|---|---|
| id | INTEGER PK | auto-increment |
| labelId | INTEGER FK | → Label (Activity labels only) |
| title | TEXT NOT NULL | display title (may differ from label name) |
| icon | TEXT? | optional icon reference |
| defaultEffort | INTEGER NOT NULL | 1-5, shipped effort rating |
| userEffort | INTEGER? | 1-5, user override |
| isEnabled | INTEGER (bool) | |
| isDefault | INTEGER (bool) | |
| seedVersion | INTEGER | for safe migration |

#### AnchorTag (join: AnchorActivity ↔ Tag)
| Column | Type | Notes |
|---|---|---|
| anchorActivityId | INTEGER FK | → AnchorActivity |
| tagId | INTEGER FK | → Tag |
| PRIMARY KEY | (anchorActivityId, tagId) | composite |

### Correlation Query Pattern

"Did eating dairy correlate with bloating?"

```sql
-- Find food entries with dairy-tagged labels
SELECT e.id, e.timestamp
FROM Entry e
JOIN EntryLabel el ON e.id = el.entryId
JOIN Label l ON el.labelId = l.id
JOIN LabelTag lt ON l.id = lt.labelId
JOIN Tag t ON lt.tagId = t.id
WHERE e.entryTypeId = (SELECT id FROM EntryType WHERE name = 'Food')
AND t.name = 'dairy'

-- Then find symptom entries within X hours with "bloating" label
```

### Safe Migration Strategy

- Seed data rows have a `seedVersion` column
- On app update: insert new rows where `seedVersion > lastSeenVersion`
- Never update or delete existing rows
- User-created data (`isDefault = false`) is never touched
- Room's built-in migration system handles schema changes

---

## Design System

| Element | Value |
|---|---|
| Color palette | Soft sage green, lavender, off-white |
| Header font | Philosopher |
| Body font | Philosopher |
| Buttons | Pill-shaped with soft shadows |
| Layout | Rounded grid for entry type buttons |
| Animations | Smooth transitions |
| Assets | All local (offline-first, no external dependencies) |

### Principles
- No scores, streaks, or pressure indicators
- Low-friction logging (minimal taps)
- Progressive disclosure (start simple, expand via Settings)
- Non-judgmental language throughout
- Accessible: clear navigation, readable fonts, intuitive icons

---

## User Journey

### Daily Flow
1. **Throughout the day:** Log entries via Tend page (food, hydration, etc.)
   - Entries saved with current timestamp (editable)
   - Optional reminders (phase 2+)
2. **End of day:** Reflect via Tend page
   - Curated reflection form (phase 2+)
   - Entries associated with date rather than specific time
3. **Ongoing:** Review history on Trace page
4. **As needed:** Explore patterns on Weave page
5. **Anytime:** Visit Anchor page for grounding activity suggestions → log doing them

---

See `docs/roadmap.md` for development phases and feature scope.

# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project

Haven — private-first Android health tracking app. Kotlin + Jetpack Compose + Room. All data local to device.

Key docs: `docs/spec.md` (feature spec), `docs/roadmap.md` (what's next), `docs/changelog.md` (what's done), `docs/schema.sql` (database schema), `docs/decisions.md` (design rationale), `docs/design.md` (visual design system).

## Build Commands

```bash
./gradlew assembleDebug                                    # Build
./gradlew test                                             # Unit tests
./gradlew connectedAndroidTest                             # Instrumented tests
./gradlew test --tests "com.haven.app.SomeTest"            # Single test class
./gradlew lint                                             # Lint
```

## Architecture

- **Single activity** (`MainActivity.kt`) hosts Compose, with bottom nav across 5 pages: Tend, Trace, Weave, Anchor, Settings
- **Feature packages** under `ui/` (e.g., `ui/tend/`, `ui/trace/`), each with a Screen composable and ViewModel
- **Data layer:** Room entities in `data/entity/`, DAOs in `data/dao/`, repositories in `data/repository/`
- **DI:** Hilt — `HavenApp.kt` is the `@HiltAndroidApp` entry point, modules in `di/`
- **Async:** Kotlin coroutines + Flow throughout; Room DAOs return `Flow<List<T>>`

## Workflow

- PRs should be small, self-contained units of work — one logical feature or change per PR
- Each PR gets one row in the `docs/changelog.md` table with the merge date, PR number, and a concise summary
- As roadmap features are completed, collapse them in `docs/roadmap.md` and move the detail to the changelog
- Link issues to PRs with `Closes #N` in the PR body — GitHub will auto-close the issue when the PR merges. Never close issues manually.
- **Never commit directly to `main`.** All changes go through a feature branch and PR, no exceptions.
- **Don't use git worktrees** unless explicitly asked. Check out branches normally so the user can see current work.

## Coding Conventions

- Package: `com.haven.app`
- PascalCase for classes and composable functions, camelCase for properties/functions
- SNAKE_CASE for database column names
- One ViewModel per major screen, injected via Hilt
- Repositories wrap DAOs — ViewModels never access DAOs directly. Repos are use-case-oriented (not DAO mirrors); only expose methods ViewModels need. See `docs/decisions.md` #12.
- Add KDoc where behavior, parameter format, or contract isn't obvious from the name and types alone (e.g. string timestamp formats, throwing vs. nullable, snapshot vs. Flow). Skip it for simple CRUD, pass-throughs, and anything self-evident. Be discerning - don't add superfluous comments. Apply it anywhere in the codebase where it adds value for Claude Code.

## Data Model

Tags go on **Labels, not Entries** — this is the key design decision. It enables retroactive correlation (tag a food label "dairy" and all past entries using it are automatically included in correlation queries). See `docs/decisions.md` #3 for rationale.

Seed data uses `seedVersion` column for safe migrations across app updates. Delivered in `RoomDatabase.Callback.onOpen` via `INSERT OR IGNORE` on the raw `SupportSQLiteDatabase`, gated by a SharedPreferences version check — only runs when `SeedData.VERSION` is bumped. See `docs/schema.sql` for full schema and seed data reference.

## Design System

See `docs/design.md` for colors, typography, components, and principles. Key constraint: non-judgmental tone — no scores, streaks, or "good/bad" framing in any user-facing text.

# Finishing a PR
- make sure PR description is up to date
- make sure all docs are up to date (especially docs/changelog.md, docs/roadmap.md, and docs/decisions.md)
    - make sure the changelog references the correct PR number
- if there's a related plan in docs/plan, make sure all items have been completed
    - consider if any content from the plan should be saved in other docs
    - when ready, delete the plan file as a final step
- Make sure all new / editted code has proper testing coverage. Be smart - don't make tests for the sake of "code coverage"; write tests that actually test the behavior.


## Design Philosophy

The following are design principles, not user stories, but document important constraints:

### Non-Judgmental System
- **No scoring, streaks, or gamification** — tracking is low-pressure and optional
- **Neutral language** — "logged" not "succeeded", no guilt-inducing messaging
- **Missing days are fine** — no warnings or penalties for not logging
- **Design for ADHD/neurodivergence** — minimal friction, low-demand interface

### Privacy-First Design
- **Local-only storage** — all data on device, never cloud-synced without explicit user action
- **No accounts or tracking** — user owns their data completely
- **Offline-first** — app fully functional without internet
- **Clear privacy messaging** — be explicit about what stays private

### Exploration Over Prescription
- **Correlations suggest, don't prescribe** — "notice" not "fix"
- **No medical claims** — user sees patterns, not diagnoses
- **Exploratory language** — "You might notice..." vs "You should..."

---

## Implementation Notes

### Data Model & EntryTypes
- **Fixed entry types:** FOOD, EMOTION, HYDRATION, SLEEP, PHYSICAL_STATE, ACTIVITY
- **Custom values within types:** Users can create custom foods, activities, physical feelings, etc.
- **No new user-created entry types** — structure stays fixed, values are flexible
- **Nested labels:** 
  - Emotion: Valence (parent) → Specific emotions (children)
  - Physical State: Body part (parent) → Symptoms (children)
  - Activity: Category (parent) → Activity labels (children)

### UI Patterns
- **Search bars with recommendations:** Food and Activity use search with empty-state suggestions
- **Comma-separated custom creation:** Type "matcha," to create new food/activity during submit
- **Multi-select:** Emotions, symptoms, general feelings allow multiple selections per entry
- **Body part picker:** Interactive diagram for location-specific symptom tracking
- **Two-step forms:** Emotion (valence → emotions), Physical State (visual prompts)

### Settings & Configuration (MVP)
- **Hydration defaults:** Increment and unit (oz vs ml)
- **Entry type toggles:** Enable/disable by type
- **Entry type sort order:** Customize Tend grid order

### Correlation Algorithm
- **Query pattern:** Find co-occurrences of tags across entry types within same day
- **Display:** "Dairy food → bloating" (count and percentage, e.g., "7 out of 10 times")
- **Safe migrations:** Seed data updates without touching user data

### Testing Strategy (User Story Driven)
- Each story has clear acceptance criteria that map to test cases
- Test cases can be generated from AC and run against builds
- Stories enable tracking of feature completion and readiness for release

---

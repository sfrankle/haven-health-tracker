# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project

Haven — private-first Android health tracking app. Kotlin + Jetpack Compose + Room. All data local to device.

Key docs: `docs/spec.md` (feature spec), `docs/roadmap.md` (what's next), `docs/changelog.md` (what's done), `docs/schema.sql` (database schema), `docs/decisions.md` (design rationale).

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
- Each PR gets one entry in `docs/changelog.md` with 1–5 concise bullet points covering the most important changes
- As roadmap features are completed, collapse them in `docs/roadmap.md` and move the detail to the changelog

## Coding Conventions

- Package: `com.haven.app`
- PascalCase for classes and composable functions, camelCase for properties/functions
- SNAKE_CASE for database column names
- One ViewModel per major screen, injected via Hilt
- Repositories wrap DAOs — ViewModels never access DAOs directly

## Data Model

Tags go on **Labels, not Entries** — this is the key design decision. It enables retroactive correlation (tag a food label "dairy" and all past entries using it are automatically included in correlation queries). See `docs/decisions.md` #3 for rationale.

Seed data uses `seedVersion` column for safe migrations across app updates. Delivered in `RoomDatabase.Callback.onCreate`. See `docs/schema.sql` for full schema and seed data reference.

## Design System

- **Colors:** Sage green (primary), lavender (secondary), off-white (background)
- **Fonts:** Philosopher (headers), Quicksand (body) — bundled as TTF in assets
- **Components:** Pill-shaped buttons, rounded cards, soft shadows
- **Tone:** Non-judgmental. No scores, streaks, or "good/bad" framing in any user-facing text

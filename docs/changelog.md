# Changelog

## Phase 0 — Project Setup

- **2026-02-11** — Project spec, database schema reference, decision log, roadmap, GitHub repo initialized (#1)

## Phase 1 — "I can log and see my logs"

### PR 1: Project Scaffolding + Database Layer
- All 10 Room entities, DAOs, repositories, Hilt DI wiring, and seed data
- Bottom nav shell with 5 destinations
- Haven design system theme (sage/lavender/off-white, Philosopher + Quicksand fonts)
- Instrumented DAO tests

### PR 3: Idempotent Seed Data
- Seed callback uses `onOpen` + `INSERT OR IGNORE` instead of `onCreate`, gated by `SeedData.VERSION` in SharedPreferences — runs once per version bump, not every launch
- Eliminated `Provider<HavenDatabase>` / `runBlocking` / `withTransaction` deadlock risk

### PR 2: Tend Page + Sleep & Hydration Logging
- Tend page with entry type button grid (reads enabled types from DB)
- Sleep logging form with hours input and optional notes
- Hydration logging with quick-add buttons (+8oz/+16oz) and running daily total
- Navigation routing from Tend to logging forms (no hardcoded IDs)
- Icon mapper, TendViewModel, SleepLoggingViewModel, HydrationLoggingViewModel

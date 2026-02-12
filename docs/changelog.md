# Changelog

## Phase 0 — Project Setup

- **2026-02-11** — Project spec, database schema reference, decision log, roadmap, GitHub repo initialized (#1)

## Phase 1 — "I can log and see my logs"

### PR 1: Project Scaffolding + Database Layer
- **2026-02-11** — Android project scaffolded with Kotlin 2.0, Compose, Room, Hilt
  - Gradle project with version catalog (AGP 8.7.3, Compose BOM 2024.12, Room 2.6.1, Hilt 2.51.1)
  - All 10 Room entities matching schema.sql with foreign keys and indices
  - 6 DAOs with Phase 1 queries (entry creation, label lookup, daily totals, joined detail views)
  - Repository layer wrapping all DAOs
  - Phase 1 seed data: 4 measurement types, 8 categories, 6 entry types, 25 food labels, 8 food tags, 27 label-tag mappings
  - Hilt DI wiring with seed data callback on first database creation
  - Bottom nav shell with 5 destinations (Tend/Trace as stubs, Weave/Anchor/Settings as placeholders)
  - Instrumented DAO tests (7 tests covering seed data, entry creation with labels, daily totals)
  - Room schema export enabled for migration testing

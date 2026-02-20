# CLAUDE.md

This file provides guidance to Claude Code when working with code in this repository.

## Project

Haven — private-first Android health tracking app. Kotlin + Jetpack Compose + Room. All data local to device.

Key docs: `docs/spec.md` (feature spec), `docs/changelog.md` (what's done), `docs/schema.sql` (database schema), `docs/decisions.md` (design rationale), `docs/design/` (design system), `docs/ux/` (UX specs).

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

Haven uses an issue-driven development workflow. All work flows through GitHub Issues and Milestones.

### Issue Hierarchy
- **User stories** (label: `user-story`) — product-level features, grouped by milestone
- **Technical tasks** (label: `technical-task`) — implementation units, linked to user stories
- One technical task = one PR. A user story may span multiple technical tasks.

### Technical Task Lifecycle
1. Claude analyzes user stories and creates technical task issues with detailed acceptance criteria
2. Claude writes a detailed plan to `docs/plans/` and posts a summary comment on the issue
3. Human approves the plan
4. Claude implements, opens a **draft PR** linking `Closes #N` (the technical task)
5. PRs reference user stories with "Contributes to #M" — never `Closes` on user stories
6. Human asks Claude to review the PR using the `superpowers:requesting-code-review` skill
7. After review approval, human merges
8. **User stories are closed manually** by the human after all contributing technical tasks are merged and the feature is complete

### PR Conventions
- Always start as **draft**
- Each PR updates `docs/changelog.md` **in the commits** (before opening PR)
- Each PR updates other docs if relevant (decisions.md, schema.sql, design/, ux/) **in the commits**
- **Never commit directly to `main`.** All changes go through a feature branch and PR.
- **Don't use git worktrees** unless explicitly asked.
- **Branch naming:**
  - `feat/<description>` - new features
  - `fix/<description>` - bug fixes
  - `refactor/<description>` - code refactoring
  - `chore/<description>` - maintenance, docs, tooling

### PR Review Process
- If review feedback requires **minor changes** (typos, small tweaks), push new commits to the branch
- If review feedback requires **major changes** (approach is wrong, significant rework needed):
  1. Close the PR with a comment explaining why
  2. Update the plan in `docs/plans/` based on feedback
  3. Create a new branch and implement the revised approach
  4. Open a new PR
- **Never force push** to a PR branch that's under review unless explicitly requested

## Coding Conventions

- Package: `com.haven.app`
- PascalCase for classes and composable functions, camelCase for properties/functions
- SNAKE_CASE for database column names
- One ViewModel per major screen, injected via Hilt
- Repositories wrap DAOs — ViewModels never access DAOs directly. Repos are use-case-oriented (not DAO mirrors); only expose methods ViewModels need. See `docs/decisions.md` #12.
- Add KDoc where behavior, parameter format, or contract isn't obvious from the name and types alone. Skip it for simple CRUD, pass-throughs, and anything self-evident.
- Proactively consider performance for user-facing operations (DB indices, avoid N+1 queries, minimize Compose recomposition)

## Code Quality Standards

**Prefer refactoring over accepting mediocre code.** There's already a lot of code in this codebase. When you encounter code that could be better—unclear naming, poor structure, inconsistent patterns, or violation of our conventions—refactor it. Don't rationalize "well, I guess this is ok..." and leave it. Make it better.

When adding new functionality:
- If existing code in the area is subpar, improve it as part of your work
- Don't perpetuate bad patterns just because they already exist
- Don't add to technical debt by accepting "good enough"
- Refactoring existing code to meet standards is expected, not optional

## Data Model

Tags go on **Labels, not Entries** — this is the key design decision. It enables retroactive correlation. See `docs/decisions.md` #3.

Seed data uses `seedVersion` column for safe migrations across app updates. Delivered in `RoomDatabase.Callback.onOpen` via `INSERT OR IGNORE` on the raw `SupportSQLiteDatabase`, gated by a SharedPreferences version check — only runs when `SeedData.VERSION` is bumped. See `docs/schema.sql` for full schema and seed data reference.

## Design System

See `docs/design/design-principles.md` (brand, voice, UX philosophy) and `docs/design/visual-style.md` (colors, typography, components). Key constraint: non-judgmental tone — no scores, streaks, or "good/bad" framing in any user-facing text.

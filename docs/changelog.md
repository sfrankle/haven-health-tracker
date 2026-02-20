# Changelog

| Date | PR | Summary |
|---|---|---|
| 2026-02-11 | — | Project spec, database schema reference, decision log, roadmap, GitHub repo initialized |
| 2026-02-13 | #1 | Project scaffolding + database layer: 10 Room entities, DAOs, repositories, Hilt DI, seed data, bottom nav shell, Haven design system theme, instrumented DAO tests |
| 2026-02-14 | #2 | Tend page + Sleep & Hydration logging: entry type button grid, sleep form with hours/notes, hydration quick-add buttons with daily total, navigation routing |
| 2026-02-14 | #3 | Idempotent seed data: `onOpen` + `INSERT OR IGNORE` gated by SharedPreferences version check, eliminated deadlock risk |
| 2026-02-15 | #4 | Trace page: day-grouped entry journal with sticky headers, journal-style summaries, entry type filter chips, paginated lazy loading |
| 2026-02-16 | #11 | Food logging: search bar, label chip grid, multi-select, time-of-day meal suggestions, meal source toggle, first label-based entry type |
| 2026-02-16 | — | Incremental seed data: added `seed_version` to `label_tag`, seeding now filters by version so user-removed tag associations aren't re-applied on app update. |
| 2026-02-20 | #58 | Documentation & workflow restructure: slim CLAUDE.md, add implementer agent, add break-down-user-stories and work-technical-task skills, add Claude PR review GitHub Action, delete roadmap and plans |

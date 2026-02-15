# Decision Log

Decisions made during Haven's design phase, with rationale.

| # | Decision | Options Considered | Outcome | Rationale |
|---|---|---|---|---|
| 1 | **Platform** | Flutter (cross-platform), Kotlin + Compose (Android-native) | Kotlin + Jetpack Compose | Android-only for now. Native Compose gives better Room integration, simpler build, and KMP upgrade path to iOS later if needed. |
| 2 | **Database** | Room (SQLite), Realm, remote DB | Room (local-only SQLite) | Privacy-first: all data stays on device. Room has first-class Compose/coroutine support. Google Drive backup deferred. |
| 3 | **Tags on Labels, not Entries** | Tag each Entry at log time, Tag Labels (items) and inherit via join | Tags on Labels with LabelTag join table | Retroactive tagging — tag a food label "dairy" once and all past entries using it are automatically included in correlation queries. No need to re-tag historical data. |
| 4 | **Entry data storage** | JSON blob payload, typed columns | Typed `numericValue` column + `EntryLabel` join table | Queryable without JSON parsing. Prior iterations (AirmedTracking) used blob payloads and it made correlation queries painful. |
| 5 | **Label hierarchy** | Separate tables per level, self-referencing parentId | Self-referencing `parentId` on Label | One table handles multiple hierarchy patterns: body part → symptoms, valence → emotions. Keeps the schema simple. |
| 6 | **Source type** | Separate SourceType table with FK | Text column ("log" or "reflect") on Entry | Only two values, unlikely to grow. Avoids an unnecessary join. Changed from the original model which had a full table. |
| 7 | **Seed data migration** | Wipe and re-seed, migration scripts, onCreate-only | `seedVersion` column + version-gated `onOpen` with `INSERT OR IGNORE` | Never touch user-created data (`isDefault = false`). Seeding runs on `onOpen` using raw `SupportSQLiteDatabase` (no deadlock risk), gated by a SharedPreferences version check so it only runs when `SeedData.VERSION` is bumped. Separate from Room schema migrations. |
| 8 | **Activity as EntryType** | Separate Activity entry type, Exercise as its own type | Activity entry type with Category grouping | Exercise is a Category within Activity, not its own entry type. Categories (Connect, Move, Reflect, Breathe, etc.) organize Activity labels. |
| 9 | **Anchor suggestions** | Separate suggestion model, reuse Activity labels | AnchorActivity links to Activity Labels via FK | Tap a suggestion → creates an Entry of type Activity. No duplicate data — suggestions are a view layer on top of the Activity label system. |
| 10 | **Body font** | Philosopher | Philosopher (unified) | Philosopher works well for both headers and body, simplifying the font stack to a single family. |
| 11 | **DI framework** | Manual DI, Koin, Hilt | Hilt | Standard for Jetpack Compose projects. ViewModel injection works out of the box. |
| 12 | **Repository layer** | Generic base repo, skip repos (inject DAOs directly), thin pass-through repos | Use-case-oriented repos | Repos expose what ViewModels need, not mirror every DAO method. DAOs are query-oriented; repos are domain-oriented (e.g., `insertWithLabels`). One-liner delegation is fine when a ViewModel needs it — don't add methods preemptively. Room DAOs already fill the generic-CRUD role, so no `BaseRepository<T>` needed. |

## Lessons from Prior Iterations

Haven is the third iteration of this concept (after AirmedTracking and EirCompanion). Key takeaways:

- **Kept:** Two-layer model (EntryType + Label), MeasurementType as form driver, sourceType distinction, isEnabled/isDefault for progressive disclosure, preloaded DB with defaults, decoupled data-UI contract
- **Changed:** Clearer naming (EntryType/Label/Tag vs confusing prior names), tags on Labels not Entries, proper join tables instead of embedded data, Kotlin/Compose instead of Flutter

## Open Questions

- Correlation engine approach (open to local ML)
- Notification system details
- Reflect mode form design

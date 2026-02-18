---
name: room-migration
description: Use when any Room @Entity, @Dao, or HavenDatabase.kt changes require a schema version bump and migration
---

When a Room schema change is needed, follow these steps in order:

1. **Identify the change** — which entity field is being added/removed/renamed?
2. **Increment version** in `HavenDatabase.kt` `@Database(version = N)`
3. **Write the Migration object:**
   val MIGRATION_N_TO_M = object : Migration(N, M) {
       override fun migrate(db: SupportSQLiteDatabase) {
           // ALTER TABLE or CREATE TABLE statement
       }
   }
4. **Register it** in the database builder: `.addMigrations(MIGRATION_N_TO_M)`
5. **Never use** `fallbackToDestructiveMigration()` — user data must survive
6. **Seed data check** — if adding a new seed item, give it the new `SeedData.VERSION` value and use `INSERT OR IGNORE`
7. **Export schema** — Room auto-exports to `app/schemas/` via the KSP config already set; commit the new JSON file
8. **Write a migration test** using `MigrationTestHelper` from `room.testing`

After completing all steps, run `./gradlew testDebugUnitTest` to verify.

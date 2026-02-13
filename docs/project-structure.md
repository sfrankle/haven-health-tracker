# Project Structure

## Package Layout

```
com.haven.app/
  ui/
    tend/          # Logging page
    trace/         # History page
    weave/         # Insights page
    anchor/        # Grounding suggestions
    settings/      # Preferences
    components/    # Shared composables
  data/
    entity/        # Room entities
    dao/           # Data access objects
    repository/    # Repository layer
  di/              # Hilt modules
```

## Building

```bash
./gradlew assembleDebug          # Build debug APK
./gradlew test                   # Run unit tests
./gradlew connectedAndroidTest   # Run instrumented tests
./gradlew lint                   # Run lint checks
```

## Design System

Haven uses a soft, calming visual language:

- **Colors:** Sage green, lavender, off-white
- **Fonts:** Philosopher (headers and body)
- **Components:** Pill-shaped buttons, rounded cards, soft shadows

## Key Design Decision

Tags are applied to **Labels, not Entries**. Tag a food label "dairy" once, and every past and future entry using that label is automatically included in correlation queries. No retroactive re-tagging needed.

See [decisions.md](decisions.md) for the full decision log.

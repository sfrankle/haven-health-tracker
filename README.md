# Haven

A gentle, private-first Android app for tracking food, feelings, habits, symptoms, and moments of reflection. Haven helps you notice patterns in your daily life through correlation discovery — not rigid metrics, streaks, or judgment.

## Why Haven?

Most health trackers push calorie counts, streaks, and scores. Haven takes a different approach:

- **Non-judgmental** — no scores, streaks, or "good/bad" framing
- **Private** — all data stays on your device, no cloud sync, no accounts
- **Correlation-focused** — discover patterns like "7 out of 10 times I ate dairy, I felt bloated"
- **Low-friction** — minimal taps to log, progressive disclosure to avoid overwhelm

Built for people who want to understand themselves better without being told what to do about it.

## Features

Haven is organized around five pages:

| Page | Purpose |
|------|---------|
| **Tend** | Log entries throughout the day — food, sleep, hydration, emotions, symptoms, activities |
| **Trace** | Review your history, grouped by day with filtering |
| **Weave** | Explore correlations and patterns across entry types |
| **Anchor** | Get calming activity suggestions based on effort level |
| **Settings** | Toggle entry types and labels, customize preferences |

### Entry Types

- **Sleep** — hours slept
- **Hydration** — fluid intake with quick-add buttons
- **Food** — multi-select labels with tags (dairy, FODMAP, gluten, etc.) for correlation
- **Emotion** — valence-based picker (pleasant/neutral/unpleasant) with specific emotions
- **Symptom** — body part + symptom selection with optional severity
- **Activity** — things you do, organized by category (Move, Create, Ground, Breathe, etc.)

## Tech Stack

- **Language:** Kotlin
- **UI:** Jetpack Compose
- **Database:** Room (local SQLite)
- **DI:** Hilt
- **Async:** Coroutines + Flow
- **Architecture:** Single-activity, MVVM with feature packages

## Development Status

Haven is in early development. See [docs/roadmap.md](docs/roadmap.md) for the phased plan and [docs/changelog.md](docs/changelog.md) for completed work.

For project structure, build commands, design system, and architecture details, see [docs/project-structure.md](docs/project-structure.md).

## License

This project is not currently licensed for redistribution.

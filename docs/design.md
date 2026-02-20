# Design System

Haven's visual language is soft, calming, and non-judgmental. Colour is used as wayfinding — each entry type and tab has its own gradient palette so users always know where they are.

## Colors

### Entry Type Palettes

Each entry type uses a full-bleed gradient background. Cards and surfaces on that screen are tinted with a pale wash of the same palette.

| Entry Type | Gradient |
|---|---|
| Hydration | Sky blue → soft teal → white |
| Food | Warm green → sage → soft mint |
| Sleep | Deep lavender → soft indigo → dusty blue |
| Emotion | Blush rose → soft peach → warm cream |
| Symptom | Warm sand → muted amber → pale gold |
| Activity | Soft coral → dusty rose → muted violet |

On the Tend hub screen, each entry type button uses its matching gradient as a subtle tint so the colour system is immediately visible.

### Tab Palettes

| Tab | Gradient | Feel |
|---|---|---|
| Tend | Warm cream → soft peach | Welcoming, neutral hub |
| Trace | Cool slate → soft blue-grey → near-white | Analytical, clear-headed |
| Weave | Warm amber → honey → soft gold | Reflective, journal-like |
| Anchor | Soft blush → pale mauve → warm white | Calm, intentional |
| Settings | Warm off-white → pale grey | Neutral utility |

## Typography

Philosopher is used throughout — bold for headers, regular for body text. Bundled as TTF in `app/src/main/res/font/` (offline-first, no external dependencies).

| Usage | Weight |
|---|---|
| Display, headlines, titles | Philosopher Bold |
| Body, labels | Philosopher Regular |

### Editorial Header Pattern

Every screen uses a two-level header:
- **Small label** (light weight, muted) — contextual descriptor, e.g. "Logging", "Your patterns"
- **Large bold display title** below it — the screen name or key subject

This creates an editorial hierarchy that feels calm and intentional rather than app-like.

## Components

- **Buttons:** Pill-shaped with soft shadows
- **Layout:** Rounded grid for entry type buttons; each button tinted with its entry type colour
- **Cards:** Rounded, tinted with a pale wash of the screen's gradient palette; large ghosted/watermark icon in the card background for depth
- **Animations:** Smooth transitions
- **Bottom nav:** Outline icons only, no labels; active state indicated by subtle weight/size shift rather than colour fill

## Principles

- No scores, streaks, or pressure indicators
- Low-friction logging (minimal taps)
- Progressive disclosure (start simple, expand via Settings)
- Non-judgmental language throughout — no "good/bad" framing in any user-facing text
- Accessible: clear navigation, readable fonts, intuitive icons
- All assets local (offline-first, no external dependencies)
- Colour as wayfinding — palette shifts reinforce where the user is without requiring labels or breadcrumbs

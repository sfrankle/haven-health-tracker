# Haven Visual Style

Version: 1.0
Scope: Material 3 visual tokens, typography, components, and motion.

## Visual Intent
Haven's visual language is soft, calming, and non-judgmental.

Design adjectives:
- Airy
- Grounded
- Quietly warm
- Notebook-like

## Color System
### Core Palette
- `primary`: `#2E2A3A` (deep night)
- `secondary`: `#7FA38A` (dusty sage)
- `tertiary`: `#A77CA4` (muted mauve)
- `accent`: `#F6C177` (warm glow, sparse use)
- `background`: `#F4F1F3` (soft fog)
- `surfaceVariant`: `#EAE6EB`

### Brand Gradient (Icon / Hero Only)
- `#D8A7B1` -> `#A77CA4` -> `#5E8B8C`

Usage guidance:
- 60% neutral surfaces
- 30% muted brand tones
- 10% accent
- Keep warm accent meaningful and rare

Dark mode guidance:
- Use deep charcoal/navy surfaces
- Avoid pure black for primary surfaces

## Typography
Primary app font family: Philosopher (bundled locally in `app/src/main/res/font/`).

Decision status:
- V1 locked to Philosopher for brand voice consistency.
- Inter / Plus Jakarta Sans are optional future alternatives, not current defaults.

Roles:
- Display / Headline / Title: Philosopher Bold
- Body / Label: Philosopher Regular

Type guidance:
- Follow Material 3 type scale sizing
- Slightly increase letter spacing for larger headers when readability benefits
- Favor readability over stylization
- Avoid overly sharp geometric fonts and decorative/script fonts in interface text

## Components
### Buttons
- Pill-shaped or large-rounded corners
- Calm contrast, no aggressive saturated fills

### Cards
- Rounded corners
- Subtle elevation only
- Spacious internal padding

### Entry Type Grid
- Rounded grid tiles
- Clear icon + text label
- Consistent height and touch targets

## Motion
- Prefer fade and gentle easing transitions
- Keep transitions short and low-amplitude
- Avoid bounce, spring-heavy, or celebratory motion
- Any decorative twinkle should be rare and non-blocking

## Anti-Patterns
Do not introduce:
- Gamified visuals (trophies, streak flames, confetti)
- Character mascots
- Dense dashboards on primary logging screens
- Red or warning tones for non-error states

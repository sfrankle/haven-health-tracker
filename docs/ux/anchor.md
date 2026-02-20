# UX Spec: Anchor Screen (Support Menu v1)

Version: 1.0
Status: Target spec for MVP UX refactor

## Goal
Help users pick a supportive action quickly when bored, overwhelmed, or dysregulated.

Primary success metric:
- User can start an Anchor activity in <= 2 taps.

## Emotional Target
- Safe
- Clear
- Low-pressure
- Practical

## Screen Purpose
Anchor is a menu of options, not a tracking dashboard.

Anchor is not:
- A progress report
- A scorecard
- A long instructional article

## Layout Specification

### Zone 1: Orientation (Top)
Content:
- Title: "Anchor"
- One-line helper copy: "Pick what fits your energy right now."

Rules:
- Neutral language only
- No urgency cues

### Zone 2: Energy Menu (Primary)
Content:
- Section A: Low Energy
- Section B: Medium Energy
- Section C: High Energy

Each section contains 3-6 options as tappable rows or chips.

Rules:
- Show sections in this order: Low -> Medium -> High
- Keep option labels concrete and short (e.g., "Drink water", "Step outside")
- Avoid abstract or motivational phrasing
- Do not hide all options behind nested flows

Interaction:
- Tap option -> start action immediately or open a minimal detail sheet
- Optional: long-press to pin/favorite

### Zone 3: Quick Repeat (Optional)
Content:
- "Recent" or "Pinned" row for 1-tap repeats

Rules:
- Visually secondary to energy menu
- Max 3 items visible

## Interaction Rules
- No required typing
- No required setup before first use
- Returning from an action preserves scroll position and section state

## Accessibility
- Large tap targets for all menu options
- Section headers always visible and readable
- Color is not the only way to distinguish energy levels

## Acceptance Criteria
- Anchor presents three energy-based sections (Low/Medium/High)
- Each section includes at least 3 immediate options
- User can start any visible option in <= 2 taps
- No analytics/streak/score content appears on Anchor

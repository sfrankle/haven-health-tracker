# Screen Archetypes

Version: 1.0
Scope: Reusable layout patterns for Haven screens.

Use these archetypes to avoid inventing new layout logic per feature.

## A1. Primary Action Screen (Tend)
Purpose: Complete a log action in under 3 taps.

Structure:
- Top: light orientation (date/context)
- Middle: primary action controls (entry type grid)
- Bottom: optional short recent activity section

Rules:
- No analytics content on this screen
- No competing secondary CTAs
- Bottom navigation remains visible

## A2. Journal List Screen (Trace)
Purpose: Review chronological logs and open details.

Structure:
- Top: lightweight filters + date range
- Middle: list of entries grouped by date
- Bottom: bottom navigation

Rules:
- Fast scanning over dense metadata
- Clear empty state with non-judgmental language
- Detail opens should preserve list scroll position

## A3. Insight Screen (Weave)
Purpose: Explore patterns and correlations when user chooses.

Structure:
- Top: timeframe and filter controls
- Middle: insight cards with plain-language observations
- Bottom: optional explanatory guidance

Rules:
- Insights are observational, never prescriptive
- Confidence/coverage shown clearly when available
- Avoid celebratory or alarming visual treatment

## A4. Supportive Tool Screen (Anchor)
Purpose: Offer a menu of supportive options based on current energy level.

Structure:
- Top: simple context title
- Middle: three energy sections (Low, Medium, High)
- Bottom: optional recent or pinned options

Rules:
- Keep actions immediate and low-friction
- Prioritize choice clarity over depth
- Each option should be startable in one tap
- Do not mix with unrelated analytics content

## A5. Settings And Privacy Screen
Purpose: Control preferences, data, and account-level behavior.

Structure:
- Top: grouped settings sections
- Middle: toggles and selectors
- Bottom: critical actions separated visually

Rules:
- Privacy and export controls are easy to locate
- Potentially destructive actions require explicit confirmation

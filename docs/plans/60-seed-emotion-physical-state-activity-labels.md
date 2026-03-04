# Plan: Seed Emotion, Physical State, and Activity Labels (#60)

## Overview

Expand `SeedData.kt` to add labels, tags, and label-tag mappings for three entry types that currently have no labels: Emotion (id=2), Physical State (id=5, renamed from Symptom), and Activity (id=6). Bump `SeedData.VERSION` from 2 → 3 so existing installs pick up the new data. Add an UPDATE in the seed callback to rename the "Symptom" entry type to "Physical State".

## Label Structure

### Emotion — nested (valence parent → emotion child)

Parents (entry_type_id=2, no parentId):
- Pleasant (id=29)
- Neutral (id=30)
- Unpleasant (id=31)

Children (entry_type_id=2, parentId = parent above):

**Pleasant (parentId=29):** Happy, Content, Hopeful, Excited, Energised, Peaceful, Playful, Proud, Trusting
**Neutral (parentId=30):** Calm, Balanced, Comfortable, Thoughtful, Mellow, Fulfilled
**Unpleasant (parentId=31):** Anxious, Overwhelmed, Irritable, Sad, Frustrated, Angry, Fearful, Down

IDs: 32–49 (children, in order above)

### Physical State — flat (entry_type_id=5)

Difficult states: Headache, Fatigue, Nausea, Bloating, Cramps, Brain fog, Back pain, Joint pain, Sore throat, Congestion, Low energy

Positive/neutral states: Settled stomach, High energy, Clear-headed, Pain-free, Well-rested

IDs: 50–66

### Activity — flat with category_id (entry_type_id=6)

| Label | Category | category_id |
|-------|----------|-------------|
| Walk | Move | 2 |
| Run | Move | 2 |
| Cycling | Move | 2 |
| Swimming | Move | 2 |
| Strength training | Move | 2 |
| Yoga | Move | 2 |
| Stretching | Move | 2 |
| Meditation | Reflect | 3 |
| Deep breathing | Breathe | 4 |
| Gardening | Ground | 7 |
| Housework | Structure | 8 |

IDs: 67–77

## Tags

### New emotion tags (ids 10–12)
- `nervous-system` / tag_group="emotion" (id=10)
- `high-arousal` / tag_group="emotion" (id=11)
- `low-arousal` / tag_group="emotion" (id=12)

### New physical state tags (ids 13–19)
- `pain` / tag_group="symptom" (id=13)
- `energy` / tag_group="symptom" (id=14)
- `nervous-system` / tag_group="symptom" (id=15)
- `gut` / tag_group="symptom" (id=16)
- `digestive` / tag_group="symptom" (id=17)
- `FODMAP` / tag_group="symptom" (id=18)
- `hormonal` / tag_group="symptom" (id=19)
- `musculoskeletal` / tag_group="symptom" (id=20)
- `immune` / tag_group="symptom" (id=21)
- `head` / tag_group="symptom" (id=22)

### New activity tags (ids 23–28)
- `cardio` / tag_group="activity" (id=23)
- `high-intensity` / tag_group="activity" (id=24)
- `low-intensity` / tag_group="activity" (id=25)
- `strength` / tag_group="activity" (id=26)
- `flexibility` / tag_group="activity" (id=27)
- `grounding` / tag_group="activity" (id=28)

## Label-Tag Mappings

### Emotion label-tags
| Label | Tags |
|-------|------|
| Anxious | nervous-system(10), high-arousal(11) |
| Overwhelmed | nervous-system(10), high-arousal(11) |
| Irritable | high-arousal(11) |
| Angry | high-arousal(11) |
| Calm | nervous-system(10), low-arousal(12) |
| Peaceful | nervous-system(10), low-arousal(12) |
| Mellow | low-arousal(12) |
| Comfortable | low-arousal(12) |
| Balanced | nervous-system(10), low-arousal(12) |
| Excited | high-arousal(11) |
| Energised | high-arousal(11) |
| Sad | low-arousal(12) |
| Down | low-arousal(12) |

### Physical state label-tags
| Label | Tags |
|-------|------|
| Headache | head(22), pain(13) |
| Fatigue | energy(14), nervous-system(15) |
| Nausea | gut(16), digestive(17) |
| Bloating | gut(16), digestive(17), FODMAP(18) |
| Cramps | gut(16), hormonal(19), pain(13), digestive(17) |
| Brain fog | energy(14), nervous-system(15) |
| Back pain | musculoskeletal(20), pain(13) |
| Joint pain | musculoskeletal(20), pain(13) |
| Sore throat | immune(21) |
| Congestion | immune(21) |
| Low energy | energy(14), nervous-system(15) |
| Settled stomach | gut(16), digestive(17) |
| High energy | energy(14), nervous-system(15) |
| Clear-headed | energy(14), nervous-system(15) |
| Pain-free | pain(13) |
| Well-rested | energy(14) |

### Activity label-tags
| Label | Tags |
|-------|------|
| Walk | cardio(23), low-intensity(25) |
| Run | cardio(23), high-intensity(24) |
| Cycling | cardio(23), high-intensity(24) |
| Swimming | cardio(23), high-intensity(24) |
| Strength training | strength(26), high-intensity(24) |
| Yoga | flexibility(27), grounding(28) |
| Stretching | flexibility(27), low-intensity(25) |
| Meditation | grounding(28) |
| Deep breathing | grounding(28) |
| Gardening | grounding(28), low-intensity(25) |
| Housework | low-intensity(25) |

## Implementation Steps

### 1. Update `SeedData.kt`
- Bump `VERSION` from 2 → 3
- Add `emotionParentLabels`, `emotionChildLabels` (with `parentId` set)
- Add `physicalStateLabels` (flat, `seedVersion = 3`)
- Add `activityLabels` (flat with `categoryId`, `seedVersion = 3`)
- Add `emotionTags`, `symptomTags`, `activityTags` (all `seedVersion = 3`)
- Add `emotionLabelTags`, `physicalStateLabelTags`, `activityLabelTags` (`seedVersion = 3`)

### 2. Update `SeedDatabaseCallback.kt`
- In `seedLabels()`: include new label lists in `allLabels`
- In `seedTags()`: include `emotionTags + symptomTags + activityTags`
- In `seedLabelTags()`: include new label-tag lists
- Add `renameSymptomEntryType(db)` call before `seedMeasurementTypes` — runs `UPDATE entry_type SET name = 'Physical State' WHERE name = 'Symptom'` unconditionally (idempotent)
- Update `SeedData.entryTypes` — change id=5 name from "Symptom" to "Physical State"

### 3. Update `docs/schema.sql`
- Update entry_type seed row for id=5: "Symptom" → "Physical State"
- Add new seed label/tag/label_tag comment blocks

### 4. Update `docs/decisions.md`
- Record decision: Physical State replaces Symptom (pre-launch rename, no formal migration)

### 5. Tests
Existing tests pass unchanged (they test the gating logic, not data content). Add:
- Test that verifies `SeedData.entryTypes` contains "Physical State" and not "Symptom"
- Test that verifies label counts per entry type are non-zero for Emotion, Physical State, Activity

## Key Constraints
- All new labels/tags/label_tags use `seedVersion = 3`
- `INSERT OR IGNORE` pattern means re-runs are safe
- The UPDATE for renaming is idempotent (if already "Physical State", no-op)
- Emotion parent labels have no `parentId`; children reference parent by hardcoded id
- Activity labels use `categoryId` matching the `category` table seed rows

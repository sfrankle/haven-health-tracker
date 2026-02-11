Goal: 
a mobile app designed to help you gently track your internal world — including food, feelings, habits, symptoms, and moments of reflection — to support nervous system regulation and everyday happiness. 

## Problem Statement

### The Problem
Many people struggle to understand patterns in their daily experiences, emotions, and physical symptoms. Traditional tracking apps often focus on rigid metrics, calorie counting, or streak-based systems that can feel overwhelming or judgmental, especially for neurodivergent individuals or those managing chronic symptoms.

### Personal Need
A private, non-judgmental space to explore personal patterns and correlations between daily experiences, emotions, and physical well-being to support self-understanding and gentle self-care practices.

## Solution Overview

### Core Concept
The app provides a gentle, private-first approach to self-tracking focused on nervous system regulation and happiness. The app emphasizes correlation discovery over rigid metrics, helping users understand their personal patterns without judgment or pressure.


# User Experience Design

## App Structure
**Main Pages:**
- **Anchor**: Capacity-based activity suggestions
- **Tend**: Logging and reflection
- **Trace**: History and timeline view
- **Weave**: Trends and correlations
- **Settings**

### Key Features

**Tend Page:**
- **Grid of rounded buttons** for entry types: Food, Emotion, Hydration, Sleep, Symptom, Exercise, etc.
- **Two logging modes:**
  - *In the moment* logging (timestamped to current time)
  - *Reflective* logging (end-of-day, date-associated)
- **"Reflect" button** visually distinct for end-of-day summary
- All entry types use defined measurement types
- Decoupled data storage - UI can change without breaking historical data


**Trace Page (Timeline Review):**
- View past logs organized by day
- Entries grouped by category or chronologically
- Pattern visualization across multiple days
- Filters, date jumping, data export for sharing with healthcare providers


**Heal Page (Insights & Correlations):**
- Real-time correlation analysis with examples like:
  - "7 out of 10 times you ate cheese, you felt bloated"
  - "On days with >6 cups of water, you felt calmer"
- **Correlation engine supports:**
  - Food tags (dairy, FODMAP, etc.)
  - Time-of-day patterns (e.g., stress-related symptoms 9-5)
  - Boolean and scale-based analysis
- Analysis triggered on page load or manually by user
- No medical claims - gentle pattern recognition only

**Anchor Page (Grounding Activity Suggestions):**
- A gentle nudge toward steadiness when you’re not sure what you need.
- Offers three calming activity suggestions designed to help you reconnect with your body, breath, or sense of self
- Includes a shuffle option to explore different possibilities until something resonates
- Each activity includes:
  - A short title (with an optional icon)
  - Tags that indicate if it supports happiness, regulation, nervous system balance, etc
  - A user-defined effort rating (1–5), so suggestions meet you where you are

**Settings Page:**
- Toggle activities on/off per category
- Helps avoid overwhelm by starting simple and expanding over time
- App preferences and customization options


**Notification System:**
- **Food reminders:** Customizable meal times (9am/1pm/7pm)
- **Water reminders:** Hourly during user-defined time range
- **Symptom follow-up:** Optional reminder X minutes after food logging
- **End-of-day reflection:** Prompt for daily mood/emotion summary

### Unique Value Proposition
- **Local-only data storage** (no cloud, data stays on device)
- **No judgment approach** (no scores, streaks, or pressure)
- **Low-friction logging** (simple inputs, not overwhelming forms)
- **Adaptable and customizable** to individual needs
- **Focus on correlation discovery** rather than rigid tracking


### Development Approach
- Personal development project
- No monetization strategy required
- Focus on learning and personal utility
- Potential for open-source sharing if desired in the future


## User Journey

**Daily Flow:**
1. **Throughout the day:** User logs entries via Tend page (food, symptoms, feelings, activities, etc)
   - Entries saved with current timestamp (editable)
   - App sends periodic reminders for logging
2. **End of day:** Reminder to reflect via Tend page
   - Curated reflection form
   - Entries associated with date rather than specific time
3. **Ongoing:** User can review history on Trace page
4. **As needed:** User explores patterns on Weave page (real-time analysis)
5. **Anytime:** User visits Anchor page for gentle activity suggestions based on their current capacity or mood
   - Three calming suggestions shown, with option to shuffle
   - Activities are tagged and effort-rated to meet user where they're at

## Key User Stories
- As someone tracking food sensitivities, I want to 
    - quickly log what I ate, with flexible ingredient entry
    - intuitively log how I felt afterward (symptoms, energy, emotion)
    - add entries in the moment or reflect later
    
    so that I can
    - better understand how food may be affecting my body and mood

- As someone exploring patterns in my wellbeing, I want to 
    - view trends based on my logs
    - receive gentle insights, not overwhelming graphs
    - filter by category or type (e.g. hydration, emotion, sleep)
    
    so that I can
    - discover what supports me over time

- As someone checking in with myself, I want to...
    - log my state without navigating complex forms
    - use toggles or sliders instead of freeform text
    - edit timestamps if I forgot to log earlier
    
    So that I can...
    - track consistently without it becoming a burden

- As someone who sometimes needs help regulating, I want to...
    - access calming suggestions when I feel scattered
    - see only activities that feel doable for me right now
    - rate how easy or hard things feel for me personally

    So that I can...
    - find grounding support that matches my capacity


## UI/UX Considerations

**Design System:**
- **Color palette:** Soft sage green, lavender, and off-white
- **Typography:** 
  - Headers: Philosopher font
  - Body: Quicksand or Nunito (to be determined)
- **Visual elements:**
  - Pill-shaped buttons with soft shadows
  - Smooth animations and transitions
  - Rounded grid layout for main entry buttons
- **Offline-first:** Local font and color assets (no external dependencies)

**Interaction Design:**
- **Low-friction logging:** Minimal taps, intuitive button layouts
- **Non-judgmental interface:** No scores, streaks, or pressure indicators
- **Progressive disclosure:** Start with few entry types, expand over time via Settings
- **Accessible design:** Clear navigation, readable fonts, intuitive icons
- **Decoupled data-UI:** Interface can evolve without breaking historical data

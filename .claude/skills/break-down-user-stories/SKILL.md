---
name: break-down-user-stories
description: Analyze user stories in a GitHub milestone and create detailed technical task issues. Use when asked to "break down" a milestone or user stories into implementation tasks.
---

## Process

### 1. Read the milestone
Fetch all issues in the target milestone labeled `user-story`:
```bash
gh api "repos/$(gh repo view --json nameWithOwner -q .nameWithOwner)/issues?milestone=<NUMBER>&labels=user-story&state=open&per_page=100"
```

Also fetch existing technical tasks in the same milestone:
```bash
gh api "repos/$(gh repo view --json nameWithOwner -q .nameWithOwner)/issues?milestone=<NUMBER>&labels=technical-task&state=all&per_page=100"
```

Parse the body of each technical task to identify which user story it contributes to (look for "Contributes to #N" in the body).

### 2. Audit the codebase
For each user story, explore the codebase to understand:
- What already exists (screens, entities, DAOs, repos, ViewModels)
- What's missing or incomplete relative to the story's requirements
- Dependencies between stories

### 3. Propose technical tasks
Evaluate existing technical tasks:
- Are they well-formed (clear acceptance criteria, proper user story link, appropriate scope)?
- Do they match current user story requirements?
- Are they still relevant or have they been superseded?

Present a summary to the user:
- Which user stories already have technical tasks (and what they are)
- Which existing technical tasks should be **removed** (poorly scoped, irrelevant, or duplicates)
- Which existing technical tasks should be **updated** (incomplete acceptance criteria, scope changes)
- Which user stories are already fully implemented
- Which user stories need new technical tasks, and what tasks are needed
- Suggested ordering / dependencies between tasks

**Wait for user approval before making changes (closing/editing issues, creating new ones).**

### 4. Clean up existing issues
For issues marked for removal or update:

**Close issues:**
```bash
gh issue close <NUMBER> --comment "Closing: <reason (poorly scoped/duplicate/superseded)>"
```

**Update issues:**
```bash
gh api "repos/$(gh repo view --json nameWithOwner -q .nameWithOwner)/issues/<NUMBER>" \
  -f body="$(cat <<'EOF'
## Summary
<updated summary>

## User Story
Contributes to #N

## Acceptance Criteria
- [ ] Updated criterion 1
- [ ] Updated criterion 2
- [ ] Tests: <what to test>

## Notes
<updated notes>
EOF
)"
```

### 5. Create new issues
For each approved technical task, create a GitHub issue:

```bash
gh api "repos/$(gh repo view --json nameWithOwner -q .nameWithOwner)/issues" \
  -f title="<title>" \
  -f body="$(cat <<'EOF'
## Summary
<what this task accomplishes>

## User Story
Contributes to #N

## Acceptance Criteria
- [ ] Criterion 1
- [ ] Criterion 2
- [ ] Tests: <what to test>

## Notes
<files likely affected, design considerations, dependencies>
EOF
)" \
  -f milestone=<MILESTONE_NUMBER>
```

Then add labels:
```bash
gh issue edit <NUMBER> --add-label "technical-task" --add-label "<area-label>"
```

Area labels: `Tend-Page`, `Trace-Page`, `Weave-Page`, `Anchor-Page`, `Settings-Page`, `Data`, `UI`

### 6. Summary
After creating all issues, present a table:
| Issue | Title | User Story | Labels |
|-------|-------|------------|--------|
| #N    | ...   | #M         | ...    |

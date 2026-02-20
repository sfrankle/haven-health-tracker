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

### 2. Audit the codebase
For each user story, explore the codebase to understand:
- What already exists (screens, entities, DAOs, repos, ViewModels)
- What's missing or incomplete relative to the story's requirements
- Dependencies between stories

### 3. Propose technical tasks
Present a summary to the user:
- Which user stories are already fully implemented
- Which need work, and what technical tasks are needed
- Suggested ordering / dependencies between tasks

**Wait for user approval before creating issues.**

### 4. Create issues
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

### 5. Summary
After creating all issues, present a table:
| Issue | Title | User Story | Labels |
|-------|-------|------------|--------|
| #N    | ...   | #M         | ...    |

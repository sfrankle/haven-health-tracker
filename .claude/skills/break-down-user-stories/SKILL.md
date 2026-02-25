---
name: break-down-user-stories
description: Analyze user stories in a GitHub milestone and create detailed technical task issues. Use when asked to "break down" a milestone or user stories into implementation tasks.
---

## Process

### 1. Read the milestone
Fetch all user stories and existing technical tasks in the target milestone:
```bash
gh issue list --milestone "<MILESTONE TITLE>" --label "user-story" --state open --limit 100 --json number,title,body
gh issue list --milestone "<MILESTONE TITLE>" --label "technical-task" --state all --limit 100 --json number,title,body,state
```

Parse each technical task body for "Contributes to #N" to map tasks → user stories.

### 2. Check for missing user stories
Before breaking down tasks, compare the milestone's user stories against `docs/spec.md` and related docs. Ask: are there user stories missing from this milestone that the spec implies? Flag any gaps — don't silently skip them.

### 3. Audit the codebase
For each user story, explore the codebase to understand:
- What already exists (screens, entities, DAOs, repos, ViewModels)
- What's missing or incomplete relative to the story's requirements
- Dependencies between stories

### 4. Think ahead to future milestones
For each proposed technical task, consider: does the implementation approach here constrain or create work in later milestones? Call out cases where:
- Seed data decisions affect future correlation/insights quality (e.g. tag coverage)
- A "flat for MVP" UI decision relies on a data model that supports hierarchy later — note that explicitly
- Shared infrastructure (shared composables, shared patterns) should be built once, not per-story

### 5. Propose technical tasks
Evaluate existing technical tasks:
- Are they well-formed (clear acceptance criteria, proper user story link, appropriate scope)?
- Do they match current user story requirements?
- Are they still relevant or have they been superseded?

Identify cross-cutting concerns — shared infrastructure needed by multiple stories (e.g. a shared date picker, a shared label grid) should be one task, not duplicated.

Present a summary to the user:
- Which user stories already have technical tasks (and what they are)
- Which existing technical tasks should be **removed** (poorly scoped, irrelevant, or duplicates)
- Which existing technical tasks should be **updated** (incomplete acceptance criteria, scope changes)
- Which user stories are already fully implemented
- Which user stories need new technical tasks, and what tasks are needed
- Suggested ordering / dependencies between tasks
- Any forward-looking risks or constraints identified in step 4

**Wait for user approval before making changes (closing/editing issues, creating new ones).**

### 6. Clean up existing issues
**Close issues:**
```bash
gh issue close <NUMBER> --comment "Closing: <reason>"
```

**Update issues:**
```bash
gh issue edit <NUMBER> --body "<updated body>"
```

### 7. Create new issues
For each approved technical task, create in one command:
```bash
gh issue create \
  --title "<title>" \
  --milestone "<MILESTONE TITLE>" \
  --label "technical-task,<area-label>,ai-authored" \
  --body "## Summary
<what this task accomplishes>

## User Story
Contributes to #N

## Acceptance Criteria
- [ ] Criterion 1
- [ ] Criterion 2
- [ ] Tests: <what to test>

## Notes
<files likely affected, design considerations, dependencies, forward-looking constraints>"
```

Area labels: `Tend-Page`, `Trace-Page`, `Weave-Page`, `Anchor-Page`, `Settings-Page`, `Data`, `UI`

Follow label conventions from CLAUDE.md (e.g. `ai-authored` on all created issues).

### 8. Set blocking relationships between technical tasks
For each dependency identified in step 5, register it using GitHub's native relationship:

```bash
# Get node IDs for the two issues
BLOCKED_ID=$(gh issue view <BLOCKED_ISSUE> --json id -q .id)
BLOCKING_ID=$(gh issue view <BLOCKING_ISSUE> --json id -q .id)

# "#BLOCKED_ISSUE is blocked by #BLOCKING_ISSUE"
gh api graphql -f query="mutation {
  addBlockedBy(input: { issueId: \"$BLOCKED_ID\", blockingIssueId: \"$BLOCKING_ID\" }) {
    clientMutationId
  }
}"
```

Also note the dependency in the blocked issue's Notes section: `Blocked by: #N`

### 9. Link technical tasks to their user stories
After all tasks are created, update each user story body to append a `## Technical Tasks` checklist. GitHub automatically checks off items as the referenced issues close — giving a clear "ready to close" signal without closing the story itself (that remains a manual human action per CLAUDE.md).

```bash
gh issue edit <USER_STORY_NUMBER> --body "<existing body>

## Technical Tasks
- [ ] #T1 Task title
- [ ] #T2 Task title
- [ ] #T3 Task title"
```

Fetch the existing body first with `gh issue view <N> --json body -q .body` so you don't overwrite it.

### 10. Summary
After creating all issues, present a table:
| Issue | Title | User Story | Blocked by |
|-------|-------|------------|------------|
| #N    | ...   | #M         | #X, #Y     |

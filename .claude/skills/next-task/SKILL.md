---
name: next-task
description: Determine the next technical task to pick up from a milestone. Use when asked "what should I work on next?" or "what's the next task?". Returns a prioritised list of unblocked tasks.
---

## Process

### 0. Check we're on main
```bash
git branch --show-current
git status --short
```

If the current branch is **not `main`**:
- Check if there's an open PR for this branch:
  ```bash
  gh pr list --head <branch> --json number,title,state
  ```
- If a PR exists (draft or open), tell the user:
  > "It looks like you're mid-task on branch `<branch>` with PR #N open. Want to keep going on that, or switch to main?"
- If no PR exists, tell the user:
  > "You're on branch `<branch>` with no open PR. Please merge or switch to `main` before picking up a new task."

Do not proceed until the user confirms they want to continue to next-task.

### 1. Get open technical tasks for the milestone
```bash
gh issue list --milestone "<MILESTONE TITLE>" --label "technical-task" --state open --limit 100 --json number,title,body
```

If the user didn't specify a milestone, check the current git branch for context, or ask which milestone they're working on.

### 2. Filter to unblocked tasks
For each open task, check whether it has open blockers:
```bash
gh api "repos/sfrankle/haven-health-tracker/issues/<N>" --jq '.issue_dependencies_summary'
```

A task is **unblocked** if `blocked_by == 0`.

Batch these checks — run one per issue, but do it methodically. Build two lists:
- **Unblocked:** `blocked_by == 0`
- **Blocked:** `blocked_by > 0` — note which issues are blocking it (parse "Blocked by: #X" from body)

### 3. Prioritise unblocked tasks
Order unblocked tasks by:
1. **Blocking others first** — tasks with `blocking > 0` unblock downstream work; do these first
2. **Cross-cutting / shared infrastructure** — tasks that multiple stories depend on
3. **Issue number** — lower number (created earlier) as a tiebreaker

### 4. Present the recommendation
Show the top recommendation clearly, followed by the full unblocked list:

```
## Next task to pick up

**Recommended: #N — Title**
<one sentence on why: e.g. "blocks 3 other tasks">

## All unblocked tasks
| # | Title | Blocks |
|---|-------|--------|
| #N | ... | #X, #Y |
| #M | ... | — |

## Blocked tasks (waiting on others)
| # | Title | Blocked by |
|---|-------|------------|
| #P | ... | #Q (open) |
```

Then suggest: `To start work, say "work on #N"` — which will invoke the `work-technical-task` skill.

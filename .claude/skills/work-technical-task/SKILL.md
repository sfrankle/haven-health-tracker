---
name: work-technical-task
description: Pick up a technical task issue, plan it, implement it, and open a draft PR. Use when asked to "work on #N" or "pick up #N".
---

## Process

### 1. Read the issue
```bash
gh api "repos/$(gh repo view --json nameWithOwner -q .nameWithOwner)/issues/<N>"
```

Extract: title, acceptance criteria, linked user story, labels, notes.

### 2. Read the user story
Follow the "Contributes to #M" link and read the user story for full product context.

### 3. Explore the codebase
Understand existing patterns, related code, and what needs to change. Read relevant files:
- Existing screens, ViewModels, repositories, DAOs, entities
- Test files for the area
- `docs/spec.md` for product requirements
- `docs/design.md` for visual guidelines
- `docs/schema.sql` for data model

### 4. Create detailed implementation plan
Write the full plan to `docs/plans/<plan-name>.md`. Use the `superpowers:writing-plans` skill to create a detailed, step-by-step plan with exact file paths, code, commands, and test expectations.

### 5. Post summary comment on the issue
Post a **summary** of the plan as a comment on the issue — not the full detail, just enough for a human to understand and approve the approach:

```bash
gh api "repos/$(gh repo view --json nameWithOwner -q .nameWithOwner)/issues/<N>/comments" \
  -f body="$(cat <<'EOF'
## Implementation Plan

**Approach:** <2-3 sentences>

**What will change:**
- <high-level summary of changes, not individual files>

**Testing approach:**
- <what will be tested>

**Questions / Risks:**
- <any ambiguities or risks>

Full plan: `docs/plans/<plan-name>.md`
EOF
)"
```

### 6. STOP — Wait for approval
Tell the user: "Plan posted on issue #N. Please review and approve before I implement."

**Do not proceed until the user explicitly approves.**

### 7. Implement
After approval:
1. Create a feature branch: `git checkout -b feat/<short-description>`
2. Follow the detailed plan in `docs/plans/`
3. Follow TDD: write failing tests, then implement
4. Commit frequently with clear messages
5. Update `docs/changelog.md`
6. Update other docs if needed (decisions.md, schema.sql, design.md)

### 8. Open draft PR
```bash
gh pr create --draft \
  --title "<concise title>" \
  --body "$(cat <<'EOF'
## Summary
<what this PR does>

Closes #<technical-task-number>
Contributes to #<user-story-number>

## Changes
- <bullet points>

## Test Plan
- <how to verify>
EOF
)"
```

### 9. Clean up
Delete the plan file from `docs/plans/` — it has served its purpose.

### 10. Report
Tell the user: "Draft PR #X is open. When you're ready, mark it ready for review to trigger the automated reviewer."

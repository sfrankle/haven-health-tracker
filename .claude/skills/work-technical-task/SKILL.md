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

### 4. Post implementation plan
Post the plan as a comment on the issue:

```bash
gh api "repos/$(gh repo view --json nameWithOwner -q .nameWithOwner)/issues/<N>/comments" \
  -f body="$(cat <<'EOF'
## Implementation Plan

**Approach:** <2-3 sentences>

**Files to create/modify:**
- `path/to/file.kt` — what changes

**Testing:**
- What will be tested and how

**Questions / Risks:**
- Any ambiguities or risks identified
EOF
)"
```

### 5. STOP — Wait for approval
Tell the user: "Plan posted on issue #N. Please review and approve before I implement."

**Do not proceed until the user explicitly approves.**

### 6. Implement
After approval:
1. Create a feature branch: `git checkout -b feat/<short-description>`
2. Follow TDD: write failing tests, then implement
3. Commit frequently with clear messages
4. Update `docs/changelog.md`
5. Update other docs if needed (decisions.md, schema.sql, design.md)

### 7. Open draft PR
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

### 8. Report
Tell the user: "Draft PR #X is open. When you're ready, mark it ready for review to trigger the automated reviewer."

---
name: work-technical-task
description: Pick up a technical task issue, plan it, implement it, and open a draft PR. Use when asked to "work on #N" or "pick up #N".
---

## Process

### 1. Read the issue and verify it's not blocked
```bash
gh issue view <N> --json number,title,body,labels,milestone,id
```

Extract: title, acceptance criteria, linked user story, labels, notes.

Then check for open blockers:
```bash
gh api "repos/sfrankle/haven-health-tracker/issues/<N>" --jq '.issue_dependencies_summary'
```

If `blocked_by > 0`, find which issues are blocking it by scanning other open issues' `issue_dependencies_summary.blocking` count, or look for "Blocked by: #X" in the body. **Stop and tell the user** which open issues are blocking this task — do not proceed.

### 2. Read the user story
Follow the "Contributes to #M" link and read the user story for full product context.

### 3. Explore the codebase
Understand existing patterns, related code, and what needs to change. Read relevant files:
- Existing screens, ViewModels, repositories, DAOs, entities
- Test files for the area
- `docs/spec.md` for product requirements
- `docs/design/` for visual guidelines and UX principles
- `docs/schema.sql` for data model

### 4. Create detailed implementation plan
Write the full plan to `docs/plans/<plan-name>.md`. Use the `superpowers:writing-plans` skill to create a detailed, step-by-step plan with exact file paths, code, commands, and test expectations.

### 5. Post summary comment on the issue
Post a **summary** of the plan as a comment on the issue — not the full detail, just enough for a human to understand and approve the approach:

```bash
gh issue comment <N> --body "## Implementation Plan

**Approach:** <2-3 sentences>

**What will change:**
- <high-level summary of changes, not individual files>

**Testing approach:**
- <what will be tested>

**Questions / Risks:**
- <any ambiguities or risks>

Full plan: \`docs/plans/<plan-name>.md\`"
```

### 6. STOP — Wait for approval
Tell the user: "Plan posted on issue #N. Please review and approve before I implement."

**Do not proceed until the user explicitly approves.**

### 7. Implement
After approval:
1. Create a branch using the appropriate prefix:
   - `feat/<short-description>` - new features
   - `fix/<short-description>` - bug fixes
   - `refactor/<short-description>` - code refactoring
   - `chore/<short-description>` - maintenance, docs, tooling
2. Follow the detailed plan in `docs/plans/`
3. Follow TDD: write failing tests, then implement
4. Commit frequently with clear messages
5. Update `docs/changelog.md` in your commits
6. Update other docs if needed (decisions.md, schema.sql, docs/design/, docs/ux/)
7. Document significant architectural decisions in `docs/decisions.md` (new patterns, major refactorings, technology choices)

### 8. Verify before opening PR
Run tests and lint to ensure everything passes:
```bash
./gradlew test lint
```

If tests or lint fail, fix them before proceeding. Do not open a PR with failing tests.

If implementation revealed significant scope changes or plan inaccuracies, update the plan file in `docs/plans/` to reflect what actually happened before opening the PR.

### 9. Open draft PR

Write the PR body following `.github/pull_request_template.md`. 

```bash
gh pr create --draft \
  --title "<concise title>" \
  --body "$(cat <<'EOF'
<see template>
EOF
)"
```

Then associate the PR with the same milestone as the technical task:
```bash
MILESTONE=$(gh issue view <N> --json milestone --jq .milestone.title)
gh pr edit --milestone "$MILESTONE"
```

### 10. Clean up
Delete the plan file from `docs/plans/` — it has served its purpose.

### 11. Report
Tell the user: "Draft PR #X is open. When you're ready, mark it ready for review to trigger the automated reviewer."

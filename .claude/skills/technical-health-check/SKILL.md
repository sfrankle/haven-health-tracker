---
name: technical-health-check
description: Proactively identify technical debt, code smells, and improvement opportunities. Use when asked to review technical health or periodically between milestones.
---

## Process

### 1. Scan the codebase
Search for common issues across key areas:

**Code smells:**
- Inconsistent patterns (e.g., some repos follow conventions, others don't)
- Duplicated logic (same code in multiple places)
- Overly complex functions (>50 lines, deeply nested)
- Unclear naming (abbreviations, ambiguous names)
- Missing or outdated KDoc where needed

**Architecture violations:**
- ViewModels accessing DAOs directly (should use repos)
- Business logic in composables (should be in ViewModels)
- Room entities with no migration path
- Inconsistent use of Hilt injection

**Performance concerns:**
- Database queries without indices
- N+1 query patterns
- Unnecessary recomposition in Compose
- Large lists without pagination/virtualization

**Test coverage gaps:**
- Features without unit tests
- Repository logic untested
- ViewModel state transitions untested

**Documentation drift:**
- `docs/decisions.md` missing recent architectural choices
- `docs/schema.sql` out of sync with actual entities
- `docs/design.md` components not matching implementation

### 2. Prioritize findings
Group issues by impact:

**High priority:**
- Architecture violations that block new features
- Performance issues affecting user experience
- Security concerns (should be rare given offline-first design)

**Medium priority:**
- Code smells that make maintenance harder
- Inconsistent patterns across similar code
- Missing tests for critical paths

**Low priority:**
- Minor naming inconsistencies
- Opportunities for DRY improvements
- Documentation polish

### 3. Present findings
Create a report:

```markdown
## Technical Health Check — [Date]

### High Priority Issues
1. **[Issue title]**
   - Where: [file paths or areas]
   - Impact: [why this matters]
   - Suggested fix: [approach]

### Medium Priority Issues
[same format]

### Low Priority Issues
[same format]

### Positive Observations
- [what's working well]
- [good patterns to maintain]
```

### 4. Recommend actions
For each high/medium priority issue, propose:
- Create a technical task issue for tracking?
- Fix it now as part of current work?
- Document as known technical debt for later?

**Wait for user decision before taking action.**

## Notes

- This is diagnostic, not prescriptive — present options, let the user decide priorities
- Balance thoroughness with actionability (don't overwhelm with minor issues)
- Celebrate good patterns found, not just problems
- Consider timing: best run between milestones, not mid-feature

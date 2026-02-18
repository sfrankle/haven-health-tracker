# CI Optimisation Design

**Date:** 2026-02-18
**Issue:** #22

## Goal

Avoid unnecessary CI work and reduce wall-clock time. Two specific cases to handle:
- Skip everything when only docs/markdown files changed
- Skip the build when only test files changed, reusing a prior build artifact

## Current State

Single job: checkout → setup Java/Gradle → `assembleDebug` → `lintDebug` → `testDebugUnitTest` (three separate Gradle invocations). Runs on every PR push regardless of what changed.

## Design

### 1. Docs-only skipping

Add `paths-ignore` to the workflow trigger. If every changed file matches the ignore list, GitHub skips the workflow entirely — no job is queued.

```yaml
on:
  pull_request:
    paths-ignore:
      - '**/*.md'
      - 'docs/**'
```

### 2. Change detection

Use `dorny/paths-filter` inside the job to classify what changed and set step-level conditions. One filter:

- `source` — `app/src/main/**`, `**/*.gradle*`, `gradle/**`

Any push that doesn't touch source files (test-only, config-only, etc.) is treated identically: attempt a cache restore and skip the build if it hits.

### 3. Dual caching strategy

**Gradle build cache** — enabled via `org.gradle.caching=true` in `gradle.properties`. Gradle caches the output of each task keyed by its inputs. On a warm cache, `assembleDebug` and `lintDebug` restore compiled outputs rather than recompiling. `gradle/actions/setup-gradle` saves and restores Gradle home (which includes the build cache) across runs automatically.

**Explicit Actions cache** — after every successful full build, save `app/build/` to GitHub Actions cache, keyed by `hashFiles('app/src/main/**', '**/*.gradle*', 'gradle/**')`. This key only changes when source (not tests or docs) changes.

On a test-only push:
1. Compute the source hash — it matches the previous run's cache key
2. Restore the Actions cache
3. Cache hit → skip `assembleDebug` + `lintDebug`, run only `testDebugUnitTest`
4. Cache miss (first run on branch, or eviction) → fall back to full build

The Gradle build cache is the fast path when Gradle home is warm. The explicit Actions cache is the reliable fallback that doesn't depend on Gradle cache state.

### 4. Build+Lint step and Test step

The build (`assembleDebug`) and lint (`lintDebug`) are merged into a single conditional Gradle invocation. The test step (`testDebugUnitTest`) is kept as a separate, unconditional step so tests always run whenever the workflow fires — docs-only changes are already excluded at the trigger level.

Merging tests into the conditional build step was considered (shared daemon startup, ~10–15s saved on full runs) but rejected: it would skip tests entirely on cache-hit runs, which is the wrong behaviour for a test-only push.

## Step Flow

```
checkout
detect changed paths (dorny/paths-filter)
setup Java + Gradle
  │
  ├─ source changed?
  │   YES → Build+Lint, save build cache
  │           ↓
  │         Test (always)
  │
  └─ source unchanged?
      restore build cache (keyed by source hash)
        │
        ├─ cache hit → skip Build+Lint
        │               ↓
        │             Test (always)
        │
        └─ cache miss → Build+Lint, save build cache
                         ↓
                       Test (always)
```

## What Changes

| File | Change |
|---|---|
| `.github/workflows/ci.yml` | Add `paths-ignore`, change detection, conditional steps, cache save/restore |
| `gradle.properties` | Add `org.gradle.caching=true` |

## Out of Scope

- Parallel lint + test jobs (adds workflow complexity; gains don't justify it at current scale)
- Instrumented tests (`connectedAndroidTest`) — not in CI yet

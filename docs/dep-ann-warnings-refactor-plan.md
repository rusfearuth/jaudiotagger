# Dep-Ann Warnings Refactor Plan (`./gradlew clean assemble`)

## Summary
- Goal: remove compiler warnings `[dep-ann] deprecated item is not annotated with @Deprecated` found during `compileDebugJavaWithJavac` and `compileReleaseJavaWithJavac`.
- Scope: 11 unique warning points across 10 files.
- Non-goal: broad API redesign; only precise deprecation contract alignment.

## Current Findings
All warnings are of one type:
- `[dep-ann] deprecated item is not annotated with @Deprecated`

Affected locations:
1. `libs/src/main/java/org/jaudiotagger/audio/flac/metadatablock/MetadataBlockDataPicture.java:356`
2. `libs/src/main/java/org/jaudiotagger/tag/id3/ID3v24Tag.java:648`
3. `libs/src/main/java/org/jaudiotagger/tag/id3/ID3v1Tag.java:214`
4. `libs/src/main/java/org/jaudiotagger/tag/id3/ID3v24Frame.java:362`
5. `libs/src/main/java/org/jaudiotagger/tag/id3/ID3v23Frame.java:312`
6. `libs/src/main/java/org/jaudiotagger/tag/id3/ID3v22Frame.java:295`
7. `libs/src/main/java/org/jaudiotagger/tag/id3/ID3v11Tag.java:265`
8. `libs/src/main/java/org/jaudiotagger/tag/id3/ID3v23Tag.java:372`
9. `libs/src/main/java/org/jaudiotagger/tag/id3/ID3v22Tag.java:214`
10. `libs/src/main/java/org/jaudiotagger/tag/id3/framebody/FrameBodyUnsupported.java:52`
11. `libs/src/main/java/org/jaudiotagger/tag/id3/framebody/FrameBodyUnsupported.java:84`

## Public API / Contract Changes
- Public signatures are not changed.
- Semantic API metadata changes:
  - Add Java annotation `@Deprecated` to APIs already documented as deprecated in Javadoc.
  - If any flagged member is intentionally not deprecated, remove/adjust stale `@deprecated` Javadoc tag instead of adding annotation.
- Expected binary compatibility impact: none.
- Expected source compatibility impact: none (only stronger deprecation signaling).

## Implementation Plan
1. Validate each warning site
- Inspect each flagged declaration and its Javadoc block.
- Confirm intent: deprecated vs active.

2. Apply contract alignment rule
- Rule A (default): when Javadoc contains `@deprecated`, add `@Deprecated` annotation to the same declaration.
- Rule B (exception): when Javadoc deprecation is stale/incorrect, remove `@deprecated` tag and keep API active.
- Record every Rule B case in changelog note in PR description.

3. File-by-file patching
- `MetadataBlockDataPicture.java`: annotate deprecated mutator `isBinary(boolean b)` if intended deprecated.
- ID3 tag/frame constructors (`ID3v22/23/24Tag`, `ID3v11/v1Tag`, `ID3v22/23/24Frame`): annotate constructors that are legacy parser entry points.
- `FrameBodyUnsupported.java`: annotate both flagged constructors or clean stale Javadoc if not deprecated.

4. Consistency pass
- Ensure `@Deprecated` placement is immediately above declaration.
- Keep formatting/style consistent with existing codebase.

5. Verification
- Run `./gradlew clean assemble`.
- Confirm no `[dep-ann]` warnings remain in both debug/release compilation sections.
- Optionally run `./gradlew :libs:lintDebug` to ensure no secondary regressions.

## Test Scenarios
1. Build warning regression
- Command: `./gradlew clean assemble`
- Expected: zero `[dep-ann]` warnings.

2. Legacy entrypoint compile safety
- Ensure all previously flagged constructors/methods still compile and are callable.
- Expected: no signature changes.

3. Downstream deprecation visibility
- IDE/compiler now surfaces standard deprecation annotation for those APIs.
- Expected: alignment between Javadoc and annotation.

## Risks and Mitigations
- Risk: accidentally marking active API deprecated.
  - Mitigation: per-site Javadoc intent review before annotation.
- Risk: inconsistent handling across similar classes.
  - Mitigation: apply uniform rule across ID3 generation classes.

## Acceptance Criteria
- `./gradlew clean assemble` succeeds without `[dep-ann]` warnings.
- All 11 current warning sites resolved.
- No functional behavior changes in tag parsing/writing flows.

## Assumptions
- Current warning list is complete for `clean assemble` on this branch.
- Existing Javadocs correctly reflect intended deprecation in most cases.

# Lint Warnings Fix Plan (libs)

## Summary
- Goal: eliminate current lint issues in `:libs:lintDebug` and restore green `./gradlew build --warning-mode all`.
- Current state: lint reports 2 errors and 42 warnings.
- Scope: fix all warnings in one pass, including Gradle dependency warnings.

## Public API / Interfaces
- No public API changes are planned.
- Changes are limited to internal code hygiene, locale safety, indentation correctness, and test dependency updates.

## Work Breakdown
1. Unblock lint errors first:
   - Fix `SuspiciousIndentation` errors in `libs/src/main/java/org/jaudiotagger/utils/tree/TreePath.java`.
2. Fix `DefaultLocale` warnings:
   - Add explicit locale (`Locale.ROOT` for internal string normalization; locale explicit in `String.format`).
3. Fix remaining `SuspiciousIndentation` warnings:
   - Normalize indentation and add braces where control flow could be misread.
4. Fix `WrongCommentType` warnings:
   - Convert block comments that document API contracts into proper Javadoc.
5. Fix `SimpleDateFormat` warnings:
   - Add explicit locale to date format construction.
6. Fix single-instance warnings:
   - `UseValueOf` in MP4 field class.
   - `AssertionSideEffect` in ASF chunk writer.
7. Fix `GradleDependency` warnings:
   - Update `androidx.test.ext:junit` and `androidx.test:runner` in `libs/build.gradle.kts`.
8. Validate:
   - `./gradlew :libs:lintDebug --warning-mode all`
   - `./gradlew :libs:testDebugUnitTest :libs:testReleaseUnitTest`
   - `./gradlew build --warning-mode all`

## Acceptance Criteria
- `:libs:lintDebug` reports 0 errors, 0 warnings.
- Unit tests in `libs` pass for debug and release.
- Full project build succeeds.

## Assumptions
- Existing behavior should not change; fixes are mostly structural and locale-explicit.
- Locale-sensitive changes use `Locale.ROOT` unless human-facing formatting explicitly requires another locale.

# TODO: Lint Warnings Fix (`libs`)

- [x] Fix `TreePath.java` lint errors (`SuspiciousIndentation`) at lines flagged by lint.
- [x] Fix all `DefaultLocale` warnings in Java sources.
- [x] Fix all remaining `SuspiciousIndentation` warnings in Java sources.
- [x] Fix `WrongCommentType` warnings in reader/header classes.
- [x] Fix `SimpleDateFormat` warnings by adding explicit locale.
- [x] Fix `AssertionSideEffect` warning in ASF writable chunk modifier.
- [x] Fix `UseValueOf` warning in MP4 byte field parser.
- [x] Update `androidx.test.ext:junit` and `androidx.test:runner` versions in `libs/build.gradle.kts`.
- [x] Run `./gradlew :libs:lintDebug --warning-mode all` and confirm 0 warnings.
- [x] Run `./gradlew :libs:testDebugUnitTest :libs:testReleaseUnitTest`.
- [x] Run `./gradlew build --warning-mode all`.
- [x] Summarize changes and close TODOs.

## Result
- `:libs:lintDebug`: **No issues found**.
- `:libs:testDebugUnitTest` and `:libs:testReleaseUnitTest`: passed.
- Full `build --warning-mode all`: passed.

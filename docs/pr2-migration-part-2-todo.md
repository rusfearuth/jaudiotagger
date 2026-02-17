# TODO: PR2 Migration Part 2 (`AudioFileIO` Path-first API)

## Implementation
- [x] Confirm base scope and target (`PR #10`, migration part 2).
- [x] Remove/verify removal of legacy `AudioFileIO` wrappers (`File/String` overloads).
- [x] Keep and verify `Path` and Android `Uri` public entry points.
- [x] Ensure `AudioFile.commit()` calls default `AudioFileIO` instance write path.
- [x] Ensure `AudioFile.delete()` calls default `AudioFileIO` instance delete path.

## Tests and parity
- [x] Keep `AudioFileIOPathApiTest` aligned with public `Path` API.
- [x] Keep `AudioFileIOPathParityRegressionTest` as regression safety net.
- [x] Keep Android PFD coverage in `AudioFileIOParcelFileDescriptorTest`.

## Documentation
- [x] Record PR2 execution plan in `docs/pr2-migration-part-2-plan.md`.
- [x] Keep migration docs in sync (`README.md`, `docs/path-migration-plan.md`).

## Validation
- [x] `./gradlew :libs:testDebugUnitTest --tests org.jaudiotagger.audio.AudioFileIOPathApiTest --tests org.jaudiotagger.audio.AudioFileIOPathParityRegressionTest`
- [x] `./gradlew :libs:compileDebugAndroidTestJavaWithJavac`

## Notes
- Plan executed as historical parity with PR2 (`#10`), without additional feature scope.

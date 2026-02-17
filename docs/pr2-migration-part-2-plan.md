# PR2 Plan: Migration Part 2 (`AudioFileIO` Path-first API)

## Summary
Implement PR2 as a historically identical step to `#10` (no functional expansions): remove legacy `File/String` wrappers in `AudioFileIO`, keep `Path` and Android `Uri` entry points as canonical public API, align tests and docs, and pass targeted validation.

## Scope and sequence
1. Branch from base after PR1.
2. Apply API cleanup in `AudioFileIO` and compatibility wiring in `AudioFile`.
3. Update tests to `Path`-first usage and parity coverage.
4. Update migration documentation.
5. Run targeted checks and verify acceptance criteria.

## Public APIs
Kept in `AudioFileIO`:
- `read(Path)`
- `readAs(Path, String)`
- `readMagic(Path)`
- `writeAs(AudioFile, Path)`
- `read(Context, Uri, String)`
- `readAs(Context, Uri, String)`
- `write(Context, AudioFile, Uri)`
- `delete(Context, AudioFile, Uri)`

Removed legacy wrappers:
- `delete(AudioFile)`
- `read(File)`
- `readAs(File, String)`
- `readMagic(File)`
- `write(AudioFile)`
- `writeAs(AudioFile, String)`

`AudioFile` contract updates:
- `commit()` uses `AudioFileIO.getDefaultAudioFileIO().writeFile(this, (Path) null)`.
- `delete()` uses `AudioFileIO.getDefaultAudioFileIO().deleteTag(this)`.

## Files to touch
- `libs/src/main/java/org/jaudiotagger/audio/AudioFileIO.java`
- `libs/src/main/java/org/jaudiotagger/audio/AudioFile.java`
- `libs/src/test/java/org/jaudiotagger/audio/AudioFileIOPathApiTest.java`
- `libs/src/test/java/org/jaudiotagger/audio/AudioFileIOPathParityRegressionTest.java`
- `libs/src/androidTest/java/org/jaudiotagger/audio/AudioFileIOParcelFileDescriptorTest.java`
- `README.md`
- `docs/path-migration-plan.md`

## Acceptance criteria
- No public `AudioFileIO` wrappers for legacy `File/String` API.
- `AudioFile.commit()` and `AudioFile.delete()` work without removed static wrappers.
- Targeted tests pass.
- Android test compilation passes.
- Documentation reflects final API and migration mappings.

## Validation commands
```bash
./gradlew :libs:testDebugUnitTest --tests org.jaudiotagger.audio.AudioFileIOPathApiTest --tests org.jaudiotagger.audio.AudioFileIOPathParityRegressionTest
./gradlew :libs:compileDebugAndroidTestJavaWithJavac
```

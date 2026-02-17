# TODO: Wave 2 Refactor (Audio Core)

## Phase 1: AudioFile Path model
- [x] Add `Path` canonical state in `AudioFile`.
- [x] Add `getPath()/setPath(Path)`.
- [x] Keep `getFile()/setFile(File)` as compatibility adapters.
- [x] Mark legacy File accessors as deprecated.
- [x] Add/extend tests for `setPath/setFile` synchronization.

## Phase 2: Reader Path-first
- [x] Invert `AudioFileReader`: `read(File)` delegates to `read(Path)`.
- [x] Make `AudioFileReader2` explicitly override `read(Path)`.
- [x] Update `OggFileReader` to create `AudioFile` from Path.
- [x] Update `RealFileReader` to create `AudioFile` from Path.
- [x] Update `AsfFileReader` to create `AudioFile` from Path.
- [x] Update `MP3FileReader` File entrypoint to delegate to Path.
- [x] Run targeted reader regression tests.

## Phase 3: Listener Path callbacks
- [x] Add Path callback defaults to `AudioFileModificationListener`.
- [x] Route `ModificationHandler` through Path callbacks.
- [x] Route `AudioFileWriter` notifications through Path callbacks.
- [x] Add regression test for legacy listener implementation compatibility.

## Phase 4: AudioFileIO integration
- [x] Switch `AudioFileIO.deleteTag(AudioFile)` extension resolution to Path.
- [x] Switch Uri write flow to `setPath/getPath` restore cycle.
- [x] Switch Uri delete flow to `setPath/getPath` restore cycle.
- [x] Switch `writeFile(AudioFile, Path)` null/ext/copy logic to Path-first.
- [x] Validate `writeAs` and `commit/delete` parity on existing tests.

## Final validation
- [x] `./gradlew :libs:testDebugUnitTest`
- [x] `./gradlew :libs:compileDebugAndroidTestJavaWithJavac`
- [x] Review deprecation warnings and decide next cleanup batch.

## Deprecation cleanup decision (2026-02-17)
- [x] Remove Gradle 9 deprecation warning about automatic test framework dependency loading by:
  - updating `testImplementation` from `junit:junit:3.8.1` to `junit:junit:4.13.2`;
  - configuring `tasks.withType<Test>().configureEach { useJUnit() }`.
- [x] Next cleanup batch candidate: review Java compiler warnings for `source/target 8` obsolescence and plan Java toolchain migration separately.
  - Result: migrated to Java 11 baseline and pinned JDK 11 toolchain (see `docs/java11-toolchain-plan.md` and `docs/java11-toolchain-todo.md`).

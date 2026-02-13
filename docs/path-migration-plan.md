# Path Migration Plan (`java.nio.file`) for jaudiotagger

## Goal
Migrate file operations toward `java.nio.file.Path` with full backward compatibility through `File` wrappers for 1-2 releases.

## Scope of current iteration
- Add `Path` overload API alongside current `File`/`String` API.
- Keep old methods operational and mark as `@Deprecated`.
- Migrate exactly one legacy format in this iteration: `MP3`.
- Run full regression before and after changes: `./gradlew :libs:test`.

## Locked decisions
- Primary API is `Path`.
- Legacy `File`/`String` methods stay as thin wrappers.
- Deprecated API is not removed in this iteration.
- Legacy format migration proceeds one format per iteration.

## Backlog checklist

### Epic A - Regression safety
- [x] Add baseline tests for current `File` behavior:
  - [x] `read(File)`
  - [x] `readAs(File, ext)`
  - [x] `readMagic(File)`
  - [x] `writeAs(AudioFile, String)`
- [x] Run baseline `./gradlew :libs:test`.
- [ ] Record and track intermittent failures if they appear in CI.

### Epic B - `Path` API in `AudioFileIO`
- [x] Add static overloads:
  - [x] `read(Path)`
  - [x] `readAs(Path, String)`
  - [x] `readMagic(Path)`
  - [x] `writeAs(AudioFile, Path)`
- [x] Add instance overloads:
  - [x] `readFile(Path)`
  - [x] `readFileAs(Path, String)`
  - [x] `readFileMagic(Path)`
  - [x] `writeFile(AudioFile, Path)`
- [x] Adapt `File`/`String` methods to delegate to `Path` methods.
- [x] Mark legacy wrappers as `@Deprecated`.
- [ ] Expand Javadoc replacement notes for each deprecated entry point.

### Epic C - Internal adapters
- [x] Add `Path` variants in `audio.generic.Utils`:
  - [x] `getExtension(Path)`
  - [x] `getMagicExtension(Path)`
- [x] Switch `AudioFileIO` extension resolution paths to `Path` variants.

### Epic D - Legacy format migration (iteration 1: MP3)
- [x] Add MP3-specific `Path` entry in reader flow.
- [x] Keep `commit()/save()` semantics unchanged.
- [x] Keep `Ogg/Asf/Real` for later iterations.

### Epic E - Tests for new API
- [x] Add `AudioFileIOPathApiTest`:
  - [x] `read(Path)`
  - [x] `readAs(Path, ext)`
  - [x] `readMagic(Path)`
  - [x] `writeAs(AudioFile, Path)`
- [x] Add basic parity check for `File` vs `Path`.

### Epic F - Post-change validation
- [x] Run post-change full regression: `./gradlew :libs:test`.
- [x] Compare against baseline and fix regressions if any.
- [x] Capture iteration status in this document.

## Definition of done for this iteration
- Baseline `:libs:test` passes.
- Post-change `:libs:test` passes.
- `Path` overload API is present and covered by tests.
- Deprecated `File` wrappers still behave correctly.
- MP3 path entry has no behavior regressions.

## Planned next iterations
1. Ogg
2. Asf/WMA
3. Real
4. Deprecation removal decision after release window

## Regression command
```bash
./gradlew :libs:test
```

## Risks
- Extension detection mismatches between `File` and `Path`.
- Unexpected write-path regressions around `writeAs`.
- Legacy format edge cases hidden behind old reader/writer internals.

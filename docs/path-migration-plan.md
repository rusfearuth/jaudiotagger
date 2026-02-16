# Path Migration Plan (`java.nio.file`) for jaudiotagger

## Goal
Migrate file operations toward `java.nio.file.Path` with full backward compatibility through `File` wrappers for 1-2 releases.

## Scope of completed iteration (iteration 4: Real)
- Add `Path` overload API alongside current `File`/`String` API.
- Keep old methods operational and mark as `@Deprecated`.
- Migrate exactly one legacy format in this iteration: `Real` (reader-first).
- Run full regression before and after changes: `./gradlew :libs:test`.

## Scope of current iteration (iteration 5: deprecation removal decision)
- Freeze deprecation policy for `File`/`String` APIs.
- Keep deprecated wrappers for 2 releases (compatibility window).
- Do not remove deprecated APIs in this iteration.
- Document removal gates and earliest removal release.
- Keep `AudioFile.commit()/delete()` as compatibility bridge in this iteration.

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
- [x] Expand Javadoc replacement notes for each deprecated entry point.

### Epic C - Internal adapters
- [x] Add `Path` variants in `audio.generic.Utils`:
  - [x] `getExtension(Path)`
  - [x] `getMagicExtension(Path)`
- [x] Switch `AudioFileIO` extension resolution paths to `Path` variants.

### Epic D - Legacy format migration (iteration 1: MP3)
- [x] Add MP3-specific `Path` entry in reader flow.
- [x] Keep `commit()/save()` semantics unchanged.
- [x] Keep `Ogg/Asf/Real` for later iterations.

### Epic G - Legacy format migration (iteration 2: Ogg)
- [x] Add Ogg-specific `Path` entry in reader flow (`OggFileReader`/`OggVorbisTagReader`).
- [x] Keep legacy `File` read behavior unchanged and backward compatible.
- [x] Keep Ogg writer internals unchanged in this iteration (reader-first scope).

### Epic H - Legacy format migration (iteration 3: Asf/WMA)
- [x] Add Asf/WMA-specific `Path` entry in reader flow (`AsfFileReader`).
- [x] Keep legacy `File` read behavior unchanged and backward compatible.
- [x] Keep Asf/WMA writer internals unchanged in this iteration (reader-first scope).

### Epic I - Legacy format migration (iteration 4: Real)
- [x] Add Real-specific `Path` entry in reader flow (`RealFileReader`).
- [x] Keep legacy `File` read behavior unchanged and backward compatible.
- [x] Keep Real writer internals unchanged in this iteration (reader-first scope).

### Epic E - Tests for new API
- [x] Add `AudioFileIOPathApiTest`:
  - [x] `read(Path)`
  - [x] `readAs(Path, ext)`
  - [x] `readMagic(Path)`
  - [x] `writeAs(AudioFile, Path)`
- [x] Add basic parity check for `File` vs `Path`.
- [x] Add Ogg `Path` API coverage (`read/readAs/readMagic parity/writeAs`).
- [x] Add Ogg `File` API regression coverage (`read/readAs/readMagic parity/writeAs`).
- [x] Add corrupt Ogg parity check for `read(Path)` exception behavior.
- [x] Add Asf/WMA `Path` API coverage (`read/readAs/readMagic parity/writeAs`).
- [x] Add Asf/WMA `File` API regression coverage (`read/readAs/readMagic parity/writeAs`).
- [x] Add Real `Path` API coverage (`read/readAs/readMagic parity` for `ra`/`rm`).
- [x] Add Real `File` API regression coverage (`read/readAs/readMagic parity` for `ra`/`rm`).

### Epic F - Post-change validation
- [x] Run post-change full regression: `./gradlew :libs:test`.
- [x] Compare against baseline and fix regressions if any.
- [x] Capture iteration status in this document.

## Definition of done for this iteration
- Baseline `:libs:test` passes.
- Post-change `:libs:test` passes.
- `Path` overload API is present and covered by tests.
- Deprecated `File` wrappers still behave correctly.
- Ogg path entry has no behavior regressions.
- Asf/WMA path entry has no behavior regressions.
- Real path entry has no behavior regressions.

## Planned next iterations
1. Implement `File`/`String` API removal no earlier than release `R+2` if all gates pass.

## Iteration 5 - Deprecation Removal Decision

### Locked decisions (iteration 5)
- Compatibility window is fixed at 2 releases.
- Earliest removal is release `R+2`.
- `AudioFile.commit()/delete()` remain supported bridge methods in this iteration.
- `Path` and Android `ParcelFileDescriptor` entry points remain supported.
- Removal implementation happens in a separate iteration after gates pass.

### Deprecated API inventory and replacements
| API entry point | Replacement | Earliest removal |
|---|---|---|
| `AudioFileIO.read(File)` | `AudioFileIO.read(Path)` | `R+2` |
| `AudioFileIO.readAs(File, String)` | `AudioFileIO.readAs(Path, String)` | `R+2` |
| `AudioFileIO.readMagic(File)` | `AudioFileIO.readMagic(Path)` | `R+2` |
| `AudioFileIO.writeAs(AudioFile, String)` | `AudioFileIO.writeAs(AudioFile, Path)` | `R+2` |
| `AudioFileIO.readFile(File)` | `AudioFileIO.readFile(Path)` | `R+2` |
| `AudioFileIO.readFileAs(File, String)` | `AudioFileIO.readFileAs(Path, String)` | `R+2` |
| `AudioFileIO.readFileMagic(File)` | `AudioFileIO.readFileMagic(Path)` | `R+2` |
| `AudioFileIO.writeFile(AudioFile, String)` | `AudioFileIO.writeFile(AudioFile, Path)` | `R+2` |

### Release timeline and decision points
- `R+0` (current): deprecation policy is documented and migration targets are fixed.
- `R+1`: migration progress review for tests/docs and compatibility signal check.
- `R+2`: removal PR is allowed only if all removal gates are green.

### Removal gates (must pass before removal PR)
- [ ] `./gradlew :libs:test` is green in CI without new migration regressions.
- [ ] Public examples and Javadocs use `Path` replacements for file-based flows.
- [ ] Dedicated regression coverage for deprecated wrappers is kept until actual removal.
- [ ] No unresolved replacement mapping remains for deprecated APIs.
- [ ] `AudioFile.commit()/delete()` strategy is reviewed separately before any bridge changes.

### Migration guide (public API mapping)
- `AudioFileIO.read(File)` -> `AudioFileIO.read(Path)`
- `AudioFileIO.readAs(File, ext)` -> `AudioFileIO.readAs(Path, ext)`
- `AudioFileIO.readMagic(File)` -> `AudioFileIO.readMagic(Path)`
- `AudioFileIO.writeAs(AudioFile, String)` -> `AudioFileIO.writeAs(AudioFile, Path)`

## Regression command
```bash
./gradlew :libs:test
```

## Risks
- Extension detection mismatches between `File` and `Path`.
- Unexpected write-path regressions around `writeAs`.
- Legacy format edge cases hidden behind old reader/writer internals.
- Ogg parse diagnostics may diverge if `Path` context is not propagated through tag readers.
- Asf parse diagnostics may diverge if `Path` context is not propagated through header parsing.
- Real parse diagnostics may diverge if `Path` context is not propagated through reader error handling.

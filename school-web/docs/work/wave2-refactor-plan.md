# Wave 2 Refactor Plan: Internal File -> Path (Audio Core)

## Summary
Second wave refactors internal audio core to be Path-first while keeping backward compatibility.
Scope is limited to `org.jaudiotagger.audio` and `org.jaudiotagger.audio.generic` integration points.

## Constraints
- No breaking changes for external users.
- Existing `File` APIs remain available as compatibility adapters.
- Delivery in small incremental PR-sized steps.

## Public/Internal Interface Changes
1. `AudioFile`
- Add canonical `Path` accessors: `getPath()/setPath(Path)`.
- Keep `getFile()/setFile(File)` as compatibility methods and mark deprecated.

2. Reader layer
- `AudioFileReader` becomes Path-primary.
- `read(File)` delegates to `read(Path)`.

3. Modification listeners
- Add Path-first callbacks:
  - `fileModifiedPath(AudioFile, Path)`
  - `fileOperationFinishedPath(Path)`
- Keep legacy callbacks and delegate by default for compatibility.

4. Core write/read flows
- `AudioFileIO` resolves current file location via `AudioFile.getPath()`.
- Keep legacy File-based internals only where Java API requires `File`.

## Execution Phases
### Phase 1: Model + adapters
- Introduce canonical path in `AudioFile`.
- Ensure both `setFile` and `setPath` keep model synchronized.

### Phase 2: Reader inversion
- Make `AudioFileReader` Path-first default behavior.
- Update reader implementations to return `AudioFile` created from Path.

### Phase 3: Listener migration
- Add Path callbacks to listener contract.
- Route notifications through Path callbacks in `ModificationHandler` and writers.

### Phase 4: IO integration
- Move `AudioFileIO` operations to `getPath()/setPath()` usage for write/delete/uri flows.
- Preserve ext inference logic and behavior parity.

## Validation
- Targeted tests during each phase.
- Final checks:
  - `./gradlew :libs:testDebugUnitTest`
  - `./gradlew :libs:compileDebugAndroidTestJavaWithJavac`

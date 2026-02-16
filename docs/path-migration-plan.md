# Path Migration Status (`java.nio.file`) for jaudiotagger

## Summary
`AudioFileIO` now uses `Path` as the primary file API and no longer exposes legacy `File`/`String` wrapper entry points.

Android `ParcelFileDescriptor` entry points are implemented as real flows and no longer fail with "not wired" stubs.

## Final public API

### File system entry points (`Path`)
- `AudioFileIO.read(Path)`
- `AudioFileIO.readAs(Path, String)`
- `AudioFileIO.readMagic(Path)`
- `AudioFileIO.writeAs(AudioFile, Path)`

### Android entry points (`ParcelFileDescriptor`)
- `AudioFileIO.read(ParcelFileDescriptor, String)`
- `AudioFileIO.readAs(ParcelFileDescriptor, String)`
- `AudioFileIO.write(AudioFile, ParcelFileDescriptor)`
- `AudioFileIO.delete(AudioFile, ParcelFileDescriptor)`

## Removed legacy API

The following legacy entry points were removed from `AudioFileIO`:
- `read(File)`
- `readAs(File, String)`
- `readMagic(File)`
- `writeAs(AudioFile, String)`
- `write(AudioFile)`
- `delete(AudioFile)`
- `readFile(File)`
- `readFileAs(File, String)`
- `readFileMagic(File)`
- `writeFile(AudioFile, String)`

## Migration guide
- `AudioFileIO.read(File)` -> `AudioFileIO.read(Path)`
- `AudioFileIO.readAs(File, ext)` -> `AudioFileIO.readAs(Path, ext)`
- `AudioFileIO.readMagic(File)` -> `AudioFileIO.readMagic(Path)`
- `AudioFileIO.writeAs(AudioFile, String)` -> `AudioFileIO.writeAs(AudioFile, Path)`
- `AudioFileIO.write(AudioFile)` -> `audioFile.commit()`
- `AudioFileIO.delete(AudioFile)` -> `audioFile.delete()`

## Validation commands
```bash
./gradlew :libs:testDebugUnitTest
./gradlew :libs:compileDebugAndroidTestJavaWithJavac
```

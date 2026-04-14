[![](https://jitpack.io/v/rusfearuth/jaudiotagger.svg)](https://jitpack.io/#rusfearuth/jaudiotagger)

# Jaudiotagger

*Jaudiotagger* is a Java API for audio metatagging. Both a common API and format
specific APIs are available, currently supports reading and writing metadata for:

- Mp3
- Flac
- OggVorbis
- Mp4
- Aiff
- Wav
- Wma
- Dsf

The main project page is http://www.jthink.net/jaudiotagger/ and you can contact the main developer via email:paultaylor@jthink.net

## Requirements

*Jaudiotagger* requires Java 11+ (builds are standardized on JDK 17 toolchain)

## Contributing

*Jaudiotagger* welcomes contributors, if you make an improvement or bug fix we are
very likely to merge it back into the master branch with a minimum of fuss.

If you can't contribute code but would like to support this project please consider
making a donation—donations can be made at
[here](http://www.jthink.net/jaudiotagger/donate.jsp).

## Include in your Project

Gradle Kotlin DSL:

```kotlin
repositories {
    maven { url = uri("https://jitpack.io") }
}

dependencies {
    implementation("com.github.rusfearuth:jaudiotagger:3.0.6")
}
```

## Build

Directory structure as follows:

### Under source control

- `libs/src/main/java`   : library source code
- `libs/src/test/java`   : unit tests
- `libs/src/androidTest` : instrumentation tests
- `libs/src/main/AndroidManifest.xml` : Android manifest for the library module
- `www`                  : java doc directory
- `testdata`             : test files for use by the junit tests, not all tests are included in the distribution because of copyright
- `build` / `libs/build` : Gradle build outputs

### License

- `license.txt` : license file
 
### Local Build details

Primary build flow is Gradle (Android AAR).

- `settings.gradle.kts` : Gradle settings (includes `:libs`)
- `build.gradle.kts` : root Gradle configuration
- `libs/build.gradle.kts` : Android library module build
- `gradle/wrapper/*` + `gradlew` : wrapper scripts and pinned Gradle version

To compile and run unit tests:

    ./gradlew :libs:test

To run Android instrumentation tests (Android 9+ device/emulator connected):

    ./gradlew :libs:connectedAndroidTest

## API Usage (Path + Uri)

The file-based API is now centered on `java.nio.file.Path`.

Read:

    AudioFile audioFile = AudioFileIO.read(Paths.get("song.mp3"));
    AudioFile byExt = AudioFileIO.readAs(Paths.get("song.bin"), "mp3");
    AudioFile byMagic = AudioFileIO.readMagic(Paths.get("song.dat"));

Write and delete:

    audioFile.commit();
    audioFile.delete();
    AudioFileIO.writeAs(audioFile, Paths.get("export/song_copy"));

### Android Uri

The Uri API provides a single entry point for both `content://` and `file://` schemes — the calling code doesn't need to distinguish between them:

    AudioFile audioFile = AudioFileIO.readAs(context, uri, "mp3");
    AudioFileIO.write(context, audioFile, uri);
    AudioFileIO.delete(context, audioFile, uri);

Under the hood the library picks the optimal strategy automatically:

- **`content://`** — data is copied into a temp file via `ContentResolver`, edited there, and streamed back.
- **`file://`** — the file is accessed directly through the filesystem, no temp copies involved.

If an Android process is interrupted mid-operation, leaked temp files can be cleaned up later from
the app cache directory:

```java
Executor executor = Executors.newSingleThreadExecutor();
AudioFileIO.cleanupLeakedUriTempFilesAsync(context, executor);
```

The cleanup API only targets `jaudiotagger_uri_*.tmp` files and, by default, removes files older than
five minutes so active operations are left alone.

If you have already finished working with an `AudioFile` loaded from `content://` and no longer need
direct file-based operations such as `audioFile.commit()` or `audioFile.delete()`, you can release its
managed temp backing file explicitly:

```java
audioFile.release();
```

After `release()`, continue persisting changes through the Uri-based entry points:

```java
AudioFileIO.write(context, audioFile, uri);
AudioFileIO.delete(context, audioFile, uri);
```

Available cleanup methods:

- `AudioFileIO.cleanupLeakedUriTempFiles(context)` - synchronous cleanup with the default 5-minute grace period
- `AudioFileIO.cleanupLeakedUriTempFiles(context, minAgeMillis)` - synchronous cleanup with a custom grace period
- `AudioFileIO.cleanupLeakedUriTempFilesAsync(context, executor)` - asynchronous cleanup with the default 5-minute grace period
- `AudioFileIO.cleanupLeakedUriTempFilesAsync(context, minAgeMillis, executor)` - asynchronous cleanup with a custom grace period

Example with a custom grace period:

```java
long minAgeMillis = TimeUnit.MINUTES.toMillis(5);
int deleted = AudioFileIO.cleanupLeakedUriTempFiles(context, minAgeMillis);
```

#### Full Uri flow (read → edit → write back)

```java
// Uri can come from anywhere: SAF picker, MediaStore, Uri.fromFile(), etc.
// The API is the same regardless of the scheme.
Uri uri = ...;  // content://media/external/audio/media/42
                // or file:///sdcard/Music/song.mp3

// 1. Read
AudioFile audioFile = AudioFileIO.readAs(context, uri, "mp3");

// 2. Edit tags
Tag tag = audioFile.getTagOrCreateAndSetDefault();
tag.setField(FieldKey.ARTIST, "Pink Floyd");
tag.setField(FieldKey.ALBUM, "The Dark Side of the Moon");
tag.setField(FieldKey.TITLE, "Time");
tag.setField(FieldKey.TRACK, "4");

// 3. Write back
AudioFileIO.write(context, audioFile, uri);
```

### Migration from removed legacy API

- `AudioFileIO.read(File)` -> `AudioFileIO.read(Path)`
- `AudioFileIO.readAs(File, String)` -> `AudioFileIO.readAs(Path, String)`
- `AudioFileIO.readMagic(File)` -> `AudioFileIO.readMagic(Path)`
- `AudioFileIO.writeAs(AudioFile, String)` -> `AudioFileIO.writeAs(AudioFile, Path)`
- `AudioFileIO.write(AudioFile)` -> `audioFile.commit()`
- `AudioFileIO.delete(AudioFile)` -> `audioFile.delete()`

## Utility scripts

- Windows helper scripts are located under `scripts/`
- `gradlew` and `gradlew.bat` stay at repo root for Gradle wrapper usage

Examples (Windows, run from repo root):

    scripts\ReadTest.bat <audio-file>
    scripts\ExtractTag.bat <input> <output>
    scripts\MergeID3AndMP3.bat <id3> <mp3> <output>
    scripts\Fix202.bat <audio-file>
    scripts\CreateTestData.bat

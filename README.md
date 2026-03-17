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

Maven:

```xml
<repositories>
    <repository>
        <id>jitpack.io</id>
        <url>https://jitpack.io</url>
    </repository>
</repositories>

<dependencies>
    <dependency>
        <groupId>com.github.rusfearuth</groupId>
        <artifactId>jaudiotagger</artifactId>
        <version>3.0.2</version>
    </dependency>
</dependencies>
```

Gradle Kotlin DSL:

```kotlin
repositories {
    maven { url = uri("https://jitpack.io") }
}

dependencies {
    implementation("com.github.rusfearuth:jaudiotagger:3.0.2")
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

### IDE files

- IDE metadata files (`*.iml`, `*.ipr`, `*.iws`) are local and ignored by Git

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

## Publishing

Publishing is handled automatically by [JitPack](https://jitpack.io/#rusfearuth/jaudiotagger). To release a new version, create and push a git tag:

```bash
git tag <version>
git push origin <version>
```

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

Android `Uri` entry points are also supported:

    AudioFile fromUri = AudioFileIO.readAs(context, uri, "mp3");
    AudioFileIO.write(context, audioFile, uri);
    AudioFileIO.delete(context, audioFile, uri);

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

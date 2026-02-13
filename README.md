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

*Jaudiotagger* requires Java 1.8

## Contributing

*Jaudiotagger* welcomes contributors, if you make an improvement or bug fix we are
very likely to merge it back into the master branch with a minimum of fuss.

If you can't contribute code but would like to support this project please consider
making a donation—donations can be made at
[here](http://www.jthink.net/jaudiotagger/donate.jsp).

## Include in your Project

Latest release is 3.0.1 available from Maven central repository, so to use in your project just include
the following in your applications pom.xml file

    `<dependency>
        <groupId>net.jthink</groupId>
        <artifactId>jaudiotagger</artifactId>
        <version>3.0.1</version>
    </dependency>
    ` 

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

## Utility scripts

- Windows helper scripts are located under `scripts/`
- `gradlew` and `gradlew.bat` stay at repo root for Gradle wrapper usage

Examples (Windows, run from repo root):

    scripts\ReadTest.bat <audio-file>
    scripts\ExtractTag.bat <input> <output>
    scripts\MergeID3AndMP3.bat <id3> <mp3> <output>
    scripts\Fix202.bat <audio-file>
    scripts\CreateTestData.bat

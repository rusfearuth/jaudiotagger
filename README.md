# Jaudiotagger

Russian translation: [`docs/README.ru.md`](docs/README.ru.md)

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

- `src`                  : source code directory
- `srctest`              : source test code directory
- `www`                  : java doc directory
- `testdata`             : test files for use by the junit tests, not all tests are included in the distribution because of copyright
- `target`               : contains the `jaudiotagger***.jar` built from maven

### IDE files

- `jaudiotagger.iml`     : JetBrains Intellij Module
- `jaudiotagger.ipr`     : JetBrains Intellij Project

### License

- `license.txt` : license file
 
### Local Build details

Build is with [Maven](http://maven.apache.org).

   `pom.xml` : Maven build file

To compile, test, build javadocs and install the default JVM artifact into your local repository run

    mvn install

### Build all library variants

Use the commands below depending on which artifact variant you need:

- Default JVM variant (compile):  
  `mvn -DskipTests clean compile`
- Default JVM variant (package JAR):  
  `mvn -DskipTests clean package`
- Android-compatible variant (compile):  
  `mvn -Pandroid -DskipTests clean compile`
- Android-compatible variant (package JAR):  
  `mvn -Pandroid -DskipTests clean package`
- Build both variants sequentially:  
  `mvn -DskipTests clean package && mvn -Pandroid -DskipTests clean package`

### Android Build (API 28+)

To build an Android-compatible variant without `java.desktop` dependencies run:

    mvn -Pandroid -DskipTests clean compile

This profile compiles Android-safe overrides from `src-android` and excludes desktop-only classes.

For SAF/`Uri` integration use `org.jaudiotagger.android.AndroidAudioFileIO`:

- `read(context, uri)`
- `readAs(context, uri, ext)`
- `write(context, audioFile, uri)`
- `delete(context, uri)`

`AndroidAudioFileIO` intentionally uses reflection, so the main artifact does not require compile-time `android.*` classes.

### Release build

To build release artifacts (sources/javadocs/signing profile from `pom.xml`):

    mvn clean deploy -Prelease


To generate a website for *Jaudiotagger* including code coverage reports run

    mvn site

they will be found in `target/site/index.html`.

Your test coverage can be seen at `target/site/cobertura/index.html`.

### Notes Maven Central Release instructions
- modify pom to remove SNAPSHOT from version
- commit pom.xml
- Create version tag of the form vx.x.x
- git push origin vx.x.x 
- mvn clean deploy -Prelease
- Login to https://s01.oss.sonatype.org/
- Release the release
- Wait for it on Maven Central
- modify pom to increase version number and reinstate SNAPSHOT part

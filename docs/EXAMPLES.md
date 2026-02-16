# EXAMPLES

## 1) Read tags from MP3 (`my_music.mp3`)

```java
import org.jaudiotagger.audio.AudioFile;
import org.jaudiotagger.audio.AudioFileIO;
import org.jaudiotagger.tag.FieldKey;
import org.jaudiotagger.tag.Tag;

import java.nio.file.Paths;

public class ReadMp3TagExample {
    public static void main(String[] args) throws Exception {
        AudioFile audioFile = AudioFileIO.read(Paths.get("my_music.mp3"));
        Tag tag = audioFile.getTag();

        if (tag == null) {
            System.out.println("Tag not found");
            return;
        }

        System.out.println("Title : " + tag.getFirst(FieldKey.TITLE));
        System.out.println("Artist: " + tag.getFirst(FieldKey.ARTIST));
        System.out.println("Album : " + tag.getFirst(FieldKey.ALBUM));
        System.out.println("Year  : " + tag.getFirst(FieldKey.YEAR));
    }
}
```

## 2) Read tags from MP4 (`my_music.mp4`)

```java
import org.jaudiotagger.audio.AudioFile;
import org.jaudiotagger.audio.AudioFileIO;
import org.jaudiotagger.tag.FieldKey;
import org.jaudiotagger.tag.Tag;

import java.nio.file.Paths;

public class ReadMp4TagExample {
    public static void main(String[] args) throws Exception {
        AudioFile audioFile = AudioFileIO.read(Paths.get("my_music.mp4"));
        Tag tag = audioFile.getTag();

        if (tag == null) {
            System.out.println("Tag not found");
            return;
        }

        System.out.println("Title : " + tag.getFirst(FieldKey.TITLE));
        System.out.println("Artist: " + tag.getFirst(FieldKey.ARTIST));
        System.out.println("Album : " + tag.getFirst(FieldKey.ALBUM));
        System.out.println("Year  : " + tag.getFirst(FieldKey.YEAR));
    }
}
```

## 3) Write tags to MP3 (`my_music.mp3`)

```java
import org.jaudiotagger.audio.AudioFile;
import org.jaudiotagger.audio.AudioFileIO;
import org.jaudiotagger.tag.FieldKey;
import org.jaudiotagger.tag.Tag;

import java.nio.file.Paths;

public class WriteMp3TagExample {
    public static void main(String[] args) throws Exception {
        AudioFile audioFile = AudioFileIO.read(Paths.get("my_music.mp3"));
        Tag tag = audioFile.getTagOrCreateAndSetDefault();

        tag.setField(FieldKey.TITLE, "New Title");
        tag.setField(FieldKey.ARTIST, "New Artist");
        tag.setField(FieldKey.ALBUM, "New Album");
        tag.setField(FieldKey.YEAR, "2026");

        audioFile.commit();
        System.out.println("Tags written to my_music.mp3");
    }
}
```

## 4) Write tags to MP4 (`my_music.mp4`)

```java
import org.jaudiotagger.audio.AudioFile;
import org.jaudiotagger.audio.AudioFileIO;
import org.jaudiotagger.tag.FieldKey;
import org.jaudiotagger.tag.Tag;

import java.nio.file.Paths;

public class WriteMp4TagExample {
    public static void main(String[] args) throws Exception {
        AudioFile audioFile = AudioFileIO.read(Paths.get("my_music.mp4"));
        Tag tag = audioFile.getTagOrCreateAndSetDefault();

        tag.setField(FieldKey.TITLE, "New Title");
        tag.setField(FieldKey.ARTIST, "New Artist");
        tag.setField(FieldKey.ALBUM, "New Album");
        tag.setField(FieldKey.YEAR, "2026");

        audioFile.commit();
        System.out.println("Tags written to my_music.mp4");
    }
}
```

## 5) Write cover image to MP3 (`my_music.mp3`)

```java
import org.jaudiotagger.audio.AudioFile;
import org.jaudiotagger.audio.AudioFileIO;
import org.jaudiotagger.tag.Tag;
import org.jaudiotagger.tag.images.ArtworkFactory;

import java.io.File;
import java.nio.file.Paths;

public class WriteCoverToMp3 {
    public static void main(String[] args) throws Exception {
        AudioFile audioFile = AudioFileIO.read(Paths.get("my_music.mp3"));
        Tag tag = audioFile.getTagOrCreateAndSetDefault();

        tag.setField(ArtworkFactory.createArtworkFromFile(new File("cover.jpg")));
        audioFile.commit();

        System.out.println("Cover written to my_music.mp3");
    }
}
```

## 6) Write cover image to MP4 (`my_music.mp4`)

```java
import org.jaudiotagger.audio.AudioFile;
import org.jaudiotagger.audio.AudioFileIO;
import org.jaudiotagger.tag.Tag;
import org.jaudiotagger.tag.images.ArtworkFactory;

import java.io.File;
import java.nio.file.Paths;

public class WriteCoverToMp4 {
    public static void main(String[] args) throws Exception {
        AudioFile audioFile = AudioFileIO.read(Paths.get("my_music.mp4"));
        Tag tag = audioFile.getTagOrCreateAndSetDefault();

        tag.setField(ArtworkFactory.createArtworkFromFile(new File("cover.jpg")));
        audioFile.commit();

        System.out.println("Cover written to my_music.mp4");
    }
}
```

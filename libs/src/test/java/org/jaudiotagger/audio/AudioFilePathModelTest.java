package org.jaudiotagger.audio;

import junit.framework.TestCase;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;

public class AudioFilePathModelTest extends TestCase
{
    public void testSetPathUpdatesLegacyFileView()
    {
        AudioFile audioFile = new AudioFile();
        Path path = Paths.get("testdatatmp", "path-model.mp3");

        audioFile.setPath(path);

        assertEquals(path, audioFile.getPath());
        assertEquals(path.toFile().getPath(), audioFile.getFile().getPath());
    }

    public void testSetFileUpdatesCanonicalPath()
    {
        AudioFile audioFile = new AudioFile();
        File file = new File("testdatatmp", "legacy-model.mp3");

        audioFile.setFile(file);

        assertEquals(file.toPath(), audioFile.getPath());
        assertEquals(file.getPath(), audioFile.getFile().getPath());
    }
}

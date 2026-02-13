package org.jaudiotagger.audio;

import org.jaudiotagger.AbstractTestCase;
import org.jaudiotagger.tag.FieldKey;

import java.io.File;

public class AudioFileIOFileApiRegressionTest extends AbstractTestCase
{
    private static final String SOURCE_MP3 = "01.mp3";

    public void testReadUsingFileApi() throws Exception
    {
        File source = copyAudioToTmp(SOURCE_MP3, new File("file-api-read.mp3"));
        AudioFile audioFile = AudioFileIO.read(source);

        assertEquals("mp3", audioFile.getExt());
        assertEquals(source.getAbsolutePath(), audioFile.getFile().getAbsolutePath());
        assertNotNull(audioFile.getAudioHeader());
    }

    public void testReadAsUsingFileApi() throws Exception
    {
        File source = copyAudioToTmp(SOURCE_MP3, new File("file-api-read-as.mp3"));
        AudioFile audioFile = AudioFileIO.readAs(source, "mp3");

        assertEquals("mp3", audioFile.getExt());
        assertNotNull(audioFile.getAudioHeader());
    }

    public void testReadMagicUsingFileApi() throws Exception
    {
        File source = copyAudioToTmp("01.mp3", new File("file-api-read-magic.mp3"));
        AudioFile audioFile = AudioFileIO.readMagic(source);

        assertEquals("mp3", audioFile.getExt());
    }

    public void testWriteAsUsingStringApi() throws Exception
    {
        File source = copyAudioToTmp(SOURCE_MP3, new File("file-api-write-as-source.mp3"));
        AudioFile audioFile = AudioFileIO.read(source);
        audioFile.getTagOrCreateAndSetDefault().setField(FieldKey.TITLE, "FileApiRegression");

        File destinationWithoutExt = new File(source.getParentFile(), "file-api-write-as-dest");
        AudioFileIO.writeAs(audioFile, destinationWithoutExt.getPath());

        File destinationWithExt = new File(destinationWithoutExt.getPath() + ".mp3");
        assertEquals(destinationWithExt.getAbsolutePath(), audioFile.getFile().getAbsolutePath());
        assertTrue(destinationWithExt.isFile());
    }
}

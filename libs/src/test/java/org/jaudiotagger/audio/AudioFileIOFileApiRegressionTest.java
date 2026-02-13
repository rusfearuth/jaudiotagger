package org.jaudiotagger.audio;

import org.jaudiotagger.AbstractTestCase;
import org.jaudiotagger.audio.exceptions.CannotReadException;
import org.jaudiotagger.tag.FieldKey;

import java.io.File;

public class AudioFileIOFileApiRegressionTest extends AbstractTestCase
{
    private static final String SOURCE_MP3 = "01.mp3";
    private static final String SOURCE_OGG = "test.ogg";

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

    public void testReadOggUsingFileApi() throws Exception
    {
        File source = copyAudioToTmp(SOURCE_OGG, new File("file-api-read.ogg"));
        AudioFile audioFile = AudioFileIO.read(source);

        assertEquals("ogg", audioFile.getExt());
        assertEquals(source.getAbsolutePath(), audioFile.getFile().getAbsolutePath());
        assertNotNull(audioFile.getAudioHeader());
    }

    public void testReadAsOggUsingFileApi() throws Exception
    {
        File source = copyAudioToTmp(SOURCE_OGG, new File("file-api-read-as.ogg"));
        AudioFile audioFile = AudioFileIO.readAs(source, "ogg");

        assertEquals("ogg", audioFile.getExt());
        assertNotNull(audioFile.getAudioHeader());
    }

    public void testReadMagicOggUsingFileApiHasParityWithPathApi() throws Exception
    {
        File source = copyAudioToTmp(SOURCE_OGG, new File("file-api-read-magic.ogg"));
        try
        {
            AudioFileIO.readMagic(source);
            fail("Expected CannotReadException");
        }
        catch (CannotReadException expected)
        {
            // parity with existing Ogg magic detection behavior
        }
    }

    public void testWriteAsOggUsingStringApi() throws Exception
    {
        File source = copyAudioToTmp(SOURCE_OGG, new File("file-api-write-as-source.ogg"));
        AudioFile audioFile = AudioFileIO.read(source);
        audioFile.getTagOrCreateAndSetDefault().setField(FieldKey.TITLE, "FileApiRegressionOgg");

        File destinationWithoutExt = new File(source.getParentFile(), "file-api-write-as-dest-ogg");
        AudioFileIO.writeAs(audioFile, destinationWithoutExt.getPath());

        File destinationWithExt = new File(destinationWithoutExt.getPath() + ".ogg");
        assertEquals(destinationWithExt.getAbsolutePath(), audioFile.getFile().getAbsolutePath());
        assertTrue(destinationWithExt.isFile());
    }
}

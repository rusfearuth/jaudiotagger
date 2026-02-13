package org.jaudiotagger.audio;

import org.jaudiotagger.AbstractTestCase;
import org.jaudiotagger.audio.exceptions.CannotReadException;
import org.jaudiotagger.tag.FieldKey;

import java.io.File;
import java.nio.file.Path;

public class AudioFileIOPathApiTest extends AbstractTestCase
{
    private static final String SOURCE_MP3 = "01.mp3";
    private static final String SOURCE_OGG = "test.ogg";

    public void testReadUsingPathApi() throws Exception
    {
        File source = copyAudioToTmp(SOURCE_MP3, new File("path-api-read.mp3"));
        AudioFile audioFile = AudioFileIO.read(source.toPath());

        assertEquals("mp3", audioFile.getExt());
        assertEquals(source.getAbsolutePath(), audioFile.getFile().getAbsolutePath());
        assertNotNull(audioFile.getAudioHeader());
    }

    public void testReadAsUsingPathApi() throws Exception
    {
        File source = copyAudioToTmp(SOURCE_MP3, new File("path-api-read-as.mp3"));
        AudioFile audioFile = AudioFileIO.readAs(source.toPath(), "mp3");

        assertEquals("mp3", audioFile.getExt());
    }

    public void testReadMagicUsingPathApi() throws Exception
    {
        File source = copyAudioToTmp(SOURCE_MP3, new File("path-api-read-magic.mp3"));
        AudioFile audioFile = AudioFileIO.readMagic(source.toPath());

        assertEquals("mp3", audioFile.getExt());
    }

    public void testWriteAsUsingPathApi() throws Exception
    {
        File source = copyAudioToTmp(SOURCE_MP3, new File("path-api-write-as-source.mp3"));
        AudioFile audioFile = AudioFileIO.read(source.toPath());
        audioFile.getTagOrCreateAndSetDefault().setField(FieldKey.ALBUM, "PathApiRegression");

        Path destinationWithoutExt = new File(source.getParentFile(), "path-api-write-as-dest").toPath();
        AudioFileIO.writeAs(audioFile, destinationWithoutExt);

        File destinationWithExt = new File(destinationWithoutExt.toString() + ".mp3");
        assertEquals(destinationWithExt.getAbsolutePath(), audioFile.getFile().getAbsolutePath());
        assertTrue(destinationWithExt.isFile());
    }

    public void testFileAndPathApisReturnEquivalentExtensions() throws Exception
    {
        File source = copyAudioToTmp(SOURCE_MP3, new File("path-api-parity.mp3"));
        AudioFile byFile = AudioFileIO.read(source);
        AudioFile byPath = AudioFileIO.read(source.toPath());

        assertEquals(byFile.getExt(), byPath.getExt());
    }

    public void testReadOggUsingPathApi() throws Exception
    {
        File source = copyAudioToTmp(SOURCE_OGG, new File("path-api-read.ogg"));
        AudioFile audioFile = AudioFileIO.read(source.toPath());

        assertEquals("ogg", audioFile.getExt());
        assertEquals(source.getAbsolutePath(), audioFile.getFile().getAbsolutePath());
        assertNotNull(audioFile.getAudioHeader());
    }

    public void testReadAsOggUsingPathApi() throws Exception
    {
        File source = copyAudioToTmp(SOURCE_OGG, new File("path-api-read-as.ogg"));
        AudioFile audioFile = AudioFileIO.readAs(source.toPath(), "ogg");

        assertEquals("ogg", audioFile.getExt());
    }

    public void testReadMagicOggUsingPathApiHasParityWithFileApi() throws Exception
    {
        File source = copyAudioToTmp(SOURCE_OGG, new File("path-api-read-magic.ogg"));
        try
        {
            AudioFileIO.readMagic(source.toPath());
            fail("Expected CannotReadException");
        }
        catch (CannotReadException expected)
        {
            // parity with existing File API behavior for Ogg magic detection
        }
    }

    public void testWriteAsOggUsingPathApi() throws Exception
    {
        File source = copyAudioToTmp(SOURCE_OGG, new File("path-api-write-as-source.ogg"));
        AudioFile audioFile = AudioFileIO.read(source.toPath());
        audioFile.getTagOrCreateAndSetDefault().setField(FieldKey.ALBUM, "PathApiRegressionOgg");

        Path destinationWithoutExt = new File(source.getParentFile(), "path-api-write-as-dest-ogg").toPath();
        AudioFileIO.writeAs(audioFile, destinationWithoutExt);

        File destinationWithExt = new File(destinationWithoutExt.toString() + ".ogg");
        assertEquals(destinationWithExt.getAbsolutePath(), audioFile.getFile().getAbsolutePath());
        assertTrue(destinationWithExt.isFile());
    }

    public void testFileAndPathApisReturnEquivalentExtensionsForOgg() throws Exception
    {
        File source = copyAudioToTmp(SOURCE_OGG, new File("path-api-parity.ogg"));
        AudioFile byFile = AudioFileIO.read(source);
        AudioFile byPath = AudioFileIO.read(source.toPath());

        assertEquals(byFile.getExt(), byPath.getExt());
    }
}

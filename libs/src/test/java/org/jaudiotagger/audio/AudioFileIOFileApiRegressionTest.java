package org.jaudiotagger.audio;

import org.jaudiotagger.AbstractTestCase;
import org.jaudiotagger.audio.exceptions.CannotReadException;
import org.jaudiotagger.tag.FieldKey;

import java.io.File;

public class AudioFileIOFileApiRegressionTest extends AbstractTestCase
{
    private static final String SOURCE_MP3 = "01.mp3";
    private static final String SOURCE_OGG = "test.ogg";
    private static final String SOURCE_WMA = "test1.wma";
    private static final String SOURCE_RA = "test01.ra";
    private static final String SOURCE_RM = "test05.rm";

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

    public void testReadWmaUsingFileApi() throws Exception
    {
        File source = copyAudioToTmp(SOURCE_WMA, new File("file-api-read.wma"));
        AudioFile audioFile = AudioFileIO.read(source);

        assertEquals("wma", audioFile.getExt());
        assertEquals(source.getAbsolutePath(), audioFile.getFile().getAbsolutePath());
        assertNotNull(audioFile.getAudioHeader());
    }

    public void testReadAsWmaUsingFileApi() throws Exception
    {
        File source = copyAudioToTmp(SOURCE_WMA, new File("file-api-read-as.wma"));
        AudioFile audioFile = AudioFileIO.readAs(source, "wma");

        assertEquals("wma", audioFile.getExt());
        assertNotNull(audioFile.getAudioHeader());
    }

    public void testReadMagicWmaUsingFileApiHasParityWithPathApi() throws Exception
    {
        File source = copyAudioToTmp(SOURCE_WMA, new File("file-api-read-magic.wma"));
        try
        {
            AudioFile byFile = AudioFileIO.readMagic(source);
            AudioFile byPath = AudioFileIO.readMagic(source.toPath());
            assertEquals(byPath.getExt(), byFile.getExt());
        }
        catch (CannotReadException expectedByFile)
        {
            try
            {
                AudioFileIO.readMagic(source.toPath());
                fail("Expected CannotReadException");
            }
            catch (CannotReadException expectedByPath)
            {
                // parity with existing Path API behavior for WMA magic detection
            }
        }
    }

    public void testWriteAsWmaUsingStringApi() throws Exception
    {
        File source = copyAudioToTmp(SOURCE_WMA, new File("file-api-write-as-source.wma"));
        AudioFile audioFile = AudioFileIO.read(source);
        audioFile.getTagOrCreateAndSetDefault().setField(FieldKey.TITLE, "FileApiRegressionWma");

        File destinationWithoutExt = new File(source.getParentFile(), "file-api-write-as-dest-wma");
        AudioFileIO.writeAs(audioFile, destinationWithoutExt.getPath());

        File destinationWithExt = new File(destinationWithoutExt.getPath() + ".wma");
        assertEquals(destinationWithExt.getAbsolutePath(), audioFile.getFile().getAbsolutePath());
        assertTrue(destinationWithExt.isFile());
    }

    public void testFileAndPathApisReturnEquivalentExtensionsForWma() throws Exception
    {
        File source = copyAudioToTmp(SOURCE_WMA, new File("file-api-parity.wma"));
        AudioFile byFile = AudioFileIO.read(source);
        AudioFile byPath = AudioFileIO.read(source.toPath());

        assertEquals(byPath.getExt(), byFile.getExt());
    }

    public void testReadRaUsingFileApi() throws Exception
    {
        File source = copyAudioToTmp(SOURCE_RA, new File("file-api-read.ra"));
        AudioFile audioFile = AudioFileIO.read(source);

        assertEquals("ra", audioFile.getExt());
        assertEquals(source.getAbsolutePath(), audioFile.getFile().getAbsolutePath());
        assertNotNull(audioFile.getAudioHeader());
    }

    public void testReadAsRaUsingFileApi() throws Exception
    {
        File source = copyAudioToTmp(SOURCE_RA, new File("file-api-read-as.ra"));
        AudioFile audioFile = AudioFileIO.readAs(source, "ra");

        assertEquals("ra", audioFile.getExt());
        assertNotNull(audioFile.getAudioHeader());
    }

    public void testReadMagicRaUsingFileApiHasParityWithPathApi() throws Exception
    {
        File source = copyAudioToTmp(SOURCE_RA, new File("file-api-read-magic.ra"));
        try
        {
            AudioFile byFile = AudioFileIO.readMagic(source);
            AudioFile byPath = AudioFileIO.readMagic(source.toPath());
            assertEquals(byPath.getExt(), byFile.getExt());
        }
        catch (CannotReadException expectedByFile)
        {
            try
            {
                AudioFileIO.readMagic(source.toPath());
                fail("Expected CannotReadException");
            }
            catch (CannotReadException expectedByPath)
            {
                // parity with existing Path API behavior for RA magic detection
            }
        }
    }

    public void testFileAndPathApisReturnEquivalentExtensionsForRa() throws Exception
    {
        File source = copyAudioToTmp(SOURCE_RA, new File("file-api-parity.ra"));
        AudioFile byFile = AudioFileIO.read(source);
        AudioFile byPath = AudioFileIO.read(source.toPath());

        assertEquals(byPath.getExt(), byFile.getExt());
    }

    public void testReadRmUsingFileApi() throws Exception
    {
        File source = copyAudioToTmp(SOURCE_RM, new File("file-api-read.rm"));
        AudioFile audioFile = AudioFileIO.read(source);

        assertEquals("rm", audioFile.getExt());
        assertEquals(source.getAbsolutePath(), audioFile.getFile().getAbsolutePath());
        assertNotNull(audioFile.getAudioHeader());
    }

    public void testReadAsRmUsingFileApi() throws Exception
    {
        File source = copyAudioToTmp(SOURCE_RM, new File("file-api-read-as.rm"));
        AudioFile audioFile = AudioFileIO.readAs(source, "rm");

        assertEquals("rm", audioFile.getExt());
        assertNotNull(audioFile.getAudioHeader());
    }

    public void testReadMagicRmUsingFileApiHasParityWithPathApi() throws Exception
    {
        File source = copyAudioToTmp(SOURCE_RM, new File("file-api-read-magic.rm"));
        try
        {
            AudioFile byFile = AudioFileIO.readMagic(source);
            AudioFile byPath = AudioFileIO.readMagic(source.toPath());
            assertEquals(byPath.getExt(), byFile.getExt());
        }
        catch (CannotReadException expectedByFile)
        {
            try
            {
                AudioFileIO.readMagic(source.toPath());
                fail("Expected CannotReadException");
            }
            catch (CannotReadException expectedByPath)
            {
                // parity with existing Path API behavior for RM magic detection
            }
        }
    }

    public void testFileAndPathApisReturnEquivalentExtensionsForRm() throws Exception
    {
        File source = copyAudioToTmp(SOURCE_RM, new File("file-api-parity.rm"));
        AudioFile byFile = AudioFileIO.read(source);
        AudioFile byPath = AudioFileIO.read(source.toPath());

        assertEquals(byPath.getExt(), byFile.getExt());
    }
}

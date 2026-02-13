package org.jaudiotagger.audio;

import org.jaudiotagger.AbstractTestCase;
import org.jaudiotagger.tag.FieldKey;

import java.io.File;
import java.nio.file.Path;

public class AudioFileIOPathApiTest extends AbstractTestCase
{
    public void testReadUsingPathApi() throws Exception
    {
        File source = copyAudioToTmp("01.mp3", new File("path-api-read.mp3"));
        AudioFile audioFile = AudioFileIO.read(source.toPath());

        assertEquals("mp3", audioFile.getExt());
        assertEquals(source.getAbsolutePath(), audioFile.getFile().getAbsolutePath());
        assertNotNull(audioFile.getAudioHeader());
    }

    public void testReadAsUsingPathApi() throws Exception
    {
        File source = copyAudioToTmp("01.mp3", new File("path-api-read-as.mp3"));
        AudioFile audioFile = AudioFileIO.readAs(source.toPath(), "mp3");

        assertEquals("mp3", audioFile.getExt());
    }

    public void testReadMagicUsingPathApi() throws Exception
    {
        File source = copyAudioToTmp("01.mp3", new File("path-api-read-magic.mp3"));
        AudioFile audioFile = AudioFileIO.readMagic(source.toPath());

        assertEquals("mp3", audioFile.getExt());
    }

    public void testWriteAsUsingPathApi() throws Exception
    {
        File source = copyAudioToTmp("01.mp3", new File("path-api-write-as-source.mp3"));
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
        File source = copyAudioToTmp("01.mp3", new File("path-api-parity.mp3"));
        AudioFile byFile = AudioFileIO.read(source);
        AudioFile byPath = AudioFileIO.read(source.toPath());

        assertEquals(byFile.getExt(), byPath.getExt());
    }
}

package org.jaudiotagger.issues;

import org.jaudiotagger.AbstractTestCase;
import org.jaudiotagger.TestImageAssertions;
import org.jaudiotagger.audio.AudioFile;
import org.jaudiotagger.audio.AudioFileIO;
import org.jaudiotagger.tag.Tag;
import org.jaudiotagger.tag.id3.valuepair.ImageFormats;
import org.jaudiotagger.tag.images.Artwork;
import org.jaudiotagger.tag.images.ArtworkFactory;

import java.io.File;

/**
  Vorbis Comment reading new Image Format
*/
public class Issue286Test extends AbstractTestCase
{
    /*
     * TestRead Vorbis COverArt One
     * @throws Exception
     */
    public void testReadVorbisCoverartOne() throws Exception
    {
        File file = new File("testdata", "test76.ogg");
        AudioFile af = AudioFileIO.read(file);
        assertEquals(1,af.getTag().getArtworkList().size());
        Artwork artwork = af.getTag().getFirstArtwork();
        System.out.println(artwork);
        TestImageAssertions.assertWidth(artwork, 600);
        TestImageAssertions.assertHeight(artwork, 800);
        assertEquals("image/jpeg",artwork.getMimeType());
        assertEquals(3,artwork.getPictureType());

    }

    /*
     * TestRead Vorbis CoverArt Two
     * @throws Exception
     */
    public void testReadVorbisCoverartTwo() throws Exception
    {
        File file = new File("testdata", "test77.ogg");
        AudioFile af = AudioFileIO.read(file);
        assertEquals(1,af.getTag().getArtworkList().size());
        Artwork artwork = af.getTag().getFirstArtwork();
        System.out.println(artwork);
        TestImageAssertions.assertWidth(artwork, 600);
        TestImageAssertions.assertHeight(artwork, 800);
        assertEquals("image/jpeg",artwork.getMimeType());
        assertEquals(3,artwork.getPictureType());

    }

    /**
     * Test reading/writing artwork to Ogg
     */
    public void testReadWriteArtworkFieldsToOggVorbis()
    {
        File testFile = null;
        Exception exceptionCaught = null;
        try
        {
            testFile = AbstractTestCase.copyAudioToTmp("test3.ogg");

            //Read File okay
            AudioFile af = AudioFileIO.read(testFile);
            Tag tag = af.getTag();

            assertEquals(1, tag.getArtworkList().size());
            assertTrue(tag.getArtworkList().get(0) instanceof Artwork);
            Artwork artwork = tag.getFirstArtwork();
            assertEquals("image/png", artwork.getMimeType());
            assertNotNull(artwork.getImage());
            assertEquals("",artwork.getDescription());
            TestImageAssertions.assertWidth(artwork, 200);

            //Now add new image
            Artwork newartwork = ArtworkFactory.createArtworkFromFile(new File("testdata", "coverart.png"));
            newartwork.setDescription("A new file");
            assertTrue(ImageFormats.isPortableFormat(newartwork.getBinaryData()));
            tag.addField(newartwork);
            af.commit();
            af = AudioFileIO.read(testFile);
            tag = af.getTag();
            assertEquals(2, tag.getArtworkList().size());


            assertTrue(tag.getArtworkList().get(0) instanceof Artwork);
            artwork = tag.getFirstArtwork();
            assertEquals("image/png", artwork.getMimeType());
            assertNotNull(artwork.getImage());
            assertEquals("",artwork.getDescription());
            TestImageAssertions.assertWidth(artwork, 200);

            assertTrue(tag.getArtworkList().get(1) instanceof Artwork);
            artwork = tag.getArtworkList().get(1);
            assertEquals("image/png", artwork.getMimeType());
            assertNotNull(artwork.getImage());
            assertEquals("A new file",artwork.getDescription());
            TestImageAssertions.assertWidth(artwork, 200);


        }
        catch (Exception e)
        {
            e.printStackTrace();
            exceptionCaught = e;
        }

        assertNull(exceptionCaught);
    }

}

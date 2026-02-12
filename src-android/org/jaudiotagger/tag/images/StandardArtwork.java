package org.jaudiotagger.tag.images;

import org.jaudiotagger.audio.flac.metadatablock.MetadataBlockDataPicture;

import java.io.File;
import java.io.IOException;

/**
 * In Android profile standard artwork is implemented by the Android-safe implementation.
 */
public class StandardArtwork extends AndroidArtwork
{
    public static StandardArtwork createArtworkFromFile(File file) throws IOException
    {
        StandardArtwork artwork = new StandardArtwork();
        artwork.setFromFile(file);
        return artwork;
    }

    public static StandardArtwork createLinkedArtworkFromURL(String url) throws IOException
    {
        StandardArtwork artwork = new StandardArtwork();
        artwork.setLinkedFromURL(url);
        return artwork;
    }

    public static StandardArtwork createArtworkFromMetadataBlockDataPicture(MetadataBlockDataPicture coverArt)
    {
        StandardArtwork artwork = new StandardArtwork();
        artwork.setFromMetadataBlockDataPicture(coverArt);
        return artwork;
    }
}

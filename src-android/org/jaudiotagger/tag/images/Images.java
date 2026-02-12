package org.jaudiotagger.tag.images;

import java.io.IOException;

/**
 * Platform-neutral accessor for image object represented by Artwork.
 */
public class Images
{
    public static Object getImage(Artwork artwork) throws IOException
    {
        return artwork.getImage();
    }
}

package org.jaudiotagger.tag.images;

import java.io.IOException;

/**
 * Image handler abstraction that does not expose desktop-only image types.
 */
public interface ImageHandler
{
    void reduceQuality(Artwork artwork, int maxSize) throws IOException;

    void makeSmaller(Artwork artwork, int size) throws IOException;

    boolean isMimeTypeWritable(String mimeType);

    byte[] writeImage(Object image, String mimeType) throws IOException;

    byte[] writeImageAsPng(Object image) throws IOException;

    void showReadFormats();

    void showWriteFormats();
}

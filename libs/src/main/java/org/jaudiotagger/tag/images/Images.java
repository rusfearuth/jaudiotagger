package org.jaudiotagger.tag.images;

import java.io.IOException;

/**
 * Utility helpers for artwork data.
 */
public class Images
{
    public static final class ImageInfo
    {
        private final int width;
        private final int height;
        private final int type;

        private ImageInfo(int width, int height, int type)
        {
            this.width = width;
            this.height = height;
            this.type = type;
        }

        public int getWidth()
        {
            return width;
        }

        public int getHeight()
        {
            return height;
        }

        public int getType()
        {
            return type;
        }
    }

    /**
     * Backward-compatible helper for legacy tests that only need dimensions.
     */
    public static ImageInfo getImage(Artwork artwork) throws IOException
    {
        if (artwork.getWidth() > 0 && artwork.getHeight() > 0)
        {
            return new ImageInfo(artwork.getWidth(), artwork.getHeight(), 0);
        }

        byte[] imageData = getImageData(artwork);
        int[] dimensions = ImageSizeExtractor.extract(imageData);
        if (dimensions == null)
        {
            int length = imageData == null ? -1 : imageData.length;
            String mimeType = artwork.getMimeType();
            throw new IOException("Unable to decode image dimensions, mimeType=" + mimeType + ", length=" + length);
        }
        return new ImageInfo(dimensions[0], dimensions[1], 0);
    }

    public static byte[] getImageData(Artwork artwork) throws IOException
    {
        Object image = artwork.getImage();
        return image instanceof byte[] ? (byte[]) image : artwork.getBinaryData();
    }
}

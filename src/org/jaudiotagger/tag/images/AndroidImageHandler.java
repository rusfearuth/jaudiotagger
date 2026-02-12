package org.jaudiotagger.tag.images;

import java.io.IOException;

/**
 * Image handling implementation used for Android mode.
 */
public class AndroidImageHandler implements ImageHandler
{
    private static AndroidImageHandler instance;

    public static AndroidImageHandler getInstanceOf()
    {
        if (instance == null)
        {
            instance = new AndroidImageHandler();
        }
        return instance;
    }

    private AndroidImageHandler()
    {
    }

    @Override
    public void reduceQuality(Artwork artwork, int maxSize) throws IOException
    {
        if (artwork.getBinaryData() != null && artwork.getBinaryData().length > maxSize)
        {
            throw new IOException("Image resize support is not available yet");
        }
    }

    @Override
    public void makeSmaller(Artwork artwork, int size) throws IOException
    {
        throw new IOException("Image resize support is not available yet");
    }

    @Override
    public boolean isMimeTypeWritable(String mimeType)
    {
        return mimeType != null && !mimeType.isEmpty();
    }

    @Override
    public byte[] writeImage(byte[] imageData, String mimeType) throws IOException
    {
        if (imageData == null)
        {
            throw new IOException("Image data is null");
        }
        return imageData;
    }

    @Override
    public byte[] writeImageAsPng(byte[] imageData) throws IOException
    {
        if (imageData == null)
        {
            throw new IOException("Image data is null");
        }
        return imageData;
    }

    @Override
    public void showReadFormats()
    {
    }

    @Override
    public void showWriteFormats()
    {
    }
}

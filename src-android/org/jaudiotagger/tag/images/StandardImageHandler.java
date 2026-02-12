package org.jaudiotagger.tag.images;

import java.io.IOException;

/**
 * Android profile does not provide desktop image pipeline; delegate to Android handler.
 */
public class StandardImageHandler implements ImageHandler
{
    private static StandardImageHandler instance;

    public static StandardImageHandler getInstanceOf()
    {
        if (instance == null)
        {
            instance = new StandardImageHandler();
        }
        return instance;
    }

    private final AndroidImageHandler delegate = AndroidImageHandler.getInstanceOf();

    private StandardImageHandler()
    {
    }

    @Override
    public void reduceQuality(Artwork artwork, int maxSize) throws IOException
    {
        delegate.reduceQuality(artwork, maxSize);
    }

    @Override
    public void makeSmaller(Artwork artwork, int size) throws IOException
    {
        delegate.makeSmaller(artwork, size);
    }

    @Override
    public boolean isMimeTypeWritable(String mimeType)
    {
        return delegate.isMimeTypeWritable(mimeType);
    }

    @Override
    public byte[] writeImage(Object image, String mimeType) throws IOException
    {
        return delegate.writeImage(image, mimeType);
    }

    @Override
    public byte[] writeImageAsPng(Object image) throws IOException
    {
        return delegate.writeImageAsPng(image);
    }

    @Override
    public void showReadFormats()
    {
        delegate.showReadFormats();
    }

    @Override
    public void showWriteFormats()
    {
        delegate.showWriteFormats();
    }
}

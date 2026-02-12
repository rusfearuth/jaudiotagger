package org.jaudiotagger.tag.images;

import org.jaudiotagger.tag.id3.valuepair.ImageFormats;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.lang.reflect.Method;

/**
 * Image handling for Android runtime without compile-time android.* dependency.
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
        if (artwork == null || artwork.getBinaryData() == null)
        {
            return;
        }
        if (artwork.getBinaryData().length <= maxSize)
        {
            return;
        }

        int target = Math.max(128, Math.min(artwork.getWidth(), artwork.getHeight()));
        while (artwork.getBinaryData().length > maxSize && target > 64)
        {
            makeSmaller(artwork, target);
            target = target / 2;
        }
    }

    @Override
    public void makeSmaller(Artwork artwork, int size) throws IOException
    {
        if (artwork == null || artwork.getBinaryData() == null)
        {
            return;
        }

        try
        {
            Object bitmap = decodeBitmap(artwork.getBinaryData());
            if (bitmap == null)
            {
                return;
            }

            Class<?> bitmapClass = Class.forName("android.graphics.Bitmap");
            int width = (Integer) bitmapClass.getMethod("getWidth").invoke(bitmap);
            int height = (Integer) bitmapClass.getMethod("getHeight").invoke(bitmap);
            if (width <= 0 || height <= 0)
            {
                return;
            }

            float scale = Math.min((float) size / (float) width, (float) size / (float) height);
            if (scale >= 1.0f)
            {
                artwork.setWidth(width);
                artwork.setHeight(height);
                return;
            }

            int newWidth = Math.max(1, Math.round(width * scale));
            int newHeight = Math.max(1, Math.round(height * scale));
            Method createScaledBitmap = bitmapClass.getMethod("createScaledBitmap", bitmapClass, int.class, int.class, boolean.class);
            Object scaled = createScaledBitmap.invoke(null, bitmap, newWidth, newHeight, true);

            String mimeType = artwork.getMimeType();
            byte[] imageData = mimeType != null && isMimeTypeWritable(mimeType)
                ? writeImage(scaled, mimeType)
                : writeImageAsPng(scaled);

            artwork.setBinaryData(imageData);
            artwork.setWidth(newWidth);
            artwork.setHeight(newHeight);
        }
        catch (ReflectiveOperationException e)
        {
            throw new IOException("Android graphics runtime not available", e);
        }
    }

    @Override
    public boolean isMimeTypeWritable(String mimeType)
    {
        if (mimeType == null)
        {
            return false;
        }
        return mimeType.equalsIgnoreCase(ImageFormats.MIME_TYPE_PNG)
            || mimeType.equalsIgnoreCase(ImageFormats.MIME_TYPE_JPEG)
            || mimeType.equalsIgnoreCase(ImageFormats.MIME_TYPE_JPG)
            || mimeType.equalsIgnoreCase(ImageFormats.MIME_TYPE_GIF)
            || mimeType.equalsIgnoreCase(ImageFormats.MIME_TYPE_BMP)
            || mimeType.equalsIgnoreCase("image/webp");
    }

    @Override
    public byte[] writeImage(Object image, String mimeType) throws IOException
    {
        String format = ImageFormats.MIME_TYPE_PNG.equalsIgnoreCase(mimeType) ? "PNG" : "JPEG";
        return compressBitmap(image, format, 92);
    }

    @Override
    public byte[] writeImageAsPng(Object image) throws IOException
    {
        return compressBitmap(image, "PNG", 100);
    }

    @Override
    public void showReadFormats()
    {
        // no-op in library mode
    }

    @Override
    public void showWriteFormats()
    {
        // no-op in library mode
    }

    Object decodeImage(byte[] data) throws IOException
    {
        return decodeBitmap(data);
    }

    int[] readDimensions(byte[] data) throws IOException
    {
        try
        {
            Object bitmap = decodeBitmap(data);
            if (bitmap == null)
            {
                return new int[]{0, 0};
            }
            Class<?> bitmapClass = Class.forName("android.graphics.Bitmap");
            int width = (Integer) bitmapClass.getMethod("getWidth").invoke(bitmap);
            int height = (Integer) bitmapClass.getMethod("getHeight").invoke(bitmap);
            return new int[]{width, height};
        }
        catch (ReflectiveOperationException e)
        {
            throw new IOException("Android graphics runtime not available", e);
        }
    }

    private Object decodeBitmap(byte[] data) throws IOException
    {
        try
        {
            Class<?> bitmapFactory = Class.forName("android.graphics.BitmapFactory");
            Method decode = bitmapFactory.getMethod("decodeByteArray", byte[].class, int.class, int.class);
            return decode.invoke(null, data, 0, data.length);
        }
        catch (ReflectiveOperationException e)
        {
            throw new IOException("Android graphics runtime not available", e);
        }
    }

    @SuppressWarnings("unchecked")
    private byte[] compressBitmap(Object bitmap, String formatName, int quality) throws IOException
    {
        if (bitmap == null)
        {
            throw new IOException("Image object is null");
        }

        try
        {
            Class<?> bitmapClass = Class.forName("android.graphics.Bitmap");
            Class<?> formatClass = Class.forName("android.graphics.Bitmap$CompressFormat");
            Object format = Enum.valueOf((Class<Enum>) formatClass.asSubclass(Enum.class), formatName);

            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            Method compress = bitmapClass.getMethod("compress", formatClass, int.class, OutputStream.class);
            boolean ok = (Boolean) compress.invoke(bitmap, format, quality, baos);
            if (!ok)
            {
                throw new IOException("Bitmap compression failed");
            }
            return baos.toByteArray();
        }
        catch (ReflectiveOperationException e)
        {
            throw new IOException("Android graphics runtime not available", e);
        }
    }
}

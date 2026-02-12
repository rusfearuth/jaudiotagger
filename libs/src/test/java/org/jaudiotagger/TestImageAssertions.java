package org.jaudiotagger;

import junit.framework.Assert;
import org.jaudiotagger.tag.id3.valuepair.ImageFormats;
import org.jaudiotagger.tag.images.Artwork;
import org.jaudiotagger.tag.images.ArtworkFactory;
import org.jaudiotagger.tag.images.Images;

import java.io.IOException;

public final class TestImageAssertions
{
    private TestImageAssertions()
    {
    }

    public static void assertWidth(Artwork artwork, int expectedWidth) throws IOException
    {
        Assert.assertEquals(expectedWidth, Images.getImage(artwork).getWidth());
    }

    public static void assertHeight(Artwork artwork, int expectedHeight) throws IOException
    {
        Assert.assertEquals(expectedHeight, Images.getImage(artwork).getHeight());
    }

    public static void assertDimensions(Artwork artwork, int expectedWidth, int expectedHeight) throws IOException
    {
        assertWidth(artwork, expectedWidth);
        assertHeight(artwork, expectedHeight);
    }

    public static void assertWidth(byte[] imageData, int expectedWidth) throws IOException
    {
        Assert.assertEquals(expectedWidth, toImageInfoSource(imageData).getWidth());
    }

    public static void assertHeight(byte[] imageData, int expectedHeight) throws IOException
    {
        Assert.assertEquals(expectedHeight, toImageInfoSource(imageData).getHeight());
    }

    public static void assertDimensions(byte[] imageData, int expectedWidth, int expectedHeight) throws IOException
    {
        assertWidth(imageData, expectedWidth);
        assertHeight(imageData, expectedHeight);
    }

    public static void assertDecodable(byte[] imageData) throws IOException
    {
        Images.ImageInfo imageInfo = toImageInfoSource(imageData);
        Assert.assertTrue(imageInfo.getWidth() > 0);
        Assert.assertTrue(imageInfo.getHeight() > 0);
    }

    public static void assertImageFormat(Artwork artwork, String expectedMimeType)
    {
        assertImageFormat(artwork.getBinaryData(), expectedMimeType);
    }

    public static void assertImageFormat(byte[] imageData, String expectedMimeType)
    {
        String detectedMimeType = ImageFormats.getMimeTypeForBinarySignature(imageData);
        Assert.assertEquals(expectedMimeType, detectedMimeType);
    }

    public static void assertBinarySignatureStable(byte[] expectedData, byte[] actualData)
    {
        String expectedMimeType = ImageFormats.getMimeTypeForBinarySignature(expectedData);
        String actualMimeType = ImageFormats.getMimeTypeForBinarySignature(actualData);
        Assert.assertEquals(expectedMimeType, actualMimeType);

        int signatureLength = getSignatureLength(expectedMimeType, expectedData.length, actualData.length);
        for (int i = 0; i < signatureLength; i++)
        {
            Assert.assertEquals(expectedData[i], actualData[i]);
        }
    }

    private static Images.ImageInfo toImageInfoSource(byte[] imageData) throws IOException
    {
        Artwork artwork = ArtworkFactory.getNew();
        artwork.setBinaryData(imageData);
        artwork.setMimeType(ImageFormats.getMimeTypeForBinarySignature(imageData));
        return Images.getImage(artwork);
    }

    private static int getSignatureLength(String mimeType, int expectedLength, int actualLength)
    {
        int max = Math.min(expectedLength, actualLength);
        if ("image/png".equals(mimeType))
        {
            return Math.min(8, max);
        }
        if ("image/jpeg".equals(mimeType))
        {
            return Math.min(4, max);
        }
        if ("image/gif".equals(mimeType))
        {
            return Math.min(6, max);
        }
        if ("image/bmp".equals(mimeType))
        {
            return Math.min(2, max);
        }
        return Math.min(4, max);
    }
}

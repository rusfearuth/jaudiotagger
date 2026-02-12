package org.jaudiotagger.tag.images;

final class ImageSizeExtractor
{
    private ImageSizeExtractor()
    {
    }

    static int[] extract(byte[] data)
    {
        if (data == null || data.length < 10)
        {
            return null;
        }

        int[] direct = extractAtOffset(data, 0);
        if (direct != null)
        {
            return direct;
        }

        int[] metadataBlock = extractMetadataBlockPicture(data, 0);
        if (metadataBlock != null)
        {
            return metadataBlock;
        }

        for (int i = 0; i < data.length - 10; i++)
        {
            int[] nested = extractAtOffset(data, i);
            if (nested != null)
            {
                return nested;
            }
        }

        return null;
    }

    private static int[] extractAtOffset(byte[] data, int offset)
    {
        int[] bmp = extractBmp(data, offset);
        if (bmp != null)
        {
            return bmp;
        }

        int[] png = extractPng(data, offset);
        if (png != null)
        {
            return png;
        }

        int[] gif = extractGif(data, offset);
        if (gif != null)
        {
            return gif;
        }

        return extractJpeg(data, offset);
    }

    private static int[] extractBmp(byte[] data, int offset)
    {
        if (data.length < offset + 26)
        {
            return null;
        }
        if (data[offset] != 'B' || data[offset + 1] != 'M')
        {
            return null;
        }

        int width = readIntLittleEndian(data, offset + 18);
        int height = readIntLittleEndian(data, offset + 22);
        if (width <= 0 || height == 0)
        {
            return null;
        }

        if (height < 0)
        {
            height = -height;
        }
        return new int[]{width, height};
    }

    private static int[] extractMetadataBlockPicture(byte[] data, int offset)
    {
        if (data.length < offset + 32)
        {
            return null;
        }

        int cursor = offset;
        int pictureType = readIntBigEndian(data, cursor);
        cursor += 4;
        if (pictureType < 0 || pictureType > 20)
        {
            return null;
        }

        int mimeLength = readIntBigEndian(data, cursor);
        cursor += 4;
        if (mimeLength < 0 || cursor + mimeLength > data.length)
        {
            return null;
        }
        cursor += mimeLength;

        if (cursor + 4 > data.length)
        {
            return null;
        }
        int descriptionLength = readIntBigEndian(data, cursor);
        cursor += 4;
        if (descriptionLength < 0 || cursor + descriptionLength > data.length)
        {
            return null;
        }
        cursor += descriptionLength;

        if (cursor + 20 > data.length)
        {
            return null;
        }

        int width = readIntBigEndian(data, cursor);
        cursor += 4;
        int height = readIntBigEndian(data, cursor);
        cursor += 4;

        readIntBigEndian(data, cursor);
        cursor += 4;
        readIntBigEndian(data, cursor);
        cursor += 4;

        int imageDataLength = readIntBigEndian(data, cursor);
        cursor += 4;
        if (imageDataLength < 0 || cursor + imageDataLength > data.length)
        {
            return null;
        }

        if (width > 0 && height > 0)
        {
            return new int[]{width, height};
        }

        return extractAtOffset(data, cursor);
    }

    private static int[] extractPng(byte[] data, int offset)
    {
        if (data.length < offset + 24)
        {
            return null;
        }
        if ((data[offset] & 0xFF) == 0x89
                && data[offset + 1] == 0x50
                && data[offset + 2] == 0x4E
                && data[offset + 3] == 0x47)
        {
            int width = readIntBigEndian(data, offset + 16);
            int height = readIntBigEndian(data, offset + 20);
            if (width > 0 && height > 0)
            {
                return new int[]{width, height};
            }
        }
        return null;
    }

    private static int[] extractGif(byte[] data, int offset)
    {
        if (data.length < offset + 10)
        {
            return null;
        }
        boolean isGif = data[offset] == 'G' && data[offset + 1] == 'I' && data[offset + 2] == 'F';
        if (!isGif)
        {
            return null;
        }

        int width = readUnsignedShortLittleEndian(data, offset + 6);
        int height = readUnsignedShortLittleEndian(data, offset + 8);
        if (width > 0 && height > 0)
        {
            return new int[]{width, height};
        }
        return null;
    }

    private static int[] extractJpeg(byte[] data, int offset)
    {
        if (data.length < offset + 4 || (data[offset] & 0xFF) != 0xFF || (data[offset + 1] & 0xFF) != 0xD8)
        {
            return null;
        }

        int cursor = offset + 2;
        while (cursor + 9 < data.length)
        {
            if ((data[cursor] & 0xFF) != 0xFF)
            {
                cursor++;
                continue;
            }

            while (cursor < data.length && (data[cursor] & 0xFF) == 0xFF)
            {
                cursor++;
            }
            if (cursor >= data.length)
            {
                return null;
            }

            int marker = data[cursor] & 0xFF;
            cursor++;

            if (marker == 0xD9 || marker == 0xDA)
            {
                break;
            }
            if (marker >= 0xD0 && marker <= 0xD7)
            {
                continue;
            }
            if (cursor + 1 >= data.length)
            {
                return null;
            }

            int segmentLength = readUnsignedShortBigEndian(data, cursor);
            if (segmentLength < 2 || cursor + segmentLength > data.length)
            {
                return null;
            }

            boolean sof = (marker >= 0xC0 && marker <= 0xC3)
                    || (marker >= 0xC5 && marker <= 0xC7)
                    || (marker >= 0xC9 && marker <= 0xCB)
                    || (marker >= 0xCD && marker <= 0xCF);
            if (sof)
            {
                if (cursor + 7 >= data.length)
                {
                    return null;
                }
                int height = readUnsignedShortBigEndian(data, cursor + 3);
                int width = readUnsignedShortBigEndian(data, cursor + 5);
                if (width > 0 && height > 0)
                {
                    return new int[]{width, height};
                }
                return null;
            }

            cursor += segmentLength;
        }

        return null;
    }

    private static int readIntBigEndian(byte[] data, int offset)
    {
        return ((data[offset] & 0xFF) << 24)
                | ((data[offset + 1] & 0xFF) << 16)
                | ((data[offset + 2] & 0xFF) << 8)
                | (data[offset + 3] & 0xFF);
    }

    private static int readUnsignedShortBigEndian(byte[] data, int offset)
    {
        return ((data[offset] & 0xFF) << 8) | (data[offset + 1] & 0xFF);
    }

    private static int readUnsignedShortLittleEndian(byte[] data, int offset)
    {
        return (data[offset] & 0xFF) | ((data[offset + 1] & 0xFF) << 8);
    }

    private static int readIntLittleEndian(byte[] data, int offset)
    {
        return (data[offset] & 0xFF)
                | ((data[offset + 1] & 0xFF) << 8)
                | ((data[offset + 2] & 0xFF) << 16)
                | ((data[offset + 3] & 0xFF) << 24);
    }
}

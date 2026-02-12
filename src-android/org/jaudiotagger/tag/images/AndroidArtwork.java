package org.jaudiotagger.tag.images;

import org.jaudiotagger.audio.flac.metadatablock.MetadataBlockDataPicture;
import org.jaudiotagger.tag.id3.valuepair.ImageFormats;
import org.jaudiotagger.tag.reference.PictureTypes;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;

/**
 * Artwork implementation for Android profile.
 */
public class AndroidArtwork implements Artwork
{
    private byte[] binaryData;
    private String mimeType = "";
    private String description = "";
    private boolean isLinked = false;
    private String imageUrl = "";
    private int pictureType = -1;
    private int width;
    private int height;

    public AndroidArtwork()
    {
    }

    @Override
    public byte[] getBinaryData()
    {
        return binaryData;
    }

    @Override
    public void setBinaryData(byte[] binaryData)
    {
        this.binaryData = binaryData;
    }

    @Override
    public String getMimeType()
    {
        return mimeType;
    }

    @Override
    public void setMimeType(String mimeType)
    {
        this.mimeType = mimeType;
    }

    @Override
    public String getDescription()
    {
        return description;
    }

    @Override
    public int getHeight()
    {
        return height;
    }

    @Override
    public int getWidth()
    {
        return width;
    }

    @Override
    public void setDescription(String description)
    {
        this.description = description;
    }

    @Override
    public boolean setImageFromData()
    {
        if (binaryData == null || binaryData.length == 0)
        {
            return false;
        }

        if (mimeType == null || mimeType.isEmpty())
        {
            mimeType = ImageFormats.getMimeTypeForBinarySignature(binaryData);
        }

        try
        {
            int[] dimensions = AndroidImageHandler.getInstanceOf().readDimensions(binaryData);
            width = dimensions[0];
            height = dimensions[1];
            return width > 0 && height > 0;
        }
        catch (IOException e)
        {
            return false;
        }
    }

    @Override
    public Object getImage() throws IOException
    {
        return AndroidImageHandler.getInstanceOf().decodeImage(getBinaryData());
    }

    @Override
    public boolean isLinked()
    {
        return isLinked;
    }

    @Override
    public void setLinked(boolean linked)
    {
        isLinked = linked;
    }

    @Override
    public String getImageUrl()
    {
        return imageUrl;
    }

    @Override
    public void setImageUrl(String imageUrl)
    {
        this.imageUrl = imageUrl;
    }

    @Override
    public int getPictureType()
    {
        return pictureType;
    }

    @Override
    public void setPictureType(int pictureType)
    {
        this.pictureType = pictureType;
    }

    @Override
    public void setFromFile(File file) throws IOException
    {
        RandomAccessFile imageFile = new RandomAccessFile(file, "r");
        byte[] imagedata = new byte[(int) imageFile.length()];
        imageFile.read(imagedata);
        imageFile.close();

        setBinaryData(imagedata);
        setMimeType(ImageFormats.getMimeTypeForBinarySignature(imagedata));
        setDescription("");
        setPictureType(PictureTypes.DEFAULT_ID);
        setImageFromData();
    }

    public static AndroidArtwork createArtworkFromFile(File file) throws IOException
    {
        AndroidArtwork artwork = new AndroidArtwork();
        artwork.setFromFile(file);
        return artwork;
    }

    public static AndroidArtwork createLinkedArtworkFromURL(String url) throws IOException
    {
        AndroidArtwork artwork = new AndroidArtwork();
        artwork.setLinkedFromURL(url);
        return artwork;
    }

    public void setLinkedFromURL(String url) throws IOException
    {
        setLinked(true);
        setImageUrl(url);
    }

    @Override
    public void setFromMetadataBlockDataPicture(MetadataBlockDataPicture coverArt)
    {
        setMimeType(coverArt.getMimeType());
        setDescription(coverArt.getDescription());
        setPictureType(coverArt.getPictureType());
        if (coverArt.isImageUrl())
        {
            setLinked(coverArt.isImageUrl());
            setImageUrl(coverArt.getImageUrl());
        }
        else
        {
            setBinaryData(coverArt.getImageData());
        }
        setWidth(coverArt.getWidth());
        setHeight(coverArt.getHeight());
    }

    public static AndroidArtwork createArtworkFromMetadataBlockDataPicture(MetadataBlockDataPicture coverArt)
    {
        AndroidArtwork artwork = new AndroidArtwork();
        artwork.setFromMetadataBlockDataPicture(coverArt);
        return artwork;
    }

    @Override
    public void setWidth(int width)
    {
        this.width = width;
    }

    @Override
    public void setHeight(int height)
    {
        this.height = height;
    }
}

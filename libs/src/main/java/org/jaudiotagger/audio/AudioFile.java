package org.jaudiotagger.audio;

import org.jaudiotagger.audio.dsf.Dsf;
import org.jaudiotagger.audio.exceptions.*;
import org.jaudiotagger.audio.flac.metadatablock.MetadataBlockDataPicture;
import org.jaudiotagger.audio.generic.Permissions;
import org.jaudiotagger.audio.real.RealTag;
import org.jaudiotagger.logging.ErrorMessage;
import org.jaudiotagger.tag.Tag;
import org.jaudiotagger.tag.TagOptionSingleton;
import org.jaudiotagger.tag.aiff.AiffTag;
import org.jaudiotagger.tag.asf.AsfTag;
import org.jaudiotagger.tag.flac.FlacTag;
import org.jaudiotagger.tag.id3.AbstractID3v2Tag;
import org.jaudiotagger.tag.id3.ID3v22Tag;
import org.jaudiotagger.tag.id3.ID3v23Tag;
import org.jaudiotagger.tag.id3.ID3v24Tag;
import org.jaudiotagger.tag.mp4.Mp4Tag;
import org.jaudiotagger.tag.reference.ID3V2Version;
import org.jaudiotagger.tag.vorbiscomment.VorbisCommentTag;
import org.jaudiotagger.tag.wav.WavTag;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.RandomAccessFile;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.logging.Logger;

/**
 * Represents an audio file together with parsed audio header and metadata tag.
 *
 * <p>The preferred way to obtain an instance is through {@link AudioFileIO#read(Path)} or
 * other {@link AudioFileIO} read entry points.</p>
 *
 * <p>{@link #getAudioHeader()} exposes stream properties (bitrate, sample rate, encoding, and so on),
 * while {@link #getTag()} exposes editable metadata fields.</p>
 *
 * @author Raphael Slinckx
 * @version $Id$
 * @see AudioFileIO
 * @see Tag
 * @since v0.01
 */
public class AudioFile
{
    //Logger
    public static Logger logger = Logger.getLogger("org.jaudiotagger.audio");

    /**
     * Physical file represented by this instance.
     *
     * @deprecated Prefer {@link #getPath()} and {@link #setPath(Path)}.
     */
    @Deprecated
    protected File file;

    /**
     * Canonical path represented by this instance.
     */
    protected Path path;

    /**
     * Parsed audio header information.
     */
    protected AudioHeader audioHeader;

    /**
     * Parsed metadata tag.
     */
    protected Tag tag;
    
    /**
     * Lower-case file extension used for format-specific operations.
     */
    protected String extension;

    public AudioFile()
    {

    }

    /**
     * Creates an audio file model.
     *
     * <p>This constructor is primarily used by readers. Client code should usually read via
     * {@link AudioFileIO#read(Path)}.</p>
     *
     * @param f physical audio file.
     * @param audioHeader parsed header.
     * @param tag parsed tag, or {@code null} if absent.
     */
    public AudioFile(File f, AudioHeader audioHeader, Tag tag)
    {
        this.file = f;
        this.path = f != null ? f.toPath() : null;
        this.audioHeader = audioHeader;
        this.tag = tag;
    }


    /**
     * Creates an audio file model from pathname.
     *
     * <p>This constructor is primarily used by readers. Client code should usually read via
     * {@link AudioFileIO#read(Path)}.</p>
     *
     * @param s pathname for physical audio file.
     * @param audioHeader parsed header.
     * @param tag parsed tag.
     */
    public AudioFile(String s, AudioHeader audioHeader, Tag tag)
    {
        this.file = new File(s);
        this.path = this.file.toPath();
        this.audioHeader = audioHeader;
        this.tag = tag;
    }

    /**
     * Creates an audio file model from path.
     *
     * @param path pathname for physical audio file.
     * @param audioHeader parsed header.
     * @param tag parsed tag.
     */
    public AudioFile(Path path, AudioHeader audioHeader, Tag tag)
    {
        this.path = path;
        this.file = path != null ? path.toFile() : null;
        this.audioHeader = audioHeader;
        this.tag = tag;
    }

    /**
     * <p>Write the tag contained in this AudioFile to disk via {@link AudioFileIO}'s default instance.
     *
     * @throws NoWritePermissionsException if the file could not be written to due to file permissions
     * @throws CannotWriteException If the file could not be written/accessed, the extension wasn't recognized, or other IO error occurred.
     * @see AudioFileIO
     */
    public void commit() throws CannotWriteException
    {
        AudioFileIO.getDefaultAudioFileIO().writeFile(this, (Path) null);
    }

    /**
     * <p>Delete any tags that exist in the file via {@link AudioFileIO}'s default instance.
     *
     * @throws CannotWriteException If the file could not be written/accessed, the extension wasn't recognized, or other IO error occurred.
     * @see AudioFileIO
     */
    public void delete() throws CannotReadException, CannotWriteException
    {
        AudioFileIO.getDefaultAudioFileIO().deleteTag(this);
    }

    /**
     * Sets the backing physical file for this model.
     *
     * @param file physical file.
     */
    @Deprecated
    public void setFile(File file)
    {
        this.file = file;
        this.path = file != null ? file.toPath() : null;
    }

    /**
     * Returns the backing physical file.
     *
     * @return physical file.
     */
    @Deprecated
    public File getFile()
    {
        if (file == null && path != null)
        {
            file = path.toFile();
        }
        return file;
    }

    /**
     * Sets the backing physical path for this model.
     *
     * @param path physical path.
     */
    public void setPath(Path path)
    {
        this.path = path;
        this.file = path != null ? path.toFile() : null;
    }

    /**
     * Returns the backing physical path.
     *
     * @return physical path.
     */
    public Path getPath()
    {
        if (path == null && file != null)
        {
            path = file.toPath();
        }
        return path;
    }

    /**
     * Sets the file extension used for format-specific behavior.
     *
     * @param ext lower-case extension without leading dot.
     */
    public void setExt(String ext)
    {
        this.extension = ext;
    }

    /**
     * Returns the currently configured extension.
     *
     * @return lower-case extension without leading dot.
     */
    public String getExt()
    {
        return extension;
    }

    /**
     * Assigns a metadata tag to this audio file.
     *
     * @param tag tag instance to assign.
     */
    public void setTag(Tag tag)
    {
        this.tag = tag;
    }

    /**
     * Returns parsed audio header information.
     *
     * @return audio header.
     */
    public AudioHeader getAudioHeader()
    {
        return audioHeader;
    }

    /**
     * <p>Returns the tag contained in this AudioFile, the <code>Tag</code> contains any useful meta-data, like
     * artist, album, title, etc. If the file does not contain any tag the null is returned. Some audio formats do
     * not allow there to be no tag so in this case the reader would return an empty tag whereas for others such
     * as mp3 it is purely optional.
     *
     * @return Returns the tag contained in this AudioFile, or null if no tag exists.
     */
    public Tag getTag()
    {
        return tag;
    }

    /**
     * <p>Returns a multi-line string with the file path, the encoding audioHeader, and the tag contents.
     *
     * @return A multi-line string with the file path, the encoding audioHeader, and the tag contents.
     *         TODO Maybe this can be changed ?
     */
    public String toString()
    {
        final Path audioPath = getPath();
        final String displayPath = audioPath != null ? audioPath.toAbsolutePath().toString() : "<null>";
        return "AudioFile " + displayPath
                + "  --------\n" + audioHeader.toString() + "\n" + ((tag == null) ? "" : tag.toString()) + "\n-------------------";
    }

    /**
     * Validates that the file exists.
     *
     * @param file file to validate.
     * @throws FileNotFoundException if file does not exist.
     */
    public void checkFileExists(File file)throws FileNotFoundException
    {
        logger.config("Reading file:" + "path" + file.getPath() + ":abs:" + file.getAbsolutePath());
        if (!file.exists())
        {
            logger.severe("Unable to find:" + file.getPath());
            throw new FileNotFoundException(ErrorMessage.UNABLE_TO_FIND_FILE.getMsg(file.getPath()));
        }
    }

    /**
     * Opens the file in read-only or read-write mode after permission checks.
     *
     * @param file file to open.
     * @param readOnly when {@code true}, validates read access only.
     * @return random access file handle.
     * @throws ReadOnlyFileException if write access is required but unavailable.
     * @throws FileNotFoundException if file does not exist.
     * @throws CannotReadException if read access is unavailable.
     */
    protected RandomAccessFile checkFilePermissions(File file, boolean readOnly) throws ReadOnlyFileException, FileNotFoundException, CannotReadException
    {
        Path path = file.toPath();
        RandomAccessFile newFile;
        checkFileExists(file);

        // Unless opened as readonly the file must be writable
        if (readOnly)
        {
            //May not even be readable
            if(!Files.isReadable(path))
            {
                logger.severe("Unable to read file:" + path);
                logger.severe(Permissions.displayPermissions(path));
                throw new NoReadPermissionsException(ErrorMessage.GENERAL_READ_FAILED_DO_NOT_HAVE_PERMISSION_TO_READ_FILE.getMsg(path));
            }
            newFile = new RandomAccessFile(file, "r");
        }
        else
        {
            if (TagOptionSingleton.getInstance().isCheckIsWritable() && !Files.isWritable(path))
            {
                logger.severe(Permissions.displayPermissions(file.toPath()));
                logger.severe(Permissions.displayPermissions(path));
                throw new ReadOnlyFileException(ErrorMessage.NO_PERMISSIONS_TO_WRITE_TO_FILE.getMsg(path));
            }
            newFile = new RandomAccessFile(file, "rw");
        }
        return newFile;
    }

    /**
     * Optional debugging method. Must override to do anything interesting.
     *
     * @return  Empty string. 
     */
    public String displayStructureAsXML()
    {
        return "";
    }

    /**
     * Optional debugging method. Must override to do anything interesting.
     *
     * @return
     */
    public String displayStructureAsPlainText()
    {
        return "";
    }


    /**
     * Creates a default tag implementation based on current file extension.
     *
     * <p>The extension is taken from {@link #getExt()} or inferred from file name when missing.</p>
     *
     * @return default format-specific tag.
     * @throws RuntimeException when no default tag implementation exists for the extension.
     */
    public Tag createDefaultTag()
    {
        String extension = getExt();
        if(extension == null)
        {
            String fileName = file.getName();
            extension = fileName.substring(fileName.lastIndexOf('.') + 1);
            setExt(extension);
        }
        if(SupportedFileFormat.FLAC.getFilesuffix().equals(extension))
        {
            return new FlacTag(VorbisCommentTag.createNewTag(), new ArrayList< MetadataBlockDataPicture >());
        }
        else if(SupportedFileFormat.OGG.getFilesuffix().equals(extension))
        {
            return VorbisCommentTag.createNewTag();
        }
        else if(SupportedFileFormat.OGA.getFilesuffix().equals(extension))
        {
            return VorbisCommentTag.createNewTag();
        }
        else if(SupportedFileFormat.MP4.getFilesuffix().equals(extension))
        {
            return new Mp4Tag();
        }
        else if(SupportedFileFormat.M4A.getFilesuffix().equals(extension))
        {
            return new Mp4Tag();
        }
        else if(SupportedFileFormat.M4P.getFilesuffix().equals(extension))
        {
            return new Mp4Tag();
        }
        else if(SupportedFileFormat.WMA.getFilesuffix().equals(extension))
        {
            return new AsfTag();
        }
        else if(SupportedFileFormat.WAV.getFilesuffix().equals(extension))
        {
            return new WavTag(TagOptionSingleton.getInstance().getWavOptions());
        }
        else if(SupportedFileFormat.RA.getFilesuffix().equals(extension))
        {
            return new RealTag();
        }
        else if(SupportedFileFormat.RM.getFilesuffix().equals(extension))
        {
            return new RealTag();
        }
        else if(SupportedFileFormat.AIF.getFilesuffix().equals(extension))
        {
            return new AiffTag();
        }
        else if(SupportedFileFormat.AIFC.getFilesuffix().equals(extension))
        {
            return new AiffTag();
        }
        else if(SupportedFileFormat.AIFF.getFilesuffix().equals(extension))
        {
            return new AiffTag();
        }
        else if(SupportedFileFormat.DSF.getFilesuffix().equals(extension))
        {
            return Dsf.createDefaultTag();
        }
        else
        {
            throw new RuntimeException("Unable to create default tag for this file format");
        }

    }

    /**
     * Returns current tag, or creates a default one if none exists.
     *
     * <p>This method does not assign the created tag to the instance.</p>
     *
     * @return existing or newly created default tag.
     */
    public Tag getTagOrCreateDefault()
    {
        Tag tag = getTag();
        if(tag==null)
        {
            return createDefaultTag();
        }
        return tag;
    }

     /**
     * Returns current tag, or creates a default one and assigns it to this file.
     *
     * @return existing or newly created and assigned default tag.
     */
    public Tag getTagOrCreateAndSetDefault()
    {
        Tag tag = getTagOrCreateDefault();
        setTag(tag);
        return tag;
    }

    /**
     * Returns tag and converts ID3 versions to configured default when needed.
     *
     * <p>If no tag exists, a default one is created. Conversion currently applies to formats using ID3 tags
     * (for example DSF and MP3).</p>
     *
     * @return existing, converted, or newly created default tag.
     */
    public Tag getTagAndConvertOrCreateDefault()
    {
        Tag tag = getTagOrCreateDefault();

        /* TODO Currently only works for Dsf We need additional check here for Wav and Aif because they wrap the ID3 tag so never return
         * null for getTag() and the wrapper stores the location of the existing tag, would that be broken if tag set to something else
         */
        if(tag instanceof AbstractID3v2Tag)
        {
            Tag convertedTag = convertID3Tag((AbstractID3v2Tag)tag, TagOptionSingleton.getInstance().getID3V2Version());
            if(convertedTag!=null)
            {
                return convertedTag;
            }
            else
            {
                return tag;
            }
        }
        else
        {
           return tag;
        }
    }

    /**
     * Returns tag from {@link #getTagAndConvertOrCreateDefault()} and assigns it to this file.
     *
     * @return existing, converted, or newly created and assigned default tag.
     */
    public Tag getTagAndConvertOrCreateAndSetDefault()
    {
        Tag tag = getTagAndConvertOrCreateDefault();
        setTag(tag);
        return getTag();
    }

    /**
     * Returns file name without the last extension segment.
     *
     * @param file source file.
     * @return file name without extension.
     */
    public static String getBaseFilename(File file)
    {
        int index=file.getName().toLowerCase().lastIndexOf(".");
        if(index>0)
        {
            return file.getName().substring(0,index);
        }
        return file.getName();
    }

    /**
     * Converts ID3v2 tags to requested target version.
     *
     * @param tag source tag.
     * @param id3V2Version target ID3 version.
     * @return converted tag, or {@code null} when no conversion is required.
     */
    public AbstractID3v2Tag convertID3Tag(AbstractID3v2Tag tag, ID3V2Version id3V2Version)
    {
        if(tag instanceof ID3v24Tag)
        {
            switch(id3V2Version)
            {
                case ID3_V22:
                    return new ID3v22Tag((ID3v24Tag)tag);
                case ID3_V23:
                    return new ID3v23Tag((ID3v24Tag)tag);
                case ID3_V24:
                    return null;
            }
        }
        else if(tag instanceof ID3v23Tag)
        {
            switch(id3V2Version)
            {
                case ID3_V22:
                    return new ID3v22Tag((ID3v23Tag)tag);
                case ID3_V23:
                    return null;
                case ID3_V24:
                    return new ID3v24Tag((ID3v23Tag)tag);
            }
        }
        else if(tag instanceof ID3v22Tag)
        {
            switch(id3V2Version)
            {
                case ID3_V22:
                    return null;
                case ID3_V23:
                    return new ID3v23Tag((ID3v22Tag)tag);
                case ID3_V24:
                    return new ID3v24Tag((ID3v22Tag)tag);
            }
        }
        return null;
    }
}

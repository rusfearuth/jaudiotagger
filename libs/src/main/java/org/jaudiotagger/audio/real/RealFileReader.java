package org.jaudiotagger.audio.real;

import org.jaudiotagger.audio.AudioFile;
import org.jaudiotagger.audio.SupportedFileFormat;
import org.jaudiotagger.audio.exceptions.CannotReadException;
import org.jaudiotagger.audio.exceptions.InvalidAudioFrameException;
import org.jaudiotagger.audio.exceptions.NoReadPermissionsException;
import org.jaudiotagger.audio.exceptions.ReadOnlyFileException;
import org.jaudiotagger.audio.generic.AudioFileReader;
import org.jaudiotagger.audio.generic.GenericAudioHeader;
import org.jaudiotagger.audio.generic.Permissions;
import org.jaudiotagger.audio.generic.Utils;
import org.jaudiotagger.logging.ErrorMessage;
import org.jaudiotagger.tag.FieldDataInvalidException;
import org.jaudiotagger.tag.FieldKey;
import org.jaudiotagger.tag.Tag;
import org.jaudiotagger.tag.TagException;

import java.io.DataInputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Real Media File Format: Major Chunks: .RMF PROP MDPR CONT DATA INDX
 */
public class RealFileReader extends AudioFileReader
{
    private static final Logger logger = Logger.getLogger("org.jaudiotagger.audio.real");

    @Override
    public AudioFile read(Path path) throws CannotReadException, IOException, TagException, ReadOnlyFileException, InvalidAudioFrameException
    {
        File file = path.toFile();
        if (logger.isLoggable(Level.CONFIG))
        {
            logger.config(ErrorMessage.GENERAL_READ.getMsg(file.getAbsolutePath()));
        }

        if (!Files.isReadable(path))
        {
            if (!Files.exists(path))
            {
                throw new FileNotFoundException(ErrorMessage.UNABLE_TO_FIND_FILE.getMsg(path));
            }
            logger.warning(Permissions.displayPermissions(path));
            throw new NoReadPermissionsException(ErrorMessage.GENERAL_READ_FAILED_DO_NOT_HAVE_PERMISSION_TO_READ_FILE.getMsg(path));
        }

        if (file.length() <= MINIMUM_SIZE_FOR_VALID_AUDIO_FILE)
        {
            throw new CannotReadException(ErrorMessage.GENERAL_READ_FAILED_FILE_TOO_SMALL.getMsg(file.getAbsolutePath()));
        }

        try (RandomAccessFile raf = new RandomAccessFile(file, "r"))
        {
            raf.seek(0);
            GenericAudioHeader info = getEncodingInfo(raf);
            raf.seek(0);
            Tag tag = getTag(raf);
            return new AudioFile(file, info, tag);
        }
        catch (CannotReadException cre)
        {
            throw cre;
        }
        catch (Exception e)
        {
            logger.log(Level.SEVERE, ErrorMessage.GENERAL_READ.getMsg(file.getAbsolutePath()), e);
            throw new CannotReadException(file.getAbsolutePath() + ":" + e.getMessage(), e);
        }
    }

    @SuppressWarnings("unused")
	@Override
    protected GenericAudioHeader getEncodingInfo(RandomAccessFile raf) throws CannotReadException, IOException
    {
        final GenericAudioHeader info = new GenericAudioHeader();
        final RealChunk prop = findPropChunk(raf);
        final DataInputStream dis = prop.getDataInputStream();
        final int objVersion = Utils.readUint16(dis);
        if (objVersion == 0)
        {
            final long maxBitRate       = Utils.readUint32(dis) / 1000;
            final long avgBitRate       = Utils.readUint32(dis) / 1000;
            final long maxPacketSize    = Utils.readUint32(dis);
            final long avgPacketSize    = Utils.readUint32(dis);
            final long packetCnt        = Utils.readUint32(dis);
            final int duration          = (int)Utils.readUint32(dis) / 1000;
            final long preroll          = Utils.readUint32(dis);
            final long indexOffset      = Utils.readUint32(dis);
            final long dataOffset       = Utils.readUint32(dis);
            final int numStreams        = Utils.readUint16(dis);
            final int flags             = Utils.readUint16(dis);
            info.setBitRate((int) avgBitRate);
            info.setPreciseLength(duration);
            info.setVariableBitRate(maxBitRate != avgBitRate);
            info.setFormat(SupportedFileFormat.RA.getDisplayName());
        }
        return info;
    }

    private RealChunk findPropChunk(RandomAccessFile raf) throws IOException, CannotReadException
    {
    	@SuppressWarnings("unused")
		final RealChunk rmf = RealChunk.readChunk(raf);
        final RealChunk prop = RealChunk.readChunk(raf);
        return prop;
    }

    private RealChunk findContChunk(RandomAccessFile raf) throws IOException, CannotReadException
    {
    	@SuppressWarnings("unused")
		final RealChunk rmf = RealChunk.readChunk(raf);
    	@SuppressWarnings("unused")
		final RealChunk prop = RealChunk.readChunk(raf);
        RealChunk rv = RealChunk.readChunk(raf);
        while (!rv.isCONT()) rv = RealChunk.readChunk(raf);
        return rv;
    }

    @Override
    protected Tag getTag(RandomAccessFile raf) throws CannotReadException, IOException
    {
        final RealChunk cont = findContChunk(raf);
        final DataInputStream dis = cont.getDataInputStream();
        final String title = Utils.readString(dis, Utils.readUint16(dis));
        final String author = Utils.readString(dis, Utils.readUint16(dis));
        final String copyright = Utils.readString(dis, Utils.readUint16(dis));
        final String comment = Utils.readString(dis, Utils.readUint16(dis));
        final RealTag rv = new RealTag();
        // NOTE: frequently these fields are off-by-one, thus the crazy
        // logic below...
        try
        {
            rv.addField(FieldKey.TITLE,(title.length() == 0 ? author : title));
            rv.addField(FieldKey.ARTIST, title.length() == 0 ? copyright : author);
            rv.addField(FieldKey.COMMENT,comment);
        }
        catch(FieldDataInvalidException fdie)
        {
            throw new RuntimeException(fdie);
        }
        return rv;
    }

}

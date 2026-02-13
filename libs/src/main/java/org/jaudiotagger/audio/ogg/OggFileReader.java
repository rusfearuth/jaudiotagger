/*
 * Entagged Audio Tag library
 * Copyright (c) 2003-2005 Raphaël Slinckx <raphael@slinckx.net>
 * 
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *  
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA
 */
package org.jaudiotagger.audio.ogg;

import org.jaudiotagger.audio.AudioFile;
import org.jaudiotagger.audio.exceptions.CannotReadException;
import org.jaudiotagger.audio.exceptions.InvalidAudioFrameException;
import org.jaudiotagger.audio.exceptions.NoReadPermissionsException;
import org.jaudiotagger.audio.exceptions.ReadOnlyFileException;
import org.jaudiotagger.audio.generic.AudioFileReader;
import org.jaudiotagger.audio.generic.GenericAudioHeader;
import org.jaudiotagger.audio.generic.Permissions;
import org.jaudiotagger.audio.ogg.util.OggInfoReader;
import org.jaudiotagger.audio.ogg.util.OggPageHeader;
import org.jaudiotagger.logging.ErrorMessage;
import org.jaudiotagger.tag.Tag;
import org.jaudiotagger.tag.TagException;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Read Ogg File Tag and Encoding information
 *
 * Only implemented for ogg files containing a vorbis stream with vorbis comments
 */
public class OggFileReader extends AudioFileReader
{
    // Logger Object
    public static Logger logger = Logger.getLogger("org.jaudiotagger.audio.ogg");

    private OggInfoReader ir;
    private OggVorbisTagReader vtr;

    public OggFileReader()
    {
        ir = new OggInfoReader();
        vtr = new OggVorbisTagReader();
    }

    protected GenericAudioHeader getEncodingInfo(RandomAccessFile raf) throws CannotReadException, IOException
    {
        return ir.read(raf);
    }

    protected Tag getTag(RandomAccessFile raf) throws CannotReadException, IOException
    {
        return vtr.read(raf);
    }

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
            Tag tag = vtr.read(raf, path);
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

    /**
     * Return count Ogg Page header, count starts from zero
     *
     * count=0; should return PageHeader that contains Vorbis Identification Header
     * count=1; should return Pageheader that contains VorbisComment and possibly SetupHeader
     * count>=2; should return PageHeader containing remaining VorbisComment,SetupHeader and/or Audio
     *
     * @param raf
     * @param count
     * @return
     * @throws CannotReadException
     * @throws IOException
     */
    public OggPageHeader readOggPageHeader(RandomAccessFile raf, int count) throws CannotReadException, IOException
    {
        OggPageHeader pageHeader = OggPageHeader.read(raf);
        while (count > 0)
        {
            raf.seek(raf.getFilePointer() + pageHeader.getPageLength());
            pageHeader = OggPageHeader.read(raf);
            count--;
        }
        return pageHeader;
    }

    /**
     * Summarize all the ogg headers in a file
     *
     * A useful utility function
     *
     * @param oggFile
     * @throws CannotReadException
     * @throws IOException
     */
    public void summarizeOggPageHeaders(File oggFile) throws CannotReadException, IOException
    {
        RandomAccessFile raf = new RandomAccessFile(oggFile, "r");

        while (raf.getFilePointer() < raf.length())
        {
            System.out.println("pageHeader starts at absolute file position:" + raf.getFilePointer());
            OggPageHeader pageHeader = OggPageHeader.read(raf);
            System.out.println("pageHeader finishes at absolute file position:" + raf.getFilePointer());
            System.out.println(pageHeader + "\n");
            raf.seek(raf.getFilePointer() + pageHeader.getPageLength());
        }
        System.out.println("Raf File Pointer at:" + raf.getFilePointer() + "File Size is:" + raf.length());
        raf.close();
    }

    /**
     * Summarizes the first five pages, normally all we are interested in
     *
     * @param oggFile
     * @throws CannotReadException
     * @throws IOException
     */
    public void shortSummarizeOggPageHeaders(File oggFile) throws CannotReadException, IOException
    {
        RandomAccessFile raf = new RandomAccessFile(oggFile, "r");

        int i = 0;
        while (raf.getFilePointer() < raf.length())
        {
            System.out.println("pageHeader starts at absolute file position:" + raf.getFilePointer());
            OggPageHeader pageHeader = OggPageHeader.read(raf);
            System.out.println("pageHeader finishes at absolute file position:" + raf.getFilePointer());
            System.out.println(pageHeader + "\n");
            raf.seek(raf.getFilePointer() + pageHeader.getPageLength());
            i++;
            if(i>=5)
            {
                break;
            }
        }
        System.out.println("Raf File Pointer at:" + raf.getFilePointer() + "File Size is:" + raf.length());
        raf.close();
    }
}

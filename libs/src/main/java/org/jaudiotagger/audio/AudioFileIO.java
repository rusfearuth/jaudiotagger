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
package org.jaudiotagger.audio;

import android.os.ParcelFileDescriptor;
import org.jaudiotagger.audio.aiff.AiffFileReader;
import org.jaudiotagger.audio.aiff.AiffFileWriter;
import org.jaudiotagger.audio.asf.AsfFileReader;
import org.jaudiotagger.audio.asf.AsfFileWriter;
import org.jaudiotagger.audio.dff.DffFileReader;
import org.jaudiotagger.audio.dsf.DsfFileReader;
import org.jaudiotagger.audio.dsf.DsfFileWriter;
import org.jaudiotagger.audio.exceptions.*;
import org.jaudiotagger.audio.flac.FlacFileReader;
import org.jaudiotagger.audio.flac.FlacFileWriter;
import org.jaudiotagger.audio.generic.*;
import org.jaudiotagger.audio.mp3.MP3FileReader;
import org.jaudiotagger.audio.mp3.MP3FileWriter;
import org.jaudiotagger.audio.mp4.Mp4FileReader;
import org.jaudiotagger.audio.mp4.Mp4FileWriter;
import org.jaudiotagger.audio.ogg.OggFileReader;
import org.jaudiotagger.audio.ogg.OggFileWriter;
import org.jaudiotagger.audio.real.RealFileReader;
import org.jaudiotagger.audio.wav.WavFileReader;
import org.jaudiotagger.audio.wav.WavFileWriter;
import org.jaudiotagger.logging.ErrorMessage;
import org.jaudiotagger.tag.TagException;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

/**
 *
 * The main entry point for the Tag Reading/Writing operations, this class will
 * select the appropriate reader/writer for the given file.
 *
 *
 * It selects the appropriate reader/writer based on the file extension (case
 * ignored).
 *
 *
 * Here is an simple example of use:
 *
 *
 * <code>
 * AudioFile audioFile = AudioFileIO.read(Paths.get("audiofile.mp3")); //Reads the given file.
 * int bitrate = audioFile.getBitrate(); //Retreives the bitrate of the file.
 * String artist = audioFile.getTag().getFirst(TagFieldKey.ARTIST); //Retreive the artist name.
 * audioFile.getTag().setGenre("Progressive Rock"); //Sets the genre to Prog. Rock, note the file on disk is still unmodified.
 * audioFile.commit(); //Write the modifications in the file on disk.
 * </code>
 *
 *
 * You can also use the <code>commit()</code> method defined for
 * <code>AudioFile</code>s to achieve the same goal.
 *
 *
 * <code>
 * AudioFile audioFile = AudioFileIO.read(Paths.get("audiofile.mp3"));
 * audioFile.getTag().setGenre("Progressive Rock");
 * audioFile.commit(); //Write the modifications in the file on disk.
 * </code>
 *
 *
 * @author Raphael Slinckx
 * @version $Id$
 * @see AudioFile
 * @see org.jaudiotagger.tag.Tag
 * @since v0.01
 */
public class AudioFileIO
{

    //Logger
    public static Logger logger = Logger.getLogger("org.jaudiotagger.audio");

    // !! Do not forget to also add new supported extensions to AudioFileFilter
    // !!

    /**
     * This field contains the default instance for static use.
     */
    private static AudioFileIO defaultInstance;

    /**
     * Android-first delete entry point.
     */
    public static void delete(AudioFile audioFile, ParcelFileDescriptor pfd) throws CannotReadException, CannotWriteException
    {
        getDefaultAudioFileIO().deleteTag(audioFile, pfd);
    }

    /**
     * This method returns the default instance for static use.<br>
     *
     * @return The default instance.
     */
    public static AudioFileIO getDefaultAudioFileIO()
    {
        if (defaultInstance == null)
        {
            defaultInstance = new AudioFileIO();
        }
        return defaultInstance;
    }

    /**
     * Read the tag contained in the given path.
     */
    public static AudioFile readAs(Path path, String ext)
            throws CannotReadException, IOException, TagException, ReadOnlyFileException, InvalidAudioFrameException
    {
        return getDefaultAudioFileIO().readFileAs(path, ext);
    }

    /**
     * Android-first read entry point.
     */
    public static AudioFile readAs(ParcelFileDescriptor pfd, String ext)
            throws CannotReadException, IOException, TagException, ReadOnlyFileException, InvalidAudioFrameException
    {
        return getDefaultAudioFileIO().readFileAs(pfd, ext);
    }

    /**
     * Read the tag using content-based type detection from a path.
     */
    public static AudioFile readMagic(Path path)
            throws CannotReadException, IOException, TagException, ReadOnlyFileException, InvalidAudioFrameException
    {
        return getDefaultAudioFileIO().readFileMagic(path);
    }

    /**
     * Read the tag contained in the given path.
     */
    public static AudioFile read(Path path)
            throws CannotReadException, IOException, TagException, ReadOnlyFileException, InvalidAudioFrameException
    {
        return getDefaultAudioFileIO().readFile(path);
    }

    /**
     * Android-first read entry point. The hint can be either plain extension ("mp3")
     * or a display name ("track01.mp3"), extension is required.
     */
    public static AudioFile read(ParcelFileDescriptor pfd, String displayNameOrExtHint)
            throws CannotReadException, IOException, TagException, ReadOnlyFileException, InvalidAudioFrameException
    {
        final String ext = extractExtensionHint(displayNameOrExtHint);
        return readAs(pfd, ext);
    }

    /**
     * Android-first write entry point.
     */
    public static void write(AudioFile audioFile, ParcelFileDescriptor pfd) throws CannotWriteException
    {
        getDefaultAudioFileIO().writeFile(audioFile, pfd);
    }

    /**
     * Write audio metadata to the provided target path without extension.
     */
    public static void writeAs(AudioFile f, Path targetPath) throws CannotWriteException
    {
        if (targetPath == null)
        {
            throw new CannotWriteException("Not a valid target path: null");
        }
        getDefaultAudioFileIO().writeFile(f, targetPath);
    }

    /**
     * This member is used to broadcast modification events to registered
     */
    private final ModificationHandler modificationHandler;

    // These tables contains all the readers/writers associated with extension
    // as a key
    private Map<String, AudioFileReader> readers = new HashMap<String, AudioFileReader>();
    private Map<String, AudioFileWriter> writers = new HashMap<String, AudioFileWriter>();


    /**
     * Creates an instance.
     */
    public AudioFileIO()
    {
        this.modificationHandler = new ModificationHandler();
        prepareReadersAndWriters();
    }

    private static String extractExtensionHint(String displayNameOrExtHint) throws CannotReadException
    {
        if (displayNameOrExtHint == null || displayNameOrExtHint.trim().isEmpty())
        {
            throw new CannotReadException("displayNameOrExtHint is required");
        }

        String hint = displayNameOrExtHint.trim().toLowerCase();
        int lastDot = hint.lastIndexOf('.');
        if (lastDot >= 0 && lastDot < hint.length() - 1)
        {
            hint = hint.substring(lastDot + 1);
        }
        if (hint.startsWith("."))
        {
            hint = hint.substring(1);
        }
        if (hint.isEmpty())
        {
            throw new CannotReadException("Unable to determine file extension from hint: " + displayNameOrExtHint);
        }
        return hint;
    }

    private static Path resolvePathFromDescriptor(ParcelFileDescriptor pfd) throws IOException
    {
        if (pfd == null)
        {
            throw new IOException("ParcelFileDescriptor cannot be null");
        }
        Path descriptorPath = Paths.get("/proc/self/fd/" + pfd.getFd());
        return descriptorPath.toRealPath();
    }

    /**
     * Adds an listener for all file formats.
     *
     * @param listener listener
     */
    public void addAudioFileModificationListener(
            AudioFileModificationListener listener)
    {
        this.modificationHandler.addAudioFileModificationListener(listener);
    }

    /**
     *
     * Delete the tag, if any, contained in the given file.
     *
     *
     * @param f The file where the tag will be deleted
     * @throws org.jaudiotagger.audio.exceptions.CannotWriteException If the file could not be written/accessed, the extension
     *                              wasn't recognized, or other IO error occurred.
     * @throws org.jaudiotagger.audio.exceptions.CannotReadException
     */
    public void deleteTag(AudioFile f) throws CannotReadException, CannotWriteException
    {
        String ext = Utils.getExtension(f.getFile().toPath());

        Object afw = writers.get(ext);
        if (afw == null)
        {
            throw new CannotWriteException(ErrorMessage.NO_DELETER_FOR_THIS_FORMAT.getMsg(ext));
        }

        ((AudioFileWriter) afw).delete(f);
    }

    /**
     * Creates the readers and writers.
     */
    private void prepareReadersAndWriters()
    {

        // Tag Readers
        readers.put(SupportedFileFormat.OGG.getFilesuffix(), new OggFileReader());
        readers.put(SupportedFileFormat.OGA.getFilesuffix(), new OggFileReader());
        readers.put(SupportedFileFormat.FLAC.getFilesuffix(),new FlacFileReader());
        readers.put(SupportedFileFormat.MP3.getFilesuffix(), new MP3FileReader());
        readers.put(SupportedFileFormat.MP4.getFilesuffix(), new Mp4FileReader());
        readers.put(SupportedFileFormat.M4A.getFilesuffix(), new Mp4FileReader());
        readers.put(SupportedFileFormat.M4P.getFilesuffix(), new Mp4FileReader());
        readers.put(SupportedFileFormat.M4B.getFilesuffix(), new Mp4FileReader());
        readers.put(SupportedFileFormat.WAV.getFilesuffix(), new WavFileReader());
        readers.put(SupportedFileFormat.WMA.getFilesuffix(), new AsfFileReader());
        readers.put(SupportedFileFormat.AIF.getFilesuffix(), new AiffFileReader());
        readers.put(SupportedFileFormat.AIFC.getFilesuffix(), new AiffFileReader());
        readers.put(SupportedFileFormat.AIFF.getFilesuffix(), new AiffFileReader());
        readers.put(SupportedFileFormat.DSF.getFilesuffix(), new DsfFileReader());
        readers.put(SupportedFileFormat.DFF.getFilesuffix(), new DffFileReader());
        final RealFileReader realReader = new RealFileReader();
        readers.put(SupportedFileFormat.RA.getFilesuffix(), realReader);
        readers.put(SupportedFileFormat.RM.getFilesuffix(), realReader);

        // Tag Writers
        writers.put(SupportedFileFormat.OGG.getFilesuffix(), new OggFileWriter());
        writers.put(SupportedFileFormat.OGA.getFilesuffix(), new OggFileWriter());
        writers.put(SupportedFileFormat.FLAC.getFilesuffix(), new FlacFileWriter());
        writers.put(SupportedFileFormat.MP3.getFilesuffix(), new MP3FileWriter());
        writers.put(SupportedFileFormat.MP4.getFilesuffix(), new Mp4FileWriter());
        writers.put(SupportedFileFormat.M4A.getFilesuffix(), new Mp4FileWriter());
        writers.put(SupportedFileFormat.M4P.getFilesuffix(), new Mp4FileWriter());
        writers.put(SupportedFileFormat.M4B.getFilesuffix(), new Mp4FileWriter());
        writers.put(SupportedFileFormat.WAV.getFilesuffix(), new WavFileWriter());
        writers.put(SupportedFileFormat.WMA.getFilesuffix(), new AsfFileWriter());
        writers.put(SupportedFileFormat.AIF.getFilesuffix(), new AiffFileWriter());
        writers.put(SupportedFileFormat.AIFC.getFilesuffix(), new AiffFileWriter());
        writers.put(SupportedFileFormat.AIFF.getFilesuffix(), new AiffFileWriter());
        writers.put(SupportedFileFormat.DSF.getFilesuffix(), new DsfFileWriter());

        for (AudioFileWriter curr : writers.values())
        {
            curr.setAudioFileModificationListener(this.modificationHandler);
        }
    }

    public AudioFile readFile(Path path)
            throws CannotReadException, IOException, TagException, ReadOnlyFileException, InvalidAudioFrameException
    {
        String ext = Utils.getExtension(path);

        AudioFileReader afr = readers.get(ext);
        if (afr == null)
        {
            throw new CannotReadException(ErrorMessage.NO_READER_FOR_THIS_FORMAT.getMsg(ext));
        }
        AudioFile tempFile = afr.read(path);
        tempFile.setExt(ext);
        return tempFile;
    }

    public AudioFile readFileAs(ParcelFileDescriptor pfd, String ext)
            throws CannotReadException, IOException, TagException, ReadOnlyFileException, InvalidAudioFrameException
    {
        final String normalizedExt = extractExtensionHint(ext);
        final Path path;
        try
        {
            path = resolvePathFromDescriptor(pfd);
        }
        catch (IOException e)
        {
            throw new CannotReadException("Unable to resolve ParcelFileDescriptor path: " + e.getMessage(), e);
        }
        return readFileAs(path, normalizedExt);
    }

    public AudioFile readFileMagic(Path path)
            throws CannotReadException, IOException, TagException, ReadOnlyFileException, InvalidAudioFrameException
    {
        String ext = Utils.getMagicExtension(path);

        AudioFileReader afr = readers.get(ext);
        if (afr == null)
        {
            throw new CannotReadException(ErrorMessage.NO_READER_FOR_THIS_FORMAT.getMsg(ext));
        }

        AudioFile tempFile = afr.read(path);
        tempFile.setExt(ext);
        return tempFile;

    }

    public AudioFile readFileAs(Path path, String ext)
            throws CannotReadException, IOException, TagException, ReadOnlyFileException, InvalidAudioFrameException
    {
        final String normalizedExt = extractExtensionHint(ext);

        AudioFileReader afr = readers.get(normalizedExt);
        if (afr == null)
        {
            throw new CannotReadException(ErrorMessage.NO_READER_FOR_THIS_FORMAT.getMsg(normalizedExt));
        }

        AudioFile tempFile = afr.read(path);
        tempFile.setExt(normalizedExt);
        return tempFile;
    }

    public void writeFile(AudioFile f, ParcelFileDescriptor pfd) throws CannotWriteException
    {
        final Path path;
        try
        {
            path = resolvePathFromDescriptor(pfd);
        }
        catch (IOException e)
        {
            throw new CannotWriteException("Unable to resolve ParcelFileDescriptor path: " + e.getMessage(), e);
        }

        f.setFile(path.toFile());
        if (f.getExt() == null || f.getExt().isEmpty())
        {
            f.setExt(Utils.getExtension(path));
        }
        writeFile(f, (Path) null);
    }

    public void deleteTag(AudioFile f, ParcelFileDescriptor pfd) throws CannotReadException, CannotWriteException
    {
        final Path path;
        try
        {
            path = resolvePathFromDescriptor(pfd);
        }
        catch (IOException e)
        {
            throw new CannotWriteException("Unable to resolve ParcelFileDescriptor path: " + e.getMessage(), e);
        }

        f.setFile(path.toFile());
        deleteTag(f);
    }

    /**
     * Removes a listener for all file formats.
     *
     * @param listener listener
     */
    public void removeAudioFileModificationListener(
            AudioFileModificationListener listener)
    {
        this.modificationHandler.removeAudioFileModificationListener(listener);
    }

    public void writeFile(AudioFile f, Path targetPath) throws CannotWriteException
    {
        if (f.getFile() == null)
        {
            throw new CannotWriteException("AudioFile file reference is null");
        }

        String ext = f.getExt();
        if (ext == null || ext.isEmpty())
        {
            ext = Utils.getExtension(f.getFile().toPath());
            f.setExt(ext);
        }

        if (targetPath != null)
        {
            final File destination = new File(targetPath.toString() + "." + ext);
            try
            {
                Utils.copyThrowsOnException(f.getFile(), destination);
                f.setFile(destination);
            }
            catch (IOException e)
            {
                throw new CannotWriteException("Error While Copying" + e.getMessage());
            }
        }

        AudioFileWriter afw = writers.get(ext);
        if (afw == null)
        {
            throw new CannotWriteException(ErrorMessage.NO_WRITER_FOR_THIS_FORMAT.getMsg(ext));
        }
        afw.write(f);
    }

}

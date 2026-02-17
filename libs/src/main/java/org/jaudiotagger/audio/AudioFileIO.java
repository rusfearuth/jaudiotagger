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

import android.content.Context;
import android.net.Uri;
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
import org.jaudiotagger.audio.io.UriIO;
import org.jaudiotagger.logging.ErrorMessage;
import org.jaudiotagger.tag.TagException;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

/**
 * Main entry point for reading and writing metadata tags in supported audio formats.
 *
 * <p>Reader and writer implementations are selected by normalized extension. File-system operations use
 * {@link Path}; Android content operations use {@link Context} + {@link Uri}.</p>
 *
 * <p>Typical file-based flow:</p>
 * <pre>
 * AudioFile audioFile = AudioFileIO.read(Paths.get("audiofile.mp3"));
 * audioFile.getTagOrCreateAndSetDefault().setField(FieldKey.GENRE, "Progressive Rock");
 * audioFile.commit();
 * </pre>
 *
 * <p>Typical Android Uri flow:</p>
 * <pre>
 * AudioFile audioFile = AudioFileIO.readAs(context, uri, "mp3");
 * AudioFileIO.write(context, audioFile, uri);
 * </pre>
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
     * Deletes tags through an Android {@link Uri} entry point.
     *
     * @param context Android context used to resolve the {@link Uri}.
     * @param audioFile mutable audio file model to delete tags from.
     * @param uri source and destination content Uri.
     * @throws CannotReadException if a required read operation fails.
     * @throws CannotWriteException if delete cannot be applied or persisted.
     */
    public static void delete(Context context, AudioFile audioFile, Uri uri) throws CannotReadException, CannotWriteException
    {
        getDefaultAudioFileIO().deleteTag(context, audioFile, uri);
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
     * Reads metadata from a path while forcing a specific format extension.
     *
     * @param path audio file path.
     * @param ext extension hint such as {@code "mp3"} or {@code ".flac"}.
     * @return parsed audio file model.
     */
    public static AudioFile readAs(Path path, String ext)
            throws CannotReadException, IOException, TagException, ReadOnlyFileException, InvalidAudioFrameException
    {
        return getDefaultAudioFileIO().readFileAs(path, ext);
    }

    /**
     * Reads metadata from Android content using an explicit extension hint.
     *
     * @param context Android context used to resolve the {@link Uri}.
     * @param uri source content Uri.
     * @param ext extension hint such as {@code "mp3"} or {@code ".flac"}.
     * @return parsed audio file model.
     */
    public static AudioFile readAs(Context context, Uri uri, String ext)
            throws CannotReadException, IOException, TagException, ReadOnlyFileException, InvalidAudioFrameException
    {
        return getDefaultAudioFileIO().readFileAs(context, uri, ext);
    }

    /**
     * Reads metadata from a path using content-based type detection.
     *
     * @param path audio file path.
     * @return parsed audio file model.
     */
    public static AudioFile readMagic(Path path)
            throws CannotReadException, IOException, TagException, ReadOnlyFileException, InvalidAudioFrameException
    {
        return getDefaultAudioFileIO().readFileMagic(path);
    }

    /**
     * Reads metadata from a path, choosing parser by file extension.
     *
     * @param path audio file path.
     * @return parsed audio file model.
     */
    public static AudioFile read(Path path)
            throws CannotReadException, IOException, TagException, ReadOnlyFileException, InvalidAudioFrameException
    {
        return getDefaultAudioFileIO().readFile(path);
    }

    /**
     * Android read entry point that accepts either an extension or a display name.
     *
     * <p>The hint can be plain extension ({@code "mp3"}) or display name ({@code "track01.mp3"}).</p>
     *
     * @param context Android context used to resolve the {@link Uri}.
     * @param uri source content Uri.
     * @param displayNameOrExtHint extension-like hint used for parser selection.
     * @return parsed audio file model.
     * @throws CannotReadException when no extension can be extracted from the hint.
     */
    public static AudioFile read(Context context, Uri uri, String displayNameOrExtHint)
            throws CannotReadException, IOException, TagException, ReadOnlyFileException, InvalidAudioFrameException
    {
        final String ext = extractExtensionHint(displayNameOrExtHint);
        return readAs(context, uri, ext);
    }

    /**
     * Persists tag updates through an Android {@link Uri} entry point.
     *
     * @param context Android context used to resolve the {@link Uri}.
     * @param audioFile mutable audio file model to write.
     * @param uri source and destination content Uri.
     * @throws CannotWriteException if write cannot be applied or persisted.
     */
    public static void write(Context context, AudioFile audioFile, Uri uri) throws CannotWriteException
    {
        getDefaultAudioFileIO().writeFile(context, audioFile, uri);
    }

    /**
     * Writes tags to a copy at {@code targetPath + "." + ext}.
     *
     * <p>The target path must not contain an extension; the source file extension from {@link AudioFile#getExt()}
     * (or inferred from source file name) is appended automatically.</p>
     *
     * @param f audio file model to write.
     * @param targetPath target path prefix without extension.
     * @throws CannotWriteException if target path is invalid or write fails.
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
     * Broadcasts modification events to registered listeners.
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

    private static String inferUriExtension(Uri uri)
    {
        if (uri == null)
        {
            return "";
        }
        String segment = uri.getLastPathSegment();
        if (segment == null || segment.isEmpty())
        {
            return "";
        }
        int slash = segment.lastIndexOf('/');
        if (slash >= 0 && slash < segment.length() - 1)
        {
            segment = segment.substring(slash + 1);
        }
        int lastDot = segment.lastIndexOf('.');
        if (lastDot < 0 || lastDot == segment.length() - 1)
        {
            return "";
        }
        return segment.substring(lastDot + 1).toLowerCase();
    }

    /**
     * Adds a listener for file modification events across all formats.
     *
     * @param listener listener instance.
     */
    public void addAudioFileModificationListener(
            AudioFileModificationListener listener)
    {
        this.modificationHandler.addAudioFileModificationListener(listener);
    }

    /**
     * Deletes tags from a file-based audio model.
     *
     * @param f audio file to delete tags from.
     * @throws CannotWriteException if no writer/deleter exists for the extension or write fails.
     * @throws CannotReadException if a read prerequisite fails during deletion.
     */
    public void deleteTag(AudioFile f) throws CannotReadException, CannotWriteException
    {
        final Path audioPath = f.getPath();
        if (audioPath == null)
        {
            throw new CannotWriteException("AudioFile path reference is null");
        }
        String ext = Utils.getExtension(audioPath);

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

    /**
     * Reads metadata using extension from the path.
     *
     * @param path audio file path.
     * @return parsed audio file.
     */
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

    /**
     * Reads metadata from Android content Uri using explicit extension hint.
     *
     * @param context Android context used to resolve the {@link Uri}.
     * @param uri source content Uri.
     * @param ext extension hint.
     * @return parsed audio file.
     */
    public AudioFile readFileAs(Context context, Uri uri, String ext)
            throws CannotReadException, IOException, TagException, ReadOnlyFileException, InvalidAudioFrameException
    {
        final String normalizedExt = extractExtensionHint(ext);
        final Path tempPath;
        try
        {
            tempPath = UriIO.copyUriToTempFile(context, uri);
        }
        catch (IOException e)
        {
            throw new CannotReadException("Unable to open uri for read: " + e.getMessage(), e);
        }
        return readFileAs(tempPath, normalizedExt);
    }

    /**
     * Reads metadata using content-based extension detection.
     *
     * @param path audio file path.
     * @return parsed audio file.
     */
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

    /**
     * Reads metadata while forcing a specific extension.
     *
     * @param path audio file path.
     * @param ext extension hint.
     * @return parsed audio file.
     */
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

    /**
     * Writes tag updates via Android Uri by editing a temporary local copy and syncing back.
     *
     * @param context Android context used to resolve the {@link Uri}.
     * @param f mutable audio file model to persist.
     * @param uri source and destination content Uri.
     * @throws CannotWriteException if read/write to Uri or tag persistence fails.
     */
    public void writeFile(Context context, AudioFile f, Uri uri) throws CannotWriteException
    {
        if (f == null)
        {
            throw new CannotWriteException("AudioFile is null");
        }

        final Path tempPath;
        final Path originalPath = f.getPath();
        final String originalExt = f.getExt();
        try
        {
            tempPath = UriIO.copyUriToTempFile(context, uri);
        }
        catch (IOException e)
        {
            throw new CannotWriteException("Unable to open uri for write: " + e.getMessage(), e);
        }

        try
        {
            f.setPath(tempPath);

            String ext = f.getExt();
            if (ext == null || ext.isEmpty())
            {
                ext = originalExt;
                if (ext == null || ext.isEmpty())
                {
                    ext = originalPath != null ? Utils.getExtension(originalPath) : "";
                }
                if (ext == null || ext.isEmpty())
                {
                    ext = inferUriExtension(uri);
                }
                if (ext == null || ext.isEmpty())
                {
                    throw new CannotWriteException("Unable to determine extension for uri write");
                }
                f.setExt(ext);
            }

            writeFile(f, (Path) null);
            UriIO.copyTempFileToUri(context, tempPath, uri);
        }
        catch (IOException e)
        {
            throw new CannotWriteException("Unable to persist changes to uri: " + e.getMessage(), e);
        }
        finally
        {
            f.setPath(originalPath);
            f.setExt(originalExt);
            UriIO.deleteQuietly(tempPath);
        }
    }

    /**
     * Deletes tags via Android Uri by editing a temporary local copy and syncing back.
     *
     * @param context Android context used to resolve the {@link Uri}.
     * @param f mutable audio file model to delete tags from.
     * @param uri source and destination content Uri.
     * @throws CannotReadException if a required read operation fails.
     * @throws CannotWriteException if delete cannot be applied or persisted.
     */
    public void deleteTag(Context context, AudioFile f, Uri uri) throws CannotReadException, CannotWriteException
    {
        if (f == null)
        {
            throw new CannotWriteException("AudioFile is null");
        }

        final Path tempPath;
        final Path originalPath = f.getPath();
        final String originalExt = f.getExt();
        try
        {
            tempPath = UriIO.copyUriToTempFile(context, uri);
        }
        catch (IOException e)
        {
            throw new CannotWriteException("Unable to open uri for delete: " + e.getMessage(), e);
        }

        try
        {
            f.setPath(tempPath);
            if (f.getExt() == null || f.getExt().isEmpty())
            {
                String ext = originalExt;
                if (ext == null || ext.isEmpty())
                {
                    ext = originalPath != null ? Utils.getExtension(originalPath) : "";
                }
                if (ext == null || ext.isEmpty())
                {
                    ext = inferUriExtension(uri);
                }
                if (ext == null || ext.isEmpty())
                {
                    throw new CannotWriteException("Unable to determine extension for uri delete");
                }
                f.setExt(ext);
            }

            deleteTag(f);
            UriIO.copyTempFileToUri(context, tempPath, uri);
        }
        catch (IOException e)
        {
            throw new CannotWriteException("Unable to persist delete to uri: " + e.getMessage(), e);
        }
        finally
        {
            f.setPath(originalPath);
            f.setExt(originalExt);
            UriIO.deleteQuietly(tempPath);
        }
    }

    /**
     * Removes a previously registered file modification listener.
     *
     * @param listener listener instance.
     */
    public void removeAudioFileModificationListener(
            AudioFileModificationListener listener)
    {
        this.modificationHandler.removeAudioFileModificationListener(listener);
    }

    /**
     * Writes tags either in-place or to a copied target path prefix.
     *
     * <p>If {@code targetPath} is non-null, this method first copies source file to
     * {@code targetPath + "." + ext} and writes there.</p>
     *
     * @param f audio file model to persist.
     * @param targetPath target path prefix without extension, or {@code null} for in-place write.
     * @throws CannotWriteException if extension cannot be resolved, no writer exists, or write fails.
     */
    public void writeFile(AudioFile f, Path targetPath) throws CannotWriteException
    {
        final Path currentPath = f.getPath();
        if (currentPath == null)
        {
            throw new CannotWriteException("AudioFile path reference is null");
        }

        String ext = f.getExt();
        if (ext == null || ext.isEmpty())
        {
            ext = Utils.getExtension(currentPath);
            f.setExt(ext);
        }

        if (targetPath != null)
        {
            final File destination = new File(targetPath.toString() + "." + ext);
            try
            {
                Utils.copyThrowsOnException(f.getPath().toFile(), destination);
                f.setPath(destination.toPath());
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

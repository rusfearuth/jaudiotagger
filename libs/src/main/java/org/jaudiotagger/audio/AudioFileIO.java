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
import java.io.FileNotFoundException;
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
 * AudioFile audioFile = AudioFileIO.read(new File("audiofile.mp3")); //Reads the given file.
 * int bitrate = audioFile.getBitrate(); //Retreives the bitrate of the file.
 * String artist = audioFile.getTag().getFirst(TagFieldKey.ARTIST); //Retreive the artist name.
 * audioFile.getTag().setGenre("Progressive Rock"); //Sets the genre to Prog. Rock, note the file on disk is still unmodified.
 * AudioFileIO.write(audioFile); //Write the modifications in the file on disk.
 * </code>
 * 
 *
 * You can also use the <code>commit()</code> method defined for
 * <code>AudioFile</code>s to achieve the same goal as
 * <code>AudioFileIO.write(File)</code>, like this:
 * 
 *
 * <code>
 * AudioFile audioFile = AudioFileIO.read(new File("audiofile.mp3"));
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
     *
     * Delete the tag, if any, contained in the given file.
     * 
     *
     * @param f The file where the tag will be deleted
     * @throws org.jaudiotagger.audio.exceptions.CannotWriteException If the file could not be written/accessed, the extension
     *                              wasn't recognized, or other IO error occurred.
     * @throws org.jaudiotagger.audio.exceptions.CannotReadException
     * @deprecated Use {@link #delete(AudioFile, ParcelFileDescriptor)} instead.
     */
    @Deprecated
    public static void delete(AudioFile f) throws CannotReadException, CannotWriteException
    {
        getDefaultAudioFileIO().deleteTag(f);
    }

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
     *
     * Read the tag contained in the given file.
     * 
     *
     * @param f The file to read.
     * @param ext The extension to be used.
     * @return The AudioFile with the file tag and the file encoding info.
     * @throws org.jaudiotagger.audio.exceptions.CannotReadException If the file could not be read, the extension wasn't
     *                             recognized, or an IO error occurred during the read.
     * @throws org.jaudiotagger.tag.TagException
     * @throws org.jaudiotagger.audio.exceptions.ReadOnlyFileException
     * @throws java.io.IOException
     * @throws org.jaudiotagger.audio.exceptions.InvalidAudioFrameException
     * @deprecated Use {@link #readAs(Path, String)} instead.
     */
    @Deprecated
    public static AudioFile readAs(File f,String ext)
            throws CannotReadException, IOException, TagException, ReadOnlyFileException, InvalidAudioFrameException
    {
        return getDefaultAudioFileIO().readFileAs(f.toPath(), ext);
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
    *
    * Read the tag contained in the given file.
    * 
    *
    * @param f The file to read.
    * @return The AudioFile with the file tag and the file encoding info.
    * @throws org.jaudiotagger.audio.exceptions.CannotReadException If the file could not be read, the extension wasn't
    *                             recognized, or an IO error occurred during the read.
    * @throws org.jaudiotagger.tag.TagException
    * @throws org.jaudiotagger.audio.exceptions.ReadOnlyFileException
    * @throws java.io.IOException
    * @throws org.jaudiotagger.audio.exceptions.InvalidAudioFrameException
    * @deprecated Use {@link #readMagic(Path)} instead.
    */
    @Deprecated
   public static AudioFile readMagic(File f)
           throws CannotReadException, IOException, TagException, ReadOnlyFileException, InvalidAudioFrameException
   {
       return getDefaultAudioFileIO().readFileMagic(f.toPath());
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
   *
   * Read the tag contained in the given file.
   * 
   *
   * @param f The file to read.
   * @return The AudioFile with the file tag and the file encoding info.
   * @throws org.jaudiotagger.audio.exceptions.CannotReadException If the file could not be read, the extension wasn't
   *                             recognized, or an IO error occurred during the read.
   * @throws org.jaudiotagger.tag.TagException
   * @throws org.jaudiotagger.audio.exceptions.ReadOnlyFileException
   * @throws java.io.IOException
   * @throws org.jaudiotagger.audio.exceptions.InvalidAudioFrameException
   * @deprecated Use {@link #read(Path)} instead.
   */
    @Deprecated
    public static AudioFile read(File f)
          throws CannotReadException, IOException, TagException, ReadOnlyFileException, InvalidAudioFrameException
  {
      return getDefaultAudioFileIO().readFile(f.toPath());
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
     *
     * Write the tag contained in the audioFile in the actual file on the disk.
     * 
     *
     * @param f The AudioFile to be written
     * @throws NoWritePermissionsException if the file could not be written to due to file permissions
     * @throws CannotWriteException If the file could not be written/accessed, the extension
     *                              wasn't recognized, or other IO error occurred.
     * @deprecated Use {@link #write(AudioFile, ParcelFileDescriptor)} instead.
     */
    @Deprecated
    public static void write(AudioFile f) throws CannotWriteException
    {
        getDefaultAudioFileIO().writeFile(f, (Path) null);
    }

    /**
     * Android-first write entry point.
     */
    public static void write(AudioFile audioFile, ParcelFileDescriptor pfd) throws CannotWriteException
    {
        getDefaultAudioFileIO().writeFile(audioFile, pfd);
    }

    /**
    *
    * Write the tag contained in the audioFile in the actual file on the disk.
    * 
    *
    * @param f The AudioFile to be written
    * @param targetPath The AudioFile path to which to be written without the extension. Cannot be null
    * @throws NoWritePermissionsException if the file could not be written to due to file permissions
    * @throws CannotWriteException If the file could not be written/accessed, the extension
    *                              wasn't recognized, or other IO error occurred.
    * @deprecated Use {@link #writeAs(AudioFile, Path)} instead.
    */
    @Deprecated
   public static void writeAs(AudioFile f, String targetPath) throws CannotWriteException
   {
       if (targetPath == null || targetPath.isEmpty()) {
           throw new CannotWriteException("Not a valid target path: " + targetPath);
       }
       getDefaultAudioFileIO().writeFile(f, Paths.get(targetPath));
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

    /**
     *
     * Read the tag contained in the given file.
     * 
     *
     * @param f The file to read.
     * @return The AudioFile with the file tag and the file encoding info.
     * @throws org.jaudiotagger.audio.exceptions.CannotReadException If the file could not be read, the extension wasn't
     *                             recognized, or an IO error occurred during the read.
     * @throws org.jaudiotagger.tag.TagException
     * @throws org.jaudiotagger.audio.exceptions.ReadOnlyFileException
     * @throws java.io.IOException
     * @throws org.jaudiotagger.audio.exceptions.InvalidAudioFrameException
     * @deprecated Use {@link #readFile(Path)} instead.
     */
    @Deprecated
    public AudioFile readFile(File f)
            throws CannotReadException, IOException, TagException, ReadOnlyFileException, InvalidAudioFrameException
    {
        return readFile(f.toPath());
    }

    public AudioFile readFile(Path path)
            throws CannotReadException, IOException, TagException, ReadOnlyFileException, InvalidAudioFrameException
    {
        //checkFileExists(path.toFile());
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
            throws CannotReadException
    {
        if (pfd == null)
        {
            throw new CannotReadException("ParcelFileDescriptor cannot be null");
        }
        String normalizedExt = extractExtensionHint(ext);
        AudioFileReader afr = readers.get(normalizedExt);
        if (afr == null)
        {
            throw new CannotReadException(ErrorMessage.NO_READER_FOR_THIS_FORMAT.getMsg(normalizedExt));
        }
        throw new CannotReadException("ParcelFileDescriptor read is not wired into format readers yet");
    }

    /**
    *
    * Read the tag contained in the given file.
    * 
    *
    * @param f The file to read.
    * @return The AudioFile with the file tag and the file encoding info.
    * @throws org.jaudiotagger.audio.exceptions.CannotReadException If the file could not be read, the extension wasn't
    *                             recognized, or an IO error occurred during the read.
    * @throws org.jaudiotagger.tag.TagException
    * @throws org.jaudiotagger.audio.exceptions.ReadOnlyFileException
    * @throws java.io.IOException
    * @throws org.jaudiotagger.audio.exceptions.InvalidAudioFrameException
    * @deprecated Use {@link #readFileMagic(Path)} instead.
    */
    @Deprecated
   public AudioFile readFileMagic(File f)
           throws CannotReadException, IOException, TagException, ReadOnlyFileException, InvalidAudioFrameException
   {
       return readFileMagic(f.toPath());
   }

    public AudioFile readFileMagic(Path path)
            throws CannotReadException, IOException, TagException, ReadOnlyFileException, InvalidAudioFrameException
    {
       //checkFileExists(path.toFile());
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
   *
   * Read the tag contained in the given file.
   * 
   *
   * @param f The file to read.
   * @param ext The extension to be used.
   * @return The AudioFile with the file tag and the file encoding info.
   * @throws org.jaudiotagger.audio.exceptions.CannotReadException If the file could not be read, the extension wasn't
   *                             recognized, or an IO error occurred during the read.
   * @throws org.jaudiotagger.tag.TagException
   * @throws org.jaudiotagger.audio.exceptions.ReadOnlyFileException
   * @throws java.io.IOException
   * @throws org.jaudiotagger.audio.exceptions.InvalidAudioFrameException
   * @deprecated Use {@link #readFileAs(Path, String)} instead.
   */
    @Deprecated
  public AudioFile readFileAs(File f,String ext)
          throws CannotReadException, IOException, TagException, ReadOnlyFileException, InvalidAudioFrameException
  {
      return readFileAs(f.toPath(), ext);
  }

    public AudioFile readFileAs(Path path, String ext)
            throws CannotReadException, IOException, TagException, ReadOnlyFileException, InvalidAudioFrameException
    {
      //checkFileExists(path.toFile());

      AudioFileReader afr = readers.get(ext);
      if (afr == null)
      {
          throw new CannotReadException(ErrorMessage.NO_READER_FOR_THIS_FORMAT.getMsg(ext));
      }

      AudioFile tempFile = afr.read(path);
      tempFile.setExt(ext);
      return tempFile;
    }

    public void writeFile(AudioFile f, ParcelFileDescriptor pfd) throws CannotWriteException
    {
        if (pfd == null)
        {
            throw new CannotWriteException("ParcelFileDescriptor cannot be null");
        }

        AudioFileWriter afw = writers.get(f.getExt());
        if (afw == null)
        {
            throw new CannotWriteException(ErrorMessage.NO_WRITER_FOR_THIS_FORMAT.getMsg(f.getExt()));
        }
        throw new CannotWriteException("ParcelFileDescriptor write is not wired into format writers yet");
    }

    public void deleteTag(AudioFile f, ParcelFileDescriptor pfd) throws CannotReadException, CannotWriteException
    {
        if (pfd == null)
        {
            throw new CannotWriteException("ParcelFileDescriptor cannot be null");
        }

        AudioFileWriter afw = writers.get(f.getExt());
        if (afw == null)
        {
            throw new CannotWriteException(ErrorMessage.NO_DELETER_FOR_THIS_FORMAT.getMsg(f.getExt()));
        }
        throw new CannotWriteException("ParcelFileDescriptor delete is not wired into format writers yet");
    }

    /**
     * Check does file exist
     *
     * @param file
     * @throws java.io.FileNotFoundException
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
     * Removes a listener for all file formats.
     *
     * @param listener listener
     */
    public void removeAudioFileModificationListener(
            AudioFileModificationListener listener)
    {
        this.modificationHandler.removeAudioFileModificationListener(listener);
    }

    /**
     *
     * Write the tag contained in the audioFile in the actual file on the disk.
     * 
     *
     * @param f The AudioFile to be written
     * @param targetPath a file path, without an extension, which provides a "save as". If null, then normal "save" function
     * @throws NoWritePermissionsException if the file could not be written to due to file permissions
     * @throws CannotWriteException If the file could not be written/accessed, the extension
     *                              wasn't recognized, or other IO error occurred.
     * @deprecated Use {@link #writeFile(AudioFile, Path)} instead.
     */
    @Deprecated
    public void writeFile(AudioFile f, String targetPath) throws CannotWriteException
    {
        final Path path = (targetPath == null || targetPath.isEmpty()) ? null : Paths.get(targetPath);
        writeFile(f, path);
    }

    public void writeFile(AudioFile f, Path targetPath) throws CannotWriteException
    {
        String ext = f.getExt();

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

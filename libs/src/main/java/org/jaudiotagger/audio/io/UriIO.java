package org.jaudiotagger.audio.io;

import android.content.ContentResolver;
import android.content.Context;
import android.net.Uri;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * Uri-based bridge for Android ContentResolver I/O.
 */
public final class UriIO
{
    public static final String TEMP_FILE_PREFIX = "jaudiotagger_uri_";
    public static final String TEMP_FILE_SUFFIX = ".tmp";

    private static final int BUFFER_SIZE = 8192;

    private UriIO()
    {
    }

    public static boolean isFileUri(Uri uri)
    {
        return uri != null && "file".equals(uri.getScheme());
    }

    public static Path toPath(Uri uri) throws IOException
    {
        if (uri == null)
        {
            throw new IOException("Uri cannot be null");
        }
        final String path = uri.getPath();
        if (path == null || path.isEmpty())
        {
            throw new IOException("Uri has no path: " + uri);
        }
        return Paths.get(path);
    }

    /**
     * Copies a {@code content://} Uri into the app cache directory and returns the temp path.
     *
     * <p>The temp file name prefix/suffix is part of the cleanup contract used by
     * {@link org.jaudiotagger.audio.AudioFileIO#cleanupLeakedUriTempFiles(Context)} and related
     * methods. Normal read/write/delete flows try to clean up these files immediately, while stale
     * leftovers can be removed later by the explicit cleanup API.</p>
     */
    public static Path copyUriToTempFile(Context context, Uri uri) throws IOException
    {
        if (context == null)
        {
            throw new IOException("Context cannot be null");
        }
        if (uri == null)
        {
            throw new IOException("Uri cannot be null");
        }

        final File cacheDir = context.getCacheDir();
        if (cacheDir == null)
        {
            throw new IOException("Context cache directory is unavailable");
        }

        final File temp = File.createTempFile(TEMP_FILE_PREFIX, TEMP_FILE_SUFFIX, cacheDir);
        final Path tempPath = temp.toPath();

        final ContentResolver resolver = context.getContentResolver();
        try (InputStream in = resolver.openInputStream(uri))
        {
            if (in == null)
            {
                throw new IOException("ContentResolver returned null InputStream for uri: " + uri);
            }
            try (OutputStream out = Files.newOutputStream(tempPath))
            {
                copy(in, out);
            }
        }

        return tempPath;
    }

    public static void copyTempFileToUri(Context context, Path sourcePath, Uri uri) throws IOException
    {
        if (context == null)
        {
            throw new IOException("Context cannot be null");
        }
        if (sourcePath == null)
        {
            throw new IOException("Source path cannot be null");
        }
        if (uri == null)
        {
            throw new IOException("Uri cannot be null");
        }

        final ContentResolver resolver = context.getContentResolver();
        try (InputStream in = Files.newInputStream(sourcePath);
             OutputStream out = openOutputStreamForOverwrite(resolver, uri))
        {
            copy(in, out);
            out.flush();
        }
    }

    public static void deleteQuietly(Path path)
    {
        if (path == null)
        {
            return;
        }
        try
        {
            Files.deleteIfExists(path);
        }
        catch (IOException ignored)
        {
        }
    }

    /**
     * Deletes leaked jaudiotagger temp files from the app cache directory.
     *
     * <p>This helper is used by {@link org.jaudiotagger.audio.AudioFileIO}'s explicit cleanup API.
     * Only files matching the internal temp-file naming contract and older than the supplied grace
     * period are eligible for deletion.</p>
     */
    public static int deleteLeakedTempFiles(Context context, long minAgeMillis) throws IOException
    {
        if (context == null)
        {
            throw new IOException("Context cannot be null");
        }
        if (minAgeMillis < 0)
        {
            throw new IllegalArgumentException("minAgeMillis cannot be negative");
        }

        final File cacheDir = context.getCacheDir();
        if (cacheDir == null)
        {
            throw new IOException("Context cache directory is unavailable");
        }

        final File[] files = cacheDir.listFiles();
        if (files == null || files.length == 0)
        {
            return 0;
        }

        final long threshold = System.currentTimeMillis() - minAgeMillis;
        int deleted = 0;
        for (File file : files)
        {
            if (file == null || !file.isFile())
            {
                continue;
            }

            final String name = file.getName();
            // Cleanup only targets this library's temp files and leaves unrelated cache entries alone.
            if (!name.startsWith(TEMP_FILE_PREFIX) || !name.endsWith(TEMP_FILE_SUFFIX))
            {
                continue;
            }

            // Fresh temp files may still belong to in-flight Uri operations, so honor the grace period.
            if (file.lastModified() > threshold)
            {
                continue;
            }

            try
            {
                if (Files.deleteIfExists(file.toPath()))
                {
                    deleted++;
                }
            }
            catch (IOException ignored)
            {
            }
        }

        return deleted;
    }

    private static OutputStream openOutputStreamForOverwrite(ContentResolver resolver, Uri uri) throws IOException
    {
        IOException firstError = null;
        OutputStream stream;

        try
        {
            stream = resolver.openOutputStream(uri, "rwt");
        }
        catch (IOException e)
        {
            stream = null;
            firstError = e;
        }

        if (stream != null)
        {
            return stream;
        }

        try
        {
            stream = resolver.openOutputStream(uri, "w");
        }
        catch (IOException e)
        {
            if (firstError != null)
            {
                e.addSuppressed(firstError);
            }
            throw e;
        }

        if (stream == null)
        {
            IOException e = new IOException("ContentResolver returned null OutputStream for uri: " + uri);
            if (firstError != null)
            {
                e.addSuppressed(firstError);
            }
            throw e;
        }

        return stream;
    }

    private static void copy(InputStream input, OutputStream output) throws IOException
    {
        byte[] buffer = new byte[BUFFER_SIZE];
        int read;
        while ((read = input.read(buffer)) >= 0)
        {
            output.write(buffer, 0, read);
        }
    }
}

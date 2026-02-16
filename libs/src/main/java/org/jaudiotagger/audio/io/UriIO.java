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

/**
 * Uri-based bridge for Android ContentResolver I/O.
 */
public final class UriIO
{
    private static final int BUFFER_SIZE = 8192;

    private UriIO()
    {
    }

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

        final File temp = File.createTempFile("jaudiotagger_uri_", ".tmp", cacheDir);
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

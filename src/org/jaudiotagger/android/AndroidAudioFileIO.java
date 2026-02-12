package org.jaudiotagger.android;

import org.jaudiotagger.audio.AudioFile;
import org.jaudiotagger.audio.AudioFileIO;
import org.jaudiotagger.audio.exceptions.CannotReadException;
import org.jaudiotagger.audio.exceptions.CannotWriteException;
import org.jaudiotagger.audio.exceptions.InvalidAudioFrameException;
import org.jaudiotagger.audio.exceptions.ReadOnlyFileException;
import org.jaudiotagger.tag.TagException;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Locale;

/**
 * Android adapter around {@link AudioFileIO} supporting SAF Uri access via reflection.
 *
 * This class intentionally avoids compile-time dependency on android.* packages.
 */
public final class AndroidAudioFileIO
{
    private static final int BUFFER_SIZE = 8192;

    private AndroidAudioFileIO()
    {
    }

    public static AudioFile read(Object context, Object uri)
        throws CannotReadException, IOException, TagException, ReadOnlyFileException, InvalidAudioFrameException
    {
        File tmp = createTempFile(context, uri, null);
        try (InputStream in = openInputStream(context, uri))
        {
            copy(in, new FileOutputStream(tmp));
        }
        return AudioFileIO.read(tmp);
    }

    public static AudioFile readAs(Object context, Object uri, String ext)
        throws CannotReadException, IOException, TagException, ReadOnlyFileException, InvalidAudioFrameException
    {
        File tmp = createTempFile(context, uri, ext);
        try (InputStream in = openInputStream(context, uri))
        {
            copy(in, new FileOutputStream(tmp));
        }
        return AudioFileIO.readAs(tmp, ext);
    }

    public static void write(Object context, AudioFile audioFile, Object uri)
        throws CannotWriteException, IOException
    {
        if (audioFile == null)
        {
            throw new CannotWriteException("AudioFile is null");
        }

        AudioFileIO.write(audioFile);

        try (InputStream in = java.nio.file.Files.newInputStream(audioFile.getFile().toPath());
             OutputStream out = openOutputStream(context, uri, "wt"))
        {
            copy(in, out);
        }
    }

    public static void delete(Object context, Object uri) throws CannotWriteException
    {
        try
        {
            Object resolver = getContentResolver(context);
            Method delete = resolver.getClass().getMethod("delete", Class.forName("android.net.Uri"), String.class, String[].class);
            delete.invoke(resolver, uri, null, null);
        }
        catch (Exception e)
        {
            throw new CannotWriteException("Unable to delete Uri", e);
        }
    }

    private static File createTempFile(Object context, Object uri, String explicitExt) throws IOException
    {
        File cacheDir = resolveCacheDir(context);
        String ext = explicitExt != null && !explicitExt.isEmpty() ? explicitExt : guessExtFromUri(uri);
        String suffix = ext.isEmpty() ? ".tmp" : "." + ext;
        return File.createTempFile("jaudiotagger_android_", suffix, cacheDir);
    }

    private static String guessExtFromUri(Object uri)
    {
        if (uri == null)
        {
            return "";
        }
        String value = String.valueOf(uri).toLowerCase(Locale.ROOT);
        int q = value.indexOf('?');
        if (q >= 0)
        {
            value = value.substring(0, q);
        }
        int dot = value.lastIndexOf('.');
        if (dot < 0 || dot == value.length() - 1)
        {
            return "";
        }
        return value.substring(dot + 1);
    }

    private static File resolveCacheDir(Object context) throws IOException
    {
        if (context == null)
        {
            return new File(System.getProperty("java.io.tmpdir"));
        }
        try
        {
            Method getCacheDir = context.getClass().getMethod("getCacheDir");
            Object dir = getCacheDir.invoke(context);
            if (dir instanceof File)
            {
                return (File) dir;
            }
        }
        catch (ReflectiveOperationException ignored)
        {
            // Fall through to tmpdir fallback.
        }
        return new File(System.getProperty("java.io.tmpdir"));
    }

    private static Object getContentResolver(Object context)
        throws NoSuchMethodException, InvocationTargetException, IllegalAccessException
    {
        Method getContentResolver = context.getClass().getMethod("getContentResolver");
        return getContentResolver.invoke(context);
    }

    private static InputStream openInputStream(Object context, Object uri) throws IOException
    {
        try
        {
            Object resolver = getContentResolver(context);
            Method openInputStream = resolver.getClass().getMethod("openInputStream", Class.forName("android.net.Uri"));
            Object stream = openInputStream.invoke(resolver, uri);
            if (!(stream instanceof InputStream))
            {
                throw new IOException("openInputStream returned null/invalid stream");
            }
            return (InputStream) stream;
        }
        catch (ReflectiveOperationException e)
        {
            throw new IOException("Unable to open Uri input stream", e);
        }
    }

    private static OutputStream openOutputStream(Object context, Object uri, String mode) throws IOException
    {
        try
        {
            Object resolver = getContentResolver(context);
            Method openOutputStream = resolver.getClass().getMethod("openOutputStream", Class.forName("android.net.Uri"), String.class);
            Object stream = openOutputStream.invoke(resolver, uri, mode);
            if (!(stream instanceof OutputStream))
            {
                throw new IOException("openOutputStream returned null/invalid stream");
            }
            return (OutputStream) stream;
        }
        catch (ReflectiveOperationException e)
        {
            throw new IOException("Unable to open Uri output stream", e);
        }
    }

    private static void copy(InputStream in, OutputStream out) throws IOException
    {
        try (InputStream input = in; OutputStream output = out)
        {
            byte[] buffer = new byte[BUFFER_SIZE];
            int read;
            while ((read = input.read(buffer)) != -1)
            {
                output.write(buffer, 0, read);
            }
            output.flush();
        }
    }
}

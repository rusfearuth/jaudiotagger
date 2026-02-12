package org.jaudiotagger.audio.io;

import android.os.ParcelFileDescriptor;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;

/**
 * Android-first adapter backed by the incoming descriptor itself (no temp-file bridge).
 */
public final class ParcelFileDescriptorIO implements SeekableInputOutput
{
    private final ParcelFileDescriptor pfd;
    private final FileChannel channel;

    public ParcelFileDescriptorIO(ParcelFileDescriptor pfd) throws IOException
    {
        if (pfd == null)
        {
            throw new IOException("ParcelFileDescriptor cannot be null");
        }
        this.pfd = pfd;

        final String procFdPath = "/proc/self/fd/" + pfd.getFd();
        Path path = Paths.get(procFdPath);

        FileChannel rwChannel;
        try
        {
            rwChannel = FileChannel.open(path, StandardOpenOption.READ, StandardOpenOption.WRITE);
        }
        catch (IOException ignored)
        {
            rwChannel = FileChannel.open(path, StandardOpenOption.READ);
        }
        this.channel = rwChannel;
    }

    @Override
    public long position() throws IOException
    {
        return channel.position();
    }

    @Override
    public void position(long newPosition) throws IOException
    {
        channel.position(newPosition);
    }

    @Override
    public int read(ByteBuffer dst) throws IOException
    {
        return channel.read(dst);
    }

    @Override
    public int write(ByteBuffer src) throws IOException
    {
        return channel.write(src);
    }

    @Override
    public long size() throws IOException
    {
        return channel.size();
    }

    @Override
    public void truncate(long size) throws IOException
    {
        channel.truncate(size);
    }

    @Override
    public void force() throws IOException
    {
        channel.force(true);
    }

    @Override
    public void close() throws IOException
    {
        IOException pending = null;
        try
        {
            channel.close();
        }
        catch (IOException e)
        {
            pending = e;
        }

        try
        {
            pfd.close();
        }
        catch (IOException e)
        {
            if (pending == null)
            {
                pending = e;
            }
        }

        if (pending != null)
        {
            throw pending;
        }
    }
}

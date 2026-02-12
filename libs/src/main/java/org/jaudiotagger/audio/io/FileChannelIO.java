package org.jaudiotagger.audio.io;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;

/**
 * File-based compatibility adapter for the new internal seekable I/O abstraction.
 */
public final class FileChannelIO implements SeekableInputOutput
{
    private final FileChannel channel;

    public FileChannelIO(Path path) throws IOException
    {
        this.channel = FileChannel.open(path, StandardOpenOption.READ, StandardOpenOption.WRITE);
    }

    public FileChannelIO(FileChannel channel)
    {
        this.channel = channel;
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
        channel.close();
    }
}

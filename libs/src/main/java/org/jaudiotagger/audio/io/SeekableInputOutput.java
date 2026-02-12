package org.jaudiotagger.audio.io;

import java.io.Closeable;
import java.io.IOException;
import java.nio.ByteBuffer;

/**
 * Internal seekable byte channel abstraction used to migrate away from File/RandomAccessFile.
 */
public interface SeekableInputOutput extends Closeable
{
    long position() throws IOException;

    void position(long newPosition) throws IOException;

    int read(ByteBuffer dst) throws IOException;

    int write(ByteBuffer src) throws IOException;

    long size() throws IOException;

    void truncate(long size) throws IOException;

    void force() throws IOException;
}

package org.jaudiotagger.audio;

import android.content.Context;
import android.os.ParcelFileDescriptor;

import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.platform.app.InstrumentationRegistry;

import org.jaudiotagger.audio.exceptions.CannotReadException;
import org.jaudiotagger.audio.exceptions.CannotWriteException;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.fail;

@RunWith(AndroidJUnit4.class)
public class AudioFileIOParcelFileDescriptorTest
{
    @Test
    public void readViaParcelFileDescriptorUsesRealFlow() throws Exception
    {
        File testFile = createFile("pfd-read.mp3", 256);
        try (ParcelFileDescriptor pfd = ParcelFileDescriptor.open(testFile, ParcelFileDescriptor.MODE_READ_ONLY))
        {
            try
            {
                AudioFileIO.readAs(pfd, "mp3");
                fail("Expected CannotReadException for invalid mp3 payload");
            }
            catch (CannotReadException expected)
            {
                assertNotStub(expected.getMessage());
            }
        }
    }

    @Test
    public void writeViaParcelFileDescriptorUsesRealFlow() throws Exception
    {
        File testFile = createFile("pfd-write.wav", 10);
        AudioFile audioFile = new AudioFile();
        audioFile.setFile(testFile);
        audioFile.setExt("wav");

        try (ParcelFileDescriptor pfd = ParcelFileDescriptor.open(testFile, ParcelFileDescriptor.MODE_READ_WRITE))
        {
            try
            {
                AudioFileIO.write(audioFile, pfd);
                fail("Expected CannotWriteException for tiny file");
            }
            catch (CannotWriteException expected)
            {
                assertNotStub(expected.getMessage());
            }
        }
    }

    @Test
    public void deleteViaParcelFileDescriptorUsesRealFlow() throws Exception
    {
        File testFile = createFile("pfd-delete.wav", 10);
        AudioFile audioFile = new AudioFile();
        audioFile.setFile(testFile);
        audioFile.setExt("wav");

        try (ParcelFileDescriptor pfd = ParcelFileDescriptor.open(testFile, ParcelFileDescriptor.MODE_READ_WRITE))
        {
            try
            {
                AudioFileIO.delete(audioFile, pfd);
                fail("Expected CannotWriteException for tiny file");
            }
            catch (CannotWriteException expected)
            {
                assertNotStub(expected.getMessage());
            }
        }
    }

    private static void assertNotStub(String message)
    {
        assertFalse(message != null && message.contains("not wired"));
    }

    private static File createFile(String name, int length) throws IOException
    {
        Context context = InstrumentationRegistry.getInstrumentation().getTargetContext();
        File file = new File(context.getCacheDir(), name);
        if (file.exists() && !file.delete())
        {
            throw new IOException("Unable to delete existing test file: " + file);
        }

        byte[] payload = new byte[length];
        for (int i = 0; i < payload.length; i++)
        {
            payload[i] = (byte) (i & 0xFF);
        }

        try (FileOutputStream out = new FileOutputStream(file))
        {
            out.write(payload);
        }

        return file;
    }
}

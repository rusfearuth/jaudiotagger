package org.jaudiotagger.audio;

import android.content.Context;
import android.net.Uri;

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
public class AudioFileIOUriTest
{
    @Test
    public void readViaUriUsesRealFlow() throws Exception
    {
        File testFile = createFile("uri-read.mp3", 256);
        Uri uri = Uri.fromFile(testFile);
        Context context = InstrumentationRegistry.getInstrumentation().getTargetContext();

        try
        {
            AudioFileIO.readAs(context, uri, "mp3");
            fail("Expected CannotReadException for invalid mp3 payload");
        }
        catch (CannotReadException expected)
        {
            assertNotStub(expected.getMessage());
        }
    }

    @Test
    public void writeViaUriUsesRealFlow() throws Exception
    {
        File testFile = createFile("uri-write.wav", 10);
        Uri uri = Uri.fromFile(testFile);
        Context context = InstrumentationRegistry.getInstrumentation().getTargetContext();

        AudioFile audioFile = new AudioFile();
        audioFile.setFile(testFile);
        audioFile.setExt("wav");

        try
        {
            AudioFileIO.write(context, audioFile, uri);
            fail("Expected CannotWriteException for tiny file");
        }
        catch (CannotWriteException expected)
        {
            assertNotStub(expected.getMessage());
        }
    }

    @Test
    public void deleteViaUriUsesRealFlow() throws Exception
    {
        File testFile = createFile("uri-delete.wav", 10);
        Uri uri = Uri.fromFile(testFile);
        Context context = InstrumentationRegistry.getInstrumentation().getTargetContext();

        AudioFile audioFile = new AudioFile();
        audioFile.setFile(testFile);
        audioFile.setExt("wav");

        try
        {
            AudioFileIO.delete(context, audioFile, uri);
            fail("Expected CannotWriteException for tiny file");
        }
        catch (CannotWriteException expected)
        {
            assertNotStub(expected.getMessage());
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

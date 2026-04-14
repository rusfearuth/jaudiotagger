package org.jaudiotagger.audio;

import android.content.Context;
import android.net.Uri;

import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.platform.app.InstrumentationRegistry;

import org.jaudiotagger.audio.exceptions.CannotReadException;
import org.jaudiotagger.audio.exceptions.CannotWriteException;
import org.jaudiotagger.audio.exceptions.InvalidAudioFrameException;
import org.jaudiotagger.audio.io.UriIO;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

@RunWith(AndroidJUnit4.class)
public class AudioFileIOUriTest
{
    @Test
    public void releaseDeletesManagedTempBackingAndNullsPath() throws Exception
    {
        File tempFile = createNamedCacheFile(UriIO.TEMP_FILE_PREFIX + "release" + UriIO.TEMP_FILE_SUFFIX, 8);

        AudioFile audioFile = new AudioFile();
        audioFile.setPath(tempFile.toPath());
        audioFile.setExt("mp3");
        audioFile.bindManagedUriTempFile(tempFile.toPath());

        assertTrue(audioFile.release());
        assertFalse(tempFile.exists());
        assertNull(audioFile.getPath());
        assertFalse(audioFile.release());
    }

    @Test
    public void releaseDoesNothingForRegularFileBackedAudioFile() throws Exception
    {
        File regularFile = createFile("regular-release.mp3", 8);

        AudioFile audioFile = new AudioFile();
        audioFile.setPath(regularFile.toPath());
        audioFile.setExt("mp3");

        assertFalse(audioFile.release());
        assertTrue(regularFile.exists());
        assertEquals(regularFile.toPath(), audioFile.getPath());
    }

    @Test
    public void commitAfterReleaseFailsWithExplicitMessage() throws Exception
    {
        File tempFile = createNamedCacheFile(UriIO.TEMP_FILE_PREFIX + "commit" + UriIO.TEMP_FILE_SUFFIX, 8);

        AudioFile audioFile = new AudioFile();
        audioFile.setPath(tempFile.toPath());
        audioFile.setExt("mp3");
        audioFile.bindManagedUriTempFile(tempFile.toPath());
        audioFile.release();

        try
        {
            audioFile.commit();
            fail("Expected CannotWriteException after release");
        }
        catch (CannotWriteException expected)
        {
            assertTrue(expected.getMessage().contains("released"));
        }
    }

    @Test
    public void deleteAfterReleaseFailsWithExplicitMessage() throws Exception
    {
        File tempFile = createNamedCacheFile(UriIO.TEMP_FILE_PREFIX + "delete" + UriIO.TEMP_FILE_SUFFIX, 8);

        AudioFile audioFile = new AudioFile();
        audioFile.setPath(tempFile.toPath());
        audioFile.setExt("mp3");
        audioFile.bindManagedUriTempFile(tempFile.toPath());
        audioFile.release();

        try
        {
            audioFile.delete();
            fail("Expected CannotWriteException after release");
        }
        catch (CannotWriteException expected)
        {
            assertTrue(expected.getMessage().contains("released"));
        }
    }

    @Test
    public void uriWriteStillWorksAfterReleaseLifecycleCheck() throws Exception
    {
        Context context = InstrumentationRegistry.getInstrumentation().getTargetContext();
        File targetFile = createFile("released-write.wav", 10);

        File tempFile = createNamedCacheFile(UriIO.TEMP_FILE_PREFIX + "uri-write" + UriIO.TEMP_FILE_SUFFIX, 8);
        AudioFile audioFile = new AudioFile();
        audioFile.setPath(tempFile.toPath());
        audioFile.setExt("wav");
        audioFile.bindManagedUriTempFile(tempFile.toPath());
        audioFile.release();

        try
        {
            AudioFileIO.write(context, audioFile, Uri.fromFile(targetFile));
        }
        catch (CannotWriteException expected)
        {
            assertNotReleasedTempGuard(expected.getMessage());
        }
    }

    @Test
    public void uriDeleteStillWorksAfterReleaseLifecycleCheck() throws Exception
    {
        Context context = InstrumentationRegistry.getInstrumentation().getTargetContext();
        File targetFile = createFile("released-delete.wav", 10);

        File tempFile = createNamedCacheFile(UriIO.TEMP_FILE_PREFIX + "uri-delete" + UriIO.TEMP_FILE_SUFFIX, 8);
        AudioFile audioFile = new AudioFile();
        audioFile.setPath(tempFile.toPath());
        audioFile.setExt("wav");
        audioFile.bindManagedUriTempFile(tempFile.toPath());
        audioFile.release();

        try
        {
            AudioFileIO.delete(context, audioFile, Uri.fromFile(targetFile));
        }
        catch (CannotWriteException expected)
        {
            assertNotReleasedTempGuard(expected.getMessage());
        }
    }

    @Test
    public void commitAfterReleaseWorksWhenAudioFileIsReboundToPhysicalPath() throws Exception
    {
        File targetFile = createFile("released-rebind-commit.wav", 10);

        File tempFile = createNamedCacheFile(UriIO.TEMP_FILE_PREFIX + "uri-commit" + UriIO.TEMP_FILE_SUFFIX, 8);
        AudioFile audioFile = new AudioFile();
        audioFile.setPath(tempFile.toPath());
        audioFile.setExt("wav");
        audioFile.bindManagedUriTempFile(tempFile.toPath());
        audioFile.release();
        audioFile.setPath(targetFile.toPath());

        try
        {
            audioFile.commit();
        }
        catch (CannotWriteException expected)
        {
            assertNotReleasedTempGuard(expected.getMessage());
        }
    }

    @Test
    public void deleteAfterReleaseWorksWhenAudioFileIsReboundToPhysicalPath() throws Exception
    {
        File targetFile = createFile("released-rebind-delete.wav", 10);

        File tempFile = createNamedCacheFile(UriIO.TEMP_FILE_PREFIX + "uri-direct-delete" + UriIO.TEMP_FILE_SUFFIX, 8);
        AudioFile audioFile = new AudioFile();
        audioFile.setPath(tempFile.toPath());
        audioFile.setExt("wav");
        audioFile.bindManagedUriTempFile(tempFile.toPath());
        audioFile.release();
        audioFile.setPath(targetFile.toPath());

        try
        {
            audioFile.delete();
        }
        catch (CannotWriteException expected)
        {
            assertNotReleasedTempGuard(expected.getMessage());
        }
    }

    @Test
    public void cleanupLeakedUriTempFilesDeletesOnlyOldMatchingTemps() throws Exception
    {
        Context context = InstrumentationRegistry.getInstrumentation().getTargetContext();
        deleteUriTempFiles(context);

        File staleTemp = createNamedCacheFile(UriIO.TEMP_FILE_PREFIX + "stale" + UriIO.TEMP_FILE_SUFFIX, 8);
        File freshTemp = createNamedCacheFile(UriIO.TEMP_FILE_PREFIX + "fresh" + UriIO.TEMP_FILE_SUFFIX, 8);
        File unrelatedTemp = createNamedCacheFile("other.tmp", 8);
        File wrongPrefix = createNamedCacheFile("jaudiotagger_other.tmp", 8);

        long oldTimestamp = System.currentTimeMillis() - TimeUnit.MINUTES.toMillis(10);
        assertTrue(staleTemp.setLastModified(oldTimestamp));

        int deleted = AudioFileIO.cleanupLeakedUriTempFiles(context, TimeUnit.MINUTES.toMillis(5));

        assertEquals(1, deleted);
        assertFalse(staleTemp.exists());
        assertTrue(freshTemp.exists());
        assertTrue(unrelatedTemp.exists());
        assertTrue(wrongPrefix.exists());
    }

    @Test
    public void cleanupLeakedUriTempFilesAsyncRunsOnProvidedExecutor() throws Exception
    {
        Context context = InstrumentationRegistry.getInstrumentation().getTargetContext();
        deleteUriTempFiles(context);

        File staleTemp = createNamedCacheFile(UriIO.TEMP_FILE_PREFIX + "async" + UriIO.TEMP_FILE_SUFFIX, 8);
        long oldTimestamp = System.currentTimeMillis() - TimeUnit.MINUTES.toMillis(10);
        assertTrue(staleTemp.setLastModified(oldTimestamp));

        ExecutorService executor = Executors.newSingleThreadExecutor();
        try
        {
            int deleted = AudioFileIO.cleanupLeakedUriTempFilesAsync(
                    context,
                    TimeUnit.MINUTES.toMillis(5),
                    executor
            ).get(10, TimeUnit.SECONDS);

            assertEquals(1, deleted);
            assertFalse(staleTemp.exists());
        }
        finally
        {
            executor.shutdownNow();
        }
    }

    @Test
    public void readViaUriUsesRealFlow() throws Exception
    {
        File testFile = createFile("uri-read.mp3", 256);
        Uri uri = Uri.fromFile(testFile);
        Context context = InstrumentationRegistry.getInstrumentation().getTargetContext();

        try
        {
            AudioFileIO.readAs(context, uri, "mp3");
            fail("Expected read failure for invalid mp3 payload");
        }
        catch (CannotReadException expected)
        {
            assertNotStub(expected.getMessage());
        }
        catch (InvalidAudioFrameException expected)
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

    private static void assertNotReleasedTempGuard(String message)
    {
        assertFalse(message != null && message.contains("backing temp file has been released"));
    }

    private static void deleteUriTempFiles(Context context)
    {
        File[] files = context.getCacheDir().listFiles();
        if (files == null)
        {
            return;
        }

        for (File file : files)
        {
            if (file.isFile()
                    && file.getName().startsWith(UriIO.TEMP_FILE_PREFIX)
                    && file.getName().endsWith(UriIO.TEMP_FILE_SUFFIX))
            {
                file.delete();
            }
        }
    }

    private static File createFile(String name, int length) throws IOException
    {
        Context context = InstrumentationRegistry.getInstrumentation().getTargetContext();
        return createPayloadFile(new File(context.getCacheDir(), name), length);
    }

    private static File createNamedCacheFile(String name, int length) throws IOException
    {
        Context context = InstrumentationRegistry.getInstrumentation().getTargetContext();
        return createPayloadFile(new File(context.getCacheDir(), name), length);
    }

    private static File createPayloadFile(File file, int length) throws IOException
    {
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

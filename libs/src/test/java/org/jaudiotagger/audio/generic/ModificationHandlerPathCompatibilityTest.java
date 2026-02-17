package org.jaudiotagger.audio.generic;

import junit.framework.TestCase;
import org.jaudiotagger.audio.AudioFile;
import org.jaudiotagger.audio.exceptions.ModifyVetoException;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;

public class ModificationHandlerPathCompatibilityTest extends TestCase
{
    public void testPathEventsReachLegacyListenerMethods() throws ModifyVetoException
    {
        ModificationHandler handler = new ModificationHandler();
        LegacyListener listener = new LegacyListener();
        AudioFile audioFile = new AudioFile();
        Path result = Paths.get("testdatatmp", "listener-path.mp3");

        handler.addAudioFileModificationListener(listener);
        handler.fileModifiedPath(audioFile, result);
        handler.fileOperationFinishedPath(result);

        assertEquals(result.toFile().getPath(), listener.lastModifiedTempPath);
        assertEquals(result.toFile().getPath(), listener.lastFinishedPath);
    }

    private static final class LegacyListener extends AudioFileModificationAdapter
    {
        private String lastModifiedTempPath;
        private String lastFinishedPath;

        @Override
        public void fileModified(AudioFile original, File temporary) throws ModifyVetoException
        {
            lastModifiedTempPath = temporary.getPath();
        }

        @Override
        public void fileOperationFinished(File result)
        {
            lastFinishedPath = result.getPath();
        }
    }
}

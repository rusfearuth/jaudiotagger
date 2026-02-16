package org.jaudiotagger.audio;

/**
 * File formats currently supported by the library.
 * Each enum value is associated with a file suffix (extension).
 */
public enum SupportedFileFormat
{
    OGG("ogg", "Ogg"),
    OGA("oga", "Oga"),
    MP3("mp3", "Mp3"),
    FLAC("flac", "Flac"),
    MP4("mp4", "Mp4"),
    M4A("m4a", "Mp4"),
    M4P("m4p", "M4p"),
    WMA("wma", "Wma"),
    WAV("wav", "Wav"),
    RA("ra", "Ra"),
    RM("rm", "Rm"),
    M4B("m4b", "Mp4"),
    AIF("aif", "Aif"),
    AIFF("aiff", "Aif"),
    AIFC("aifc", "Aif Compressed"),
    DSF("dsf", "Dsf"),
    DFF("dff", "Dff");

    /**
     * File suffix without leading dot.
     */
    private String filesuffix;

    /**
     * User-friendly display name.
     */
    private String displayName;

    /**
     * Constructor for internal use by this enum.
     */
    SupportedFileFormat(String filesuffix, String displayName)
    {
        this.filesuffix = filesuffix;
        this.displayName = displayName;
    }

    /**
     * Returns the file suffix associated with the format.
     *
     * @return lower-case extension without leading dot.
     */
    public String getFilesuffix()
    {
        return filesuffix;
    }

    /**
     * Returns a human-readable format name.
     *
     * @return display name.
     */
    public String getDisplayName()
    {
        return displayName;
    }
}

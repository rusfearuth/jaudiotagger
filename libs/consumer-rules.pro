# ============================================================================
# jaudiotagger — consumer ProGuard / R8 rules
# Shipped inside the AAR; automatically applied to consuming apps.
# ============================================================================

# --------------------------------------------------------------------------
# 1. FrameBody classes loaded via Class.forName() (CRITICAL)
#
# AbstractID3v2Frame and ID3v22Frame construct class names at runtime:
#   Class.forName("org.jaudiotagger.tag.id3.framebody.FrameBody" + identifier)
# They then call newInstance(), getConstructor(ByteBuffer, int), and
# getConstructor(body.getClass()) — all three constructor forms must survive.
# --------------------------------------------------------------------------
-keep class org.jaudiotagger.tag.id3.framebody.FrameBody* {
    <init>(...);
}
-keep class org.jaudiotagger.tag.id3.framebody.Abstract* {
    <init>(...);
}

# --------------------------------------------------------------------------
# 2. DataType copy-constructors used by ID3Tags.copyObject()
#
# ID3Tags.copyObject() calls:
#   copyObject.getClass().getConstructor(copyObject.getClass()).newInstance(...)
# Every concrete AbstractDataType subclass must retain its constructors.
# --------------------------------------------------------------------------
-keep class org.jaudiotagger.tag.datatype.** {
    <init>(...);
}

# --------------------------------------------------------------------------
# 3. ASF ChunkReader instantiation via Class.newInstance()
#
# ChunkContainerReader.register() calls toRegister.newInstance().
# --------------------------------------------------------------------------
-keepclassmembers class * implements org.jaudiotagger.audio.asf.io.ChunkReader {
    <init>();
}

# --------------------------------------------------------------------------
# 4. Serializable classes — preserve serialization infrastructure
#
# EventListenerList deserializes listener class names via Class.forName().
# --------------------------------------------------------------------------
-keepclassmembers class * implements java.io.Serializable {
    static final long serialVersionUID;
    private void writeObject(java.io.ObjectOutputStream);
    private void readObject(java.io.ObjectInputStream);
    java.lang.Object writeReplace();
    java.lang.Object readResolve();
}

-keep class org.jaudiotagger.utils.tree.** { *; }

# --------------------------------------------------------------------------
# 5. Enums — protect values() and valueOf() from removal
#
# 45+ enum classes; some accessed by ordinal (e.g. BlockType.values()[type]).
# --------------------------------------------------------------------------
-keepclassmembers enum org.jaudiotagger.** {
    public static **[] values();
    public static ** valueOf(java.lang.String);
}

# --------------------------------------------------------------------------
# 6. Logging — prevent removal of Logger fields
# --------------------------------------------------------------------------
-keepclassmembers class org.jaudiotagger.** {
    static java.util.logging.Logger *;
}

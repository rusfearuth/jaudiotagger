package org.jaudiotagger.tag.asf;

import org.jaudiotagger.audio.asf.data.MetadataDescriptor;
import org.jaudiotagger.tag.TagField;

/**
 * Android-safe image field abstraction that avoids desktop image APIs.
 */
abstract class AbstractAsfTagImageField extends AsfTagField
{
    public AbstractAsfTagImageField(final AsfFieldKey field)
    {
        super(field);
    }

    public AbstractAsfTagImageField(final MetadataDescriptor source)
    {
        super(source);
    }

    public AbstractAsfTagImageField(final String fieldKey)
    {
        super(fieldKey);
    }

    public Object getImage()
    {
        return getRawImageData();
    }

    public abstract int getImageDataSize();

    public abstract byte[] getRawImageData();
}

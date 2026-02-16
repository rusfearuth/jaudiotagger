/*
 * Entagged Audio Tag library
 * Copyright (c) 2003-2010 Raphaël Slinckx <raphael@slinckx.net>
 * 
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *  
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA
 */
package org.jaudiotagger.tag;

import org.jaudiotagger.tag.images.Artwork;

import java.nio.charset.Charset;
import java.util.Iterator;
import java.util.List;

/**
 * Format-agnostic metadata container for a single audio file.
 *
 * <p>Implementations map common {@link FieldKey} values to format-specific fields (ID3, Vorbis Comment,
 * MP4 atoms, and so on). Some formats support multi-value fields while others collapse values into a
 * single stored representation.</p>
 *
 * <p>Typical usage:</p>
 * <pre>
 * AudioFile file = AudioFileIO.read(Paths.get("test.mp3"));
 * Tag tag = file.getTagOrCreateAndSetDefault();
 * tag.setField(FieldKey.ARTIST, "Artist");
 * </pre>
 *
 * @author Raphael Slinckx
 * @author Paul Taylor
 */
public interface Tag {

    /**
     * Sets the value(s) of a generic field key, replacing existing values for that key.
     *
     * @param genericKey format-agnostic key to set.
     * @param value value(s) to assign.
     * @throws KeyNotFoundException if the key is unsupported by this tag implementation.
     * @throws FieldDataInvalidException if provided value cannot be encoded for this format.
     */
    void setField(FieldKey genericKey, String... value) throws KeyNotFoundException, FieldDataInvalidException;

    /**
     * Adds value(s) for a generic field key without deleting existing values first.
     *
     * <p>Behavior for multi-value storage is format-specific.</p>
     *
     * @param genericKey format-agnostic key to add.
     * @param value value(s) to add.
     * @throws KeyNotFoundException if the key is unsupported by this tag implementation.
     * @throws FieldDataInvalidException if provided value cannot be encoded for this format.
     */
    void addField(FieldKey genericKey, String... value) throws KeyNotFoundException, FieldDataInvalidException;

    /**
     * Deletes all fields mapped to the provided generic key.
     *
     * @param fieldKey format-agnostic key to delete.
     * @throws KeyNotFoundException if key is unsupported.
     */
    void deleteField(FieldKey fieldKey) throws KeyNotFoundException;

    /**
     * Deletes all fields with the exact format-specific field identifier.
     *
     * @param key format-specific field id (for example Vorbis key).
     * @throws KeyNotFoundException if key is unsupported.
     */
    void deleteField(String key)throws KeyNotFoundException;

    /**
     * Returns a {@linkplain List list} of {@link TagField} objects whose &quot;{@linkplain TagField#getId() id}&quot;
     * is the specified one.<br>
     *
     * <p>Can be used to retrieve fields with any identifier, useful if the identifier is not within {@link FieldKey}
     *
     * @param id The field id.
     * @return A list of {@link TagField} objects with the given &quot;id&quot;.
     */
    List<TagField> getFields(String id);

    /**
     * Returns a {@linkplain List list} of {@link TagField} objects whose &quot;{@linkplain TagField#getId() id}&quot;
     * is the specified one.<br>
     *
     * @param id The field id.
     * @return A list of {@link TagField} objects with the given &quot;id&quot;.
     * @throws KeyNotFoundException
     */
    List<TagField> getFields(FieldKey id) throws KeyNotFoundException;


    /**
     * Iterator over all the fields within the tag, handle multiple fields with the same id
     *
     * @return iterator over whole list
     */
    Iterator<TagField> getFields();


    /**
     * Returns first value for a format-specific identifier.
     *
     * <p>Useful for fields not represented by {@link FieldKey}.</p>
     *
     * @param id format-specific field id.
     * @return first value or empty string.
     */
    String getFirst(String id);

    /**
     * Returns first value mapped to a generic key.
     *
     * @param id format-agnostic key.
     * @return first value or empty string.
     * @throws KeyNotFoundException if key is unsupported.
     */
    String getFirst(FieldKey id) throws KeyNotFoundException;

    /**
     * Returns all values mapped to a generic key.
     *
     * @param id format-agnostic key.
     * @return possibly empty list of values.
     * @throws KeyNotFoundException if key is unsupported.
     */
    List<String> getAll(FieldKey id) throws KeyNotFoundException;

    /**
     * Returns Nth value mapped to a generic key.
     *
     * @param id format-agnostic key.
     * @param n zero-based index.
     * @return value at index or empty string if unavailable.
     */
    String getValue(FieldKey id, int n);

    /**
     * Retrieve the first field that exists for this format specific key
     *
     * <p>Can be used to retrieve fields with any identifier, useful if the identifier is not within {@link FieldKey}
     *
     * @param id audio specific key
     * @return tag field or null if doesn't exist
     */
    TagField getFirstField(String id);

    /**
     * Returns first field object matching this generic key.
     *
     * @param id format-agnostic key.
     * @return field object or {@code null} when unavailable.
     */
    TagField getFirstField(FieldKey id);

    /**
     * Returns <code>true</code>, if at least one of the contained
     * {@linkplain TagField fields} is a common field ({@link TagField#isCommon()}).
     *
     * @return <code>true</code> if a {@linkplain TagField#isCommon() common}
     *         field is present.
     */
    boolean hasCommonFields();

    /**
     * Determines whether the tag has at least one value for the provided generic key.
     *
     * @param fieldKey format-agnostic key.
     * @return {@code true} when at least one value exists.
     */
    boolean hasField(FieldKey fieldKey);

    /**
     * Determines whether the tag has at least one field with the specified
     * &quot;id&quot;.
     *
     * @param id The field id to look for.
     * @return <code>true</code> if tag contains a {@link TagField} with the
     *         given {@linkplain TagField#getId() id}.
     */
    boolean hasField(String id);

    /**
     * Determines whether the tag has no fields specified.<br>
     *
     * @return <code>true</code> if tag contains no field.
     */
    boolean isEmpty();


    //TODO, do we need this
    String toString();

    /**
     * Return the number of fields
     *
     * <p>Fields with the same identifiers are counted separately
     *
     * i.e two TITLE fields in a Vorbis Comment file would count as two
     *
     * @return total number of fields
     */
    int getFieldCount();


    /**
     * Returns number of fields counting sub-values for multi-value fields.
     *
     * <p>For example, one ID3v2.4 TCON field with two genres contributes {@code 2}.</p>
     *
     * @return total count including sub-values.
     */
    int getFieldCountIncludingSubValues();


    //TODO is this a special field?
    boolean setEncoding(Charset enc) throws FieldDataInvalidException;


    /**
     * @return a list of all artwork in this file using the format independent Artwork class
     */
    List<Artwork> getArtworkList();

    /**
     * @return first artwork or null if none exist
     */
    Artwork getFirstArtwork();

    /**
     * Delete any instance of tag fields used to store artwork
     *
     * <p>We need this additional deleteField method because in some formats artwork can be stored
     * in multiple fields
     *
     * @throws KeyNotFoundException
     */
    void deleteArtworkField() throws KeyNotFoundException;


    /**
     * Create artwork field based on the data in artwork
     *
     * @param artwork
     * @return suitable tagfield for this format that represents the artwork data
     * @throws FieldDataInvalidException
     */
    TagField createField(Artwork artwork) throws FieldDataInvalidException;

    /**
     * Create artwork field based on the data in artwork and then set it in the tag itself
     *
     *
     * @param artwork
     * @throws FieldDataInvalidException
     */
    void setField(Artwork artwork) throws FieldDataInvalidException;

    /**
     * Create artwork field based on the data in artwork and then add it to the tag itself
     *
     *
     * @param artwork
     * @throws FieldDataInvalidException
     */
    void addField(Artwork artwork) throws FieldDataInvalidException;

    /**
     * Sets a field in the structure, used internally by the library<br>
     *
     *
     * @param field The field to add.
     * @throws FieldDataInvalidException
     */
    void setField(TagField field) throws FieldDataInvalidException;

    /**
     * Adds a field to the structure, used internally by the library<br>
     *
     *
     * @param field The field to add.
     * @throws FieldDataInvalidException
     */
    void addField(TagField field) throws FieldDataInvalidException;

    /**
     * Create a new field based on generic key, used internally by the library
     *
     * <p>Only textual data supported at the moment. The genericKey will be mapped
     * to the correct implementation key and return a TagField.
     *
     * Usually the value field should only be one value, but certain fields may require more than one value
     * currently the only field to require this is the MUSICIAN field, it should contain instrument and then
     * performer name
     *
     * @param genericKey is the generic key
     * @param value      to store
     * @return
     * @throws KeyNotFoundException
     * @throws FieldDataInvalidException
     */
    TagField createField(FieldKey genericKey, String... value) throws KeyNotFoundException, FieldDataInvalidException;

    /**
     * Creates isCompilation field
     *
     * It is useful to have this method because it handles ensuring that the correct value to represent a boolean
     * is stored in the underlying field format.
     *
     * @param value
     * @return
     * @throws KeyNotFoundException
     * @throws FieldDataInvalidException
     */
    TagField createCompilationField(boolean value) throws KeyNotFoundException, FieldDataInvalidException;

}

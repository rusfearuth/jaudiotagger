/**
 * @author : Paul Taylor
 *
 * Version @version:$Id$
 * Date :${DATE}
 *
 * Jaikoz Copyright Copyright (C) 2003 -2005 JThink Ltd
 */
package org.jaudiotagger.audio.exceptions;

/**
 * Thrown when bytes expected to represent an audio frame are invalid.
 */
public class InvalidAudioFrameException extends Exception
{
    /**
	 * 
	 */
	private static final long serialVersionUID = 7213597113547233971L;

	/**
     * Creates an instance with details.
     *
     * @param message error details.
     */
	public InvalidAudioFrameException(String message)
    {
        super(message);
    }
}

package org.jaudiotagger.audio.exceptions;

/**
 * Specific {@link CannotWriteException} indicating write failure caused by missing file permissions.
 */
public class NoWritePermissionsException extends CannotWriteException
{
    /**
	 * 
	 */
	private static final long serialVersionUID = -156467854598317547L;

	/**
     * Creates an instance.
     */
    public NoWritePermissionsException()
    {
        super();
    }

    public NoWritePermissionsException(Throwable ex)
    {
        super(ex);
    }

    /**
     * Creates an instance.
     *
     * @param message The message.
     */
    public NoWritePermissionsException(String message)
    {
        super(message);
    }

    /**
     * Creates an instance.
     *
     * @param message The error message.
     * @param cause   The throwable causing this exception.
     */
    public NoWritePermissionsException(String message, Throwable cause)
    {
        super(message, cause);
    }
}

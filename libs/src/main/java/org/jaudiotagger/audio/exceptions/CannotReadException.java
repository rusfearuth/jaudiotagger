/*
 * Entagged Audio Tag library
 * Copyright (c) 2003-2005 Raphaël Slinckx <raphael@slinckx.net>
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
package org.jaudiotagger.audio.exceptions;

/**
 * Thrown when audio metadata or header information cannot be read.
 *
 * <p>Typical causes include unsupported format payload, malformed frames/chunks, and I/O failures.</p>
 *
 * @author Raphaël Slinckx
 */
public class CannotReadException extends Exception
{
    /**
	 * 
	 */
	private static final long serialVersionUID = 8012136673806032717L;

	/**
     * Creates an instance.
     */
    public CannotReadException()
    {
        super();
    }

    /**
     * Creates an instance with root cause.
     *
     * @param ex root cause.
     */
    public CannotReadException(Throwable ex)
    {
        super(ex);
    }

    /**
     * Creates an instance with details.
     *
     * @param message error details.
     */
    public CannotReadException(String message)
    {
        super(message);
    }

    /**
     * Creates an instance.
     *
     * @param message The error message.
     * @param cause   The throwable causing this exception.
     */
    public CannotReadException(String message, Throwable cause)
    {
        super(message, cause);
    }
}

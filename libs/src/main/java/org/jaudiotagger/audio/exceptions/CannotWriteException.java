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
 * Thrown when metadata cannot be persisted to an audio file.
 *
 * <p>Typical causes include unsupported output format, permission problems, and I/O failures.</p>
 *
 * @author Raphaël Slinckx
 */
public class CannotWriteException extends Exception
{
    /**
	 * 
	 */
	private static final long serialVersionUID = -4477951875399481164L;

    /**
     * Creates an instance.
     */
    public CannotWriteException()
    {
        super();
    }

    /**
     * Creates an instance with details.
     *
     * @param message error details.
     */
    public CannotWriteException(String message)
    {
        super(message);
    }

    /**
     * Creates an instance with details and root cause.
     *
     * @param message error details.
     * @param cause root cause.
     */
    public CannotWriteException(String message, Throwable cause)
    {
        super(message, cause);
    }

    /**
     * Creates an instance with root cause.
     *
     * @param cause root cause.
     */
    public CannotWriteException(Throwable cause)
    {
        super(cause);

    }

}

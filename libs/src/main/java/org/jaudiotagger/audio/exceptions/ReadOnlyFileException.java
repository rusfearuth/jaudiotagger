/**
 *  @author : Paul Taylor
 *  @author : Eric Farng
 *
 *  Version @version:$Id$
 *
 *  MusicTag Copyright (C)2003,2004
 *
 *  This library is free software; you can redistribute it and/or modify it under the terms of the GNU Lesser
 *  General Public  License as published by the Free Software Foundation; either version 2.1 of the License,
 *  or (at your option) any later version.
 *
 *  This library is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even
 *  the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 *  See the GNU Lesser General Public License for more details.
 *
 *  You should have received a copy of the GNU Lesser General Public License along with this library; if not,
 *  you can get a copy from http://www.opensource.org/licenses/lgpl-license.php or write to the Free Software
 *  Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA
 *
 */
package org.jaudiotagger.audio.exceptions;

/**
 * Thrown when an operation requires write access but target file is read-only.
 */
public class ReadOnlyFileException extends Exception
{
    /**
	 * 
	 */
	private static final long serialVersionUID = 3390133566776688874L;

    /**
     * Creates an instance.
     */
    public ReadOnlyFileException()
    {
    }

    /**
     * Creates an instance with root cause.
     *
     * @param ex root cause.
     */
    public ReadOnlyFileException(Throwable ex)
    {
        super(ex);
    }

    /**
     * Creates an instance with details.
     *
     * @param msg error details.
     */
    public ReadOnlyFileException(String msg)
    {
        super(msg);
    }

    /**
     * Creates an instance with details and root cause.
     *
     * @param msg error details.
     * @param ex root cause.
     */
    public ReadOnlyFileException(String msg, Throwable ex)
    {
        super(msg, ex);
    }
}

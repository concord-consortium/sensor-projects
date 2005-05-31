
/*
 *  Copyright (C) 2004  The Concord Consortium, Inc.,
 *  10 Concord Crossing, Concord, MA 01741
 *
 *  Web Site: http://www.concord.org
 *  Email: info@concord.org
 *
 *  This library is free software; you can redistribute it and/or
 *  modify it under the terms of the GNU Lesser General Public
 *  License as published by the Free Software Foundation; either
 *  version 2.1 of the License, or (at your option) any later version.
 *
 *  This library is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *  Lesser General Public License for more details.
 *
 *  You should have received a copy of the GNU Lesser General Public
 *  License along with this library; if not, write to the Free Software
 *  Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 *
 */

/*
 * Created on Feb 25, 2005
 *
 */
package org.concord.serialport;

import java.io.IOException;
import java.io.InputStream;

import org.concord.ftdi.FTClassicPort;

/**
 * @author scytacki
 *
 */
public class FTClassicInputStream extends InputStream 
{
	private FTClassicPort port;
	byte [] tmpBuff = new byte [1];
	
	public FTClassicInputStream(FTClassicPort port)
	{
		this.port = port;
	}
	/* (non-Javadoc)
	 * @see java.io.InputStream#read()
	 */
	public int read() 
		throws IOException 
	{
		read(tmpBuff, 0, 1);
		return tmpBuff[0] & 0xFF; 
	}

	
	public int read(byte [] buffer)
		throws IOException
	{
		return read(tmpBuff, 0, buffer.length);
	}
	
	public int read(byte [] buffer, int offset, int length)
		throws IOException
	{
		long numRead = -1;
		if(offset == 0) {
			numRead = port.read(buffer, length);
		} else {
			byte [] tmpBuffer = new byte [length];
			numRead = port.read(tmpBuffer, length);
			System.arraycopy(tmpBuffer, 0, buffer, offset, length);
		}

		return (int)numRead;
	}
}

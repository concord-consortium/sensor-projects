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

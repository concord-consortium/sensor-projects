/*
 * Created on Feb 25, 2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package org.concord.serialport;

import java.io.IOException;
import java.io.OutputStream;

import org.concord.ftdi.FTClassicPort;

/**
 * @author Informaiton Services
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class FTClassicOutputStream extends OutputStream 
{
	private FTClassicPort port;
	byte [] tmpBuf = new byte [1];
	
	public FTClassicOutputStream(FTClassicPort port)
	{
		this.port = port;
	}

	/* (non-Javadoc)
	 * @see java.io.OutputStream#write(int)
	 */
	public void write(int arg0) 
		throws IOException 
	{
		tmpBuf[0] = (byte)arg0;
		write(tmpBuf, 0, 1);
	}
	
	public void write(byte [] buffer)
		throws IOException
	{
		write(buffer, 0, buffer.length);
	}

	public void write(byte [] buffer, int offset, int length)
		throws IOException
	{
		long numWritten = -1;
		if(offset == 0) {
			numWritten = port.write(buffer, length);
		} else {
			byte [] tmpBuffer = new byte[length];
			System.arraycopy(buffer, offset, tmpBuffer, 0, length);
			numWritten = port.write(tmpBuffer, length);			
		}
		
		if(numWritten != length) {
			throw new IOException("Failed to write all the bytes");
		}
	}
}

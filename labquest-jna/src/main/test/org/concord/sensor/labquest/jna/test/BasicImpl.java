package org.concord.sensor.labquest.jna.test;

import java.io.IOException;

public class BasicImpl implements Basic
{
	public int print(String string)
	{
		System.out.println(string);
		return string.length();
	}

	public void throwException() throws IOException
	{
		throw new IOException("Test Exception");		
	}
	
	public int getInt()
	{
		return 1;
	}
	
	public void close()
	{
		System.out.println("closing");
	}	
}

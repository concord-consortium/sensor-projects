package org.concord.sensor.labquest.jna.test;

import java.io.IOException;


public interface Basic {
	public int print(String str);
	
	public void throwException() throws IOException;
	
	public int getInt();
	
	public void close();
}

/*
 * Last modification information:
 * $Revision: 1.1 $
 * $Date: 2005-02-23 18:04:18 $
 * $Author: scytacki $
 *
 * Licence Information
 * Copyright 2004 The Concord Consortium 
*/
package org.concord.sensor.waba;


public class SensorDeviceMode
{
	int port;
	int mode;

	public SensorDeviceMode(int port, int mode)
	{
		this.port = port;
		this.mode = mode;
	}
	
	
	/**
	 * @return Returns the mode.
	 */
	public int getMode()
	{
		return mode;
	}
		
	/**
	 * @return Returns the port.
	 */
	public int getPort()
	{
		return port;
	}
}
/*
 * Last modification information:
 * $Revision: 1.1 $
 * $Date: 2004-12-10 07:22:02 $
 * $Author: scytacki $
 *
 * Licence Information
 * Copyright 2004 The Concord Consortium 
*/
package org.concord.sensor.device;


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
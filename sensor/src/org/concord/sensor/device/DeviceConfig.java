/*
 * Last modification information:
 * $Revision: 1.1 $
 * $Date: 2005-01-06 15:59:45 $
 * $Author: scytacki $
 *
 * Licence Information
 * Copyright 2004 The Concord Consortium 
*/
package org.concord.sensor.device;


/**
 * DeviceConfig
 * Class name and description
 *
 * Date created: Dec 1, 2004
 *
 * @author scott<p>
 *
 */
public class DeviceConfig
{
	/**
	 * An id assigned by CC for this device
	 * This id will map to a particular SensorDevice class and maybe
	 * some extra config for that device for example if the device has
	 * a native driver then the id will map to the jni device class and
	 * the native driver dll name.
	 */ 
	protected int deviceId;
	
	/**
	 * This is a string that configures this device.  It could be serial
	 * port number.  Or if it is a networked device it could be an
	 * internet address for this device.
	 */
	protected String configString;

	public DeviceConfig(int deviceId, String configString)
	{
		this.deviceId = deviceId;
		this.configString = configString;
	}
	
	public void setDeviceId(int id)
	{
		deviceId = id;
	}
	
	public int getDeviceId()
	{
		return deviceId;
	}
	
	public void setConfigString(String config)
	{
		configString = config;
	}
	
	public String getConfigString()
	{
		return configString;
	}
}

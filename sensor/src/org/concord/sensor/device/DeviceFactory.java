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
 * DeviceFactory
 * Class name and description
 *
 * Date created: Dec 1, 2004
 *
 * @author scott<p>
 *
 */
public interface DeviceFactory
{
	// This should open the device before returning it.
	SensorDevice createDevice(DeviceConfig config);
	
	// This should close and clean up the device.
	void destroyDevice(SensorDevice device);
}

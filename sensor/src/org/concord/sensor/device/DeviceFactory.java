/*
 * Last modification information:
 * $Revision: 1.2 $
 * $Date: 2005-01-12 04:13:22 $
 * $Author: scytacki $
 *
 * Licence Information
 * Copyright 2004 The Concord Consortium 
*/
package org.concord.sensor.device;

import org.concord.sensor.DeviceConfig;


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

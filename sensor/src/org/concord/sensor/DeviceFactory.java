/*
 * Last modification information:
 * $Revision: 1.2 $
 * $Date: 2004-12-13 07:16:33 $
 * $Author: scytacki $
 *
 * Licence Information
 * Copyright 2004 The Concord Consortium 
*/
package org.concord.sensor;

import org.concord.framework.text.UserMessageHandler;


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
	SensorDevice createDevice(DeviceConfig config, UserMessageHandler messager);
	
	// This should close and clean up the device.
	void destroyDevice(SensorDevice device);
}

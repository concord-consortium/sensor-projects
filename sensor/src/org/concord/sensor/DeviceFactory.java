/*
 * Last modification information:
 * $Revision: 1.3 $
 * $Date: 2004-12-24 15:34:59 $
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
	SensorDataProducer createDevice(DeviceConfig config, UserMessageHandler messager);
	
	// This should close and clean up the device.
	void destroyDevice(SensorDataProducer device);
}

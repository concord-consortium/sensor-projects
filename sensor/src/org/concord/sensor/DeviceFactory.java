/*
 * Last modification information:
 * $Revision: 1.1 $
 * $Date: 2004-12-10 07:22:02 $
 * $Author: scytacki $
 *
 * Licence Information
 * Copyright 2004 The Concord Consortium 
*/
package org.concord.sensor;


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
	SensorDevice createDevice(DeviceConfig config);
}

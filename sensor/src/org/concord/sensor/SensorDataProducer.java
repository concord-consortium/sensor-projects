/*
 * Last modification information:
 * $Revision: 1.3 $
 * $Date: 2005-01-11 06:07:02 $
 * $Author: scytacki $
 *
 * Licence Information
 * Copyright 2004 The Concord Consortium 
*/
package org.concord.sensor;

import org.concord.framework.data.stream.DataProducer;


/**
 * SensorDataProducer
 * 
 * This is a special data producer that represents a sensor device.
 * It can be configured with ExperimentRequests and it can be asked for the
 * current configuration.
 * 
 * Generally the SensorDataManager is used to create these data producers.
 *
 * Date created: Nov 30, 2004
 *
 * @author scott<p>
 *
 */
public interface SensorDataProducer
	extends DataProducer
{
	/**
	 * This determines if the deivice is attached.  This is 
	 * generally called by the InterfaceManager
	 * @return
	 */
	public boolean isAttached();
	
	/**
	 * This returns the configuration attached to the interface
	 * right now.  (if it is available).  If canDetectSensors() returns
	 * true then this method should return the most acurrate list of 
	 * sensors.  If canDetectSensors is false then this will probably
	 * return the configuration most recently set on the device with the
	 * configure method.
	 * @return
	 */
	public ExperimentConfig getCurrentConfig();
	
	/**
	 * This returns true if this device can detect if sensor are attached.
	 * 
	 * @return
	 */
	public boolean canDetectSensors();
	
	/**
	 * Close the underlying device.  This is generally handled by the
	 * InterfaceManager.
	 *
	 */
	public void close();
}

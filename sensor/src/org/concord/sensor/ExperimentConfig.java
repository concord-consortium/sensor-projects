/*
 * Last modification information:
 * $Revision: 1.5 $
 * $Date: 2005-01-06 15:59:45 $
 * $Author: scytacki $
 *
 * Licence Information
 * Copyright 2004 The Concord Consortium 
*/
package org.concord.sensor;


/**
 * ExperimentConfig
 * 
 * This interface is returned by a SensorDevice.  It provides the current
 * configuration of the device and the sensors.
 *
 * Date created: Nov 30, 2004
 *
 * @author scott<p>
 *
 */
public interface ExperimentConfig
{
	/**
	 * If the set of sensors attached does not match the requested sensors then
	 * this should be false.
	 * @return
	 */
	public boolean isValid();
	
	/**
	 * If isValid is false then this should return a String that can be presented
	 * to the user.  This should be the reason the request can not be handled.
	 * The user will be given an option to try again, in which case the request
	 * will be sent again.  So the message might take this into account.
	 * @return
	 */
	public String getInvalidReason();
	
	/**
	 * The time in seconds between the returned samples.  If this isn't exact
	 * because of how the device is implemented this should return the approximate
	 * time.
	 * @return
	 */
	public float getPeriod();
	
	/**
	 * An array of SensorConfig, each SensorConfig contains configuration
	 * information about the sensor.
	 */
	public SensorConfig [] getSensorConfigs();	
	
	/**
	 * The name of the device that is handling this experiment.
	 * It could be a collection of devices.  For example the Venier
	 * GoLinks could be working together to do an experiment.  In this
	 * case the name should reflect that it is a collection of devices.
	 * The name might be presented to the user show it should be human 
	 * readable.
	 * @param name
	 */
	public String getDeviceName();
}


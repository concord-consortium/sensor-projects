/*
 * Last modification information:
 * $Revision: 1.8 $
 * $Date: 2005-02-19 13:40:31 $
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
	 * If at least one the requested sensors does not have an auto id,
	 * then this should be true.  In this case isConfirmed should be
	 * false for that sensor.
	 * If the device cannot auto id sensors then this should always be
	 * true.    
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
	 * If this returns true then the period is an exact
	 * period.  If it is false then it is an approximate
	 * period and the real time will be in the time channel
	 * returned by the SensorDataProducer. 
	 * 
	 */
	public boolean getExactPeriod();
	
	/**
	 * This is the time between read calls that the device
	 * prefers.  The units are seconds per read.  It can't be
	 * guarunteed but the call will do its best to call 
	 * read at these times. Implementors should return 
	 * smallest reasonable value.  Most likely the data will be displayed
	 * in realtime, so a faster response time is better.
	 * 
	 * @return
	 */
	public float getDataReadPeriod();
		
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


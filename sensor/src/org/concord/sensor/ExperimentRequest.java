/*
 * Created on Dec 13, 2004
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package org.concord.sensor;

/**
 * An object implementing this interface is passed to the SensorDevice
 * The SensorDevice uses this request to figure out how to configure
 * the device and sensors.
 * 
 * @author Scott Cytacki
 *
 */
public interface ExperimentRequest 
{
	/**
	 * The length of time between measurements in seconds
	 */ 
	public float getPeriod();
	
	/**
	 * the sensor requests for this sensor.
	 * @return
	 */
	public SensorRequest [] getSensorRequests();		
}

/*
 * Last modification information:
 * $Revision: 1.3 $
 * $Date: 2005-03-07 04:24:27 $
 * $Author: scytacki $
 *
 * Licence Information
 * Copyright 2004 The Concord Consortium 
*/
package org.concord.sensor;



/**
 * SensorDataManager
 * 
 * This Manager is used to create SensorDataProducers.  
 * Usually a class that implements this interface will be retrieved from 
 * an underlying configuration system.  The only current implementation is
 * org.concord.sensor.device.impl.InterfaceManager
 *
 * Date created: Jan 4, 2005
 *
 * @author scott<p>
 *
 */
public interface SensorDataManager
{
	/**
	 * A request is sent in allong with a data consumer.  A SensorDataProducer
	 * is created and then passed to the consumer.  This method
	 * does not directly return a SensorDataProducer because it might take
	 * a while to create and initialize it.   
	 * 
	 * @param request
	 * @param consumer
	 */
	public SensorDataProducer createDataProducer();
	
	/**
	 * This should return a sensordataproducer for all the currently 
	 * attached devices.  This method currently is not implemented.  When it 
	 * is implement it might take a long time to return. 
	 * @return
	 */
	public SensorDataProducer [] getAttachedDevices();
}

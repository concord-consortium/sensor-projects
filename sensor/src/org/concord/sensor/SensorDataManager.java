/*
 * Last modification information:
 * $Revision: 1.2 $
 * $Date: 2005-02-28 21:37:49 $
 * $Author: dmarkman $
 *
 * Licence Information
 * Copyright 2004 The Concord Consortium 
*/
package org.concord.sensor;

import org.concord.framework.data.stream.DataConsumer;


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
	public void prepareDataProducer(ExperimentRequest request, 
			DataConsumer consumer);
	
	/**
	 * A request is sent in allong with a data consumer.  A SensorDataProducer
	 * is created and then passed to the consumer.  This method
	 * does not directly return a SensorDataProducer because it might take
	 * a while to create and initialize it.   
	 * 
	 * @param request
	 */
	public SensorDataProducer prepareDataProducer(ExperimentRequest requestconsumer);
	
	/**
	 * This should return a sensordataproducer for all the currently 
	 * attached devices.  This method currently is not implemented.  When it 
	 * is implement it might take a long time to return. 
	 * @return
	 */
	public SensorDataProducer [] getAttachedDevices();
}

/*
 * Last modification information:
 * $Revision: 1.1 $
 * $Date: 2005-02-23 23:44:30 $
 * $Author: dmarkman $
 *
 * Licence Information
 * Copyright 2004 The Concord Consortium 
*/
package org.concord.sensor;

import org.concord.framework.data.stream.DataConsumer;


/**
 * SensorDataProducer
 * 
 * This is a special data consumer that has
 * additional API to retreive SensorDataConsumer
 *
 * Date created: Feb. 23, 2005
 *
 * @author dima<p>
 *
 */
public interface SensorDataConsumer
	extends DataConsumer
{

	/**
	 * This return SensorDataProducer associated with 
	 * that consumer
	 * @return
	 */
    public SensorDataProducer getSensorDataProducer();


}

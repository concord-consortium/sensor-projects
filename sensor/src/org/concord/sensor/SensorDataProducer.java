/*
 * Last modification information:
 * $Revision: 1.1 $
 * $Date: 2004-12-24 15:34:59 $
 * $Author: scytacki $
 *
 * Licence Information
 * Copyright 2004 The Concord Consortium 
*/
package org.concord.sensor;

import org.concord.framework.data.stream.DataProducer;


/**
 * SensorDevice
 * Class name and description
 *
 * Date created: Nov 30, 2004
 *
 * @author scott<p>
 *
 */
public interface SensorDataProducer
	extends DataProducer
{
	public boolean isAttached();
	
	public ExperimentConfig configure(ExperimentRequest experiment);
	
	/**
	 * This returns the configuration attached to the interface
	 * right now.  (if it is available)
	 * @return
	 */
	public ExperimentConfig getCurrentConfig();
	
	public boolean canDetectSensors();
	
	public void close();
}

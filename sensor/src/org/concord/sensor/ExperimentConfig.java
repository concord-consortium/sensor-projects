/*
 * Last modification information:
 * $Revision: 1.4 $
 * $Date: 2004-12-18 07:01:31 $
 * $Author: scytacki $
 *
 * Licence Information
 * Copyright 2004 The Concord Consortium 
*/
package org.concord.sensor;


/**
 * ExperimentConfig
 * Class name and description
 *
 * Date created: Nov 30, 2004
 *
 * @author scott<p>
 *
 */
public interface ExperimentConfig
{
	public boolean isValid();
	
	public String getInvalidReason();
	
	public float getPeriod();
	
	public SensorConfig [] getSensorConfigs();	
	
	/**
	 * The name of the device that is handling this experiment.
	 * It could be a collection of devices.  For example the Venier
	 * GoLinks could be working together to do an experiment.  In this
	 * case the name should reflect that.
	 * @param name
	 */
	public String getDeviceName();
}


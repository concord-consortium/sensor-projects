/*
 * Last modification information:
 * $Revision: 1.3 $
 * $Date: 2004-12-13 17:53:55 $
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
	public void setValid(boolean valid);
	
	public String getInvalidReason();
	public void setInvalidReason(String reason);
	
	public float getPeriod();
	public void setPeriod(float period);
	
	public void setSensorConfigs(SensorConfig [] configs);
	public SensorConfig [] getSensorConfigs();	
	
	/**
	 * The name of the device that is handling this experiment.
	 * It could be a collection of devices.  For example the Venier
	 * GoLinks could be working together to do an experiment.  In this
	 * case the name should reflect that.
	 * @param name
	 */
	public void setDeviceName(String name);
	public String getDeviceName();
}


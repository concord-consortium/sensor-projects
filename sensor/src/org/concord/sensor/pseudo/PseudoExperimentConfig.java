/*
 * Created on Jan 25, 2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package org.concord.sensor.pseudo;

import org.concord.sensor.ExperimentConfig;
import org.concord.sensor.SensorConfig;

/**
 * @author scott
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class PseudoExperimentConfig 
	implements ExperimentConfig 
{
	private SensorConfig [] sensorConfigs = null;
	
	/* (non-Javadoc)
	 * @see org.concord.sensor.ExperimentConfig#isValid()
	 */
	public boolean isValid() 
	{
		return true;
	}

	/* (non-Javadoc)
	 * @see org.concord.sensor.ExperimentConfig#getInvalidReason()
	 */
	public String getInvalidReason() 
	{
		return null;
	}

	/* (non-Javadoc)
	 * @see org.concord.sensor.ExperimentConfig#getPeriod()
	 */
	public float getPeriod() 
	{
		return 0.1f;
	}
	
	/* (non-Javadoc)
	 * @see org.concord.sensor.ExperimentConfig#getSensorConfigs()
	 */
	public SensorConfig[] getSensorConfigs() 
	{
		return sensorConfigs;
	}

	public void setSensorConfigs(SensorConfig [] sensorConfigs)
	{
		this.sensorConfigs = sensorConfigs;
	}
	
	/* (non-Javadoc)
	 * @see org.concord.sensor.ExperimentConfig#getDeviceName()
	 */
	public String getDeviceName() 
	{
		return "Pseudo Device";
	}

}

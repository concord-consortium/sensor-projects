/*
 * Created on Jan 25, 2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package org.concord.sensor.device.impl;

import org.concord.sensor.ExperimentConfig;
import org.concord.sensor.SensorConfig;

/**
 * @author scott
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class ExperimentConfigImpl 
	implements ExperimentConfig 
{
	private SensorConfig [] sensorConfigs = null;
	private boolean valid;
	private String invalidReason;
	private float period;
    private boolean exactPeriod;
    private String deviceName;
	private int deviceId;
    private float dataReadPeriod;
    
	/* (non-Javadoc)
	 * @see org.concord.sensor.ExperimentConfig#isValid()
	 */
	public boolean isValid() 
	{
		return valid;
	}

	public void setValid(boolean valid)
	{
	    this.valid = valid;
	}
	
	/* (non-Javadoc)
	 * @see org.concord.sensor.ExperimentConfig#getInvalidReason()
	 */
	public String getInvalidReason() 
	{
		return invalidReason;
	}

	public void setInvalidReason(String reason)
	{
	    invalidReason = reason;
	}
	/* (non-Javadoc)
	 * @see org.concord.sensor.ExperimentConfig#getPeriod()
	 */
	public float getPeriod() 
	{
		return period;
	}
	
	public void setPeriod(float period)
	{
	    this.period = period; 
	}
	
	/* (non-Javadoc)
	 * @see org.concord.sensor.ExperimentConfig#getExactPeriod()
	 */
	public boolean getExactPeriod() 
	{
		return exactPeriod;
	}
	
	public void setExactPeriod(boolean exact)
	{
	    exactPeriod = exact;
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
		return deviceName;
	}

	public void setDeviceName(String name)
	{
	    deviceName = name;
	}
	
	/**
     * @return Returns the deviceId.
     */
    public int getDeviceId()
    {
        return deviceId;
    }
    
    /**
     * @param deviceId The deviceId to set.
     */
    public void setDeviceId(int deviceId)
    {
        this.deviceId = deviceId;
    }
    
    /* (non-Javadoc)
     * @see org.concord.sensor.ExperimentConfig#getDataReadPeriod()
     */
    public float getDataReadPeriod()
    {
        return dataReadPeriod;
    }
    
    /**
     * @param dataReadPeriod The dataReadPeriod to set.
     */
    public void setDataReadPeriod(float dataReadPeriod)
    {
        this.dataReadPeriod = dataReadPeriod;
    }
}

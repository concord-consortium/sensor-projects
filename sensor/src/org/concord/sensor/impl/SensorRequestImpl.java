package org.concord.sensor.impl;

import org.concord.framework.data.DataDimension;
import org.concord.sensor.SensorRequest;


public class SensorRequestImpl
	implements SensorRequest
{
	protected int type = 0;
	protected float stepSize = 0.1f;
	protected float requiredMax;
	protected float requiredMin;
	protected int displayPrecision;
	protected int port;
	protected DataDimension unit;
	
	// we can't use hashtables because we want this class
	// to be portable to waba
	String [] paramKeys;
	String [] paramValues;
	
	public int getType()
	{
		return type;
	}
	
	public void setType(int type)
	{
		this.type = type;
	}
	
	public float getStepSize()
	{
		return stepSize;
	}
	
	public void setStepSize(float stepSize)
	{
		this.stepSize = stepSize;
	}
	
	public float getRequiredMax()
	{
		return requiredMax;
	}
	
	public void setRequiredMax(float requiredMax)
	{
		this.requiredMax = requiredMax;
	}
	
	public float getRequiredMin()
	{
		return requiredMin;
	}
	
	public void setRequiredMin(float requiredMin)
	{
		this.requiredMin = requiredMin;
	}
	
	public int getDisplayPrecision()
	{
		return displayPrecision;
	}
	
	public void setDisplayPrecision(int displayPrecision)
	{
		this.displayPrecision = displayPrecision;
	}
	
	public int getPort()
	{
		return port;
	}
	
	public void setPort(int port)
	{
		this.port = port;
	}
	
	public DataDimension getUnit()
	{
		return unit;
	}
	
	public void setUnit(DataDimension unit)
	{
		this.unit = unit;
	}
	
	public String getSensorParam(String key)
	{
		if(paramKeys == null) {
			return null;
		}
		
		for(int i=0; i<paramKeys.length; i++) {
			if(paramKeys[i].equals(key)) {
				return paramValues[i];
			}
		}

		return null;
	}

	public String [] getSensorParamKeys()
	{
		return paramKeys;
	}
	
	public void setSensorParams(String [] keys, String [] values)
	{
		paramKeys = keys;
		paramValues = values;
	}
}

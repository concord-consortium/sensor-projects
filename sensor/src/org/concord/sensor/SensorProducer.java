package org.concord.sensor;

import org.concord.waba.extra.io.*;
import org.concord.framework.text.UserMessageHandler;

public abstract class SensorProducer
{
	protected String [] sensorNames;
	protected String [] interfaceNames;
	protected Ticker ticker;
	protected UserMessageHandler messageHandler;
	
	abstract public short getId();
	
	public boolean isValidSensorType(short sensorType)
	{
		return sensorType >= 0 &&
			sensorType < sensorNames.length;
	}

	public Sensor loadSensor(short sensorType, DataStream in)
	{
		Sensor sensor = createSensor(false, sensorType);
		if(sensor != null) sensor.readExternal(in);
		return  sensor;		
	}

	public void saveSensor(Sensor sensor, DataStream out)
	{
		sensor.writeExternal(out);		
	}

	/**
	 *  When the interface of this probe is set 
	 *  we might need to add a port property
	 */
	public Sensor createSensor(short sensorType)
	{
		return createSensor(true, sensorType);
	}

    public String getSensorName(short sensorType)
    {
    	if(isValidSensorType(sensorType)) {
    		return null;
    	}

    	return sensorNames[sensorType];
    }

    public short getSensorType(String name)
    {
		for(short i=0; i<sensorNames.length; i++){
			if(sensorNames[i].equals(name)){
				return i;
			}
		}

		return -1;
    }

    /**
     * This should be replaced by a sensor lookup system
     * a user should be able to say I need a sensor with these
     * properties.  This data for this lookup system should be
     * stored outside of the code. (text files, database...)
     * 
     * @return
     */
    public String [] getSensorNames()
    {
		return sensorNames;
    }

	public abstract Sensor createSensor(boolean init, short sensorType);

    public int getInterfaceId(String name)
    {
		for(int i=0; i<interfaceNames.length; i++){
			if(interfaceNames[i].equals(name)){
				return i;
			}
		}

		return -1;
    }

    public String [] getInterfaceNames()
    {
		return interfaceNames;
    }

    public abstract InterfaceManager createInterface(int id);
    
    public void setTicker(Ticker t)
    {
    	ticker = t;
    }
    
    public void setUserMessageHandler(UserMessageHandler h)
    {
    	messageHandler = h;
    }
}

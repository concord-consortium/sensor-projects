package org.concord.sensor;

import org.concord.framework.text.UserMessageHandler;

import waba.util.Vector;

public class InterfaceManager
{
	protected UserMessageHandler messageHandler;
	protected 		waba.util.Vector 	dataListeners = null;
	
	protected waba.util.Vector sensorConfigs = new waba.util.Vector();
	
	private boolean prepared;
		
	public InterfaceManager(UserMessageHandler h)
	{
		messageHandler = h;
	}
		
	/**
	 * This method is used to add requested sensor configurations
	 * to the interface.  After they have been added the interface
	 * can be asked: is this valid?
	 * @param probe
	 */
	public void addSensorConfig(SensorConfig probe)
	{
		if(sensorConfigs.find(probe) < 0){
			sensorConfigs.add(probe);
			prepared = false;
		}
	}

	/**
	 * This method removes a sensor config from the interface.
	 * 
	 * @param probe
	 */
	public void removeSensorConfig(SensorConfig probe)
	{
		int index = sensorConfigs.find(probe);
		if(index >= 0) {
			sensorConfigs.del(index);
			prepared = false;
		}		
	}
		
	public void removeAllSensorConfigs()
	{		
		sensorConfigs = new Vector();
		prepared = false;
	}
	
	/**
	 * This returns the configuration attached to the interface
	 * right now.  (if it is available)
	 * @return
	 */
	public SensorConfig [] getAutoConfiguration()
	{
		return null;
	}
	
	/**
	 * @return
	 */
	private boolean configure()
	{
		// TODO Auto-generated method stub
		return true;
	}
}

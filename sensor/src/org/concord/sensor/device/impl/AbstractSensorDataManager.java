/*
 * Last modification information:
 * $Revision: 1.1 $
 * $Date: 2006-05-05 15:44:30 $
 * $Author: scytacki $
 *
 * Licence Information
 * Copyright 2004 The Concord Consortium 
*/
package org.concord.sensor.device.impl;

import org.concord.framework.text.UserMessageHandler;
import org.concord.sensor.DeviceConfig;
import org.concord.sensor.SensorDataManager;
import org.concord.sensor.device.DeviceFactory;
import org.concord.sensor.device.DeviceService;
import org.concord.sensor.device.SensorDevice;
import org.concord.sensor.impl.Ticker;

public abstract class AbstractSensorDataManager
    implements SensorDataManager
{
    protected static DeviceFactory deviceFactory = null;

    protected UserMessageHandler messageHandler;
    protected Ticker ticker;
        
    private DeviceConfig[] deviceConfigs;
        
    private SensorDevice currentDevice = null; 

    public AbstractSensorDataManager(UserMessageHandler msgHandler,
            Ticker ticker)
    {
        this.messageHandler = msgHandler;
        this.ticker = ticker;
    }
    
    public SensorDevice getSensorDevice()
    {
    	if(deviceConfigs == null) {
            log("Searching all possible devices isn't supported yet");
    		return null;
    	}
     
        if(currentDevice != null) {
            // check if it is attached.
            // if not then it should be closed.
            // this means we only support one device at a time
            if(ticker.isTicking()) {
                ticker.stopTicking(null);
            }
            
            if(currentDevice.isAttached()) {
                return currentDevice;
            } else {
                deviceFactory.destroyDevice(currentDevice);
                currentDevice = null;                
            }
        }
        
        String lastError = null;
        for(int i=0; i<deviceConfigs.length; i++) {
            SensorDevice device = 
                deviceFactory.createDevice(deviceConfigs[i]);
            if(device.isAttached()){
                currentDevice = device;
                return currentDevice;
            }
            
            lastError = device.getErrorMessage(0);
            if(lastError == null) {
                lastError = "null";
            }
            
            deviceFactory.destroyDevice(device);
        }

        // If we got here then it means there was no device
        // found, we'll print the error from the last device
        // searched for.
        if(messageHandler != null) {
            messageHandler.showMessage("Can't find device: " + lastError, "Device Not Found");
        }

        return currentDevice;        
    }
    
    /**
     * This returns all the device configs that have been set.  
     * These configs are used to determine the available devices.
     * @return
     */
    public DeviceConfig [] getDeviceConfigs()
    {
        return deviceConfigs; 
    }
    
    /**
     * Set the list of device configs.  This list is used when
     * prepareDataProducer is called.  The list is traversed and the 
     * first available device is used.  If the list is null then all 
     * devices available from the DeviceFactory are checked for.  
     * 
     * @param configs
     */
    public void setDeviceConfigs(DeviceConfig [] configs)
    {
        deviceConfigs = configs;
    }
    
    /**
     * This factory is used to actually create the SensorDataProducers.
     * 
     * 
     * @param factory
     */
    public static void setDeviceFactory(DeviceFactory factory)
    {
        deviceFactory = factory;
    }
    
    protected void log(String message)
    {
        if(deviceFactory instanceof DeviceService) {
            ((DeviceService)deviceFactory).log(message);
        }
    }
}

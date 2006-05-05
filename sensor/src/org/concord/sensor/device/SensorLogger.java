/*
 * Last modification information:
 * $Revision: 1.1 $
 * $Date: 2006-05-05 15:44:30 $
 * $Author: scytacki $
 *
 * Licence Information
 * Copyright 2004 The Concord Consortium 
*/
package org.concord.sensor.device;

import org.concord.sensor.DeviceTime;
import org.concord.sensor.LoggingRequest;

public interface SensorLogger
    extends SensorDevice
{
    /**
     * Get the records avaiable on this device
     * @return
     */
    public SensorLoggedRecord [] getAvailableRecords();
        
    /**
     * Send a request to setup a log to this device.
     * @param request
     */
    public void sendLoggingRequest(LoggingRequest request);
        
    /**
     * This reads the current time from the device.  This is important for logging
     * because triggered and delayed start logs rely on the clock of the device.
     * 
     * If the device doesn't support this feature it should return null here.
     * @return
     */
    public DeviceTime getLoggerCurrentTime();
    
    /**
     * This sets the current time in the device.
     * @param time
     */
    public void setLoggerCurrentTime(DeviceTime time);
}

/*
 * Last modification information:
 * $Revision: 1.1 $
 * $Date: 2006-02-22 21:38:13 $
 * $Author: scytacki $
 *
 * Licence Information
 * Copyright 2004 The Concord Consortium 
*/
package org.concord.sensor.device;

import org.concord.sensor.serial.SensorSerialPort;

public interface DeviceServiceProvider
{
    public void log(String message);
    
    public void sleep(int millis);
    
    public SensorSerialPort getSerialPort(String name, SensorSerialPort oldPort);
}

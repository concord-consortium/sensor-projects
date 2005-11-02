package org.concord.sensor.serial;

import org.concord.sensor.device.impl.JavaDeviceFactory;


public class JavaSerialPortFactory
	implements SerialPortFactory
{

	public SensorSerialPort getSerialPort(String name, SensorSerialPort oldPort)
	{
		String portClassName = null;
		
		if(name.equals("ftdi")) {
			portClassName = "org.concord.sensor.dataharvest.SensorSerialPortFTDI";
		} else if(name.equals("os")){			
			portClassName = "org.concord.sensor.serial.SensorSerialPortRXTX";
		}
		
		try {		    
			Class portClass = getClass().getClassLoader().loadClass(portClassName);
			
			if(!portClass.isInstance(oldPort)){
				return(SensorSerialPort) portClass.newInstance();
			} else {
				return oldPort;
			}
		} catch (Exception e) {
			System.err.println("Can't load serial port driver class: " +
					portClassName);
		}
		
		return null;
	}

	public int getOSType()
	{
		if(System.getProperty("os.name").startsWith("Windows")) {
			return WINDOWS;
		}
			
		return UNKNOWN;		
	}
}

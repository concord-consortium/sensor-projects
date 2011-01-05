package org.concord.sensor.device.impl;

import org.concord.sensor.device.DeviceService;
import org.concord.sensor.serial.SensorSerialPort;

public abstract class JavaDeviceService implements DeviceService {

	public int getOSType() {
	    String osName = System.getProperty("os.name");
	    if(osName.startsWith("Windows")){
	        return OS_WINDOWS;
	    }
	    if(osName.startsWith("Linux")){
	        return OS_LINUX;
	    }
	    if(osName.startsWith("Mac OS X")){
	        return OS_OSX;
	    }
	    
	    return OS_UNKNOWN;
	}

	public SensorSerialPort getSerialPort(String name, SensorSerialPort oldPort) {
	    String portClassName = null;
	    
	    if(FTDI_SERIAL_PORT.equals(name)){
	        portClassName = "org.concord.sensor.dataharvest.SensorSerialPortFTDI";
	    } else if(OS_SERIAL_PORT.equals(name)) {
	        portClassName = "org.concord.sensor.serial.SensorSerialPortRXTX";
	    } else if(LABPROUSB_SERIAL_PORT.equals(name)) {
	        portClassName = "org.concord.sensor.vernier.labpro.SensorSerialPortLabProUSB";        	
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

	public void sleep(int millis) {
	    try{
	        Thread.sleep(millis);
	    } catch (InterruptedException e){
	        e.printStackTrace();
	    }
	    
	}

	public long currentTimeMillis() {
	    return System.currentTimeMillis();
	}

	public float intBitsToFloat(int valueInt) {
	    return Float.intBitsToFloat(valueInt);
	}

	public boolean isValidFloat(float val) {
	    return !Float.isNaN(val);
	}

}

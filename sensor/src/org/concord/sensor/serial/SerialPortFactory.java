package org.concord.sensor.serial;


public interface SerialPortFactory
{
	public final static int UNKNOWN = 0;
	public final static int LINUX = 1;
	public final static int OSX = 2;
	public final static int WINDOWS = 3;
	public final static int PALMOS = 4;
	public final static int WINCE = 4;	
	
	public SensorSerialPort getSerialPort(String name, SensorSerialPort oldPort);
	
	public int getOSType();
}

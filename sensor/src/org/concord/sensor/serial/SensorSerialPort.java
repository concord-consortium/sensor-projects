/*
 * Created on Feb 25, 2005
 *
 */
package org.concord.sensor.serial;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * @author scytacki
 *
 */
public interface SensorSerialPort 
{	
	public static final int  FLOWCONTROL_NONE       =0;
	public static final int  FLOWCONTROL_RTSCTS_IN  =1;
	public static final int  FLOWCONTROL_RTSCTS_OUT =2;
	public static final int  FLOWCONTROL_XONXOFF_IN =4;
	public static final int  FLOWCONTROL_XONXOFF_OUT=8;

	public abstract void open(String portName)
		throws IOException;
	
	public abstract void close()
		throws IOException;
	
	public abstract void setSerialPortParams( int b, int d, int s, int p )
		throws IOException;
	public abstract int getBaudRate();
	public abstract int getDataBits();
	public abstract int getStopBits();
	public abstract int getParity();
	public abstract void setFlowControlMode( int flowcontrol )
		throws IOException;

	public abstract void disableReceiveTimeout();
	public abstract void enableReceiveTimeout( int time )
		throws IOException;

	public abstract InputStream getInputStream() throws IOException;
	public abstract OutputStream getOutputStream() throws IOException;
}

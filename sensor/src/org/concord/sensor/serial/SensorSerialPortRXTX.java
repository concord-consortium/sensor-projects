/*
 * Created on Feb 25, 2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package org.concord.sensor.serial;

import gnu.io.CommPortIdentifier;
import gnu.io.RXTXCommDriver;
import gnu.io.UnsupportedCommOperationException;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Enumeration;


/**
 * @author Informaiton Services
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class SensorSerialPortRXTX 
	implements SensorSerialPort 
{
    RXTXCommDriver commDriver = null;
	gnu.io.SerialPort port = null;
		
	public void open(String portName)
		throws IOException
	{
		if(commDriver == null) {
			commDriver = new RXTXCommDriver();
			commDriver.initialize();
		}
		
		if(port != null) {
			// assert
			throw new RuntimeException("The port was not closed before being opened");
		}
		
		Enumeration ports = CommPortIdentifier.getPortIdentifiers();
		while(ports.hasMoreElements()) {
			CommPortIdentifier portID = (CommPortIdentifier)ports.nextElement();
			System.out.println("found port: " + portID.getName());
		}
		
		port = (gnu.io.SerialPort) commDriver.getCommPort(portName, 
				CommPortIdentifier.PORT_SERIAL);
		
		if(port == null) {
			throw new IOException("can't open serial port");
		}
	}
	
	/* (non-Javadoc)
	 * @see org.concord.sensor.dataharvest.DHSerialPort#close()
	 */
	public void close() throws IOException 
	{
		if(port == null) {
			throw new RuntimeException("Port was not opened before being closed");
		}
		port.close();
		port = null;
	}
	/* (non-Javadoc)
	 * @see org.concord.sensor.dataharvest.SerialPort#setSerialPortParams(int, int, int, int)
	 */
	public void setSerialPortParams(int b, int d, int s, int p)
			throws IOException 
	{
		try {
			port.setSerialPortParams(b, d, s, p);
		} catch (UnsupportedCommOperationException e) {
			throw new IOException("UnsupportedCommOperation");
		}
	}

	/* (non-Javadoc)
	 * @see org.concord.sensor.dataharvest.SerialPort#getBaudRate()
	 */
	public int getBaudRate() 
	{
		return port.getBaudRate();
	}

	/* (non-Javadoc)
	 * @see org.concord.sensor.dataharvest.SerialPort#getDataBits()
	 */
	public int getDataBits() 
	{
		return port.getDataBits();
	}

	/* (non-Javadoc)
	 * @see org.concord.sensor.dataharvest.SerialPort#getStopBits()
	 */
	public int getStopBits() 
	{
		return port.getStopBits();
	}

	/* (non-Javadoc)
	 * @see org.concord.sensor.dataharvest.SerialPort#getParity()
	 */
	public int getParity() 
	{
		return port.getParity();
	}

	/* (non-Javadoc)
	 * @see org.concord.sensor.dataharvest.SerialPort#setFlowControlMode(int)
	 */
	public void setFlowControlMode(int flowcontrol) 
		throws IOException 
	{
		try {
			port.setFlowControlMode(flowcontrol);
		} catch (UnsupportedCommOperationException e) {
			throw new IOException("UnsupportedCommOperation");
		}
	}

	/* (non-Javadoc)
	 * @see org.concord.sensor.dataharvest.SerialPort#disableReceiveTimeout()
	 */
	public void disableReceiveTimeout() 
	{
		port.disableReceiveTimeout();
	}

	/* (non-Javadoc)
	 * @see org.concord.sensor.dataharvest.SerialPort#enableReceiveTimeout(int)
	 */
	public void enableReceiveTimeout(int time) throws IOException 
	{
		try {
			port.enableReceiveTimeout(time);
		} catch (UnsupportedCommOperationException e) {
			throw new IOException("UnsupportedCommOperation");
		}
	}

	/* (non-Javadoc)
	 * @see org.concord.sensor.dataharvest.SerialPort#getInputStream()
	 */
	public InputStream getInputStream() throws IOException 
	{
		return port.getInputStream();
	}

	/* (non-Javadoc)
	 * @see org.concord.sensor.dataharvest.SerialPort#getOutputStream()
	 */
	public OutputStream getOutputStream() throws IOException 
	{
		return port.getOutputStream();
	}

}

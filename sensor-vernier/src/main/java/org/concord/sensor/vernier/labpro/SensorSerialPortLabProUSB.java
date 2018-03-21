package org.concord.sensor.vernier.labpro;

import java.io.IOException;

import org.concord.sensor.impl.Vector;
import org.concord.sensor.labprousb.jna.LabProUSB;
import org.concord.sensor.labprousb.jna.LabProUSBException;
import org.concord.sensor.labprousb.jna.LabProUSBLibrary;
import org.concord.sensor.serial.SensorSerialPort;
import org.concord.sensor.serial.SerialException;

public class SensorSerialPortLabProUSB implements SensorSerialPort 
{
	byte [] tmpBuffer = new byte [2048];

	private static LabProUSB lpusb;
	
	static {
		Runtime.getRuntime().addShutdownHook(new Thread(){
			public void run() {
				if (lpusb != null) {
					System.err.println("Closing LabProUSB.  Its open state is: " + lpusb.isOpen());
					
					// Make sure the labpro is closed
					lpusb.close();
				}
			}
		});
	}
	
	public SensorSerialPortLabProUSB() {
		
	}
	
	public void close() throws SerialException 
	{
		lpusb.close();
		lpusb = null;
	}

	public void reset() {
		if (lpusb != null) {
			lpusb.clearInputs((short)0);
		}
	}

	public void disableReceiveTimeout() 
	{
		// there are no timeouts
	}

	public void enableReceiveTimeout(int time) throws SerialException 
	{
		// this isn't supported
	}

	public Vector getAvailablePorts() 
	{		
		// TODO Auto-generated method stub
		return null;
	}

	public int getBaudRate() 
	{
		// TODO Auto-generated method stub
		return 0;
	}

	public int getDataBits() 
	{
		// TODO Auto-generated method stub
		return 0;
	}

	public int getParity() 
	{
		// TODO Auto-generated method stub
		return 0;
	}

	public int getStopBits() 
	{
		// TODO Auto-generated method stub
		return 0;
	}

	public boolean isOpen() 
	{
		if(lpusb == null){
			return false;
		}
		short open = lpusb.isOpen();
		return open == 1;
	}

	public void open(String portName) throws SerialException 
	{
		try {
			LabProUSBLibrary lplib = new LabProUSBLibrary();
			lplib.init();
			lpusb = lplib.openDevice();
			if (lpusb != null) {
				lpusb.clearInputs((short)0);
			}
			else {
				throw new SerialException("Unable to open device");
			}
		} catch (LabProUSBException e) {
			e.printStackTrace();
			throw new SerialException("Unable to open device");
		} catch (IOException e) {
			e.printStackTrace();
			throw new SerialException("Unable to initialize native library");
		} catch (InterruptedException e) {
			e.printStackTrace();
			throw new SerialException("Unable to initialize native library (interrupted)");
		} catch (UnsatisfiedLinkError e){
			e.printStackTrace();
			throw new SerialException("Can't load labprousb library", e);
		}
	}

	public void setNumChannels(int numChannels) {
		short binaryMode = 0;	// default to text mode
		short realTime = 1;		// default to real-time collection
		int clearResult = lpusb.clearInputs((short)0);
		int setResult = lpusb.setNumChannelsAndModes(numChannels, binaryMode, realTime);
	}

	/**
	 * This is not thread safe.
	 */
	public int readBytes(byte [] buf, int off, int len, long timeout)
		throws SerialException
	{
		return readBytesUntil(buf, off, len, timeout, NO_TERMINATE_BYTE);
	}

	private boolean bufferHasTerminateByte(byte [] buf, int off, int size, int terminateByte)
	{
		if ((size <= 0) || (terminateByte == NO_TERMINATE_BYTE)) {
			return false;
		}
		return buf[off + size - 1] == terminateByte;
	}

	public int readBytesUntil(byte [] buf, int off, int len, long timeout, int terminateByte)
		throws SerialException
	{	
		if (!this.isOpen()) {
			throw new SerialException("SensorSerialPortLabProUSB can't read from closed device");
		}
		int size = 0;
		int tries = 1;
	    long startTime = System.currentTimeMillis();
	    while(size != -1 && size < len && !bufferHasTerminateByte(buf, off, size, terminateByte) &&
	            (System.currentTimeMillis() - startTime) < timeout){
	    	
	    	int availableBytes = lpusb.getAvailableBytes();
	    	if(availableBytes > 0){
					// System.out.println(String.format("SensorSerialPortLabProUSB.readBytesUntil availableBytes: %d, tries: %d",
					// 										availableBytes, tries));
					int numRead = (int) lpusb.readBytes(availableBytes, tmpBuffer);
		        if(numRead < 0) {	      
		            System.err.println();
		            System.err.println("error in readBytesUntil: " + numRead);
		            
		            return numRead;
		        }
					
				// TODO: handle case when numRead exceeds remaining size of buf
				System.arraycopy(tmpBuffer, 0, buf, size+off, numRead);
					size += numRead;
	    	} 
	    	
	    	try {
				Thread.sleep(10);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			++tries;
			}
			// System.out.println(String.format("SensorSerialPortLabProUSB.readBytesUntil [readTerminate] millis: %d, hasTerminateByte: %b",
			// 																	System.currentTimeMillis() - startTime, bufferHasTerminateByte(buf, off, size, terminateByte)));
	    
			// System.out.println(String.format("SensorSerialPortLabProUSB.readBytesUntil [end] size: %d, tries: %d",
			// 										size, tries - 1));
	    return size;	
	}

	public void setFlowControlMode(int flowcontrol) throws SerialException {
		// TODO Auto-generated method stub

	}

	public void setSerialPortParams(int baud, int data, int stop, int parity)
			throws SerialException {
		// TODO Auto-generated method stub

	}

	public void write(int value) throws SerialException 
	{
		tmpBuffer[0] = (byte)value;
		write(tmpBuffer, 0, 1);
	}

	public void write(byte[] buffer) throws SerialException 
	{
		write(buffer, 0, buffer.length);
	}

	public void write(byte[] buffer, int start, int length)
			throws SerialException 
	{
		byte [] bufToWrite = buffer;
		if(start != 0){
			System.arraycopy(buffer, start, tmpBuffer, 0, length);
			bufToWrite = tmpBuffer;
		}
		
		if (!this.isOpen()) {
			throw new SerialException("SensorSerialPortLabProUSB can't write to closed device");
		}

		short numWritten = lpusb.writeBytes((short)length, bufToWrite);
		if(numWritten < length){			
			throw new SerialException("Didn't write all bytes. Wrote: " + numWritten +
					" out of: " + length);
		}		
	}

	/**
	 * This port can close and open quickly.
	 * 
	 */
	public boolean isOpenFast() 
	{
		return false;
	}
}

/*
 *  Copyright (C) 2004  The Concord Consortium, Inc.,
 *  10 Concord Crossing, Concord, MA 01742
 *
 *  Web Site: http://www.concord.org
 *  Email: info@concord.org
 *
 *  This library is free software; you can redistribute it and/or
 *  modify it under the terms of the GNU Lesser General Public
 *  License as published by the Free Software Foundation; either
 *  version 2.1 of the License, or (at your option) any later version.
 *
 *  This library is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *  Lesser General Public License for more details.
 *
 *  You should have received a copy of the GNU Lesser General Public
 *  License along with this library; if not, write to the Free Software
 *  Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 *
 * END LICENSE */

package org.concord.sensor.impl;

import java.util.Vector;

import org.concord.framework.data.stream.DataListener;
import org.concord.framework.data.stream.DataStreamDescription;
import org.concord.framework.data.stream.DataStreamEvent;
import org.concord.framework.text.UserMessageHandler;
import org.concord.sensor.ExperimentConfig;
import org.concord.sensor.ExperimentRequest;
import org.concord.sensor.SensorConfig;
import org.concord.sensor.SensorDataProducer;
import org.concord.sensor.device.DeviceReader;
import org.concord.sensor.device.SensorDevice;


public class SensorDataProducerImpl
	implements SensorDataProducer, DeviceReader, TickListener
{
	public long		startTimer =  0;
	protected Ticker ticker = null;
	protected UserMessageHandler messageHandler;
	protected Vector dataListeners = null;
	
	protected Vector sensorConfigs = new Vector();
	
	public DataStreamDescription dDesc = new DataStreamDescription();
	public DataStreamEvent	processedDataEvent = new DataStreamEvent();

	protected float [] processedData;
	private static final int DEFAULT_BUFFERED_SAMPLE_NUM = 1000;
	private boolean prepared;
	
	int timeWithoutData = 0;
	protected String [] okOptions = {"Ok"};
	protected String [] continueOptions = {"Continue"};	
	public final static int DATA_TIME_OUT = 40;
	private boolean inDeviceRead;
	private int totalDataRead;
	private SensorDevice device;
	private ExperimentConfig experimentConfig = null;
    private float dataTimeOffset;
	
	public SensorDataProducerImpl(SensorDevice device, Ticker t, UserMessageHandler h)
	{
		this.device = device;
		
		ticker = t;
		
		
		messageHandler = h;
		
		processedData = new float[DEFAULT_BUFFERED_SAMPLE_NUM];
		processedDataEvent.setData(processedData);
		processedDataEvent.setSource(this);
		processedDataEvent.setDataDescription(dDesc);
		dataTimeOffset = 0;
	}

	public void tick()
	{
	    int ret;

	    /*
		if(messageHandler != null) messageHandler.showOptionMessage(null, "Message test",			
				continueOptions, continueOptions[0]);
	    */
	    
	    // reset the total data read so we can track data coming from
	    // flushes
	    totalDataRead = 0;

		dDesc.setDataOffset(0);

	    // track when we are in the device read so if flush
	    // is called outside of this we can complain
	    inDeviceRead = true;
	    ret = device.read(processedData, 0, dDesc.getChannelsPerSample(),
	    		this);
	    inDeviceRead = false;
	    
	    if(ret < 0) {
			stop();
			String message = device.getErrorMessage(ret);
			if(message == null) {
			    message = "Error reading information from device";
			}
			if(messageHandler != null) messageHandler.showOptionMessage(message, "Interface Error",
					continueOptions, continueOptions[0]);
			return;
	    }
	    
	    totalDataRead += ret;
	    if(totalDataRead == 0) {
			// we didn't get any data. 
	    	// keep track of this so we can report there is
	    	// is a problem.  If this persists too long
			timeWithoutData++;
			if(timeWithoutData > DATA_TIME_OUT){
				stop();
				if(messageHandler != null) messageHandler.showOptionMessage("Serial Read Error: " +
										 "possibly no interface " +
										 "connected", "Interface Error",
										 continueOptions, continueOptions[0]);					
			}
			return;
	    }
	    
	    // We either got data or there was an error
		timeWithoutData = 0;

		if(ret > 0){
			// There was some data that didn't get flushed during the read
			// so send this out to our listeners.
			processedDataEvent.setNumSamples(ret);
			notifyDataListenersReceived(processedDataEvent);				
		} 	
	}
	
	public void tickStopped()
	{
	    deviceStop(false);
	}
	
	/*
	 * This is a helper method for slow devices.  It be called within deviceRead.
	 * If the data should be written into the values array passed to deviceRead
	 * the values read from the offset passed in until offset+numSamples will 
	 * be attempted to be flushed.
	 * the method returns the new offset into the data. 
	 * 
	 * You don't need to call this, but if your device is going to work on a slow
	 * computer (for example an older palm) then you will probably have to use
	 * this method.  Otherwise you will build up too much data to be processed later
	 * and then while all that data is being processed the serial buffer will overflow.
	 * 
	 * Instead this method will partially process the data.  This will give the device
	 * a better chance to "get ahead" of the serial buffer.  Once the device has gotten
	 * far enough ahead of the serial buffer it can return from deviceRead the
	 * data will be fully processed.
	 */
	public int flushData(int numSamples)
	{
		if(!inDeviceRead) {
			// error we need an assert here but we are in waba land 
			// so no exceptions or asserts for now we'll print 
			// but later we can force a null pointer exception
			System.err.println("calling flush outside of deviceRead");
		}
		
		processedDataEvent.setNumSamples(numSamples);
		notifyDataListenersReceived(processedDataEvent);
		dDesc.setDataOffset(dDesc.getDataOffset()+numSamples);
		
		totalDataRead += numSamples;
		
		return 0;
	}
	
	protected int getBufferedSampleNum()
	{
		return DEFAULT_BUFFERED_SAMPLE_NUM;
	}
	
	/**
	 * This method is called by users of the sensor
	 * device.  After the producer is created this method
	 * is called.  In some cases it is called before every
	 * start().
	 * 
	 * It might take a while to return.  It might also fail
	 * in which case it will return null, or it will return
	 * a config for which getValid() return false.
	 */
	public ExperimentConfig configure(ExperimentRequest request)
	{
	    if(ticker.isTicking()) {
	        ticker.stopTicking(this);
	    }
	    
		ExperimentConfig actualConfig = device.configure(request);
		if(actualConfig == null || !actualConfig.isValid()) {
			// prompt the user because the attached sensors do not
			// match the requested sensors.
			// It is in this case that we need more error information
			// from the device.  I suppose one solution is to get a 
			// listing of the actual sensors and then do the comparision
			// here in a general way.
			// That will work if the interface can auto identify sensors
			// if it can't then how would it know they are incorrect???
			// I guess in case it would have to check if the returned values
			// are valid.  Othwise it will just have to trust the student and
			// the experiments will have to be designed (technical hints) to help
			// the student figure out what is wrong.
			// So we will try to tackle the general error cases here :S
			// But there is now a way for the device to explain why the configuration
			// is invalid.
			System.err.println("Attached sensors don't match requested sensors");
			if(messageHandler != null) messageHandler.showMessage("Attached sensors don't match requested sensors", "Alert");
			if(actualConfig != null) {
				System.err.println("  device reason: " + actualConfig.getInvalidReason());
				SensorConfig [] sensorConfigs = actualConfig.getSensorConfigs();
				System.err.println("  sensor attached: " + sensorConfigs[0].getType());
			}
			
			// Maybe should be a policy decision somewhere
			// because maybe you would want to just return the
			// currently attached setup
		}

	    experimentConfig = actualConfig;
		DataStreamDescUtil.setupDescription(dDesc, request, actualConfig);

		DataStreamEvent event = 
		    new DataStreamEvent(DataStreamEvent.DATA_DESC_CHANGED, null, dDesc);
		event.setSource(this);
		notifyDataListenersEvent(event);
		
		return actualConfig;
	}
	
	public final void start()
	{
	    if(ticker == null) {
	        throw new RuntimeException("Null ticker object in start");
	    }
	    
	    if(ticker.isTicking()) {
	        // this is an error some other object is using
	        // this ticker, or we are trying to start it twice
	        throw new RuntimeException("Trying to start device twice");
	    }
	    
	    if(device == null) {
	        throw new RuntimeException("Null device in start");
	    }
	    
		device.start();
		
		timeWithoutData = 0;

		startTimer = System.currentTimeMillis();
		int dataReadMillis = (int)(experimentConfig.getDataReadPeriod()*1000.0);
		// Check if the data read millis is way below the experiment period
		// if it is then tick code will time out incorrectly.  So 
		// we try to correct it so that the read time is no less than
		// 1/5th of the period.
		int autoDataReadMillis = (int)(experimentConfig.getPeriod()*1000/5);
		if(dataReadMillis < autoDataReadMillis){
		    dataReadMillis = autoDataReadMillis;
		}
		ticker.startTicking(dataReadMillis, this);

	}
	
	/**
	 *  This doesn't really need to do anything if
	 * the sensor isn't storing any cache.
	 * however for sensors that need to put timestamps
	 * on the data this method should be used to 
	 * reset the timestamp
	 */
	public final void reset()
	{	
	    dataTimeOffset = 0;
	}
	
	public final void stop()
	{
		boolean ticking = ticker.isTicking();

		// just to make sure
		// even if we are not ticking just incase
		ticker.stopTicking(this);

		deviceStop(ticking);
	}

	
	protected void deviceStop(boolean ticking)
	{
		device.stop(ticking);

		// FIXME we should get the time the device sends back
		// instead of using our own time.
		dataTimeOffset += (System.currentTimeMillis() - startTimer) / 1000f;	    
	}
	
	/* (non-Javadoc)
	 * @see org.concord.sensor.SensorDataProducer#isAttached()
	 */
	public boolean isAttached()
	{
	    if(ticker != null && ticker.isTicking()) {
	        // this will have the ticker send a tickStopped event 
	        // which should cause us to stop the device
	        ticker.stopTicking(null);
	    }
	    
		return device.isAttached();
	}
	
	
	/* (non-Javadoc)
	 * @see org.concord.sensor.SensorDataProducer#canDetectSensors()
	 */
	public boolean canDetectSensors()
	{
		// TODO Auto-generated method stub
		return device.canDetectSensors();
	}
	
	public ExperimentConfig getCurrentConfig()
	{
		return device.getCurrentConfig();
	}
	
	public void close()
	{
		device.close();
	}
	
	public final DataStreamDescription getDataDescription()
	{
		return dDesc;
	}
		
	public void addDataListener(DataListener l){
		if(dataListeners == null){ 
		    dataListeners = new Vector();	   
		}
		if(!dataListeners.contains(l)){
			dataListeners.add(l);
		}
	}
	
	public void removeDataListener(DataListener l){
		if(dataListeners == null) return;
		int index = dataListeners.indexOf(l);
		if(index >= 0) dataListeners.remove(index);
		if(dataListeners.size() == 0) dataListeners = null;
	}

	public void notifyDataListenersEvent(DataStreamEvent e){
		if(dataListeners == null) return;
		for(int i = 0; i < dataListeners.size(); i++){
			DataListener l = (DataListener)dataListeners.get(i);
			l.dataStreamEvent(e);
		}
	}

	public void notifyDataListenersReceived(DataStreamEvent e)
	{
		if(dataListeners == null) return;
		
		// if the data has timestamps they should be adjusted
		// the contract for sensor devices is that time starts 
		// at 0 when start is called, however for data producers
		// time starts at 0 when reset is called.  The stop method
		// is more like a pause for data producers.
		if(dDesc.getDataType() == DataStreamDescription.DATA_SERIES){
		    // the first channel will be time.
		    for(int i=dDesc.getDataOffset(); 
		    	i < e.getNumSamples(); 
		    	i+= dDesc.getNextSampleOffset()){
		        processedData[i] += dataTimeOffset;
		    }
		}
		
		for(int i = 0; i < dataListeners.size(); i++){
			DataListener l = (DataListener)dataListeners.get(i);
			l.dataReceived(e);
		}
	}
}

package org.concord.sensor.device;

import org.concord.framework.data.stream.DataChannelDescription;
import org.concord.framework.data.stream.DataListener;
import org.concord.framework.data.stream.DataStreamDescription;
import org.concord.framework.data.stream.DataStreamEvent;
import org.concord.framework.text.UserMessageHandler;
import org.concord.sensor.ExperimentConfig;
import org.concord.sensor.SensorConfig;
import org.concord.sensor.SensorDevice;

import waba.util.Vector;

public abstract class DefaultSensorDevice
	implements SensorDevice
{
	public int		startTimer =  0;
	protected Ticker ticker = null;
	protected UserMessageHandler messageHandler;
	protected 		waba.util.Vector 	dataListeners = null;
	
	protected waba.util.Vector sensorConfigs = new waba.util.Vector();
	
	public DataStreamDescription dDesc = new DataStreamDescription();
	public DataStreamEvent	processedDataEvent = new DataStreamEvent();

	protected int [] sensorChannelIndexes;
	protected float [] processedData;
	private static final int DEFAULT_BUFFERED_SAMPLE_NUM = 1000;
	private boolean prepared;
		
	public DefaultSensorDevice(Ticker t, UserMessageHandler h)
	{
		ticker = t;
		ticker.setInterfaceManager(this);
		
		messageHandler = h;
	}
		
	public boolean isAttached()
	{
		return true;
	}
	
	public ExperimentConfig configure(ExperimentConfig experiment)
	{
		return null;
	}
	
	/**
	 * This method is used to add requested sensor configurations
	 * to the interface.  After they have been added the interface
	 * can be asked: is this valid?
	 * @param probe
	 */
	public void addSensorConfig(SensorConfig probe)
	{
		if(sensorConfigs.find(probe) < 0){
			sensorConfigs.add(probe);
			prepared = false;
		}
	}

	/**
	 * This method removes a sensor config from the interface.
	 * 
	 * @param probe
	 */
	public void removeSensorConfig(SensorConfig probe)
	{
		int index = sensorConfigs.find(probe);
		if(index >= 0) {
			sensorConfigs.del(index);
			prepared = false;
		}		
	}
		
	public void removeAllSensorConfigs()
	{		
		sensorConfigs = new Vector();
		prepared = false;
	}
	
	/**
	 * This returns the configuration attached to the interface
	 * right now.  (if it is available)
	 * @return
	 */
	public SensorConfig [] getAutoConfiguration()
	{
		return null;
	}
	
	/**
	 * prepare for a start call.  This will validate the 
	 * current sensors as best as possible and setup
	 * the datastreamdescription
	 */
	public boolean prepare()
	{
		if(prepared) {
			return true;
		}
		
		int numChannels = 0;
		int sensorChannels = 0;
		int numSensors = sensorConfigs.getCount();
		
		sensorChannelIndexes = new int [numSensors];
		
		if(!configure()) {
			// the interface was not able to configure with this
			// set of sensor configs
			return false;
		}

		int totalNumChannels = 0;
		
		for(int i=0; i<sensorConfigs.getCount(); i++) {
			// FIXME: this should get the sensor instance given
			// this configuration
			Sensor p = (Sensor)sensorConfigs.get(i);
			Object mode = p.getInterfaceMode();
			
			if(!checkMode(p, mode)) {
				return false;
			}
			
			updateMode(p);
			
			sensorChannels = p.getActiveChannels();
			sensorChannelIndexes[i] = totalNumChannels;
			totalNumChannels += sensorChannels;
		}

		// Now that all the sensors have been configured and the 
		// the mode of the interface has been updated we can calculate
		// the dt of the data that will be returned
		// it might be that the data doesn't have a dt in which case
		// the time will be sent in a separate channel
		if(hasDt()) {
			dDesc.setDataType(DataStreamDescription.DATA_SEQUENCE);
			dDesc.setDt(getDt());
		} else {
			dDesc.setDataType(DataStreamDescription.DATA_SERIES);
		}
		
		int sensorDataLength = getBufferedSampleNum()*totalNumChannels;
		if(processedData == null || 
				processedData.length < sensorDataLength) {
			processedData = new float [sensorDataLength];
			processedDataEvent.setData(processedData);
		}
		
		// Now that we know how many channels there are
		// we can configure the data description
		dDesc.setChannelsPerSample(totalNumChannels);
		int currentChannel = 0;
		for(int i=0; i<sensorConfigs.getCount(); i++) {
			Sensor sensor = (Sensor)sensorConfigs.get(i);
			
			for(int j=0; j<sensor.getActiveChannels(); j++) {
				DataChannelDescription channelDescription =
					sensor.getChannelDescription(j);

				dDesc.setChannelDescription(channelDescription, currentChannel);
				currentChannel++;
			}
		
		}
		
		prepared = true;

		return true;
	}
	
	/**
	 * @return
	 */
	private boolean configure()
	{
		// TODO Auto-generated method stub
		return true;
	}

	/**
	 * This should return the dt of this interface given the current
	 * mode that it is in.
	 * @return
	 */
	protected float getDt()
	{
		// TODO
		return (float)0.1;
	}

	/**
	 * This should return true if the current interface mode has 
	 * a valid dt;
	 * 
	 * @return
	 */
	protected boolean hasDt()
	{
		return true;
	}

	public abstract void dispose();

	protected int getBufferedSampleNum()
	{
		return DEFAULT_BUFFERED_SAMPLE_NUM;
	}
	
	/**
	 * Check where the current group of sensors is a valid group
	 * 
	 * @return
	 */
	public boolean checkMode()
	{		
		// Call some global function to get the interface mode for this probe
		// it needs to be based on the probes properties
		// But we need to watch out for stuff that currently get set by this function
		for(int i=0; i<sensorConfigs.getCount(); i++) {
			Sensor p = (Sensor)sensorConfigs.get(i);
			Object mode = p.getInterfaceMode();
			
			if(!checkMode(p, mode)) {
				return false;
			}
		}

		return true;
	}

	/**
	 * this is called to setup the interface so it is ready
	 * to read from this probe.
	 * This will set the requested mode of the interface to 
	 * match.  Then when start is called the requested mode
	 * will be sent to the interface
	 */
	abstract protected boolean updateMode(Sensor probe);

	/**
	 * This is called to see mode the probe has is a
	 * valid mode for the interface
	 */
	abstract protected boolean checkMode(Sensor probe, Object mode);

	abstract protected void tick();
	
	public abstract void start();
	/**
	 *  This doesn't really need to do anything if
	 * the sensor isn't storing any cache.
	 */
	public void reset()
	{
		
	}
	public abstract void stop();

	public DataStreamDescription getDataDescription()
	{
		return dDesc;
	}
		
	public void addDataListener(DataListener l){
		if(dataListeners == null){ dataListeners = new waba.util.Vector();	   }
		if(dataListeners.find(l) < 0){
			dataListeners.add(l);
		}
	}
	public void removeDataListener(DataListener l){
		if(dataListeners == null) return;
		int index = dataListeners.find(l);
		if(index >= 0) dataListeners.del(index);
		if(dataListeners.getCount() == 0) dataListeners = null;
	}

	public void notifyDataListenersEvent(DataStreamEvent e){
		if(dataListeners == null) return;
		for(int i = 0; i < dataListeners.getCount(); i++){
			DataListener l = (DataListener)dataListeners.get(i);
			l.dataStreamEvent(e);
		}
	}

	public void notifyDataListenersReceived(DataStreamEvent e)
	{
		if(dataListeners == null) return;
		for(int i = 0; i < dataListeners.getCount(); i++){
			DataListener l = (DataListener)dataListeners.get(i);
			l.dataReceived(e);
		}
	}
}

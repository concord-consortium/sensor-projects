/*
 * Last modification information:
 * $Revision: 1.1 $
 * $Date: 2005-02-19 13:40:31 $
 * $Author: scytacki $
 *
 * Licence Information
 * Copyright 2004 The Concord Consortium 
*/
package org.concord.sensor.device;

import org.concord.sensor.ExperimentConfig;


/**
 * AbstractJavaSensorDevice
 * Class name and description
 *
 * Date created: Dec 10, 2004
 *
 * @author scott<p>
 *
 */
public abstract class AbstractJavaSensorDeviceTmp 
	implements SensorDevice
{
	protected int [] requestedMode;
	protected int [] currentMode;

	protected Sensor [] portSensors;

	protected int [] sensorChannelIndexes;

	public AbstractJavaSensorDeviceTmp()
	{		
	}
	
	public ExperimentConfig deviceConfig(ExperimentConfig experiment)
	{
		return null;
	}
	
	/*
	 * FIXME this method is totally broken
	 *  (non-Javadoc)
	 * @see org.concord.sensor.SensorDevice#configure(org.concord.sensor.ExperimentConfig)
	 *
	public ExperimentConfig configure(ExperimentConfig experiment)
	{
		int numChannels = 0;
		int sensorChannels = 0;
		SensorConfig [] sensorConfigs = experiment.getSensorConfigs();
		int numSensors = sensorConfigs.length;
		
		sensorChannelIndexes = new int [numSensors];
		
		int totalNumChannels = 0;
		
		for(int i=0; i<numSensors; i++) {
			// FIXME: this should get the sensor instance given
			// this configuration
			SensorConfig sensConfig = sensorConfigs[i];
			
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
	*/


	/**
	 * Check where the current group of sensors is a valid group
	 * 
	 * @return
	 */
	
	/* We no longer have sensor Configs
	 * we will need to remember them ourselves.
	 * after the deviceConfig call
	 *
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
	*/

	/**
	 * This is called to see mode the probe has is a
	 * valid mode for the interface
	 */
	protected abstract boolean checkMode(Sensor probe, Object mode);


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

//	public abstract ExperimentConfig deviceConfigure(ExperimentConfig experiment);
	
	/*
	 * FIXME after the refactoring we need to look at how the
	 * devices are cleaned up when they aren't being used anymore
	 *
	public void dispose()
	{
		// This needs to notify its parents that it needs to stop
		stop();

		for(int i=0; i<portSensors.length; i++){
			portSensors[i].setInterface(null);
			portSensors[i] = null;
		}
	}
	
	protected void finalize() throws Throwable 
	{
		dispose();
	}
	*/
	
	public void removeSensor(Sensor probe)
	{
		for(int i=0; i<portSensors.length; i++) {
			if(portSensors[i] == probe){
				// notify the probe
				// stop it if it is started
				portSensors[i] = null;
			}
		}

		return;
	}

	/**
	 * this is unnecessary I think 
	 * this is called to setup the interface so it is ready
	 * to read from this probe.
	 * This will set the requested mode of the interface to 
	 * match.  Then when start is called the requested mode
	 * will be sent to the interface
	 */
	public boolean updateMode(Sensor p)
	{
		// Call some global function to get the interface mode for this probe
		// it needs to be based on the probes properties
		// But we need to watch out for stuff that currently gets set by this function
		SensorDeviceMode mode = (SensorDeviceMode)p.getInterfaceMode();

		int port = mode.getPort();
		
		// check if it is valid
		if(!checkMode(p, mode)) return false;

		requestedMode[port] = mode.getMode();

		return true;
	}

	public boolean addSensor(Sensor probe)
	{
		SensorDeviceMode mode = (SensorDeviceMode)probe.getInterfaceMode();
		if(portSensors[mode.getPort()] != null &&
		   portSensors[mode.getPort()] != probe)
		{
			// there is a probe already assigned to that port
			return false;
		}

		// We have to be careful here not to overwrite an existing port property
		// probe.setPortProperty(new PropObject("Port", "Port", Probe.PROP_PORT, portNames));

		portSensors[mode.getPort()] = probe;
		return updateMode(probe);
	}	
}

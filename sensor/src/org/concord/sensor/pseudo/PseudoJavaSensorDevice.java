/*
 * Created on Jan 25, 2005
 *
 */
package org.concord.sensor.pseudo;

import org.concord.sensor.ExperimentConfig;
import org.concord.sensor.ExperimentRequest;
import org.concord.sensor.SensorConfig;
import org.concord.sensor.SensorRequest;
import org.concord.sensor.device.AbstractJavaSensorDevice;
import org.concord.sensor.device.DeviceReader;
import org.concord.sensor.device.Sensor;
import org.concord.sensor.device.SensorUnit;

/**
 * @author scott
 *
 */
public class PseudoJavaSensorDevice extends AbstractJavaSensorDevice 
{
	float time = 0;
	PseudoExperimentConfig expConfig = null;
	
	/* (non-Javadoc)
	 * @see org.concord.sensor.device.AbstractJavaSensorDevice#checkMode(org.concord.sensor.device.Sensor, java.lang.Object)
	 */
	protected boolean checkMode(Sensor probe, Object mode) 
	{
		// We have no real sensors so the mode is always ok
		return true;
	}

	/* (non-Javadoc)
	 * @see org.concord.sensor.device.SensorDevice#getRightMilliseconds()
	 */
	public int getRightMilliseconds() 
	{
		// send a default read time of 0.1 s
		return 100;
	}

	/* (non-Javadoc)
	 * @see org.concord.sensor.device.SensorDevice#open(java.lang.String)
	 */
	public void open(String openString) 
	{
		// we have no real sensors so we dont' need to open anything
	}

	/* (non-Javadoc)
	 * @see org.concord.sensor.device.SensorDevice#close()
	 */
	public void close() 
	{
		// again no real sensors so nothing to close
	}

	/* (non-Javadoc)
	 * @see org.concord.sensor.device.SensorDevice#configure(org.concord.sensor.ExperimentRequest)
	 */
	public ExperimentConfig configure(ExperimentRequest request) 
	{
		// We need to return a valid config that matches the request
		// because we are going to pretend to handle anything.
		expConfig = new PseudoExperimentConfig();
		SensorRequest [] sensRequests = request.getSensorRequests();
		SensorConfig [] sensConfigs = new SensorConfig [sensRequests.length]; 
		for(int i =0; i<sensRequests.length; i++) {
			PseudoSensorConfig sensConfig = new PseudoSensorConfig();
			sensConfigs[i] = sensConfig;
			sensConfig.setPort(sensRequests[i].getPort());
			sensConfig.setStepSize(sensRequests[i].getStepSize());
			sensConfig.setType(sensRequests[i].getType());
			sensConfig.setUnit(sensRequests[i].getUnit());
		}
		expConfig.setSensorConfigs(sensConfigs);
		return expConfig;
	}

	/* (non-Javadoc)
	 * @see org.concord.sensor.device.SensorDevice#start()
	 */
	public boolean start() 
	{
		time = 0;
		// We have no real sensors so no problem starting 
		return true;
	}

	/* (non-Javadoc)
	 * @see org.concord.sensor.device.SensorDevice#read(float[], int, int, org.concord.sensor.device.DeviceReader)
	 */
	public int read(float[] values, int offset, int nextSampleOffset,
			DeviceReader reader) 
	{
		// for a test just return a single value each time
		// this could make it seem out of sync because we said the period 
		// was X but we are only return a single value every time, so if the 
		// read takes longer than X then we will be behind.  But for testing
		// purposes this should be ok
		values[offset] = (float)(10 + 5*Math.sin(time));
		time += 0.1;
		return 1;		
	}

	/* (non-Javadoc)
	 * @see org.concord.sensor.device.SensorDevice#stop(boolean)
	 */
	public void stop(boolean wasRunning) 
	{
		time = 0;
	}

	/* (non-Javadoc)
	 * @see org.concord.sensor.device.SensorDevice#getErrorMessage(int)
	 */
	public String getErrorMessage(int error) 
	{
		return null;
	}

	/* (non-Javadoc)
	 * @see org.concord.sensor.device.SensorDevice#isAttached()
	 */
	public boolean isAttached() 
	{
		return true;
	}

	/* (non-Javadoc)
	 * @see org.concord.sensor.device.SensorDevice#canDetectSensors()
	 */
	public boolean canDetectSensors() 
	{
		return true;
	}

	/* (non-Javadoc)
	 * @see org.concord.sensor.device.SensorDevice#getCurrentConfig()
	 */
	public ExperimentConfig getCurrentConfig() 
	{
		// If we have been configured before return that configuration
		if(expConfig != null) {
			return expConfig;
		}

		// Otherwise return a new configuration that says we 
		// have a temperature sensor
		
		// We need to return a valid config that matches the request
		// because we are going to pretend to handle anything.
		expConfig = new PseudoExperimentConfig();
		SensorConfig [] sensConfigs = new SensorConfig [1]; 
		PseudoSensorConfig sensConfig = new PseudoSensorConfig();
		sensConfigs[0] = sensConfig;
		
		sensConfig.setPort(0);
		sensConfig.setStepSize(0.1f);
		sensConfig.setType(SensorConfig.QUANTITY_TEMPERATURE);
		sensConfig.setUnit(new SensorUnit("degC"));
		expConfig.setSensorConfigs(sensConfigs);
		return expConfig;
	}

}

/*
 * Last modification information:
 * $Revision: 1.1 $
 * $Date: 2005-01-19 05:26:10 $
 * $Author: scytacki $
 *
 * Licence Information
 * Copyright 2004 The Concord Consortium 
*/
package org.concord.sensor.nativelib;

import org.concord.sensor.ExperimentRequest;
import org.concord.sensor.SensorRequest;
import org.concord.sensor.device.DeviceReader;
import org.concord.sensor.device.SensorDevice;

import ccsd.ti.ExperimentConfig;
import ccsd.ti.SensorConfig;
import ccsd.ti.NativeBridge;
import ccsd.ti.SWIGTYPE_p_float;
import ccsd.ti.SWIGTYPE_p_void;

/**
 * NativeSensorDevice
 * Class name and description
 *
 * Date created: Dec 2, 2004
 *
 * @author scott<p>
 *
 */
public class NativeTISensorDevice 
	implements SensorDevice
{
	SWIGTYPE_p_void deviceHandle = null;
	SWIGTYPE_p_float readBuffer = null;
	private boolean open;
	
	
	/**
	 * 
	 */
	public NativeTISensorDevice()
	{
		try {
			System.loadLibrary("ti_ccsd");
		} catch (Throwable thr) {
			thr.printStackTrace();
		}
	}
	
	public synchronized void open(String config)
	{
		open = true;
		deviceHandle = NativeBridge.SensDev_open(config);
		if(readBuffer == null) {
			readBuffer = NativeBridge.new_floatArray(200);
		}
	}
	
	public synchronized void close()
	{
		open = false;
		if(deviceHandle != null) {
			NativeBridge.SensDev_close(deviceHandle);
		}
	}
		
	/* (non-Javadoc)
	 * @see org.concord.sensor.device.AbstractSensorDevice#getErrorMessage(int)
	 */
	public String getErrorMessage(int error)
	{
		// TODO configure errors and error messages
		return "no error message yet";
	}
	
	
	/* (non-Javadoc)
	 * @see org.concord.sensor.device.AbstractSensorDevice#getRightMilliseconds()
	 */
	public int getRightMilliseconds()
	{
		// TODO Base this on the time between samples from the device
		return 50;
	}
	
	/**
	 * Send this directly to the native dll.
	 * It will need some configuration string which would be set earlier
	 * so there probably needs to be a cookie that is passed around for
	 * this device to store things.
	 * @see org.concord.sensor.SensorDataProducer#isAttached()
	 */
	public synchronized boolean isAttached()
	{
		if(!open) return false;

		int result = NativeBridge.SensDev_isAttached(deviceHandle);
		return result == 1;
	}

	/**
	 * convert the experimentconfig to native structures.
	 * this is probably done in the native code???
	 * @see org.concord.sensor.SensorDataProducer#configure(org.concord.sensor.ExperimentConfig)
	 */
	public synchronized org.concord.sensor.ExperimentConfig configure(ExperimentRequest request)
	{
		ExperimentConfig requestConfig = 
			new ExperimentConfig();

		// TODO convert the passed in request into a config to
		// pass to the native code. It might be nice let the 
		// sensor device create the request object which the
		// author then fills in.  But this will make the OTrunk
		// stuff harder to handle
		requestConfig.setPeriod(request.getPeriod());

		SensorRequest [] sensorRequests = request.getSensorRequests();
		
		requestConfig.setNumSensorConfigs(sensorRequests.length);
		requestConfig.createSensorConfigArray(sensorRequests.length);
		for(int i=0; i<sensorRequests.length; i++) {
			SensorRequest sensorRequest = sensorRequests[i];
			SensorConfig sensorConfig = new SensorConfig();
			sensorConfig.setPort(sensorRequest.getPort());
			sensorConfig.setType(sensorRequest.getType());
			sensorConfig.setStepSize(sensorRequest.getStepSize());
			// TODO this should be based on the default unit
			// for this type 
			sensorConfig.setUnitStr("degC");
			requestConfig.setSensorConfig(sensorConfig, i);
		}
		
		// TODO we need to make sure that java owns this object
		// that is returned by the sensor device.  Otherwise
		// the memory will never get freed.
		return NativeBridge.configureHelper(deviceHandle, requestConfig);
	}

	/**
	 * Can be native.
	 * 
	 * @see org.concord.sensor.SensorDataProducer#getCurrentConfig()
	 */
	public org.concord.sensor.ExperimentConfig getCurrentConfig()
	{
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * can be native
	 * @see org.concord.sensor.SensorDataProducer#canDetectSensors()
	 */
	public boolean canDetectSensors()
	{
		// TODO: send this request to the NativeBridge
		return false;
	}

	/**
	 * 
	 * native. but with the associated cookie.  unless there is another
	 * perhaps the cookie can be stored in this object so the native code
	 * can look it up and then it won't need to be in each of these methods.
	 * @see org.concord.framework.data.DataFlow#stop()
	 */
	public synchronized void stop(boolean wasRunning)
	{
		// TODO check for null device
		NativeBridge.SensDev_stop(deviceHandle);
	}
	
	/**
	 * native.
	 * @see org.concord.framework.data.DataFlow#start()
	 */
	public synchronized boolean start()
	{
		// TODO check for null device
		NativeBridge.SensDev_start(deviceHandle);
		
		return true;

	}

	public synchronized int read(float [] values, int offset, 
			int nextSampleOffset, DeviceReader reader)
	{
		// take our existing native float pointer
		// pass it to the native read function so it gets filled in
		// traverse the resutling array (in C) and copy the values 
		// into this array.  This could be more efficient if the
		// the array functions generated by swig included a "copy"
		// function that would copy a section of the array.
		int numberRead = NativeBridge.SensDev_read(deviceHandle, readBuffer, 200);
		
		if (numberRead < 0) {
			System.err.println("error reading values from device");
		}
		
		// FIXME: this only handles single sensor setups
		int valPos = offset;
		for(int i=0; i<numberRead; i++) {
			values[valPos] = NativeBridge.floatArray_getitem(readBuffer, i);

			valPos += nextSampleOffset;
		}
		
		return numberRead;
	}
}

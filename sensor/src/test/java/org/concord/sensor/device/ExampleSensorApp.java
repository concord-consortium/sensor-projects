package org.concord.sensor.device;

import java.util.ArrayList;

import org.concord.sensor.ExperimentConfig;
import org.concord.sensor.SensorConfig;
import org.concord.sensor.SensorRequest;
import org.concord.sensor.device.impl.DeviceConfigImpl;
import org.concord.sensor.device.impl.JavaDeviceFactory;
import org.concord.sensor.impl.ExperimentRequestImpl;
import org.concord.sensor.impl.SensorRequestImpl;
import org.concord.sensor.impl.SensorUtilJava;

public abstract class ExampleSensorApp {
	protected SensorDevice device;
	
	// this should be set by subclasses to the id of the device
	// you want to test 
	protected int deviceId = Integer.MIN_VALUE;
	
	// This is passed to the open method on the device
	// several devices don't use this
	protected String openString = null;

	protected JavaDeviceFactory deviceFactory;
	
	void prepareDevice() {
		deviceFactory = new JavaDeviceFactory(){
			public void log(String message) {
				System.out.println("DeviceTest: " + message);					
			}				
		};
		
		setup();
		
		device = deviceFactory.createDevice(new DeviceConfigImpl(deviceId, openString));
	}
		
	public abstract void setup();
	
	public void teardown(){
		if(device == null){
			return;
		}
		
		device.close();
		device = null;
	}

	protected String getDeviceLabel(){
		return device.getVendorName() + " " + device.getDeviceName();
	}
	
	protected boolean supportsAttachingSingleTemperatureSensor() {
		return true;
	}
	
	protected boolean supportsRawValueSensors() {
		return true;
	}
	
	public void testTemperature(){
		prepareDevice();
		
		// Check what is attached, this isn't necessary if you know what you want
		// to be attached.  But sometimes you want the user to see what is attached
		ExperimentConfig currentConfig = device.getCurrentConfig();
		SensorUtilJava.printExperimentConfig(currentConfig);
		
		
		ExperimentRequestImpl request = new ExperimentRequestImpl();
		request.setPeriod(0.1f);
		request.setNumberOfSamples(-1);
		
		SensorRequestImpl sensor = new SensorRequestImpl();
		sensor.setDisplayPrecision(-2);
		sensor.setRequiredMax(Float.NaN);
		sensor.setRequiredMin(Float.NaN);
		sensor.setPort(0);
		sensor.setStepSize(0.1f);
		sensor.setType(SensorConfig.QUANTITY_TEMPERATURE);

		request.setSensorRequests(new SensorRequest [] {sensor});
				
		ExperimentConfig actualConfig = device.configure(request);
				
		device.start();		
		System.out.println("started device");
		
		long startTime = System.currentTimeMillis();
		float [] data = new float [1024];
		while((System.currentTimeMillis() - startTime) < 1000 ){
			int numSamples = device.read(data, 0, 1, null);
			if(numSamples > 0) {
				System.out.println("" + numSamples + " " +
							data[0]);
				System.out.flush();
			} 
			else {
				System.out.println("" + numSamples);
			}
			try {
				Thread.sleep((long)(actualConfig.getDataReadPeriod()*1000));
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		
		device.stop(true);
		teardown();		
		System.exit(0);		
	}

	
	/**
	 * Test collecting data from a Temperature and a Light probe simultaneously
	 * from a device which supports multiple sensors.
	 */
	public void testAllConnectedProbes(){
		prepareDevice();
		
		// Check what is attached.
		ExperimentConfig currentConfig = device.getCurrentConfig();
		SensorUtilJava.printExperimentConfig(currentConfig);
		
		SensorConfig [] sensorConfigArr = currentConfig.getSensorConfigs();
		if(sensorConfigArr.length == 0) {
			String mesg = "Must have at least one sensor attached, found: 0";
			System.out.println(mesg);
			throw new RuntimeException(mesg);
		}
		
		ArrayList<SensorRequest> sensorRequestList = new ArrayList<SensorRequest>();

		for (SensorConfig config : sensorConfigArr) {
			SensorRequestImpl sensor = new SensorRequestImpl();
			sensor.setDisplayPrecision(-2);
			sensor.setUnit(config.getUnit());
			sensor.setPort(config.getPort());
			sensor.setStepSize(0.1f);
			sensor.setType(config.getType());
			sensorRequestList.add(sensor);			
		}
		
		ExperimentRequestImpl request = new ExperimentRequestImpl();
		request.setPeriod(0.1f);
		request.setNumberOfSamples(-1);
		
		SensorRequest [] sensorRequestArr = sensorRequestList.toArray(new SensorRequest[0]); 
		request.setSensorRequests(sensorRequestArr);
				
		ExperimentConfig actualConfig = device.configure(request);
		SensorConfig[] sensorConfigs = actualConfig.getSensorConfigs();
		
		int numberOfSensors = sensorConfigs.length;
		int numberOfValuesInASample = numberOfSensors;
		if(!actualConfig.getExactPeriod()){
			numberOfValuesInASample += 1;
		}
		
		device.start();
		System.out.println("started device ... will collect for 10s");
		
		long startTime = System.currentTimeMillis();
		float [] data = new float [1024*numberOfValuesInASample];
		int totalSamples = 0;
		while((System.currentTimeMillis() - startTime) < 10000 ){
			int numSamples = device.read(data, 0, numberOfValuesInASample, null);
			for(int i=0; i<numSamples; i++){
				System.out.print("sample(" + totalSamples + ") ");
				int offset = i*numberOfValuesInASample;
				if(!actualConfig.getExactPeriod()){
					System.out.print(data[offset] + "s ");
					offset++;
				}
			
				for(int j=0; j<numberOfSensors; j++){
					System.out.print(String.format("%s: %4.2f %s ",
							sensorConfigs[j].getName(), data[offset+j], sensorConfigs[j].getUnit()));
				}
				System.out.println("");
				System.out.flush();
				totalSamples++;
			}
			try {
				Thread.sleep((long)(actualConfig.getDataReadPeriod()*1000));
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		
		teardown();
		
		System.exit(0);
	}

}

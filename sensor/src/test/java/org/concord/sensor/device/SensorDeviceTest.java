package org.concord.sensor.device;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import javax.swing.JOptionPane;

import junit.framework.Assert;

import org.concord.sensor.ExperimentConfig;
import org.concord.sensor.ExperimentRequest;
import org.concord.sensor.SensorConfig;
import org.concord.sensor.SensorRequest;
import org.concord.sensor.device.impl.JavaDeviceService;
import org.concord.sensor.impl.ExperimentRequestImpl;
import org.concord.sensor.impl.SensorRequestImpl;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public abstract class SensorDeviceTest {	
	protected SensorDevice device;
	
	// this will be used if the device is DeviceIdAware 
	protected int deviceId = Integer.MIN_VALUE;
	
	// This is passed to the open method on the device
	// several devices don't use this
	protected String openString = null;
	
	// This is to catch exceptions on second threads
	Throwable otherThreadException = null;

	protected int msToWaitBeforeReadingData = 500;
	
	// This is used so we don't tell the user the same thing multiple times
	protected static String lastUserMessage;
	
	@Test
	public void testDeviceCreated() {
		assertNotNull("The setup method needs to set the device instance variable", device);
		if(device instanceof DeviceIdAware){
			assertTrue("If the device is id aware then setup should set the deviceId instance variable",
					Integer.MIN_VALUE != deviceId);
		}
	}
	
	@Test
	public void testGetVendorName() {
		// test this before open is called
		assertNotNull("Device should return valid vendor name before open is called", device.getVendorName());
		
		// and if the device is idAware test after setting the device id
		if(device instanceof DeviceIdAware){
			((DeviceIdAware)device).setDeviceId(deviceId);

			// test that the device name is still a valid name
			assertNotNull("Device should return valid vendor name after id is set, before open is called", device.getVendorName());
		}
				
		
		// and also test after the open call
		openDevice();
		
		assertNotNull("Device should return valid vendor name after open is called", device.getVendorName());
	}
	
	@Test
	public void testGetDeviceName() {
		// test this before open is called
		assertNotNull("Device should return valid vendor name before open is called", device.getDeviceName());
		
		// and if the device is idAware test after setting the device id
		if(device instanceof DeviceIdAware){
			((DeviceIdAware)device).setDeviceId(deviceId);

			// test that the device name is still a valid name
			assertNotNull("Device should return valid vendor name after id is set, before open is called", device.getDeviceName());
		}
				
		
		// and also test after the open call
		openDevice();
		
		assertNotNull("Device should return valid vendor name after open is called", device.getDeviceName());
	}
	
	@Test
	public void testOpen() {
		// there isn't much to test here basically just to make sure nothing blows up
		if(device instanceof DeviceIdAware){
			((DeviceIdAware)device).setDeviceId(deviceId);
		}

		// go for it
		openDevice();
	}
	
	@Test 
	public void testIsAttached() {
		tellUserToAttachTheDevice();

		prepareDevice();

		assertTrue("Device should be attached for this test", device.isAttached());
	}
	
	@Test
	public void testGetCurrentConfig() {
		tellUserToAttachTheDeviceWith("a temperature sensor");

		prepareDevice();

		ExperimentConfig currentConfig = device.getCurrentConfig();
		assertTrue("Device should return a non null config", currentConfig != null);
		
		SensorConfig[] sensorConfigs = currentConfig.getSensorConfigs();
		if(supportsAttachingSingleTemperatureSensor()){
			assertTrue("Sensor configs should have one sensor", sensorConfigs.length == 1);

			assertTrue("Sensor config should be a temperature sensor", 
					sensorConfigs[0].getType() == SensorConfig.QUANTITY_TEMPERATURE ||
					sensorConfigs[0].getType() == SensorConfig.QUANTITY_TEMPERATURE_WAND);		
		} else {
			boolean foundSensor = false;
			for(SensorConfig config: sensorConfigs){
				if(config.getType() == SensorConfig.QUANTITY_TEMPERATURE ||
					config.getType() == SensorConfig.QUANTITY_TEMPERATURE_WAND){
					foundSensor = true;
					break;
				}
			}
			assertTrue("Sensor config should have a temperature sensor", foundSensor);
		}
	}
	
	@Test
	public void testConfigure(){
		prepareForTemperatureCollection();
	}

	/**
	 * NOTE this is a problem with the current API
	 *	 it is not clear what to do if a user wants to find out the attached sensors after they 
	 *   have already done a configure.  
	 */
	@Test
	public void testGetCurrentConfigAfterConfigure(){
		prepareForTemperatureCollection();	
	
		device.getCurrentConfig();
		
		//		ExperimentConfig currentConfig = device.getCurrentConfig();

		// This part will fail on some devices because calling getCurrentConfig will
		// partially reset the configured sensors, so that starting it now will 
		// try to access invalid sensors objects
		
		//		assertTrue("Device started correctly", device.start());
		//		
		//		float[] values = new float[10000];		
		//		int count = device.read(values, 0, 1, null);
		//		assertTrue("Read doesn't return error", count >=0);
		//
		//		count = readData(values, 0);
		//		assertTrue("Read got some valid values", count > 0);
		//				
		//		device.stop(true);				
	}
	
	@Test
	public void testConfigureInvalidSensor(){
		tellUserToAttachTheDeviceWith("a temperature sensor");

		prepareDevice();
		
		ExperimentRequestImpl experimentRequest = new ExperimentRequestImpl();		
		SensorRequestImpl sensorRequest = new SensorRequestImpl();
		experimentRequest.setSensorRequests(new SensorRequest[] {sensorRequest});
		sensorRequest.setType(SensorConfig.QUANTITY_FORCE);
		
		
		ExperimentConfig experimentConfig = device.configure(experimentRequest);
		assertNotNull("Non null experiment config", experimentConfig);
		
		assertTrue("Correctly didn't find a force sensor", !experimentConfig.isValid());
	}

	@Test
	public void testStartStop(){
		prepareForTemperatureCollection();
		
		assertTrue("Device started correctly", device.start());
		
		device.stop(true);		
	}
	

	@Test
	public void testThreadedCollection() throws Throwable{
		prepareForTemperatureCollectionBetween("10 and 40 C (50 - 104 F)");

		assertTrue("Device started correctly", device.start());
		
		Thread thread = new Thread(){
			public void run() {
				float[] values = new float[10000];		
				int count = device.read(values, 0, 1, null);
				assertTrue("Read doesn't return error", count >=0);				

				count = readData(values, 0);
				assertTrue("Read got some valid values", count > 0);
				assertTrue("Temp value is sane", values[0] > 10 && values[0] < 40);
			}
		};

		otherThreadException = null;
		Thread.setDefaultUncaughtExceptionHandler(new Thread.UncaughtExceptionHandler() {			
			public void uncaughtException(Thread t, Throwable e) {
				otherThreadException = e;
				
			}
		});
		thread.start();
		
		thread.join();
		
		if(otherThreadException != null){
			throw otherThreadException;
		}
		
		device.stop(true);				
	}

	@Test
	public void testTemperatureCollection() throws InterruptedException{
		prepareForTemperatureCollectionBetween("10 and 40 C (50 - 104 F)");

		assertTrue("Device started correctly", device.start());
		
		float[] values = new float[10000];		
		int count = device.read(values, 0, 1, null);
		assertTrue("Read doesn't return error", count >=0);

		count = readData(values, 0);
		assertTrue("Read got some valid values", count > 0);
		assertTrue("Temp value is sane", values[0] > 10 && values[0] < 40);
				
		device.stop(true);				
	}
	
	@Test
	public void testConfigureRawVoltage1(){
		if(!supportsRawValueSensors()) {
			return;
		}

		// This might not be supported by all devices.  The raw voltage configuration has
		// 2 goals: 
		// -- allow people to build their own sensors
		// -- debugging calibrations of production sensors 
		tellUserToAttachTheDeviceWith("a raw voltage compatible sensor reading between 0 and 5 Volts");

		prepareDevice();
		
		ExperimentRequestImpl experimentRequest = new ExperimentRequestImpl();		
		SensorRequestImpl sensorRequest = new SensorRequestImpl();
		experimentRequest.setSensorRequests(new SensorRequest[] {sensorRequest});
		sensorRequest.setType(SensorConfig.QUANTITY_RAW_VOLTAGE_1);
		
		
		ExperimentConfig experimentConfig = device.configure(experimentRequest);
		assertNotNull("Non null experiment config", experimentConfig);
		
		if(supportsRawValueSensors()){
			assertTrue("Correctly configured a raw sensor", experimentConfig.isValid());
			Assert.assertEquals("First sensor is raw voltage 1", SensorConfig.QUANTITY_RAW_VOLTAGE_1, 
					experimentConfig.getSensorConfigs()[0].getType());
		} else {
			assertTrue("Correctly invalid raw sensor", !experimentConfig.isValid());			
		}
	}
	
	@Test
	public void testConfigureRawVoltage2(){
		if(!supportsRawValueSensors()) {
			return;
		}

		// This might not be supported by all devices.  The raw voltage configuration has
		// 2 goals: 
		// -- allow people to build their own sensors
		// -- debugging calibrations of production sensors 

		tellUserToAttachTheDeviceWith("a raw voltage compatible sensor reading between 0 and 5 Volts");
		
		prepareDevice();
		
		ExperimentRequestImpl experimentRequest = new ExperimentRequestImpl();		
		SensorRequestImpl sensorRequest = new SensorRequestImpl();
		experimentRequest.setSensorRequests(new SensorRequest[] {sensorRequest});
		sensorRequest.setType(SensorConfig.QUANTITY_RAW_VOLTAGE_2);
		
		
		ExperimentConfig experimentConfig = device.configure(experimentRequest);
		assertNotNull("Non null experiment config", experimentConfig);
		
		if(supportsRawValueSensors()){
			assertTrue("Correctly configured a raw sensor", experimentConfig.isValid());
			Assert.assertEquals("First sensor is raw voltage 2", SensorConfig.QUANTITY_RAW_VOLTAGE_2, 
					experimentConfig.getSensorConfigs()[0].getType());
		} else {
			assertTrue("Correctly invalid raw sensor", !experimentConfig.isValid());			
		}
	}

	@Test
	public void testRawVoltage1Collection() throws InterruptedException{
		if(!supportsRawValueSensors()) {
			return;
		}
		
		tellUserToAttachTheDeviceWith("a raw voltage compatible sensor reading between 0 and 5 Volts");

		prepareDevice();

		ExperimentRequestImpl experimentRequest = new ExperimentRequestImpl();
		experimentRequest.setPeriod(0.1f);
		SensorRequestImpl sensorRequest = new SensorRequestImpl();
		experimentRequest.setSensorRequests(new SensorRequest[] {sensorRequest});
		sensorRequest.setType(SensorConfig.QUANTITY_RAW_VOLTAGE_1);

		ExperimentConfig experimentConfig = device.configure(experimentRequest);
		assertNotNull("Non null experiment config", experimentConfig);
		assertTrue("Correctly configured a raw voltage sensor", experimentConfig.isValid());

		assertTrue("Device started correctly", device.start());
		
		float[] values = new float[10000];		
		int count = device.read(values, 0, 1, null);
		assertTrue("Read doesn't return error", count >=0);

		count = readData(values, 0);
		assertTrue("Read got some valid values", count > 0);
		assertTrue("Voltage value is sane", values[0] >= 0 && values[0] <= 5);
				
		device.stop(true);				
	}

	
	@Test
	public void testRepeatConfiguration(){
		tellUserToAttachTheDeviceWith("a temperature sensor");

		prepareDevice();
		
		ExperimentRequestImpl experimentRequest = new ExperimentRequestImpl();		
		SensorRequestImpl sensorRequest = new SensorRequestImpl();
		experimentRequest.setSensorRequests(new SensorRequest[] {sensorRequest});
		sensorRequest.setType(SensorConfig.QUANTITY_FORCE);
				
		ExperimentConfig experimentConfig = device.configure(experimentRequest);
		assertNotNull("Non null experiment config", experimentConfig);
		assertTrue("Correctly didn't find a force sensor", !experimentConfig.isValid());
		
		experimentConfig = null;
		
		tellUserToAttachTheDeviceWith("a force sensor");

		experimentConfig = device.configure(experimentRequest);		
		
		assertNotNull("Non null experiment config", experimentConfig);
		assertTrue("Correctly configured a force sensor", experimentConfig.isValid());
	}
		
	@Test
	public void testConfigureInvalidResolution(){
		tellUserToAttachTheDeviceWith("a force sensor");

		prepareDevice();
		
		ExperimentRequestImpl experimentRequest = new ExperimentRequestImpl();		
		SensorRequestImpl sensorRequest = new SensorRequestImpl();
		experimentRequest.setSensorRequests(new SensorRequest[] {sensorRequest});
		sensorRequest.setType(SensorConfig.QUANTITY_FORCE);
		sensorRequest.setStepSize(0.00001f);
		
		
		ExperimentConfig experimentConfig = device.configure(experimentRequest);
		assertNotNull("Non null experiment config", experimentConfig);
		
		assertTrue("Correctly didn't find a force sensor with 0.00001 resolution", !experimentConfig.isValid());
	}

	@Test
	public void testMotionCollection() throws InterruptedException{
		tellUserToAttachTheDeviceWith("a motion sensor between 0 and 1 meter");

		prepareDevice();

		ExperimentRequestImpl experimentRequest = new ExperimentRequestImpl();
		experimentRequest.setPeriod(0.1f);
		SensorRequestImpl sensorRequest = new SensorRequestImpl();
		experimentRequest.setSensorRequests(new SensorRequest[] {sensorRequest});
		sensorRequest.setType(SensorConfig.QUANTITY_DISTANCE);

		ExperimentConfig experimentConfig = device.configure(experimentRequest);
		assertNotNull("Non null experiment config", experimentConfig);
		assertTrue("Correctly configured a motion sensor", experimentConfig.isValid());

		assertTrue("Device started correctly", device.start());
		
		float[] values = new float[10000];		
		int count = device.read(values, 0, 1, null);
		assertTrue("Read doesn't return error", count >=0);

		count = readData(values, 0);
		assertTrue("Read got some valid values", count > 0);
		assertTrue("Temp value is sane", values[0] > 0f && values[0] < 1f);
				
		device.stop(true);				
	}

	@Test
	public void testOxygenGasCollection() throws InterruptedException{
		tellUserToAttachTheDeviceWith("a oxygen gas sensor");

		prepareDevice();

		ExperimentRequestImpl experimentRequest = new ExperimentRequestImpl();
		experimentRequest.setPeriod(0.1f);
		SensorRequestImpl sensorRequest = new SensorRequestImpl();
		experimentRequest.setSensorRequests(new SensorRequest[] {sensorRequest});
		sensorRequest.setType(SensorConfig.QUANTITY_OXYGEN_GAS);

		ExperimentConfig experimentConfig = device.configure(experimentRequest);
		assertNotNull("Non null experiment config", experimentConfig);
		assertTrue("Correctly configured a oxygen gas sensor", experimentConfig.isValid());

		assertTrue("Device started correctly", device.start());
		
		float[] values = new float[10000];		
		int count = device.read(values, 0, 1, null);
		assertTrue("Read doesn't return error", count >=0);

		// This requires a 5 second warm up time, but we don't want to what that long
		// instead we just add 500 extra milliseconds
		count = readData(values, 500);
		assertTrue("Read got some valid values", count > 0);
		assertTrue("Oxygen gas % is sane", values[0] > 15.0f && values[0] < 25.0f);
				
		device.stop(true);				
	}

	@Test
	public void testIsNotAttached() throws Throwable {
		tellUserToDetachTheDevice();

		prepareDevice();

		// This fails after running the other tests with the pasco PowerLink
		assertTrue("Device should not be attached", !device.isAttached());
		
	}
	
	@Before
	public abstract void setup();
	
	@After
	public void teardown(){
		if(device == null){
			return;
		}
		
		device.close();
		device = null;
	}
	
	protected void tellUser(String message) {
		if(message.equals(lastUserMessage)){
			return;
		}
		JOptionPane.showMessageDialog(null, message);
		lastUserMessage = message;
	}
	
	protected void tellUserToAttachTheDevice() {
		tellUser("Attach the " + getDeviceLabel());
	}
	
	protected void tellUserToAttachTheDeviceWith(String message) {
		tellUser("Attach the " + getDeviceLabel() + " with " + message);
	}
	
	protected void tellUserToDetachTheDevice() {
		tellUser("Detach the " + getDeviceLabel());
	}
	
	protected void prepareDevice() {
		if(device instanceof DeviceIdAware){
			((DeviceIdAware)device).setDeviceId(deviceId);
		}
		
		openDevice();
	}
	
 	protected void prepareForTemperatureCollectionBetween(String range) {
 		if(range == null){
 			tellUserToAttachTheDeviceWith("a temperature sensor");
 		} else {
 			tellUserToAttachTheDeviceWith("a temperature sensor between " + range);
 		}

		prepareDevice();
		
		ExperimentRequestImpl experimentRequest = new ExperimentRequestImpl();		
		experimentRequest.setPeriod(0.1f);
		SensorRequestImpl sensorRequest = new SensorRequestImpl();
		experimentRequest.setSensorRequests(new SensorRequest[] {sensorRequest});
		sensorRequest.setType(SensorConfig.QUANTITY_TEMPERATURE);

		ExperimentConfig experimentConfig = device.configure(experimentRequest);
		assertNotNull("Non null experiment config", experimentConfig);
		assertTrue("Correctly configured a temperature sensor", experimentConfig.isValid());
 	}

	protected void prepareForTemperatureCollection() {
		prepareForTemperatureCollectionBetween(null);
 	}
 	
	void openDevice(){
		// this assumes:
		// - the device instance is already created
		// - the id of the device has already been set
		
		if(device instanceof DeviceServiceAware){
			((DeviceServiceAware) device).setDeviceService(new JavaDeviceService(){

				public UserMessageHandler getMessageHandler() {
					// TODO Auto-generated method stub
					return null;
				}

				public void log(String message) {
					System.out.println("DeviceTest: " + message);					
				}				
			});
		}
		
		device.open(openString);
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
	
	protected int readData(float [] values, int extraTime){
		try {
			Thread.sleep(msToWaitBeforeReadingData + extraTime);
		} catch (InterruptedException e) {
			assertTrue("Should not throw an exception while waiting", false);
		}
		return device.read(values, 0, 1, null);
	}
}

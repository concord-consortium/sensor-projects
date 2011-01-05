package org.concord.sensor.device;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import javax.swing.JOptionPane;

import org.concord.framework.text.UserMessageHandler;
import org.concord.sensor.ExperimentConfig;
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
		JOptionPane.showMessageDialog(null, "Attach the " + getDeviceLabel());

		prepareDevice();

		assertTrue("Device should be attached for this test", device.isAttached());
	}
	
	@Test
	public void testGetCurrentConfig() {
		JOptionPane.showMessageDialog(null, "Attach the " + getDeviceLabel() +
				" and a temperature sensor");

		prepareDevice();

		ExperimentConfig currentConfig = device.getCurrentConfig();
		assertTrue("Device should return a non null config", currentConfig != null);
		
		SensorConfig[] sensorConfigs = currentConfig.getSensorConfigs();
		assertTrue("Sensor configs should have one sensor", sensorConfigs.length == 1);
		
		assertTrue("Sensor config should be a temperature sensor", 
				sensorConfigs[0].getType() == SensorConfig.QUANTITY_TEMPERATURE ||
				sensorConfigs[0].getType() == SensorConfig.QUANTITY_TEMPERATURE_WAND);		
	}
	
	@Test
	public void testConfigure(){
		JOptionPane.showMessageDialog(null, "Attach the " + getDeviceLabel() +
			" and a temperature sensor");

		prepareDevice();
		
		ExperimentRequestImpl experimentRequest = new ExperimentRequestImpl();		
		SensorRequestImpl sensorRequest = new SensorRequestImpl();
		experimentRequest.setSensorRequests(new SensorRequest[] {sensorRequest});
		sensorRequest.setType(SensorConfig.QUANTITY_TEMPERATURE);
		
		
		ExperimentConfig experimentConfig = device.configure(experimentRequest);
		
		assertTrue("Correctly configured a temperature sensor", experimentConfig.isValid());
	}
	
	@Test
	public void testStartStop(){
		JOptionPane.showMessageDialog(null, "Attach the " + getDeviceLabel() +
		" and a temperature sensor");

		prepareDevice();

		ExperimentRequestImpl experimentRequest = new ExperimentRequestImpl();		
		SensorRequestImpl sensorRequest = new SensorRequestImpl();
		experimentRequest.setSensorRequests(new SensorRequest[] {sensorRequest});
		sensorRequest.setType(SensorConfig.QUANTITY_TEMPERATURE);

		ExperimentConfig experimentConfig = device.configure(experimentRequest);
		assertTrue("Correctly configured a temperature sensor", experimentConfig.isValid());

		assertTrue("Device started correctly", device.start());
		
		device.stop(true);		
	}
	
	@Test
	public void testCollection() throws InterruptedException{
		JOptionPane.showMessageDialog(null, "Attach the " + getDeviceLabel() +
		" and a temperature sensor between 10 and 40 C (50 - 104 F)");

		prepareDevice();

		ExperimentRequestImpl experimentRequest = new ExperimentRequestImpl();
		experimentRequest.setPeriod(0.1f);
		SensorRequestImpl sensorRequest = new SensorRequestImpl();
		experimentRequest.setSensorRequests(new SensorRequest[] {sensorRequest});
		sensorRequest.setType(SensorConfig.QUANTITY_TEMPERATURE);

		ExperimentConfig experimentConfig = device.configure(experimentRequest);
		assertTrue("Correctly configured a temperature sensor", experimentConfig.isValid());

		assertTrue("Device started correctly", device.start());
		
		float[] values = new float[10000];		
		int count = device.read(values, 0, 1, null);
		assertTrue("Read doesn't return error", count >=0);

		Thread.sleep(500);
		count = device.read(values, 0, 1, null);
		assertTrue("Read got some valid values", count > 0);
		assertTrue("Temp value is sane", values[0] > 10 && values[0] < 40);
				
		device.stop(true);				
	}
	
	@Test
	public void testConfigureInvalid(){
		JOptionPane.showMessageDialog(null, "Attach the " + getDeviceLabel() +
			" with NO temperature sensor");

		prepareDevice();
		
		ExperimentRequestImpl experimentRequest = new ExperimentRequestImpl();		
		SensorRequestImpl sensorRequest = new SensorRequestImpl();
		experimentRequest.setSensorRequests(new SensorRequest[] {sensorRequest});
		sensorRequest.setType(SensorConfig.QUANTITY_TEMPERATURE);
		
		
		ExperimentConfig experimentConfig = device.configure(experimentRequest);
		
		assertTrue("Correctly configured a temperature sensor", !experimentConfig.isValid());
	}

	@Test
	public void testIsNotAttached() {
		JOptionPane.showMessageDialog(null, "Detach the " + getDeviceLabel());

		prepareDevice();

		assertTrue("Device should not be attached for this test", !device.isAttached());
		
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
	
	void prepareDevice() {
		if(device instanceof DeviceIdAware){
			((DeviceIdAware)device).setDeviceId(deviceId);
		}
		
		openDevice();
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
	
	String getDeviceLabel(){
		return device.getVendorName() + " " + device.getDeviceName();
	}
	
}

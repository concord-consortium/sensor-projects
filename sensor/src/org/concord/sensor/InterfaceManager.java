package org.concord.sensor;

import org.concord.framework.data.stream.DataConsumer;
import org.concord.framework.text.UserMessageHandler;

public class InterfaceManager
{
	protected UserMessageHandler messageHandler;
	protected 		waba.util.Vector 	dataListeners = null;
	
	protected waba.util.Vector sensorConfigs = new waba.util.Vector();
	
	private boolean prepared;
	private DeviceConfig[] deviceConfigs;
	private static DeviceFactory deviceFactory;
		
	public InterfaceManager(UserMessageHandler h)
	{
		messageHandler = h;
	}
		
	public void prepareDataProducer(ExperimentConfig config, 
			DataConsumer consumer)
	{
		// Check the policy it can be one of the following:
		// use only a specific device
		// use only one of a collection of devices
		// use any attached device.
		// The policy will be determined by looking at the
		// device list.  If it is null then any device will
		// be used.  Otherwise the list will be traversed trying
		// to find an available device.
		if(deviceConfigs == null) {
			// in this case how do we know the address strings
			// and how do we know the set of known devices?  I guess
			// the factory would need to tell us and we would need to create
			// each one and see if it is attached.
			System.err.println("Searching all possible devices isn't supported yet");
			return;
		}
		
		SensorDevice attachedDevice = null;
		
		for(int i=0; i<deviceConfigs.length; i++) {
			SensorDevice device = 
				deviceFactory.createDevice(deviceConfigs[i], messageHandler);
			if(device.isAttached()){
				attachedDevice = device;
				break;
			}
			deviceFactory.destroyDevice(device);
		}
		
		if(attachedDevice == null) {
			// prompt the user to connect one of the supported devices
			// then try again, recursively?
			System.err.println("Couldn't find attached device");
			return;
		}
		
		ExperimentConfig actualConfig = attachedDevice.configure(config);
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
			if(actualConfig != null) {
				System.err.println("  device reason: " + actualConfig.getInvalidReason());
			}
		}
				
		consumer.addDataProducer(attachedDevice);
	}
	
	public SensorDevice [] getAttachedDevices()
	{
		return null;
	}	
	
	public DeviceConfig [] getDeviceConfigs()
	{
		return deviceConfigs; 
	}
	
	public void setDeviceConfigs(DeviceConfig [] configs)
	{
		deviceConfigs = configs;
	}
	
	public static void setDeviceFactory(DeviceFactory factory)
	{
		deviceFactory = factory;
	}
}

package org.concord.sensor.device.impl;

import org.concord.framework.data.stream.DataConsumer;
import org.concord.framework.text.UserMessageHandler;
import org.concord.sensor.ExperimentConfig;
import org.concord.sensor.ExperimentRequest;
import org.concord.sensor.SensorDataManager;
import org.concord.sensor.SensorDataProducer;
import org.concord.sensor.device.DeviceConfig;
import org.concord.sensor.device.DeviceFactory;
import org.concord.sensor.device.SensorDevice;

/**
 * 
 * InterfaceManager
 * 
 * This class is used to create SensorDataProducers.  The InterfaceManager
 * needs a DeviceFactory and a set of DeviceConfigs.  Probably the Interface
 * Manager will be create from a configuration that includes the device configs
 * and the configuration for the DeviceFactory.
 * 
 * The DeviceConfig objects map setup the different types of sensor devices
 * with any connection settings.  For example if it is a serial device then 
 * the serial port can be saved a connection setting.
 * 
 * The type of SensorDevice that is created to work with the SensorDataProducer
 * is determined by the list of DeviceConfig objects.  
 * - If there is no list then
 * each device from the DeviceFactory will be created and checked if it is
 * attached.  In this case some default connection settings will be used by the 
 * device.  
 * - If there is a list then
 * the first device on the list will be created and checked if it is attached,
 * then the second device, etc.    
 *
 * NOTES: Probably this should be turned into an interface or abstract class.  And
 * there should be a java and waba version of this class.  This could be used
 * to remove dependencies between the this package and the device package.
 *
 * Date created: Jan 4, 2005
 *
 * @author scott<p>
 *
 */
public class InterfaceManager implements SensorDataManager
{
	protected UserMessageHandler messageHandler;
	protected Ticker ticker;
	
	protected 		waba.util.Vector 	dataListeners = null;
	
	protected waba.util.Vector sensorConfigs = new waba.util.Vector();
	
	private boolean prepared;
	private DeviceConfig[] deviceConfigs;
	private static DeviceFactory deviceFactory = new JavaDeviceFactory();
		
	
	
	/**
	 * The UserMessageHandler is used by the SensorDataProducer
	 * to show messages to the user.  If an device is detached in the 
	 * middle of a data collection.  Or if the wrong device is connected
	 * this message handler is used.
	 * 
	 * The ticker is used to read bytes from the device at regular intervals
	 * This is required because waba does not have threads, so a ticker 
	 * must be provided to abstract this.
	 * 
	 * @param h
	 */
	public InterfaceManager(UserMessageHandler h)
	{
		messageHandler = h;
		ticker = new JavaTicker();
	}
		
	/**
	 * A request is sent in allong with a data consumer.  A SensorDataProducer
	 * is created and then passed to the consumer.  This method
	 * does not directly return a SensorDataProducer because it might take
	 * a while to create and initialize it.   
	 * 
	 * @param request
	 * @param consumer
	 */
	public void prepareDataProducer(ExperimentRequest request, 
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
				deviceFactory.createDevice(deviceConfigs[i]);
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
		
		ExperimentConfig actualConfig = attachedDevice.configure(request);
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
		
		SensorDataProducerImpl dataProducer = 
			new SensorDataProducerImpl(attachedDevice, ticker, messageHandler);
				
		dataProducer.configure(request, actualConfig);
		
		consumer.addDataProducer(dataProducer);
	}
	
	/**
	 * This should return a sensordataproducer for all the currently 
	 * attached devices.  This method currently is not implemented.  When it 
	 * is implement it might take a long time to return. 
	 * @return
	 */
	public SensorDataProducer [] getAttachedDevices()
	{
		return null;
	}	
	
	/**
	 * This returns all the device configs that have been set.  
	 * These configs are used to determine the available devices.
	 * @return
	 */
	public DeviceConfig [] getDeviceConfigs()
	{
		return deviceConfigs; 
	}
	
	/**
	 * Set the list of device configs.  This list is used when
	 * prepareDataProducer is called.  The list is traversed and the 
	 * first available device is used.  If the list is null then all 
	 * devices available from the DeviceFactory are checked for.  
	 * 
	 * @param configs
	 */
	public void setDeviceConfigs(DeviceConfig [] configs)
	{
		deviceConfigs = configs;
	}
	
	/**
	 * This factory is used to actually create the SensorDataProducers.
	 * 
	 * 
	 * @param factory
	 */
	public static void setDeviceFactory(DeviceFactory factory)
	{
		deviceFactory = factory;
	}
}

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

package org.concord.sensor.device.impl;

import org.concord.framework.text.UserMessageHandler;
import org.concord.sensor.DeviceConfig;
import org.concord.sensor.SensorDataManager;
import org.concord.sensor.SensorDataProducer;
import org.concord.sensor.device.DeviceFactory;
import org.concord.sensor.device.SensorDevice;
import org.concord.sensor.impl.JavaSensorDataProducer;
import org.concord.sensor.impl.SensorDataProducerImpl;
import org.concord.sensor.impl.Ticker;

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
		
	private boolean prepared;
	private DeviceConfig[] deviceConfigs;
	private static DeviceFactory deviceFactory = new JavaDeviceFactory();
		
	protected SensorDevice currentDevice = null; 
	
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
		this(h, new JavaTicker());
	}

	public InterfaceManager(UserMessageHandler h, Ticker ticker)
	{
		messageHandler = h;
		this.ticker = ticker;
	}
		
	/**
	 * 
	 * @param request
	 * @param consumer
	 */
	public SensorDataProducer createDataProducer()
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
			return null;
		}
				
		if(currentDevice != null) {
			// check if it is attached.
			// if not then it should be closed.
			// this means we only support one device at a time
		    if(ticker.isTicking()) {
		        ticker.stopTicking(null);
		    }
		    
			if(!currentDevice.isAttached()) {
				deviceFactory.destroyDevice(currentDevice);
				currentDevice = null;
			}
		}
		
		if(currentDevice == null) {
			for(int i=0; i<deviceConfigs.length; i++) {
				SensorDevice device = 
					deviceFactory.createDevice(deviceConfigs[i]);
				if(device.isAttached()){
					currentDevice = device;
					break;
				}
				deviceFactory.destroyDevice(device);
			}
		}
		
		if(currentDevice == null) {
			// prompt the user to connect one of the supported devices
			// then try again, recursively?
			System.err.println("Couldn't find attached device");
			
			// for now we need to give them a default device
			// so we can test this code
			
			return null;
		}

		if(ticker.isTicking()) {
		    // we need to stop this ticker from running
		    // it was probably running from being added to another
		    // dataProducer.  It would be better if we had an instance
		    // of this dataProducer so we could stop it directly.
		    ticker.stopTicking(null);
		}
		
		SensorDataProducer dataProducer = 
		    new JavaSensorDataProducer(currentDevice, ticker, messageHandler);

		return dataProducer;
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
	
	
	public static SensorDataProducer getDataProducerForDeviceNoConsumer(int deviceId){
	    return getDataProducerForDeviceNoConsumer(deviceId,null,null);
	}
	
	public static SensorDataProducer getDataProducerForDeviceNoConsumer(int deviceId, String configString,UserMessageHandler messenger){
		SensorDataManager  sdManager = new InterfaceManager(messenger);
		DeviceConfig [] dConfigs = new DeviceConfig[1];
		dConfigs[0] = new DeviceConfigImpl(deviceId, null);		
		((InterfaceManager)sdManager).setDeviceConfigs(dConfigs);
		org.concord.sensor.ExperimentRequest request = new org.concord.sensor.impl.ExperimentRequestImpl();
		SensorDataProducer producer = sdManager.createDataProducer();
		if(producer != null) producer.configure(request);
		return producer;
	}
	
}

/*
 * Last modification information:
 * $Revision: 1.10 $
 * $Date: 2005-02-23 21:02:30 $
 * $Author: scytacki $
 *
 * Licence Information
 * Copyright 2004 The Concord Consortium 
*/
package org.concord.sensor.device.impl;

import java.util.Hashtable;

import org.concord.sensor.DeviceConfig;
import org.concord.sensor.cc.CCInterface0;
import org.concord.sensor.cc.CCInterface1;
import org.concord.sensor.cc.CCInterface2;
import org.concord.sensor.device.DeviceFactory;
import org.concord.sensor.device.SensorDevice;
import org.concord.sensor.impl.Ticker;
import org.concord.sensor.pseudo.PseudoJavaSensorDevice;


/**
 * JavaDeviceFactory
 * Class name and description
 *
 * Date created: Dec 1, 2004
 *
 * @author scott<p>
 *
 */
public class JavaDeviceFactory
	implements DeviceFactory
{
	public final static int PSEUDO_DEVICE = 0;
	public final static int VERNIER_GO_LINK = 10;
	public final static int TI_CONNECT = 20;
	public final static int FOURIER = 30;
	public final static int DATA_HARVEST_USB = 40;
	public final static int DATA_HARVEST_CF = 45;
	public final static int IMAGIWORKS_SERIAL = 50;
	public final static int IMAGIWORKS_SD = 55;
	public final static int PASCO_SERIAL = 60;
	public final static int CCPROBE_VERSION_0 = 70;
	public final static int CCPROBE_VERSION_1 = 71;
	public final static int CCPROBE_VERSION_2 = 72;
	public final static int COACH = 80;
	
	Ticker ticker = null;
		
	Hashtable deviceTable = new Hashtable();
	Hashtable configTable = new Hashtable();
	
	/**
	 * 
	 */
	public JavaDeviceFactory()
	{
		ticker = new JavaTicker();
	}

	/* (non-Javadoc)
	 * @see org.concord.sensor.DeviceFactory#createDevice(org.concord.sensor.DeviceConfig)
	 */
	public SensorDevice createDevice(DeviceConfig config)
	{
		int id = config.getDeviceId();
		String configStr = config.getConfigString();
		String deviceConfigId = "" + id + ":" + configStr;
		SensorDevice existingDevice = 
			(SensorDevice)deviceTable.get(deviceConfigId);
		if(existingDevice != null) {
			return existingDevice;
		}
		
		String className = null;
		SensorDevice device = null;
		
		switch(id) {
			case PSEUDO_DEVICE:
				device = new PseudoJavaSensorDevice();
				break;
			case VERNIER_GO_LINK:
				className = "org.concord.sensor.nativelib.NativeVernierSensorDevice";
				break;
			case TI_CONNECT:
				className = "org.concord.sensor.nativelib.NativeTISensorDevice";
				break;				
			case FOURIER:
			    className = "org.concord.sensor.dataharvest.DataHarvestSensorDevice";
			    break;
			case DATA_HARVEST_USB:
			case DATA_HARVEST_CF:
			case IMAGIWORKS_SERIAL:
			case IMAGIWORKS_SD:
			case PASCO_SERIAL:
			case COACH:
				device = null;
				break;
				
			// TODO: need to handle config string so
			// the serial port can be specified
			case CCPROBE_VERSION_0:
			    className = "org.concord.sensor.cc.CCInterface0";
			    break;			    
			case CCPROBE_VERSION_1:
				className = "org.concord.sensor.cc.CCInterface1";
				break;
			case CCPROBE_VERSION_2:
			    className = "org.concord.sensor.cc.CCinterface2";
			    break;
		}

		if(className != null) {
			try {
				Class sensDeviceClass = 
					getClass().getClassLoader().loadClass(className);
				
				device = (SensorDevice) sensDeviceClass.newInstance();
				
				device.open(config.getConfigString());
				
			} catch (Exception e) {
				e.printStackTrace();
				
			}
		}

		deviceTable.put(deviceConfigId, device);
		configTable.put(device, deviceConfigId);
		return device;		
	}

	public void destroyDevice(SensorDevice device)
	{
		device.close();
		
		String configStr = (String)configTable.get(device);		
		deviceTable.remove(configStr);
		configTable.remove(device);
		
	}
}

/*
 * Last modification information:
 * $Revision: 1.4 $
 * $Date: 2005-01-12 04:13:22 $
 * $Author: scytacki $
 *
 * Licence Information
 * Copyright 2004 The Concord Consortium 
*/
package org.concord.sensor.device.impl;

import org.concord.sensor.DeviceConfig;
import org.concord.sensor.cc.CCInterface0;
import org.concord.sensor.cc.CCInterface1;
import org.concord.sensor.cc.CCInterface2;
import org.concord.sensor.device.DeviceFactory;
import org.concord.sensor.device.SensorDevice;


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
		String className = null;
		SensorDevice device = null;
		
		switch(id) {
			case VERNIER_GO_LINK:
				className = "org.concord.sensor.nativelib.NativeVernierSensorDevice";
				break;
			case TI_CONNECT:
			case FOURIER:
			case DATA_HARVEST_USB:
			case DATA_HARVEST_CF:
			case IMAGIWORKS_SERIAL:
			case IMAGIWORKS_SD:
			case PASCO_SERIAL:
			case COACH:
				device = null;
				
			// TODO: need to handle config string so
			// the serial port can be specified
			case CCPROBE_VERSION_0:
				device =  new CCInterface0();
				
			case CCPROBE_VERSION_1:
				
				device = new CCInterface1();
				
			case CCPROBE_VERSION_2:
				device = new CCInterface2();
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
		
		return device;		
	}

	public void destroyDevice(SensorDevice device)
	{
		device.close();
	}
}

/*
 * Last modification information:
 * $Revision: 1.2 $
 * $Date: 2004-12-13 17:53:55 $
 * $Author: scytacki $
 *
 * Licence Information
 * Copyright 2004 The Concord Consortium 
*/
package org.concord.sensor.device;

import java.lang.reflect.Constructor;

import org.concord.framework.text.UserMessageHandler;
import org.concord.sensor.DeviceConfig;
import org.concord.sensor.DeviceFactory;
import org.concord.sensor.SensorDevice;
import org.concord.sensor.cc.CCInterface0;
import org.concord.sensor.cc.CCInterface1;
import org.concord.sensor.cc.CCInterface2;


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
	public SensorDevice createDevice(DeviceConfig config, UserMessageHandler messager)
	{
		int id = config.getDeviceId();
		String className = null;
		
		switch(id) {
			case VERNIER_GO_LINK:
				className = "org.concord.sensor.nativelib.NativeSensorDevice";
				break;
			case TI_CONNECT:
			case FOURIER:
			case DATA_HARVEST_USB:
			case DATA_HARVEST_CF:
			case IMAGIWORKS_SERIAL:
			case IMAGIWORKS_SD:
			case PASCO_SERIAL:
			case COACH:
				return null;
				
			// TODO: need to handle config string so
			// the serial port can be specified
			case CCPROBE_VERSION_0:
				return new CCInterface0(ticker, messager);
				
			case CCPROBE_VERSION_1:
				
				return new CCInterface1(ticker, messager);
				
			case CCPROBE_VERSION_2:
				return new CCInterface2(ticker, messager);
		}

		// if we are here we have a classname
		try {
			Class sensDeviceClass = 
				getClass().getClassLoader().loadClass(className);
			Constructor constructor = sensDeviceClass.getConstructor(
					new Class [] {Ticker.class, UserMessageHandler.class});
			
			AbstractSensorDevice device = (AbstractSensorDevice)
			constructor.newInstance(new Object [] {ticker, messager});
			
			device.deviceOpen(config.getConfigString());
			
			return device;
		} catch (Exception e) {
			e.printStackTrace();
			
		}
		
		return null;
	}

	public void destroyDevice(SensorDevice device)
	{
		((AbstractSensorDevice)device).deviceClose();
	}
}

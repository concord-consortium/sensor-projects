/*
 * Last modification information:
 * $Revision: 1.1 $
 * $Date: 2004-12-24 15:34:59 $
 * $Author: scytacki $
 *
 * Licence Information
 * Copyright 2004 The Concord Consortium 
*/
package org.concord.sensor.device.impl;

import java.lang.reflect.Constructor;

import org.concord.framework.text.UserMessageHandler;
import org.concord.sensor.DeviceConfig;
import org.concord.sensor.DeviceFactory;
import org.concord.sensor.SensorDataProducer;
import org.concord.sensor.cc.CCInterface0;
import org.concord.sensor.cc.CCInterface1;
import org.concord.sensor.cc.CCInterface2;
import org.concord.sensor.device.SensorDevice;
import org.concord.sensor.device.Ticker;


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
	public SensorDataProducer createDevice(DeviceConfig config, UserMessageHandler messager)
	{
		int id = config.getDeviceId();
		String className = null;
		SensorDevice device = null;
		
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
				Constructor constructor = sensDeviceClass.getConstructor(
						new Class [] {Ticker.class, UserMessageHandler.class});
				
				device = (SensorDevice)
				constructor.newInstance(new Object [] {ticker, messager});
				
				device.open(config.getConfigString());
				
			} catch (Exception e) {
				e.printStackTrace();
				
			}
		}
		
		if (device == null) {
			return null;
		}
		
		
		return null;
	}

	public void destroyDevice(SensorDataProducer device)
	{
		device.close();
	}
}

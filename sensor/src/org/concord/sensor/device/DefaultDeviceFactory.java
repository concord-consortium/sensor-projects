/*
 * Last modification information:
 * $Revision: 1.1 $
 * $Date: 2004-12-10 07:22:02 $
 * $Author: scytacki $
 *
 * Licence Information
 * Copyright 2004 The Concord Consortium 
*/
package org.concord.sensor.device;

import org.concord.framework.text.UserMessageHandler;
import org.concord.sensor.DeviceConfig;
import org.concord.sensor.DeviceFactory;
import org.concord.sensor.SensorDevice;
import org.concord.sensor.cc.CCInterface0;
import org.concord.sensor.cc.CCInterface1;
import org.concord.sensor.cc.CCInterface2;


/**
 * DefaultDeviceFactory
 * Class name and description
 *
 * Date created: Dec 1, 2004
 *
 * @author scott<p>
 *
 */
public class DefaultDeviceFactory
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
	UserMessageHandler messager = null;
	
	/**
	 * 
	 */
	public DefaultDeviceFactory(Ticker ticker, UserMessageHandler messager)
	{
		this.ticker = ticker;
		this.messager = messager;
	}

	/* (non-Javadoc)
	 * @see org.concord.sensor.DeviceFactory#createDevice(org.concord.sensor.DeviceConfig)
	 */
	public SensorDevice createDevice(DeviceConfig config)
	{
		int id = config.getDeviceId();
		switch(id) {
			case VERNIER_GO_LINK:
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

		return null;
	}

}

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

/*
 * Last modification information:
 * $Revision: 1.17 $
 * $Date: 2005-11-02 04:48:53 $
 * $Author: scytacki $
 *
 * Licence Information
 * Copyright 2004 The Concord Consortium 
*/
package org.concord.sensor.device.impl;

import java.util.Hashtable;

import org.concord.sensor.DeviceConfig;
import org.concord.sensor.device.DeviceFactory;
import org.concord.sensor.device.DeviceIdAware;
import org.concord.sensor.device.SensorDevice;
import org.concord.sensor.impl.Ticker;


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
	implements DeviceFactory, DeviceID
{	
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
				className = "org.concord.sensor.pseudo.JavaPseudoSensorDevice";
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
			    className = "org.concord.sensor.dataharvest.DataHarvestSensorDevice";
			    break;			    
			case PASCO_SERIAL:
			    className = "org.concord.sensor.pasco.SW500SensorDevice";
			    break;
			case DATA_HARVEST_CF:
			case IMAGIWORKS_SERIAL:
			case IMAGIWORKS_SD:
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
				
				if(device instanceof DeviceIdAware) {
				    ((DeviceIdAware)device).setDeviceId(id);
				}
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

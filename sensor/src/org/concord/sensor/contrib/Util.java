/*
 * Created on Feb 22, 2005
 *
 */
 
package org.concord.sensor.contrib;
 
import org.concord.framework.data.DataDimension;
import org.concord.sensor.ExperimentRequest;
import org.concord.sensor.impl.ExperimentConfigImpl;
import org.concord.sensor.SensorConfig;
import org.concord.sensor.SensorRequest;

/**
 * some static helper functions
 * 
 * @author Dmitry Markman
 *
 */
 
import org.concord.framework.text.UserMessageHandler;
import org.concord.sensor.SensorDataProducer;
import org.concord.sensor.SensorDataManager;
import org.concord.sensor.DeviceConfig;
import org.concord.sensor.device.impl.InterfaceManager;
import org.concord.sensor.device.impl.DeviceConfigImpl;

public class Util
{
	public static SensorDataProducer getSensorDataProducer(int deviceId,int sensorType){
	    return getSensorDataProducer(deviceId,sensorType, null,null);
	}

	public static SensorDataProducer getSensorDataProducer(int deviceId, int sensorType, String configString,UserMessageHandler messenger){
		SensorDataManager  sdManager = new InterfaceManager(messenger);
		DeviceConfig [] dConfigs = new DeviceConfig[1];
		dConfigs[0] = new DeviceConfigImpl(deviceId, null);		
		((InterfaceManager)sdManager).setDeviceConfigs(dConfigs);
		org.concord.sensor.ExperimentRequest request = new DefaultExperimentRequest(sensorType);
		SensorDataProducer producer = sdManager.createDataProducer();
		if(producer != null){
		    org.concord.sensor.ExperimentConfig ec = producer.configure(request);
		    System.out.println("11 ExperimentConfig "+ec); 
		    if(ec != null){
		        org.concord.sensor.SensorConfig []scs = ec.getSensorConfigs();
		        if(scs != null){
		            for(int i = 0; i < scs.length; i++){
		               org.concord.sensor.SensorConfig sc = scs[i];
		               String name = sc.getName();
		               String portName = sc.getPortName();
		               System.out.println("name "+name+" portName "+portName+" type "+sc.getType()); 
		               System.out.println("getUnit "+sc.getUnit().getDimension()); 
		               if(sc instanceof ccsd.vernier.SensorConfig){
		                    ccsd.vernier.SensorConfig vsc = (ccsd.vernier.SensorConfig)sc;
		                    System.out.println("getNumSensorParams "+vsc.getNumSensorParams()); 
		                }
		            }
		        }
		    }
	    }
		return producer;
	}
}

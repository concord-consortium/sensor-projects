package org.concord.sensor.device;

import org.concord.framework.text.UserMessageHandler;
import org.concord.waba.extra.io.DataStream;

import waba.sys.Vm;
import waba.util.Vector;

public class SensorFactory
{
	public static final int INVALID_PROBE = 0xFFFFFFFF;
	static Vector producers = new Vector();

	// This is the ticker that will for all the interfacemanagers
	// created by this factory.
	static Ticker ticker = null;
	static UserMessageHandler messageHandler = null;
	
	public static void setTicker(Ticker t)
	{
		ticker = t;
	}

	public static void setUserMessageHandler(UserMessageHandler h)
	{
		messageHandler = h;
	}
	
	/**
	 * You must call setTicker before registering any procuders
	 * @param pp
	 */
	public static void registerProducer(SensorProducer pp)
	{
		producers.add(pp);
		pp.setTicker(ticker);
		pp.setUserMessageHandler(messageHandler);
	}

	public static SensorProducer findProducer(int sensorGlobalId)
	{
		short producerId = (short)(sensorGlobalId >> 16);
		SensorProducer pp = null;		
		for(int i=0; i<producers.getCount(); i++){
			pp = (SensorProducer)producers.get(i);
			if(pp.getId() == producerId){
				return pp;
			}
		}
		return null;
	}

	public static SensorProducer [] getProducers()
	{
		Object [] tmpArray = producers.toObjectArray();
		int count = tmpArray.length;
		SensorProducer [] producerArray = new SensorProducer [count];
		Vm.copyArray(tmpArray, 0, producerArray, 0, count);
		return producerArray;
	}

	public static Sensor createProbeFromStream(DataStream in)
	{
		int sensorGlobalId = in.readInt();
		SensorProducer pp = findProducer(sensorGlobalId);
		short sensorType = (short)(sensorGlobalId & 0x0000FFFF);
		if(pp != null) return pp.loadSensor(sensorType, in);

		return null;		
	}

	public static void storeSensorToStream(Sensor sensor, DataStream out)
	{
		if(sensor == null){
			out.writeInt(INVALID_PROBE);
		} else {
			int sensorGlobalId = sensor.getGlobalId();
			out.writeInt(sensorGlobalId);
			SensorProducer pp = sensor.getProducer();
			pp.saveSensor(sensor, out);
		}			
	}

	public static DefaultSensorDevice getInterface(int id)
	{
		DefaultSensorDevice im = null;

		SensorProducer pp = null;
		for(int i=0; i<producers.getCount(); i++){
			pp = (SensorProducer)producers.get(i);
			im = pp.createInterface(id);
			if(im != null) return im;
		}
		return null;
	}
}

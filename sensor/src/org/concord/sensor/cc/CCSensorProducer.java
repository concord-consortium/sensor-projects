package org.concord.sensor.cc;

import org.concord.sensor.device.Sensor;
import org.concord.sensor.device.SensorDevice;
import org.concord.sensor.device.SensorProducer;
import org.concord.sensor.device.impl.SensorDataProducerImpl;

public class CCSensorProducer extends SensorProducer
{
	// This id must be obtained from Concord Consortium.  Because
	// these numbers must remain unique
	// if you are not going to work with Concord Consortium please
	// choose a number above 16384 (14 bits)
	public final static short CC_SENSOR_PRODUCER_ID = 0; 
	
	protected final static String [] mySensorNames = 
	{"Temperature", "Light", "SmartWheel", "RawData","Force","VoltageCurrent"};

	public final static short SENSOR_UNDEFINED 		= -1;
	public final static short SENSOR_THERMAL_COUPLE 	= 0;
	public final static short SENSOR_LIGHT 			= 1;
	public final static short SENSOR_SMART_WHEEL		= 2;
	public final static short SENSOR_RAW_DATA        = 3;
	public final static short SENSOR_FORCE        	= 4;
	public final static short SENSOR_VOLT_CURRENT    = 5;
	
	protected final static String [] myInterfaceNames =
	{"1 Port Interface(CCA2D2 v0)", 
			"1 Port Interface(CCA2D2 v1)", 
			"2 Port Interface (CCA2D2 v2)", };

	public final static int INTERFACE_0 = 0;
	public final static int INTERFACE_1 = 1;
	public final static int INTERFACE_2 = 2;

	public static int BUF_SIZE = 1000;

	
	public CCSensorProducer()
	{
		sensorNames = mySensorNames;
		interfaceNames = myInterfaceNames;
	}

	public short getId()
	{
		return CC_SENSOR_PRODUCER_ID;
	}
	
	public Sensor createSensor(boolean init, short probeType){
		Sensor newProb = null;
		switch(probeType){
		case SENSOR_THERMAL_COUPLE:
			newProb = new CCThermalCouple(init, probeType, this);
			break;
		case SENSOR_LIGHT:
			newProb = new CCLightIntens(init, probeType, this);
			break;
		case SENSOR_SMART_WHEEL:
			newProb = new CCSmartWheel(init, probeType, this);
			break;
		case SENSOR_RAW_DATA:
			newProb = new CCRawData(init, probeType, this);
			break;
		case SENSOR_FORCE:
			newProb = new CCForce(init, probeType, this);
			break;
		case SENSOR_VOLT_CURRENT:
			newProb = new CCVoltCurrent(init, probeType, this);
			break;
		}

		return newProb;
	}

	public SensorDevice createInterface(int id)
	{
		// Make a new ticker if this one is already being used 
		if(ticker.getInterfaceManager() != null) {
			ticker = ticker.createNew();			
		}
				
		switch(id)
		{
		case INTERFACE_0:
			return new CCInterface0();
		case INTERFACE_1:
			return new CCInterface1();
		case INTERFACE_2:			
			return new CCInterface2();
		}

		return null;
	}
}


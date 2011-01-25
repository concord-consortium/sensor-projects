package org.concord.sensor.pasco;

import org.concord.sensor.SensorConfig;
import org.concord.sensor.SensorRequest;
import org.concord.sensor.device.impl.SensorConfigImpl;
import org.concord.sensor.impl.SensorUnit;


public class PasportSensorMeasurement extends SensorConfigImpl
{
	PasportSensorDataSheet dataSheet = null;
	
	// ushort
	int id;
	// NUL terminated var string
	String name;
	// NUL terminated var string
	String unitStr;
	// ubyte - this is just called type in the spec.  it is 
	//    renamed because the sensorconfig has a type as well
	int measTypeId;
	
	MeasurementType measType;
	
	// ubyte
	int typeDescLength;
	// variable format set of bytes
	byte [] typeDesc;
	// ubyte - flags for visibility of measurement to user 
	int visible;
	// fixed - 4 byte fixed decimal
	float accuracy;
	// ubyte - number digits left of decimal point
	int precision; 
	// ubyte - flags for prefered display format
	int displayFormat;
	// fixed - lowest posible value of the measurement
	float minValue;
	// fixed - highest posible value of the measurement
	float maxValue;
	// fixed - typical lowest value
	float typicalMin;
	// fixed - typical highets value
	float typicalMax;	
	
	// parsing helpers
	int offsetInSample = 0;
	
	
	public PasportSensorMeasurement(PasportSensorDataSheet ds,
			ByteBufferStreamReversed bb)
	{
		dataSheet = ds;
		
		// parse the setup information
		
		id = bb.readUShort();
		name = bb.readNulTermString();
		unitStr = bb.readNulTermUnitString();
		measTypeId = bb.readUByte();
		typeDescLength = bb.readUByte();
		switch(measTypeId) {
			case 3:
				// simple calibration
			case 7:
				// user calibration
				measType = new SimpleMeasurementType(bb, typeDescLength);
				break;
			case 4:
				measType = new MacroMeasurement(bb, typeDescLength);
				break;
			default:
				// typeDesc - variable format set of bytes
				typeDesc = bb.skipAndSave(typeDescLength);
				break;
		}
		
		visible = bb.readUByte(); 
			
		// fixed - 4 byte fixed decimal
		accuracy = bb.readFixed();
		
		// ubyte - number digits right of decimal point
		precision = bb.readUByte();

		// ubyte - flags for prefered display format
		displayFormat = bb.readUByte();

		// fixed - lowest posible value of the measurement
		minValue = bb.readFixed();
		
		// fixed - highest posible value of the measurement
		maxValue = bb.readFixed();
		
		// fixed - typical lowest value
		typicalMin = bb.readFixed();
		
		// fixed - typical highest value
		typicalMax = bb.readFixed();		
		
		////////////////////////
	    // configure the Sensor settings
		////////////////////////
		
		setPort(id);
		setName(name);
		setStepSize(accuracy);
		SensorUnit unit = new SensorUnit(unitStr);
		setUnit(unit);
		setConfirmed(true);
		
		// this is the hard part we have to figure out what type
		// of sensor this is. It seems the only way is to check
		// the name
		if((name.indexOf("Temperature") >= 0) && 
				(unitStr.indexOf("C") > 0)) {
			if(name.indexOf("Probe") >= 0) {
				setType(SensorConfig.QUANTITY_TEMPERATURE_WAND);				
			} else {
				setType(SensorConfig.QUANTITY_TEMPERATURE);	
			} 
		} else if(name.indexOf("Position") >= 0) {
			setType(SensorConfig.QUANTITY_DISTANCE);
		} else if(name.indexOf("Light") >= 0) {
			setType(SensorConfig.QUANTITY_LIGHT);			
		} else if(name.indexOf("Humidity") >=0) {
			setType(SensorConfig.QUANTITY_RELATIVE_HUMIDITY); 				
		} else if((name.indexOf("Force") >= 0) &&
				(unitStr.indexOf("N") >= 0)){
			// need to choose the reversed measurement
			setType(SensorConfig.QUANTITY_FORCE);
		} else if((name.indexOf("Voltage") >= 0) &&
				(unitStr.indexOf("V") >= 0)) {
			setType(SensorConfig.QUANTITY_VOLTAGE);
		} else if((name.indexOf("Current") >= 0) &&
				(unitStr.indexOf("A") >= 0)) {
			setType(SensorConfig.QUANTITY_CURRENT);
		} else if((name.indexOf("Pressure") >=0) &&
				(unitStr.indexOf("kPa") >= 0)){
			setType(SensorConfig.QUANTITY_GAS_PRESSURE);
		} else if(name.indexOf("Sound") >=0){
			setType(SensorConfig.QUANTITY_SOUND_INTENSITY);
		} else {
			setType(SensorConfig.QUANTITY_UNKNOWN);
		}
	}
	
	protected int getSampleSize()
	{
		switch(measTypeId) {
			case 0:
				// direct value - fixed 4 bytes 
				return 4;
			case 1:
				// raw analog count - 1 to 4 bytes based
				// on one description byte
			case 2:
				// raw digital word - lenth based on 
				// one description byte I'll assume it is less
				// than 128 so we don't need to worry about the
				// sign
				return typeDesc[0];
			default:
				return 0;
		}
	}
	
	protected float readSample(byte [] buf, int sampleStart) 
	{
		int offset = sampleStart + offsetInSample;
		
		/**
		 * If this is a complex measurement that combines two other
		 * measurements. Then measType takes care of it.  That will end up calling this method 
		 * again but it should be on a different instance of this class.
		 */
		if(measType != null) {
			return measType.getValue(dataSheet, buf, sampleStart);
		}
		
		switch(measTypeId) {
			case 0:
				return ByteBufferStreamReversed.readFixed(buf, offset);
			case 1:
			case 2:
				switch(typeDesc[0]) {
					case 1:
						return ByteBufferStreamReversed.readUByte(buf, offset);
					case 2:
						return ByteBufferStreamReversed.readUShort(buf, offset);
					case 4:
						return ByteBufferStreamReversed.readInt(buf, offset);
				}
				return 0;				
		}
		
		return 0f;
	}
	
	/**
	 * I do not remember what being visible means. However from the code that
	 * was deciding if a sensor request matched one of these measurement, the 
	 * measurement was not considered valid if it was not visible
	 *  
	 * @return
	 */
	protected boolean isVisible()
	{
		return (visible & 0x2) == 0;
	}
	
	/**
	 * return a score 0 to 100
	 * this should be moved to a more abstract class
	 * perhaps it can be moved totally outside of this
	 * class altogether.
	 * 
	 * @param request
	 * @return
	 */
	protected int matches(SensorRequest request)
	{
		if((visible & 0x2) != 0) {
			// this measurement isn't visible
			return 0;
		}
        if(request.getType() == SensorConfig.QUANTITY_TEMPERATURE){
        	if(getType() == SensorConfig.QUANTITY_TEMPERATURE) {
        		return 75;
        	}
        	if(getType() == SensorConfig.QUANTITY_TEMPERATURE_WAND) {
        		return 100;
        	}
        } 
        
        if(getType() == SensorConfig.QUANTITY_DISTANCE) {
        	if(request.getType() == SensorConfig.QUANTITY_DISTANCE ||
                    request.getType() == SensorConfig.QUANTITY_VELOCITY) {
        		return 100;
        	}
        }

        if(getType() == request.getType()) {
        	return 100;
        }
        
		return 0;
	}
	
	protected String getStringView()
	{
		String ret = "";
		ret += "id: " + id + "\n";
		ret += "name: " + name + "\n";
		ret += "units: " + unitStr + "\n";
		ret += "type: " + measTypeId + "\n";
		ret += "typeDescLength: " + typeDescLength + "\n";
		if(measType != null) {
			ret += measType.getStringView();
		} else {
			if(typeDesc != null) {
				ret += "typeDesc[0]" + typeDesc[0] + "\n";
			}
		}
		ret += "visible: " + visible + "\n";
		ret += "accuracy: " + accuracy + "\n";

		/*
		// ubyte - number digits left of decimal point
		int precision; 
		// ubyte - flags for prefered display format
		int displayFormat;
		// fixed - lowest posible value of the measurement
		float minValue;
		// fixed - highest posible value of the measurement
		float maxValue;
		// fixed - typical lowest value
		float typicalMin;
		// fixed - typical highets value
		float typicalMax;	
		*/
		return ret;
	}
}

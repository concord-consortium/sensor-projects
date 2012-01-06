package org.concord.sensor.pasco.datasheet;




public class PasportSensorMeasurement
{

	PasportSensorDataSheet dataSheet = null;
	
	// ushort
	int id;
	// NUL terminated var string
	String name;
	// NUL terminated var string
	String unitStr;
	// ubyte - type of measurement
	int type;
	// ubyte
	int typeDescriptorLength;
	// variable format set of bytes
	byte [] typeDescriptor;
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

	// some types are complex so they have separate objects
	MeasurementType typeDescriptorObject;
	
	public PasportSensorMeasurement(PasportSensorDataSheet ds, ByteBufferStreamReversed bb)
	{
		dataSheet = ds;
		
		// parse the setup information
		
		id = bb.readUShort();
		name = bb.readNulTermString();
		unitStr = bb.readNulTermUnitString();
		type = bb.readUByte();
		typeDescriptorLength = bb.readUByte();
		switch(type) {
			case 3:
				// simple calibration
			case 7:
				// user calibration
				typeDescriptorObject = new SimpleCalibration(bb, typeDescriptorLength, type);
				break;
			case 4:
				typeDescriptorObject = new MacroCalculation(bb, typeDescriptorLength);
				break;
			default:
				// typeDesc - variable format set of bytes
				typeDescriptor = bb.skipAndSave(typeDescriptorLength);
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
	}
	
	protected int getSampleSize()
	{
		switch(type) {
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
				return typeDescriptor[0];
			default:
				return 0;
		}
	}
	
	public float readSample(byte [] buf, int sampleStart) 
	{
		int offset = sampleStart + offsetInSample;
		
		/**
		 * If this is a complex measurement that combines two other
		 * measurements. Then measType takes care of it.  That will end up calling this method 
		 * again but it should be on a different instance of this class.
		 */
		if(typeDescriptorObject != null) {
			return typeDescriptorObject.getValue(dataSheet, buf, sampleStart);
		}
		
		switch(type) {
			case 0:
				return ByteBufferStreamReversed.readFixed(buf, offset);
			case 1:
			case 2:
				switch(typeDescriptor[0]) {
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
	 * The visible field is a collection of bits to indicate if if a particular measurement
	 * should be displayed to the user.  Some measurements are hidden, or they are "raw"
	 * un-calibrated voltage measurements.  These will not be marked visible. 
	 *  
	 * @return
	 */
	public boolean isVisible()
	{
		return (visible & 0x2) == 0;
	}
	
	public int getId() {
		return id;
	}

	public String getName() {
		return name;
	}

	public String getUnitStr() {
		return unitStr;
	}

	public int getType() {
		return type;
	}

	public int getVisible() {
		return visible;
	}

	public float getAccuracy() {
		return accuracy;
	}

	public int getPrecision() {
		return precision;
	}

	public int getDisplayFormat() {
		return displayFormat;
	}

	public float getMinValue() {
		return minValue;
	}

	public float getMaxValue() {
		return maxValue;
	}

	public float getTypicalMin() {
		return typicalMin;
	}

	public float getTypicalMax() {
		return typicalMax;
	}

	public PasportSensorDataSheet getDataSheet() {
		return dataSheet;
	}
	
	protected void print(Printer p)
	{
		p.puts("SensorMeasurement");
		p = new Printer("  ", p);
		p.puts("id: " + id);
		p.puts("name: " + name);
		p.puts("units: " + unitStr);
		p.puts("type: " + type);
		p.puts("typeDescriptorLength: " + typeDescriptorLength);
		if(typeDescriptorObject != null) {
			typeDescriptorObject.print(p);
		} else {
			if(typeDescriptor != null) {
				p.puts("typeDescriptor[0]:" + typeDescriptor[0]);
			}
		}
		p.puts("visible: " + visible);
		p.puts("accuracy: " + accuracy);
		p.puts("precision: " + precision);
		p.puts("displayFormat: " + displayFormat);
		p.puts("minValue: " + minValue);
		p.puts("maxVale: " + maxValue);
		p.puts("typicalMin: " + typicalMin);
		p.puts("typicalMax: " + typicalMax);
	}
}

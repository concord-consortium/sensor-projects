package org.concord.sensor.pasco.datasheet;

import java.util.ArrayList;



public class PasportSensorDataSheet 
{
	// ushort is 2 byte unsigned number
	// unit is a 4 byte unsigned number
	
	// ushort
	int id;	
	// ushort
	int maxDataSheetSize;	
	// ushort
	int dataSheetLength;	
	// ushort
	int extendedDataSheetLength;	
	// ushort
	int classCode;
	// 4 byte string padded with NUL
	String version;	
	// 10 byte string padded with spaces
	String modelNumber;	
	// ushort
	int numberMeasurements;	
	// icon 32 bytes
	// byte [] icon;
	// NUL terminated variable string
	String name;
	// ~uint msb is units 0=hz, 1=sec
	// the value is converted to sec when it is read in
	// in the docs this is the minRate but when it is stored as second it
	// really is the maxPeriod
	float maxPeriod;
	// ~uint msb is units 0=hz, 1=sec
	// the value is converted to sec when it is read in
	// in the docs this is the maxRate but when it is stored as second it
	// really is the minPeriod
	float minPeriod;
	// ~uint msb is units 0=hz, 1=sec
	// the value is converted to sec when it is read in
	// in the docs this is the defaultRate but as seconds is defaultPeriod
	float defaultPeriod;
	// uint micro secs - we are mis-representing this by 1 bit
	int latency;
	// uint milli secs - we are mis-representing this by 1 bit
	int warmUp;
	
	PasportSensorMeasurement [] measurements;
	
	int sampleDataSize;
	
	// uint - 4 byte checksum
	// int checksum;
	
	public PasportSensorDataSheet(ByteBufferStreamReversed bb)
	{
		id = bb.readUShort();
		maxDataSheetSize = bb.readUShort();
		dataSheetLength = bb.readUShort();
		extendedDataSheetLength = bb.readUShort();
		classCode = bb.readUShort();
		version = bb.readFixedLengthString(4);
		modelNumber = bb.readFixedLengthString(10);
		numberMeasurements = bb.readUShort();
		// icon 32 bytes
		bb.skip(32);
		name = bb.readNulTermString();
		maxPeriod = readSecsOrHz(bb);
		minPeriod = readSecsOrHz(bb);
		defaultPeriod = readSecsOrHz(bb);

		// uint micro secs - we are mis-representing this by 1 bit
		latency = bb.readInt();
		// uint milli secs - we are mis-representing this by 1 bit
		warmUp = bb.readInt();

		measurements = new PasportSensorMeasurement [numberMeasurements];
		sampleDataSize = 0;
		for(int i=0; i<numberMeasurements; i++) {
			measurements[i] = new PasportSensorMeasurement(this, bb);		
			
			if(measurements[i].type <= 2) {
				measurements[i].offsetInSample = sampleDataSize;
				sampleDataSize += measurements[i].getSampleSize();
			}
		}

		
		// checksum
		// I should check it
	}
	
	public int getId() {
		return id;
	}

	public int getMaxDataSheetSize() {
		return maxDataSheetSize;
	}

	public int getClassCode() {
		return classCode;
	}

	public String getVersion() {
		return version;
	}

	public String getModelNumber() {
		return modelNumber;
	}

	public String getName() {
		return name;
	}

	public float getMaxPeriod() {
		return maxPeriod;
	}

	public float getMinPeriod() {
		return minPeriod;
	}

	public float getDefaultPeriod() {
		return defaultPeriod;
	}

	public int getLatency() {
		return latency;
	}

	public int getWarmUp() {
		return warmUp;
	}

	public PasportSensorMeasurement [] getMeasurements() {
		return measurements;
	}
	
	
	/**
	 * There are some values that are either secs or hz
	 * based on the msb.  This reads can converts them all
	 * to secs
	 * 
	 * @param bb
	 * @return
	 */
    protected float readSecsOrHz(ByteBufferStreamReversed bb)
    {
    	int raw = bb.readInt();
    	
    	int val = raw & 0x7FFFFFFF; 
    		
    	if((raw & 0x80000000) == 0) {
    		return 1 / (float)val;
    	} else {
    		return val;
    	}	
    }
    

	
	/**
	 * This computes the size of one sample based on all the measurement
	 * types
	 * 
	 * @return
	 */
	public int getSampleSize()
	{
		return sampleDataSize;
	}
		
	public void print(Printer p)
	{
		p.puts("id: " + id);
		p.puts("maxDataSheetSize: " + maxDataSheetSize);
		p.puts("dataSheetLength: " + dataSheetLength); 
		p.puts("extendedDataSheetLength: " + extendedDataSheetLength);
		p.puts("classCode: " + classCode);
		p.puts("version: " + version);
		p.puts("modelNumber: " + modelNumber);
		p.puts("numberMeasurements: " + numberMeasurements);
		p.puts("name: " + name);
		p.puts("maxPeriod: " + maxPeriod);
		p.puts("minPeriod: " + minPeriod);
		p.puts("defaultPeriod: " + defaultPeriod);
		p.puts("latency: " + latency);
		p.puts("warmUp: " + warmUp);

		for(int i=0; i<measurements.length; i++) {
			Printer pMeas = new Printer("  ", p);
			measurements[i].print(pMeas);
		}
	}
		
	public void printSample(byte [] buffer, int offset, String indent)
	{
		ArrayList<StringBuffer> lines = new ArrayList<StringBuffer>();
		StringBuffer line = new StringBuffer();
		lines.add(line);
		for (PasportSensorMeasurement m : measurements) {
			line.append(m.name + ": " + m.readSample(buffer, offset) + "(" + m.unitStr + "), ");
			if(line.length() > 80){
				line = new StringBuffer();
				lines.add(line);
			}
		}
		if(line.length() > 0 || lines.size() > 1){
			for (StringBuffer stringBuffer : lines) {
				if(stringBuffer.length() == 0){
					continue;
				}
				String out = stringBuffer.substring(0, stringBuffer.length() - 2);
				System.out.println(indent + out);
			}
		}
	}
}

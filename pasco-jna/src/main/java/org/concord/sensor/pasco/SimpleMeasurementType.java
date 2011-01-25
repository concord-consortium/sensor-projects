package org.concord.sensor.pasco;


public class SimpleMeasurementType implements MeasurementType
{
	int inputId;

	float slope;
	float yIntersect;
	
	SimpleMeasurementType(ByteBufferStreamReversed bb, int length)
	{
		if(length < 18) {
			// this is an error
		}
		
		// user calibration
		inputId = bb.readUShort();
		//PasportSensorMeasurement input = dataSheet.measurements[inputId];
		//float value = input.readSample(buf, sampleStart);
		float inVal1 = bb.readFixed();
		float outVal1 = bb.readFixed();
		float inVal2 = bb.readFixed();
		float outVal2 = bb.readFixed();
		
		// sometimes this type of measurement appears to have
		// an extra byte
		if(length > 18) {
			bb.skip(length - 18);
		}
		
		slope = (outVal2 - outVal1) / (inVal2 - inVal1);
		yIntersect = outVal1 - slope * inVal1;
	}
	
	public float getValue(PasportSensorDataSheet ds, byte [] buf, int sampleStart)
	{
		PasportSensorMeasurement meas = ds.measurements[inputId];
		float input = meas.readSample(buf, sampleStart);

		// convert it
		return slope * input + yIntersect; 
	}
	
	public String getStringView()	
	{
		String ret = "input: " + inputId + "\n" +		
			"slope: " + slope + "\n" +
			"intersect: " + yIntersect + "\n";
		
		return ret;
	}
}

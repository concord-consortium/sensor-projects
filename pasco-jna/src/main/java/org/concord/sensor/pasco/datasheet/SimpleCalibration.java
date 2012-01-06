package org.concord.sensor.pasco.datasheet;


public class SimpleCalibration implements MeasurementType
{
	int inputId;
	float input1, output1, input2, output2;
	float slope;
	float yIntersect;

	boolean userCalibration;
	int userCalibrationFlags;
		
	SimpleCalibration(ByteBufferStreamReversed bb, int length, int type)
	{
		if(length < 18) {
			// this is an error
		}
		
		// user calibration
		inputId = bb.readUShort();
		if(type == 7) {
			userCalibration = true;
			userCalibrationFlags = bb.readUByte();
		}
		
		//PasportSensorMeasurement input = dataSheet.measurements[inputId];
		//float value = input.readSample(buf, sampleStart);
		input1 = bb.readFixed();
		output1 = bb.readFixed();
		input2 = bb.readFixed();
		output2 = bb.readFixed();
				
//		enum PascoUSBUserCalFlags
//		{
//			USER_CAL_ENTER_DATA_IN_1 = 0x01,
//			USER_CAL_ENTER_VALUE_1 = 0x02,
//			USER_CAL_ENTER_DATA_IN_2 = 0x04,
//			USER_CAL_ENTER_VALUE_2 = 0x08
//		};
		
		slope = (output2 - output1) / (input2 - input1);
		if(Math.abs(input1) > Math.abs(input2)){
			yIntersect = output1 - slope * input1;
		} else {
			yIntersect = output2 - slope * input2;
		}
	}
	
	public float getValue(PasportSensorDataSheet ds, byte [] buf, int sampleStart)
	{
		PasportSensorMeasurement meas = ds.measurements[inputId];
		float input = meas.readSample(buf, sampleStart);

		// convert it
		return slope * input + yIntersect; 
	}
	
	public void print(Printer p)	
	{
		p.puts("SimpleCalibration");
		p = new Printer("  ", p);
		p.puts("input: " + inputId);
		if(userCalibration){
			p.puts("userCalibrationFlags: 0b" + Integer.toBinaryString(userCalibrationFlags));
		}
		p.puts("input1: " + input1);
		p.puts("output1: " + output1);
		p.puts("input2: " + input2);
		p.puts("output2: " + output2);
		
		p.puts("slope: " + slope);
		p.puts("intersect: " + yIntersect);
	}
}

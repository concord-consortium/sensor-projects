package org.concord.sensor.pasco;


public class MacroCalculation
	implements MeasurementType
{
	int inputId;
	int type;
	float [] args;
	
	public MacroCalculation(ByteBufferStreamReversed bb, int size)
	{
		inputId = bb.readUShort();
		type = bb.readUByte();
		int numArgs = (size - 3) / 4;
		if(numArgs <= 0 ) {
			return;
		}

		args = new float[numArgs];
		for(int i=0; i < numArgs; i++) {
			args[i] = bb.readFixed();
		}
	}
	
	
	public float getValue(PasportSensorDataSheet ds, byte[] buf, int sampleStart)
	{
		PasportSensorMeasurement meas = ds.measurements[inputId];
		float input = meas.readSample(buf, sampleStart);

		switch(type) {
			case 1:
				// smooth not implemented
				return input;
			case 2:
				// rate averge arg[0] time window arg[1] scale factor
				// we'll ignore the average for now
				return input * args[1];
			case 3:
				// ultra sound position
				// assumes input is ping/echo time in usec
				return args[0] * input / 2000000f;
			case 4:
				// rotary pos change
				return args[0] * input / args[1];
			case 5:
				// derivative, not implemented
				// should return rate of change for each pair of points
				return input;
			case 6:
				// absorbance
				// 2.0 ? log( limit( 0.1, 100, input )  )
				return input;
			case 7:
				// linear conversion
				return input * args[0] + args[1];
			case 8:
				// two input combo
			case 9:
				// two input vector sum
			case 10:
				// 3 input vector sum
				return input;
		}
		// TODO Auto-generated method stub
		return 0;
	}

	public void print(Printer p)
	{
		p.puts("MacroCalculation");
		p = new Printer("  ", p);
		p.puts("input: " + inputId);
		p.puts("type: " + type);
		String sArgs = "";
		sArgs += "args: ";
		
		if(args == null) {
			sArgs += "null";
		} else {
			for(int i=0; i<args.length; i++) {
				sArgs += args[i] + " ";
			}
		}
		
		p.puts(sArgs);
	}
	
}

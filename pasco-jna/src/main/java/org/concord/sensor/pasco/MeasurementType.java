package org.concord.sensor.pasco;


public interface MeasurementType
{
	public float getValue(PasportSensorDataSheet ds, 
			byte [] buf, int sampleStart);
	
	public void print(Printer p);

}

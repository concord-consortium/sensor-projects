package org.concord.sensor.labquest.jna;


public class GCalibrationPage extends NGIOStructure 
{
	public final static int MAX_CALIBRATION_UNITS_CHARS_ON_SENSOR = 7;
	
	public float		CalibrationCoefficientA;
	public float		CalibrationCoefficientB;
	public float		CalibrationCoefficientC;
	public byte []		Units = new byte[MAX_CALIBRATION_UNITS_CHARS_ON_SENSOR];

}

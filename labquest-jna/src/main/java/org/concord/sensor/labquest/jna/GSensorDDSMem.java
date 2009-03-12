package org.concord.sensor.labquest.jna;


public class GSensorDDSMem extends NGIOStructure {
	public byte		MemMapVersion;
	public byte		SensorNumber;			//Identifies type of sensor; (SensorNumber >= 20) generally implies that
										//GSensorDDSRec is stored on the sensor hardware. Such sensors are called 'smart'.
	public byte []		SensorSerialNumber = new byte[3];	//[0][1][2] - serial number as 3-byte integer, Little-Endian (LSB first).
	public byte []		SensorLotCode = new byte[2];		//Lot code as 2-byte BCD date, [0] = YY, [1] == WW.
	public byte 		ManufacturerID;
	public byte []		SensorLongName = new byte[20];
	public byte []		SensorShortName = new byte[12];
	public byte		Uncertainty;
	public byte		SignificantFigures;		//sig figs in high nibble, decimal precision in low nibble
	public byte		CurrentRequirement;		//Number of mA(average) required to power sensor.
	public byte		Averaging;
	public float		MinSamplePeriod;		//seconds
	public float		TypSamplePeriod;		//seconds
	public short		TypNumberofSamples;
	public short		WarmUpTime;				//Time (in seconds) required for the sensor to have power before reaching equilibrium.
	public byte		ExperimentType;
	public byte		OperationType;			//This is a LabPro specific field.
										//Go! devices use this field to infer probe type(5 volt or 10 volt). See EProbeType.
	public byte		CalibrationEquation;	//See EEquationType.
	public float		YminValue;
	public float		YmaxValue;
	public byte		Yscale;
	public byte		HighestValidCalPageIndex;//First index is 0.
	public byte		ActiveCalPage;
	public GCalibrationPage []	CalibrationPage = new GCalibrationPage[3];
	public byte		Checksum;				//Result of XORing bytes 0-126.
	
	public static int getUnsignedVar(byte byteValue)
	{
		return ((int)byteValue)&0xFF;
	}

	public static int getUnsignedVar(short shortValue)
	{
		return ((int)shortValue)&0xFFFF;
	}
	
	public final static int kProbeTypeNoProbe = 0;
	public final static int kProbeTypeTime = 1;
	public final static int kProbeTypeAnalog5V = 2;
	public final static int kProbeTypeAnalog10V = 3;
	public final static int kProbeTypeHeatPulser = 4;
	public final static int kProbeTypeAnalogOut =5;
	public final static int kProbeTypeMD = 6;
	public final static int kProbeTypePhotoGate = 7;
	public final static int kProbeTypeDigitalCount = 10;
	public final static int kProbeTypeRotary = 11;
	public final static int kProbeTypeDigitalOut = 12;
	public final static int kProbeTypeLabquestAudio = 13;


}

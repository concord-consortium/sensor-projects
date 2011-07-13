package org.concord.sensor.labquest.jna;

import com.sun.jna.Pointer;
import com.sun.jna.Structure;

public interface LabQuest {

	public void open(String deviceName, Pointer hLibrary)
			throws LabQuestException;

	public void close() throws LabQuestException;

	/**
	 * isremotecollection active appears to always return false.
	 * 
	 * @return
	 * @throws LabQuestException
	 */
	public boolean isRemoteCollectionActive() throws LabQuestException;

	public void acquireExclusiveOwnership() throws LabQuestException;

	public String getSensorName(byte channel) throws LabQuestException;
	
	public int getSensorId(byte channel) throws LabQuestException;

	public void ddsMemReadRecord(byte channel, boolean strict)
			throws LabQuestException;

	public GSensorDDSMem ddsMemGetRecord(byte channel) throws LabQuestException;

	public void setMeasurementPeriod(byte channel, double desiredPeriod)
			throws LabQuestException;

	public void setSensorChannelEnableMask(int mask) throws LabQuestException;

	public void clearIO(byte channel) throws LabQuestException;

	public void startMeasurements() throws LabQuestException;

	public void stopMeasurements() throws LabQuestException;

	public int getNumberOfMeasurementsAvailable(byte channel)
			throws LabQuestException;

	public int readRawMeasurementsAnalog(byte channel, int[] pMeasurementsBuf,
			int maxCount) throws LabQuestException;

	public int readRawMeasurementsMotion(byte channel, int[] pMeasurementsBuf,
			long[] timestampBuf, int maxCount) throws LabQuestException;

	public float convertToVoltage(byte channel, int rawMeasurement,
			int probeType);

	public void setAnalogInput(byte channel, byte analogInput) 
		throws LabQuestException;

	public void setSamplingMode(byte channel, byte samplingMode)
			throws LabQuestException;

	public float calibrateData2(byte channel, int rawValue);

	public void printAttachedSensors() throws LabQuestException;

	public void sendCmdAndGetResponse(byte cmd, Structure params,
			Structure response) throws NGIOException;

}
package org.concord.sensor.d2pio.jna;

import java.io.IOException;
import java.nio.charset.Charset;

import org.junit.Test;

import com.sun.jna.Native;

public class D2PIOJNATest {
	private static D2PIOLibrary d2pLib;

	@Test
	public void generalTest() throws IOException{
		main(null);
	}

	public static void main(String[] args) throws IOException {

		try {
			D2PIOLibrary d2p;
			System.out.println("Default Charset=" + Charset.defaultCharset());
			System.out.println("start D2PIOJNATest.main");

			System.out.println("jna.encoding: " + System.getProperty("jna.encoding"));

			d2p = new D2PIOLibrary();

			if(!d2p.initLibrary()) {
				System.out.println("D2PIOLibrary.initLibrary() failed --bye");
				return;
			}

			if(!d2p.init()) {
				System.out.println("D2PIOLibrary.init() failed --bye");
				return;
			}

			String libVersion = d2p.getLibVersion();
			System.out.println("D2PIO_lib version: " + (libVersion != null ? libVersion : "ERROR!"));

			// d2p.setDebugLevel(D2PIOJNALibrary.D2PIO_TRACE_SEVERITY_LOWEST);

			boolean isDeviceAttached = d2p.isSensorAttached();
			System.out.println("Is D2PIO sensor attached? " + isDeviceAttached);

			D2PIOSensorList sensors = d2p.getSensorList();
			if (sensors == null) {
				System.out.println("D2PIOLibrary.getDeviceList() failed --bye");
				return;
			}
			System.out.println("D2PIO device count: " + sensors.getCount());

			D2PIOSensor sensor = sensors.getSensor(0);

			if (sensor != null) {
				System.out.println("D2PIO sensor name: " + sensor.getName());
				System.out.println("D2PIO friendly name: " + sensor.getFriendlyName());

				if (sensor.open()) {
					System.out.println("Open sensor succeeded!");

					int currStatus = sensor.sendCmdAndGetResponse(D2PIOJNALibrary.D2PIO_CMD_ID_GET_STATUS);

					if (currStatus == 0) {
						int channelMask = sensor.getMeasurementChannelAvailabilityMask();
						if (channelMask == 0) {
							System.out.println("No sensors found");
						} else {
							sensor.setMeasurementPeriod(0.100);
							sensor.sendCmdAndGetResponse(D2PIOJNALibrary.D2PIO_CMD_ID_START_MEASUREMENTS);
							Thread.sleep(1000);  //wait 1 second for measurements
							for (int channel = 0; channel < 32; channel++) {
								if (((1 << channel) & channelMask) != 0) {
									sensor.getMeasurementChannelSensorId(channel);
									sensor.getMeasurementChannelSensorDescription(channel);
									sensor.getMeasurementChannelSensorUnits(channel);
									int numericType = sensor.getMeasurementChannelNumericType(channel);
									if (numericType == D2PIOJNALibrary.D2PIO_NUMERIC_MEAS_TYPE_REAL64) {
										double[] calbMeasurements = sensor.readMeasurements(channel, 200);
										int numMeasurements = calbMeasurements != null ? calbMeasurements.length: 0;
										if (numMeasurements > 0) {
											double averageCalbMeasurement = 0.0;
											for (int i = 0; i < numMeasurements; i++) {
												averageCalbMeasurement += calbMeasurements[i];
											}
										  if (numMeasurements > 1) {
											  averageCalbMeasurement = averageCalbMeasurement / numMeasurements;
											}
											System.out.println("Sensor Measurements Average Value: " + averageCalbMeasurement);
										}
									} else if (numericType == D2PIOJNALibrary.D2PIO_NUMERIC_MEAS_TYPE_INT32) {
								    int[] rawMeasurements = sensor.readRawMeasurements(channel, 200);
										int numMeasurements = rawMeasurements != null ? rawMeasurements.length: 0;
										if (numMeasurements > 0) {
											int averageRawMeasurement = 0;
											for (int i = 0; i < numMeasurements; i++) {
												averageRawMeasurement += rawMeasurements[i];
											}
										  if (numMeasurements > 1) {
											  averageRawMeasurement = averageRawMeasurement / numMeasurements;
											}
											System.out.println("Sensor Raw Measurements Average Value: " + averageRawMeasurement);
										}
									}
								}
							}
						}
					}



					// int sensorType = sensor.getType();
					// System.out.println("Sensor type: " + sensorType);
					String orderCode = sensor.getOrderCode();
					System.out.println("Order code: " + orderCode);
					String serialNumber = sensor.getSerialNumber();
					System.out.println("Serial number: " + serialNumber);
					String description = sensor.getDescription();
					System.out.println("Sensor description: " + description);
					sensor.getManufactureDate();


					int closeResult = sensor.close();
					System.out.println("Close sensor result: " + closeResult);
				}
				else {
					System.out.println("Open sensor failed!");
				}
			}
            /*
			short[] version = labQuestLib.getDLLVersion();
			System.out.println("major: " + version[0] +
					" minor: " + version[1]);

			// This is necessary on windows.
			// on my tests only 50ms was necessary but I choose 100ms to be safe
			Thread.sleep(100);

			labQuestLib.searchForDevices();
			labQuestLib.printListOfDevices();

			String firstDevice = null;
			firstDevice = labQuestLib.getFirstDeviceName();

			if(firstDevice == null){
				return;
			}

            labQuest = labQuestLib.openDevice(firstDevice);
            */

			// test();

			d2p.uninit();
		} catch (Throwable t) {
			t.printStackTrace();
		}
/*
		if(labQuest != null){
			try {
				labQuest.close();
			} catch (LabQuestException e) {
				e.printStackTrace();
			}
		}

		labQuestLib.uninit("main");
*/

		System.out.println("end D2PIOJNATest.main");
	}

	/**
	 * @param args
	 * @throws IOException
	 */
/*
	public static void test() throws LabQuestException {
		LabQuestStatus status = labQuest.getStatus();
		System.out.println("labQuest status: " + status.inspect());
		boolean remoteCollectionActive = false;
		remoteCollectionActive = labQuest.isRemoteCollectionActive();
		System.out.println("remote collection active: " + remoteCollectionActive);

		// isremotecollection active appears to always return false.
		// so even if it isn't active try to acquire ownership
		labQuest.acquireExclusiveOwnership();

		labQuest.printAttachedSensors();

		int channelOneId = labQuest.getSensorId(NGIOSourceCmds.CHANNEL_ID_ANALOG1);

		String units = "";
		if(channelOneId >= 20){
			labQuest.ddsMemReadRecord(NGIOSourceCmds.CHANNEL_ID_ANALOG1, false);
			GSensorDDSMem sensorDDSMem = labQuest.ddsMemGetRecord(NGIOSourceCmds.CHANNEL_ID_ANALOG1);

			byte [] unitBuf = sensorDDSMem.CalibrationPage[sensorDDSMem.ActiveCalPage].Units;
			units = Native.toString(unitBuf);
		}

		// period in seconds
		labQuest.setMeasurementPeriod((byte)-1, 0.1);

		// send a NGIO_CMD_ID_SET_SENSOR_CHANNEL_ENABLE_MASK
		labQuest.setSensorChannelEnableMask(0x02);

		// Send a NGIO_CMD_ID_START_MEASUREMENTS
		labQuest.startMeasurements();

		// NGIO_Device_ReadRawMeasurements();
		int [] pMeasurementsBuf = new int [1000];
		for(int count=0; count<10; count++){
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			int numMeasurements = labQuest.readRawMeasurementsAnalog(
					NGIOSourceCmds.CHANNEL_ID_ANALOG1,
					pMeasurementsBuf, pMeasurementsBuf.length);
			for(int i=0; i<numMeasurements; i++){
				float calibratedData = labQuest.calibrateData2(
						NGIOSourceCmds.CHANNEL_ID_ANALOG1, pMeasurementsBuf[i]);
				System.out.println("value: " + calibratedData + " " + units);
			}


		}

		// NGIO_CMD_ID_STOP_MEASUREMENTS
		labQuest.stopMeasurements();


		// need to clear the buffer before reading more
	}

	public static void testMotion() throws LabQuestException {
		labQuest.acquireExclusiveOwnership();

		labQuest.printAttachedSensors();

		int channelId = labQuest.getSensorId(NGIOSourceCmds.CHANNEL_ID_DIGITAL1);
		if(channelId != 2){
			System.err.println("didn't find the motion sensor on the first digitial channel");
			throw new RuntimeException();
		}

		// period in seconds
		labQuest.setMeasurementPeriod((byte)-1, 1);

		// send a NGIO_CMD_ID_SET_SENSOR_CHANNEL_ENABLE_MASK
		labQuest.setSensorChannelEnableMask(1 << 5);

		labQuest.setSamplingMode(NGIOSourceCmds.CHANNEL_ID_DIGITAL1,
				NGIOSourceCmds.SAMPLING_MODE_PERIODIC_MOTION_DETECT);

		labQuest.clearIO(NGIOSourceCmds.CHANNEL_ID_DIGITAL1);

		labQuest.startMeasurements();

		// NGIO_Device_ReadRawMeasurements();
		int [] pMeasurementsBuf = new int [1000];
		long [] pTimestampsBuf = new long [1000];
		for(int count=0; count<10; count++){
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			labQuest.readRawMeasurementsMotion(
					NGIOSourceCmds.CHANNEL_ID_DIGITAL1, pMeasurementsBuf,
					pTimestampsBuf, pMeasurementsBuf.length);
		}

		// NGIO_CMD_ID_STOP_MEASUREMENTS
		labQuest.stopMeasurements();

		// need to clear the buffer before reading more
	}
*/
}

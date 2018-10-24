package org.concord.sensor.d2pio.jna;

import com.sun.jna.Memory;
import com.sun.jna.Pointer;
import com.sun.jna.ptr.ByteByReference;
import com.sun.jna.ptr.IntByReference;
import com.sun.jna.ptr.ShortByReference;
import com.sun.jna.ptr.LongByReference;
import com.sun.jna.ptr.DoubleByReference;

/**
 * In the D2PIO SDK a sensor represents an attached device
 *
 * @author kswenson
 *
 */
public class D2PIOSensor {
	protected D2PIOJNALibrary libInstance;
	protected Pointer libHandle;
	protected String deviceName;
	protected String friendlyName;
	protected Pointer deviceHandle;
	private static final int LOCK_TIMEOUT_MS = 1000;

	D2PIOSensor(D2PIOJNALibrary libInstance, Pointer libHandle,
				String deviceName, String friendlyName) {
		this.libInstance = libInstance;
		this.libHandle = libHandle;
		this.deviceName = deviceName;
		this.friendlyName = friendlyName;
	}

	protected void finalize() {
		close();
	}

	public String getName() {
		return deviceName;
	}

	public String getFriendlyName() {
		return friendlyName;
	}

	public boolean open() {
		deviceHandle = libInstance.D2PIO_Device_Open(
						libHandle, deviceName, null, 0,
						D2PIOJNALibrary.D2PIO_USB_OPEN_TIMEOUT_MS, null, null);
		if (deviceHandle == null) return false;
		unlock();

		int result;
		IntByReference pOpenStatus = new IntByReference();
		pOpenStatus.setValue(D2PIOJNALibrary.D2PIO_DEVICE_OPEN_STATUS_IN_PROGRESS);
		while (true) {
			try {
				Thread.sleep(50);
			}
			catch(InterruptedException ex) {
				System.out.println("Sleep interrupted!");
			}
			result = libInstance.D2PIO_Device_GetOpenStatus(deviceHandle, pOpenStatus);
			if ((result != 0) || (pOpenStatus.getValue() != D2PIOJNALibrary.D2PIO_DEVICE_OPEN_STATUS_IN_PROGRESS))
				break;
		}
		if ((result == 0) && (pOpenStatus.getValue() == D2PIOJNALibrary.D2PIO_DEVICE_OPEN_STATUS_SUCCESS))
			System.out.println("D2PIO_Device_GetOpenStatus - SUCCESS!");
		else
			System.out.println("D2PIO_Device_GetOpenStatus - FAILED! status: " + pOpenStatus.getValue());

		int MAX_NAME_SIZE = D2PIOJNALibrary.D2PIO_MAX_SIZE_DEVICE_NAME;
		Pointer pDeviceName = new Memory(MAX_NAME_SIZE);
		Pointer pFriendlyName = new Memory(MAX_NAME_SIZE);
		result = libInstance.D2PIO_Device_GetOpenDeviceName(
								deviceHandle,
								pDeviceName, MAX_NAME_SIZE,
								pFriendlyName, MAX_NAME_SIZE);
		if (result == 0) {
			this.friendlyName = pFriendlyName.getString(0, "UTF-8");
		}
		else {
			System.out.println("D2PIO_Device_GetOpenDeviceName ERROR: " + result);
		}
		return true;
	}

	public int close() {
		int result = 0;
		lock();
		if (deviceHandle != null) {
			result = libInstance.D2PIO_Device_Close(deviceHandle);
			deviceHandle = null;
		}
		// after the device is closed it cannot be unlocked
		return result;
	}

	/**
	 * There are lock and unlock methods around each D2PIOSensor call.
	 * These call the built in D2PIO SDK lock and unlock calls.
	 */
	private void lock() {
		int ret = libInstance.D2PIO_Device_Lock(deviceHandle, LOCK_TIMEOUT_MS);
		if(ret != 0){
			throw new RuntimeException("Unable to lock device");
		}
	}

	private void unlock() {
		int ret = libInstance.D2PIO_Device_Unlock(deviceHandle);
		if(ret != 0){
			throw new RuntimeException("Unable to unlock device");
		}
	}

	public void clearIO(int channel) {
		lock();
		int ret = libInstance.D2PIO_Device_ClearIO(deviceHandle, (byte)channel);
		unlock();

		if(ret != 0){
			throw new RuntimeException("Error clearing IO");
		}
	}

	public void startMeasurements() {
		lock();
		int ret = sendCmdAndGetResponse(D2PIOJNALibrary.D2PIO_CMD_ID_START_MEASUREMENTS);
		unlock();
		if(ret != 0){
			throw new RuntimeException("Error starting measurements");
		}
	}

	public void stopMeasurements() {
		lock();
		int ret = sendCmdAndGetResponse(D2PIOJNALibrary.D2PIO_CMD_ID_STOP_MEASUREMENTS);
		unlock();
		if(ret != 0){
			throw new RuntimeException("Error stopping measurements");
		}
	}

	public int sendCmdAndGetResponse(byte command) {
		int status = 0;
		Pointer pRespBuf = null;
		IntByReference pnRespBytes = null;
		lock();
		status = libInstance.D2PIO_Device_SendCmdAndGetResponse(deviceHandle,
				command, null,
				0, pRespBuf, pnRespBytes,
				D2PIOJNALibrary.D2PIO_TIMEOUT_MS_DEFAULT);
		unlock();
		if (status != 0) {
			System.out.println("sendCmdAndGetResponse ERROR: " + status);
		}
		return status;
	}

	public int setMeasurementPeriod(double desiredPeriod) {
		int result = 0;
		byte channelNum = -1; //all channels
		lock();
		result = libInstance.D2PIO_Device_SetMeasurementPeriod(deviceHandle,
							channelNum,
							desiredPeriod,
							D2PIOJNALibrary.D2PIO_TIMEOUT_MS_DEFAULT);
		unlock();
		if (result != 0) {
			System.out.println("setMeasurementPeriod ERROR: " + result);
			throw new RuntimeException("error setting measurement period");
		}
		return result;
	}

	public double getMeasurementPeriod() {
		int result = 0;
		byte channelNum = -1; //all channels
		DoubleByReference pPeriod = new DoubleByReference();
		double period = 0;
		lock();
		result = libInstance.D2PIO_Device_GetMeasurementPeriod(deviceHandle,
							channelNum,
							pPeriod,
							D2PIOJNALibrary.D2PIO_TIMEOUT_MS_DEFAULT);
		unlock();
		if (result == 0) {
			period = pPeriod.getValue();
		} else {
			System.out.println("getMeasurementPeriod ERROR: " + result);
			throw new RuntimeException("error getting measurement period");
		}
		return period;
	}

	public int getMeasurementChannelAvailabilityMask() {
		int result = 0;
		int uChannelMask = 0;
		ByteByReference pChannelMask = new ByteByReference();
		lock();
		result = libInstance.D2PIO_GetMeasurementChannelAvailabilityMask(deviceHandle,
							pChannelMask);
		unlock();
		if (result == 0) {
			// it is an unsigned char so deal with negative numbers
	    uChannelMask = pChannelMask.getValue() & 0x0FF;
		} else {
			System.out.println("getMeasurementChannelAvailabilityMask ERROR: " + result);
		}
		return uChannelMask;
	}

	public int getMeasurementChannelSensorId(int channel) {
		int result = 0;
		int sensorId = 0;
		IntByReference pSensorId = new IntByReference();
		lock();
		result = libInstance.D2PIO_Device_GetMeasurementChannelSensorId(deviceHandle,
							(byte)channel,
							pSensorId);
		unlock();
		if (result == 0) {
			sensorId = pSensorId.getValue();
		} else {
			System.out.println("getMeasurementChannelSensorId ERROR: " + result);
		}
		return sensorId;
	}

	public String getMeasurementChannelSensorDescription(int channel) {
		int result = 0;
		String sensorDesc = "";
		int MAX_LEN = D2PIOJNALibrary.D2PIO_MAX_NUM_BYTES_IN_SENSOR_DESCRIPTION;
		Pointer pSensorDescription = new Memory(MAX_LEN);
		lock();
		result = libInstance.D2PIO_Device_GetMeasurementChannelSensorDescription(deviceHandle,
							(byte)channel,
							pSensorDescription,
							MAX_LEN);
		unlock();
		if (result == 0) {
			sensorDesc = pSensorDescription.getString(0, "UTF-8");
		} else {
			System.out.println("getMeasurementChannelSensorDescription ERROR: " + result);
		}
		return sensorDesc;
	}

	public String getMeasurementChannelSensorUnits(int channel) {
		int result = 0;
		String sensorUnits = "";
		int MAX_LEN = D2PIOJNALibrary.D2PIO_MAX_NUM_BYTES_IN_SENSOR_UNIT;
		Pointer pSensorUnit = new Memory(MAX_LEN);
		lock();
		result = libInstance.D2PIO_Device_GetMeasurementChannelSensorUnit(deviceHandle,
							(byte)channel,
							pSensorUnit,
							MAX_LEN);
		unlock();
		if (result == 0) {
			sensorUnits = pSensorUnit.getString(0, "UTF-8");
		} else {
			System.out.println("getMeasurementChannelSensorUnits ERROR: " + result);
		}
		return sensorUnits;
	}

	public int getMeasurementChannelNumericType(int channel) {
		int result = 0;
		int numericType = D2PIOJNALibrary.D2PIO_NUMERIC_MEAS_TYPE_REAL64;
		ByteByReference pNumericMeasType = new ByteByReference();
		lock();
		result = libInstance.D2PIO_Device_GetMeasurementChannelNumericType(deviceHandle,
							(byte)channel,
							pNumericMeasType);
		unlock();
		if (result == 0) {
			numericType = pNumericMeasType.getValue();
		} else {
			System.out.println("getMeasurementChannelNumericType ERROR: " + result);
		}
		return numericType;
	}

	public boolean measurementIsRaw(int numericType) {
		return (numericType == D2PIOJNALibrary.D2PIO_NUMERIC_MEAS_TYPE_INT32);
	}

	public int[] readRawMeasurements(int channel, int maxCount) {
		int numMeasurements = 0;
		int [] pMeasurementsBuf = new int[maxCount];
		long [] pTimeStamps = new long[maxCount];
		lock();
		numMeasurements = libInstance.D2PIO_Device_ReadRawMeasurements(deviceHandle,
							(byte)channel,
							pMeasurementsBuf,
							pTimeStamps,
							maxCount);
		unlock();
		if (numMeasurements > 0) {
			int [] retBuffer = new int [numMeasurements];
			System.arraycopy(pMeasurementsBuf, 0, retBuffer, 0, numMeasurements);
			return retBuffer;
		} else {
			System.out.println("readRawMeasurements found 0 valid measurements");
			return null;
		}
	}

	public double[] readMeasurements(int channel, int maxCount) {
		int numMeasurements = 0;
		double [] pMeasurementsBuf = new double[maxCount];
		lock();
		numMeasurements = libInstance.D2PIO_Device_ReadMeasurements(deviceHandle,
							(byte)channel,
							pMeasurementsBuf,
							null,
							maxCount);
		unlock();
		if (numMeasurements > 0) {
			double [] retBuffer = new double [numMeasurements];
			System.arraycopy(pMeasurementsBuf, 0, retBuffer, 0, numMeasurements);
			return retBuffer;
		} else {
			System.out.println("readMeasurements found 0 valid measurements");
			return null;
		}
	}

	public String getDescription() {
		if (deviceHandle != null) {
			int MAX_LEN = D2PIOJNALibrary.D2PIO_MAX_SIZE_DEVICE_NAME;
			Pointer pDescription = new Memory(MAX_LEN);
			lock();
			int result = libInstance.D2PIO_Device_GetDeviceDescription(
										deviceHandle, pDescription, MAX_LEN);
			unlock();
			if (result == 0) {
				return pDescription.getString(0, "UTF-8");
			}
		}
		return "";
	}

	public String getOrderCode() {
		if (deviceHandle != null) {
			int MAX_LEN = D2PIOJNALibrary.D2PIO_MAX_SIZE_DEVICE_NAME;
			Pointer pOrderCode = new Memory(MAX_LEN);
			lock();
			int result = libInstance.D2PIO_Device_GetOrderCode(
										deviceHandle, pOrderCode, MAX_LEN);
			unlock();
			if (result == 0) {
				return pOrderCode.getString(0, "UTF-8");
			}
		}
		return "";
	}

	public String getSerialNumber() {
		if (deviceHandle != null) {
			int MAX_LEN = D2PIOJNALibrary.D2PIO_MAX_SIZE_DEVICE_NAME;
			Pointer pSerialNumber = new Memory(MAX_LEN);
			lock();
			int result = libInstance.D2PIO_Device_GetSerialNumber(
										deviceHandle, pSerialNumber, MAX_LEN);
			unlock();
			if (result == 0) {
				return pSerialNumber.getString(0, "UTF-8");
			}
		}
		return "";
	}

	public void getManufactureDate() {
		if (deviceHandle != null) {
			ShortByReference pManufacturerId = new ShortByReference();
			ShortByReference pManufacturedYear = new ShortByReference();
			ByteByReference pManufacturedMonth = new ByteByReference();
			ByteByReference pManufacturedDay = new ByteByReference();
			lock();
			int result = libInstance.D2PIO_Device_GetManufacturingInfo(
										deviceHandle,
										pManufacturerId,
										pManufacturedYear,
										pManufacturedMonth,
										pManufacturedDay);
			unlock();
			if (result == 0) {
				System.out.println("Manufacturer ID: " + pManufacturerId.getValue());
				System.out.println("Manufacture year: " + pManufacturedYear.getValue());
				System.out.println("Manufacture month: " + pManufacturedMonth.getValue());
				System.out.println("Manufacture day: " + pManufacturedDay.getValue());
			}
		}
	}

  public String getDeviceLabel(){
		return "Go Direct";
	}

	public int getAttachedSensorId(int channel) {
		int sensorId = this.getMeasurementChannelSensorId(channel);
		return sensorId;
	}
}

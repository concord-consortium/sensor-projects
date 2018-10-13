package org.concord.sensor.d2pio.jna;

import com.sun.jna.Memory;
import com.sun.jna.Pointer;
import com.sun.jna.ptr.ByteByReference;
import com.sun.jna.ptr.IntByReference;
import com.sun.jna.ptr.ShortByReference;

/**
 * In the GoIO SDK a sensor represents an attached device
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
		// byte[] devName = null;
		// try {
		// 	devName = (deviceName + '\0').getBytes("UTF-8");
		// }
		// catch(Exception ex) {
		// }
		// System.out.println("deviceNameStringLength: " + deviceName.length());
		// System.out.println("devNameBytesLength: " + devName.length);
		deviceHandle = libInstance.D2PIO_Device_Open(
						libHandle, deviceName, null, 0,
						D2PIOJNALibrary.D2PIO_USB_OPEN_TIMEOUT_MS, null, null);
		if (deviceHandle == null) return false;

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
			System.out.println("D2PIO_Device_GetOpenDeviceName deviceName: " + pDeviceName.getString(0, "UTF-8"));
			System.out.println("D2PIO_Device_GetOpenDeviceName friendlyName: " + friendlyName);
		}
		else {
			System.out.println("D2PIO_Device_GetOpenDeviceName ERROR: " + result);
		}
		return true;
	}

	public int close() {
		int result = 0;
		if (deviceHandle != null) {
			result = libInstance.D2PIO_Device_Close(deviceHandle);
			deviceHandle = null;
		}
		return result;
	}

/*
	public int getType() {
		int type = 0;
		if (deviceHandle != null) {
			System.out.println("getType calling D2PIO_Device_GetOpenDeviceType()");
			type = libInstance.D2PIO_Device_GetOpenDeviceType(deviceHandle);
			System.out.println("getType returned " + type);
		}
		return type;
	}
*/
	public String getDescription() {
		if (deviceHandle != null) {
			System.out.println("getDescription calling D2PIO_Device_GetDeviceDescription()");
			int MAX_LEN = D2PIOJNALibrary.D2PIO_MAX_SIZE_DEVICE_NAME;
			Pointer pDescription = new Memory(MAX_LEN);
			int result = libInstance.D2PIO_Device_GetDeviceDescription(
										deviceHandle, pDescription, MAX_LEN);
			System.out.println("getDescription result: " + result);
			if (result == 0) {
				return pDescription.getString(0, "UTF-8");
			}
		}
		return "";
	}

	// D2PIO_LIB_INTERFACE_DECL gtype_int32 D2PIO_Device_GetOrderCode(
	// 	D2PIO_DEVICE_HANDLE hDevice,	//[in] handle to open device.
	// 	char *pOrderCode,				//[out] ptr to buffer to store NULL terminated UTF-8 string for the OrderCode.
	// 	gtype_uint32 Bufsize);			//[in] number of UTF-8 chars in buffer pointed to by pOrderCode. strlen(pOrderCode) < bufSize, because the string is NULL terminated.
	// 									//strlen(pOrderCode) is guaranteed to be < D2PIO_MAX_ORDERCODE_LENGTH.
	
	public String getOrderCode() {
		if (deviceHandle != null) {
			System.out.println("getOrderCode calling D2PIO_Device_GetOrderCode()");
			int MAX_LEN = D2PIOJNALibrary.D2PIO_MAX_SIZE_DEVICE_NAME;
			Pointer pOrderCode = new Memory(MAX_LEN);
			int result = libInstance.D2PIO_Device_GetOrderCode(
										deviceHandle, pOrderCode, MAX_LEN);
			System.out.println("getOrderCode result: " + result);
			if (result == 0) {
				return pOrderCode.getString(0, "UTF-8");
			}
		}
		return "";
	}

	public String getSerialNumber() {
		if (deviceHandle != null) {
			System.out.println("getSerialNumber calling D2PIO_Device_GetSerialNumber()");
			int MAX_LEN = D2PIOJNALibrary.D2PIO_MAX_SIZE_DEVICE_NAME;
			Pointer pSerialNumber = new Memory(MAX_LEN);
			int result = libInstance.D2PIO_Device_GetSerialNumber(
										deviceHandle, pSerialNumber, MAX_LEN);
			System.out.println("D2PIO_Device_GetSerialNumber result: " + result);
			if (result == 0) {
				return pSerialNumber.getString(0, "UTF-8");
			}
		}
		return "";
	}

	public void getManufactureDate() {
		if (deviceHandle != null) {
			System.out.println("getManufactureDate calling D2PIO_Device_GetManufacturingInfo()");
			ShortByReference pManufacturerId = new ShortByReference();
			ShortByReference pManufacturedYear = new ShortByReference();
			ByteByReference pManufacturedMonth = new ByteByReference();
			ByteByReference pManufacturedDay = new ByteByReference();
			int result = libInstance.D2PIO_Device_GetManufacturingInfo(
										deviceHandle,
										pManufacturerId,
										pManufacturedYear,
										pManufacturedMonth,
										pManufacturedDay);
			System.out.println("D2PIO_Device_GetSerialNumber result: " + result);
			if (result == 0) {
				System.out.println("Manufacturer ID: " + pManufacturerId.getValue());
				System.out.println("Manufacture year: " + pManufacturedYear.getValue());
				System.out.println("Manufacture month: " + pManufacturedMonth.getValue());
				System.out.println("Manufacture day: " + pManufacturedDay.getValue());
			}
		}
	}

	/*
	private static final int LOCK_TIMEOUT_MS = 1000;
	GoIOJNALibrary goIOLibrary;
	char []deviceName = new char[GoIOJNALibrary.GOIO_MAX_SIZE_DEVICE_NAME];
	int productId;
	Pointer hDevice;
	@SuppressWarnings("unused")
	private int openedSensorId;
	
	public GoIOSensor(GoIOJNALibrary goIOLibrary){
		this.goIOLibrary = goIOLibrary;
	}
	
	// this assumes someone has updated the list of devices with
	// GoIO_UpdateListOfAvailableDevices
	public void init(int productId, int index){
		// make sure our list is up to date
//		goIOLibrary.GoIO_UpdateListOfAvailableDevices(
//				GoIOJNALibrary.VERNIER_DEFAULT_VENDOR_ID,
//				productId
//				);
		
		this.productId = productId;
		if(goIOLibrary.GoIO_GetNthAvailableDeviceName(deviceName, GoIOJNALibrary.GOIO_MAX_SIZE_DEVICE_NAME, 
				GoIOJNALibrary.VERNIER_DEFAULT_VENDOR_ID, productId, index) != 0) {
				// error getting device name
				// TODO replace with more standard exception
				throw new RuntimeException("error getting device name");
		}		
	}
	
	public void open() {
		hDevice = goIOLibrary.GoIO_Sensor_Open(deviceName, GoIOJNALibrary.VERNIER_DEFAULT_VENDOR_ID,
				productId, 0);
		if(hDevice == null) {
			throw new RuntimeException("error opening device");
		}
	
		unlock();
		
		// This is stored here because the open call also reads the DDS which is specific to the attached
		// sensor, however with a go link the user can unplug this sensor and plug in another
		// in which case the sensor should be closed and opened again.
		openedSensorId = getAttachedSensorId();  
	}
	
	public void close() {
		lock();
		goIOLibrary.GoIO_Sensor_Close(hDevice);
		// after the device is closed it cannot be unlocked
	}

	/**
	 * There are lock and unlock methods around each GoIOSensor call.  
	 * These call the built in GoIO SDK lock and unlock calls.  This wasn't done with java synchronize keyword, 
	 * because that requires leaving the device unlocked all the time.  
	 * And the GoIO SDK asserts sometimes when calls are made and it is unlocked.
	 * 
	 * When the library asserts on a mac it makes a system beep, this doesn't stop things from running but it can
	 * be confusing and annoying.
	 * 
	private void lock() {
		int ret = goIOLibrary.GoIO_Sensor_Lock(hDevice, LOCK_TIMEOUT_MS);
		if(ret != 0){
			throw new RuntimeException("unable to lock device");
		}
	}
	
	private void unlock() {
		int ret = goIOLibrary.GoIO_Sensor_Unlock(hDevice);		
		if(ret != 0){
			throw new RuntimeException("unable to unlock device");
		}
	}
	
	public void clearIO() {
		lock();
		int ret = goIOLibrary.GoIO_Sensor_ClearIO(hDevice);
		unlock();
		
		if(ret != 0){
			throw new RuntimeException("error clearing IO");
		}
	}

	public void startMeasurements() {
		lock();
		int ret = goIOLibrary.GoIO_Sensor_SendCmdAndGetResponse(hDevice, 
				GoIOJNALibrary.SKIP_CMD_ID_START_MEASUREMENTS, null,
		0, null, null,
		GoIOJNALibrary.SKIP_TIMEOUT_MS_DEFAULT);
		unlock();
		
		if(ret != 0){
			throw new RuntimeException("error starting measurments");
		}
	}
	
	public void stopMeasurements() {
		lock();
		int ret = goIOLibrary.GoIO_Sensor_SendCmdAndGetResponse(hDevice, 
				GoIOJNALibrary.SKIP_CMD_ID_STOP_MEASUREMENTS, null,
				0, null, null,
				GoIOJNALibrary.SKIP_TIMEOUT_MS_DEFAULT);
		unlock();
		
		if(ret != 0){
			throw new RuntimeException("error stopping measurments");
		}
	}
	
	public int readRawMeasurements(int [] rawBuffer) {
		// to be safe make sure it is multiple of 6
		int safeCount = (rawBuffer.length / 6) * 6;
		
		lock();
		int ret = goIOLibrary.GoIO_Sensor_ReadRawMeasurements(hDevice, rawBuffer, safeCount);
		unlock();
		return ret;		
	}
	
	public double convertToVoltage(int rawData) {
		return goIOLibrary.GoIO_Sensor_ConvertToVoltage(hDevice, rawData);
	}
	
	public double calibrateData(double voltage) {
		return goIOLibrary.GoIO_Sensor_CalibrateData(hDevice, voltage);
	}
	
	public void setMeasurementPeriod(double desiredPeriod){
		lock();
		int ret = goIOLibrary.GoIO_Sensor_SetMeasurementPeriod(hDevice, desiredPeriod, GoIOJNALibrary.SKIP_TIMEOUT_MS_DEFAULT);
		unlock();
		
		if(ret != 0) {
			throw new RuntimeException("error setting measurement period");
		}		
	}

	public int getAttachedSensorId() {
		byte [] sensorId = new byte[1];
		
		lock();
		int ret = goIOLibrary.GoIO_Sensor_DDSMem_GetSensorNumber(hDevice, sensorId, 1, GoIOJNALibrary.SKIP_TIMEOUT_MS_DEFAULT);
	    unlock();

	    if(ret != 0) {
	    	throw new RuntimeException("error getting sensor id");
	    }
	    
	    
	    // it is an unsigned char so deal with negative numbers
	    return sensorId[0] & 0x0FF;
	}
	
	public double getMeasurementPeriod() {
		
		lock();
		double ret = goIOLibrary.GoIO_Sensor_GetMeasurementPeriod(hDevice,  GoIOJNALibrary.SKIP_TIMEOUT_MS_DEFAULT);
		unlock();
		
		if(ret == 1000000.0) {
			throw new RuntimeException("error getting measurement period");
		}
		return ret;
	}
	
	public byte getDDSCheckSum() {
		lock();
		byte[] pChecksum = new byte[1];
		int ret = goIOLibrary.GoIO_Sensor_DDSMem_GetChecksum(hDevice, pChecksum );
		unlock();
		
		if(ret != 0){
			throw new RuntimeException("Can't get DDS checksum");
		}
		
		return pChecksum[0];
	}

	public final static int ANALOG_CHANNEL_5V = 0;
	public final static int ANALOG_CHANNEL_10V = 1;
	
	public void setAnalogInputChannel(int channel){
		byte channelConst = GoIOJNALibrary.SKIP_ANALOG_INPUT_CHANNEL_VIN_LOW;
		
		if(channel == ANALOG_CHANNEL_5V){
			channelConst = GoIOJNALibrary.SKIP_ANALOG_INPUT_CHANNEL_VIN_LOW;
		} else if(channel == ANALOG_CHANNEL_10V){
			channelConst = GoIOJNALibrary.SKIP_ANALOG_INPUT_CHANNEL_VIN;
		}
		
		lock();
		goIOLibrary.GoIO_Sensor_SendCmdAndGetResponse(
				hDevice, GoIOJNALibrary.SKIP_CMD_ID_SET_ANALOG_INPUT_CHANNEL, 
				new byte []{channelConst}, 1, null, null, 
				GoIOJNALibrary.SKIP_TIMEOUT_MS_DEFAULT);
		unlock();
	}

	
	public String getDeviceLabel(){
		switch(productId){
		case GoIOJNALibrary.SKIP_DEFAULT_PRODUCT_ID: 
			return "GoLink";
		case GoIOJNALibrary.USB_DIRECT_TEMP_DEFAULT_PRODUCT_ID:
			return "GoTemp";
		case GoIOJNALibrary.CYCLOPS_DEFAULT_PRODUCT_ID:
			return "GoMotion";
		case GoIOJNALibrary.MINI_GC_DEFAULT_PRODUCT_ID:
			return "MiniGasChromatograph";
		default:
			return "Unknown GoIO Device";
		}
	}

	public boolean isGoMotion(){
		return productId == GoIOJNALibrary.CYCLOPS_DEFAULT_PRODUCT_ID;
	}	
	public boolean isGoTemp(){
		return productId == GoIOJNALibrary.USB_DIRECT_TEMP_DEFAULT_PRODUCT_ID;
	}
	public boolean isGoLink(){
		return productId == GoIOJNALibrary.SKIP_DEFAULT_PRODUCT_ID;
	}
	public boolean isMiniGasChromatograph(){
		return productId == GoIOJNALibrary.MINI_GC_DEFAULT_PRODUCT_ID;
	}
*/

}
/*
class HandleOpenStatusNotification
		implements D2PIOJNALibrary.P_D2PIO_DEVICENOTIFICATION_CALLBACK {
	public int apply(Pointer pNotification, int numBytes, Pointer hDevice, Pointer pContextInfo) {
		System.out.println("HandleOpenStatusNotification received!");
		int type = pNotification.getByte(0);
		int status = pNotification.getByte(1);
		// D2PIOJNALibrary.tagD2PIODeviceNotificationOpenStatus
		// 	notification = new D2PIOJNALibrary.tagD2PIODeviceNotificationOpenStatus(pNotification);
		System.out.println("notificationSize: " + numBytes);
		System.out.println("notificationType: " + type);
		System.out.println("openStatus: " + status);
		return 0;
	}
}
*/
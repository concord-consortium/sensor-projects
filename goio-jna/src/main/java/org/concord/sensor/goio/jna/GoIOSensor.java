package org.concord.sensor.goio.jna;

import com.sun.jna.Pointer;

/**
 * In the GoIO SDK a sensor represents an attached device this could be a 
 * GoLink, GoMotion, or GoTemp
 * 
 * @author scytacki
 *
 */
public class GoIOSensor {
	private static final int LOCK_TIMEOUT_MS = 1000;
	GoIOJNALibrary goIOLibrary;
	char []deviceName = new char[GoIOJNALibrary.GOIO_MAX_SIZE_DEVICE_NAME];
	int productId;
	Pointer hDevice;
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
	 */
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


}

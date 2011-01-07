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
		
		// This is stored here because the open call also reads the DDS which is specific to the attached
		// sensor, however with a go link the user can unplug this sensor and plug in another
		// in which case the sensor should be closed and opened again.
		openedSensorId = getAttachedSensorId();  
	}
	
	public int getAttachedSensorId() {
		byte [] sensorId = new byte[1];
		int ret = goIOLibrary.GoIO_Sensor_DDSMem_GetSensorNumber(hDevice, sensorId, 1, GoIOJNALibrary.SKIP_TIMEOUT_MS_DEFAULT);
	    if(ret != 0) {
	    	throw new RuntimeException("error getting sensor id");
	    }
	    
	    // it is an unsigned char so deal with negative numbers
	    return sensorId[0] & 0x0FF;
	}
	
	public double getMeasurementPeriod() {
		double ret = goIOLibrary.GoIO_Sensor_GetMeasurementPeriod(hDevice,  GoIOJNALibrary.SKIP_TIMEOUT_MS_DEFAULT);
		if(ret == 1000000.0) {
			throw new RuntimeException("error getting measurement period");
		}
		return ret;
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

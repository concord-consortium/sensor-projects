package org.concord.sensor.pasco.jna;

import com.sun.jna.Library;

public interface PascoJNALibrary extends Library {
	/**
	Allocate and initialize:
	Returns a handle, use as first arg for all functions.
	*/
    int PasInit();

	/**
	Start it:
	*/
    int PasStart(int handle);

	/**
	Stop it:
	*/
	int PasStop(int handle);

	/**
	Free it:
	*/
	int PasDelete(int handle);

	/**
	Get all open usb devices
	Returns # devices, 0 - N, -1 error
	*/
	int PasGetDevices(int handle, int[] arr, int arrsiz);

//	#define PASCO_USB_LINK_NEEDS_LOAD_PRODUCT_CODE 	0x0001		// USBLink that needs firmware.
//	#define PASCO_USB_LINK_LOADED_PRODUCT_CODE 		0x0002		// USBLink loaded with firmware.
//	#define PASCO_USB_XPLORER_PRODUCT_CODE 			0x0003		// Xplorer loaded with firmware.
//	#define PASCO_USB_POWERLINK_PRODUCT_CODE 		0x0005		// PowerLink.
//	#define PASCO_USB_XPLORER_GLX_PRODUCT_CODE 		0x0006		// XplorerGLX
//	#define PASCO_USB_AIRLINK_PRODUCT_CODE 			0x0007		// AirLink (reported as a USB device)
//	#define PASCO_USB_SPARK_PRODUCT_CODE 			0x0008		// Spark
//	#define PASCO_USB_SPARKLINK_PRODUCT_CODE 		0x0009		// SparkLink
//	#define PASCO_USB_DFG_PRODUCT_CODE 				0x000a		// Digital Function Generator
//	#define PASCO_USB_AIRLINK2_PRODUCT_CODE			0x000b		// AirLink2
//	#define PASCO_USB_MAX_PASPORT_PRODUCT_CODE		0x00ff		// For easy PASPORT/nonPASPORT test
//	#define PASCO_USB_TO_SERIAL_PRODUCT_CODE		0x0100		// PASCO USB to serial converter
//	#define PASCO_USB_SW750_PRODUCT_CODE 			0x0101		// USB SW750 loaded with firmware.
//	#define PASCO_USB_SLS_SIM_PRODUCT_CODE 			0x0200		// Simulated USB device for SLS hardware.
		
		/**
		 Get the usb product id of the device
		 Returns -1 if the device no longer attached
		 */
	int PasGetDeviceProductID(int handle, int device);

	/**
	Get the number of channels for a device (round ports)
	Returns # channels, 0-N, -1 device not found, -2 other (worse) 
	*/
	int PasGetNumChannels(int handle, int device);

	/**
	Does a channel exist? (sensor plugged in)
	Returns # 1 yes, 0 no, -1 error 
	*/
	int PasGetExistChannel(int handle, int device, int channel);


	/**
	 Is a sensor plugged into the channel.  This is different from the above method which 
	 will return true if a datasheet can be read from the channel. 
	 
	 Returns # 1 yes, 0 no, -1 error 
	 */
    int PasGetSensorDetected(int handle, int device, int channel);
	
	/**
	Get sample size for a channel (sensor) 
	Returns sample size in bytes,0-N.
	-1 if no sensor (channel) attached (to a round port on the HW).
	-2 if error
	*/
	int PasGetSampleSize(int handle, int device, int channel);


	/**
	Family of three functions: 
		Min, Default and Max data rates a sensor can support.

	Get sample rates for a channel (sensor) 
	Returns rate 0-N samples per second. 
	MSB Flag: 0 = Hz; 1 = Sec

	-1 if no sensor (channel) attached (to a round port on the HW).
	-2 if error
	*/
	int PasGetSampleRateMinimum(int handle, int device, int channel);

	int PasGetSampleRateMaximum(int handle, int device, int channel);

	int PasGetSampleRateDefault(int handle, int device, int channel);


	/**
	Read the datasheet and return the number of measurements defined in the datasheet.
	This is a helper method to get you started working with datasheets.
	Currently if you want to parse the incoming data you will need to read the full datasheet and 
	parse it to get the details of each measurement

	-1 if no sensor (channel) attached (to a round port on the HW).
	-2 if error
	*/
	int PasGetNumMeasurements(int handle, int device, int channel);

	/**
	 Read the datasheet and return the name of the specified measurement.
	 This is a helper method to get you started working with datasheets.
	 Currently if you want to parse the incoming data you will need to read the full datasheet and 
	 parse it to get the details of each measurement

	 Returns:
	 0-N string length
	 -1 no sensor (channel) attached (to a round port on the HW).
	 -2 no such measurement
	 -3 buffer too short
	 -4 other error
	 */
	int PasGetMeasurementName(int handle, int device, int channel, int measurement, byte [] buf, int bufsiz);

	/**
	 Read the datasheet and return whether sensor supports probe validity detection
	 This is a helper method to get you started working with datasheets.
	 Currently if you want to parse the incoming data you will need to read the full datasheet and 
	 parse it to get the details of each measurement
	 
	  1-4  number of bytes validity bits
	  0  not supported
	 -1 if no sensor (channel) attached (to a round port on the HW).
	 -2 if error
	 */
     int PasGetSupportsProbeValidityDetection(int handle, int device, int channel);
	
	/**
	 If the sensor supports checking measurement validity this fills in the passed in 
	 result buffer with the validity bytes.  The result buffer needs to be at least 4 bytes big.
	 
	 Some sensors do not support this command.  Check the datasheet or use the command above.
	 If the sensor doesn't support it then 0 will be returned.
	 
	 Input:
	 count: number of bytes to read 1-4, should be based on PasGetSupportsProbeValidityDetection, or datasheet directly
	 
	 Output:
	 result   --4 byte buffer to fill with validity bytes
	 
	 Returns:
	  0 success
	 -1 usb error reading bytes, could be sensor, or device is not attached
	 -2 device is no longer attached
	 */
    int PasCheckMeasurementValidity(int handle, int device, int channel, int count, byte[] result);
	
	/**
	Get the name of a channel (sensor)
	Returns string length, 0-N 
	-1 if no sensor (channel) attached (to a round port on the HW).
	-2 if buffer to short

	Input:
	buf    --buffer to fill in with name
	bufsiz --size of buffer

	Output:
	buf	  --filled with a string

	*/	
	int PasGetName(int handle, int device, int channel, byte[] buf, int bufsiz);



	/**
	Get a single sample.
	Returns sample length, 0-N 
	-1 if no sensor (channel) attached (to a round port on the HW).
	-2 if buffer to short

	Input:
	buf    --buffer to fill in with name
	bufsiz --size of buffer

	Output:
	buf	  --filled with a sample

	*/
	int PasGetOneSample(int handle, int device, int channel, byte[]buffer, int bufsiz);


	/*
	********************************************************************
	Continuous Sampling
	1) Initialize & start:          PasStartContinuousSampling()
	2) Call N times to get samples:    PasGetSampleData()
	3) Stop:                        PasStopContinuousSampling()
	
	NOTE: Do not call any other API functions between Start() and Stop() 
	********************************************************************
	*/

	/**
	Start Continuous Sampling

	Returns sample length, 0-N 
	-1 if no sensor (channel) attached (to a round port on the HW).
	-2 if no USB device

	Input:
	period -- sampling period in msec. NOTE: Each sensor has its own operating range.

	*/
	int PasStartContinuousSampling(int handle, int device, int channel,int period);


	/**
	Get samples.
	Returns  --number of samples 0-N
	-1 if no sensor attached

	Input:
	buf      --buffer to fill in with data
	count    --max samples to get
	sampsize --sample size
	
	Output:
	buf	     --filled with samples

	*/
	int PasGetSampleData (int handle, int device, int channel, int sampsize, byte[] buf, int count);


	/**
	Stop Continuous Sampling

	*/
	int PasStopContinuousSampling (int handle, int device, int channel);


	int PasGetSensorDataSheetSize(int handle, int device, int channel);
	
	int PasReadSensorDataSheet(int handle, int device, int channel, byte[] dataSheetBuffer, int dataSheetSize);
	
	/******************
	Utility functions:
	*******************
	*/

	/**
	sleep milliseconds 
	*/
	void PasMSsleep(int sleep); 
}

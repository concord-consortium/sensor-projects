#pragma once
#ifdef __cplusplus
extern "C" {
#endif

	/**
	-------------------------------------------------------------------------------------------------------------------------
	The purpose of this 'C' API to allow of Pasco HW.
	
	(If you are using C++ it is recommended that you use the Pasco C++ API directly.)

	>>This API is specifically designed to be used by mixed language programs.<<
	IE Pasco Native code with java, Python, etc.  //It can obviously be used with 'C' as well.

	-------------------------------------------------------------------------------------------------------------------------
	The API uses only fundamental data types: 

	>>char and int<< 
	* This is so it is easy to use by other languages.
	* ints are 32 bits (signed 31 bits). //This should be the case for any compiler, 32 bit, 64 bit targets, etc. 
	* char is 8 bits                     //Used as a char in some places, a byte in others. 

	The API is thread safe.

	-------------------------------------------------------------------------------------------------------------------------
	HOW  to use the API
	Start.
	 GetDevices()
	 while(true)
	  {
	    Do stuff....
	    GetDevices()
	  }
	Stop.

	GetDevices() is a polling function, it should be called every now and then to see what is attached.
	All functions return <0 should a device have been unplugged.

	-------------------------------------------------------------------------------------------------------------------------
	All functions return <0 on error:

	Function arguments:
	int handle     -- Created by PasInit(), passed as FIRST arg to all functions
	int device     -- Usb device, created internally by library, found by GetDevices(), passed as SECOND arg to most functions
	int channel    -- channel (often sensor attached to a round port), passed as THIRD arg to most functions

	-------------------------------------------------------------------------------------------------------------------------
	
	*/

	/**
	Build options (#defines) for:
	* Windows dll
	* Windows static library
	* Any OS
	*/


#ifdef _WINDOWS

#ifndef NO_PASCO_API_DLL /* Define in MSVC projects that do not Use or Create dll */
	/** 
	Windows dll 
	*/
#ifdef PASCO_API_EXPORTS
	/* Build dll: */
#define PASCO_API __declspec(dllexport)
#else
	/* Use dll: */
#define PASCO_API __declspec(dllimport)
#endif 

#endif /* NO_PASCO_API_DLL */

#endif /* _WINDOWS */



#ifndef PASCO_API
	/**
	1) Any OS
	2) Windows static library or static link App
	*/
#define PASCO_API 
#endif




	/**
	Allocate and initialize:
	Returns a handle, use as first arg for all functions.
	*/
PASCO_API	int PasInit();

	/**
	Start it:
	*/
PASCO_API	int PasStart(int handle);

	/**
	Stop it:
	*/
PASCO_API	int PasStop(int handle);

	/**
	Free it:
	*/
PASCO_API	int PasDelete(int handle);

	/**
	Get all open usb devices
	Returns # devices, 0 - N, -1 error
	*/
PASCO_API	int PasGetDevices(int handle, int* arr, int arrsiz);


#define PASCO_USB_LINK_NEEDS_LOAD_PRODUCT_CODE 	0x0001		// USBLink that needs firmware.
#define PASCO_USB_LINK_LOADED_PRODUCT_CODE 		0x0002		// USBLink loaded with firmware.
#define PASCO_USB_XPLORER_PRODUCT_CODE 			0x0003		// Xplorer loaded with firmware.
#define PASCO_USB_POWERLINK_PRODUCT_CODE 		0x0005		// PowerLink.
#define PASCO_USB_XPLORER_GLX_PRODUCT_CODE 		0x0006		// XplorerGLX
#define PASCO_USB_AIRLINK_PRODUCT_CODE 			0x0007		// AirLink (reported as a USB device)
#define PASCO_USB_SPARK_PRODUCT_CODE 			0x0008		// Spark
#define PASCO_USB_SPARKLINK_PRODUCT_CODE 		0x0009		// SparkLink
#define PASCO_USB_DFG_PRODUCT_CODE 				0x000a		// Digital Function Generator
#define PASCO_USB_AIRLINK2_PRODUCT_CODE			0x000b		// AirLink2
#define PASCO_USB_MAX_PASPORT_PRODUCT_CODE		0x00ff		// For easy PASPORT/nonPASPORT test
#define PASCO_USB_TO_SERIAL_PRODUCT_CODE		0x0100		// PASCO USB to serial converter
#define PASCO_USB_SW750_PRODUCT_CODE 			0x0101		// USB SW750 loaded with firmware.
#define PASCO_USB_SLS_SIM_PRODUCT_CODE 			0x0200		// Simulated USB device for SLS hardware.
	
	/**
	 Get the usb product id of the device
	 Returns -1 if the device no longer attached
	 */
PASCO_API   int PasGetDeviceProductID(int handle, int device);

	/**
	Get the number of channels for a device (round ports)
	Returns # channels, 0-N, -1 device not found, -2 other (worse) 
	*/
PASCO_API	int PasGetNumChannels(int handle, int device);

	/**
	Does a channel exist? (sensor plugged in)
	Returns # 1 yes, 0 no, -1 error 
	*/
PASCO_API	int PasGetExistChannel(int handle, int device, int channel);

	/**
	 Is a sensor plugged into the channel.  This is different from the above method which 
	 will return true if a datasheet can be read from the channel. 
	 
	 Returns # 1 yes, 0 no, -1 error 
	 */
PASCO_API	int PasGetSensorDetected(int handle, int device, int channel);
	
	/**
	Get sample size for a channel (sensor) 
	Returns sample size in bytes,0-N.
	-1 if no sensor (channel) attached (to a round port on the HW).
	-2 if error
	*/
PASCO_API	int PasGetSampleSize(int handle, int device, int channel);


	/**
	Family of three functions: 
		Min, Default and Max data rates a sensor can support.

	Get sample rates for a channel (sensor) 
	Returns rate 0-N samples per second. 
	MSB Flag: 0 = Hz; 1 = Sec

	-1 if no sensor (channel) attached (to a round port on the HW).
	-2 if error
	*/
PASCO_API	int PasGetSampleRateMinimum(int handle, int device, int channel);

PASCO_API	int PasGetSampleRateMaximum(int handle, int device, int channel);

PASCO_API	int PasGetSampleRateDefault(int handle, int device, int channel);


	/**
	Read the datasheet and return the number of measurements defined in the datasheet.
	This is a helper method to get you started working with datasheets.
	Currently if you want to parse the incoming data you will need to read the full datasheet and 
	parse it to get the details of each measurement

	-1 if no sensor (channel) attached (to a round port on the HW).
	-2 if error
	*/
PASCO_API	int PasGetNumMeasurements(int handle, int device, int channel);

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
PASCO_API	int PasGetMeasurementName(int handle, int device, int channel, int measurement, char* buf, int bufsiz);

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
PASCO_API	int PasGetSupportsProbeValidityDetection(int handle, int device, int channel);
	
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
PASCO_API	int PasCheckMeasurementValidity(int handle, int device, int channel, int count, char *result);
	
	
	
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
PASCO_API	int PasGetName(int handle, int device, int channel, char* buf, int bufsiz);



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
PASCO_API	int PasGetOneSample(int handle, int device, int channel, char *buf, int bufsiz);


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
PASCO_API	int PasStartContinuousSampling(int handle, int device, int channel,int period);


	/**
	Get samples.
	Returns  --number of bytes read
	-1 if no sensor attached

	Input:
	buf      --buffer to fill in with data
	count    --max samples to get
	sampsize --sample size
	
	Output:
	buf	     --filled with samples

	*/
PASCO_API	int PasGetSampleData (int handle, int device, int channel, int sampsize, char* buf, int count);


	/**
	Stop Continuous Sampling

	*/
PASCO_API	int PasStopContinuousSampling (int handle, int device, int channel);


PASCO_API	int PasGetSensorDataSheetSize(int handle, int device, int channel);
	
PASCO_API	int PasReadSensorDataSheet(int handle, int device, int channel, char* dataSheetBuffer, int dataSheetSize);
	
	/******************
	Utility functions:
	*******************
	*/

	/**
	sleep milliseconds 
	*/
PASCO_API	void PasMSsleep(int sleep); 


#ifdef __cplusplus
} /* closing brace for extern "C" */
#endif

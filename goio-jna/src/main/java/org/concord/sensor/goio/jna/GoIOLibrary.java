package org.concord.sensor.goio.jna;

import com.sun.jna.Library;
import com.sun.jna.Pointer;
import com.sun.jna.Structure;
import com.sun.jna.ptr.ByteByReference;
import com.sun.jna.ptr.DoubleByReference;
import com.sun.jna.ptr.IntByReference;
import com.sun.jna.ptr.ShortByReference;






public interface GoIOLibrary extends Library {
	
	
	//Some stuff from GVernierUSB.h
	//Constants used by the USB protocol to identify our devices:
	public final static int VERNIER_DEFAULT_VENDOR_ID  = 0x08F7;

	public final static int LABPRO_DEFAULT_PRODUCT_ID = 0x0001;
	public final static int USB_DIRECT_TEMP_DEFAULT_PRODUCT_ID = 0x0002;	//aka GoTemp
	public final static int SKIP_DEFAULT_PRODUCT_ID = 0x0003;				//aka GoLink
	public final static int CYCLOPS_DEFAULT_PRODUCT_ID = 0x0004;			//aka GoMotion
	public final static int NGI_DEFAULT_PRODUCT_ID = 0x0005;				//aka LabQuest
	public final static int LOWCOST_SPEC_DEFAULT_PRODUCT_ID = 0x0006;		//aka CK Spectrometer
	public final static int MINI_GC_DEFAULT_PRODUCT_ID = 0x0007;			//aka Vernier Mini Gas Chromatograph
	public final static int STANDALONE_DAQ_DEFAULT_PRODUCT_ID = 0x0008;
			
	//Some of ^^ in human readable form:
	public final static int PROBE_USB_TEMPERATURE = USB_DIRECT_TEMP_DEFAULT_PRODUCT_ID; 
	public final static int PROBE_GOLINK = SKIP_DEFAULT_PRODUCT_ID; 
	public final static int PROBE_GOMOTION = CYCLOPS_DEFAULT_PRODUCT_ID;
	public final static int PROBE_LABQUEST = NGI_DEFAULT_PRODUCT_ID; 
	public final static int PROBE_CK_SPECTROMETER = LOWCOST_SPEC_DEFAULT_PRODUCT_ID; 
	public final static int PROBE_MINI_GAS_CHROMATOGRAPH = MINI_GC_DEFAULT_PRODUCT_ID ;			
	
	
	//Some from GoIO_DLL_interface.h
	public final static int STRUCTURE_ALIGNMENT = Structure.ALIGN_NONE; 
	public final static int GOIO_MAX_SIZE_DEVICE_NAME = 255; //FIX: 260 Non Mac OS's

	//Functions in GoIO_DLL_interface.h	
	int GoIO_Init();
	
	int GoIO_Uninit();	
	
	
	/***************************************************************************************************************************
	Function Name: GoIO_UpdateListOfAvailableDevices()
	
	Purpose:	This routine queries the operating system to build a list of available devices
				that have the specified USB vendor id and product id. Only Go! Link, Go! Temp, Go! Motion, and Vernier Mini GC
				vendor and product id's are supported.

	Return:		number of devices found.

	 ****************************************************************************************************************************/
	int GoIO_UpdateListOfAvailableDevices(
											int vendorId, //[in]
											int productId //[in]
											);	


	/***************************************************************************************************************************
	Function Name: GoIO_GetNthAvailableDeviceName()
	
	Purpose:	Get a unique device name string for the n'th device in the list of known devices with a specified
				USB vendor id and product id. This routine will only succeed if GoIO_UpdateListOfAvailableDevices()
				has been called previously.

	Return:		0 iff successful, else -1.

	 ****************************************************************************************************************************/
	int GoIO_GetNthAvailableDeviceName(
										char []pBuf,	//[out] ptr to buffer to store device name string.
										int bufSize,    //[in] number of bytes in buffer pointed to by pBuf. Strlen(pBuf) < bufSize, because the string is NULL terminated.
										int vendorId,	//[in] USB vendor id
										int productId,	//[in] USB product id
										int N);			//[in] index into list of known devices, 0 => first device in list.


	/***************************************************************************************************************************
	Function Name: GoIO_Sensor_Open()
	
	Purpose:	Open a specified Go! device and the attached sensor.. 
	
				If the device is already open, then this routine will fail.

				In addition to establishing basic communication with the device, this routine will initialize the
				device. Each Pointer sensor object has an associated DDS memory record. If the physical 
				sensor being opened is a 'smart' sensor with its own physical DDS memory, then this routine will copy
				the contents of the memory on the device to the sensor object's DDS memory record. If the physical 
				sensor does not have DDS memory, then the associated DDS memory record is set to default values.

				The following commands are sent to Go! Temp devices:
					SKIP_CMD_ID_INIT,
					SKIP_CMD_ID_READ_LOCAL_NV_MEM. - read DDS record

				The following commands are sent to Go! Link and Vernier Mini GC devices:
					SKIP_CMD_ID_INIT,
					SKIP_CMD_ID_GET_SENSOR_ID,
					SKIP_CMD_ID_READ_REMOTE_NV_MEM, - read DDS record if this is a 'smart' sensor
					SKIP_CMD_ID_SET_ANALOG_INPUT_CHANNEL. - based on sensor EProbeType

				SKIP_CMD_ID_GET_SENSOR_ID is superfluous when sent to the Mini GC, but the Mini GC is implemented internally
				as a Go! Link with a fixed sensor plugged in.

				Only SKIP_CMD_ID_INIT is sent to Go! Motion. Go! Motion does not contain DDS memory, but this routine
				initializes the sensor's associated DDS memory record with calibrations for both meters and feet.

				Since the device stops sending measurements in response to SKIP_CMD_ID_INIT, an application must send
				SKIP_CMD_ID_START_MEASUREMENTS to the device in order to receive measurements.

				At any given time, a sensor is 'owned' by only one thread. The thread that calls this routine is the
				initial owner of the sensor. If a GoIO() call is made from a thread that does not own the sensor object
				that is passed in, then the call will generally fail. To allow another thread to access a sensor,
				the owning thread should call GoIO_Sensor_Unlock(), and then the new thread must call GoIO_Sensor_Lock().
  
	Return:		handle to open sensor device if successful, else NULL.

	 ****************************************************************************************************************************/
	Pointer   GoIO_Sensor_Open(
								char []pDeviceName,	//[in] NULL terminated string that uniquely identifies the device. See GoIO_GetNthAvailableDeviceName().
								int vendorId,		//[in] USB vendor id
								int productId,		//[in] USB product id
								int strictDDSValidationFlag);//[in] insist on exactly valid checksum if 1, else use a more lax validation test.

	/***************************************************************************************************************************
	Function Name: GoIO_Sensor_Close()
	
	Purpose:	Close a specified sensor. After this routine runs the hSensor handle is no longer valid.
	
	Return:		0 if successful, else -1.

	****************************************************************************************************************************/
	int		GoIO_Sensor_Close(
							   Pointer hSensor);//[in] handle to open sensor.
	
	/***************************************************************************************************************************
	Function Name: GoIO_Sensor_GetOpenDeviceName()
	
	Purpose:	Get the unique device name string and USB id's for a specified open device.

	Return:		0 iff successful, else -1.
	
	****************************************************************************************************************************/
	int 	GoIO_Sensor_GetOpenDeviceName(
											Pointer hSensor,	//[in] handle to open sensor.
											char []pBuf,				//[out] ptr to buffer to store device name string.
											int bufSize,	//[in] number of bytes in buffer pointed to by pBuf. Strlen(pBuf) < bufSize, because the string is NULL terminated.
											int []pVendorId,	//[out]
											int []pProductId);//[out]
	

	/***************************************************************************************************************************
	Function Name: GoIO_Sensor_Lock()
	
	Purpose:	Lock a specified sensor so that no other thread can access it. This routine will fail if the sensor
				is currently locked by another thread.

				GoIO_Sensor_Lock() increments the lock count for the specified sensor by 1. In order for a second
				thread to acquire access to the sensor, the first thread must call GoIO_Sensor_Unlock() until the lock
				count reaches 0. Note that the lock count is set to 1 by GoIO_Sensor_Open().
				
				It is ok for the lock count to be greater than 1 when GoIO_Sensor_Close() is called.

				This is an advanced routine that most users should not use. It is only necessary if an application
				needs to access a single sensor from multiple threads.
	
	Return:		0 if successful, else -1.

	****************************************************************************************************************************/
	int 	GoIO_Sensor_Lock(
								Pointer hSensor,	//[in] handle to open sensor.
								int timeoutMs);		//[in] wait this long to acquire the lock before failin	g.

	/***************************************************************************************************************************
	Function Name: GoIO_Sensor_Unlock()
	
	Purpose:	Decrement the lock count for the specified sensor by 1. This routine will fail if the sensor
				is not currently locked by the calling thread.

				In order for a second thread to acquire access to the sensor, the first thread must call 
				GoIO_Sensor_Unlock() until the lock count reaches 0. Note that the lock count is set to 1 by 
				GoIO_Sensor_Open().

				If the lock count reaches zero, it is important to make a call to GoIO_Sensor_Lock() to lock the
				device. This will prevent multiple threads from simultaneously accessing the device, which can cause
				unpredictable results.
				
				It is ok for the lock count to be greater than 1 when GoIO_Sensor_Close() is called.

				This is an advanced routine that most users should not use. It is only necessary if an application
				needs to access a single sensor from multiple threads.
	
	Return:		0 if successful, else -1.

	****************************************************************************************************************************/
	int	Sensor_Unlock(
						Pointer hSensor);//[in] handle to open sensor.
	
	/***************************************************************************************************************************
	Function Name: GoIO_Sensor_ClearIO()
	
	Purpose:	Clear the input/output communication buffers for a specified sensor. 
				This also expties the GoIO Measurement Buffer, which holds measurements that have been retrieved from 
				the sensor.
	
	Return:		0 if successful, else -1.

	****************************************************************************************************************************/
	int GoIO_Sensor_ClearIO(
							Pointer hSensor);//[in] handle to open sensor.	


	/***************************************************************************************************************************
	Function Name: GoIO_Sensor_SendCmdAndGetResponse()
	
	Purpose:	Send a command to the specified Go! device hardware and wait for a response. 

				Note that GoIO_Sensor_SendCmdAndGetResponse() will ASSERT if you send a SKIP_CMD_ID_START_MEASUREMENTS
				while GoIO_Sensor_GetNumMeasurementsAvailable() says measurements are available. 
				This will not occur the first time SKIP_CMD_ID_START_MEASUREMENTS is sent after GoIO_Sensor_Open()
				because GoIO_Sensor_Open() automatically calls GoIO_Sensor_ClearIO().

				So...if you are restarting measurements, you should clear any old measurements in the GoIO Measurement 
				Buffer first by reading in the measurements until the Buffer is empty, or you should call 
				GoIO_Sensor_ClearIO().
	
	Return:		0 if successful, else -1.

	****************************************************************************************************************************/
	int GoIO_Sensor_SendCmdAndGetResponse(
											Pointer hSensor,	//[in] handle to open sensor.
											byte cmd,		//[in] command code. See SKIP_CMD_ID_* in GSkipCommExt.h.
											Pointer pParams,			//[in] ptr to cmd specific parameter block, may be NULL. See GSkipCommExt.h.
											int nParamBytes,//[in] # of bytes in (*pParams).
											Pointer pRespBuf,			//[out] ptr to destination buffer, may be NULL. See GSkipCommExt.h.
											int []pnRespBytes,//[in, out] ptr to size of of pRespBuf buffer on input, size of response on output, may be NULL if pRespBuf is NULL.
											int timeoutMs);	//[in] # of milliseconds to wait for a reply before giving up. Go! devices should reply to almost all the 
																	//currently defined commands within SKIP_TIMEOUT_MS_DEFAULT(1000) milliseconds. In fact, typical response
																	//times are less than 50 milliseconds. See SKIP_TIMEOUT_MS_* definitions.
										
	/***************************************************************************************************************************
	Function Name: GoIO_Sensor_SendCmd()
	
	Purpose:	GoIO_Sensor_SendCmd() is an advanced function. You should usually use 
				GoIO_Sensor_SendCmdAndGetResponse() instead. After calling GoIO_Sensor_SendCmd(), you must call
				GoIO_Sensor_GetNextResponse() before sending any more commands to the device.

				The main reason that GoIO_Sensor_SendCmd() is made available to the user is to allow a program to send
				SKIP_CMD_ID_START_MEASUREMENTS commands to several different devices as close together as possible so that
				measurements start at about the same time on separate devices.
	
	Return:		0 if successful, else -1.

	****************************************************************************************************************************/
	int GoIO_Sensor_SendCmd(
								Pointer hSensor,	//[in] handle to open sensor.
								byte cmd,	//[in] command code
								Pointer pParams,			//[in] ptr to cmd specific parameter block, may be NULL. See GSkipCommExt.h.
								int nParamBytes);//[in] # of bytes in (*pParams).
	
	
	/***************************************************************************************************************************
	Function Name: GoIO_Sensor_GetNextResponse()
	
	Purpose:	GoIO_Sensor_GetNextResponse() is an advanced function. You should usually use 
				GoIO_Sensor_SendCmdAndGetResponse() instead. After calling GoIO_Sensor_SendCmd(), you must call
				GoIO_Sensor_GetNextResponse() before sending any more commands to the device.


	Return:		0 if successful, else -1.
	
	****************************************************************************************************************************/
	int GoIO_Sensor_GetNextResponse(
										Pointer hSensor,	//[in] handle to open sensor.
										Pointer pRespBuf,				//[out] ptr to destination buffer, may be NULL. See GSkipCommExt.h.
										int  []pnRespBytes,	//[in, out] ptr to size of of pRespBuf buffer on input, size of response on output, may be NULL if pRespBuf is NULL.
										byte []pCmd,		//[out] identifies which command this response is for. Ptr must NOT be NULL!
										int  []pErrRespFlag,	//[out] flag(1 or 0) indicating that the response contains error info. Ptr must NOT be NULL!
										int  nTimeoutMs);	//[in] # of milliseconds to wait before giving up.


	
	/***************************************************************************************************************************
	Function Name: GoIO_Sensor_GetMeasurementTickInSeconds()
	
	Purpose:	The measurement period for Go! devices is specified in discrete 'ticks', so the actual time between 
				measurements is an integer multiple of the tick time. The length of time between ticks is different 
				for Go! Link versus Go! Temp. 
	
	Return:		If hSensor is not valid, then this routine returns -1.0, else the return value = the length of time
				in seconds between ticks.

	****************************************************************************************************************************/
	//FIX: verify that double is 64 bit float
	double  GoIO_Sensor_GetMeasurementTickInSeconds(
														Pointer hSensor);//[in] handle to open sensor.

	
	
	/***************************************************************************************************************************
	Function Name: GoIO_Sensor_GetMinimumMeasurementPeriod()
	
	Return:		If hSensor is not valid, then this routine returns -1.0, else the return value = minimum measurement
				period in seconds that is supported by the device.

	****************************************************************************************************************************/
	double GoIO_Sensor_GetMinimumMeasurementPeriod(
													Pointer hSensor);//[in] handle to open sensor.
	
	/***************************************************************************************************************************
		Function Name: GoIO_Sensor_GetMaximumMeasurementPeriod()
		
		Return:		If hSensor is not valid, then this routine returns -1.0, else the return value = maximum measurement
					period in seconds that is supported by the device.
	
	****************************************************************************************************************************/
	double GoIO_Sensor_GetMaximumMeasurementPeriod(
													Pointer hSensor);//[in] handle to open sensor.



	/***************************************************************************************************************************
		Function Name: GoIO_Sensor_GetNumMeasurementsAvailable()
		
		Purpose:	Report the number of measurements currently stored in the GoIO Measurement Buffer. 

					After SKIP_CMD_ID_START_MEASUREMENTS has been sent to the sensor, the sensor starts
					sending measurements to the host computer. These measurements are stored in the 
					GoIO Measurement Buffer. A separate GoIO Measurement Buffer is maintained for each
					open sensor.

					The delay between sending SKIP_CMD_ID_START_MEASUREMENTS and the appearance of the first
					measurement in the GoIO Measurement Buffer varies according to the type of Go! device.

					Go! device type	                Delay before first measurement
					-----------------               --------------------------------
					Go! Temp                        ~510 milliseconds
					Go! Link                        ~10 milliseconds
					Mini GC                         ~10 milliseconds
					Go! Motion                      ~ measurement period + 10 milliseconds

					The 10 millisecond delay specifed for Go! Link is just the approximate delay required for the
					data to come back from the device via USB. Go! Link actually performs the first measurement
					immediately.

					Subsequent measurements are sent at the currently configured measurement period interval. 
					See GoIO_Sensor_SetMeasurementPeriod().

					Call GoIO_Sensor_ReadRawMeasurements() to retrieve measurements from the
					GoIO Measurement Buffer. The GoIO Measurement Buffer is guaranteed to hold at least 1200
					measurements. The buffer is circular, so if you do not service it often enough, the
					oldest measurements in the buffer are lost. If you wish to capture all the 
					measurements coming from the sensor, you must call GoIO_Sensor_ReadRawMeasurements()
					often enough so that the GoIO_Sensor_GetNumMeasurementsAvailable() does not reach 1200.
					On the other hand, we reserve the right to make the Measurement Buffer > 1200 measurements, so
					do not assume that you can empty the buffer simply by reading in 1200 measurements.

					Each of the following actions clears the GoIO Measurement Buffer:
						1) Call GoIO_Sensor_ReadRawMeasurements() with count set to GoIO_Sensor_GetNumMeasurementsAvailable(), or
						2) Call GoIO_Sensor_GetLatestRawMeasurement(), or
						3) Call GoIO_Sensor_ClearIO().

					The GoIO Measurement Buffer is empty after GoIO_Sensor_Open() is called. It does not
					start filling up until SKIP_CMD_ID_START_MEASUREMENTS is sent to the sensor.

					Note that the sensor stops sending measurements to the host computer after
					SKIP_CMD_ID_STOP_MEASUREMENTS or SKIP_CMD_ID_INIT is sent, but sending these commands
					does not clear the GoIO Measurement Buffer.

		Return:		number of measurements currently stored in the GoIO Measurement Buffer. 

	****************************************************************************************************************************/
	int GoIO_Sensor_GetNumMeasurementsAvailable(
												Pointer hSensor);//[in] handle to open sensor.


	

	/***************************************************************************************************************************
		Function Name: GoIO_Sensor_ReadRawMeasurements()
		
		Purpose:	Retrieve measurements from the GoIO Measurement Buffer. The measurements reported
					by this routine are actually removed from the GoIO Measurement Buffer.

					After SKIP_CMD_ID_START_MEASUREMENTS has been sent to the sensor, the sensor starts
					sending measurements to the host computer. These measurements are stored in the 
					GoIO Measurement Buffer. A separate GoIO Measurement Buffer is maintained for each
					open sensor. See the description of GoIO_Sensor_GetNumMeasurementsAvailable().

					Note that for Go! Temp and Go! Link, raw measurements range from -32768 to 32767.
					Go! Motion raw measurements are in microns and can range into the millions.

					To convert a raw measurement to a voltage use GoIO_Sensor_ConvertToVoltage().
					To convert a voltage to a sensor specific calibrated unit, use GoIO_Sensor_CalibrateData().

					WARNING!!! IF YOU ARE COLLECTING MORE THAN 50 MEASUREMENTS A SECOND FROM GO! LINK,
					READ THIS: The GoIO Measurement Buffer is packet oriented. If you are collecting 50 or
					less measurements per second, then each packet contains only 1 measurement, and there is
					no problem.

					If you are collecting more than 50 measurements a second, then each packet may contain 2 
					or 3 measurements. Depending on the exact measurement period, all the packets will 
					contain 2, or all the packets will contain 3 measurements. IF THE LAST MEASUREMENT COPIED
					INTO pMeasurementsBuf IS NOT THE LAST MEASUREMENT IN ITS PACKET, THEN MEASUREMENTS MAY
					BE LOST.
					There are a couple of safe workarounds to this problem:
						1) Always set the maxCount parameter to a multiple of 6, or
						2) Always set the maxCount parameter to GoIO_Sensor_GetNumMeasurementsAvailable().

		Return:		number of measurements retrieved from the GoIO Measurement Buffer. This routine
					returns immediately, so the return value may be less than maxCount.

	****************************************************************************************************************************/
	 int GoIO_Sensor_ReadRawMeasurements(
											Pointer hSensor,		//[in] handle to open sensor.
											int []pMeasurementsBuf,	//[out] ptr to loc to store measurements.
											int maxCount);	//[in] maximum number of measurements to copy to pMeasurementsBuf. See warning above.

	/***************************************************************************************************************************
		Function Name: GoIO_Sensor_GetLatestRawMeasurement()
		
		Purpose:	Report the most recent measurement put in the GoIO Measurement Buffer. If no 
					measurements have been placed in the GoIO Measurement Buffer since it was
					created byGoIO_Sensor_Open(), then report a value of 0. 
					
					This routine also empties the GoIO Measurement Buffer, so GoIO_Sensor_GetNumMeasurementsAvailable()
					will report 0 after calling GoIO_Sensor_GetLatestRawMeasurement().

					After SKIP_CMD_ID_START_MEASUREMENTS has been sent to the sensor, the sensor starts
					sending measurements to the host computer. These measurements are stored in the 
					GoIO Measurement Buffer. A separate GoIO Measurement Buffer is maintained for each
					open sensor. See the description of GoIO_Sensor_GetNumMeasurementsAvailable().

					Note that for Go! Temp and Go! Link, raw measurements range from -32768 to 32767.
					Go! Motion raw measurements are in microns and can range into the millions.

					To convert a raw measurement to a voltage use GoIO_Sensor_ConvertToVoltage().
					To convert a voltage to a sensor specific calibrated unit, use GoIO_Sensor_CalibrateData().

		Return:		most recent measurement put in the GoIO Measurement Buffer. If no 
					measurements have been placed in the GoIO Measurement Buffer since it was
					created byGoIO_Sensor_Open(), then report a value of 0.

	****************************************************************************************************************************/
	int GoIO_Sensor_GetLatestRawMeasurement(
			 									Pointer hSensor);//[in] handle to open sensor.


	 
	 
	 
	/***************************************************************************************************************************
	Function Name: GoIO_Sensor_ConvertToVoltage()
	
	Purpose:	Convert a raw measurement integer value into a real voltage value.
				Depending on the type of sensor(see GoIO_Sensor_GetProbeType()), the voltage
				may range from 0.0 to 5.0, or from -10.0 to 10.0 . For Go! Motion, voltage returned is simply distance
				in meters.

	Return:		voltage corresponding to a specified raw measurement value.

	****************************************************************************************************************************/
	double GoIO_Sensor_ConvertToVoltage(
											Pointer hSensor,	//[in] handle to open sensor.
											int rawMeasurement);//[in] raw measurement obtained from GoIO_Sensor_GetLatestRawMeasurement() or 
																		//GoIO_Sensor_ReadRawMeasurements().
	
	/***************************************************************************************************************************
		Function Name: GoIO_Sensor_CalibrateData()
		
		Purpose:	Convert a voltage value into a sensor specific unit.
					What units this routine produces can be determined by calling
					GoIO_Sensor_DDSMem_GetCalPage(hSensor, GoIO_Sensor_DDSMem_GetActiveCalPage(),...) .
	
		Return:		value in sensor specific units corresponding to a specified voltage. Just return input volts
					unless GoIO_Sensor_DDSMem_GetCalibrationEquation() indicates kEquationType_Linear.
	
	****************************************************************************************************************************/
	double GoIO_Sensor_CalibrateData(
										Pointer hSensor,	//[in] handle to open sensor.
										double volts);		//[in] voltage value obtained from GoIO_Sensor_ConvertToVoltage();
	
	/***************************************************************************************************************************
		Function Name: GoIO_Sensor_GetProbeType()
		
		Purpose:	Find out the probe type. See EProbeType in GSensorDDSMem.h.
	
					For Go! Link devices, this attribute is dependent on the OperationType in the SensorDDSRecord. 
					See GoIO_Sensor_DDSMem_GetOperationType().
					If (2 == OperationType) then the sensor is kProbeTypeAnalog10V, else kProbeTypeAnalog5V.
	
					Note that for Go! Link devices, SKIP_CMD_ID_SET_ANALOG_INPUT_CHANNEL must be sent to the device with
					the analogInputChannel parameter set to SKIP_ANALOG_INPUT_CHANNEL_VIN_LOW for kProbeTypeAnalog5V,
					or set to SKIP_ANALOG_INPUT_CHANNEL_VIN for kProbeTypeAnalog10V devices. Generally, application
					programs do not have to deal with this, because GoIO_Sensor_Open() automatically sends
					SKIP_CMD_ID_SET_ANALOG_INPUT_CHANNEL to the device with the appropriate parameters.
	
					Go! Temp => kProbeTypeAnalog5V.
					Go! Motion => kProbeTypeMD.
	
		Return:		EProbeType.
	
	****************************************************************************************************************************/
	int GoIO_Sensor_GetProbeType(
									Pointer hSensor);//[in] handle to open sensor.


	/*
	 * READ THIS:
	 * The bottom of the GoIO_DLL_interface.h.file (line 604 and below) has DMM functions that can be added if needed.
	 * (IE they are NOT here yet.)
	 */
	 
	 
	 
}
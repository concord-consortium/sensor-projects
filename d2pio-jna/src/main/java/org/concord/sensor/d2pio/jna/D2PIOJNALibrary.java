package org.concord.sensor.d2pio.jna;

import com.sun.jna.Library;
import com.sun.jna.Pointer;
import com.sun.jna.Structure;
import com.sun.jna.ptr.ByteByReference;
import com.sun.jna.ptr.IntByReference;
import com.sun.jna.ptr.ShortByReference;
import com.sun.jna.ptr.LongByReference;
import com.sun.jna.ptr.DoubleByReference;

public interface D2PIOJNALibrary extends Library {

	public final static int D2PIO_DEVTYPE_GENERIC = 30;
	public final static int D2PIO_COMM_TRANSPORT_USB = 1;
	public final static int D2PIO_MAX_SIZE_DEVICE_NAME = 220;
	public final static int D2PIO_MAX_NUM_BYTES_IN_SENSOR_DESCRIPTION = 60;
	public final static int D2PIO_MAX_NUM_BYTES_IN_SENSOR_UNIT = 32;

	public final static int D2PIO_DEVICE_OPEN_STATUS_ERROR = 1;
	public final static int D2PIO_DEVICE_OPEN_STATUS_IN_PROGRESS = 2;
	public final static int D2PIO_DEVICE_OPEN_STATUS_TIMEOUT = 3;
	public final static int D2PIO_DEVICE_OPEN_STATUS_SUCCESS = 4;
	public final static int D2PIO_DEVICE_OPEN_STATUS_CLOSED = 5;

	public final static int D2PIO_USB_OPEN_TIMEOUT_MS = 3000;

	public final static byte D2PIO_CMD_ID_GET_STATUS = 0x10;
	public final static byte D2PIO_CMD_ID_START_MEASUREMENTS = 0x18;
	public final static byte D2PIO_CMD_ID_STOP_MEASUREMENTS = 0x19;
	public final static int D2PIO_TIMEOUT_MS_DEFAULT = (2000*4);

	public final static int D2PIO_NUMERIC_MEAS_TYPE_REAL64 = 0;
	public final static int D2PIO_NUMERIC_MEAS_TYPE_INT32 = 1;

	/***************************************************************************************************************************
	Function Name: D2PIO_Init()

	Purpose:	Call D2PIO_Init() once before making any other D2PIO function calls.
				D2PIO_Init() and D2PIO_Uninit() should be called from the same thread.

	Return:		Handle to D2PIO library if successful, else NULL.
	****************************************************************************************************************************/
	Pointer D2PIO_Init(
		byte bInitUSB,	//[in]
		byte bInitBLE,	//[in]
		Pointer pOS_libParms, //[in] ptr to OS specific parms, usually NULL
		int parmsLen);	//[in] length of OS specific parms, usually zero

	/***************************************************************************************************************************
	Function Name: D2PIO_Uninit()

	Purpose:	Call D2PIO_Uninit() once to 'undo' D2PIO_Init().
				D2PIO_Init() and D2PIO_Uninit() should be called from the same thread.

	Return:		0 iff successful, else -1.
	****************************************************************************************************************************/
	int D2PIO_Uninit(Pointer hLib);//[in] handle to library returned by D2PIO_Init().

	/***************************************************************************************************************************
	Function Name: D2PIO_GetLibVersion()

	Purpose:	This routine returns the major and minor version numbers for the instance of the D2PIO library that is
				currently running.

				If a function is not guaranteed to be present in all supported versions of the D2PIO library, then the line
				"Added in version 'major.minor'" will appear in the function description in this file.

				It is our intention that all versions of the D2PIO library created subsequent to a given version, will be
				backwards compatible with the older version. You should be able to replace an old version of the D2PIO library
				with a newer version and everything should still work without rebuilding your application.

				Note that version major2.minor2 is later than version major1.minor1
				iff. ((major2 > major1) || ((major2 == major1) && (minor2 > minor1))).

				Backwards compatibility is definitely our intention, but we do not absolutely guarantee it. If you think
				that you have detected a backwards compatibility bug, then please report it to Vernier Software & Technology.
				Calling D2PIO_GetDLLVersion() from your application is a way to identify precisely which version of
				the D2PIO library you are actually using.

	Return:		0 iff successful, else -1.
	****************************************************************************************************************************/
	int D2PIO_GetLibVersion(
		Pointer hLib,	//[in] handle returned by D2PIO_Init()
		ShortByReference pMajorVersion, //[out]
		ShortByReference pMinorVersion); //[out]

	/***************************************************************************************************************************
	Function Name: D2PIO_SearchForDevices()

	Purpose:	The D2PIO library maintains a separate list of available devices for each supported device type.
				D2PIO_SearchForDevices() updates the list for the specified device type.

				Currently, D2PIO_SearchForDevices() only works for D2PIO_COMM_TRANSPORT_USB.
				Use D2PIO_EnableAutoDeviceDiscoveryAcrossCommTransport() for D2PIO_COMM_TRANSPORT_BLUETOOTH_LE.

				Call D2PIO_OpenDeviceListSnapshot() to access the list of discovered devices.

	Return:		0 iff successful, else -1.
	****************************************************************************************************************************/
	int D2PIO_SearchForDevices(
		Pointer hLib,			//[in] handle returned by D2PIO_Init()
		int deviceType,			//[in] D2PIO_DEVTYPE_...
		int commTransportId,	//[in] D2PIO_COMM_TRANSPORT_...
		Pointer pParams,		//[in] ptr to context specific search params. Not currently used.
		IntByReference pDeviceListSignature);//[out] ptr to device list signature. Signatures are not fully implemented yet.

	/***************************************************************************************************************************
	Function Name: D2PIO_OpenDeviceListSnapshot()

	Purpose:	The D2PIO library maintains a separate list of available devices for each supported device type.
				Use D2PIO_SearchForDevices() to update the list for D2PIO_COMM_TRANSPORT_USB.
				Use D2PIO_EnableAutoDeviceDiscoveryAcrossCommTransport() to update the list for D2PIO_COMM_TRANSPORT_BLUETOOTH_LE.

				D2PIO_OpenDeviceListSnapshot() creates a snapshot copy of the device list for the specified device type.
				Use the handle returned by D2PIO_OpenDeviceListSnapshot() to walk the device list.

				A device is placed in the list snapshot even if it has already been opened.

	Return:		handle to device list snapshot iff successful, else NULL.
	****************************************************************************************************************************/
	Pointer D2PIO_OpenDeviceListSnapshot(
		Pointer hLib,				//[in] handle returned by D2PIO_Init()
		int deviceType,				//[in] D2PIO_DEVTYPE_...
		int commTransportId,		//[in] D2PIO_COMM_TRANSPORT_...
		IntByReference pNumDevices,	//[out] ptr to storage loc for the number of devices in the list
		IntByReference pDeviceListSignature);//[out] ptr to device list signature. Signatures are not fully implemented yet.

	/***************************************************************************************************************************
	Function Name: D2PIO_CloseDeviceListSnapshot()

	Purpose:	Close the list snapshot created by D2PIO_OpenDeviceListSnapshot().

	Return:		0 iff successful, else -1.
	****************************************************************************************************************************/
	int D2PIO_CloseDeviceListSnapshot(Pointer hDeviceList);//[in] handle returned by D2PIO_OpenDeviceListSnapshot().

	/***************************************************************************************************************************
	Function Name: D2PIO_DeviceListSnapshot_GetNthEntry()

	Purpose:	Return the Nth entry in the list created by D2PIO_OpenDeviceListSnapshot().
				A device is placed in the list snapshot even if it has already been opened.

				Pass the device name string placed in *pDevnameBuf to D2PIO_Device_Open() to open the device. Each
				device name string uniquely identifies the device, so you can determine if a device is already open
				by comparing *pDevnameBuf with the string returned by D2PIO_Device_GetOpenDeviceName() for each open
				device handle.

	Return:		0 iff successful, else -1.
	****************************************************************************************************************************/
	int D2PIO_DeviceListSnapshot_GetNthEntry(
		Pointer hDeviceList,	//[in] handle returned by D2PIO_OpenDeviceListSnapshot()
		int N,					//[in] index into list of known devices, 0 => first device in list.
		Pointer pDevnameBuf,	//[out] ptr to buffer to store NULL terminated UTF-8 string that uniquely identifies the device. Pass this to D2PIO_Device_Open().
		int DevnameBufsize,		//[in] number of bytes in buffer pointed to by pDevnameBuf. strlen(pDevnameBuf) < bufSize, because the string is NULL terminated.
								//strlen(pDevnameBuf)is guaranteed to be < D2PIO_MAX_SIZE_DEVICE_NAME.
		Pointer pDevFriendlyNameBuf,	//[out] ptr to buffer to store NULL terminated UTF-8 string that may be displayed for the user to identify the device.
								//Note that pDevFriendlyNameBuf is not very friendly for D2PIO_COMM_TRANSPORT_USB.
								//However, the friendly name can be obtained via D2PIO_Device_GetOpenDeviceName() after the device is open.
		int DevFriendlyNameBufsize,//[in] number of UTF-8 chars in buffer pointed to by pDevFriendlyNameBuf. strlen(pDevFriendlyNameBuf) < bufSize,
								//because the string is NULL terminated.
								//strlen(pDevFriendlyNameBuf)is guaranteed to be < D2PIO_MAX_SIZE_DEVICE_NAME.
		IntByReference pRSSI_level);	//[out] Valid RSSI levels are < 0. 0 is an invalid RSSI level. This output is only set for D2PIO_COMM_TRANSPORT_BLUETOOTH_LE.
	/***************************************************************************************************************************
	Function Name: D2PIO_Device_Open()

	Purpose:	Open a device with the name returned by D2PIO_DeviceListSnapshot_GetNthEntry.

				D2PIO_Device_Open() returns immediately with a non NULL handle if successful.
				Call D2PIO_Device_GetOpenStatus() or wait for D2PIODeviceNotificationOpenStatus to be reported via the supplied
				callback function until device_open_status is not D2PIO_DEVICE_OPEN_STATUS_IN_PROGRESS.

				In theory, once the device_open_status is D2PIO_DEVICE_OPEN_STATUS_SUCCESS, the library knows basically
				everything that it needs to know about the open device. Query functions such as
				D2PIO_GetMeasurementChannelAvailabilityMask() and D2PIO_Device_GetMeasurementChannelInfo() allow client
				applications to discover device details. These query functions generally run almost instantaneously
				because the info that they are supplying to the caller was cached in local memory by D2PIO_Device_Open().

				After calling D2PIO_Device_Open() and waiting for D2PIO_DEVICE_OPEN_STATUS_SUCCESS, an application needs to perform the
				following operations to take measurements:

				-	Call D2PIO_Device_SetMeasurementPeriod() to set the sampling period used for periodic measurements.
				-	Send a D2PIO_CMD_ID_START_MEASUREMENTS command to start taking measurements.
				-	Call D2PIO_Device_ReadMeasurements(channel) to retrieve measurements.
				-	Send a D2PIO_CMD_ID_STOP_MEASUREMENTS command to stop taking measurements. Once D2PIO_CMD_ID_STOP_MEASUREMENTS
					has been sent, the app can take its time about calling D2PIO_Device_ReadMeasurements() to pull measurements
					out of the measurement buffers. However, the app must empty the measurement buffers before sending
					D2PIO_CMD_ID_START_MEASUREMENTS again.

				An more detailed overview of this process is demonstrated in the D2PIO_DeviceCheck sample application.

				D2PIO_Device_Open() primarily opens devices of the type D2PIO_DEVTYPE_GENERIC, which corresponds to any of the
				standard Go Direct devices from Vernier.
				If the D2PIO library was built with the macro SUPPORT_GO_WIRELESS defined, then D2PIO_Device_Open() can also open
				devices of the type D2PIO_DEVTYPE_GO_WIRELESS_TEMPERATURE, D2PIO_DEVTYPE_GO_WIRELESS_PH, D2PIO_DEVTYPE_GO_WIRELESS_EA,
				and D2PIO_DEVTYPE_POLAR_HEART_RATE.

				The Go Direct devices support the command protocol documented in D2PIOSourceCmds.h .
				The Go Wireless devices support a small subset of the protocol. Basically just enough to take measurements.
				The command protocol is implemented using D2PIO_Device_SendCmdAndGetResponse().

				At any given time, a device is 'owned' by only one thread. The thread that calls D2PIO_Device_Open() is the
				initial owner of the device. If a D2PIO() call is made from a thread that does not own the device object
				that is passed in, then the call will generally fail. To allow another thread to access a device,
				the owning thread must call D2PIO_Device_Unlock(), and then the new thread should call D2PIO_Device_Lock().

				Note that the supplied function notificationCallbackFunc runs on a thread internal to D2PIO library.
				No assumptions should be made about this execution context. In particular, the code supplied in
				notificationCallbackFunc should be very 'light weight' and it should never block. This function should always
				return in less than 10 milliseconds. Failure to meet this constraint can break D2PIO lib.
				Basically this routine should post a message and return immediately. You should not call D2PIO functions in this
				callback, and if you do so, they will generally fail.

	Return:		handle to open device if successful, else NULL.
	****************************************************************************************************************************/
	Pointer D2PIO_Device_Open(
		Pointer hLib,
		String pDeviceName,		//[in] NULL terminated UTF-8 string that uniquely identifies the device. See D2PIO_DeviceListSnapshot_GetNthEntry().
		Pointer parmsPtr,		//[in] ptr to open configuration parms
		int parmsLen,			//[in] length of open configuration parms. (parmsLen == sizeof(gtype_bool)) => parmsPtr points to (gtype_bool ReadNVMemoryFlag).
		int timeoutMs,			//[in] D2PIO_Device_Open() returns immediately. Call D2PIO_Device_GetOpenStatus() to see how Open is progressing.
		Pointer notificationCallbackFunc, //ptr to notification callback, used for various device notifications including open status changes. May be NULL.
		Pointer pContextInfo);	//ptr passed back via notificationCallbackFunc
	/***************************************************************************************************************************
	Function Name: D2PIO_Device_GetOpenStatus()

	Purpose:	Determine that status of the most recent D2PIO_Device_Open() call.

				D2PIO_Device_Open() returns immediately with a non NULL handle if successful.
				Call D2PIO_Device_GetOpenStatus() or wait for D2PIODeviceNotificationOpenStatus to be reported via the supplied
				callback function until device_open_status is not D2PIO_DEVICE_OPEN_STATUS_IN_PROGRESS.

	Return:		0 iff successful, else -1.
	****************************************************************************************************************************/
	int D2PIO_Device_GetOpenStatus(
		Pointer hDevice,				//[in] handle to open device.
		IntByReference pDeviceOpenStatus);//[out] D2PIO_DEVICE_OPEN_STATUS_...

	/***************************************************************************************************************************
	Function Name: D2PIO_Device_Close()

	Purpose:	Close a specified device. After this routine runs, the hDevice handle is no longer valid.
				If D2PIO_Device_Open() returns a non NULL handle, then you should always call D2PIO_Device_Close(handle) when
				you are done with the device, even if an error occurred.

				D2PIO_Device_Close() must be called from the thread that currently owns the device.

	Return:		0 if successful, else -1.
	****************************************************************************************************************************/
	int D2PIO_Device_Close(
		Pointer hDevice);//[in] handle to open device.

	/***************************************************************************************************************************
	Function Name: D2PIO_Device_GetOpenDeviceName()

	Purpose:	Get the unique device name string for a specified open device. This is the string returned by
				D2PIO_DeviceListSnapshot_GetNthEntry() and passed in to D2PIO_Device_Open().

				This routine also returns the 'friendly' name for the device which may be displayed for the user to identify the device.

	Return:		0 iff successful, else -1.
	****************************************************************************************************************************/
	int D2PIO_Device_GetOpenDeviceName(
		Pointer hDevice,			//[in] handle to open device.
		Pointer pDeviceName,		//[out] ptr to buffer to store NULL terminated UTF-8 string that uniquely identifies the device. This was passed to D2PIO_Device_Open(). May be NULL.
		int DevnameBufsize,			//[in] number of bytes in buffer pointed to by pDeviceName. strlen(pDeviceName) < bufSize, because the string is NULL terminated.
		Pointer pDevFriendlyNameBuf,//[out] ptr to buffer to store NULL terminated UTF-8 string that may be displayed for the user to identify the device. May be NULL.
		int DevFriendlyNameBufsize);//[in] number of UTF-8 chars in buffer pointed to by pDevFriendlyNameBuf. strlen(pDevFriendlyNameBuf) < bufSize, because the string is NULL terminated.
									//strlen(pDevFriendlyNameBuf)is guaranteed to be < D2PIO_MAX_SIZE_DEVICE_NAME.

	/***************************************************************************************************************************
	Function Name: D2PIO_Device_GetDeviceDescription()

	Purpose:	Get the device description string for the open device.

	Return:		0 iff successful, else -1.
	****************************************************************************************************************************/
	int D2PIO_Device_GetDeviceDescription(
		Pointer hDevice,			//[in] handle to open device.
		Pointer pDeviceDescription,	//[out] ptr to buffer to store NULL UTF-8 terminated string that describes the device.
		int DescriptionBufsize);	//[in] number of bytes in buffer pointed to by pDeviceDescription. strlen(pDeviceDescription) < bufSize, because the string is NULL terminated.
									//strlen(pDeviceDescription)is guaranteed to be < D2PIO_MAX_SIZE_DEVICE_NAME.

	/***************************************************************************************************************************
	Function Name: D2PIO_Device_GetOpenDeviceType()

	Purpose:	Query open device for device type.

	Return:		D2PIO_DEVTYPE_... if successful, else -1.
	****************************************************************************************************************************/
	int D2PIO_Device_GetOpenDeviceType( //Returns D2PIO_DEVTYPE_... if successful, else -1
		Pointer hDevice);				//[in] handle to open device.

	/***************************************************************************************************************************
	Function Name: D2PIO_Device_GetOrderCode()

	Purpose:	Get the OrderCode string for the open device.

	Return:		0 iff successful, else -1.
	****************************************************************************************************************************/
	int D2PIO_Device_GetOrderCode(
		Pointer hDevice,	//[in] handle to open device.
		Pointer pOrderCode,	//[out] ptr to buffer to store NULL terminated UTF-8 string for the OrderCode.
		int Bufsize);		//[in] number of UTF-8 chars in buffer pointed to by pOrderCode. strlen(pOrderCode) < bufSize, because the string is NULL terminated.
							//strlen(pOrderCode) is guaranteed to be < D2PIO_MAX_ORDERCODE_LENGTH.

	/***************************************************************************************************************************
	Function Name: D2PIO_Device_GetSerialNumber()

	Purpose:	Get the SerialNumber string for the open device.

	Return:		0 iff successful, else -1.
	****************************************************************************************************************************/
	int D2PIO_Device_GetSerialNumber(
		Pointer hDevice,		//[in] handle to open device.
		Pointer pSerialNumber,	//[out] ptr to buffer to store NULL terminated UTF-8 string for the SerialNumber.
		int Bufsize);			//[in] number of UTF-8 chars in buffer pointed to by pSerialNumber. strlen(pSerialNumber) < bufSize, because the string is NULL terminated.
								//strlen(pSerialNumber) is guaranteed to be < D2PIO_MAX_SERIALNUMBER_LENGTH.

	/***************************************************************************************************************************
	Function Name: D2PIO_Device_GetManufacturingInfo()

	Purpose:	Get the manufacturing info for the open device.

	Return:		0 iff successful, else -1.
	****************************************************************************************************************************/
	int D2PIO_Device_GetManufacturingInfo(
		Pointer hDevice,					//[in] handle to open device.
		ShortByReference pManufacturerId,	//[out]
		ShortByReference pManufacturedYear,	//[out]
		ByteByReference pManufacturedMonth,	//[out]
		ByteByReference pManufacturedDay);	//[out] day of month

	/***************************************************************************************************************************
	Function Name: D2PIO_Device_GetFirmwareVersionInfo()

	Purpose:	Get the firmware version info for the open device.

	Return:		0 iff successful, else -1.
	****************************************************************************************************************************/
	int D2PIO_Device_GetFirmwareVersionInfo(
		Pointer hDevice,	//[in] handle to open device.
		ByteByReference pMajorVersionMasterCPU, //[out] Binary coded decimal
		ByteByReference pMinorVersionMasterCPU, //[out] Binary coded decimal
		ShortByReference pBuildNumMasterCPU, 	//[out]
		ByteByReference pMajorVersionSlaveCPU, 	//[out] Binary coded decimal
		ByteByReference pMinorVersionSlaveCPU, 	//[out] Binary coded decimal
		ShortByReference pBuildNumSlaveCPU); 	//[out]


	/***************************************************************************************************************************
	Purpose:	Send a command to the specified device hardware and wait for a response.

				Each device type has a command protocol that is unique to that device type.
				The Go Direct device (D2PIO_DEVTYPE_GENERIC) protocol is documented in D2PIOSourceCmds.h .

				Note that if you send D2PIO_CMD_ID_START_MEASUREMENTS while D2PIO_Device_GetNumMeasurementsAvailable()
				says measurements are available, then the internal measurement buffers managed by the D2PIO library will
				be flushed so that any measurements retrieved sebsequently will have come in after D2PIO_CMD_ID_START_MEASUREMENTS
				was sent.

				Every command supported by D2PIO_Device_SendCmdAndGetResponse() has an associated response. If no response
				specific to a command is defined, then the format of the response is D2PIODefaultCmdResponse. Some commands
				have associated parameter blocks defined for them. If the caller is not concerned about the contents
				of the response for a command, he may pass in NULL for pRespBuf. This is reasonable because the return
				code for D2PIO_Device_SendCmdAndGetResponse() will indicate success or failure. Even if NULL is
				passed in for pRespBuf, D2PIO_Device_SendCmdAndGetResponse() always waits for a response to come back
				from the device. If no response is received after timeoutMs milliseconds, then D2PIO_Device_SendCmdAndGetResponse()
				will return with and error code.

				If D2PIO_Device_SendCmdAndGetResponse() returns -1 and (1 == *pnRespBytes), then *pRespBuf contains
				D2PIODefaultCmdResponse, even if a different response structure is defined for the command.
				The D2PIODefaultCmdResponse structure contains only a single status byte field, which contains
				a D2PIO_STATUS_ERROR_... value. Additional information about a D2PIO_Device_SendCmdAndGetResponse() error may be obtained
				by calling D2PIO_Device_GetLastCmdResponseStatus().

				Errors coming back from D2PIO_Device_SendCmdAndGetResponse() may or may not be fatal.
				By definition errors are fatal if they cause cause D2PIO_Device_GetOpenStatus() to return
				D2PIO_DEVICE_OPEN_STATUS_ERROR or D2PIO_DEVICE_OPEN_STATUS_TIMEOUT.

	Return:		0 if successful, else -1.
	****************************************************************************************************************************/
  int D2PIO_Device_SendCmdAndGetResponse(
		Pointer hDevice,	//[in] handle to open device.
		Byte cmd,			//[in] command code.
		Pointer pParams,			//[in] ptr to cmd specific parameter block, may be NULL. See GSkipCommExt.h.
		int nParamBytes,	//[in] # of bytes in (*pParams).
		Pointer pRespBuf,			//[out] ptr to destination buffer, may be NULL.
		IntByReference pnRespBytes,	//[in, out] ptr to size of of pRespBuf buffer on input, size of response on output, should be NULL if pRespBuf is NULL.
		int timeoutMs);	//[in] # of milliseconds to wait for a reply before giving up. Most devices should reply to almost all the
															//currently defined commands within D2PIO_TIMEOUT_MS_DEFAULT milliseconds. In fact, typical response
															//times are less than 50 milliseconds. See D2PIO_TIMEOUT_MS_* definitions.

	/***************************************************************************************************************************
	Function Name: D2PIO_Device_GetMeasurementChannelInfo()

	Purpose:	In theory, most of what you need to know about taking measurements from a previously unknown sensor can
				be obtained by calling D2PIO_Device_GetMeasurementChannelInfo(channel) for the channels listed by
				D2PIO_GetMeasurementChannelAvailabilityMask().

	Return:		0 if successful, else -1.
	****************************************************************************************************************************/
	int D2PIO_GetMeasurementChannelAvailabilityMask(
		Pointer hDevice,	//[in] handle to open device.
		ByteByReference pChannelAvailabilityMask);//[out] ptr to loc to store mask identifying which channels are supported by the device


	/***************************************************************************************************************************
	Function Name: D2PIO_Device_SetMeasurementPeriod()

	Purpose:	This routine sends D2PIO_CMD_ID_SET_MEASUREMENT_PERIOD to the device to set the measurement period for
				the specified channel.

				Note that the measurement period setting only affects channels for which D2PIO_Device_GetMeasurementChannelInfo(channel)
				reports SamplingMode == D2PIO_SAMPLING_MODE_PERIODIC.

				You can find out information about valid measurment periods by calling D2PIO_Device_GetMeasurementChannelPeriodInfo().

				Currently, D2PIO devices only support a single measurement period per device, so the channel argument to
				D2PIO_Device_SetMeasurementPeriod() is ignored. This may change in the future.

	Return:		0 if successful, else -1.
	****************************************************************************************************************************/
  int D2PIO_Device_SetMeasurementPeriod(
	  Pointer hDevice,//[in] handle to open device.
	  byte channel,		//[in] -1 => all channels. Currently ignored.
	  double desiredPeriod,	//[in] desired measurement period in seconds.
	  int timeoutMs);	//[in] # of milliseconds to wait for a reply before giving up. D2PIO_TIMEOUT_MS_DEFAULT is recommended.

	/***************************************************************************************************************************
	Function Name: D2PIO_Device_GetMeasurementPeriod()

	Purpose:	This routine sends D2PIO_CMD_ID_GET_MEASUREMENT_PERIOD to the device to get the measurement period for
				the specified channel.

				Currently, D2PIO devices only support a single measurement period per device, so the channel argument is
				ignored. This may change in the future.

	Return:		0 if successful, else -1.

	****************************************************************************************************************************/
	int D2PIO_Device_GetMeasurementPeriod(
		Pointer hDevice,//[in] handle to open device.
		byte channel,		//[in] -1 => return 'base' period, else return period for specified channel. Currently ignored.
		DoubleByReference pPeriod,		//[out] ptr to loc to store period in seconds.
		int timeoutMs);	//[in] # of milliseconds to wait for a reply before giving up. D2PIO_TIMEOUT_MS_DEFAULT is recommended.

	/***************************************************************************************************************************
	Function Name: D2PIO_Device_GetMeasurementChannelSensorId()

	Purpose:	Retrieve the sensor id for the specified channel.
				This information is also available from D2PIO_Device_GetMeasurementChannelInfo().

	Return:		0 if successful, else -1.

	****************************************************************************************************************************/
	int D2PIO_Device_GetMeasurementChannelSensorId(
		Pointer hDevice,	//[in] handle to open device.
		byte channel,			//[in]
		IntByReference pSensorId);		//[out] sensor id, which is unique record key for the Sensor Map.

	/***************************************************************************************************************************
	Function Name: D2PIO_Device_GetMeasurementChannelSensorDescription()

	Purpose:	Retrieve the description string for the sensor in the specified channel.
				This information is also available from D2PIO_Device_GetMeasurementChannelInfo().

	Return:		0 if successful, else -1.

	****************************************************************************************************************************/
	int D2PIO_Device_GetMeasurementChannelSensorDescription(
		Pointer hDevice,	//[in] handle to open device.
		byte channel,			//[in]
		Pointer pSensorDescription,//[out] ptr to loc to store NULL terminated UTF-8 string describing sensor.
		int sensorDescriptionLen);//[in] number of bytes in buffer pointed to by pSensorDescription. strlen(pSensorDescription) < sensorDescriptionLen, because the string is NULL terminated.
											//strlen(pSensorDescription)is guaranteed to be < D2PIO_MAX_NUM_BYTES_IN_SENSOR_DESCRIPTION.


	/***************************************************************************************************************************
	Function Name: D2PIO_Device_GetMeasurementChannelSensorUnit()

	Purpose:	Retrieve the unit string for the sensor in the specified channel.
				This information is also available from D2PIO_Device_GetMeasurementChannelInfo().

	Return:		0 if successful, else -1.

	****************************************************************************************************************************/
	int D2PIO_Device_GetMeasurementChannelSensorUnit(
		Pointer hDevice,	//[in] handle to open device.
		byte channel,			//[in]
		Pointer pSensorUnit,		//[out] ptr to loc to store NULL terminated UTF-8 string describing measurement unit.
		int sensorUnitLen);	//[in] number of bytes in buffer pointed to by pSensorUnit. strlen(pSensorUnit) < sensorUnitLen, because the string is NULL terminated.
										//strlen(pSensorUnit) is guaranteed to be < D2PIO_MAX_NUM_BYTES_IN_SENSOR_UNIT.


	/***************************************************************************************************************************
	Function Name: D2PIO_Device_GetMeasurementChannelNumericType()

	Purpose:	Retrieve the numeric measurement type(D2PIO_NUMERIC_MEAS_TYPE_REAL64 or D2PIO_NUMERIC_MEAS_TYPE_INT32)
				for the specified channel.
				This information is also available from D2PIO_Device_GetMeasurementChannelInfo().

				If the numeric measurement type is D2PIO_NUMERIC_MEAS_TYPE_INT32, then call D2PIO_Device_ReadRawMeasurements()
				to retrieve measurements from the D2PIO Measurement Buffer for the specified channel.
				If the numeric measurement type is D2PIO_NUMERIC_MEAS_TYPE_REAL64, then call D2PIO_Device_ReadMeasurements().

	Return:		0 if successful, else -1.

	****************************************************************************************************************************/
	int D2PIO_Device_GetMeasurementChannelNumericType(
		Pointer hDevice,	//[in] handle to open device.
		byte channel,			//[in]
		ByteByReference pNumericMeasType);//[out] numeric measurement type, eg D2PIO_NUMERIC_MEAS_TYPE...

	/***************************************************************************************************************************
	Function Name: D2PIO_Device_ReadMeasurements()

	Purpose:	Retrieve gtype_int32 measurements from the D2PIO Measurement Buffer for a specified channel. The measurements
				reported by this routine are actually removed from the D2PIO Measurement Buffer.

				After D2PIO_CMD_ID_START_MEASUREMENTS has been sent to the device, the device starts
				sending measurements to the host computer. These measurements are stored in the D2PIO Measurement Buffer.
				A separate D2PIO Measurement Buffer is maintained for each channel of the device.

				If D2PIO_Device_GetMeasurementChannelInfo(channel) reports that the NumericMeasType is D2PIO_NUMERIC_MEAS_TYPE_INT32,
				then call D2PIO_Device_ReadRawMeasurements() to retrieve measurements from the D2PIO Measurement Buffer for the
				specified channel. If NumericMeasType is D2PIO_NUMERIC_MEAS_TYPE_REAL64, then call D2PIO_Device_ReadMeasurements().

				See the description of D2PIO_Device_GetNumMeasurementsAvailable().

	Return:		Number of measurements retrieved from the D2PIO Measurement Buffer. This routine
				returns immediately, so the return value may be less than maxCount.

	****************************************************************************************************************************/
	int D2PIO_Device_ReadRawMeasurements( //Returns the number of measurements read
		Pointer hDevice,	//[in] handle to open device.
		byte channel,			//[in]
		int [] pMeasurementsBuf,	//[out] ptr to loc to store measurements.
		long [] pTimeStamps,		//[out] ptr to loc to store 'tick' resolution timestamps. May be NULL.
		int maxCount);	//[in] maximum number of measurements to copy to pMeasurementsBuf.


	/***************************************************************************************************************************
	Function Name: D2PIO_Device_ReadMeasurements()

	Purpose:	Retrieve gtype_real64 measurements from the D2PIO Measurement Buffer for a specified channel. The measurements
				reported by this routine are actually removed from the D2PIO Measurement Buffer.

				After D2PIO_CMD_ID_START_MEASUREMENTS has been sent to the device, the device starts
				sending measurements to the host computer. These measurements are stored in the D2PIO Measurement Buffer.
				A separate D2PIO Measurement Buffer is maintained for each channel of the device.

				If D2PIO_Device_GetMeasurementChannelInfo(channel) reports that the NumericMeasType is D2PIO_NUMERIC_MEAS_TYPE_INT32,
				then call D2PIO_Device_ReadRawMeasurements() to retrieve measurements from the D2PIO Measurement Buffer for the
				specified channel. If NumericMeasType is D2PIO_NUMERIC_MEAS_TYPE_REAL64, then call D2PIO_Device_ReadMeasurements().

				See the description of D2PIO_Device_GetNumMeasurementsAvailable().

	Return:		Number of measurements retrieved from the D2PIO Measurement Buffer. This routine
				returns immediately, so the return value may be less than maxCount.

	****************************************************************************************************************************/
	int D2PIO_Device_ReadMeasurements(
		Pointer hDevice,	//[in] handle to open device.
		byte channel,			//[in]
		double [] pMeasurementsBuf,	//[out] ptr to loc to store measurements.
		long [] pTimeStamps,		//[out] ptr to loc to store 'tick' resolution timestamps. May be NULL.
		int maxCount);			//[in] maximum number of measurements to copy to pMeasurementsBuf.


	/***************************************************************************************************************************
	Function Name: D2PIO_Device_Lock()

	Purpose:	Lock a specified device so that no other thread can access it. This routine will fail if the device
				is currently locked by another thread.

				D2PIO_Device_Lock() increments the lock count for the specified device by 1. In order for a second
				thread to acquire access to the device, the first thread must call D2PIO_Device_Unlock() until the lock
				count reaches 0. Note that the lock count is set to 1 by D2PIO_Device_Open().

				It is ok for the lock count to be greater than 1 when D2PIO_Device_Close() is called.

				This is an advanced routine that most users should not use. It is only necessary if an application
				needs to access a single device from multiple threads.

	Return:		0 if successful, else -1.

	****************************************************************************************************************************/
	int D2PIO_Device_Lock(
		Pointer hDevice,	//[in] handle to open device.
		int timeoutMs);	//[in] wait this long to acquire the lock before failing.

	/***************************************************************************************************************************
	Function Name: D2PIO_Device_Unlock()

	Purpose:	Decrement the lock count for the specified device by 1. This routine will fail if the device
				is not currently locked by the calling thread.

				In order for a second thread to acquire access to the device, the first thread must call
				D2PIO_Device_Unlock() until the lock count reaches 0. Note that the lock count is set to 1 by
				D2PIO_Device_Open().

				If the lock count reaches zero, it is important to make a call to D2PIO_Device_Lock() to lock the
				device. This will prevent multiple threads from simultaneously accessing the device, which can cause
				unpredictable results.

				It is ok for the lock count to be greater than 1 when D2PIO_Device_Close() is called.

				This is an advanced routine that most users should not use. It is only necessary if an application
				needs to access a single device from multiple threads.

	Return:		0 if successful, else -1.

	****************************************************************************************************************************/
	int D2PIO_Device_Unlock(
		Pointer hDevice);//[in] handle to open device.


	/***************************************************************************************************************************
	Function Name: D2PIO_Device_ClearIO()

	Purpose:	Clear the input/output communication buffers for a specified device.
				This also expties the D2PIO Measurement Buffer associated with the specified channel, which holds measurements
				that have been retrieved from the device.

	Return:		0 if successful, else -1.

	****************************************************************************************************************************/
	int D2PIO_Device_ClearIO(
		Pointer hDevice,	//[in] handle to open device.
		byte channel);		//[in] -1 => all channels.


	/***************************************************************************************************************************
	Function Name: D2PIO_Device_GetMeasurementChannelRangeInfo()

	Purpose:	Retrieve the measurement range information for the sensor in the specified channel.

	Return:		0 if successful, else -1.

	****************************************************************************************************************************/
	int D2PIO_Device_GetMeasurementChannelRangeInfo(
		Pointer hDevice,	//[in] handle to open device.
		byte channel,			//[in]
		DoubleByReference pMeasurementUncertainty, //[out] uncertainty of measurement expressed in SensorUnit's.
		DoubleByReference pMinMeasurement,	//[out] minimum measurement expressed in SensorUnit's.
		DoubleByReference pMaxMeasurement);	//[out] maximum measurement expressed in SensorUnit's.

	/***************************************************************************************************************************
	Function Name: D2PIO_Device_GetMeasurementChannelPeriodInfo()

	Purpose:	Retrieve the measurement period range information for the sensor in the specified channel.
				This information is only valid for sensors whose sampling mode is D2PIO_SAMPLING_MODE_PERIODIC.

	Return:		0 if successful, else -1.

	****************************************************************************************************************************/
	int D2PIO_Device_GetMeasurementChannelPeriodInfo(
		Pointer hDevice,	//[in] handle to open device.
		byte channel,			//[in]
		DoubleByReference pMinMeasurementPeriod,//[out] minimum measurement period in seconds
		DoubleByReference pMaxMeasurementPeriod,//[out] maximum measurement period in seconds
		DoubleByReference pTypMeasurementPeriod,//[out] typical measurement period in seconds
		DoubleByReference pMeasurementPeriodGranularity);//[out] in seconds, supported periods are integer multiples of measurementPeriodGranularity.

}


package org.concord.sensor.d2pio.jna;

import com.sun.jna.Library;
import com.sun.jna.Pointer;
import com.sun.jna.Structure;
import com.sun.jna.ptr.ByteByReference;
import com.sun.jna.ptr.IntByReference;
import com.sun.jna.ptr.ShortByReference;

public interface D2PIOJNALibrary extends Library {

	public final static int D2PIO_DEVTYPE_GENERIC = 30;
	public final static int D2PIO_COMM_TRANSPORT_USB = 1;
	public final static int D2PIO_MAX_SIZE_DEVICE_NAME = 220;

	public final static int D2PIO_DEVICE_OPEN_STATUS_ERROR = 1;
	public final static int D2PIO_DEVICE_OPEN_STATUS_IN_PROGRESS = 2;
	public final static int D2PIO_DEVICE_OPEN_STATUS_TIMEOUT = 3;
	public final static int D2PIO_DEVICE_OPEN_STATUS_SUCCESS = 4;
	public final static int D2PIO_DEVICE_OPEN_STATUS_CLOSED = 5;
	
	public final static int D2PIO_USB_OPEN_TIMEOUT_MS = 3000;

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

}


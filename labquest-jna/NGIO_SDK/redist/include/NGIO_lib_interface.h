#ifndef _NGIO_LIB_INTERFACE_H_
#define _NGIO_LIB_INTERFACE_H_

/***************************************************************************************************************************
	NGIO_lib_interface.h version 1.022

	This file documents the 'C' interface to the NGIO library.

	This library is implemented as NGIO_lib.dll on Windows and as libNGIO.dylib on the Mac.
	
***************************************************************************************************************************/
#ifdef TARGET_OS_LINUX
#ifdef __cplusplus
	#define NGIO_LIB_INTERFACE_DECL extern "C" __attribute__ ((visibility("default")))
#else
	#define NGIO_LIB_INTERFACE_DECL __attribute__ ((visibility("default")))
#endif
#elif TARGET_OS_MAC
#ifdef __cplusplus
	#define NGIO_LIB_INTERFACE_DECL extern "C"
#else
	#define NGIO_LIB_INTERFACE_DECL
#endif
#else
#ifdef __cplusplus
	#define NGIO_LIB_INTERFACE_DECL extern "C"
#else
	#define NGIO_LIB_INTERFACE_DECL
#endif
#endif

#include "NGIOSourceCmds.h"
#include "GSensorDDSMem.h"
#include "GVernierUSB.h"

#ifndef GTYPE_NUMS
#define GTYPE_NUMS
typedef short gtype_int16;
typedef unsigned short gtype_uint16;
#ifdef TARGET_OS_DAQ
typedef long int gtype_int32;
typedef unsigned long int gtype_uint32;
#else
typedef int gtype_int32;
typedef unsigned int gtype_uint32;
#endif
#ifdef TARGET_OS_WIN
typedef __int64 gtype_int64;
typedef unsigned __int64 gtype_uint64;
#else
typedef long long gtype_int64;
typedef unsigned long long gtype_uint64;
#endif
typedef unsigned char gtype_bool;
typedef double gtype_real64;
typedef float gtype_real32;
#endif


#ifdef TARGET_OS_MAC
	#define NGIO_MAX_SIZE_DEVICE_NAME 220
#endif

#if defined (TARGET_OS_WIN) || defined (TARGET_OS_LINUX)
	#define NGIO_MAX_SIZE_DEVICE_NAME 220
#endif

typedef void *NGIO_LIBRARY_HANDLE;
typedef void *NGIO_DEVICE_HANDLE;
typedef void *NGIO_DEVICE_LIST_HANDLE;
typedef void *NGIO_PTR;

//List of known communication transport types.
//Note that this list may grow. Call NGIO_GetNthAvailableCommTransport() to determine complete list at runtime.
#define NGIO_COMM_TRANSPORT_USB 1
#define NGIO_COMM_TRANSPORT_SERIAL 2
#define NGIO_COMM_TRANSPORT_BLUETOOTH 3
#define NGIO_COMM_TRANSPORT_BLUETOOTH_ALT1 4
#define NGIO_COMM_TRANSPORT_WIRELESS_80211_X 5
#define NGIO_COMM_TRANSPORT_HARDWIRE_LAN 6

#define NGIO_DEVTYPE_LABPRO 1
#define NGIO_DEVTYPE_GO_TEMP 2
#define NGIO_DEVTYPE_GO_LINK 3
#define NGIO_DEVTYPE_GO_MOTION 4
#define NGIO_DEVTYPE_LABPRO2 5
#define NGIO_DEVTYPE_WDSS 6
#define NGIO_DEVTYPE_NI_SENSORDAQ 7
#define NGIO_DEVTYPE_LABPRO2_FILE_SERVER 8
#define NGIO_DEVTYPE_LABPRO2_AUDIO 9
#define NGIO_DEVTYPE_DAISYCHAIN_TEST 10

#define NGIO_TCPIP_PORT_LABPRO2 0x9500

#define NGIO_TIMEOUT_MS_DEFAULT 2000
#define NGIO_TIMEOUT_MS_READ_DDSMEMBLOCK 2000
#define NGIO_TIMEOUT_MS_WRITE_DDSMEMBLOCK 4000

/***************************************************************************************************************************
	Function Name: NGIO_Init()
	
	Purpose:	Call NGIO_Init() once before making any other NGIO function calls.
				NGIO_Init() and NGIO_Uninit() should be called from the same thread.

				In principle, several different applications should be able to use the NGIO library to access
				separate LabQuests concurrently. This is our design goal. However, this has not been tested much yet.

	Return:		0 iff successful, else -1.

****************************************************************************************************************************/
NGIO_LIB_INTERFACE_DECL NGIO_LIBRARY_HANDLE NGIO_Init();

/***************************************************************************************************************************
	Function Name: NGIO_Uninit()
	
	Purpose:	Call NGIO_Uninit() once to 'undo' NGIO_Init().
				NGIO_Init() and NGIO_Uninit() should be called from the same thread.

	Return:		0 iff successful, else -1.

****************************************************************************************************************************/
NGIO_LIB_INTERFACE_DECL gtype_int32 NGIO_Uninit(NGIO_LIBRARY_HANDLE hLib);

/***************************************************************************************************************************
	Function Name: NGIO_GetDLLVersion()
	
	Purpose:	This routine returns the major and minor version numbers for the instance of the NGIO library that is
				currently running.

				If a function is not guaranteed to be present in all supported versions of the NGIO library, then the line
				"Added in version 'major.minor'" will appear in the function description in this file.

				It is our intention that all versions of the NGIO library created subsequent to a given version, will be
				backwards compatible with the older version. You should be able to replace an old version of the NGIO library
				with a newer version and everything should still work without rebuilding your application.

				Note that version major2.minor2 is later than version major1.minor1 
				iff. ((major2 > major1) || ((major2 == major1) && (minor2 > minor1))).
				
				Backwards compatibility is definitely our intention, but we do not absolutely guarantee it. If you think
				that you have detected a backwards compatibility bug, then please report it to Vernier Software & Technology.
				Calling NGIO_GetDLLVersion() from your application is a way to identify precisely which version of
				the NGIO library you are actually using.

	Return:		0 iff successful, else -1.

****************************************************************************************************************************/
NGIO_LIB_INTERFACE_DECL gtype_int32 NGIO_GetDLLVersion(
	NGIO_LIBRARY_HANDLE hLib,	//[in] handle returned by NGIO_Init()
	gtype_uint16 *pMajorVersion, //[out]
	gtype_uint16 *pMinorVersion); //[out]

/***************************************************************************************************************************
	Function Name: NGIO_GetNthAvailableCommTransport()
	
	Purpose:	Return the N'th supported communication transport id and description string.
				This function has been defined so that an application can access transports that are added after the app is
				written.
				This function is not currently implemented. Look for NGIO_COMM_TRANSPORT_... constants to see what the known
				transports are.

	Return:		0 iff successful, else -1.

****************************************************************************************************************************/
NGIO_LIB_INTERFACE_DECL gtype_int32 NGIO_GetNthAvailableCommTransport(
	NGIO_LIBRARY_HANDLE hLib,	//[in] handle returned by NGIO_Init()
	gtype_uint32 N,		//[in] index into list of supported transports.
	gtype_uint32 *pCommTransportId,	//[out] ptr to loc to store NGIO_COMM_TRANSPORT_... id.
	char *pDescBuf,		//[out] ptr to buffer to store transport description string.
	gtype_uint32 bufSize);//[in] number of bytes in buffer pointed to by pDescBuf. Strlen(pBuf) < bufSize, because the string is NULL terminated.

/***************************************************************************************************************************
	Function Name: NGIO_EnableAutoDeviceDiscoveryAcrossCommTransport()
	
	Purpose:	Eventually, calling this function will cause the library to automatically search for new devices attached to
				system via the specified comm transport.

				This function is not implemented yet. Use NGIO_SearchForDevices() instead.

	Return:		0 iff successful, else -1.

****************************************************************************************************************************/
NGIO_LIB_INTERFACE_DECL gtype_int32 NGIO_EnableAutoDeviceDiscoveryAcrossCommTransport(
	NGIO_LIBRARY_HANDLE hLib,	//[in] handle returned by NGIO_Init()
	gtype_uint32 deviceType,		//[in] NGIO_DEVTYPE_...
	gtype_uint32 commTransportId);	//[in] NGIO_COMM_TRANSPORT_...

/***************************************************************************************************************************
	Function Name: NGIO_DisableAutoDeviceDiscoveryAcrossCommTransport()
	
	Purpose:	Eventually, calling this function will cause the library to stop automatically searching for new devices 
				attached to system via the specified comm transport.

				This function is not implemented yet.

	Return:		0 iff successful, else -1.

****************************************************************************************************************************/
NGIO_LIB_INTERFACE_DECL gtype_int32 NGIO_DisableAutoDeviceDiscoveryAcrossCommTransport(
	NGIO_LIBRARY_HANDLE hLib,		//[in] handle returned by NGIO_Init()
	gtype_uint32 deviceType,		//[in] NGIO_DEVTYPE_...
	gtype_uint32 commTransportId);	//[in] NGIO_COMM_TRANSPORT_...

/***************************************************************************************************************************
	Function Name: NGIO_IsAutoDeviceDiscoveryEnabledAcrossCommTransport()
	
	Purpose:	Eventually, calling this function will indicate if the library is looking for devices on the specified comm
				comm transport.

				This function is not implemented yet.

	Return:		1 if device discovery is enabled, else 0

****************************************************************************************************************************/
NGIO_LIB_INTERFACE_DECL gtype_bool NGIO_IsAutoDeviceDiscoveryEnabledAcrossCommTransport(
	NGIO_LIBRARY_HANDLE hLib,		//[in] handle returned by NGIO_Init()
	gtype_uint32 deviceType,		//[in] NGIO_DEVTYPE_...
	gtype_uint32 commTransportId);	//[in] NGIO_COMM_TRANSPORT_...

/***************************************************************************************************************************
	Function Name: NGIO_SearchForDevices()
	
	Purpose:	The NGIO library maintains a separate list of available devices for each supported device type.
				NGIO_SearchForDevices() updates the list for the specified device type.

				For each device list a signature is maintained that is incremented every time the list changes. The signature
				is implemented as a simple 32 bit counter that wraps from 0xffffffff to 0.
				
				Let int delta = sig2 - sig1. 
				If delta > 0, then sig2 is newer than sig1.
				If delta < 0, then sig2 is older than sig1.

	Return:		0 iff successful, else -1.

****************************************************************************************************************************/
NGIO_LIB_INTERFACE_DECL gtype_int32 NGIO_SearchForDevices(
	NGIO_LIBRARY_HANDLE hLib,		//[in] handle returned by NGIO_Init()
	gtype_uint32 deviceType,		//[in] NGIO_DEVTYPE_...
	gtype_uint32 commTransportId,	//[in] NGIO_COMM_TRANSPORT_...
	NGIO_PTR pParams,				//[in] ptr to context specific search params. Not currently used.
	gtype_uint32 *pDeviceListSignature);//[out] ptr to device list signature. Signatures are not fully implemented yet.

/***************************************************************************************************************************
	Function Name: NGIO_OpenDeviceListSnapshot()
	
	Purpose:	The NGIO library maintains a separate list of available devices for each supported device type.
				NGIO_SearchForDevices() updates the list for the specified device type.
				Eventually, when NGIO_EnableAutoDeviceDiscoveryAcrossCommTransport() is fully implemented, device lists
				will be fairly volatile. Until that happens, the only way to update a device list is to call
				NGIO_SearchForDevices(). NGIO_OpenDeviceListSnapshot() creates a snapshot copy of the device list for
				the specified device type. Use the handle returned by NGIO_OpenDeviceListSnapshot() to walk the device
				list.

				A device is placed in the list snapshot even if it has already been opened.

				For each device list a signature is maintained that is incremented every time the list changes. The signature
				is implemented as a simple 32 bit counter that wraps from 0xffffffff to 0.
				
				Let int delta = sig2 - sig1. 
				If delta > 0, then sig2 is newer than sig1.
				If delta < 0, then sig2 is older than sig1.

	Return:		handle to device list snapshot iff successful, else NULL.

****************************************************************************************************************************/
NGIO_LIB_INTERFACE_DECL NGIO_DEVICE_LIST_HANDLE NGIO_OpenDeviceListSnapshot(
	NGIO_LIBRARY_HANDLE hLib,			//[in] handle returned by NGIO_Init()
	gtype_uint32 deviceType,			//[in] NGIO_DEVTYPE_...
	gtype_uint32 *pNumDevices,			//[out] ptr to storage loc for the number of devices in the list
	gtype_uint32 *pDeviceListSignature);//[out] ptr to device list signature. Signatures are not fully implemented yet.

/***************************************************************************************************************************
	Function Name: NGIO_CloseDeviceListSnapshot()
	
	Purpose:	Close the list created by NGIO_OpenDeviceListSnapshot().

	Return:		0 iff successful, else -1.

****************************************************************************************************************************/
NGIO_LIB_INTERFACE_DECL gtype_int32 NGIO_CloseDeviceListSnapshot(NGIO_DEVICE_LIST_HANDLE hDeviceList);

#define NGIO_DEVICE_STATUS_MASK_OPEN 1
#define NGIO_DEVICE_STATUS_MASK_OPENED_BY_THIS_CLIENT 2

/***************************************************************************************************************************
	Function Name: NGIO_DeviceListSnapshot_GetNthEntry()
	
	Purpose:	Return the Nth entry in the list created by NGIO_OpenDeviceListSnapshot().
				A device is placed in the list snapshot even if it has already been opened.
				Eventually, the bits returned in *pDeviceStatusMask will tell you if the device is already open,
				but these status bits are not supported yet.

				Pass the device name string placed in *pDevnameBuf to NGIO_Device_Open() to open the device. Each
				device name string uniquely identifies the device, so you can determine if a device is already open
				by comparing *pDevnameBuf with the string returned by NGIO_Device_GetOpenDeviceName() for each open
				device handle.

	Return:		0 iff successful, else -1.

****************************************************************************************************************************/
NGIO_LIB_INTERFACE_DECL gtype_int32 NGIO_DeviceListSnapshot_GetNthEntry(
	NGIO_DEVICE_LIST_HANDLE hDeviceList,	//[in] handle returned by NGIO_OpenDeviceListSnapshot()
	gtype_uint32 N,		//[in] index into list of known devices, 0 => first device in list.
	char *pDevnameBuf,	//[out] ptr to buffer to store device name string.
	gtype_uint32 bufSize,//[in] number of bytes in buffer pointed to by pDevnameBuf. Strlen(pDevnameBuf) < bufSize, because the string is NULL terminated.
						//Strlen(pDevnameBuf)is guaranteed to be < NGIO_MAX_SIZE_DEVICE_NAME.
	gtype_uint32 *pDeviceStatusMask);	//[out] NGIO_DEVICE_STATUS_MASK_... Not implemented yet.

/***************************************************************************************************************************
	Function Name: NGIO_GetParentDeviceHandleFromChildName()
	
	Purpose:	Given a child device name, return the handle to the parent device. If the specified device has no
				parent, then return NULL.

				Some devices are regarded as children of other devices. When a LabQuest device is opened on a desktop
				computer, the audio and FTL devices associated with the LabQuest are regarded as children of the LabQuest
				device.

	Return:		0 iff successful, else -1.

****************************************************************************************************************************/
NGIO_LIB_INTERFACE_DECL gtype_int32 NGIO_GetParentDeviceHandleFromChildName(
	const char *pDeviceName,	//[in] NULL terminated string that uniquely identifies the device. See NGIO_GetNthAvailableDeviceName().
	NGIO_DEVICE_HANDLE *parentDeviceHandle);//[out] loc to store handle to parent device if one exists, else NULL.

/***************************************************************************************************************************
	Function Name: NGIO_GetDeviceTypeFromDeviceName()
	
	Purpose:	Given a device name(from NGIO_GetNthAvailableDeviceName()), return the NGIO_DEVTYPE_ device type.

	Return:		0 iff successful, else -1.

****************************************************************************************************************************/
NGIO_LIB_INTERFACE_DECL gtype_int32 NGIO_GetDeviceTypeFromDeviceName(
	const char *pDeviceName,	//[in] NULL terminated string that uniquely identifies the device. See NGIO_GetNthAvailableDeviceName().
	gtype_uint32 *pDeviceType);	//[out] loc to store NGIO_DEVTYPE_...

typedef gtype_int32 (*P_NGIO_DEVICELISTNOTIFICATION_CALLBACK)
(
	NGIO_PTR pNotification,
	gtype_uint32 numBytes,
	gtype_uint32 deviceListSignature,
	NGIO_PTR pContextInfo
);

/***************************************************************************************************************************
	Function Name: NGIO_RegisterCallbackForDeviceListNotifications()
	
	Purpose:	Register a callback function which we be called when a device list changes.
				Eventually, device lists will change when NGIO_EnableAutoDeviceDiscoveryAcrossCommTransport() is implemented.
				NGIO_RegisterCallbackForDeviceListNotifications() is not implemented yet.

	Return:		0 iff successful, else -1.

****************************************************************************************************************************/
NGIO_LIB_INTERFACE_DECL gtype_int32 NGIO_RegisterCallbackForDeviceListNotifications(
	NGIO_LIBRARY_HANDLE hLib,
	P_NGIO_DEVICELISTNOTIFICATION_CALLBACK notificationCallbackFunc,
	NGIO_PTR pContextInfo);

NGIO_LIB_INTERFACE_DECL gtype_int32 NGIO_DeregisterCallbackForDeviceListNotifications(
	NGIO_LIBRARY_HANDLE hLib,
	P_NGIO_DEVICELISTNOTIFICATION_CALLBACK notificationCallbackFunc);

/***************************************************************************************************************************
	Function Name: NGIO_Device_Open()
	
	Purpose:	Open a device with the name returned by NGIO_DeviceListSnapshot_GetNthEntry.
				Note that you must almost always call NGIO_Device_AcquireExclusiveOwnership() immediately after calling
				NGIO_Device_Open(deviceName) if deviceName corresponds to a basic LabQuest device.

				NGIO_Device_Open() currently opens devices of the types NGIO_DEVTYPE_LABPRO2, 
				NGIO_DEVTYPE_LABPRO2_FILE_SERVER, and NGIO_DEVTYPE_LABPRO2_AUDIO.
				NGIO_DEVTYPE_LABPRO2 refers to the basic LabQuest device. Standard Vernier analog and digital sensors
				are accessed via the NGIO_DEVTYPE_LABPRO2 device. The NGIO_DEVTYPE_LABPRO2_AUDIO device is used to access
				the audio devices(internal and external) that are built in to the LabQuest. The NGIO_DEVTYPE_LABPRO2_FILE_SERVER
				device is a special device that provides the caller with access to the Linux file system that is internal to
				the LabQuest.

				For each open NGIO_DEVTYPE_LABPRO2 device, there is a corresponding NGIO_DEVTYPE_LABPRO2_FILE_SERVER and
				NGIO_DEVTYPE_LABPRO2_AUDIO device that is a logical 'child' of the NGIO_DEVTYPE_LABPRO2 device.
				NGIO_SearchForDevices() will not find any NGIO_DEVTYPE_LABPRO2_FILE_SERVER or NGIO_DEVTYPE_LABPRO2_AUDIO
				devices until at least one parent NGIO_DEVTYPE_LABPRO2 device has been opened.

				Each device type has a command protocol that is unique to that device type. The command protocol used by
				NGIO_DEVTYPE_LABPRO2_AUDIO devices is a subset of the protocol used by NGIO_DEVTYPE_LABPRO2 devices. This
				protocol is documented in NGIOSourceCmds.h .

				The command protocol used by NGIO_DEVTYPE_LABPRO2_FILE_SERVER devices is documented in NGIOFTLSourceCmds.h 
				
				The following discussion generally only applies to NGIO_DEVTYPE_LABPRO2 devices. The behaviour of the 
				NGIO_DEVTYPE_LABPRO2_AUDIO devices types is similar, but not always identical. NGIO_DEVTYPE_LABPRO2_FILE_SERVER
				devices are very different. The one thing that all device types share is support for 
				NGIO_Device_SendCmdAndGetResponse().

				After establishing basic communication, NGIO_Device_Open() sends a NGIO_CMD_ID_INIT command to the 
				NGIO_DEVTYPE_LABPRO2 device. In response to NGIO_CMD_ID_INIT, all measurement buffers are cleared and each 
				channel is configured with default settings. For NGIO_CHANNEL_ID_ANALOG1 .. NGIO_CHANNEL_ID_ANALOG4,
				sampling mode defaults to NGIO_SAMPLING_MODE_PERIODIC_LEVEL_SNAPSHOT, and analog input defaults to 
				NGIO_ANALOG_INPUT_5V_BUILTIN_12BIT_ADC. For NGIO_CHANNEL_ID_DIGITAL1 and NGIO_CHANNEL_ID_DIGITAL2,
				the sampling mode defaults to NGIO_SAMPLING_MODE_APERIODIC_EDGE_DETECT. No measurements will be taken until
				a NGIO_CMD_ID_START_MEASUREMENTS command is sent to the device.

				Currently, NGIO_Device_Open() does not automatically detect what kinds of sensors are plugged in and it does
				not automatically read the DDS memory on the sensor. The application program has to do this manually.

				After calling NGIO_Device_Open(), an application needs to perform the following operations to take 
				measurements:

				For each channel:
					-	Send a NGIO_CMD_ID_GET_SENSOR_ID command to identify type of sensor connected on a specified channel.
					-	If the sensor is smart(id >= kSensorIdNumber_FirstSmartSensor) then call 
							NGIO_Device_DDSMem_ReadRecord() to read the DDS memory from the sensor.
					-	If the channel is analog, then set the analog input to NGIO_ANALOG_INPUT_5V_BUILTIN_12BIT_ADC
							for most sensors, and set analog input to NGIO_ANALOG_INPUT_PM10V_BUILTIN_12BIT_ADC for
							=/- 10 volt sensors. 
							Note that analog sensors default to NGIO_ANALOG_INPUT_5V_BUILTIN_12BIT_ADC and
							NGIO_SAMPLING_MODE_PERIODIC_LEVEL_SNAPSHOT.
					-	If the channel is digital, then set the sampling mode to a mode appropriate for the connected sensor.
					-	Call NGIO_Device_SetMeasurementPeriod(channel) to set the sampling period used for periodic measurements.
							(channel == -1) => set the sampling period for all channels.

				-	Send a NGIO_CMD_ID_SET_SENSOR_CHANNEL_ENABLE_MASK command to define which channels will take measurements in
						the next collection.
				-	Send a NGIO_CMD_ID_START_MEASUREMENTS command to start taking measurements.
				-	Call NGIO_Device_ReadRawMeasurements() to get raw binary measurements. Call NGIO_Device_ConvertToVoltage()
						and NGIO_Device_CalibrateData() to convert raw measurements to cooked values. NGIO library maintains a large
						(>= 10000 measurements) circular measurement buffer for each channel. If a collection exceeds 10000
						measurements, then the application needs to pull the measurements out of the buffer by calling
						NGIO_Device_ReadRawMeasurements() before the buffer fills.
				-	Send a NGIO_CMD_ID_STOP_MEASUREMENTS command to stop taking measurements. Once NGIO_CMD_ID_STOP_MEASUREMENTS
						has been sent, the app can take its time about calling NGIO_Device_ReadRawMeasurements() to pull measurements
						out of the measurement buffers. However, the app must empty the measurement buffers before sending
						NGIO_CMD_ID_START_MEASUREMENTS again.

				At any given time, a device is 'owned' by only one thread. The thread that calls this routine is the
				initial owner of the device. If an NGIO() call is made from a thread that does not own the device object
				that is passed in, then the call will generally fail. To allow another thread to access a device,
				the owning thread must call NGIO_Device_Unlock(), and then the new thread should call NGIO_Device_Lock().

				We currently have some additional restrictions on what threads can open and close a LabQuest device.
				A LabQuest(NGIO_DEVTYPE_LABPRO2) device must be closed on the same thread that initially opened it.
				Furthermore, the audio and file server child devices of that LabQuest must be opened and closed on the same 
				thread that initially opened the LabQuest. Once opened, you can transfer ownership of the devices to other 
				threads and operate on the devices. However, before you close the devices, you must transfer ownership back to 
				the single thread that the LabQuest and all its children were opened on. Eventually, we plan to relax these
				restrictions, but for now, that is the way things are.
  
	Return:		handle to open device if successful, else NULL.

****************************************************************************************************************************/
NGIO_LIB_INTERFACE_DECL NGIO_DEVICE_HANDLE NGIO_Device_Open(
	NGIO_LIBRARY_HANDLE hLib,
	const char *pDeviceName,	//[in] NULL terminated string that uniquely identifies the device. See NGIO_GetNthAvailableDeviceName().
	gtype_bool bDemandExclusiveOwnership);	//[in] 1 => demand exclusive control of the DAQ, except that this does not work.
								//Please set bDemandExclusiveOwnership = 0.
								//To wrest control of the LabQuest data acquistion processor(the DAQ) away from the
								//LabQuest data collection app that runs on the LabQuest, you must call
								//NGIO_Device_AcquireExclusiveOwnership() after calling NGIO_Device_Open(LabQuestName, 0);

/***************************************************************************************************************************
	Function Name: NGIO_Device_Close()
	
	Purpose:	Close a specified device. After this routine runs, the hDevice handle is no longer valid.
	
				At any given time, a device is 'owned' by only one thread. The thread that called NGIO_Device_Open() is the
				initial owner of the device. If an NGIO() call is made from a thread that does not own the device object
				that is passed in, then the call will generally fail. To allow another thread to access a device,
				the owning thread must call NGIO_Device_Unlock(), and then the new thread should call NGIO_Device_Lock().

				We currently have some additional restrictions on what threads can open and close a LabQuest device.
				A LabQuest(NGIO_DEVTYPE_LABPRO2) device must be closed on the same thread that initially opened it.
				Furthermore, the audio and file server child devices of that LabQuest must be opened and closed on the same 
				thread that initially opened the LabQuest. Once opened, you can transfer ownership of the devices to other 
				threads and operate on the devices. However, before you close the devices, you must transfer ownership back to 
				the single thread that the LabQuest and all its children were opened on. Eventually, we plan to relax these
				restrictions, but for now, that is the way things are.

	Return:		0 if successful, else -1.

****************************************************************************************************************************/
NGIO_LIB_INTERFACE_DECL gtype_int32 NGIO_Device_Close(
	NGIO_DEVICE_HANDLE hDevice);//[in] handle to open device.

/***************************************************************************************************************************
	Function Name: NGIO_Device_IsRemoteCollectionActive()
	
	Purpose:	Query a LabQuest to see if its built in data collection application is currently collecting data.
				If so, then you may not want to call NGIO_Device_AcquireExclusiveOwnership() because that will shut down
				the collection that was in progress.
	
	Return:		0 if successful, else -1.

****************************************************************************************************************************/
NGIO_LIB_INTERFACE_DECL gtype_int32 NGIO_Device_IsRemoteCollectionActive(
	NGIO_DEVICE_HANDLE hDevice,	//[in] handle to open device.
	gtype_bool *pRemoteCollectionActiveFlag,	//[out] ptr to location to store flag.
	gtype_uint32 timeoutMs);	//NGIO_TIMEOUT_MS_DEFAULT is recommended.

/***************************************************************************************************************************
	Function Name: NGIO_Device_AcquireExclusiveOwnership()
	
	Purpose:	Tell the LabQuest's built in data collection app to stop collecting data and then grab exclusive ownership
				of LabQuest's data acquisition facilities(the DAQ).
	
	Return:		0 if successful, else -1.

****************************************************************************************************************************/
//NGIO_GRAB_DAQ_TIMEOUT is so long because the LabQuest needs to save data when it shuts down remote collection.
#define NGIO_GRAB_DAQ_TIMEOUT 12000
NGIO_LIB_INTERFACE_DECL gtype_int32 NGIO_Device_AcquireExclusiveOwnership(
	NGIO_DEVICE_HANDLE hDevice,	//[in] handle to open device.
	gtype_uint32 timeoutMs);	//NGIO_GRAB_DAQ_TIMEOUT is recommended.

/***************************************************************************************************************************
	Function Name: NGIO_IsDeviceExclusivelyOurs()
	
	Purpose:	Find out if we currently have exclusive access to the LabQuest's data acquisition facilities. To get
				exclusive access, call NGIO_Device_AcquireExclusiveOwnership().
	
	Return:		0 if successful, else -1.

****************************************************************************************************************************/
NGIO_LIB_INTERFACE_DECL gtype_int32 NGIO_IsDeviceExclusivelyOurs(
	NGIO_DEVICE_HANDLE hDevice,	//[in] handle to open device.
	gtype_bool *pExclusiveOwnershipFlag);	//[out] ptr to location to store flag.

//We will also define another API that allows the user to register a callback function that is invoked when sensors are
//plugged and unplugged. spam

/***************************************************************************************************************************
	Function Name: NGIO_Device_GetOpenDeviceName()
	
	Purpose:	Get the unique device name string for a specified open device. This is the string returned by
				NGIO_DeviceListSnapshot_GetNthEntry() and passed in to NGIO_Device_Open().


	Return:		0 iff successful, else -1.

****************************************************************************************************************************/
NGIO_LIB_INTERFACE_DECL gtype_int32 NGIO_Device_GetOpenDeviceName(
	NGIO_DEVICE_HANDLE hDevice,	//[in] handle to open device.
	char *pDeviceName,			//[out] ptr to buffer to store device name string.
	gtype_uint32 bufSize);	//[in] number of bytes in buffer pointed to by pDeviceName. Strlen(pDeviceName) < bufSize, because the string is NULL terminated.

/***************************************************************************************************************************
	Function Name: NGIO_Device_Lock()
	
	Purpose:	Lock a specified device so that no other thread can access it. This routine will fail if the device
				is currently locked by another thread.

				NGIO_Device_Lock() increments the lock count for the specified device by 1. In order for a second
				thread to acquire access to the device, the first thread must call NGIO_Device_Unlock() until the lock
				count reaches 0. Note that the lock count is set to 1 by NGIO_Device_Open().
				
				It is ok for the lock count to be greater than 1 when NGIO_Device_Close() is called.

				This is an advanced routine that most users should not use. It is only necessary if an application
				needs to access a single device from multiple threads.
	
	Return:		0 if successful, else -1.

****************************************************************************************************************************/
NGIO_LIB_INTERFACE_DECL gtype_int32 NGIO_Device_Lock(
	NGIO_DEVICE_HANDLE hDevice,	//[in] handle to open device.
	gtype_uint32 timeoutMs);	//[in] wait this long to acquire the lock before failing.

/***************************************************************************************************************************
	Function Name: NGIO_Device_Unlock()
	
	Purpose:	Decrement the lock count for the specified device by 1. This routine will fail if the device
				is not currently locked by the calling thread.

				In order for a second thread to acquire access to the device, the first thread must call 
				NGIO_Device_Unlock() until the lock count reaches 0. Note that the lock count is set to 1 by 
				NGIO_Device_Open().

				If the lock count reaches zero, it is important to make a call to NGIO_Device_Lock() to lock the
				device. This will prevent multiple threads from simultaneously accessing the device, which can cause
				unpredictable results.
				
				It is ok for the lock count to be greater than 1 when NGIO_Device_Close() is called.

				This is an advanced routine that most users should not use. It is only necessary if an application
				needs to access a single device from multiple threads.
	
	Return:		0 if successful, else -1.

****************************************************************************************************************************/
NGIO_LIB_INTERFACE_DECL gtype_int32 NGIO_Device_Unlock(
	NGIO_DEVICE_HANDLE hDevice);//[in] handle to open device.

/***************************************************************************************************************************
	Function Name: NGIO_Device_ClearIO()
	
	Purpose:	Clear the input/output communication buffers for a specified device. 
				This also expties the NGIO Measurement Buffer associated with the specified channel, which holds measurements 
				that have been retrieved from the device.
	
	Return:		0 if successful, else -1.

****************************************************************************************************************************/
NGIO_LIB_INTERFACE_DECL gtype_int32 NGIO_Device_ClearIO(
	NGIO_DEVICE_HANDLE hDevice,	//[in] handle to open device.
	signed char channel);		//[in] -1 => all channels.

/***************************************************************************************************************************
	Function Name: NGIO_Device_SendCmdAndGetResponse()
	
	Purpose:	Send a command to the specified device hardware and wait for a response. 

				Each device type has a command protocol that is unique to that device type. The command protocol used by
				NGIO_DEVTYPE_LABPRO2_AUDIO devices is a subset of the protocol used by NGIO_DEVTYPE_LABPRO2 devices. This
				protocol is documented in NGIOSourceCmds.h .

				The command protocol used by NGIO_DEVTYPE_LABPRO2_FILE_SERVER devices is documented in NGIOFTLSourceCmds.h 

				Note that NGIO_Device_SendCmdAndGetResponse() will fail if you send a NGIO_CMD_ID_START_MEASUREMENTS
				command while NGIO_Device_GetNumMeasurementsAvailable() says measurements are available. 

				So...if you are restarting measurements, you should clear any old measurements in the NGIO Measurement 
				Buffer first by reading in the measurements until the Buffer is empty, or you should call 
				NGIO_Device_ClearIO().
	
	Return:		0 if successful, else -1.

****************************************************************************************************************************/
NGIO_LIB_INTERFACE_DECL gtype_int32 NGIO_Device_SendCmdAndGetResponse(
	NGIO_DEVICE_HANDLE hDevice,	//[in] handle to open device.
	unsigned char cmd,			//[in] command code.
	NGIO_PTR pParams,			//[in] ptr to cmd specific parameter block, may be NULL. See GSkipCommExt.h.
	gtype_uint32 nParamBytes,	//[in] # of bytes in (*pParams).
	NGIO_PTR pRespBuf,			//[out] ptr to destination buffer, may be NULL.
	gtype_uint32 *pnRespBytes,	//[in, out] ptr to size of of pRespBuf buffer on input, size of response on output, may be NULL if pRespBuf is NULL.
	gtype_uint32 timeoutMs);	//[in] # of milliseconds to wait for a reply before giving up. Most devices should reply to almost all the 
							//currently defined commands within NGIO_TIMEOUT_MS_DEFAULT milliseconds. In fact, typical response
							//times are less than 50 milliseconds. See NGIO_TIMEOUT_MS_* definitions.

/***************************************************************************************************************************
	Function Name: NGIO_Device_SendCmd()
	
	Purpose:	NGIO_Device_SendCmd() is an advanced function. You should usually use 
				NGIO_Device_SendCmdAndGetResponse() instead. After calling NGIO_Device_SendCmd(), you must call
				NGIO_Device_GetNextResponse() before sending any more commands to the device.

				The main reason that NGIO_Device_SendCmd() is made available to the user is to allow a program to send
				NGIO_CMD_ID_START_MEASUREMENTS commands to several different devices as close together as possible so that
				measurements start at about the same time on separate devices.
	
	Return:		0 if successful, else -1.

****************************************************************************************************************************/
NGIO_LIB_INTERFACE_DECL gtype_int32 NGIO_Device_SendCmd(
	NGIO_DEVICE_HANDLE hDevice,	//[in] handle to open device.
	unsigned char cmd,	//[in] command code
	NGIO_PTR pParams,	//[in] ptr to cmd specific parameter block, may be NULL.
	gtype_uint32 nParamBytes,//[in] # of bytes in (*pParams).
	gtype_uint32 *pSignature);//[out] if not NULL, then put command signature emebedded in cmd blob here.

/***************************************************************************************************************************
	Function Name: NGIO_Device_GetNextResponse()
	
	Purpose:	NGIO_Device_GetNextResponse() is an advanced function. You should usually use 
				NGIO_Device_SendCmdAndGetResponse() instead. After calling NGIO_Device_SendCmd(), you must call
				NGIO_Device_GetNextResponse() before sending any more commands to the device.

	Return:		0 if successful, else -1.

****************************************************************************************************************************/
NGIO_LIB_INTERFACE_DECL gtype_int32 NGIO_Device_GetNextResponse(
	NGIO_DEVICE_HANDLE hDevice,	//[in] handle to open device.
	NGIO_PTR pRespBuf,			//[out] ptr to destination buffer, may be NULL.
	gtype_uint32 *pnRespBytes,  //[in, out] size of of dest buffer on input, size of response on output, may be NULL if pRespBuf is NULL.
	unsigned char *pCmd,		//[out] identifies which command this response is for. Must NOT be NULL!
	gtype_bool *pErrRespFlag,	//[out] flag indicating that the response contains error info. Must NOT be NULL!
	gtype_uint32 *pSignature,	//[in] if not NULL, then compare command signature emebedded in response blob with *pSignature.
	gtype_uint32 nTimeoutMs);	//[in] # of milliseconds to wait before giving up.

/***************************************************************************************************************************
	Function Name: NGIO_Device_GetMeasurementTick()
	
	Purpose:	The measurement period for devices is specified in discrete 'ticks', so the actual time between 
				measurements is an integer multiple of the tick time.
	
	Return:		If hDevice is not valid, then this routine returns -1.0, else the return value = the length of time
				in seconds between ticks.

****************************************************************************************************************************/
NGIO_LIB_INTERFACE_DECL gtype_real64 NGIO_Device_GetMeasurementTick(
	NGIO_DEVICE_HANDLE hDevice,	//[in] handle to open device.
	signed char channel);	//[in] -1 => all channels.
	
/***************************************************************************************************************************
	Function Name: NGIO_Device_GetMinimumMeasurementPeriod()
	
	Return:		If hDevice is not valid, then this routine returns -1.0, else the return value = minimum measurement
				period in seconds that is supported by the device.

****************************************************************************************************************************/
NGIO_LIB_INTERFACE_DECL gtype_real64 NGIO_Device_GetMinimumMeasurementPeriod(
	NGIO_DEVICE_HANDLE hDevice,	//[in] handle to open device.
	signed char channel);	//[in] -1 => all channels.

/***************************************************************************************************************************
	Function Name: NGIO_Device_GetMaximumMeasurementPeriod()
	
	Return:		If hDevice is not valid, then this routine returns -1.0, else the return value = maximum measurement
				period in seconds that is supported by the device.

****************************************************************************************************************************/
NGIO_LIB_INTERFACE_DECL gtype_real64 NGIO_Device_GetMaximumMeasurementPeriod(
	NGIO_DEVICE_HANDLE hDevice,//[in] handle to open device.
	signed char channel);	//[in] -1 => all channels.

/***************************************************************************************************************************
	Function Name: NGIO_Device_SetMeasurementPeriod()
	
	Purpose:	Set the measurement period to a specified number of seconds. The device will report measurements
				to the host computer at the measurement period interval once measurements have been started if the
				sampling mode for a channel is periodic. All currently supported sampling modes are periodic
				except NGIO_SAMPLING_MODE_APERIODIC_EDGE_DETECT. These measurements are held in the NGIO Measurement 
				Buffer allocated for each channel. 
				See documentation for NGIO_Device_GetNumMeasurementsAvailable().

				This routine sends NGIO_CMD_ID_SET_MEASUREMENT_PERIOD to the device with the parameter block configured 
				for the desiredPeriod.

				If all channels are sampled at the same rate, call NGIO_Device_SetMeasurementPeriod(channel = -1).
				If channels are sampled at different rates, you must still call NGIO_Device_SetMeasurementPeriod(channel = -1)
				before setting the periods for the individual channels.
				NGIO_Device_SetMeasurementPeriod(channel = -1) sets the 'base' period.
				Sampling periods for individual channels are restricted to be an integer multiple of the base period, where the
				multiplier ranges between 1 and 0xffff . NGIO_Device_SetMeasurementPeriod(channel != -1) will find the multiple 
				of the base period that is closest to the desiredPeriod.

				Because the base measurement period is constrained to be a multiple of the tick returned by
				NGIO_Device_GetMeasurementTick(), and because it must be 
					>= NGIO_Device_GetMinimumMeasurementPeriod(), and
					<= NGIO_Device_GetMaximumMeasurementPeriod(), 
				the actual base period is different than the desiredPeriod.

				You can determine the actual period by calling NGIO_Device_GetMeasurementPeriod().

				This routine will fail if we are currently collecting measurements from the device. Note that collection
				is started by sending NGIO_CMD_ID_START_MEASUREMENTS to the device, and stopped by sending 
				NGIO_CMD_ID_STOP_MEASUREMENTS.

	Return:		0 if successful, else -1.

****************************************************************************************************************************/
NGIO_LIB_INTERFACE_DECL gtype_int32 NGIO_Device_SetMeasurementPeriod(
	NGIO_DEVICE_HANDLE hDevice,	//[in] handle to open device.
	signed char channel,		//[in] -1 => all channels.
	gtype_real64 desiredPeriod,	//[in] desired measurement period in seconds.
	gtype_uint32 timeoutMs);	//[in] # of milliseconds to wait for a reply before giving up. NGIO_TIMEOUT_MS_DEFAULT is recommended.

/***************************************************************************************************************************
	Function Name: GoIO_Sensor_GetMeasurementPeriod()

	Purpose:	This routine sends NGIO_CMD_ID_GET_MEASUREMENT_PERIOD to the device to get the measurement period for
				the specified channel and then converts the result to seconds.
	
	Return:		0 if successful, else -1.

****************************************************************************************************************************/
NGIO_LIB_INTERFACE_DECL gtype_int32 NGIO_Device_GetMeasurementPeriod(
	NGIO_DEVICE_HANDLE hDevice,	//[in] handle to open device.
	signed char channel,		//[in] -1 => return 'base' period, else return period for specified channel.
	gtype_real64 *pPeriod,		//[out] ptr to loc to store period in seconds.
	gtype_uint32 timeoutMs);	//[in] # of milliseconds to wait for a reply before giving up. NGIO_TIMEOUT_MS_DEFAULT is recommended.

NGIO_LIB_INTERFACE_DECL gtype_int32 NGIO_Device_SetCurrentTime(		//This function is not implemented.
	NGIO_DEVICE_HANDLE hDevice,	//[in] handle to open device.
	gtype_real64 currentTime,	//[in] current time in seconds.
	gtype_uint32 timeoutMs);	//[in] # of milliseconds to wait for a reply before giving up. NGIO_TIMEOUT_MS_DEFAULT is recommended.

NGIO_LIB_INTERFACE_DECL gtype_int32 NGIO_Device_GetCurrentTime(		//This function is not implemented.
	NGIO_DEVICE_HANDLE hDevice,	//[in] handle to open device.
	gtype_real64 *pCurrentTime,	//[out] ptr to loc to store current time in seconds
	gtype_uint32 timeoutMs);	//[in] # of milliseconds to wait for a reply before giving up. NGIO_TIMEOUT_MS_DEFAULT is recommended.

/***************************************************************************************************************************
	Function Name: NGIO_Device_GetNumMeasurementsAvailable()
	
	Purpose:	Report the number of measurements currently stored in the NGIO Measurement Buffer for the
				specified channel.

				After NGIO_CMD_ID_START_MEASUREMENTS has been sent to the device, the device starts
				sending measurements to the host computer. These measurements are stored in the 
				NGIO Measurement Buffer. A separate NGIO Measurement Buffer is maintained for each
				channel of the device.

				Generally the device starts taking measurements immediately after receiving NGIO_CMD_ID_START_MEASUREMENTS.
				However, it may take approximately a 20 milliseconds for the data to come back from the device via a USB
				transport.

				Subsequent measurements are sent at the currently configured measurement period interval if the current
				mode is periodic, ie not NGIO_SAMPLING_MODE_APERIODIC_EDGE_DETECT.
				See NGIO_Device_SetMeasurementPeriod().

				Call NGIO_Device_ReadRawMeasurements() to retrieve measurements from the NGIO Measurement Buffer for a 
				specified channel. The NGIO Measurement Buffer is guaranteed to hold at least 10000 measurements(per channel). 
				The buffer is circular, so if you do not service it often enough, the oldest measurements in the buffer 
				are lost. If you wish to capture all the measurements coming from the device, you must call 
				NGIO_Device_ReadRawMeasurements() often enough so that the NGIO_Device_GetNumMeasurementsAvailable() does not 
				reach 10000. On the other hand, we reserve the right to make the Measurement Buffer > 10000 measurements, so
				do not assume that you can empty the buffer simply by reading in 10000 measurements.

				Each of the following actions clears the NGIO Measurement Buffer for a specified channel:
					1) Call NGIO_Device_ReadRawMeasurements() with count set to NGIO_Device_GetNumMeasurementsAvailable(), or
					2) Call NGIO_Device_GetLatestRawMeasurement(), or
					3) Call NGIO_Device_ClearIO().

				The NGIO Measurement Buffers are empty after NGIO_Device_Open() is called. They do not
				start filling up until NGIO_CMD_ID_START_MEASUREMENTS is sent to the device.

				Note that the device stops sending measurements to the host computer after
				NGIO_CMD_ID_STOP_MEASUREMENTS or NGIO_CMD_ID_INIT is sent, but sending these commands
				does not clear the NGIO Measurement Buffers.

	Return:		number of measurements currently stored in the NGIO Measurement Buffer for the specified channel. 

****************************************************************************************************************************/
NGIO_LIB_INTERFACE_DECL gtype_int32 NGIO_Device_GetNumMeasurementsAvailable(
	NGIO_DEVICE_HANDLE hDevice,	//[in] handle to open device.
	signed char channel);		//[in]

/***************************************************************************************************************************
	Function Name: NGIO_Device_ReadRawMeasurements()
	
	Purpose:	Retrieve measurements from the NGIO Measurement Buffer for a specified channel. The measurements reported
				by this routine are actually removed from the NGIO Measurement Buffer.

				After NGIO_CMD_ID_START_MEASUREMENTS has been sent to the device, the device starts
				sending measurements to the host computer. These measurements are stored in the 
				NGIO Measurement Buffer. A separate NGIO Measurement Buffer is maintained for each
				channel of the device.
				See the description of NGIO_Device_GetNumMeasurementsAvailable().

				If the current sampling mode is periodic with period Tp, then the first measurement in a
				channel's NGIO Measurement Buffer occurred at time 0, the next measurement occurred at time Tp,
				then time 2*Tp, 3*Tp, and so on. If several different channels are sampled at the same rate, then
				at times n*Tp, the separate channels are sampled concurrently. In fact, if the hardware is configured
				two measure two different channels at the same time, the measurements occur several microseconds apart,
				but we generally report these measurements to have occurred at the same time.

				NGIO_Device_ReadRawMeasurements() provides a pTimeStamps parameter so that the caller can obtain
				timestamps for the measurements. Timestamps have a 1 'tick' resolution, which is 1 microsecond for the 
				LabQuest. However, the pTimeStamps paramater is only supported if the current sampling mode is 
				NGIO_SAMPLING_MODE_APERIODIC_EDGE_DETECT or NGIO_SAMPLING_MODE_PERIODIC_MOTION_DETECT.
				The pTimeStamps parameter must be NULL for the other sampling modes, or the routine will fail.
				When pTimeStamps is not supported, then the sampling mode is periodic, and we place the burden of
				creating the timestamps on the caller. These timestamps are easily reconstructed according to the logic
				described in the previous paragraph. Eventually, we plan to have NGIO_Device_ReadRawMeasurements()
				always support the pTimeStamps parameter.

				NGIO_SAMPLING_MODE_PERIODIC_LEVEL_SNAPSHOT:
					This sampling mode is used with NGIO_CHANNEL_ID_ANALOG1 thru NGIO_CHANNEL_ID_ANALOG4.
					The values returned are generally 16 bit integers obtained from an analog to digital converter.
					Use NGIO_Device_ConvertToVoltage() to convert the raw value to volts, and use 
					NGIO_Device_CalibrateData() to convert volts to sensor specific units.
					Use NGIO_Device_DDSMem_GetCalPage to identify calibration units.

				NGIO_SAMPLING_MODE_APERIODIC_EDGE_DETECT:
					Used with photogates and drop counters in NGIO_CHANNEL_ID_DIGITAL1 and NGIO_CHANNEL_ID_DIGITAL2.
					Values returned are NGIO_MEAS_PHOTOGATE_BLOCKED or NGIO_MEAS_PHOTOGATE_UNBLOCKED.
					Timestamps are returned with measurements. 

				NGIO_SAMPLING_MODE_PERIODIC_MOTION_DETECT:
					Used with motion detectors in NGIO_CHANNEL_ID_DIGITAL1 and NGIO_CHANNEL_ID_DIGITAL2.
					Values returned are NGIO_MEAS_MOTION_DETECTOR_PING, NGIO_MEAS_MOTION_DETECTOR_ECHO,
					or NGIO_MEAS_MOTION_DETECTOR_FALSE_ECHO. A false echo is reported if no echo is detected after a ping.
					Timestamps are returned with measurements.
					Distance is calculated = 0.5*(echo time - previous ping time)*(speed of sound) since the delta
					time is how long the ping audio pulse takes to travel from the detector to the object and then back
					to the detector.

				NGIO_SAMPLING_MODE_PERIODIC_PULSE_COUNT:
					Used with radiation counters in NGIO_CHANNEL_ID_DIGITAL1 and NGIO_CHANNEL_ID_DIGITAL2.
					Values returned are cumulative counts that range from 0 to 2147483647.
					The count is set to 0 when the mode is configured, and the device starts keeping track at
					mode configuration time. The first measurement returned is the number of counts detected between
					initial configuration and when NGIO_CMD_ID_START_MEASUREMENTS is processed. You can reset the count
					using NGIO_CMD_ID_SET_DIGITAL_COUNTER. If the application wants to act like a geiger counter, it needs
					to calculate the differences between successive measurement counts.

				NGIO_SAMPLING_MODE_PERIODIC_ROTATION_COUNTER:
				NGIO_SAMPLING_MODE_PERIODIC_ROTATION_COUNTER_X4:
					Used with rotary motion sensors in NGIO_CHANNEL_ID_DIGITAL1 and NGIO_CHANNEL_ID_DIGITAL2.
					Values returned are net cumulative counts that range from -2147483648 to 2147483647.
					Convert from counts to CCW wheel rotations by dividing by 360 or (360x4).
					The count is set to 0 when the mode is configured, and the device starts keeping track at
					mode configuration time. The first measurement returned is the number of counts detected between
					initial configuration and when NGIO_CMD_ID_START_MEASUREMENTS is processed. You can reset the count
					using NGIO_CMD_ID_SET_DIGITAL_COUNTER. Note that these counts are 'net' counts, so spin the wheel CCW to
					increase the count, spin the wheel CW to decrease the count.

	Return:		number of measurements retrieved from the NGIO Measurement Buffer. This routine
				returns immediately, so the return value may be less than maxCount.

****************************************************************************************************************************/
//Photogates return the following raw measurements:
#define NGIO_MEAS_PHOTOGATE_BLOCKED 1
#define NGIO_MEAS_PHOTOGATE_UNBLOCKED 0
//Motion Detectors return the following raw measurements:
#define NGIO_MEAS_MOTION_DETECTOR_PING 0
#define NGIO_MEAS_MOTION_DETECTOR_ECHO 1
//A false echo is reported if no echo is detected after a ping.
#define NGIO_MEAS_MOTION_DETECTOR_FALSE_ECHO 2

NGIO_LIB_INTERFACE_DECL gtype_int32 NGIO_Device_ReadRawMeasurements(
	NGIO_DEVICE_HANDLE hDevice,		//[in] handle to open device.
	signed char channel,			//[in]
	gtype_int32 *pMeasurementsBuf,	//[out] ptr to loc to store measurements.
	gtype_int64 *pTimeStamps,		//[out] ptr to loc to store 'tick' resolution timestamps. May be NULL.
									//Timestamp is 0 at time of trigger. For normal real time mode,
									//measurements are triggered when NGIO_CMD_ID_START_MEASUREMENTS is received.
	gtype_uint32 maxCount);	//[in] maximum number of measurements to copy to pMeasurementsBuf. See warning above.

NGIO_LIB_INTERFACE_DECL gtype_int32 NGIO_Device_GetLatestRawMeasurement(
	NGIO_DEVICE_HANDLE hDevice,	//[in] handle to open device.
	signed char channel);				//[in]

/***************************************************************************************************************************
	Function Name: NGIO_Device_ConvertToVoltage()

	Purpose:	This routine converts a raw measurement obtained from NGIO_Device_GetLatestRawMeasurement() or 
				NGIO_Device_ReadRawMeasurements() into a voltage value. The range of possible output voltages
				is generally implied by the probeType.

				This routine is moderately fast because for most probeTypes it is not necessary to access the hDevice,
				so the routine is a simple mathematical transform. However, the return value is real, so the
				inefficiencies associated with real numbers apply here.
	
	Return:		volts

****************************************************************************************************************************/
NGIO_LIB_INTERFACE_DECL gtype_real32 NGIO_Device_ConvertToVoltage(
	NGIO_DEVICE_HANDLE hDevice,	//[in] handle to open device.
	signed char channel,		//[in]
	gtype_int32 rawMeasurement, //[in] raw measurement obtained from NGIO_Device_GetLatestRawMeasurement() or 
								//NGIO_Device_ReadRawMeasurements().
	gtype_int32 probeType);		//[in] See declaration of EProbeType

/***************************************************************************************************************************
	Function Name: NGIO_Device_CalibrateData()

	Purpose:	This routine converts a voltage value into a calibrated measurement.

				For most sensors, the following operation is performed(kEquationType_Linear):
					calibrated val = CalibrationCoefficientB*volts + CalibrationCoefficientA

				NGIO_Device_CalibrateData() gets the coefficients from NGIO_Device_DDSMem_GetActiveCalPage(),
				and NGIO_Device_DDSMem_GetCalPage(). NGIO_Device_DDSMem_GetCalibrationEquation() must return 
				kEquationType_Linear for NGIO_Device_CalibrateData() to work.

				If NGIO_Device_DDSMem_GetCalibrationEquation() does not return kEquationType_Linear,
				then you must obtain the calibration coefficients using the NGIO_Device_DDSMem_ routines,
				and then do the arithmetic yourself. NGIO_Device_CalibrateData() is not very efficient, 
				so if performance is an issue, that is another reason to do the arithmetic yourself.
				See GSensorDDSMem.h .

	Return:		calibrated measurement value

****************************************************************************************************************************/
NGIO_LIB_INTERFACE_DECL gtype_real32 NGIO_Device_CalibrateData(
	NGIO_DEVICE_HANDLE hDevice,	//[in] handle to open device.
	signed char channel,		//[in]
	gtype_real32 volts);		//[in] voltage value obtained from NGIO_Device_ConvertToVoltage();

/***************************************************************************************************************************
	Function Name: NGIO_Device_CalibrateData2()

	Purpose:	This routine converts a raw measurement obtained from NGIO_Device_GetLatestRawMeasurement() or 
				NGIO_Device_ReadRawMeasurements() into a calibrated measurement.

				This is basically just a convenience function that does the following:
					calibrated val = NGIO_Device_CalibrateData(hDevice, channel, 
										NGIO_Device_ConvertToVoltage(
											hDevice, channel, rawMeasurement, 
												NGIO_Device_GetProbeType(hDevice, channel)))

				If NGIO_Device_DDSMem_GetCalibrationEquation() does not return kEquationType_Linear,
				then you must obtain the calibration coefficients using the NGIO_Device_DDSMem_ routines,
				and then do the arithmetic yourself. See GSensorDDSMem.h .
	
	Return:		calibrated measurement value

****************************************************************************************************************************/
NGIO_LIB_INTERFACE_DECL gtype_real32 NGIO_Device_CalibrateData2(
	NGIO_DEVICE_HANDLE hDevice,	//[in] handle to open device.
	signed char channel,				//[in]
	gtype_int32 rawMeasurement);//[in] raw measurement obtained from NGIO_Device_GetLatestRawMeasurement() or 
								//NGIO_Device_ReadRawMeasurements().

/***************************************************************************************************************************
	Function Name: NGIO_Device_GetProbeType()
	
	Purpose:	Find out the probe type. See EProbeType in GSensorDDSMem.h.

				For analog sensors(in channels NGIO_CHANNEL_ID_ANALOG1 .. NGIO_CHANNEL_ID_ANALOG4), this 
				attribute is dependent on the OperationType in the SensorDDSRecord. 
				See NGIO_Device_DDSMem_GetOperationType().
				If (2 == OperationType) then the sensor is kProbeTypeAnalog10V, else kProbeTypeAnalog5V.

	Return:		EProbeType.

****************************************************************************************************************************/
NGIO_LIB_INTERFACE_DECL gtype_int32 NGIO_Device_GetProbeType(
	NGIO_DEVICE_HANDLE hDevice,	//[in] handle to open device.
	signed char channel);				//[in]


NGIO_LIB_INTERFACE_DECL gtype_int32 NGIO_Device_GetChannelRange(
	NGIO_DEVICE_HANDLE hDevice,	//[in] handle to open device.
	gtype_uint32 *pMinChannel,	//[out]
	gtype_uint32 *pMaxChannel);	//[out]

NGIO_LIB_INTERFACE_DECL gtype_int32 NGIO_Device_NVMemBlk_GetAddrRange(
	NGIO_DEVICE_HANDLE hDevice,	//[in] handle to open device.
	signed char channel,		//[in]
	gtype_uint32 *pMinAddr,		//[out]
	gtype_uint32 *pMaxAddr);	//[out]

NGIO_LIB_INTERFACE_DECL gtype_int32 NGIO_Device_NVMemBlk_Read(
	NGIO_DEVICE_HANDLE hDevice,	//[in] handle to open device.
	signed char channel,		//[in]
	NGIO_PTR pDestBuf,
	gtype_uint32 addr,
	gtype_uint32 numBytesToRead,
	gtype_uint32 timeoutMs);		//[in] # of milliseconds to wait for a reply before giving up.

NGIO_LIB_INTERFACE_DECL gtype_int32 NGIO_Device_NVMemBlk_Write(
	NGIO_DEVICE_HANDLE hDevice,	//[in] handle to open device.
	signed char channel,		//[in]
	NGIO_PTR pSrcBuf,
	gtype_uint32 addr,
	gtype_uint32 numBytesToWrite,
	gtype_uint32 timeoutMs);		//[in] # of milliseconds to wait for a reply before giving up.

/***************************************************************************************************************************/
/***************************************************************************************************************************/
/***************************************************************************************************************************
NGIO_Device_DDSMem_* routines:

The NGIO device object maintains a list of SensorDDSRecords of type GSensorDDSRec, one for each channel. 
The fields in this record describe the sensor plugged into the channel.
The NGIO_Device_DDSMem_* routines allow an application program to access the SensorDDSRecord.
See GSensorDDSMem.h for the declaration of GSensorDDSRec.

Sensors come in 3 basic flavors:
1) Smart sensors,
2) AutoId capable sensors that are not smart, and
3) Legacy sensors.

Smart sensors store the entire 128 byte GSensorDDSRec record on the actual sensor hardware. This allows calibration
data that is specific to a given sensor to be stored on the sensor. Smart sensors report an AutoId number
in response to the NGIO_CMD_ID_GET_SENSOR_ID command that is >= kSensorIdNumber_FirstSmartSensor. 

If a sensor reports an AutoId >= kSensorIdNumber_FirstSmartSensor, then NGIO_Device_Open() will get the GSensorDDSRec 
info from the sensor hardware and store in the NGIO sensor object's SensorDDSRecord. 

Note that Go! Motion pretends to be smart: its id is > kSensorIdNumber_FirstSmartSensor, and NGIO_Device_Open() sets 
up its GSensorDDSRec info with calibrations for meters and feet. However, there is no way to store a modified
GSensorDDSRec record on the sensor permanently, which truly smart sensors do support.

AutoId capable sensors that are not smart report an AutoId value > 0 and < kSensorIdNumber_FirstSmartSensor.

Legacy (aka Dumb) sensors report an AutoId = 0.

Non smart sensors just get default values for most of the fields in the SensorDDSRecord. The default calibration 
parameters are configured so that NGIO_Device_CalibrateData() just reports volts.

For all 3 types of sensors, SensorDDSRecord.SensorNumber = AutoId after NGIO_Device_Open(). However, for
legacy sensors that means SensorDDSRecord.SensorNumber will be 0 which is not informative. In fact, Go! Link
reports an AutoID of 0 even if no sensor is plugged in!

All the NGIO_Device_DDSMem_* routines return 0 if successful, or a negative number if an error occurs. 
The NGIO_Device_DDSMem_* routines that do not take a timeout parameter will not fail if hDevice is valid and owned by
the calling thread.

****************************************************************************************************************************/
/***************************************************************************************************************************/
/***************************************************************************************************************************/

/***************************************************************************************************************************
	Function Name: NGIO_Device_DDSMem_WriteRecord()
	
	Purpose:	Write the SensorDDSRecord into a smart sensor's DDS memory hardware. 
				Before the record is written to the sensor, SensorDDSRecord.Checksum is recalculated.
				Before the record is written to the sensor, a copy of the record is marshalled into little endian format,
				and then this copy is actually written to the sensor.

				This routine sends NGIO_CMD_ID_WRITE_LOCAL_NV_MEM_*(Go! Temp) or NGIO_CMD_ID_WRITE_REMOTE_NV_MEM_*(Go! Link)
				commands to the device. To alter DDS memory on the sensor, we recommend using 
				NGIO_Device_DDSMem_WriteRecord rather than the low level NGIO_CMD_ID_WRITE commands.

				WARNING: Be careful about using this routine. Changing a smart sensor's DDS memory can cause the sensor
				to stop working with Logger Pro.
	
	Return:		0 if successful, else -1.

****************************************************************************************************************************/
NGIO_LIB_INTERFACE_DECL gtype_int32 NGIO_Device_DDSMem_WriteRecord(
	NGIO_DEVICE_HANDLE hDevice,	//[in] handle to open device.
	signed char channel,				//[in]
	gtype_uint32 timeoutMs);//[in] # of milliseconds to wait for a reply before giving up. NGIO_TIMEOUT_MS_WRITE_DDSMEMBLOCK is recommended.


/***************************************************************************************************************************
	Function Name: NGIO_Device_DDSMem_ReadRecord()
	
	Purpose:	Read the SensorDDSRecord from a smart sensor's DDS memory hardware. 
				After the record is read from the sensor, it is unmarshalled from little endian format into processor
				specific format.

				This routine sends NGIO_CMD_ID_READ_LOCAL_NV_MEM(Go! Temp) or NGIO_CMD_ID_READ_REMOTE_NV_MEM(Go! Link)
				commands to the device. 
	
	Return:		0 if successful, 
				else if data validation fails then -2,
				else -1.

****************************************************************************************************************************/
NGIO_LIB_INTERFACE_DECL gtype_int32 NGIO_Device_DDSMem_ReadRecord(
	NGIO_DEVICE_HANDLE hDevice,	//[in] handle to open device.
	signed char channel,				//[in]
	gtype_bool strictDDSValidationFlag,//[in] insist on exactly valid checksum if 1, else use a more lax validation test.
	gtype_uint32 timeoutMs);//[in] # of milliseconds to wait for a reply before giving up. NGIO_TIMEOUT_MS_READ_DDSMEMBLOCK is recommended.
						   //Note that NGIO_TIMEOUT_MS_READ_DDSMEMBLOCK is much longer than the typical time required which is < 500 ms.

/***************************************************************************************************************************
	Function Name: NGIO_Device_DDSMem_SetRecord()
	
	Purpose:	Update the SensorDDSRecord. 
				This routine does not send data to the sensor's DDS memory hardware - use NGIO_Device_DDSMem_WriteRecord for that.

	Return:		0 if hDevice is valid, else -1.

****************************************************************************************************************************/
NGIO_LIB_INTERFACE_DECL gtype_int32 NGIO_Device_DDSMem_SetRecord(
	NGIO_DEVICE_HANDLE hDevice,	//[in] handle to open device.
	signed char channel,				//[in]
	const GSensorDDSRec *pRec);	//[in] ptr to data to copy into the SensorDDSRecord.

/***************************************************************************************************************************
	Function Name: NGIO_Device_DDSMem_GetRecord()
	
	Purpose:	Get a copy of the SensorDDSRecord. 
				This routine does not read data from the sensor's DDS memory hardware - use NGIO_Device_DDSMem_ReadRecord for that.

	Return:		0 if hDevice is valid, else -1.

****************************************************************************************************************************/
NGIO_LIB_INTERFACE_DECL gtype_int32 NGIO_Device_DDSMem_GetRecord(
	NGIO_DEVICE_HANDLE hDevice,	//[in] handle to open device.
	signed char channel,				//[in]
	GSensorDDSRec *pRec);		//[out] ptr to dest buf to copy the SensorDDSRecord into.

/***************************************************************************************************************************
	Function Name: NGIO_Device_DDSMem_CalculateChecksum()
	
	Return:		0 if hDevice is valid, else -1.

****************************************************************************************************************************/
NGIO_LIB_INTERFACE_DECL gtype_int32 NGIO_Device_DDSMem_CalculateChecksum(
	NGIO_DEVICE_HANDLE hDevice,	//[in] handle to open device.
	signed char channel,				//[in]
	unsigned char *pChecksum);	//[out] ptr to checksum calculated by XOR'ing the first 127 bytes of the SensorDDSRecord.

/***************************************************************************************************************************/
/***************************************************************************************************************************/
/***************************************************************************************************************************
Languages that can handle GSensorDDSRec can access the DDS fields using NGIO_Device_DDSMem_SetRecord and GetRecord.
Languages such as Basic are unlikely to be able to handle a complex record like GSensorDDSRec, especially because of
byte alignment issues. The individual field level API's defined below provide an alternate way to access the SensorDDSRecord.
****************************************************************************************************************************/
/***************************************************************************************************************************/
/***************************************************************************************************************************/

NGIO_LIB_INTERFACE_DECL gtype_int32 NGIO_Device_DDSMem_SetMemMapVersion(
	NGIO_DEVICE_HANDLE hDevice,
	signed char channel,				//[in]
	unsigned char MemMapVersion);
NGIO_LIB_INTERFACE_DECL gtype_int32 NGIO_Device_DDSMem_GetMemMapVersion(
	NGIO_DEVICE_HANDLE hDevice,
	signed char channel,				//[in]
	unsigned char *pMemMapVersion);

NGIO_LIB_INTERFACE_DECL gtype_int32 NGIO_Device_DDSMem_SetSensorNumber(
	NGIO_DEVICE_HANDLE hDevice,	//[in] handle to open device.
	signed char channel,				//[in]
	unsigned char SensorNumber);//[in]

NGIO_LIB_INTERFACE_DECL gtype_int32 NGIO_Device_DDSMem_GetSensorNumber(
	NGIO_DEVICE_HANDLE hDevice,		//[in] handle to open device. 
	signed char channel,					//[in]
	unsigned char *pSensorNumber,	//[out] ptr to SensorNumber.
	gtype_bool sendQueryToHardwareflag,//[in] If sendQueryToHardwareflag != 0, then send a NGIO_CMD_ID_GET_SENSOR_ID to the sensor hardware. 
	gtype_uint32 *pChannelSignature,//[out] May be NULL if (0 == sendQueryToHardwareflag). More about this later. spam
	gtype_uint32 timeoutMs);//[in] # of milliseconds to wait for a reply before giving up. NGIO_TIMEOUT_MS_DEFAULT is recommended.

NGIO_LIB_INTERFACE_DECL gtype_int32 NGIO_Device_DDSMem_SetSerialNumber(
	NGIO_DEVICE_HANDLE hDevice,
	signed char channel,				//[in]
	unsigned char leastSigByte_SerialNumber,
	unsigned char midSigByte_SerialNumber,
	unsigned char mostSigByte_SerialNumber);
NGIO_LIB_INTERFACE_DECL gtype_int32 NGIO_Device_DDSMem_GetSerialNumber(
	NGIO_DEVICE_HANDLE hDevice,
	signed char channel,				//[in]
	unsigned char *pLeastSigByte_SerialNumber,
	unsigned char *pMidSigByte_SerialNumber,
	unsigned char *pMostSigByte_SerialNumber);

NGIO_LIB_INTERFACE_DECL gtype_int32 NGIO_Device_DDSMem_SetLotCode(
	NGIO_DEVICE_HANDLE hDevice,
	signed char channel,				//[in]
	unsigned char YY_BCD,
	unsigned char WW_BCD);
NGIO_LIB_INTERFACE_DECL gtype_int32 NGIO_Device_DDSMem_GetLotCode(
	NGIO_DEVICE_HANDLE hDevice,
	signed char channel,				//[in]
	unsigned char *pYY_BCD,
	unsigned char *pWW_BCD);

NGIO_LIB_INTERFACE_DECL gtype_int32 NGIO_Device_DDSMem_SetManufacturerID(
	NGIO_DEVICE_HANDLE hDevice,
	signed char channel,				//[in]
	unsigned char ManufacturerID);
NGIO_LIB_INTERFACE_DECL gtype_int32 NGIO_Device_DDSMem_GetManufacturerID(
	NGIO_DEVICE_HANDLE hDevice,
	signed char channel,				//[in]
	unsigned char *pManufacturerID);

NGIO_LIB_INTERFACE_DECL gtype_int32 NGIO_Device_DDSMem_SetLongName(
	NGIO_DEVICE_HANDLE hDevice,
	signed char channel,				//[in]
	const char *pLongName);//[in] ptr to NULL terminated string.
NGIO_LIB_INTERFACE_DECL gtype_int32 NGIO_Device_DDSMem_GetLongName(
	NGIO_DEVICE_HANDLE hDevice,			//[in] handle to open device. 
	signed char channel,						//[in]
	char *pLongName,					//[out] ptr to buffer for NULL terminated output string.
	gtype_uint16 maxNumBytesToCopy);	//[in] size of pLongName buffer.

NGIO_LIB_INTERFACE_DECL gtype_int32 NGIO_Device_DDSMem_SetShortName(
	NGIO_DEVICE_HANDLE hDevice,
	signed char channel,			//[in]
	const char *pShortName);//[in] ptr to NULL terminated string.
NGIO_LIB_INTERFACE_DECL gtype_int32 NGIO_Device_DDSMem_GetShortName(
	NGIO_DEVICE_HANDLE hDevice,			//[in] handle to open device. 
	signed char channel,						//[in]
	char *pShortName,					//[out] ptr to buffer for NULL terminated output string. 
	gtype_uint16 maxNumBytesToCopy);	//[in] size of pShortName buffer.

NGIO_LIB_INTERFACE_DECL gtype_int32 NGIO_Device_DDSMem_SetUncertainty(
	NGIO_DEVICE_HANDLE hDevice,
	signed char channel,						//[in]
	unsigned char Uncertainty);
NGIO_LIB_INTERFACE_DECL gtype_int32 NGIO_Device_DDSMem_GetUncertainty(
	NGIO_DEVICE_HANDLE hDevice,
	signed char channel,						//[in]
	unsigned char *pUncertainty);

NGIO_LIB_INTERFACE_DECL gtype_int32 NGIO_Device_DDSMem_SetSignificantFigures(
	NGIO_DEVICE_HANDLE hDevice,
	signed char channel,						//[in]
	unsigned char SignificantFigures);
NGIO_LIB_INTERFACE_DECL gtype_int32 NGIO_Device_DDSMem_GetSignificantFigures(
	NGIO_DEVICE_HANDLE hDevice,
	signed char channel,						//[in]
	unsigned char *pSignificantFigures);

NGIO_LIB_INTERFACE_DECL gtype_int32 NGIO_Device_DDSMem_SetCurrentRequirement(
	NGIO_DEVICE_HANDLE hDevice,
	signed char channel,						//[in]
	unsigned char CurrentRequirement);
NGIO_LIB_INTERFACE_DECL gtype_int32 NGIO_Device_DDSMem_GetCurrentRequirement(
	NGIO_DEVICE_HANDLE hDevice,
	signed char channel,						//[in]
	unsigned char *pCurrentRequirement);

NGIO_LIB_INTERFACE_DECL gtype_int32 NGIO_Device_DDSMem_SetAveraging(
	NGIO_DEVICE_HANDLE hDevice,
	signed char channel,						//[in]
	unsigned char Averaging);
NGIO_LIB_INTERFACE_DECL gtype_int32 NGIO_Device_DDSMem_GetAveraging(
	NGIO_DEVICE_HANDLE hDevice,
	signed char channel,						//[in]
	unsigned char *pAveraging);

NGIO_LIB_INTERFACE_DECL gtype_int32 NGIO_Device_DDSMem_SetMinSamplePeriod(
	NGIO_DEVICE_HANDLE hDevice,
	signed char channel,						//[in]
	gtype_real32 MinSamplePeriod);
NGIO_LIB_INTERFACE_DECL gtype_int32 NGIO_Device_DDSMem_GetMinSamplePeriod(
	NGIO_DEVICE_HANDLE hDevice,
	signed char channel,						//[in]
	gtype_real32 *pMinSamplePeriod);

NGIO_LIB_INTERFACE_DECL gtype_int32 NGIO_Device_DDSMem_SetTypSamplePeriod(
	NGIO_DEVICE_HANDLE hDevice,
	signed char channel,						//[in]
	gtype_real32 TypSamplePeriod);
NGIO_LIB_INTERFACE_DECL gtype_int32 NGIO_Device_DDSMem_GetTypSamplePeriod(
	NGIO_DEVICE_HANDLE hDevice,
	signed char channel,						//[in]
	gtype_real32 *pTypSamplePeriod);

NGIO_LIB_INTERFACE_DECL gtype_int32 NGIO_Device_DDSMem_SetTypNumberofSamples(
	NGIO_DEVICE_HANDLE hDevice,
	signed char channel,						//[in]
	gtype_uint16 TypNumberofSamples);
NGIO_LIB_INTERFACE_DECL gtype_int32 NGIO_Device_DDSMem_GetTypNumberofSamples(
	NGIO_DEVICE_HANDLE hDevice,
	signed char channel,						//[in]
	gtype_uint16 *pTypNumberofSamples);

NGIO_LIB_INTERFACE_DECL gtype_int32 NGIO_Device_DDSMem_SetWarmUpTime(
	NGIO_DEVICE_HANDLE hDevice,
	signed char channel,						//[in]
	gtype_uint16 WarmUpTime);
NGIO_LIB_INTERFACE_DECL gtype_int32 NGIO_Device_DDSMem_GetWarmUpTime(
	NGIO_DEVICE_HANDLE hDevice,
	signed char channel,						//[in]
	gtype_uint16 *pWarmUpTime);

NGIO_LIB_INTERFACE_DECL gtype_int32 NGIO_Device_DDSMem_SetExperimentType(
	NGIO_DEVICE_HANDLE hDevice,
	signed char channel,						//[in]
	unsigned char ExperimentType);
NGIO_LIB_INTERFACE_DECL gtype_int32 NGIO_Device_DDSMem_GetExperimentType(
	NGIO_DEVICE_HANDLE hDevice,
	signed char channel,						//[in]
	unsigned char *pExperimentType);

//NGIO_Device_DDSMem_SetOperationType() can change the probe type. See NGIO_Device_GetProbeType().
NGIO_LIB_INTERFACE_DECL gtype_int32 NGIO_Device_DDSMem_SetOperationType(
	NGIO_DEVICE_HANDLE hDevice,
	signed char channel,						//[in]
	unsigned char OperationType);
NGIO_LIB_INTERFACE_DECL gtype_int32 NGIO_Device_DDSMem_GetOperationType(
	NGIO_DEVICE_HANDLE hDevice,
	signed char channel,						//[in]
	unsigned char *pOperationType);

NGIO_LIB_INTERFACE_DECL gtype_int32 NGIO_Device_DDSMem_SetCalibrationEquation(
	NGIO_DEVICE_HANDLE hDevice,
	signed char channel,						//[in]
	char CalibrationEquation);
NGIO_LIB_INTERFACE_DECL gtype_int32 NGIO_Device_DDSMem_GetCalibrationEquation(
	NGIO_DEVICE_HANDLE hDevice,
	signed char channel,						//[in]
	char *pCalibrationEquation);

NGIO_LIB_INTERFACE_DECL gtype_int32 NGIO_Device_DDSMem_SetYminValue(
	NGIO_DEVICE_HANDLE hDevice,
	signed char channel,						//[in]
	gtype_real32 YminValue);
NGIO_LIB_INTERFACE_DECL gtype_int32 NGIO_Device_DDSMem_GetYminValue(
	NGIO_DEVICE_HANDLE hDevice,
	signed char channel,						//[in]
	gtype_real32 *pYminValue);

NGIO_LIB_INTERFACE_DECL gtype_int32 NGIO_Device_DDSMem_SetYmaxValue(
	NGIO_DEVICE_HANDLE hDevice,
	signed char channel,						//[in]
	gtype_real32 YmaxValue);
NGIO_LIB_INTERFACE_DECL gtype_int32 NGIO_Device_DDSMem_GetYmaxValue(
	NGIO_DEVICE_HANDLE hDevice,
	signed char channel,						//[in]
	gtype_real32 *pYmaxValue);

NGIO_LIB_INTERFACE_DECL gtype_int32 NGIO_Device_DDSMem_SetYscale(
	NGIO_DEVICE_HANDLE hDevice,
	signed char channel,						//[in]
	unsigned char Yscale);
NGIO_LIB_INTERFACE_DECL gtype_int32 NGIO_Device_DDSMem_GetYscale(
	NGIO_DEVICE_HANDLE hDevice,
	signed char channel,						//[in]
	unsigned char *pYscale);

NGIO_LIB_INTERFACE_DECL gtype_int32 NGIO_Device_DDSMem_SetHighestValidCalPageIndex(
	NGIO_DEVICE_HANDLE hDevice,
	signed char channel,						//[in]
	unsigned char HighestValidCalPageIndex);
NGIO_LIB_INTERFACE_DECL gtype_int32 NGIO_Device_DDSMem_GetHighestValidCalPageIndex(
	NGIO_DEVICE_HANDLE hDevice,
	signed char channel,						//[in]
	unsigned char *pHighestValidCalPageIndex);

NGIO_LIB_INTERFACE_DECL gtype_int32 NGIO_Device_DDSMem_SetActiveCalPage(
	NGIO_DEVICE_HANDLE hDevice,
	signed char channel,						//[in]
	unsigned char ActiveCalPage);
NGIO_LIB_INTERFACE_DECL gtype_int32 NGIO_Device_DDSMem_GetActiveCalPage(
	NGIO_DEVICE_HANDLE hDevice,
	signed char channel,						//[in]
	unsigned char *pActiveCalPage);

NGIO_LIB_INTERFACE_DECL gtype_int32 NGIO_Device_DDSMem_SetCalPage(
	NGIO_DEVICE_HANDLE hDevice,
	signed char channel,						//[in]
	unsigned char CalPageIndex,
	gtype_real32 CalibrationCoefficientA,
	gtype_real32 CalibrationCoefficientB,
	gtype_real32 CalibrationCoefficientC,
	const char *pUnits);//[in] ptr to NULL terminated string.
NGIO_LIB_INTERFACE_DECL gtype_int32 NGIO_Device_DDSMem_GetCalPage(
	NGIO_DEVICE_HANDLE hDevice,
	signed char channel,						//[in]
	unsigned char CalPageIndex,
	gtype_real32 *pCalibrationCoefficientA,
	gtype_real32 *pCalibrationCoefficientB,
	gtype_real32 *pCalibrationCoefficientC,
	char *pUnits,						//[out] ptr to buffer for NULL terminated output string. 
	gtype_uint16 maxNumBytesToCopy);	//[in] size of pUnits buffer.

NGIO_LIB_INTERFACE_DECL gtype_int32 NGIO_Device_DDSMem_SetChecksum(
	NGIO_DEVICE_HANDLE hDevice,
	signed char channel,						//[in]
	unsigned char Checksum);
NGIO_LIB_INTERFACE_DECL gtype_int32 NGIO_Device_DDSMem_GetChecksum(
	NGIO_DEVICE_HANDLE hDevice,
	signed char channel,						//[in]
	unsigned char *pChecksum);

/***************************************************************************************************************************/
/***************************************************************************************************************************/
/***************************************************************************************************************************/
/* 'Advanced' API's:								*/
typedef void *NGIO_DEVICE_STREAM_HANDLE;

#define NGIO_PRIORITY_BELOW_NORMAL 0
#define NGIO_PRIORITY_NORMAL 1
#define NGIO_PRIORITY_ABOVE_NORMAL 2
#define NGIO_PRIORITY_HIGH 3
#define NGIO_PRIORITY_TIME_CRITICAL 4

//return value < 0 => error, else success.
typedef gtype_int32 (*P_NGIO_XFER_FUNC)
(
	unsigned char *pBytes,
	gtype_uint32 numBytes,
	NGIO_PTR pContextInfo
);

typedef gtype_int32 (*P_NGIO_QUERY_BUF_DEPTH_FUNC)
(
	NGIO_PTR pContextInfo
);

NGIO_LIB_INTERFACE_DECL NGIO_DEVICE_STREAM_HANDLE NGIO_Device_Stream_Open(
	NGIO_LIBRARY_HANDLE hLib,
	const char *pDeviceName,	//[in] NULL terminated string that uniquely identifies the device. See NGIO_GetNthAvailableDeviceName().
	P_NGIO_XFER_FUNC pReadCallback,
	gtype_uint32 readCallbackPriority,	//[IN] NGIO_PRIORITY_...
	NGIO_PTR pContextInfo);

NGIO_LIB_INTERFACE_DECL gtype_int32 NGIO_Device_Stream_Close(
	NGIO_DEVICE_STREAM_HANDLE hDeviceStream);//[in]

NGIO_LIB_INTERFACE_DECL gtype_int32 NGIO_Device_Stream_Write(
	NGIO_DEVICE_STREAM_HANDLE hDeviceStream,	//[in]
	NGIO_PTR pBuf,								//[in]
	gtype_uint32 numBytes);						//[in]

NGIO_LIB_INTERFACE_DECL gtype_int32 NGIO_Diags_SetIOTraceEnableFlag(
	NGIO_DEVICE_HANDLE hDevice,	//[in] handle to open device.
	gtype_bool flag);

NGIO_LIB_INTERFACE_DECL gtype_int32 NGIO_Diags_GetIOTraceEnableFlag(
	NGIO_DEVICE_HANDLE hDevice,	//[in] handle to open device.
	gtype_bool *pFlag);			//[out] ptr to loc to store IO trace enable flag. Note that IO tracing is enabled by default.

NGIO_LIB_INTERFACE_DECL gtype_int32 NGIO_Diags_GetNumInputTraceBytesAvailable(
	NGIO_DEVICE_HANDLE hDevice);//[in] handle to open device.

NGIO_LIB_INTERFACE_DECL gtype_int32 NGIO_Diags_ReadInputTraceBytes(
	NGIO_DEVICE_HANDLE hDevice,		//[in] handle to open device.
	unsigned char *pInputTraceBuf,	//[out] ptr to loc to store trace info.
	gtype_int32 maxCount);	//[in] maximum number of bytes to copy into pInputTraceBuf.

NGIO_LIB_INTERFACE_DECL gtype_int32 NGIO_Diags_GetNumOutputTraceBytesAvailable(
	NGIO_DEVICE_HANDLE hDevice);//[in] handle to open device.

NGIO_LIB_INTERFACE_DECL gtype_int32 NGIO_Diags_ReadOutputTraceBytes(
	NGIO_DEVICE_HANDLE hDevice,		//[in] handle to open device.
	unsigned char *pOutputTraceBuf,	//[out] ptr to loc to store trace info.
	gtype_int32 maxCount);	//[in] maximum number of bytes to copy into pOutputTraceBuf.

#define NGIO_TRACE_SEVERITY_LOWEST 1
#define NGIO_TRACE_SEVERITY_LOW 10
#define NGIO_TRACE_SEVERITY_MEDIUM 50
#define NGIO_TRACE_SEVERITY_HIGH 100
NGIO_LIB_INTERFACE_DECL gtype_int32 NGIO_Diags_SetDebugTraceThreshold(
	NGIO_LIBRARY_HANDLE hLib,
	gtype_int32 threshold);//[in] Only trace messages marked with a severity >= threshold are actually sent to the debug output.

NGIO_LIB_INTERFACE_DECL gtype_int32 NGIO_Diags_GetDebugTraceThreshold(
	NGIO_LIBRARY_HANDLE hLib,
	gtype_int32 *pThreshold);//[out]

#endif //_NGIO_LIB_INTERFACE_H_

#ifndef _NGIO_SOURCE_CMDS_PRIVATE_H_
#define _NGIO_SOURCE_CMDS_PRIVATE_H_

/***************************************************************************************************/
//
// This file contains declarations for parameter and response structures used by the function 
// GMBLBufferedMeasurementSource::SendCmdAndGetResponse().
//
/***************************************************************************************************/

//
// General NGIO commands include:
//
#define NGIO_CMD_ID_SET_EDGE_DETECT_REFRESH_TIME 0x48
#define NGIO_CMD_ID_GET_EDGE_DETECT_REFRESH_TIME 0x49
#define NGIO_CMD_ID_CALIBRATE_AUTOID 0x50
#define NGIO_CMD_ID_RUN_DAQ_SELF_TEST 0x51

#define NGIO_CMD_ID_PRIV_OPEN_RMT_DEVICE 0x70
#define NGIO_CMD_ID_PRIV_CLOSE_RMT_DEVICE 0x71
#define NGIO_CMD_ID_PRIV_OPEN_RMT_CONTROLLER 0x72
#define NGIO_CMD_ID_PRIV_QUERY_RMT_COLLECTION 0x73
#define NGIO_CMD_ID_PRIV_TAKE_DAQ_CONTROL 0x74

#define NGIO_INIT_CMD_RESPONSE_TIMEOUT_MS 2000

#if defined (TARGET_OS_WIN)
#pragma pack(push)
#pragma pack(1)
#endif

#ifdef TARGET_OS_MAC
#pragma pack(1)
#endif

//This is unfortunate, but gcc 3.x does not support pragma pack(gcc 4.x does!).
//We are stuck with gcc 3.x for now, so we use _XPACK1 .
//Note that some docs on the web mentioned problems with using typedefs and
//__attribute__, so we are putting the typedef on a separate line.
#ifndef _XPACK1
#ifdef TARGET_OS_LINUX
#define _XPACK1 __attribute__((__packed__))
#else
#define _XPACK1
#endif
#endif

/***************************************************************************************************/
struct tagNGIOOpenRemoteDeviceParams
{
	unsigned char lsbyteLsword_DeviceId;
	unsigned char msbyteLsword_DeviceId;
	unsigned char lsbyteMsword_DeviceId;
	unsigned char msbyteMsword_DeviceId;
	unsigned char deviceDescriptorLength;	//== strlen(deviceDescriptorBuf) + 1; Must be <= NGIO_MAX_SIZE_DEVICE_NAME. 
	unsigned char deviceDescriptorBuf[1];	//Must be NULL terminated.
	//length of params = sizeof(NGIOOpenRemoteDeviceParams) + deviceDescriptorLength - 1;
} _XPACK1; //Used with NGIO_CMD_ID_PRIV_OPEN_RMT_DEVICE.
typedef struct tagNGIOOpenRemoteDeviceParams NGIOOpenRemoteDeviceParams;

/***************************************************************************************************/
struct tagNGIOCloseRemoteDeviceParams
{
	unsigned char lsbyteLsword_DeviceId;
	unsigned char msbyteLsword_DeviceId;
	unsigned char lsbyteMsword_DeviceId;
	unsigned char msbyteMsword_DeviceId;
} _XPACK1; //Used with NGIO_CMD_ID_PRIV_CLOSE_RMT_DEVICE.
typedef struct tagNGIOCloseRemoteDeviceParams NGIOCloseRemoteDeviceParams;

/***************************************************************************************************/
struct tagNGIOOpenRemoteControllerParams
{
	unsigned char lsbyteLsword_DeviceId;
	unsigned char msbyteLsword_DeviceId;
	unsigned char lsbyteMsword_DeviceId;
	unsigned char msbyteMsword_DeviceId;
	unsigned char grabDAQFlag;
} _XPACK1; //Used with NGIO_CMD_ID_PRIV_OPEN_RMT_CONTROLLER.
typedef struct tagNGIOOpenRemoteControllerParams NGIOOpenRemoteControllerParams;

/***************************************************************************************************/
struct tagNGIOQueryRemoteCollectionCmdResponsePayload
{
	unsigned char remoteCollectionActive;
} _XPACK1; //This is the response payload returned by GetNextResponse() after sending NGIO_CMD_ID_PRIV_QUERY_RMT_COLLECTION.
typedef struct tagNGIOQueryRemoteCollectionCmdResponsePayload NGIOQueryRemoteCollectionCmdResponsePayload;
/***************************************************************************************************/

#define NGIO_NVMEM_CHANNEL_ID2_KEY1 0x3a
#define NGIO_NVMEM_CHANNEL_ID2_KEY2 0xe2
struct tagNGIO_NVMEM_CHANNEL_ID2_rec
{
	gtype_uint16 AdcValRam[7][17];
	unsigned char key1; //Should be NGIO_NVMEM_CHANNEL_ID2_KEY1
	unsigned char key2; //Should be NGIO_NVMEM_CHANNEL_ID2_KEY2
	unsigned char badChannelMask;
	unsigned char spare;
} _XPACK1;
typedef struct tagNGIO_NVMEM_CHANNEL_ID2_rec NGIO_NVMEM_CHANNEL_ID2_rec;

struct tagNGIOSetMeasurementPeriodCmdResponsePayloadX
{
	unsigned char status;	//Should be zero because this struct is only used when no error occurred.
	signed char channel;	/* -1 => all channels.	*/
	unsigned char lsbyteLswordMeasurementPeriod;
	unsigned char msbyteLswordMeasurementPeriod;
	unsigned char lsbyteMswordMeasurementPeriod;
	unsigned char msbyteMswordMeasurementPeriod;
} _XPACK1;
typedef struct tagNGIOSetMeasurementPeriodCmdResponsePayloadX NGIOSetMeasurementPeriodCmdResponsePayloadX;//This is the response 
//payload returned by GetNextResponse() after sending NGIO_CMD_ID_SET_MEASUREMENT_PERIOD to LabQuest DAQ firmware version 1.28 or newer.
//Older DAQ firmware just returns a status field.

/***************************************************************************************************/
//Parameter block for the NGIO_CMD_ID_START_MEASUREMENTS command:
//This parameter block is optional for NGIO_CMD_ID_START_MEASUREMENTS. If the SendCmdAndGetResponse
//parameter block ptr is NULL, then the device should use defaults. Typically, this means real time
//data collection is triggered immediately.

struct tagNGIOStartMeasurementsParams
{
	unsigned char lsbyteLswordDataRunId;	/* ffffffffh => next non RealTime data run, 0 => RealTime, else id's archived run.	*/
	unsigned char msbyteLswordDataRunId;	/* ffffffffh => next non RealTime data run, 0 => RealTime, else id's archived run.	*/
	unsigned char lsbyteMswordDataRunId;	/* ffffffffh => next non RealTime data run, 0 => RealTime, else id's archived run.	*/
	unsigned char msbyteMswordDataRunId;	/* ffffffffh => next non RealTime data run, 0 => RealTime, else id's archived run.	*/
	unsigned char lsbyteLswordLsdwordCollectionDuration;	/* Length of time in 'ticks' to report before stopping, only used for archived runs.	*/
	unsigned char msbyteLswordLsdwordCollectionDuration;	/* Length of time in 'ticks' to report before stopping, only used for archived runs.	*/
	unsigned char lsbyteMswordLsdwordCollectionDuration;	/* Length of time in 'ticks' to report before stopping, only used for archived runs.	*/
	unsigned char msbyteMswordLsdwordCollectionDuration;	/* Length of time in 'ticks' to report before stopping, only used for archived runs.	*/
	unsigned char lsbyteLswordMsdwordCollectionDuration;	/* Length of time in 'ticks' to report before stopping, only used for archived runs.	*/
	unsigned char msbyteLswordMsdwordCollectionDuration;	/* Length of time in 'ticks' to report before stopping, only used for archived runs.	*/
	unsigned char lsbyteMswordMsdwordCollectionDuration;	/* Length of time in 'ticks' to report before stopping, only used for archived runs.	*/
	unsigned char msbyteMswordMsdwordCollectionDuration;	/* Length of time in 'ticks' to report before stopping, only used for archived runs.	*/
	unsigned char lsbyteLswordLsdwordCollectionOffset;	/* Offset in 'ticks' of next measurement to report, only used for archived runs.	*/
	unsigned char msbyteLswordLsdwordCollectionOffset;	/* Offset in 'ticks' of next measurement to report, only used for archived runs.	*/
	unsigned char lsbyteMswordLsdwordCollectionOffset;	/* Offset in 'ticks' of next measurement to report, only used for archived runs.	*/
	unsigned char msbyteMswordLsdwordCollectionOffset;	/* Offset in 'ticks' of next measurement to report, only used for archived runs.	*/
	unsigned char lsbyteLswordMsdwordCollectionOffset;	/* Offset in 'ticks' of next measurement to report, only used for archived runs.	*/
	unsigned char msbyteLswordMsdwordCollectionOffset;	/* Offset in 'ticks' of next measurement to report, only used for archived runs.	*/
	unsigned char lsbyteMswordMsdwordCollectionOffset;	/* Offset in 'ticks' of next measurement to report, only used for archived runs.	*/
	unsigned char msbyteMswordMsdwordCollectionOffset;	/* Offset in 'ticks' of next measurement to report, only used for archived runs.	*/
} _XPACK1; 
typedef struct tagNGIOStartMeasurementsParams NGIOStartMeasurementsParams;

/***************************************************************************************************/
//NGIO_CMD_ID_SET_EDGE_DETECT_REFRESH_TIME is used to force periodic data updates for framed collections.
#define NGIO_MIN_EDGE_DETECT_REFRESH_TIME_TICKS 10000 
#define NGIO_DEF_EDGE_DETECT_REFRESH_TIME_TICKS 400000000 
struct tagNGIOSetEdgeDetectRefreshTimeParams
{
	unsigned char lsbyteLswordRefreshTime;	//in microsecond ticks.
	unsigned char msbyteLswordRefreshTime;	//in microsecond ticks.	
	unsigned char lsbyteMswordRefreshTime;	//in microsecond ticks.
	unsigned char msbyteMswordRefreshTime;	//in microsecond ticks.
} _XPACK1;//Parameter block passed into SendCmd() with NGIO_CMD_ID_SET_EDGE_DETECT_REFRESH_TIME.
typedef struct tagNGIOSetEdgeDetectRefreshTimeParams NGIOSetEdgeDetectRefreshTimeParams;
typedef NGIOSetEdgeDetectRefreshTimeParams NGIOGetEdgeDetectRefreshTimeCmdResponsePayload;

/***************************************************************************************************/
//NGIO_CMD_ID_RUN_DAQ_SELF_TEST:
struct tagNGIODAQSelfTestChannelMeasureRec
{
	unsigned char lsbyteMeasurement;
	unsigned char msbyteMeasurement;
} _XPACK1;
typedef struct tagNGIODAQSelfTestChannelMeasureRec NGIODAQSelfTestChannelMeasureRec;

struct tagNGIODAQSelfTestChannelSensorIdRec
{
	unsigned char lsbyteSensorId;
	unsigned char msbyteSensorId;
} _XPACK1; 
typedef struct tagNGIODAQSelfTestChannelSensorIdRec NGIODAQSelfTestChannelSensorIdRec;

struct tagNGIODAQSelfTestParams
{
	NGIOWriteIOConfigParams IOConfig[2];
	NGIOWriteIOParams OutputLevels[2];
} _XPACK1;
typedef struct tagNGIODAQSelfTestParams NGIODAQSelfTestParams;

struct tagNGIODAQSelfTestCmdResponsePayload
{
	NGIODAQSelfTestChannelSensorIdRec ids[NGIO_CHANNEL_ID_DIGITAL2+1];//indexed by NGIO_CHANNEL_ID_..., [0] not used
	NGIODAQSelfTestChannelMeasureRec measurements_5v[NGIO_CHANNEL_ID_BUILT_IN_TEMP+1];//indexed by NGIO_CHANNEL_ID_..., [0] not used
	NGIODAQSelfTestChannelMeasureRec measurements_10v[NGIO_CHANNEL_ID_ANALOG4+1];//indexed by NGIO_CHANNEL_ID_..., [0] not used
	unsigned char digitalIOLive[2];	//IO levels(NGIO_MASK_DGX_LINE1..NGIO_MASK_DGX_LINE4) read back for NGIO_CHANNEL_ID_DIGITAL1 and NGIO_CHANNEL_ID_DIGITAL2
} _XPACK1;
typedef struct tagNGIODAQSelfTestCmdResponsePayload NGIODAQSelfTestCmdResponsePayload;

/***************************************************************************************************/
/***************************************************************************************************/
/***************************************************************************************************/
// The following declarations are for functions that are not currently intended for public consumption.

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

typedef gtype_int32 (*P_NGIO_DEVICELISTNOTIFICATION_CALLBACK)
(
	NGIO_PTR pNotification,				//Typically ptr to NGIODevicePlugNotification.
	gtype_uint32 numBytes,				//Typically sizeof(NGIODevicePlugNotification).
	NGIO_PTR pContextInfo				//Context passed into NGIO_RegisterCallbackForDeviceListNotifications().
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
	Function Name: NGIO_RegisterCallbackForDeviceNotifications()
	
	Purpose:	Register a callback function which is invoked when notifications are received describing a state change
				on an open device. This callback function is invoked when sensors are plugged in or unplugged from the 
				specified device. 
				
				After calling NGIO_RegisterCallbackForDeviceNotifications(), the application should call
				NGIO_Device_SendCmdAndGetResponse(hDevice, NGIO_CMD_ID_ENABLE_SENSOR_ID_NOTIFICATIONS) to
				enable plug/unplug notifications for the device.

				This function is generally run on a separate thread than the one that your application runs on, and it 
				does not run on the thread that owns the device. Therefore, it is important that you make no assumptions 
				about the thread context. Furthermore, the callback function should not block for more than a couple 
				milliseconds. Typically, the callback function should post a message or set a flag, and then return.

	Return:		0 iff successful, else -1.

****************************************************************************************************************************/
typedef gtype_int32 (*P_NGIO_DEVICENOTIFICATION_CALLBACK)
(
	NGIO_PTR pNotification,				//Typically ptr to NGIOSensorIdChangeNotification.
	gtype_uint32 numBytes,				//Typically sizeof(NGIOSensorIdChangeNotification).
	gtype_uint32 persistentDeviceId,	//Uniquely identifies a device until NGIO_Uninit() is called.
										//Use NGIO_ConvertPersistentDeviceIdToDeviceHandle() to convert this to a NGIO_DEVICE_HANDLE.
	NGIO_PTR pContextInfo				//Context passed into NGIO_RegisterCallbackForDeviceNotifications().
);

NGIO_LIB_INTERFACE_DECL gtype_int32 NGIO_RegisterCallbackForDeviceNotifications(
	NGIO_DEVICE_HANDLE hDevice,
	P_NGIO_DEVICENOTIFICATION_CALLBACK notificationCallbackFunc,
	NGIO_PTR pContextInfo);

/***************************************************************************************************************************
	Function Name: NGIO_ConvertPersistentDeviceIdToDeviceHandle()
	
	Purpose:	The hDevice handle returned from NGIO_Device_Open() may get reused after NGIO_Device_Close() is called,
				so it may no longer refer to the same device after the device is closed.
				The persistentDeviceId parameter passed in to the device notification callback function is guaranteed
				to uniquely identify a device until NGIO_Uninit() is called.
				Call NGIO_ConvertPersistentDeviceIdToDeviceHandle() to convert a persistentDeviceId to an hDevice handle
				for a currently opened device. If the persistentDeviceId corresponds to a device that has been closed,
				then the function returns NULL.

	Return:		hDevice iff successful, else NULL.

****************************************************************************************************************************/
NGIO_LIB_INTERFACE_DECL NGIO_DEVICE_HANDLE NGIO_ConvertPersistentDeviceIdToDeviceHandle(
	gtype_uint32 persistentDeviceId);	//[in] Uniquely identifies a device until NGIO_Uninit() is called.

NGIO_LIB_INTERFACE_DECL gtype_int32 NGIO_Device_SetCurrentTime(		//This function is not implemented.
	NGIO_DEVICE_HANDLE hDevice,	//[in] handle to open device.
	gtype_real64 currentTime,	//[in] current time in seconds.
	gtype_uint32 timeoutMs);	//[in] # of milliseconds to wait for a reply before giving up. NGIO_TIMEOUT_MS_DEFAULT is recommended.

NGIO_LIB_INTERFACE_DECL gtype_int32 NGIO_Device_GetCurrentTime(		//This function is not implemented.
	NGIO_DEVICE_HANDLE hDevice,	//[in] handle to open device.
	gtype_real64 *pCurrentTime,	//[out] ptr to loc to store current time in seconds
	gtype_uint32 timeoutMs);	//[in] # of milliseconds to wait for a reply before giving up. NGIO_TIMEOUT_MS_DEFAULT is recommended.

/***************************************************************************************************/
//NGIO_CMD_ID_GET_ARCHIVED_RUN_IDS(not currently supported):

struct tagNGIOGetArchivedRunIdsParams
{
	unsigned char firstRunIndex;	//Retrieve id's for firstRunIndex'th run thru the (firstRunIndex + numRuns -1 )'th run.
	unsigned char numRuns;
} _XPACK1;
//typedef struct tagNGIOGetArchivedRunIdsParams NGIOGetArchivedRunIdsParams;

struct tagNGIOGetArchivedRunIdsCmdResponsePayload
{
	unsigned char numRuns;		//Can never exceed NGIOGetArchivedRunIdsParams.numRuns. 
								//If NGIOGetArchivedRunIdsCmdResponsePayload.numRuns < NGIOGetArchivedRunIdsParams.numRuns,
								//then total number of runs stored is 
								//NGIOGetArchivedRunIdsParams.firstRunIndex + NGIOGetArchivedRunIdsCmdResponsePayload.numRuns.
	unsigned char lsbyteLswordDataRunIdA;
	unsigned char msbyteLswordDataRunIdA;
	unsigned char lsbyteMswordDataRunIdA;
	unsigned char msbyteMswordDataRunIdA;
//	unsigned char lsbyteLswordDataRunIdB;
//	unsigned char msbyteLswordDataRunIdB;
//	unsigned char lsbyteMswordDataRunIdB;
//	unsigned char msbyteMswordDataRunIdB;
//   ...
} _XPACK1;
//typedef struct tagNGIOGetArchivedRunIdsCmdResponsePayload NGIOGetArchivedRunIdsCmdResponsePayload;

/***************************************************************************************************/
//NGIO_CMD_ID_DELETE_ARCHIVED_RUNS(not currently supported):

//typedef NGIOGetArchivedRunIdsCmdResponsePayload NGIODeleteArchivedRunsParams;
//(NGIODeleteArchivedRunsParams.numRuns == 0xff) => delete all archived runs.

/***************************************************************************************************/

/***************************************************************************************************/
//Notifications:
/***************************************************************************************************/
//Following notifications are sent to the callback registered with NGIO_RegisterCallbackForDeviceListNotifications():
#define NGIO_NOTIFY_TYPE_DEVICE_PLUGIN	1
#define NGIO_NOTIFY_TYPE_DEVICE_UNPLUG	2

//Following notifications are sent to the callback registered with NGIO_RegisterCallbackForDeviceNotifications():
#define NGIO_NOTIFY_TYPE_SENSOR_CHANGE	3
#define NGIO_NOTIFY_TYPE_FRAME_CAPTURED	10

struct tagNGIODevicePlugNotification
{
	unsigned char notificationType;	//NGIO_NOTIFY_TYPE_DEVICE_PLUGIN or NGIO_NOTIFY_TYPE_DEVICE_UNPLUG
	unsigned char deviceType;		//NGIO_DEVTYPE_...
	char deviceName[NGIO_MAX_SIZE_DEVICE_NAME];
	unsigned char lsbyteLswordDeviceListSignature;//This increments every time a device of deviceType is added or removed.
	unsigned char msbyteLswordDeviceListSignature;//A separate list is maintained for each known deviceType.
	unsigned char lsbyteMswordDeviceListSignature;
	unsigned char msbyteMswordDeviceListSignature;
} _XPACK1;
typedef struct tagNGIODevicePlugNotification NGIODevicePlugNotification;

struct tagNGIOSensorIdChangeNotification
{
	unsigned char notificationType;	//NGIO_NOTIFY_TYPE_SENSOR_CHANGE
	signed char channel;	//NGIO_CHANNEL_ID_ANALOG1 .. NGIO_CHANNEL_ID_DIGITAL2
	unsigned char lsbyteLswordSensorId;
	unsigned char msbyteLswordSensorId;
	unsigned char lsbyteMswordSensorId;
	unsigned char msbyteMswordSensorId;
	unsigned char lsbyteLswordChannelStateSignature;//This increments every time the Sensor Id changes on this channel.
	unsigned char msbyteLswordChannelStateSignature;
	unsigned char lsbyteMswordChannelStateSignature;
	unsigned char msbyteMswordChannelStateSignature;
} _XPACK1;
typedef struct tagNGIOSensorIdChangeNotification NGIOSensorIdChangeNotification;

#if defined (TARGET_OS_WIN)
#pragma pack(pop)
#endif

#ifdef TARGET_OS_MAC
#pragma pack()
#endif


#endif //_NGIO_SOURCE_CMDS_PRIVATE_H_

#ifndef _SKIP_COMMUNICATION_EXT_H_
#define _SKIP_COMMUNICATION_EXT_H_

/***************************************************************************************************/
// Go! Link is also known as Skip.
// Go! Temp is also known as Jonah.
// Go! Motion is also known as Cyclops.
//
// This file contains declarations for parameter and response structures used by the Skip support
// function SendCmdAndGetResponse().
/***************************************************************************************************/
#ifdef TARGET_OS_WIN
#pragma pack(push)
#pragma pack(1)
#else
#pragma options align=packed
#endif


#include "GSkipComm.h"

typedef struct
{
	unsigned char addr;
	unsigned char payload[6];
} GSkipWriteI2CMemParams; //Used with SKIP_CMD_ID_WRITE_LOCAL_NV_MEM_1BYTE .. SKIP_CMD_ID_WRITE_LOCAL_NV_MEM_6BYTES .. SKIP_CMD_ID_WRITE_REMOTE_NV_MEM_6BYTES.

typedef struct
{
	unsigned char addr;
	unsigned char count;
} GSkipReadI2CMemParams; //Parameter block passed into SendCmd() with SKIP_CMD_ID_READ_LOCAL_NV_MEM and SKIP_CMD_ID_READ_REMOTE_NV_MEM.

typedef struct
{
	unsigned char lsbyteLswordMeasurementPeriod;//Units are 'ticks'. Length of tick is different for Skip versus Jonah.
	unsigned char msbyteLswordMeasurementPeriod;
	unsigned char lsbyteMswordMeasurementPeriod;
	unsigned char msbyteMswordMeasurementPeriod;
} GSkipSetMeasurementPeriodParams; //Parameter block passed into SendCmd() with SKIP_CMD_ID_SET_MEASUREMENT_PERIOD.

// Some redundant LED declarations:
typedef enum
{
	kLEDSettings_Off,
	kLEDSettings_Red,
	kLEDSettings_Green,
	kLEDSettings_Orange
} ELEDSettings;

const unsigned char kLEDOff = SKIP_LED_COLOR_BLACK;
const unsigned char kLEDRed = SKIP_LED_COLOR_RED;
const unsigned char kLEDGreen = SKIP_LED_COLOR_GREEN;
const unsigned char kLEDOrange = SKIP_LED_COLOR_RED_GREEN;
const unsigned char kSkipMaxLedBrightness = SKIP_LED_BRIGHTNESS_MAX;
const unsigned char kSkipOrangeLedBrightness = 4;

typedef struct
{
	unsigned char color;
	unsigned char brightness;
} GSkipSetLedStateParams; //Parameter block passed into SendCmd() with SKIP_CMD_ID_SET_LED_STATE.

typedef struct
{
	unsigned char analogInputChannel; //See SKIP_ANALOG_INPUT_CHANNEL definitions.
} GSkipSetAnalogInputChannelParams;//Parameter block passed into SendCmd() with SKIP_CMD_ID_SET_ANALOG_INPUT_CHANNEL.

typedef struct
{
	char dacSetting;
} GSkipSetVinOffsetDacParams;//Parameter block passed into SendCmd() with SKIP_CMD_ID_SET_VIN_OFFSET_DAC.

typedef struct
{
	unsigned char status;
	unsigned char minorVersionMasterCPU;	//Binary coded decimal
	unsigned char majorVersionMasterCPU;	//Binary coded decimal
	unsigned char minorVersionSlaveCPU;		//Binary coded decimal - updated only by Skip, not by Jonah
	unsigned char majorVersionSlaveCPU;		//Binary coded decimal - updated only by Skip, not by Jonah
} GSkipGetStatusCmdResponsePayload; //This is the response payload returned by GetNextResponse() after sending SKIP_CMD_ID_GET_STATUS.

typedef GSkipSetLedStateParams GSkipGetLedStateCmdResponsePayload; //This is the response payload returned by GetNextResponse() after sending SKIP_CMD_ID_GET_LED_STATE.
typedef GSkipSetMeasurementPeriodParams GSkipGetMeasurementPeriodCmdResponsePayload;//This is the response payload returned by GetNextResponse() 
																					//after sending SKIP_CMD_ID_GET_MEASUREMENT_PERIOD.
typedef struct
{
	unsigned char ww;			//week in year(starting at 1) in BCD format
	unsigned char yy;			//last two digits of year in BCD format
	unsigned char lsbyteLswordSerialCounter;
	unsigned char msbyteLswordSerialCounter;
	unsigned char lsbyteMswordSerialCounter;
	unsigned char msbyteMswordSerialCounter;
} GSkipGetSerialNumberCmdResponsePayload; //This is the response payload returned by GetNextResponse() after sending SKIP_CMD_ID_GET_SERIAL_NUMBER.

typedef struct
{
	unsigned char lsbyteLswordSensorId;
	unsigned char msbyteLswordSensorId;
	unsigned char lsbyteMswordSensorId;
	unsigned char msbyteMswordSensorId;
} GSkipGetSensorIdCmdResponsePayload; //This is the response payload returned by GetNextResponse() after sending SKIP_CMD_ID_GET_SENSOR_ID.

typedef GSkipSetAnalogInputChannelParams GSkipGetAnalogInputChannelResponsePayload; //This is the response payload returned by GetNextResponse() 
//after sending SKIP_CMD_ID_GET_ANALOG_INPUT_CHANNEL.
typedef GSkipSetVinOffsetDacParams GSkipGetVinOffsetDacResponsePayload; //This is the response payload returned by GetNextResponse() 
//after sending SKIP_CMD_ID_GET_VIN_OFFSET_DAC.


#ifdef TARGET_OS_WIN
#pragma pack(pop)
#else
#pragma options align=reset
#endif

#endif //_SKIP_COMMUNICATION_EXT_H_
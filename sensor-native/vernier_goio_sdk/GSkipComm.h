#ifndef _SKIP_COMMUNICATION_H_
#define _SKIP_COMMUNICATION_H_
/***************************************************************************************************/
// Go! Link is also known as Skip.
// Go! Temp is also known as Jonah.
// Go! Motion is also known as Cyclops.
//
// Skip and Jonah use the same basic protocol. The set of commands supported by Jonah is a subset
// of the command set supported by Skip.
//
/***************************************************************************************************/
//
// The currently defined commands are:
//
#define SKIP_CMD_ID_GET_STATUS 0x10
#define SKIP_CMD_ID_WRITE_LOCAL_NV_MEM_1BYTE	0x11
#define SKIP_CMD_ID_WRITE_LOCAL_NV_MEM_2BYTES 0x12
#define SKIP_CMD_ID_WRITE_LOCAL_NV_MEM_3BYTES 0x13
#define SKIP_CMD_ID_WRITE_LOCAL_NV_MEM_4BYTES 0x14
#define SKIP_CMD_ID_WRITE_LOCAL_NV_MEM_5BYTES 0x15
#define SKIP_CMD_ID_WRITE_LOCAL_NV_MEM_6BYTES 0x16
#define SKIP_CMD_ID_READ_LOCAL_NV_MEM 0x17
#define SKIP_CMD_ID_START_MEASUREMENTS 0x18
#define SKIP_CMD_ID_STOP_MEASUREMENTS 0x19
#define SKIP_CMD_ID_INIT 0x1A
#define SKIP_CMD_ID_SET_MEASUREMENT_PERIOD 0x1B
#define SKIP_CMD_ID_GET_MEASUREMENT_PERIOD 0x1C
#define SKIP_CMD_ID_SET_LED_STATE 0x1D
#define SKIP_CMD_ID_GET_LED_STATE 0x1E
#define SKIP_CMD_ID_GET_SERIAL_NUMBER 0x20
//Commands defined above are supported by Skip, Jonah, and Cyclops, except that Cyclops does not support the serial # or the NV_MEM cmds.
//Skip extensions:
#define SKIP_CMD_ID_SET_VIN_OFFSET_DAC 0x1F
#define SKIP_CMD_ID_WRITE_REMOTE_NV_MEM_1BYTE 0x21	
#define SKIP_CMD_ID_WRITE_REMOTE_NV_MEM_2BYTES 0x22
#define SKIP_CMD_ID_WRITE_REMOTE_NV_MEM_3BYTES 0x23
#define SKIP_CMD_ID_WRITE_REMOTE_NV_MEM_4BYTES 0x24
#define SKIP_CMD_ID_WRITE_REMOTE_NV_MEM_5BYTES 0x25
#define SKIP_CMD_ID_WRITE_REMOTE_NV_MEM_6BYTES 0x26
#define SKIP_CMD_ID_READ_REMOTE_NV_MEM 0x27
#define SKIP_CMD_ID_GET_SENSOR_ID 0x28
#define SKIP_CMD_ID_SET_ANALOG_INPUT_CHANNEL 0x29
#define SKIP_CMD_ID_GET_ANALOG_INPUT_CHANNEL 0x2A
#define SKIP_CMD_ID_GET_VIN_OFFSET_DAC 0x2B
#define SKIP_CMD_ID_SPARE1 0x2C
#define SKIP_CMD_ID_SPARE2 0x2D
#define SKIP_CMD_ID_SPARE3 0x2E
#define SKIP_CMD_ID_SPARE4 0x2F
#define FIRST_SKIP_CMD_ID SKIP_CMD_ID_GET_STATUS
#define LAST_SKIP_CMD_ID SKIP_CMD_ID_SPARE4

//
/***************************************************************************************************/
//SKIP_CMD_ID_SET_LED_STATE:
//
#define SKIP_LED_COLOR_BLACK 0xC0
#define SKIP_LED_COLOR_RED 0x40
#define SKIP_LED_COLOR_GREEN 0x80
#define SKIP_LED_COLOR_RED_GREEN 0
#define SKIP_LED_BRIGHTNESS_MIN 0
#define SKIP_LED_BRIGHTNESS_MAX 0x10
//
/***************************************************************************************************/
//SKIP_CMD_ID_SET_ANALOG_INPUT_CHANNEL:
//
#define SKIP_ANALOG_INPUT_CHANNEL_VOFF 0
#define SKIP_ANALOG_INPUT_CHANNEL_VIN  1
#define SKIP_ANALOG_INPUT_CHANNEL_VIN_LOW 2
#define SKIP_ANALOG_INPUT_CHANNEL_VID  3
//
//SKIP_ANALOG_INPUT_CHANNEL_VIN is used for +/- 10 volt probes.
//SKIP_ANALOG_INPUT_CHANNEL_VIN_LOW is used for 5 volt probes.
//

#endif //_SKIP_COMMUNICATION_H_
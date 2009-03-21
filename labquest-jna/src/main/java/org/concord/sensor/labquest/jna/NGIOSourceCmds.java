package org.concord.sensor.labquest.jna;


public interface NGIOSourceCmds 
{
	public final static byte CMD_ID_GET_STATUS = 0x10;
	public final static byte CMD_ID_START_MEASUREMENTS = 0x18;
	public final static byte CMD_ID_STOP_MEASUREMENTS = 0x19;
	public final static byte CMD_ID_INIT = 0x1A;
	public final static byte CMD_ID_SET_MEASUREMENT_PERIOD = 0x1B;
	public final static byte CMD_ID_GET_MEASUREMENT_PERIOD = 0x1C;
	public final static byte CMD_ID_SET_LED_STATE = 0x1D;
	public final static byte CMD_ID_GET_LED_STATE = 0x1E;
	public final static byte CMD_ID_SET_ANALOG_INPUT = 0x21;
	public final static byte CMD_ID_GET_ANALOG_INPUT = 0x22;
	public final static byte CMD_ID_WRITE_NV_MEM = 0x26;	
	public final static byte CMD_ID_READ_NV_MEM = 0x27;
	public final static byte CMD_ID_GET_SENSOR_ID = 0x28;
	public final static byte CMD_ID_SET_SAMPLING_MODE = 0x29;
	public final static byte CMD_ID_GET_SAMPLING_MODE = 0x2A;
	public final static byte CMD_ID_SET_SENSOR_CHANNEL_ENABLE_MASK = 0x2C;
	public final static byte CMD_ID_GET_SENSOR_CHANNEL_ENABLE_MASK = 0x2D;
	public final static byte CMD_ID_SET_COLLECTION_PARAMS = 0x2E;
	public final static byte CMD_ID_GET_COLLECTION_PARAMS = 0x2F;

	public final static byte CMD_ID_GET_ARCHIVED_RUN_IDS = 0x30;
	public final static byte CMD_ID_DELETE_ARCHIVED_RUNS = 0x31;
	public final static byte CMD_ID_SET_DIGITAL_COUNTER = 0x32;
	public final static byte CMD_ID_GET_DIGITAL_COUNTER = 0x33;
	public final static byte CMD_ID_CLEAR_ERROR_FLAGS = 0x34;

	/***************************************************************************************************/

	public final static int DEFAULT_CMD_RESPONSE_TIMEOUT_MS = 1000;
	public final static int INIT_CMD_RESPONSE_TIMEOUT_MS = 2000;

	public final static byte CHANNEL_ID_TIME = 0;
	public final static byte CHANNEL_ID_ANALOG1 = 1;
	public final static byte CHANNEL_ID_ANALOG2 = 2;
	public final static byte CHANNEL_ID_ANALOG3 = 3;
	public final static byte CHANNEL_ID_ANALOG4 = 4;
	public final static byte CHANNEL_ID_DIGITAL1 = 5;
	public final static byte CHANNEL_ID_DIGITAL2 = 6;
	public final static byte CHANNEL_ID_BUILT_IN_TEMP = 7;
	public final static byte CHANNEL_ID_ANALOG_EXT1 = 8;
	public final static byte CHANNEL_ID_ANALOG_EXT2 = 9;

	public final static int MAX_NUM_CHANNELS = 8;

	public final static int MAX_NUM_DIGITAL_CHANNELS = 2;
	
	public final static byte STATUS_SUCCESS = 0;
	public final static byte STATUS_NOT_READY_FOR_NEW_CMD = 0x30;
	public final static byte STATUS_CMD_NOT_SUPPORTED = 0x31;
	public final static byte STATUS_INTERNAL_ERROR1 = 0x32;
	public final static byte STATUS_INTERNAL_ERROR2 = 0x33;
	public final static byte STATUS_ERROR_CANNOT_CHANGE_PERIOD_WHILE_COLLECTING = 0x34;
	public final static byte STATUS_ERROR_CANNOT_READ_NV_MEM_BLK_WHILE_COLLECTING_FAST = 0x35;
	public final static byte STATUS_ERROR_INVALID_PARAMETER = 0x36;
	public final static byte STATUS_ERROR_CANNOT_WRITE_FLASH_WHILE_COLLECTING = 0x37;
	public final static byte STATUS_ERROR_CANNOT_WRITE_FLASH_WHILE_HOST_FIFO_BUSY = 0x38;
	public final static byte STATUS_ERROR_OP_BLOCKED_WHILE_COLLECTING = 0x39;
	public final static byte STATUS_ERROR_CALCULATOR_CANNOT_MEASURE_WITH_NO_BATTERIES = 0x3A;
	public final static byte STATUS_ERROR_OP_NOT_SUPPORTED_IN_CURRENT_MODE = 0x3B;
	public final static byte STATUS_ERROR_AUDIO_CONTROL_FAILURE = 0x3C;
	public final static byte STATUS_ERROR_AUDIO_STREAM_FAILURE = 0x3D;

	public final static byte ANALOG_INPUT_5V_BUILTIN_12BIT_ADC = 0;
	public final static byte ANALOG_INPUT_5V_BUILTIN_10BIT_ADC = 1;
	public final static byte ANALOG_INPUT_5V_EXT_12BIT_ADC = 2;
	public final static byte ANALOG_INPUT_5V_EXT_16BIT_ADC = 3;
	public final static byte ANALOG_INPUT_PM10V_BUILTIN_12BIT_ADC = 4;
	public final static byte ANALOG_INPUT_PM10V_BUILTIN_10BIT_ADC = 5;
	public final static byte ANALOG_INPUT_PM10V_EXT_12BIT_ADC = 6;
	public final static byte ANALOG_INPUT_PM10V_EXT_16BIT_ADC = 7;
	
	public final static byte SAMPLING_MODE_PERIODIC_LEVEL_SNAPSHOT = 0;
	public final static byte SAMPLING_MODE_APERIODIC_EDGE_DETECT = 1;
	public final static byte SAMPLING_MODE_PERIODIC_PULSE_COUNT = 2;
	public final static byte SAMPLING_MODE_PERIODIC_MOTION_DETECT = 3;
	public final static byte SAMPLING_MODE_PERIODIC_ROTATION_COUNTER = 4;
	public final static byte SAMPLING_MODE_PERIODIC_ROTATION_COUNTER_X4 = 5;

	
	class NGIOStartMeasurementsParams extends NGIOStructure {
		public byte [] dataRunId = new byte[4];  // byte 0 is least significant
		public byte [] collectionDuration = new byte[8]; // byte 0 is least significant
		public byte [] collectionOffset = new byte[8]; // byte 0 is least significant
	}
	
	class NGIOSetMeasurementPeriodParams extends NGIOStructure {
		public byte channel;
		public byte [] dataRunId = new byte[4];  // byte 0 is least significant
		public byte [] measurementPeriod = new byte[4]; // byte 0 is least significant
	}
	
	class NGIOGetMeasurementPeriodParams extends NGIOStructure {
		public byte channel;
		public byte [] dataRunId = new byte[4];  // byte 0 is least significant		
	}
	
	class NGIOGetSensorIdParams extends NGIOStructure 
	{
		public byte channel;	//NGIO_CHANNEL_ID_ANALOG1 .. NGIO_CHANNEL_ID_DIGITAL2
	} 

	 //This is the response payload returned by GetNextResponse() after sending NGIO_CMD_ID_GET_SENSOR_ID.
	class NGIOGetSensorIdCmdResponsePayload extends NGIOStructure
	{
		public byte [] sensorId = new byte [4]; // byte 0 is least significant
	}
	
	class NGIOSetSensorChannelEnableMaskParams extends NGIOStructure
	{
		public byte [] enableSensorChannels = new byte[4];  // byte 0 channels 0-7, byte 1 8-15 ...
	} 

	class NGIOSetAnalogInputParams extends NGIOStructure
	{
		public byte channel; //NGIO_CHANNEL_ID_ANALOG1 ...
		
		/**
		 * This should be one of the ANALOG_INPUT_* constants
		 * NGIO_CMD_ID_INIT causes analogInput to be ANALOG_INPUT_5V_BUILTIN_12BIT_ADS
		 */
		public byte analogInput; 
	}

	class NGIOSetSamplingModeParams extends NGIOStructure
	{
		public byte channel; //NGIO_CHANNEL_ID_ANALOG1 ...
		public byte samplingMode; //NGIO_CMD_ID_INIT causes samplingMode to be NGIO_SAMPLING_MODE_PERIODIC_LEVEL_SNAPSHOT
		//for analog channels, and NGIO_SAMPLING_MODE_APERIODIC_EDGE_DETECT for digital channels.
	}
	
}

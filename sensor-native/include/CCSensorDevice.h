typedef struct
{
	char *			key;
	char *			value;
} SensorParam;

typedef struct
{
	// 1 means this sensor has been confirmed to match the requested
	// sensor.  This is usually done by reading an id from the sensor
	// if the sensor can't be confirmed then this should be 0
	// devices that don't support reading ids from sensors
	// should always return 0
	unsigned char confirmed;
	
	int 			type; // The type of sensor or quantity requested
	float 			stepSize; // The actual or maximum step size between values
							  // This is in the standard units for this type
	/**
	 * This is the port the sensor is or should be plugged into.
	 * This value ranges from 0 on up.  This value might be ignored
	 * if the ports can figure out which sensor is attached.  However the
	 * data returned in a "read" call must be in the same order as the 
	 * SensorConfig are listed in the ExperimentConfig
	 * 
	 * Also there could be more than one "sensor config" for a single
	 * port.  If the author wants distance and velocity from the same
	 * sensor.
	 * 
	 * The ports in a experiment should be continuous starting at 0.
	 * The SensorDevice implementation should assign these ports to the 
	 * first available physical port that has the correct type for the sensor.
	 * So a SensorConfig with a port number of 1 might be mapped to digital 
	 * port 0.
	 */
	int 			port;
	
	/*
	 * The name of the currently used port.  For example this could be:
	 * "Digital 1" or "A".  This value might be presented to the end user in
	 * a dialog such as [portName]: [sensorName]  
	 * So it might be "Digital 1: Distance Wheel"
	 * 
	 * So this name should be readable by a user, and not too long.
	 * must be NUL terminated
	 */
	char 			portName[64];	
	
	/*
	 * The name of the sensor attached
	 * must be NUL terminated.
	 */
	char 			name[64];
	
	/*
	 * The unit the data will be returned in
	 * This must be in a specific format:
	 * the strings for these units are 
	 * next to the quantity definitions
	 * must be NUL terminated
	 */
	char 			unitStr[32];
	
	int 			numSensorParams;
	SensorParam *	sensorParams;
} SensorConfig;

typedef struct 
{
	/*
	 *  This cofiguration is valid if this is 1
	 */
	unsigned char	valid;  
	
	/*
	 *  A NUL terminated string indicating why the configuration is invalid
	 * If there is no reason it should be NULL.
	 * The implementor of this api should create the memory 
	 * with malloc.  It will be freed by the caller.
	 */
	char *			invalidReason; 

	/*
	 * The period is seconds/sample that the data will be
	 * collected.
	 */
	float			period;

	/*
	 * The requested data read period.  This is the time
	 * between calls to SensDev_read()
	 * This should be set by the implementor of the API
	 * and it will be used by the caller.  Its units are
	 * seconds/read
	 */
	 float 			dataReadPeriod;

	/*
	 * Name of the attached device 
	 * must be null terminated
	 */
	char  			deviceName[64];
	
	/*
	 * The number of sensor configurations
	 */
	int				numSensorConfigs;
	
	/*
	 * an array of sensor configs.
	 * The implementor of this API should create this array
	 * with malloc.  The array will be freed by the caller.
	 */
	SensorConfig *	sensorConfigArray;
} ExperimentConfig;

enum QuantityType
{
	/****************************************************
	 ****************************************************
	public static int QUANTITY_UNKNOWN=             -1;
	
	// Required
	public static int QUANTITY_TEMPERATURE=			0;  // degC
	public static int QUANTITY_TEMPERATURE_WAND=    1;  // degC
	public static int QUANTITY_LIGHT=				2;  // lx
	public static int QUANTITY_GAS_PRESSURE= 		3;  // Pa
	public static int QUANTITY_VOLTAGE= 			4;  // V
	public static int QUANTITY_FORCE=				5;  // N
	public static int QUANTITY_VELOCITY=			6;  // m/s
	public static int QUANTITY_RELATIVE_HUMIDITY=	7;  // pctRH

	// Recommended
	public static int QUANTITY_ACCELERATION=		8;  // m/s^2
	public static int QUANTITY_PULSE_RATE=			9;  // beat/s
		
	// not required
	public static int QUANTITY_CURRENT=				10; // A
	public static int QUANTITY_POWER=				11; // W
	public static int QUANTITY_ENERGY=				12; // J
	
	public static int QUANTITY_DISTANCE=			13; // m
	public static int QUANTITY_SOUND_INTENSITY=		14; // dB
	public static int QUANTITY_COMPASS= 			15; // deg
	
	public static int QUANTITY_ANGULAR_VELOCITY=	16; // rad/s
	
	public static int QUANTITY_WIND_SPEED=			17; // m/s
	 *****************************************************
	 *****************************************************/
	
	// This is returned by an device if it knows a sensor
	// is attached but it doesn't know which one.
	QUANTITY_UNKNOWN = -1,
	
	// Required
	QUANTITY_TEMPERATURE,
	QUANTITY_TEMPERATURE_WAND,
	QUANTITY_LIGHT,
	QUANTITY_GAS_PRESSURE,
	QUANTITY_VOLTAGE,
	QUANTITY_FORCE,
	QUANTITY_VELOCITY,
	QUANTITY_RELATIVE_HUMIDITY,
	
	// Recommended
	QUANTITY_ACCELERATION,
	QUANTITY_PULSE_RATE,
	
	// not required
	QUANTITY_CURRENT,
	QUANTITY_POWER,
	QUANTITY_ENERGY,

	QUANTITY_DISTANCE,
	QUANTITY_SOUND_INTENSITY,
	QUANTITY_COMPASS,
		
	QUANTITY_ANGULAR_VELOCITY,
	
	QUANTITY_WIND_SPEED,
};

/*
 *  The general practice of passing structures and having the sensor device
 * create or modify those structures is probably not very good.  It might
 * be better to make these function calls.  However this requires the device
 * to keep track of these settings between function calls.  And the device
 * needs a way to pass back several settings at once.   So it seems the structs
 * are required.
 */

typedef void *SENSOR_DEVICE_HANDLE;

/*
 * This can be passed a string to configure the device.  It seems
 * most devices won't use this.  But maybe for:
 * Perhaps usb device ids, or serial port indexes,
 */
SENSOR_DEVICE_HANDLE SensDev_open(char *configString);

/*
 * return 0 if successful -1 if failed
 */
int SensDev_close(
	SENSOR_DEVICE_HANDLE hDevice // [in] handle to open device
	);

/*
 * return 1 if the device is attached 
 * otherwise return 0.
 */
int SensDev_isAttached(
	SENSOR_DEVICE_HANDLE hDevice // [in] handle to open device
	);

/*
 * This will be called before start is called.  The start method
 * should return quickly, so if there is any lengthly preperation
 * work to be done it should be done in this method
 */
int SensDev_configure(
	SENSOR_DEVICE_HANDLE hDevice, // [in] handle to open device
	ExperimentConfig *request,  // [in] requested configuration
	ExperimentConfig **response  // [out] current configuration based on request
	);

/*
 * Can this device automatically detect what sensors are attached?
 * 0 if it can't 1 if it can.
 * Even it can only tell there is sensor is attached this should return 
 * 1.  In this case the current config will have the sensor type set to unknown.
 */
int SensDev_canDetectSensors(
	SENSOR_DEVICE_HANDLE hDevice // [in] handle to open device
	);		

/*
 * Only called if there canDectectSensors returns 1.
 */	
int SensDev_getCurrentConfig(
	SENSOR_DEVICE_HANDLE hDevice, // [in] handle to open device
	ExperimentConfig **current // [out] current configuration from auto detection
	);

/*
 * return 0 if successful -1 if failed
 */
int SensDev_start(
	SENSOR_DEVICE_HANDLE hDevice // [in] handle to open device
	);		

/*
 * return 0 if successful -1 if failed
 */
int SensDev_stop(
	SENSOR_DEVICE_HANDLE hDevice // [in] handle to open device
	);		

/*
 * This method will be called after start.  The device should put converted
 * values into this buffer.  The buffer is really an array of samples, and each
 * sample can have multiple float values.
 * The number of values for each sample is the number of sensor configs passed
 * into the configure function.
 * The values should be in the units that was returned in the configure 
 * response struct.  
 */
int SensDev_read(
	SENSOR_DEVICE_HANDLE hDevice,// [in] handle to open device
	float * samples,             // [out] buffer to hold samples read in
	int length                   // [in] this is the size of the passed in buffer	
	);		

#include <stdio.h>
#include <strings.h>
#include "CCSensorDevice.h"


SENSOR_DEVICE_HANDLE SensDev_open(char *configString)
{
	// We don't need any special state for this 
	// pseudo device
	// but probably you should create some struct here
	// and store device info in it.
	// This method treats a return value of NULL 
	// as an error condition.  So we are just 
	// returning a fake handle here. 
	return (SENSOR_DEVICE_HANDLE)1;
}

/*
 * return 0 if successful -1 if failed
 */
int SensDev_close(
	SENSOR_DEVICE_HANDLE hDevice // [in] handle to open device
	)
{	
	// do nothing
	return 0;
}

/*
 * check if the device is attached.  The device will
 * be opened before this called.
 */
int SensDev_isAttached(
	SENSOR_DEVICE_HANDLE hDevice // [in] handle to open device
	)
{
	return 1;	
}

/*
 * This will be called before start is called.  The start method
 * should return quickly, so if there is any lengthly preperation
 * work to be done it should be done in this method
 */
int SensDev_configure(
	SENSOR_DEVICE_HANDLE hDevice, // [in] handle to open device
	ExperimentConfig *request,  // [in] requested configuration
	ExperimentConfig **response // [out] actual config available
	)
{
	*response = (ExperimentConfig *)malloc(sizeof(ExperimentConfig));
	if(!response) {
		return -1;
	}
		
	SensorConfig *sensConfig = (SensorConfig *)malloc(sizeof(SensorConfig));

	(*response)->sensorConfigArray = sensConfig;
	
	(*response)->numSensorConfigs = 1;
	(*response)->valid = 1;
	
	(*response)->period = request->period;

	(*response)->invalidReason = NULL;

	// grab the first sensor request
	SensorConfig *sensRequest = request->sensorConfigArray;

	// Essentially return the request that was sent in	
	sensConfig->type = sensRequest->type;
	sensConfig->confirmed = 1;
	sprintf(sensConfig->name, "Pseudo");
	sensConfig->numSensorParams = 0;
	sensConfig->port = 0;
	sensConfig->stepSize = 0.1;
	sprintf(sensConfig->unitStr, sensRequest->unitStr);
	
	return 0;
}

/*
 * Can this device automatically detect what sensors are attached?
 * 0 if it can't 1 if it can.
 * Even it can only tell there is sensor is attached this should return 
 * 1.  In this case the current config will have the sensor type set to unknown.
 */
int SensDev_canDetectSensors(
	SENSOR_DEVICE_HANDLE hDevice // [in] handle to open device
	)
{
	return 1;
}

/*
 * Only called if there canDectectSensors returns 1.
 */	
int SensDev_getCurrentConfig(
	SENSOR_DEVICE_HANDLE hDevice, // [in] handle to open device
	ExperimentConfig **current // [out] current configuration from auto detection
	)
{
	ExperimentConfig * expConfig;
	
	
	expConfig = (ExperimentConfig *)malloc(sizeof(ExperimentConfig));
	expConfig->valid = 1;
	expConfig->invalidReason = NULL;
	expConfig->period = 0.1;
	sprintf(expConfig->deviceName, "PseudoSensorDevice");
	expConfig->numSensorConfigs = 1;

	SensorConfig *sensConfig = (SensorConfig *)malloc(sizeof(SensorConfig));
	expConfig->sensorConfigArray = sensConfig;
	sprintf(sensConfig->name,"PseudoSensor1");
	sensConfig->stepSize = 0.1;
	sensConfig->type = QUANTITY_TEMPERATURE;
	*current = expConfig;
	
	return 1;
}

/*
 * return 0 if successful -1 if failed
 */
int SensDev_start(
	SENSOR_DEVICE_HANDLE hDevice // [in] handle to open device
	)
{
	return 0;
}	

/*
 * return 0 if successful -1 if failed
 */
int SensDev_stop(
	SENSOR_DEVICE_HANDLE hDevice // [in] handle to open device
	)
{
	return 0;
}

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
	)
{
	// just return a constant value for now
	samples[0] = 10;
	return 1;
}


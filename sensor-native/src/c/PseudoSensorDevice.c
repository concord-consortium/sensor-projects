#include <stdio.h>
#include <strings.h>
#include "CCSensorDevice.h"


SENSOR_DEVICE_HANDLE SensDev_open(char *configString)
{
	// We don't need any special state for this 
	// pseudo device
	return NULL;
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
	ExperimentConfig **response // [out] actually config available
	)
{
	return -1;
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
 * probably these memory things should work like bitmaps
 * I pass in a buffer and they try to use it if it is
 * not large enought they complain and I send in a bigger 
 * one.
 * We could make this a helper function too something
 * like "createConfig" that allocates all the memory and
 * returns the config.
 */	
int SensDev_getCurrentConfig(
	SENSOR_DEVICE_HANDLE hDevice, // [in] handle to open device
	ExperimentConfig **current // [out] current configuration from auto detection
	)
{
	ExperimentConfig * expConfig;
	
	
	expConfig = (ExperimentConfig *)malloc(sizeof(ExperimentConfig));
	expConfig->valid = 1;
	expConfig->rate = 1;
	expConfig->deviceName = "PseudoSensorDevice";
	expConfig->numSensorConfigs = 1;
	expConfig->sensorConfigs = (SensorConfig *)malloc(sizeof(SensorConfig));
	sprintf(expConfig->sensorConfigs[0].name,"PseudoSensor1");
	
	current[0] = expConfig;
	
	return 1;
}



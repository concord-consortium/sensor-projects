#include <stdio.h>

#include "CCSensorDevice.h"
#include "CCSensorUtils.h"

void printConfig(ExperimentConfig *expConfig)
{
	printf("ExperimentConfig: %d\n", expConfig);
	printf(" deviceName: %s\n", expConfig->deviceName);
	printf(" valid: %d\n", (int)(expConfig->valid));
	printf(" invalidReason: %s\n", expConfig->invalidReason);
	printf(" period: %f\n", expConfig->period);
	printf(" dataReadPeriod: %f\n", expConfig->dataReadPeriod);
	printf(" numSensorConfigs: %d\n", expConfig->numSensorConfigs);
	int i;
	for(i=0; i<expConfig->numSensorConfigs; i++) {
		printSensorConfig(&(expConfig->sensorConfigArray[i]));
	}
}

void printSensorConfig(SensorConfig *sensConfig)
{
	printf("  SensorConfig: %d\n", sensConfig);
	printf("   confirmed: %d\n", (int)(sensConfig->confirmed));
	printf("   name: %s\n", sensConfig->name);
	printf("   numParams: %d\n", sensConfig->numSensorParams);
	printf("   port: %d\n", sensConfig->port);
	printf("   portName: %s\n", sensConfig->portName);
	printf("   stepSize: %f\n", sensConfig->stepSize);
	printf("   type: %d\n", sensConfig->type);
	printf("   unitStr: %s\n", sensConfig->unitStr);
}

SENSOR_DEVICE_HANDLE verboseOpenDevice(char *configString)
{
	int attached = 0;
		
	// you'll need to change this if your device needs 
	// a configuration string.
	SENSOR_DEVICE_HANDLE hDevice = SensDev_open(NULL);
	
	attached = SensDev_isAttached(hDevice);
	
	printf("Device attached: %d\n", attached);
	if(!attached){
		printf("Closing because device is not attached\n");
		SensDev_close(hDevice);
		return NULL;			
	}

	return hDevice;
}

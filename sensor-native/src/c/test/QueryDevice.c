#include <stdio.h>

#include "CCSensorDevice.h"
#include "CCSensorUtils.h"

int main()
{
	int canDetectSensors = 0;
	
	SENSOR_DEVICE_HANDLE hDevice = 
		verboseOpenDevice(NULL);
	if(!hDevice) {
		return 0;
	}

	canDetectSensors = SensDev_canDetectSensors(hDevice);

	printf("Device can detect sensors: %d\n", canDetectSensors);

	if(canDetectSensors) {
		ExperimentConfig * newConfig;
		
		SensDev_getCurrentConfig(hDevice, &newConfig);
	
		printConfig(newConfig);
	}
	
	SensDev_close(hDevice);
}


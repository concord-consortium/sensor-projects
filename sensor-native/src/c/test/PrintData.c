#include <stdio.h>
#include <unistd.h>

#include "CCSensorDevice.h"
#include "CCSensorUtils.h"

int main()
{
	int attached = 0;
	int canDetectSensors = 0;
		
	// you'll need to change this if your device needs 
	// a configuration string.
	SENSOR_DEVICE_HANDLE hDevice = 
		verboseOpenDevice(NULL);
	if(!hDevice) {
		return 0;
	}

	canDetectSensors = SensDev_canDetectSensors(hDevice);
	printf("Device can detect sensors: %d\n", canDetectSensors);

	ExperimentConfig * expResponse;
	ExperimentConfig expRequest;
	SensorConfig sensRequest;

	expRequest.period = 0.1; // sec / sample
	expRequest.numSensorConfigs = 1;
	expRequest.sensorConfigArray = &sensRequest;
	
	sensRequest.type = QUANTITY_TEMPERATURE;
	sensRequest.numSensorParams = 0;
	sensRequest.port = 0;
	sensRequest.stepSize = 0.1; // degC

	SensDev_configure(hDevice, &expRequest, &expResponse);

	if(!expResponse->valid){
		printf("Sensor device responded saying request is invalid\n");
		SensDev_close(hDevice);
		return 0;	
	}
	
	SensDev_start(hDevice);
		
	float dataBuffer [200];
	float timestampBuffer [200];
	int i;
	for(i=0; i<100; i++) {
		int numValues = SensDev_read(hDevice, dataBuffer, 
			timestampBuffer, 200);
				
		int j;
		for(j=0; j<numValues; j++) {
			printf("%f\n", dataBuffer[j]);
			fflush(stdout);
		}
		
		usleep(expResponse->dataReadPeriod * 1000000);
	}			

	SensDev_stop(hDevice);
	
	SensDev_close(hDevice);
}

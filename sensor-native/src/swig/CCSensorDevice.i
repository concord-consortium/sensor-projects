%module NativeBridge
%{
#include "CCSensorDevice.h"
%}

%include "typemaps.i"

// we should use this type map so the ExperimentConfig
// and sensor config proxy classes implement the java SensorDevice
// interfaces.  This way the returned ExperimentConfigs 
// can be passed write back to the java code.
// However if a user passes in an ExperimentConfig as a request
// we'll have to copy it into the correct proxy class (ick)
// So maybe it is better to keep these seperate and just copy
// all the time.  Unless it is a requirement that the user of
// the sensor device needs to get the ExperimentConfig from the 
// device.  However this means that the state saving code can't
// create these configs.

%typemap(javainterfaces) ExperimentConfig %{
	org.concord.sensor.ExperimentConfig
%}

%typemap(javacode) ExperimentConfig %{
	public boolean isValid()
	{
		return getValid() == 1;
	}
	
	public org.concord.sensor.SensorConfig [] getSensorConfigs()
	{
		int num = getNumSensorConfigs();
		SensorConfig [] configs = new SensorConfig [num];
		for(int i=0; i<num; i++) {
			configs[i] = getSensorConfig(i);
		}
		
		return configs;
	}
%}

%typemap(javacode) SensorConfig %{
	public boolean isConfirmed()
	{
		return getConfirmed() == 1;
	}
	
	public org.concord.framework.data.DataDimension getUnit()
	{
		return new org.concord.sensor.device.SensorUnit(getUnitStr());
	}
%}

%typemap(javainterfaces) SensorConfig %{
	org.concord.sensor.SensorConfig
%}

%include "CCSensorDevice.h"

%extend ExperimentConfig {
	SensorConfig * getSensorConfig(int i)
	{
		return (self->sensorConfigArray) + i;
	}
	
	void createSensorConfigArray(int size)
	{
		self->sensorConfigArray = malloc(sizeof(SensorConfig)*size);
	}
	
	void setSensorConfig(SensorConfig *sConfig, int i)
	{
		self->sensorConfigArray[i] = *sConfig;
	}
}

%extend SensorConfig {
	char * getSensorParam(char *key)
	{
		int i;
		for(i=0; i < self->numSensorParams; i++) {
			SensorParam * param = (self->sensorParams) + i;
			if(!strcmp(param->key, key)) {
				return param->value;
			}
		}
		
		return NULL;
	}
}

// There probably is a better way to do this
// it could be done with a typemap
// or maybe this code is useful enought that it should
// go in the some support code for users of this library
%inline %{

ExperimentConfig *getCurrentConfigHelper(SENSOR_DEVICE_HANDLE hDevice)
{
	ExperimentConfig *currentConfig;
	int err = SensDev_getCurrentConfig(hDevice, &currentConfig);
	if(err) {
		return 0;  // NULL
	}
	return currentConfig;
}

ExperimentConfig *configureHelper(SENSOR_DEVICE_HANDLE hDevice, ExperimentConfig *request)
{
	ExperimentConfig *response;
	response = NULL;
	int err = SensDev_configure(hDevice, request,&response);
	if(err) {
		return 0;  // NULL
	}
	return response;	
}

%}

%include "carrays.i"
%array_functions(float, floatArray);

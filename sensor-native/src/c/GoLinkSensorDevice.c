#include <stdio.h>
#include <strings.h>
#include <math.h>

#define _MAX_PATH 256


#define assert(condition) (condition) ? 0 : \
	printf("assert: %s: line: %d", "condition",__LINE__)

//#define assert(condition) 

	

#include "CCSensorDevice.h"
#include "GoIO_DLL_interface.h"

typedef enum _GoDeviceType{
	GoDeviceType_Unknown,
	GoDeviceType_GoTemp,
	GoDeviceType_GoLink,	
	GoDeviceType_GoMotion,
} GoDeviceType;


/*
 * These are taken from page 76 of the labpro tech manual.pdf
 * I don't know if any of these values are correct.  The manual
 * only lists their resistance value not the id number.  So the 
 * resistance value is the number in the comment.
 */
#define SENSOR_ID_THEROCOUPLE		 1  // 2.2K
#define SENSOR_ID_TI_VOLTAGE		 2  // 33K
#define SENSOR_ID_CURRENT			 3  // 6.8K
#define SENSOR_ID_RESISTANCE		 4  // 3.3K: 1kOhm to 100 kOhm
#define SENSOR_ID_LONG_TEMP			 5  // 22K:  extra long temp sensor degC
#define SENSOR_ID_CO2				 6  // 68K:  PPM 0 to 5000 ppm
#define SENSOR_ID_OXYGEN			 7  // 100K: PCT 0 to 27%
#define SENSOR_ID_CV_VOLTAGE		 8  // 150K: volts
#define SENSOR_ID_CV_CURRENT 		 9  // 220K: amps
#define SENSOR_ID_TEMPERATURE_C 	10  // 10K:  verified for fast response probe
#define SENSOR_ID_TEMPERATURE_F 	11  // 15K:
#define SENSOR_ID_LIGHT 			12  // 4.7K: verified for light sensor
#define SENSOR_ID_HEART_RATE		13  // 1K:   BPM 
#define SENSOR_ID_VOLTAGE			14  // 47K: 
#define SENSOR_ID_EKG				15  // 1.5K:

/*
 * smart sensors
 */
#define SENSOR_ID_GASS_PRESSURE     24
#define SENSOR_ID_DUAL_R_FORCE_10   25
#define SENSOR_ID_DUAL_R_FORCE_50   26
#define SENSOR_ID_GO_TEMP           60 
#define SENSOR_ID_GO_MOTION         69 

/*
 * This is not how this is supposed to be done
 * but I just want to get this working.
 * perhaps we don't even need a handle if we
 * are only going to have one device or compound device
 * attached at once.
 * 
 * currently this only supports one golink or temp
 */
typedef struct {
	GOIO_SENSOR_HANDLE goHandle;	
	GoDeviceType deviceType;
	unsigned char sensorID;
	float (*calibrationFunct) (float);
} GO_STATE; 

void open_go(GO_STATE *state);
void close_go(GOIO_SENSOR_HANDLE goHandle);
int configure_sensor(GO_STATE *state, SensorConfig *request,
	SensorConfig *sensConfig);
float calibrate_temp(float voltage);
float calibrate_illum(float voltage);
float calibrate_rel_hum(float voltage);
float calibrate_student_force(float voltage);

SENSOR_DEVICE_HANDLE SensDev_open(char *configString)
{
	printf("SensDev_open\n");
	
	if(GoIO_Init() != 0) {		
		printf("  can't init go_io, You have another program using the Go device\n");
	} else {
		printf("  successfully called GoIO_Init\n");
	}
	
	void * stateMem = (void *)malloc(sizeof(GO_STATE));
	if(!stateMem) {
		printf("  error allocating memory\n");	
	}
	
	GO_STATE *state = (GO_STATE *)stateMem; 
	state->goHandle = NULL;
	
	// We don't need any special state for this 
	// pseudo device

	return (void *)state;
}

/*
 * return 0 if successful -1 if failed
 */
int SensDev_close(
	SENSOR_DEVICE_HANDLE hDevice // [in] handle to open device
	)
{	
	printf("Closing device\n");

	GO_STATE *state = (GO_STATE *)hDevice; 
	
	if(state->goHandle != NULL) {
		close_go(state->goHandle);
		state->goHandle = NULL;	
	}
	
	if(GoIO_Uninit() != 0) {
		printf("Can't uninit the goio");	
	}
	
	// do nothing
	return 0;
}

void close_go(GOIO_SENSOR_HANDLE goHandle)
{
	printf("close_go\n");	
	assert(goHandle != NULL);
	
	int err = GoIO_Sensor_Lock(goHandle, 0);
	if(err) {
		printf("  Can't lock sensor before closing it");
	}
		
	GoIO_Sensor_Close(goHandle);	
	
}

/*
 * for now this returns 1 if there is a device attached
 * otherwise it returns 0
 */
int SensDev_isAttached(
	SENSOR_DEVICE_HANDLE hDevice // [in] handle to open device
	)
{
	GO_STATE *state = (GO_STATE *)hDevice; 

	int numDevices = 
		GoIO_UpdateListOfAvailableDevices(VERNIER_DEFAULT_VENDOR_ID,
			SKIP_DEFAULT_PRODUCT_ID);

	if(numDevices > 0) return 1;
	
	numDevices =
		GoIO_UpdateListOfAvailableDevices(VERNIER_DEFAULT_VENDOR_ID,
			USB_DIRECT_TEMP_DEFAULT_PRODUCT_ID);

	if(numDevices > 0) return 1;
		
	numDevices = 
		GoIO_UpdateListOfAvailableDevices(VERNIER_DEFAULT_VENDOR_ID,
			CYCLOPS_DEFAULT_PRODUCT_ID);
			
	if(numDevices > 0) return 1;
				
	return 0;	
}

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
	GO_STATE *state = (GO_STATE *)hDevice; 
	
	float period = -1.0;
	
	if(state->goHandle == NULL) {
		open_go(state);	
	}
	
	if(state->goHandle == NULL) {
		return -1;
	}
	
	ExperimentConfig * expConfig;
	
	
	expConfig = (ExperimentConfig *)malloc(sizeof(ExperimentConfig));
	expConfig->valid = 1;
	expConfig->invalidReason = NULL;
	
	period = GoIO_Sensor_GetMeasurementPeriod(state->goHandle, 
		SKIP_TIMEOUT_MS_DEFAULT);
		
	printf("sensor measurement period: %f\n", period);
		
	expConfig->period = period;
	expConfig->exactPeriod = 1;
	expConfig->dataReadPeriod = 0.1;
	
	switch(state->deviceType){
		case GoDeviceType_GoLink:
			sprintf(expConfig->deviceName, "GoLinkSensorDevice");
			break;
		case GoDeviceType_GoTemp:
			sprintf(expConfig->deviceName,"GoTempSensorDevice");
			break;
		default:
			sprintf(expConfig->deviceName, "Unknown device type");
	}
	
	expConfig->numSensorConfigs = 1;
	SensorConfig *sensConfig = (SensorConfig *)malloc(sizeof(SensorConfig));
	expConfig->sensorConfigArray = sensConfig;
	
	configure_sensor(state, NULL, sensConfig);
		
	current[0] = expConfig;
	
	return 1;
}

int configure_sensor(GO_STATE *state, SensorConfig *request, SensorConfig *sensConfig)
{
	printf("configure_sensor\n");
	int err = 0;
	GSensorDDSRec ddsRec;
	
	if(state->goHandle == NULL) {
		printf("  configure_sensor: sensor handle is null\n");
		return 0;
	}

	err = GoIO_Sensor_DDSMem_GetRecord(state->goHandle,
		&ddsRec);
	if(err) {
		printf("  error getting sensor info: %d\n", err);
		return 0;
	}
		
	int valid = 0;	
		
	printf("  attached sensor number: %d\n", (int)(ddsRec.SensorNumber));
	if(request) {
		printf("  requested sensor id: %d\n", request->type);
		printf("  requested sensor requiredMax: %f\n", request->requiredMax);
		printf("  requested sensor requiredMax isnan: %d\n", isnan(request->requiredMax));
	}
	printf("  sensor long name: %s\n", ddsRec.SensorLongName);
	printf("  sensor short name: %s\n", ddsRec.SensorShortName);
	printf("  sensor activeCalPage: %d\n", ddsRec.ActiveCalPage);
	if(ddsRec.ActiveCalPage > 2) {
		// try this and see what we get ???
		// I suppose this also means the calibration code
		// won't work. 
		ddsRec.ActiveCalPage = 0;
		
	}
	printf("  sensor units: %.7s\n", ddsRec.CalibrationPage[ddsRec.ActiveCalPage].Units);
	printf("  sensor current: %d\n", (int)ddsRec.CurrentRequirement);
	
	if(ddsRec.SensorNumber >= 20) {
		sensConfig->confirmed = 1;
		
		// populate the sensor information from the ddsRec
		// however there still needs to be a mapping from
		// the native id to the cc quantity id
		sprintf(sensConfig->name, ddsRec.SensorLongName);
		state->calibrationFunct = NULL;
		switch(ddsRec.SensorNumber){
			case SENSOR_ID_GASS_PRESSURE:
				if(request &&
					request->type == QUANTITY_GAS_PRESSURE) {
					valid = 1;
				}
				sprintf(sensConfig->unitStr, "Pa");
				sensConfig->type = QUANTITY_GAS_PRESSURE;
				sensConfig->stepSize = 0.01; // FIXME: this is a hack we should be able calc this
				break;
			case SENSOR_ID_DUAL_R_FORCE_10:
				if(request &&
					request->type == QUANTITY_FORCE &&
					(isnan(request->requiredMax) ||
					  request->requiredMax <= 10.0)) {
					valid = 1;
				}
				sprintf(sensConfig->unitStr, "N");
				sensConfig->type = QUANTITY_FORCE;
				sensConfig->stepSize = 0.01;
				break;
			
			case SENSOR_ID_DUAL_R_FORCE_50:
				if(request &&
					request->type == QUANTITY_FORCE &&
					request->stepSize >= 0.05) {
					valid = 1;
				}
				sprintf(sensConfig->unitStr, "N");
				sensConfig->type = QUANTITY_FORCE;
				sensConfig->stepSize = 0.05;
				break;
				
			case SENSOR_ID_GO_TEMP:
				if(request &&
					(request->type == QUANTITY_TEMPERATURE ||
					 request->type == QUANTITY_TEMPERATURE_WAND)){
					 valid = 1;
				}
				sprintf(sensConfig->unitStr, "degC");
				sensConfig->type = QUANTITY_TEMPERATURE_WAND;
				sensConfig->stepSize = 0.01;
				break;
			case SENSOR_ID_GO_MOTION:
				if(request &&
					(request->type == QUANTITY_VELOCITY)){
					 valid = 1;
				}
				sprintf(sensConfig->unitStr, "m");
				sensConfig->type = QUANTITY_VELOCITY;
				sensConfig->stepSize = 0.01;
				break;
				
		}	
				
	} else if(ddsRec.SensorNumber != 0) {
		sensConfig->confirmed = 1;
		
		// do a lookup from our list of known sensors and calibrations	
		printf("  current attached sensor: %d\n", ddsRec.SensorNumber);

		switch(ddsRec.SensorNumber){
			case SENSOR_ID_TEMPERATURE_C:
				if(request &&
					request->type == QUANTITY_TEMPERATURE) {
					valid = 1;
				}
				sprintf(sensConfig->unitStr, "degC");
				sprintf(sensConfig->name, "Temperature");
				sensConfig->type = QUANTITY_TEMPERATURE;			
				sensConfig->stepSize = 0.01; // FIXME: this is a hack we should be able calc this
				state->calibrationFunct = calibrate_temp;
				break;
			case SENSOR_ID_THEROCOUPLE:
				if(request &&
					request->type == QUANTITY_TEMPERATURE) {
					valid = 1;
				}
				sprintf(sensConfig->unitStr, "degC");
				sprintf(sensConfig->name, "Temperature");
				sensConfig->type = QUANTITY_TEMPERATURE;			
				sensConfig->stepSize = 0.01; // FIXME: this is a hack we should be able calc this
				state->calibrationFunct = calibrate_temp;
				break;
			case SENSOR_ID_LIGHT:
				if(request &&
					request->type == QUANTITY_LIGHT) {
					valid = 1;
				}
				sprintf(sensConfig->unitStr, "lux");
				sprintf(sensConfig->name, "Illumaninace");
				sensConfig->type = QUANTITY_LIGHT;			
				sensConfig->stepSize = 2;
				state->calibrationFunct = calibrate_illum;
				break;			
			case SENSOR_ID_TI_VOLTAGE:			
			case SENSOR_ID_CURRENT:
			case SENSOR_ID_RESISTANCE:
			case SENSOR_ID_LONG_TEMP:
			case SENSOR_ID_CO2:
			case SENSOR_ID_OXYGEN:
			case SENSOR_ID_CV_VOLTAGE:
			case SENSOR_ID_CV_CURRENT:
			case SENSOR_ID_TEMPERATURE_F:
			case SENSOR_ID_HEART_RATE:
			case SENSOR_ID_VOLTAGE:
			case SENSOR_ID_EKG:
				break;
			default:
				printf("Unknown sensor id: %d", (int)ddsRec.SensorNumber);
		}
	} else {
			sensConfig->confirmed = 0;
			
			// This is not an auto id sensor
			// as long as there is only one sensor that matches 
			// the requested quantity type.  If not then
			// we are going to have problems.  The api breaks
			// down here.  Lets cross our fingers and hope we don't
			// have to deal with that.
			if(!request) {
				// we need a request to determine what calibration
				// to use.
				return 0;
			}
			switch(request->type){
			case QUANTITY_RELATIVE_HUMIDITY:
				valid = 1;
				sprintf(sensConfig->unitStr, "%RH");
				sprintf(sensConfig->name, "Relative Humidity");
				sensConfig->type = QUANTITY_RELATIVE_HUMIDITY;			
				sensConfig->stepSize = 0.04;
				// probably we should start using 
				// goio_calibrations.
				// however I think this is now just a demo
				// because a better implementation would be to 
				// use java.
				state->calibrationFunct = calibrate_rel_hum;
				break;
			case QUANTITY_FORCE:
				// if we are here it means they are using
				// a student force sensor
				valid = 1;
				sprintf(sensConfig->unitStr, "N");
				sprintf(sensConfig->name, "Force");
				sensConfig->type = QUANTITY_FORCE;			
				sensConfig->stepSize = 0.02;
				// probably we should start using 
				// goio_calibrations.
				// however I think this is now just a demo
				// because a better implementation would be to 
				// use java.
				state->calibrationFunct = calibrate_student_force;
				break;						
		}
		
		
	}

	return valid;

/*	
	err = GoIO_Sensor_DDSMem_ReadRecord(state.goHandle, 0, SKIP_TIMEOUT_MS_READ_DDSMEMBLOCK);
	if(err) {
		printf("error readding ddsmem: %d\n", err);
		return;
	}
*/
}

void open_go(GO_STATE *state)
{
	printf("open_go\n");
	int deviceProductId = -1;
	char deviceName [GOIO_MAX_SIZE_DEVICE_NAME];
	GOIO_SENSOR_HANDLE goHandle = NULL;
	GoDeviceType devType = GoDeviceType_Unknown;
	
	assert(state->goHandle == NULL);
	
	int numDevices = 
		GoIO_UpdateListOfAvailableDevices(VERNIER_DEFAULT_VENDOR_ID,
			SKIP_DEFAULT_PRODUCT_ID);

	if(numDevices > 0) {
		deviceProductId = SKIP_DEFAULT_PRODUCT_ID;
		devType = GoDeviceType_GoLink;
	} else {
		numDevices =
			GoIO_UpdateListOfAvailableDevices(VERNIER_DEFAULT_VENDOR_ID,
				USB_DIRECT_TEMP_DEFAULT_PRODUCT_ID);
	}	
	
	if(numDevices > 0) {
		deviceProductId = USB_DIRECT_TEMP_DEFAULT_PRODUCT_ID;
		devType = GoDeviceType_GoTemp;
	} else {
		numDevices =
			GoIO_UpdateListOfAvailableDevices(VERNIER_DEFAULT_VENDOR_ID,
				CYCLOPS_DEFAULT_PRODUCT_ID);
	}		
	
	if(numDevices > 0) {
		deviceProductId = CYCLOPS_DEFAULT_PRODUCT_ID;
		devType = GoDeviceType_GoMotion;
	}
	
	if(GoIO_GetNthAvailableDeviceName(deviceName, GOIO_MAX_SIZE_DEVICE_NAME, 
		VERNIER_DEFAULT_VENDOR_ID, deviceProductId, 0)) {
		// error getting device name
		printf("  error getting device name");
		return;			
	}
	
	printf("  device address: %s\n", deviceName);
	
	goHandle = GoIO_Sensor_Open(deviceName, VERNIER_DEFAULT_VENDOR_ID,
		deviceProductId, 1);
	
	if(goHandle == NULL) {
		printf("  error opening device\n");
		return;
	}

	printf("  goHandle: %d\n", goHandle);
		
	state->goHandle = goHandle;
	state->deviceType = devType;
	int err;
	
	int lockNum = 1;
	int unlockAttempts = 0;
	while(lockNum > 0 && unlockAttempts < 10){
		lockNum = GoIO_Sensor_Unlock(goHandle);
		unlockAttempts++;
	}
	printf("  Unlocked the device %d times\n", unlockAttempts);
	
	if(lockNum) {
		printf("  Cannot unlock the device. error %d\n", lockNum);
	}
		
	err = GoIO_Sensor_DDSMem_GetSensorNumber(goHandle, &(state->sensorID), 
		1, SKIP_TIMEOUT_MS_DEFAULT);
	if(err) {
		printf("  error getting sensor id");
	}
	
	return;	
}

int SensDev_configure(SENSOR_DEVICE_HANDLE hDevice,
	ExperimentConfig *request,
	ExperimentConfig **response)
{
	printf("SensDev_configure\n");
	GO_STATE *state = (GO_STATE *)hDevice; 
	
	if(state->goHandle != NULL){
		close_go(state->goHandle);
		state->goHandle = NULL;
	}
	
	open_go(state);

	printf("  go-link opened\n");

	*response = (ExperimentConfig *)malloc(sizeof(ExperimentConfig));
	if(!response) {
		return -1;
	}
	
	SensorConfig *sensConfig = (SensorConfig *)malloc(sizeof(SensorConfig));

	(*response)->sensorConfigArray = sensConfig;
	
	(*response)->numSensorConfigs = 1;
	(*response)->valid = 1;

	(*response)->period = GoIO_Sensor_GetMeasurementPeriod(state->goHandle, 
		SKIP_TIMEOUT_MS_DEFAULT);
	printf("  goio period: %f\n", (*response)->period);

	(*response)->exactPeriod = 1;

	(*response)->dataReadPeriod = 0.1;
				
	(*response)->valid = configure_sensor(state, request->sensorConfigArray, sensConfig);

	(*response)->invalidReason = NULL;
	/*
	if(!((*response)->valid)){		
		sprintf((*response)->invalidReason, "Attached sensor doesn't match");	
	}
	*/
	
	// we shoulds adjust the time here at least and do our best to 
	// figure out if the sensors they requests are available.
	return 0;
}
	
int SensDev_start(SENSOR_DEVICE_HANDLE hDevice)
{
	printf("SensDev_start\n");
	GO_STATE *state = (GO_STATE *)hDevice; 

	printf("  goHandle %d\n", state->goHandle);

	int err;
	
	err = GoIO_Sensor_ClearIO(state->goHandle);
	if(err) {
		printf("  error clearing sensor io: %d\n", err);
		// if we get an error when we clear then I guess we either ignore
		// it, or we call getnumber of measurements available.
		// return;
	}
	
	err = GoIO_Sensor_SendCmdAndGetResponse(state->goHandle, 
		SKIP_CMD_ID_START_MEASUREMENTS, NULL,
		0, NULL, NULL,
		SKIP_TIMEOUT_MS_DEFAULT);
	
	printf("  started measurements\n");
		
	if(err) {
		printf("  error starting measurements: %d\n", err);
		return;
	}
		
	return 0;
}

int SensDev_stop(SENSOR_DEVICE_HANDLE hDevice)
{
	printf("SensDev_stop\n");
	
	GO_STATE *state = (GO_STATE *)hDevice; 
	
	int err;
	
	err = GoIO_Sensor_SendCmdAndGetResponse(state->goHandle, 
		SKIP_CMD_ID_STOP_MEASUREMENTS, NULL,
		0, NULL, NULL,
		SKIP_TIMEOUT_MS_DEFAULT);
		
	if(err) {
		printf("  error stoping measurements: %d\n", err);
		return;
	}

	return 0;
}

int SensDev_read(SENSOR_DEVICE_HANDLE hDevice, 
	float *buffer, float *timestamps, int size)
{
	GO_STATE *state = (GO_STATE *)hDevice; 
	
	// should be a multiple of 6
	gtype_int32 raw [102];
	int err = -1;
	
	if(size < 102) {
		// FIXME
		printf("SensDev_read: error size is less than my interal buffer size, fix this\n");
		return -1;
	}
	
	char deviceName [255];
	gtype_int32 vendorId;
	gtype_int32 productId;
		
	int numRawValues = GoIO_Sensor_ReadRawMeasurements(state->goHandle, raw, 102);

	if(numRawValues < 0) {
		printf("SensDev_read: error reading raw measurements: %d\n", err);
		return -1;
	}	
	int i=0;
	for(i=0; i<numRawValues; i++) {
		gtype_real64 voltage = GoIO_Sensor_ConvertToVoltage(state->goHandle, raw[i]);
		gtype_real64 physicalValue;
		if(state->sensorID >= 20) {
			physicalValue = GoIO_Sensor_CalibrateData(state->goHandle, voltage);
		} else {
			physicalValue = state->calibrationFunct(voltage);
		}		
		buffer[i] = (float)physicalValue;		
	}
	
	return i;
}

/*
 * First get the R of the sensor:
 * V0 = measured voltage
 * Vres = reference voltage 5V
 * Rknown = resistance of Vres
 * V1 = voltage we measured
 * Rsensor = V0*Rknown/(Vres-V0)
 * this equation comes from the standard voltage division
 * equation.
 * 
 * Now with the resistance the equation for the temp in
 * degC is:
 * T (degC) = 1/(K0 + K1*ln(1000*R) + K2*ln(1000*R)^3) - 273.15
 * K0 = 1.02119E-3
 * K1 = 2.22468E-4
 * K2 = 1.33342E-7
 */
#define TEMP_K0 1.02119E-3
#define TEMP_K1 2.22468E-4
#define TEMP_K2 1.33342E-7
float calibrate_temp(float voltage)
{
	float R = voltage*15/(5-voltage);
	
	float lnR = log(1000*R);

	return 1.0 /(TEMP_K0 + TEMP_K1*lnR + TEMP_K2*lnR*lnR*lnR) - 273.15;
}

/*
 * From the vernier light sensor booklet.
 */ 
#define ILLUM_B0 5.0E-3  // most sensitive switch position
#define ILLUM_B1 4.5E-4  // middle switch position
#define ILLUM_B2 2.0E-5  // least sensitive (outdoor) position
float calibrate_illum(float voltage)
{
	// The only sensor I have doesn't have a switch
	// so I'm going to guess it is in the middle position
	return voltage/ILLUM_B1;
}

#define REL_HUM_A -23.8
#define REL_HUM_B 32.9
float calibrate_rel_hum(float voltage)
{
	return REL_HUM_B*voltage + REL_HUM_A;
} 

#define STUDENT_FORCE_A 9.8
#define STUDENT_FORCE_B -9.8
float calibrate_student_force(float voltage)
{
	return STUDENT_FORCE_B*voltage + STUDENT_FORCE_A;
} 


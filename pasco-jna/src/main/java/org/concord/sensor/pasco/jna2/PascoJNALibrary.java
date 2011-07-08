package org.concord.sensor.pasco.jna2;

import com.sun.jna.Library;

public interface PascoJNALibrary extends Library {
	/**
	Allocate and initialize:
	Returns a handle, use as first arg for all functions.
	*/
    int PasInit();

	/**
	Start it:
	*/
    int PasStart(int handle);

	/**
	Stop it:
	*/
	int PasStop(int handle);

	/**
	Free it:
	*/
	int PasDelete(int handle);

	/**
	Get all open usb devices
	Returns # devices, 0 - N, -1 error
	*/
	int PasGetDevices(int handle, int[] arr, int arrsiz);

	/**
	Get the number of channels for a device (round ports)
	Returns # channels, 0-N, -1 device not found, -2 other (worse) 
	*/
	int PasGetNumChannels(int handle, int device);

	/**
	Does a channel exist? (sensor plugged in)
	Returns # 1 yes, 0 no, -1 error 
	*/
	int PasGetExistChannel(int handle, int device, int channel);


	/**
	Get sample size for a channel (sensor) 
	Returns sample size in bytes,0-N.
	-1 if no sensor (channel) attached (to a round port on the HW).
	-2 if error
	*/
	int PasGetSampleSize(int handle, int device, int channel);


	/**
	Family of three functions: 
		Min, Default and Max data rates a sensor can support.

	Get sample rates for a channel (sensor) 
	Returns rate 0-N samples per second. 
	-1 if no sensor (channel) attached (to a round port on the HW).
	-2 if error
	*/
	int PasGetSampleRateMinimum(int handle, int device, int channel);

	int PasGetSampleRateMaximum(int handle, int device, int channel);

	int PasGetSampleRateDefault(int handle, int device, int channel);


	/**
	Get the name of a channel (sensor)
	Returns string length, 0-N 
	-1 if no sensor (channel) attached (to a round port on the HW).
	-2 if buffer to short

	Input:
	buf    --buffer to fill in with name
	bufsiz --size of buffer

	Output:
	buf	  --filled with a string

	*/	
	int PasGetName(int handle, int device, int channel, byte[] buf, int bufsiz);



	/**
	Get a single sample.
	Returns sample length, 0-N 
	-1 if no sensor (channel) attached (to a round port on the HW).
	-2 if buffer to short

	Input:
	buf    --buffer to fill in with name
	bufsiz --size of buffer

	Output:
	buf	  --filled with a sample

	*/
	int PasGetOneSample(int handle, int device, int channel, byte[]buffer, int bufsiz);


	/*
	********************************************************************
	Continuous Sampling
	1) Initialize & start:          PasStartContinuousSampling()
	2) Call N times to get samples:    PasGetSampleData()
	3) Stop:                        PasStopContinuousSampling()
	
	NOTE: Do not call any other API functions between Start() and Stop() 
	********************************************************************
	*/

	/**
	Start Continuous Sampling

	Returns sample length, 0-N 
	-1 if no sensor (channel) attached (to a round port on the HW).
	-2 if no USB device

	Input:
	period -- sampling period in msec. NOTE: Each sensor has its own operating range.

	*/
	int PasStartContinuousSampling(int handle, int device, int channel,int period);


	/**
	Get samples.
	Returns  --number of samples 0-N
	-1 if no sensor attached

	Input:
	buf      --buffer to fill in with data
	count    --max samples to get
	sampsize --sample size
	
	Output:
	buf	     --filled with samples

	*/
	int PasGetSampleData (int handle, int device, int channel, int sampsize, byte[] buf, int count);


	/**
	Stop Continuous Sampling

	*/
	int PasStopContinuousSampling (int handle, int device, int channel);


	int GetSensorDataSheetSize(int handle, int device, int channel);
	
	int ReadSensorDataSheet(int handle, int device, int channel, byte[] dataSheetBuffer, int dataSheetSize);
	
	/******************
	Utility functions:
	*******************
	*/

	/**
	sleep milliseconds 
	*/
	void PasMSsleep(int sleep); 
}

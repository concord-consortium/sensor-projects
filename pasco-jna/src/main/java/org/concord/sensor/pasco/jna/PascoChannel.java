package org.concord.sensor.pasco.jna;

public class PascoChannel {

	private PascoDevice device;
	private PascoLibrary library;
	private PascoJNALibrary jnaLib;
	private int channel;


	public PascoChannel(PascoDevice pascoDevice, int channel) {
		this.device = pascoDevice;
		this.channel = channel;
		library = device.getLibary();
		jnaLib = library.getJNALibrary();
	}

	
	/**
	Does a channel exist?
	*/
	public boolean getExist() throws PascoException {
		int exists = jnaLib.PasGetExistChannel(library.getHandle(), device.getHandle(), channel);
		if(exists < 0){
			throwException("Error checking if sensor is attached", exists);
		}
		if(exists == 0){
			return false;
		} else if (exists == 1){
			return true;
		} else {
			throwException("Unexpected return value from PasGetExistsChannel", exists);
		}
		
		return false;
	}

	public boolean getSensorDetected() throws PascoException {
		int detected = jnaLib.PasGetSensorDetected(library.getHandle(), device.getHandle(), channel);
		if(detected < 0){
			throwException("Error checking if sensor is attached", detected);
		}
		if(detected == 0){
			return false;
		} else if (detected == 1){
			return true;
		} else {
			throwException("Unexpected return value from PasGetSensorDetected", detected);
		}
		
		return false;		
	}
	
	/**
	Get sample size for a channel (sensor) 
	Returns sample size in bytes,0-N.

    @throws PascoNoSensorAttachedException if no sensor is attached
    @throws PascoException if some other error occurs
	*/
	public int getSampleSize() throws PascoException {
		int size = jnaLib.PasGetSampleSize(library.getHandle(), device.getHandle(), channel);
		if(size == -1){
			throw new PascoNoSensorAttachedException(this);
		} else if (size < -1) {
			throwException("Error getting sample size", size);
		} 
		
		return size;
	}


	/**
	Family of three functions: 
		Min, Default and Max data rates a sensor can support.

	Get sample rates for a channel (sensor) 
	Returns rate 0-N samples per second. 
	
	MSB Flag: 0 = Hz; 1 = Sec
	
    @throws PascoNoSensorAttachedException if no sensor is attached
    @throws PascoException if some other error occurs
	*/
	public int getSampleRateMinimum() throws PascoException {
		int rate = jnaLib.PasGetSampleRateMinimum(library.getHandle(), device.getHandle(), channel);
		if(rate == -1){
			throw new PascoNoSensorAttachedException(this);
		} else if (rate < -1 && rate > -10) {
			throwException("Error getting sample rate", rate);
		} 
		
		return rate;
	}
	
	public int getSampleRateMaximum() throws PascoException {
		int rate = jnaLib.PasGetSampleRateMaximum(library.getHandle(), device.getHandle(), channel);
		if(rate == -1){
			throw new PascoNoSensorAttachedException(this);
		} else if (rate < -1 && rate > -10) {
			throwException("Error getting sample rate", rate);
		} 
		
		return rate;
		
	}

	public int getSampleRateDefault() throws PascoException {
		int rate = jnaLib.PasGetSampleRateDefault(library.getHandle(), device.getHandle(), channel);
		if(rate == -1){
			throw new PascoNoSensorAttachedException(this);
		} else if (rate < -1 && rate > -10) {
			throwException("Error getting sample rate", rate);
		} 
		
		return rate;
		
	}

	/**
	 * return rate in seconds per sample or Hz
	 * @param rate
	 * @return
	 */
	public static float convertRate(int rate) {
    	int val = rate & 0x7FFFFFFF; 
		
    	if((rate & 0x80000000) == 0) {
    		return 1 / (float)val;
    	} else {
    		return val;
    	}	
	}
	
	/**
	Get the name of a channel (sensor)

    @throws PascoNoSensorAttachedException if no sensor is attached
    @throws PascoException if some other error occurs
	*/	
	public String getName() throws PascoException {
		byte [] buf = new byte[256];
		int length = 0;
		for(int i=0; i<5; i++){
			length = fillNameBuffer(buf);
			if(length >= 0){
				break;
			}
			buf = new byte[buf.length*2];
		}
		
		if(length < 0){
			// still don't have a buffer large enough or some other unexpected error happened
			throwException("Error getting name of sensor", length);
		}
				
		return new String(buf, 0, length);
	}
	
	private int fillNameBuffer(byte [] buf) throws PascoException{
		int length = jnaLib.PasGetName(library.getHandle(), device.getHandle(), channel, buf, buf.length);
		
		// handle errors
		if(length == -1){
			throw new PascoNoSensorAttachedException(this);
		} else if(length == -2){
			// the string wasn't long enough so just return the length so we can try again
		} else if(length < -2) {
			throwException("Error getting name of sensor", length);
		}

		return length;
	}
	
	/**
	Get a single sample.
	Returns sample length, 0-N 
	-2 if buffer to short

	Input:
	buf    --buffer to fill in with name
	bufsiz --size of buffer

	Output:
	buf	  --filled with a sample

	*/
	public int getOneSample(byte []buffer) throws PascoException {
		int size = jnaLib.PasGetOneSample(library.getHandle(), device.getHandle(), channel, buffer, buffer.length);
		if(size == -1){
			throw new PascoNoSensorAttachedException(this);
		} else if (size == -2) {
			return size;
		} else if (size < -2){
			throwException("Error reading one sample", size);
		} 
		
		return size;
	}


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

	Input:
	period -- sampling period in msec. NOTE: Each sensor has its own operating range.
    @throws PascoNoSensorAttachedException if no sensor is attached
    @throws PascoException if some other error occurs

	*/
	public int startContinuousSampling(int period) throws PascoException{
		int length = jnaLib.PasStartContinuousSampling(library.getHandle(), device.getHandle(), channel, period);
		if(length == -1){
			throw new PascoNoSensorAttachedException(this);
		} else if (length < -1) {
			throwException("Error starting sampling", length);
		} 
		
		return length;
	}


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
	public int getSampleData(int sampsize, byte []buf, int count) throws PascoException {
	  	int number = jnaLib.PasGetSampleData(library.getHandle(), device.getHandle(), channel, sampsize, buf, count);
	  	
		if(number == -1){
			throw new PascoNoSensorAttachedException(this);
		} else if (number < -1) {
			throwException("Error getting sample data", number);
		} 
	  	
	  	return number;
	}

	/**
	Stop Continuous Sampling
	 * @throws PascoException 

	*/
	public void stopContinuousSampling() throws PascoException{
		int ret = jnaLib.PasStopContinuousSampling(library.getHandle(), device.getHandle(), channel);
		
		if(ret < 0){
			throwException("Error stopping sampling", ret);
		}
	}
	

    public int getSensorDataSheetSize() throws PascoException {
    	int size = jnaLib.PasGetSensorDataSheetSize(library.getHandle(), device.getHandle(), channel);
    	
    	if (size < 0){
    		throwException("Error getting sensor datasheet size", size);
    	}
    	
    	return size;
    }

    public void readSensorDataSheet(byte [] dataSheetBuffer, int dataSheetSize) throws PascoException {
		int ret = jnaLib.PasReadSensorDataSheet(library.getHandle(), device.getHandle(), channel, dataSheetBuffer, dataSheetSize);
		
		if(ret < 0){
			throwException("Error reading sensor datasheet", ret);
		}
    }

	private void throwException(String msg, int error) throws PascoException {
		throw new PascoException(msg + " on channel: " + channel + ". Error code: " + error);
	}


	public int getChannel() {
		return channel;
	}
}

package org.concord.sensor.pasco.jna;

public class PascoDevice {

	private int handle;
	private PascoLibrary library;
	private PascoJNALibrary jnaLib;

	public PascoDevice(int handle, PascoLibrary library) {
		this.handle = handle;
		this.library = library;
		this.jnaLib = library.getJNALibrary();
	}
	
	public int getNumOfChannels() throws PascoException {
		int number = jnaLib.PasGetNumChannels(library.getHandle(), handle);
		if(number < 0){
			throw new PascoException("Cannot retrieve number of channels.  Error code: " + number);
		}
		return number;
	}

	public int getProductID() throws PascoException {
		int ret = jnaLib.PasGetDeviceProductID(library.getHandle(), handle);
		if(ret < 0){
			throw new PascoException("Cannot get device product id. Error code: " + ret);
		}
		return ret;
	}
	
	public PascoChannel [] getChannels() throws PascoException {
		int number = getNumOfChannels();
		PascoChannel [] channels = new PascoChannel[number];
		for(int i = 0; i<number; i++){
		  channels[i] = new PascoChannel(this, i);	
		}
		return channels;
	}

	public String getDeviceName() throws PascoException {
		int productID = getProductID();
    	switch (productID) {
    	case 0x0001:
    	case 0x0002:
    		return "USB Link";
    	case 0x0003:
    		return "Xplorer";
		case 0x0005:
			return "PowerLink";
		case 0x0006:
			return "Xplorer GLX";
		case 0x0007:
			return "AirLink";
		case 0x0008:
			return "Spark";
		case 0x0009:
			return "SparkLink";
		case 0x0100:
			return "USB to Serial";
		case 0x0101:
			return "SW750 (USB)";
		default:
			return "USB Device";
		}
	}
	
	PascoLibrary getLibary() {
		return library;
	}

	int getHandle() {
		return handle;
	}
	
	@Override
	public boolean equals(Object obj) {
		if(!(obj instanceof PascoDevice)){
			return false;
		}
		return ((PascoDevice)obj).getHandle() == getHandle();
	}
	
}

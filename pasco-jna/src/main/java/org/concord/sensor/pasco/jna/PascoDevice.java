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
	
	public PascoChannel [] getChannels() throws PascoException {
		int number = getNumOfChannels();
		PascoChannel [] channels = new PascoChannel[number];
		for(int i = 0; i<number; i++){
		  channels[i] = new PascoChannel(this, i);	
		}
		return channels;
	}

	PascoLibrary getLibary() {
		return library;
	}

	int getHandle() {
		return handle;
	}
	
}

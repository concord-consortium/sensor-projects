package org.concord.sensor.labprousb.jna;

import com.sun.jna.ptr.LongByReference;
import com.sun.jna.ptr.ShortByReference;

public class LabProUSB {
	private LabProUSBJNA lpusb;

	public LabProUSB(LabProUSBJNA lpusb) {
		this.lpusb = lpusb;
	}

	public short open() {
		return lpusb.open();
	}

	public short close() {
		return lpusb.close();
	}

	public short isOpen() {
		return lpusb.isOpen();
	}

	public long getAvailableBytes() {
		return lpusb.getAvailableBytes();
	}

	public short readBytes(LongByReference numBytes, byte[] buffer) {
		return lpusb.readBytes(numBytes, buffer);
	}

	public short writeBytes(ShortByReference numBytes, byte[] buffer) {
		return lpusb.writeBytes(numBytes, buffer);
	}

	public short clearInputs(short ignored) {
		return lpusb.clearInputs(ignored);
	}

	public short setNumChannelsAndModes(int numChannels, short binaryMode, short realTime) {
		return lpusb.setNumChannelsAndModes(numChannels, binaryMode, realTime);
	}
}

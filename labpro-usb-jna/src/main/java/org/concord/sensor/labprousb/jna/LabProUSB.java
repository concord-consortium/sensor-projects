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

	public long readBytes(long numBytes, byte[] buffer) {
		LongByReference longRef = new LongByReference(numBytes);
		short ret = lpusb.readBytes(longRef, buffer);
		if (ret >= 0) {
			return longRef.getValue();
		}
		return ret;
	}

	public short writeBytes(short numBytes, byte[] buffer) {
		ShortByReference shortRef = new ShortByReference(numBytes);
		short ret = lpusb.writeBytes(shortRef, buffer);
		if (ret >= 0) {
			return shortRef.getValue();
		}
		return ret;
	}

	public short clearInputs(short ignored) {
		return lpusb.clearInputs(ignored);
	}

	public short setNumChannelsAndModes(int numChannels, short binaryMode, short realTime) {
		return lpusb.setNumChannelsAndModes(numChannels, binaryMode, realTime);
	}
}

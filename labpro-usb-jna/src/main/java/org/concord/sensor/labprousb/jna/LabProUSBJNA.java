package org.concord.sensor.labprousb.jna;

import com.sun.jna.Library;
import com.sun.jna.ptr.LongByReference;
import com.sun.jna.ptr.ShortByReference;

public interface LabProUSBJNA extends Library {
	short open();
	short close();
	short isOpen();
	long  getAvailableBytes();
	short readBytes(LongByReference numBytes, byte[] buffer);
	short writeBytes(ShortByReference numBytes, byte[] buffer);
	short clearInputs(short ignored);
	short setNumChannelsAndModes(int numChannels, short binaryMode, short realTime);
}

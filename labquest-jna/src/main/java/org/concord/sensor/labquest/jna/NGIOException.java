package org.concord.sensor.labquest.jna;

public class NGIOException extends Exception {

	private byte status;

	public NGIOException(byte status) {
		this.status = status;
	}

}

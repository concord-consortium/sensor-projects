package org.concord.sensor.labquest.jna;


@SuppressWarnings("serial")
public class NGIOException extends Exception {

	@SuppressWarnings("unused")
	private byte status;

	public NGIOException(byte status) {
		this.status = status;
	}

}

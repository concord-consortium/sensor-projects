package org.concord.sensor.pasco.jna2;

public class PascoNoSensorAttachedException extends PascoException {

	public PascoNoSensorAttachedException() {
		super("No sensor attached");
	}

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
}

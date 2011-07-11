package org.concord.sensor.pasco.jna;

public class PascoNoSensorAttachedException extends PascoException {

	public PascoNoSensorAttachedException(PascoChannel channel) {
		super("No sensor attached: " + channel.getChannel());
	}

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
}

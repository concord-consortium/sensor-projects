package org.concord.sensor;

/**
 * This is an interface that SensorConfig implementations should implement
 * to indicate they support zeroing in some way.
 *
 * @author scott
 *
 */
public interface ZeroingSensor {
	/**
	 * Check if this sensor supports zeroing.  
	 * 
	 * This duplicates the purpose
	 * of this interface, but it is easier for implementors to provide a single
	 * sensor implementation that determines the zeroing support based on its 
	 * content.
	 */
	public boolean getSupportsZeroing();
	
	
	/**
	 * This will be called while the sensor is running.  It is expected to 
	 * send a message to the device telling it to zero the sensor.  This will 
	 * probably have to be refined based on the requirements of the different
	 * sensors. 
	 * If a sensor supports zeroing with a hardware button only.  Then this
	 * method might want to send a message to the user explaining how to do 
	 * that.
	 *
	 */
	public void zeroSensor();
}

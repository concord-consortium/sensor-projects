/*
 * Created on Dec 13, 2004
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package org.concord.sensor;

import org.concord.framework.data.DataDimension;

/**
 * @author Informaiton Services
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public interface SensorRequest 
{
	/**
	 * This is the type of quantity one of the types above.
	 * @return
	 */
	public int getType();
	
	/**
	 * This is the maximum step size between values.  This
	 * is dependent on the units returned by this sensor.  There
	 * will be implicit units for each quantity, and this step
	 * size will be in those units.
	 * 
	 * When the actual config is returned this value should
	 * be the actual step size.
	 * @return
	 */
	public float getStepSize();
	
	/**
	 * This is used by the author to set the precision as a power of
	 * 10 that they wish to be displayed in the graph, table, or other
	 * display of this data.  For example:
	 * setting this to -1 will give a 0.1 precision
	 * setting this to 0 will give integer precision.
	 * 
	 * Most SensorDevices can ignore this because this will be handled
	 * automatically by the AbstractSensorDevice class. 
	 * 
	 * If we do split this up into two interfaces or classes this
	 * property should only be in the interface used by authors.
	 * @return
	 */
	public int getDisplayPrecsion();
	
	/**
	 * This is the port the sensor is or should be plugged into.
	 * This value ranges from 0 on up.  This value might be ignored
	 * if the ports can figure out which sensor is attached.  
	 * 
	 * Also there could be more than one "sensor config" for a single
	 * port.  If the author wants distance and velocity from the same
	 * sensor.
	 * 
	 * The ports in a experiment should be continuous starting at 0. 
	 * The SensorDevice implementation should assign these ports to the 
	 * first available physical port that has the correct type.  Ports
	 * types could be analog, digital or some other special type.
	 *        
	 * @return
	 */
	public int getPort();
	
	/**
	 * The unit of the sensor plugged in, or the unit
	 * of the requested sensor.
	 * 
	 * This value can probably be ignored in the request
	 * because the unit is implicit based on the quantity
	 * however it should be set correctly incase someone 
	 * wants to use it.
	 * @return
	 */
	public DataDimension getUnit();
		
	/**
	 * These parameters can be used to customize a sensor.  If a parameter
	 * is device specific then the key should start with a device specific
	 * id.  
	 * @param key
	 * @return
	 */
	public String getSensorParam(String key);
}

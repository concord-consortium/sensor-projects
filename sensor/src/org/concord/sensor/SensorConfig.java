/*
 * Last modification information:
 * $Revision: 1.2 $
 * $Date: 2004-12-10 07:22:02 $
 * $Author: scytacki $
 *
 * Licence Information
 * Copyright 2004 The Concord Consortium 
*/
package org.concord.sensor;


/**
 * SensorConfiguration
 * Class name and description
 *
 * Date created: Nov 12, 2004
 *
 * @author scott<p>
 *
 */
public interface SensorConfig
{	
	/*
	 * One question is whether there should be differences
	 * between the distance sensors.  One could be a smartwheel
	 * the other could be an ultra sonic sensor.
	 * 
	 * Or acceleration could be a derivative from a distance sensor
	 * or it could be an dedicated acc sensor.   Which would give
	 * instantaeous acceration.
	 * 
	 * Also several of these quantity can be derived from other 
	 * quantities.  So if they are specified in a experiment config
	 * how will the software know what the author wants.
	 * 
	 * Also the technical hints will depend on how the sensors are 
	 * configured.  So there needs to be conection between this
	 * configuration and the technical hints. 
	 * 
	 * Lets say no for now.  Because we are writing most of this
	 * in house I can delay these decisions until they become
	 * a problem.
	 */

	// This is returned by an device if it knows a sensor
	// is attached but it doesn't know which one.
	public static int QUANTITY_UNKNOWN=             -1;
	
	// Required
	public static int QUANTITY_TEMPERATURE=			0;
	public static int QUANTITY_LIGHT=				1;
	public static int QUANTITY_SOUND_INTENSITY=		2;
	public static int QUANTITY_GAS_PRESSURE= 		3;
	public static int QUANTITY_COMPASS= 			4;
	public static int QUANTITY_VOLTAGE= 			5;
	public static int QUANTITY_CURRENT=				6;
	
	// not required
	public static int QUANTITY_POWER=				7;
	public static int QUANTITY_ENERGY=				8;
	
	// required
	public static int QUANTITY_FORCE=				9;
	public static int QUANTITY_DISTANCE=			10;
	public static int QUANTITY_VELOCITY=			11;
	
	// not required
	public static int QUANTITY_ANGULAR_VELOCITY=	12;
	
	// required
	public static int QUANTITY_RELATIVE_HUMIDITY=	13;
	public static int QUANTITY_WIND_SPEED=			14;
	public static int QUANTITY_ACCELERATION=		15;
	public static int QUANTITY_PULSE_RATE=			16;
	
	
	/**
	 * This is the type of quantity one of the types above.
	 * @return
	 */
	public int getType();
	public void setType(int type);
	
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
	public void setStepSize(float size);
	
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
	public void setDisplayPrecision();
	
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
	public void setPort(int port);
	
	/**
	 * This is the name of the port the sensor is plugged into.
	 * It should only be set by the interface.
	 * @return
	 */
	public String getPortName();
	public void setPortName(String name);
	
	/**
	 * This is the name of sensor that is plugged in.  It should
	 * only be set by the interface.
	 * 
	 * @param key
	 * @return
	 */
	public String getName();
	public void setName(String name);
	
	/**
	 * These parameters can be used to customize a sensor.  If a parameter
	 * is device specific then the key should start with a device specific
	 * id.  
	 * @param key
	 * @return
	 */
	public String getSensorParam(String key);
	public void setSensorParam(String key, String value);	
}

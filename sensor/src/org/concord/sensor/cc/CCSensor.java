/*
 * Last modification information:
 * $Revision: 1.2 $
 * $Date: 2005-02-23 18:04:18 $
 * $Author: scytacki $
 *
 * Licence Information
 * Copyright 2004 The Concord Consortium 
*/
package org.concord.sensor.cc;

import org.concord.framework.data.stream.DataStreamEvent;
import org.concord.sensor.waba.Sensor;
import org.concord.sensor.waba.SensorProducer;


/**
 * CCSensor
 * Class name and description
 *
 * Date created: Nov 30, 2004
 *
 * @author scott<p>
 *
 */
public abstract class CCSensor extends Sensor
{

	/**
	 * @param init
	 * @param type
	 * @param p
	 */
	public CCSensor(boolean init, short type, SensorProducer p)
	{
		super(init, type, p);
		// TODO Auto-generated constructor stub
	}

	public boolean startSampling(DataStreamEvent e)
	{
		return true;
	}
	
	public boolean stopSampling(DataStreamEvent e)
	{
		return true;
	}
	
}

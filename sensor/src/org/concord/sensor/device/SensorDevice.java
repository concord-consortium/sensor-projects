/*
 * Last modification information:
 * $Revision: 1.1 $
 * $Date: 2004-12-24 15:34:59 $
 * $Author: scytacki $
 *
 * Licence Information
 * Copyright 2004 The Concord Consortium 
*/
package org.concord.sensor.device;

import org.concord.sensor.ExperimentConfig;
import org.concord.sensor.ExperimentRequest;


/**
 * SensorDevice
 * Class name and description
 *
 * Date created: Dec 24, 2004
 *
 * @author scott<p>
 *
 */
public interface SensorDevice
{
	public int getRightMilliseconds();
	
	public void open(String openString);
	
	public void close();
	
	public ExperimentConfig configure(ExperimentRequest request);
	
	public boolean start();
	
	public void stop(boolean wasRunning);
	
	public int read(float [] values, int offset, int nextSampleOffset,
			DeviceReader reader);

	public String getErrorMessage(int error);
	
	public boolean isAttached();
	
	public boolean canDetectSensors();
	
	public ExperimentConfig getCurrentConfig();
}

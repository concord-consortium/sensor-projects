/*
 * Created on Dec 13, 2004
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package org.concord.sensor;

/**
 * @author Informaiton Services
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public interface ExperimentRequest 
{
	public float getPeriod();
	
	public SensorRequest [] getSensorRequests();		
}

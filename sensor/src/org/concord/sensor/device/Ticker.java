/*
 * Created on Jun 19, 2004
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
package org.concord.sensor.device;

/**
 * @author scott
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
public interface Ticker 
{
	public void startTicking(int millis);
	
	public void stopTicking();
	
	public boolean isTicking();
	
	public void setInterfaceManager(DefaultSensorDevice manager);		
	
	public DefaultSensorDevice getInterfaceManager();
	
	/**
	 * This is need because waba can't do reflection. This will
	 * be used to make new copies of this ticker if they are 
	 * needed.
	 * @return
	 */
	public Ticker createNew();
}

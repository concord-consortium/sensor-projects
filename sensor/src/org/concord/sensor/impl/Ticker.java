/*
 * Created on Jun 19, 2004
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
package org.concord.sensor.impl;



/**
 * @author scott
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
public interface Ticker 
{
    /**
     * If the passed in listener is not equal to the current 
     * listener then the current listener is notified of this
     * start event
     * 
     * @param millis
     * @param listener
     */
	public void startTicking(int millis, TickListener listener);
	
    /**
     * If the passed in listener is not equal to the current 
     * listener then the current listener is notified of this
     * stop event
     * 
     * @param millis
     * @param listener
     */
	public void stopTicking(TickListener listener);
	
	public boolean isTicking();
	
	public TickListener getTickListener();
	
	/**
	 * This is need because waba can't do reflection. This will
	 * be used to make new copies of this ticker if they are 
	 * needed.
	 * @return
	 */
	public Ticker createNew();
}

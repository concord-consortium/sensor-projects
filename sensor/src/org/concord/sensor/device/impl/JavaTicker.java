/*
 * Created on Jun 19, 2004
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
package org.concord.sensor.device.impl;

import org.concord.sensor.impl.TickListener;
import org.concord.sensor.impl.Ticker;


/**
 * @author scott
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
public class JavaTicker extends Thread
	implements Ticker
{
	int millis;
	boolean ticking = false;
	boolean started = false;
	TickListener tickListener;
		
	/* (non-Javadoc)
	 * @see org.concord.sensor.Ticker#start(int)
	 */
	synchronized public void startTicking(int millis) 
	{
		this.millis = millis;
		ticking = true;
		if(started) {
			notify();
		} else {
			started = true;
			start();
		}
	}

	synchronized public void stopTicking() 
	{
		ticking = false;
	}
	
	/* (non-Javadoc)
	 * @see org.concord.sensor.Ticker#isTicking()
	 */
	synchronized public boolean isTicking() 
	{
		return ticking;
	}

	/* (non-Javadoc)
	 * @see org.concord.sensor.Ticker#setInterfaceManager(org.concord.sensor.InterfaceManager)
	 */
	synchronized public void setTickListener(TickListener tListener) 
	{
	    // We check if the listener is null here
	    // We want to make sure the no one is expecting a tick
	    // and isn't getting one, so each user of this ticker
	    // needs to set this to null when they are done with it
	    if(tListener != null && tickListener != null){
	        throw new RuntimeException("Inconsitant ticker state");
	    }
	    tickListener = tListener;
	}

	/* (non-Javadoc)
	 * @see org.concord.sensor.Ticker#getInterfaceManager()
	 */
	synchronized public TickListener getTickListener() 
	{
	    return tickListener;
	}

	/* (non-Javadoc)
	 * @see org.concord.sensor.Ticker#createNew()
	 */
	public Ticker createNew() 
	{
		return new JavaTicker();
	}
	
	synchronized public void run()
	{
		while(true) {
			if(!ticking) {
				try {
					wait();
				} 
				catch(InterruptedException e) {					
					e.printStackTrace();
				}
			}
	
			if(tickListener != null) {
			    tickListener.tick();
			} else {
			    System.err.println("ticking a null listener");
			}
			
			try {
				// We wait so that we release the lock
				wait(millis);										
			}
			catch(InterruptedException e) {				
			}
		}
	}
}

/*
 * Created on Jun 19, 2004
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
package org.concord.sensor.waba;


import org.concord.sensor.impl.TickListener;
import org.concord.sensor.impl.Ticker;

import waba.ui.Control;
import waba.ui.ControlEvent;
import waba.ui.Event;
import waba.ui.Timer;

/**
 * @author scott
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
public class WabaTicker extends Control
	implements Ticker
{
    TickListener tickListener;
	Timer timer = null;
	
	public void setTickListener(TickListener tListener) {
		tickListener = tListener;
	}

	public TickListener getTickListener() {
	    return tickListener;
	}
	
	public void startTicking(int millis)
	{
		timer = addTimer(millis);
	}
	
	public void stopTicking()
	{
		removeTimer(timer);
		timer = null;
	}
	
	public boolean isTicking()
	{
		return timer != null;
	}
	
	public Ticker createNew()
	{
		return new WabaTicker();
	}
	
	public void onEvent(Event event)
	{
		if (event.type==ControlEvent.TIMER){
			tickListener.tick();
		}
	}	
}

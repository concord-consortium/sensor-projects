/*
 * Created on Jun 19, 2004
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
package org.concord.sensor;

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
	InterfaceManager interfaceManager = null;
	Timer timer = null;
	
	public void setInterfaceManager(InterfaceManager manager) {
		interfaceManager = manager;
	}

	public InterfaceManager getInterfaceManager() {
		return interfaceManager;
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
			interfaceManager.tick();
		}
	}	
}

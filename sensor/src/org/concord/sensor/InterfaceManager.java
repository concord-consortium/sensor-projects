package org.concord.sensor;

import org.concord.framework.text.UserMessageHandler;

public abstract class InterfaceManager
{
	public int		startTimer =  0;
	protected Ticker ticker = null;
	protected UserMessageHandler messageHandler;
	
	public InterfaceManager(Ticker t, UserMessageHandler h)
	{
		ticker = t;
		ticker.setInterfaceManager(this);
		
		messageHandler = h;
	}
	
	public boolean syncInterfaceWithSensor(Sensor p)
	{
		if(!checkMode(p)) return false;

		updateMode(p);

		return true;
	}
		
	public abstract boolean addSensor(Sensor probe);

	public abstract void removeSensor(Sensor probe);
		
	public abstract void dispose();

	public boolean checkMode(Sensor p)
	{
		// Call some global function to get the interface mode for this probe
		// it needs to be based on the probes properties
		// But we need to watch out for stuff that currently get set by this function
		Object mode = p.getInterfaceMode();

		return checkMode(p, mode);
	}

	/**
	 * this is called to setup the interface so it is ready
	 * to read from this probe.
	 * This will set the requested mode of the interface to 
	 * match.  Then when start is called the requested mode
	 * will be sent to the interface
	 */
	abstract public boolean updateMode(Sensor probe);

	/**
	 * This is called to see mode the probe has is a
	 * valid mode for the interface
	 */
	abstract public boolean checkMode(Sensor probe, Object mode);


	abstract public void start(Sensor p);

	abstract public void stop(Sensor p);
	
	abstract public void tick();
}

package org.concord.sensor.goio.jna;

import com.sun.jna.Structure;

public abstract class GoIOStructure extends Structure {
	public GoIOStructure()	
	{
		super(ALIGN_NONE);
	}
}


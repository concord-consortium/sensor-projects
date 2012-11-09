package org.concord.sensor.labquest.jna;

import java.util.List;

import com.sun.jna.Structure;

public abstract class NGIOStructure extends Structure {
	public NGIOStructure()	
	{
		super(ALIGN_NONE);
	}
}

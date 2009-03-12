package org.concord.sensor.labquest.jna;

import com.sun.jna.Structure;

public class NGIOStructure extends Structure {
	public NGIOStructure()	
	{
		super(CALCULATE_SIZE, ALIGN_NONE);
	}
}

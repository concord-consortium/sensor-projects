package org.concord.sensor.goio.jna;

import com.sun.jna.Structure;

public class GoIOStructure extends Structure {
	public GoIOStructure()	
	{
		super(CALCULATE_SIZE, ALIGN_NONE);
	}
}


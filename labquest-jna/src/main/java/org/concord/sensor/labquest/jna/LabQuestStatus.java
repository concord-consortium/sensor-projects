package org.concord.sensor.labquest.jna;

import java.util.Arrays;
import java.util.List;

public class LabQuestStatus extends NGIOStructure {
	public byte status;	//See NGIO_MASK_STATUS_*.
	public byte minorVersionMasterCPU;	//Binary coded decimal
	public byte majorVersionMasterCPU;	//Binary coded decimal
	public byte minorVersionSlaveCPU;		//Binary coded decimal - updated only by Skip and Cyclops, not by Jonah
	public byte majorVersionSlaveCPU;		//Binary coded decimal - updated only by Skip and Cyclops, not by Jonah

	@Override
	protected List<String> getFieldOrder() {
		return Arrays.asList(new String[] { 
				"status", 
				"minorVersionMasterCPU",
				"majorVersionMasterCPU",
				"minorVersionSlaveCPU",
				"majorVersionSlaveCPU"
		});
	}

	public String inspect() {
		return "status: " + status +
				", masterCPUVersion: " + majorVersionMasterCPU + "." + minorVersionMasterCPU +
				", slaveCPUVersion: " + majorVersionSlaveCPU + "." + minorVersionSlaveCPU;
	}

}

package org.concord.probe.meld;

import org.concord.probe.*;
import org.concord.waba.extra.io.*;

public class MeldProbeProducer extends ProbeProducer
{
	public final static int Meld_RawData  = 6;

	public MeldProbeProducer()
	{
		String [] names = {"MeldRawData"};
		probeNames = names;
		lowProbeType = 6;
		highProbeType = 6;
	}

	public Probe createProbe(boolean init, int probType){
		Probe newProb = null;
		switch(probType){
		case Meld_RawData:
			newProb = new MeldRawData(init, probType, this);
			break;
		}

		return newProb;
	}
}

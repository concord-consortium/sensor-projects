/*
 *  Copyright (C) 2004  The Concord Consortium, Inc.,
 *  10 Concord Crossing, Concord, MA 01742
 *
 *  Web Site: http://www.concord.org
 *  Email: info@concord.org
 *
 *  This library is free software; you can redistribute it and/or
 *  modify it under the terms of the GNU Lesser General Public
 *  License as published by the Free Software Foundation; either
 *  version 2.1 of the License, or (at your option) any later version.
 *
 *  This library is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *  Lesser General Public License for more details.
 *
 *  You should have received a copy of the GNU Lesser General Public
 *  License along with this library; if not, write to the Free Software
 *  Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 *
 * END LICENSE */

package org.concord.probe.meld;

import org.concord.probe.*;
import org.concord.waba.extra.util.*;
import org.concord.framework.data.stream.*;

public class MeldRawData extends Probe
{
	float  			[]rawData = new float[32];
	int  			[]rawIntData = new int[32];

    String [] channelNames = {"0", "1", "2", "3"};

	PropObject chanProp = new PropObject("Channel #", "Channel", PROP_CHAN_NUM, channelNames);

    int curChannel = 0;

	MeldRawData(boolean init, int type, ProbeProducer p)
	{
		super(init, type, p);

		interfaceType = Meld.MELD;

		defQuantityName = "Voltage";

		dDesc.setChPerSample(1);
		dDesc.setIntChPerSample(1);
		dDesc.setDt(0.0f);
		dEvent.setDataDesc(dDesc);
		dEvent.setDataOffset(0);
		dEvent.setNumbSamples(1);
		dEvent.setData(rawData);
		dEvent.setIntData(rawIntData);

		addProperty(chanProp);
		unit = "V";
	}

	public int 	getInterfaceType(){return Meld.MELD;}

	public void setDataDescParam(int chPerSample,float dt){
		dDesc.setDt(dt);
		dDesc.setChPerSample(chPerSample);
		dDesc.setIntChPerSample(chPerSample);
	}

	public String getLabel()
	{
		return "Voltage " + "Ch. " + curChannel;
	}

    public boolean startSampling(DataEvent e){
		dEvent.type = e.type;
		dDesc.setDt(e.getDataDesc().getDt());
		// Change to Volts
		dDesc.setTuneValue(e.getDataDesc().getTuneValue()/1000f);
		dDesc.setChPerSample(1);
		dDesc.setIntChPerSample(1);

		return super.startSampling(dEvent);
	}

    public boolean dataArrived(DataEvent e)
    {
		int nOffset 		= e.getDataOffset();
		int[] data = e.getIntData();
		int  	chPerSample = e.dataDesc.chPerSample;
		int		nSamples	= e.getNumbSamples();
		int 	ndata 		= nSamples*chPerSample;
		int 	v = 0,v1 = 0;
		dEvent.type = e.type;
		dEvent.intTime = e.intTime;
		int j=0;
		for(int i = nOffset; i < ndata; i+=chPerSample){
			v = data[i];
			rawIntData[j] = v;
			rawData[j] = (float)v*dDesc.tuneValue;
			j++;
		}
		dEvent.setNumbSamples(nSamples);
		return super.dataArrived(dEvent);
    }

	// need a function this called to setup the probe before
	// it is started
	public int getInterfaceMode()
	{
		int chIndex = chanProp.getIndex();
		channelArray[0] = chIndex;
		return Meld.A2D_INTERNAL;
	}
}

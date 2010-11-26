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

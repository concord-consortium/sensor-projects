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

package org.concord.sensor.impl;

import java.util.Vector;

import org.concord.framework.data.stream.DataListener;
import org.concord.framework.data.stream.DataStreamDescription;
import org.concord.framework.data.stream.DataStreamEvent;
import org.concord.framework.text.UserMessageHandler;
import org.concord.sensor.device.SensorDevice;


public class JavaSensorDataProducer extends SensorDataProducerImpl
{
	public JavaSensorDataProducer(SensorDevice device, Ticker t, UserMessageHandler h)
	{
		super(device, t, h);
	}
	
	protected Vector dataListeners = null;

	public void addDataListener(DataListener l){
		if(dataListeners == null){ 
		    dataListeners = new Vector();	   
		}
		if(!dataListeners.contains(l)){
			dataListeners.add(l);
		}
	}
	
	public void removeDataListener(DataListener l){
		if(dataListeners == null) return;
		int index = dataListeners.indexOf(l);
		if(index >= 0) dataListeners.remove(index);
		if(dataListeners.size() == 0) dataListeners = null;
	}

	public void notifyDataListenersEvent(DataStreamEvent e){
		if(dataListeners == null) return;
		for(int i = 0; i < dataListeners.size(); i++){
			DataListener l = (DataListener)dataListeners.get(i);
			l.dataStreamEvent(e);
		}
	}

	public void notifyDataListenersReceived(DataStreamEvent e)
	{
		if(dataListeners == null) return;
		
		// if the data has timestamps they should be adjusted
		// the contract for sensor devices is that time starts 
		// at 0 when start is called, however for data producers
		// time starts at 0 when reset is called.  The stop method
		// is more like a pause for data producers.
		if(dDesc.getDataType() == DataStreamDescription.DATA_SERIES){
		    // the first channel will be time.
		    for(int i=dDesc.getDataOffset(); 
		    	i < e.getNumSamples(); 
		    	i+= dDesc.getNextSampleOffset()){
		        processedData[i] += dataTimeOffset;
		    }
		}
		
		for(int i = 0; i < dataListeners.size(); i++){
			DataListener l = (DataListener)dataListeners.get(i);
			l.dataReceived(e);
		}
	}
}

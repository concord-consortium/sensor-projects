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

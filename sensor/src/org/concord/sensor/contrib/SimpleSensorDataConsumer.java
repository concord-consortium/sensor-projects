/*
 * Created on Feb 22, 2005
 *
 */
package org.concord.sensor.contrib;

import org.concord.sensor.*;
import org.concord.framework.data.DataDimension;
import org.concord.framework.data.stream.DataListener;
import org.concord.framework.data.stream.DataStreamEvent;
import org.concord.framework.data.stream.DataConsumer;
import org.concord.framework.data.stream.DataProducer;

/**
 * simpel implementation of the DataConsumer
 * 
 * @author Dmitry Markman
 *
 */
public class SimpleSensorDataConsumer implements DataConsumer 
{
protected SensorDataProducer  sensorDataProducer;
protected DataListener        dataListener;
    
    public SimpleSensorDataConsumer(){
    }

    public SensorDataProducer getSensorDataProducer(){
        return sensorDataProducer;
    }

    public void setSensorDataProducer(SensorDataProducer sensorDataProducer){
        this.sensorDataProducer = sensorDataProducer;
    }

	/* (non-Javadoc)
	 * @see org.concord.framework.data.stream.DataConsumer#addDataProducer(org.concord.framework.data.stream.DataProducer)
	 */
	public void addDataProducer(DataProducer source) {
	    if(source instanceof SensorDataProducer){
		    setSensorDataProducer((SensorDataProducer)source);
		    dataListener = new SimpleDataListener();
		    source.addDataListener(dataListener);
        }
	}
	/* (non-Javadoc)
	 * @see org.concord.framework.data.stream.DataConsumer#removeDataProducer(org.concord.framework.data.stream.DataProducer)
	 */
	public void removeDataProducer(DataProducer source) {
	    if(source != sensorDataProducer) return;
	    if(source != null && dataListener != null) source.removeDataListener(dataListener);
	}
}

class SimpleDataListener implements DataListener{
	public void dataReceived(DataStreamEvent dataEvent)
	{
		int numSamples = dataEvent.getNumSamples();
		float [] data = dataEvent.getData();
		if(numSamples > 0) {
			System.out.println("" + numSamples + " " +
						data[0]);
			System.out.flush();
		} 
		else {
			System.out.println("" + numSamples);
		}
	}

	public void dataStreamEvent(DataStreamEvent dataEvent)
	{				
		String eventString;
		int eventType = dataEvent.getType();
		
		if(eventType == 1001) return;
		
		switch(eventType) {
			case DataStreamEvent.DATA_READY_TO_START:
				eventString = "Ready to start";
			break;
			case DataStreamEvent.DATA_STOPPED:
				eventString = "Stopped";
			break;
			case DataStreamEvent.DATA_DESC_CHANGED:
				eventString = "Description changed";
			break;
			default:
				eventString = "Unknown event type";					
		}
		
		System.out.println("Data Event: " + eventString); 
	}
}

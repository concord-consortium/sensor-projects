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

/*
 * Created on Jan 12, 2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package org.concord.sensor.state;

import java.util.Vector;

import org.concord.framework.data.stream.DataListener;
import org.concord.framework.data.stream.DataProducer;
import org.concord.framework.data.stream.DataStreamDescription;
import org.concord.framework.data.stream.DataStreamEvent;
import org.concord.framework.otrunk.DefaultOTObject;
import org.concord.framework.otrunk.OTObjectService;
import org.concord.framework.otrunk.OTResourceSchema;
import org.concord.framework.util.Copyable;
import org.concord.sensor.ExperimentConfig;
import org.concord.sensor.ExperimentRequest;
import org.concord.sensor.SensorDataManager;
import org.concord.sensor.SensorDataProducer;
import org.concord.sensor.impl.DataStreamDescUtil;

/**
 * @author Informaiton Services
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class OTSensorDataProxy extends DefaultOTObject 
	implements DataProducer, Copyable
{
	public static interface ResourceSchema extends OTResourceSchema
	{
		OTExperimentRequest getRequest();
        void setRequest(OTExperimentRequest request);
        
        OTZeroSensor getZeroSensor();
	}
	
	private ResourceSchema resources;
	private SensorDataManager sensorManager;
	private DataProducer producer = null;
	private SensorDataProducer sensorDataProducer = null;

	private Vector dataListeners = new Vector();
	
	// This is just to keep a reference so it doesn't get garbage collected
	// while this sensor proxy is still around
	private OTZeroSensor zeroSensor;
	
	/**
	 * @param resources
	 */
	public OTSensorDataProxy(ResourceSchema resources,
			SensorDataManager sdm) {
		super(resources);
		
		this.resources = resources;
		sensorManager = sdm;
		// TODO Auto-generated constructor stub
	}

	/* (non-Javadoc)
	 * @see org.concord.framework.data.stream.DataProducer#addDataListener(org.concord.framework.data.stream.DataListener)
	 */
	public void addDataListener(DataListener listener) 
	{
		// people might add data listeners to us
		// before the real sensor data producer is ready
		// so we'll need to proxy these listeners
		
		// we can either proxy the list of listeners
		// or we can proxy the events themselves.
		// on issue might be the event source.  If someone
		// is testing whether the source matches this object
		// then they will get screwed up unless we change
		// the source.
		dataListeners.add(listener);
		if(producer != null) {
		    producer.addDataListener(listener);
		}
	}
	
	/* (non-Javadoc)
	 * @see org.concord.framework.data.stream.DataProducer#removeDataListener(org.concord.framework.data.stream.DataListener)
	 */
	public void removeDataListener(DataListener listener) 
	{
		dataListeners.remove(listener);
        if(producer != null) {
            producer.removeDataListener(listener);
        }
	}
	
	/* (non-Javadoc)
	 * @see org.concord.framework.data.stream.DataProducer#getDataDescription()
	 */
	public DataStreamDescription getDataDescription() 
	{
		if(producer == null) {
			DataStreamDescription dDesc = new DataStreamDescription();
			
			DataStreamDescUtil.setupDescription(dDesc, 
					resources.getRequest(), null);
			
			// this should return a partially correct description
			// before the real device is ready.  some fields
			// will be missing or approximate: period, stepSize
			// series or sequence
			
			return dDesc;
		} 
			
		return producer.getDataDescription();
	}
	
	/* (non-Javadoc)
	 * @see org.concord.framework.data.DataFlow#start()
	 */
	public void start() 
	{
		// ask the devicemanager to configure the device with
		// our experimentrequest, once the producer from the 
		// device is recieved we should start it and pass 
		// connect up the currently attached data listeners
		// the datamanager should be careful so it doens't
		// start two requests at once.  

		ExperimentRequest request = resources.getRequest();	
		if(sensorDataProducer == null) {
			sensorDataProducer = sensorManager.createDataProducer();
			producer = sensorDataProducer;
            if(sensorDataProducer == null) {
                // we couldn't create the producer
                return;
            }
			for(int i=0; i<dataListeners.size(); i++) {
				DataListener listener = (DataListener)dataListeners.get(i);
				DataStreamEvent changeEvent = 
					new DataStreamEvent(DataStreamEvent.DATA_DESC_CHANGED);
				changeEvent.setDataDescription(getDataDescription());
				listener.dataStreamEvent(changeEvent);
				producer.addDataListener(listener);
			}
		}
		ExperimentConfig config = sensorDataProducer.configure(request);
		if(config == null || !config.isValid()) {
		    return;
		}
		
		if(resources.getZeroSensor() != null){
			// need to setup the taring datafilter and wrap it around the
			// producer
			
			// keep a reference to this zero sensor object so it doesn't
			// get garbage collected before the button is pushed
			zeroSensor = resources.getZeroSensor();
			
			DataProducer newProducer = zeroSensor.setupDataFilter(sensorDataProducer);
			if(producer != newProducer){
				// need to transfer all the listeners from the old
				// producer to the new one
				for(int i=0; i<dataListeners.size(); i++) {
					DataListener listener = (DataListener)dataListeners.get(i);
					producer.removeDataListener(listener);
					newProducer.addDataListener(listener);
				}

				producer = newProducer;
			}
			
		}
		
		producer.start();
	}
	
	/* (non-Javadoc)
	 * @see org.concord.framework.data.DataFlow#stop()
	 */
	public void stop() 
	{
		// stop the proxied dataProducer
		// the dataProducer might be stopped already this
		// could happen if some other proxy started it.
		
		// FIXME we will a potential memory leak here unless
		// we clean up these listeners.
		if(producer != null) {
			producer.stop();
		}
	}	
	
	/* (non-Javadoc)
	 * @see org.concord.framework.data.DataFlow#reset()
	 */
	public void reset() 
	{
	    if(producer != null) {
	        producer.reset();
	    }
	}
	
	public void init()
	{
	}	
    
    public Object getCopy()
    {        
        OTObjectService service = getOTObjectService();
        try {
            OTSensorDataProxy copy = 
                (OTSensorDataProxy)service.createObject(OTSensorDataProxy.class);
            copy.resources.setName(resources.getName());
            copy.resources.setRequest(resources.getRequest());
            return copy;
        } catch (Exception e){
            e.printStackTrace();
        }
        
        return null;
    }
}

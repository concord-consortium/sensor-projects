/*
 * Created on Jan 12, 2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package org.concord.sensor.state;

import org.concord.framework.data.stream.DataConsumer;
import org.concord.framework.data.stream.DataProducer;
import org.concord.framework.data.stream.DataProducerProxy;
import org.concord.framework.otrunk.DefaultOTObject;
import org.concord.framework.otrunk.OTResourceSchema;
import org.concord.sensor.ExperimentRequest;
import org.concord.sensor.SensorDataManager;
import org.concord.sensor.SensorDataProducer;

/**
 * @author Informaiton Services
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class OTSensorDataProxy extends DefaultOTObject 
	implements DataProducerProxy, DataConsumer 
{
	public static interface ResourceSchema extends OTResourceSchema
	{
		OTExperimentRequest getRequest();		
	}
	
	private ResourceSchema resources;
	private SensorDataManager sensorManager;
	private SensorDataProducer producer = null;
	
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
	 * @see org.concord.framework.data.stream.DataProducerProxy#getDataProducer()
	 */
	public DataProducer getDataProducer() 
	{
		ExperimentRequest request = resources.getRequest();		

		// FIXME we are assuming this method will be single threaded
		// which might not always be true
		sensorManager.prepareDataProducer(request, this);		
		// we are assuming that the producer has been
		// created at this point.
		// if the producer is null we should probably block
		return producer;
	}

	public void init()
	{
	}
	
	/* (non-Javadoc)
	 * @see org.concord.framework.data.stream.DataConsumer#addDataProducer(org.concord.framework.data.stream.DataProducer)
	 */
	public void addDataProducer(DataProducer source) 
	{
		producer = (SensorDataProducer)source;
	}
	
	/* (non-Javadoc)
	 * @see org.concord.framework.data.stream.DataConsumer#removeDataProducer(org.concord.framework.data.stream.DataProducer)
	 */
	public void removeDataProducer(DataProducer source) {
		// TODO Auto-generated method stub
	}
}

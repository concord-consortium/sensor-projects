/*
 * Created on Jan 11, 2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package org.concord.sensor.state;

import org.concord.framework.otrunk.DefaultOTObject;
import org.concord.framework.otrunk.OTObjectList;
import org.concord.framework.otrunk.OTResourceSchema;
import org.concord.sensor.ExperimentRequest;
import org.concord.sensor.SensorRequest;

/**
 * @author scytacki
 *
 */
public class OTExperimentRequest extends DefaultOTObject implements
		ExperimentRequest 
{
	public static interface ResourceSchema extends OTResourceSchema{
		public final static float DEFAULT_period = 0.1f;
		public float getPeriod();
		public void setPeriod(float period);
		
		OTObjectList getSensorRequests();		
	};
	private ResourceSchema resources;
	
	public OTExperimentRequest(ResourceSchema resources)
	{
		super(resources);
		this.resources = resources;
	}
	/* (non-Javadoc)
	 * @see org.concord.sensor.ExperimentRequest#getPeriod()
	 */
	public float getPeriod() {
		// TODO Auto-generated method stub
		return resources.getPeriod();
	}

	/* (non-Javadoc)
	 * @see org.concord.sensor.ExperimentRequest#getSensorRequests()
	 */
	public SensorRequest[] getSensorRequests() {
		OTObjectList sensorRequests = resources.getSensorRequests();
		int size = sensorRequests.size();
		SensorRequest [] requestArray = new SensorRequest [size];
		for(int i=0; i<size; i++){
			requestArray[i] = (SensorRequest)sensorRequests.get(i);
		}
			
		return requestArray;
	}

}

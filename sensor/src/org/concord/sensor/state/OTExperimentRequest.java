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
		
		public final static float DEFAULT_recordingTime = -1;
		public float getRecordingTime();
		public void setRecordingTime(float recordingTime);
		
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
		return resources.getPeriod();
	}

	/* (non-Javadoc)
	 * @see org.concord.sensor.ExperimentRequest#getNumberOfSamples()
	 */
	public int getNumberOfSamples() 
	{	 
	    float recordingTime = resources.getRecordingTime();
	    float period = resources.getPeriod();
	    if(Float.isNaN(recordingTime) ||
	            Float.isNaN(period)) {
	        return -1;
	    }
		return (int)(recordingTime/period);
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

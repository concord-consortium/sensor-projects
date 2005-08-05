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
 * Created on Feb 22, 2005
 *
 */
 
package org.concord.sensor.contrib;
 
import org.concord.framework.data.DataDimension;
import org.concord.sensor.ExperimentRequest;
import org.concord.sensor.impl.ExperimentConfigImpl;
import org.concord.sensor.SensorConfig;
import org.concord.sensor.SensorRequest;

/**
 * implementation of the ExperimentRequest
 * it's possible to set sensor type
 * @see SensorConfig
 * 
 * @author Dmitry Markman
 *
 */
 
public class DefaultExperimentRequest extends org.concord.sensor.impl.ExperimentRequestImpl 
{
protected int sensorType = SensorConfig.QUANTITY_UNKNOWN;
protected SensorRequest     []sensorRequests;
    public DefaultExperimentRequest(int sensorType){
        this.sensorType = sensorType;
    }	

	/* (non-Javadoc)
	 * @see org.concord.sensor.ExperimentRequest#getSensorRequests()
	 */
	public SensorRequest[] getSensorRequests() {
	    if(sensorRequests == null){
	        sensorRequests = new SensorRequest[1];
		    sensorRequests[0] = new DefaultSensorRequest(sensorType);
	    }
	    return sensorRequests;
	}
	
	public void setSensorRequests(SensorRequest []sensorRequests){
	    this.sensorRequests = sensorRequests;
	}
}

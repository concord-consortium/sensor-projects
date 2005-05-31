
/*
 *  Copyright (C) 2004  The Concord Consortium, Inc.,
 *  10 Concord Crossing, Concord, MA 01741
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
 */

/*
 * Created on Feb 22, 2005
 *
 */
package org.concord.sensor.impl;

import org.concord.framework.data.DataDimension;
import org.concord.sensor.ExperimentRequest;
import org.concord.sensor.SensorRequest;

/**
 * empty implementation of the ExperimentRequest
 * The SensorDevice uses this request to figure out how to configure
 * the device and sensors.
 * 
 * @author Dmitry Markman
 *
 */
public class ExperimentRequestImpl implements ExperimentRequest 
{
	/* (non-Javadoc)
	 * @see org.concord.sensor.ExperimentRequest#getPeriod()
	 */
	public float getPeriod() 
	{

		return 0;
	}
	
	/* (non-Javadoc)
	 * @see org.concord.sensor.ExperimentRequest#getNumberOfSamples()
	 */
	public int getNumberOfSamples() 
	{
		return -1;
	}
	
	/* (non-Javadoc)
	 * @see org.concord.sensor.ExperimentRequest#getSensorRequests()
	 */
	public SensorRequest[] getSensorRequests() {
		// TODO Auto-generated method stub
		SensorRequest [] sensors = new SensorRequest[1];
		sensors[0] = new SensorRequest(){
			/* (non-Javadoc)
			 * @see org.concord.sensor.SensorRequest#getDisplayPrecision()
			 */
			public int getDisplayPrecision() {
				// TODO Auto-generated method stub
				return -2;
			}
			
			/* (non-Javadoc)
			 * @see org.concord.sensor.SensorRequest#getRequiredMax()
			 */
			public float getRequiredMax() 
			{
				return Float.NaN;
			}
			
			/* (non-Javadoc)
			 * @see org.concord.sensor.SensorRequest#getRequiredMin()
			 */
			public float getRequiredMin() 
			{
				return Float.NaN;
			}
			
			/* (non-Javadoc)
			 * @see org.concord.sensor.SensorRequest#getPort()
			 */
			public int getPort() {
				// TODO Auto-generated method stub
				return 0;
			}
			
			/* (non-Javadoc)
			 * @see org.concord.sensor.SensorRequest#getSensorParam(java.lang.String)
			 */
			public String getSensorParam(String key) {
				// TODO Auto-generated method stub
				return null;
			}
			
			/* (non-Javadoc)
			 * @see org.concord.sensor.SensorRequest#getStepSize()
			 */
			public float getStepSize() {
				// TODO Auto-generated method stub
				return 0.1f;
			}
			/* (non-Javadoc)
			 * @see org.concord.sensor.SensorRequest#getType()
			 */
			public int getType() {
				// TODO Auto-generated method stub
				return 0;
			}
			/* (non-Javadoc)
			 * @see org.concord.sensor.SensorRequest#getUnit()
			 */
			public DataDimension getUnit() {
				// TODO Auto-generated method stub
				return null;
			}
		};
		return sensors;
	}
}

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

import org.concord.framework.data.DataDimension;
import org.concord.framework.otrunk.DefaultOTObject;
import org.concord.framework.otrunk.OTResourceMap;
import org.concord.framework.otrunk.OTResourceSchema;
import org.concord.sensor.SensorRequest;
import org.concord.sensor.impl.SensorUnit;

/**
 * @author scytacki
 *
 */
public class OTSensorRequest extends DefaultOTObject 
	implements SensorRequest 
{
	public static interface ResourceSchema extends OTResourceSchema{
		public final static float DEFAULT_type = -1;
		public int getType();
		public void setType(int type);

		public final static float DEFAULT_stepSize = 1;
		public float getStepSize();
		public void setStepSize(float stepSize);
		
		public final static float DEFAULT_requiredMax = Float.NaN;
		public float getRequiredMax();
		public void setRequiredMax(float requiredMax);
		
		public final static float DEFAULT_requiredMin = Float.NaN;
		public float getRequiredMin();
		public void setRequiredMin(float requiredMin);
				
		public final static int DEFAULT_displayPrecision = -2;
		public int getDisplayPrecision();
		public void setDisplayPrecision(int precision);
		
		public final static int DEFAULT_port = 0;
		public int getPort();
		public void setPort(int port);
		
		public String getUnit();
		public void setUnit(String unit);
		
		public OTResourceMap getParamMap();		
	};
	private ResourceSchema resources;

	
	/**
	 * @param resources
	 */
	public OTSensorRequest(ResourceSchema resources) 
	{
		super(resources);
		this.resources = resources;
	}

	/* (non-Javadoc)
	 * @see org.concord.sensor.SensorRequest#getType()
	 */
	public int getType() 
	{
		return resources.getType();
	}
	
	public void setType(int type){
		resources.setType(type);
	}

	/* (non-Javadoc)
	 * @see org.concord.sensor.SensorRequest#getStepSize()
	 */
	public float getStepSize() 
	{
		return resources.getStepSize();
	}
	
	public void setStepSize(float stepSize){
		resources.setStepSize(stepSize);
	}

	/* (non-Javadoc)
	 * @see org.concord.sensor.SensorRequest#getRequiredMax()
	 */
	public float getRequiredMax() 
	{
		return resources.getRequiredMax();
	}
	
	public void setRequiredMax(float requiredMax){
		resources.setRequiredMax(requiredMax);
	}
	
	/* (non-Javadoc)
	 * @see org.concord.sensor.SensorRequest#getRequiredMin()
	 */
	public float getRequiredMin() 
	{
		return resources.getRequiredMin();
	}
	
	public void setRequiredMin(float requiredMin){
		resources.setRequiredMin(requiredMin);
	}

	/* (non-Javadoc)
	 * @see org.concord.sensor.SensorRequest#getDisplayPrecision()
	 */
	public int getDisplayPrecision() 
	{
		return resources.getDisplayPrecision();
	}
	
	public void setDisplayPrecision(int precision){
		resources.setDisplayPrecision(precision);
	}

	/* (non-Javadoc)
	 * @see org.concord.sensor.SensorRequest#getPort()
	 */
	public int getPort() 
	{
		return resources.getPort();
	}

	/* (non-Javadoc)
	 * @see org.concord.sensor.SensorRequest#getUnit()
	 */
	public DataDimension getUnit() 
	{
		String unitStr = resources.getUnit();
		return new SensorUnit(unitStr);
	}
	
	public void setUnit(String unit){
		resources.setUnit(unit);
	}

	/* (non-Javadoc)
	 * @see org.concord.sensor.SensorRequest#getSensorParam(java.lang.String)
	 */
	public String getSensorParam(String key) 
	{
		OTResourceMap map = resources.getParamMap();
		return (String)map.get(key);		
	}
	
	public String [] getSensorParamKeys()
	{
		OTResourceMap map = resources.getParamMap();
		return map.getKeys();
	}
}

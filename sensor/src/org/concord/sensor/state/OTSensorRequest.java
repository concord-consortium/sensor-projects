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
import org.concord.sensor.device.SensorUnit;

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

	/* (non-Javadoc)
	 * @see org.concord.sensor.SensorRequest#getStepSize()
	 */
	public float getStepSize() 
	{
		return resources.getStepSize();
	}

	/* (non-Javadoc)
	 * @see org.concord.sensor.SensorRequest#getDisplayPrecision()
	 */
	public int getDisplayPrecision() 
	{
		return getDisplayPrecision();
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

	/* (non-Javadoc)
	 * @see org.concord.sensor.SensorRequest#getSensorParam(java.lang.String)
	 */
	public String getSensorParam(String key) 
	{
		OTResourceMap map = resources.getParamMap();
		return (String)map.get(key);		
	}
}

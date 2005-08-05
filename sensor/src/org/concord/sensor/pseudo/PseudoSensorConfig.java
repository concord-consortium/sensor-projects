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
 * Created on Jan 25, 2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package org.concord.sensor.pseudo;

import org.concord.framework.data.DataDimension;
import org.concord.sensor.SensorConfig;

/**
 * @author scott
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class PseudoSensorConfig 
	implements SensorConfig 
{
	int type = -1;
	float stepSize = -1;
	int port = -1;
	String name = null;
	private DataDimension unit;
    private float sinOffset = 5;
    private float sinMagnitude = 10;
	
	
	/* (non-Javadoc)
	 * @see org.concord.sensor.SensorConfig#isConfirmed()
	 */
	public boolean isConfirmed() 
	{
		return true;
	}

	/* (non-Javadoc)
	 * @see org.concord.sensor.SensorConfig#getType()
	 */
	public int getType() 
	{
		return type;
	}

	public void setType(int type)
	{
		this.type = type;
	}
		
	/* (non-Javadoc)
	 * @see org.concord.sensor.SensorConfig#getStepSize()
	 */
	public float getStepSize() 
	{
		return stepSize;
	}

	public void setStepSize(float size)
	{
		this.stepSize = size;
	}
	
	/* (non-Javadoc)
	 * @see org.concord.sensor.SensorConfig#getPort()
	 */
	public int getPort() 
	{
		return port;
	}

	public void setPort(int port)
	{
		this.port = port;
	}
	
	/* (non-Javadoc)
	 * @see org.concord.sensor.SensorConfig#getPortName()
	 */
	public String getPortName() 
	{
		return "Pseudo Port " + port;
	}

	/* (non-Javadoc)
	 * @see org.concord.sensor.SensorConfig#getName()
	 */
	public String getName() 
	{
		// FIXME this should take into account the quantity type
		return "PseudoSensor";
	}

	/* (non-Javadoc)
	 * @see org.concord.sensor.SensorConfig#getUnit()
	 */
	public DataDimension getUnit() 
	{
		// TODO Auto-generated method stub
		return unit;
	}

	public void setUnit(DataDimension unit)
	{
		this.unit = unit;
	}
	
	/* (non-Javadoc)
	 * @see org.concord.sensor.SensorConfig#getSensorParam(java.lang.String)
	 */
	public String getSensorParam(String key) 
	{
		// no sensor params in this pseudo sensor
		return null;
	}

    /**
     * @param f
     */
    public void setSinOffset(float f)
    {
        sinOffset = f;        
    }

    public float getSinOffset()
    {
        return sinOffset;
    }
    
    /**
     * @param f
     */
    public void setSinMagnitude(float f)
    {
        sinMagnitude = f;        
    }

    public float getSinMagnitude()
    {
        return sinMagnitude;
    }
}

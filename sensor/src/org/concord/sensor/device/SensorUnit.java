/*
 * Last modification information:
 * $Revision: 1.1 $
 * $Date: 2004-12-01 20:40:41 $
 * $Author: scytacki $
 *
 * Licence Information
 * Copyright 2004 The Concord Consortium 
*/
package org.concord.sensor.device;

import org.concord.framework.data.DataDimension;


/**
 * SensorUnit
 * Class name and description
 *
 * Date created: Nov 30, 2004
 *
 * @author scott<p>
 *
 */
public class SensorUnit
	implements DataDimension
{
	String unit;
	
	/**
	 * 
	 */
	public SensorUnit(String unit)
	{
		this.unit = unit;
	}

	/* (non-Javadoc)
	 * @see org.concord.framework.data.DataDimension#getDimension()
	 */
	public String getDimension()
	{
		return unit;
	}

	/* (non-Javadoc)
	 * @see org.concord.framework.data.DataDimension#setDimension(java.lang.String)
	 */
	public void setDimension(String dimension)
	{
		unit = dimension;
	}
}

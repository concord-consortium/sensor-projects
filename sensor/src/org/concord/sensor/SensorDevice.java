/*
 * Last modification information:
 * $Revision: 1.1 $
 * $Date: 2004-12-01 20:40:41 $
 * $Author: scytacki $
 *
 * Licence Information
 * Copyright 2004 The Concord Consortium 
*/
package org.concord.sensor;

import org.concord.framework.data.stream.DataProducer;


/**
 * SensorDevice
 * Class name and description
 *
 * Date created: Nov 30, 2004
 *
 * @author scott<p>
 *
 */
public interface SensorDevice
	extends DataProducer
{
	public boolean isAttached();
	
	public ExperimentConfig configure(ExperimentConfig experiment);
}

/*
 * Last modification information:
 * $Revision: 1.1 $
 * $Date: 2004-12-24 15:34:59 $
 * $Author: scytacki $
 *
 * Licence Information
 * Copyright 2004 The Concord Consortium 
*/
package org.concord.sensor.device;


/**
 * DeviceReader
 * Class name and description
 *
 * Date created: Dec 24, 2004
 *
 * @author scott<p>
 *
 */
public interface DeviceReader
{

	/**
	 * @param numSamples
	 * @return
	 */
	int flushData(int numSamples);

}

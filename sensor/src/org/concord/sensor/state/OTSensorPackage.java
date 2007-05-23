/**
 * 
 */
package org.concord.sensor.state;

import org.concord.framework.otrunk.OTControllerRegistry;
import org.concord.framework.otrunk.OTPackage;
import org.concord.framework.otrunk.OTrunk;

/**
 * @author scott
 *
 */
public class OTSensorPackage
    implements OTPackage
{

	/* (non-Javadoc)
	 * @see org.concord.framework.otrunk.OTPackage#initialize(org.concord.framework.otrunk.OTrunk)
	 */
	public void initialize(OTrunk otrunk)
	{
		OTControllerRegistry registry = 
			(OTControllerRegistry) otrunk.getService(OTControllerRegistry.class);
		
		registry.registerControllerClass(OTSensorDataProxyController.class);
	}

}

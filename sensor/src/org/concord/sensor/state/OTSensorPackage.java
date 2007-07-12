/**
 * 
 */
package org.concord.sensor.state;

import java.util.ArrayList;
import java.util.List;

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

	public List getOTClasses() 
	{
		ArrayList list = new ArrayList();

		list.add(OTDeviceConfig.class);
		list.add(OTExperimentRequest.class);
		list.add(OTInterfaceManager.class);
		list.add(OTLoggingRequest.class);
		list.add(OTSensorDataProxy.class);
		list.add(OTSensorRequest.class);
		list.add(OTSetupLogger.class);
		list.add(OTZeroSensor.class);
		
		return list;
	}

}

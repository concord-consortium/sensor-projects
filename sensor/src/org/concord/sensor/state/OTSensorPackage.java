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

	public Class [] getOTClasses() 
	{
		return new Class [] {
				OTDeviceConfig.class,
				OTExperimentRequest.class,
				OTInterfaceManager.class,
				OTLoggingRequest.class,
				OTSensorDataProxy.class,
				OTSensorRequest.class,
				OTSetupLogger.class,
				OTZeroSensor.class,
		};
	}

	public Class[] getPackageDependencies() 
	{
		// TODO return a list of dependencies
		return null;
	}

}

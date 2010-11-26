/**
 * 
 */
package org.concord.sensor.state;

import org.concord.data.state.OTDataProducer;

public interface OTSensorDataProxy extends OTDataProducer
{

	// QX: may be tentative.
	public final static boolean DEFAULT_sharable=false;
	public void setSharable(boolean b);
	public boolean getSharable();
	
	public OTExperimentRequest getRequest();
    public void setRequest(OTExperimentRequest request);
    
    public OTZeroSensor getZeroSensor();
    public void setZeroSensor(OTZeroSensor zeroSensor);
    
}
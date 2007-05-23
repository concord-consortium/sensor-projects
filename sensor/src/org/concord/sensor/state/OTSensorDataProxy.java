/**
 * 
 */
package org.concord.sensor.state;

import org.concord.data.state.OTDataProducer;

public interface OTSensorDataProxy extends OTDataProducer
{
	public OTExperimentRequest getRequest();
    public void setRequest(OTExperimentRequest request);
    
    public OTZeroSensor getZeroSensor();
    public void setZeroSensor(OTZeroSensor zeroSensor);
}
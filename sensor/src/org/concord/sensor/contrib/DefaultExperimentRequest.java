/*
 * Created on Feb 22, 2005
 *
 */
 
package org.concord.sensor.contrib;
 
import org.concord.framework.data.DataDimension;
import org.concord.sensor.ExperimentRequest;
import org.concord.sensor.impl.ExperimentConfigImpl;
import org.concord.sensor.SensorConfig;
import org.concord.sensor.SensorRequest;

/**
 * implementation of the ExperimentRequest
 * it's possible to set sensor type
 * @see SensorConfig
 * 
 * @author Dmitry Markman
 *
 */
 
public class DefaultExperimentRequest extends org.concord.sensor.impl.ExperimentRequestImpl 
{
protected int sensorType = SensorConfig.QUANTITY_UNKNOWN;
protected SensorRequest     []sensorRequests;
    public DefaultExperimentRequest(int sensorType){
        this.sensorType = sensorType;
    }	

	/* (non-Javadoc)
	 * @see org.concord.sensor.ExperimentRequest#getSensorRequests()
	 */
	public SensorRequest[] getSensorRequests() {
	    if(sensorRequests == null){
	        sensorRequests = new SensorRequest[1];
		    sensorRequests[0] = new DefaultSensorRequest(sensorType);
	    }
	    return sensorRequests;
	}
	
	public void setSensorRequests(SensorRequest []sensorRequests){
	    this.sensorRequests = sensorRequests;
	}
}

/*
 * Created on Feb 22, 2005
 *
 */
 
package org.concord.sensor.contrib;
 
import org.concord.framework.data.DataDimension;
import org.concord.sensor.SensorConfig;
import org.concord.sensor.SensorRequest;

/**
 * implementation of the SensorRequest
 * it's possible to set sensor type
 * @see SensorConfig
 * @author Dmitry Markman
 *
 */
public class DefaultSensorRequest implements SensorRequest 
{
protected int sensorType = SensorConfig.QUANTITY_UNKNOWN;

    public DefaultSensorRequest(int sensorType){
        this.sensorType = sensorType;
    }	

	/* (non-Javadoc)
	 * @see org.concord.sensor.SensorRequest#getDisplayPrecision()
	 */
	public int getDisplayPrecision() {
		// TODO Auto-generated method stub
		return -2;
	}
	
	/* (non-Javadoc)
	 * @see org.concord.sensor.SensorRequest#getRequiredMax()
	 */
	public float getRequiredMax() 
	{
		return Float.NaN;
	}
			
	/* (non-Javadoc)
	 * @see org.concord.sensor.SensorRequest#getRequiredMin()
	 */
	public float getRequiredMin() 
	{
		return Float.NaN;
	}
	
	/* (non-Javadoc)
	 * @see org.concord.sensor.SensorRequest#getPort()
	 */
	public int getPort() {
		// TODO Auto-generated method stub
		return 0;
	}
			
	/* (non-Javadoc)
	 * @see org.concord.sensor.SensorRequest#getSensorParam(java.lang.String)
	 */
	public String getSensorParam(String key) {
		// TODO Auto-generated method stub
		return null;
	}
	
	/* (non-Javadoc)
	 * @see org.concord.sensor.SensorRequest#getStepSize()
	 */
	public float getStepSize() {
		// TODO Auto-generated method stub
		return 0.1f;
	}
	/* (non-Javadoc)
	 * @see org.concord.sensor.SensorRequest#getType()
	 */
	public int getType() {
		// TODO Auto-generated method stub
		return sensorType;
	}
	/* (non-Javadoc)
	 * @see org.concord.sensor.SensorRequest#getUnit()
	 */
	public DataDimension getUnit() {
		// TODO Auto-generated method stub
		return null;
	}
}

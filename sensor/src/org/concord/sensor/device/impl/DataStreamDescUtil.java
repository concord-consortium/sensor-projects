/*
 * Created on Jan 26, 2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package org.concord.sensor.device.impl;

import org.concord.framework.data.stream.DataChannelDescription;
import org.concord.framework.data.stream.DataStreamDescription;
import org.concord.sensor.ExperimentConfig;
import org.concord.sensor.ExperimentRequest;
import org.concord.sensor.SensorConfig;
import org.concord.sensor.SensorRequest;

/**
 * @author scott
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class DataStreamDescUtil 
{
	/**
	 * The result can be null. 
	 * 
	 * @param dDesc
	 * @param request
	 * @param result
	 */
	public static void setupDescription(DataStreamDescription dDesc,
			ExperimentRequest request, 
			ExperimentConfig result)
	{
		SensorConfig [] sensConfigs = null;
		SensorRequest [] sensRequests = request.getSensorRequests();		
		
		if(result != null) {
			sensConfigs = result.getSensorConfigs();
		}
		
		dDesc.setChannelsPerSample(sensRequests.length);
		
		if(result != null) {
			dDesc.setDt(result.getPeriod());
		} else {
			dDesc.setDt(request.getPeriod());			
		}
		
		dDesc.setDataType(DataStreamDescription.DATA_SEQUENCE);
		
		for(int i=0; i<sensRequests.length; i++) {
			DataChannelDescription chDescrip = new DataChannelDescription();
			if(result != null) {
				chDescrip.setName(sensConfigs[i].getName());
			}
			chDescrip.setUnit(sensRequests[i].getUnit());
			
			chDescrip.setPrecision(sensRequests[i].getDisplayPrecision());			
			chDescrip.setNumericData(true);
		}		
	}
}

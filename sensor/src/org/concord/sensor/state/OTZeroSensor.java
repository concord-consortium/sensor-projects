/*
 *  Copyright (C) 2004  The Concord Consortium, Inc.,
 *  10 Concord Crossing, Concord, MA 01742
 *
 *  Web Site: http://www.concord.org
 *  Email: info@concord.org
 *
 *  This library is free software; you can redistribute it and/or
 *  modify it under the terms of the GNU Lesser General Public
 *  License as published by the Free Software Foundation; either
 *  version 2.1 of the License, or (at your option) any later version.
 *
 *  This library is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *  Lesser General Public License for more details.
 *
 *  You should have received a copy of the GNU Lesser General Public
 *  License along with this library; if not, write to the Free Software
 *  Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 *
 * END LICENSE */

/*
 * Last modification information:
 * $Revision: 1.3 $
 * $Date: 2007-09-26 18:39:31 $
 * $Author: imoncada $
 *
 * Licence Information
 * Copyright 2004 The Concord Consortium 
*/
package org.concord.sensor.state;

import org.concord.data.stream.TaringDataFilter;
import org.concord.framework.data.stream.DataProducer;
import org.concord.framework.otrunk.DefaultOTObject;
import org.concord.framework.otrunk.OTResourceSchema;
import org.concord.framework.otrunk.view.OTAction;
import org.concord.framework.otrunk.view.OTActionContext;
import org.concord.sensor.ExperimentConfig;
import org.concord.sensor.SensorConfig;
import org.concord.sensor.SensorDataProducer;
import org.concord.sensor.ZeroingSensor;

public class OTZeroSensor extends DefaultOTObject
    implements OTAction
{
    public static interface ResourceSchema extends OTResourceSchema
    {
        public int getSensorIndex();
        public void setSensorIndex(int index);
    }

    private ResourceSchema resources;
    private TaringDataFilter dataFilter;
    
    /**
     * probably need some services here
     * @param resources
     */
    public OTZeroSensor(ResourceSchema resources) 
    {
        super(resources);
        this.resources = resources;        
    }
    
    public DataProducer setupDataFilter(SensorDataProducer producer)
    {
    	int index = resources.getSensorIndex();
    	
    	ExperimentConfig expConfig = producer.getCurrentConfig();
    	
    	// Some devices don't correctly return the current config
    	if(expConfig != null && expConfig.getSensorConfigs() != null){    		
        	SensorConfig [] configs = expConfig.getSensorConfigs();
        	
        	if(configs[index] instanceof ZeroingSensor &&
        			((ZeroingSensor)configs[index]).getSupportsZeroing()){
        		((ZeroingSensor)configs[index]).zeroSensor();
        		return producer;
        	}
    	}
    	
    	
    	dataFilter = new TaringDataFilter();
    	dataFilter.addDataProducer(producer);
    	dataFilter.setChannel(resources.getSensorIndex() + 1);
    	return dataFilter;
    }
    
	/**
	 * @see org.concord.framework.otrunk.view.OTAction#doAction(org.concord.framework.otrunk.view.OTActionContext)
     * Send the logg request to the attached sensor interface
	 */
	public void doAction(OTActionContext context)
    {
    	if(dataFilter == null){
    		// FIXME
    		// This would happen if the producer isn't started yet
    		// this will also currently happen if the sensors handle the zeroing
    		// themselves.
    		return;
    	}
    	
    	dataFilter.tare();    	
    }

    public String getActionText()
    {
        return "Zero Sensor";
    }    
}

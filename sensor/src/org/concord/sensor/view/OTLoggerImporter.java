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
 * $Revision: 1.5 $
 * $Date: 2007-06-25 18:53:36 $
 * $Author: scytacki $
 *
 * Licence Information
 * Copyright 2004 The Concord Consortium 
*/
package org.concord.sensor.view;

import org.concord.framework.otrunk.DefaultOTObject;
import org.concord.framework.otrunk.OTResourceSchema;
import org.concord.sensor.SensorDataManager;
import org.concord.sensor.device.SensorDevice;
import org.concord.sensor.device.SensorLoggedRecord;
import org.concord.sensor.device.SensorLogger;
import org.concord.sensor.state.OTExperimentRequest;

public class OTLoggerImporter extends DefaultOTObject
{
    public static interface ResourceSchema extends OTResourceSchema
    {
        OTLoggerRecordChooser getChooser();
        
        OTExperimentRequest getRequest();
    }

    private ResourceSchema resources;
    private SensorDataManager sensorManager;
    
    public OTLoggerImporter(ResourceSchema resources,
            SensorDataManager sdm) 
    {
        super(resources);
        this.resources = resources;
        this.sensorManager = sdm;
    }
    
    public OTExperimentRequest getRequest()
    {
    	return resources.getRequest();
    }
    
    /**
     * Methods on the logger will automatically open it
     * so be sure to close it again.
     * @return
     */
    public SensorLogger getLogger()
    {
        SensorDevice device = sensorManager.getSensorDevice();
        if(!(device instanceof SensorLogger)) {
            return null;
        }

        return (SensorLogger)device;        
    }
    
    public SensorLoggedRecord [] getRecords()
    {
    	SensorLogger logger = getLogger();
    	if(logger == null){
    		return null;
    	}
        SensorLoggedRecord [] records =  logger.getAvailableRecords();

        logger.close();
        return records;
    }

}

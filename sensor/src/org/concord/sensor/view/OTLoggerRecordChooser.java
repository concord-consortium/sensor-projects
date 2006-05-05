/*
 * Last modification information:
 * $Revision: 1.1 $
 * $Date: 2006-05-05 15:44:30 $
 * $Author: scytacki $
 *
 * Licence Information
 * Copyright 2004 The Concord Consortium 
*/
package org.concord.sensor.view;

import java.util.Vector;

import org.concord.framework.otrunk.DefaultOTObject;
import org.concord.framework.otrunk.OTResourceSchema;
import org.concord.sensor.SensorDataManager;
import org.concord.sensor.device.SensorDevice;
import org.concord.sensor.device.SensorLoggedRecord;
import org.concord.sensor.device.SensorLogger;
import org.concord.sensor.state.OTLoggingRequest;

public class OTLoggerRecordChooser extends DefaultOTObject
{
    public static interface ResourceSchema extends OTResourceSchema
    {
    }

    private ResourceSchema resources;
    private SensorDataManager sensorManager;

    public OTLoggerRecordChooser(ResourceSchema resources, 
            SensorDataManager sdm) {
        super(resources);
        this.resources = resources;
        this.sensorManager = sdm;
    }
    
    SensorLogger getLogger()
    {
        SensorDevice device = sensorManager.getSensorDevice();
        if(!(device instanceof SensorLogger)) {
            return null;
        }

        return (SensorLogger)device;
    }
    
    SensorLoggedRecord [] getRecords()
    {
        SensorDevice device = sensorManager.getSensorDevice();
        if(!(device instanceof SensorLogger)) {
            return null;
        }

        SensorLogger logger = (SensorLogger)device;
        SensorLoggedRecord [] records =  logger.getAvailableRecords();
        
        return records;
    }
}

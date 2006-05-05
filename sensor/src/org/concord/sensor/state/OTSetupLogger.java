/*
 * Last modification information:
 * $Revision: 1.1 $
 * $Date: 2006-05-05 15:44:31 $
 * $Author: scytacki $
 *
 * Licence Information
 * Copyright 2004 The Concord Consortium 
*/
package org.concord.sensor.state;

import org.concord.framework.otrunk.DefaultOTObject;
import org.concord.framework.otrunk.OTResourceSchema;
import org.concord.framework.otrunk.view.OTAction;
import org.concord.sensor.SensorDataManager;
import org.concord.sensor.device.SensorDevice;
import org.concord.sensor.device.SensorLogger;

public class OTSetupLogger extends DefaultOTObject
    implements OTAction
{
    public static interface ResourceSchema extends OTResourceSchema
    {
        OTLoggingRequest getRequest();
        void setRequest(OTLoggingRequest request);
    }

    private ResourceSchema resources;
    private SensorDataManager sensorManager;
    
    /**
     * probably need some services here
     * @param resources
     */
    public OTSetupLogger(ResourceSchema resources, 
            SensorDataManager sdm) 
    {
        super(resources);
        this.resources = resources;        
        this.sensorManager = sdm;
    }
    
    /**
     * Send the logg request to the attached sensor interface
     */
    public void doAction()
    {
        // get the logger device
        // send the logging request to that
        SensorLogger logger = getLogger();
        
        // This could potentially return an error if
        // there isn't enough memory.  
        logger.sendLoggingRequest(resources.getRequest());
        
        logger.close();
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

    public String getActionText()
    {
        return "Setup Logger";
    }
}

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
 * $Revision: 1.2 $
 * $Date: 2006-05-05 15:46:09 $
 * $Author: maven $
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

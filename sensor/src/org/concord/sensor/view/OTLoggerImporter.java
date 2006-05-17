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
 * $Revision: 1.4 $
 * $Date: 2006-05-17 19:56:43 $
 * $Author: scytacki $
 *
 * Licence Information
 * Copyright 2004 The Concord Consortium 
*/
package org.concord.sensor.view;

import org.concord.framework.data.stream.DataStoreCollection;
import org.concord.framework.data.stream.DataStreamDescription;
import org.concord.framework.data.stream.WritableArrayDataStore;
import org.concord.framework.otrunk.DefaultOTObject;
import org.concord.framework.otrunk.OTResourceSchema;
import org.concord.framework.otrunk.view.OTAction;
import org.concord.sensor.ExperimentConfig;
import org.concord.sensor.SensorConfig;
import org.concord.sensor.SensorDataManager;
import org.concord.sensor.device.SensorDevice;
import org.concord.sensor.device.SensorLoggedRecord;
import org.concord.sensor.device.SensorLogger;
import org.concord.sensor.impl.DataStreamDescUtil;
import org.concord.sensor.state.OTExperimentRequest;

public class OTLoggerImporter extends DefaultOTObject
    implements OTAction
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
    
    public void doAction()
    {
        System.out.println("Got action");
    }
    
    public String getActionText()
    {
        return "Import";
    }
    
    public void importData(DataStoreCollection collection, 
            SensorLoggedRecord record)
    {
        
       int numSamples = record.getNumSamples();
       
       OTExperimentRequest request = resources.getRequest();
       
       ExperimentConfig config = record.initializeRead(request);
       
       int currentSample = 0;
       // This isn't quite right it needs to be multiplied by the 
       // sample size.
       float [] values = new float [numSamples*8];
       while(currentSample < numSamples){
           int numRead = 
               record.read(currentSample, values, currentSample, 8);
           currentSample += numRead;
       }
       
       SensorConfig [] sensorConfigs = config.getSensorConfigs();
       int sensorIndex = 1;
       for(int i=0; i<numSamples; i++){
           System.out.println("" + values[i*8+sensorIndex] + " " + 
                   sensorConfigs[sensorIndex].getName());
       }
       
       // need to create a DataStore 
       // and then add it to the collection
       WritableArrayDataStore dataStore = collection.createDataStore();

       dataStore.setDt(record.getLoggedConfig().getPeriod());

       DataStreamDescription dDesc = new DataStreamDescription();
       DataStreamDescUtil.setupDescription(dDesc, request, config);
       dataStore.setDataChannelDescription(-1, dDesc.getDtChannelDescription());
       for(int i=0; i<dDesc.getChannelsPerSample(); i++){
           dataStore.setDataChannelDescription(i, dDesc.getChannelDescription(i));
       }

       
       
       dataStore.setValues(sensorConfigs.length, values, 0, numSamples, 8);
       
       collection.addDataStore(record.getDescription(), dataStore);
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

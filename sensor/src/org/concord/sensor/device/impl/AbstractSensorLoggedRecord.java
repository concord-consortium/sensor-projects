/*
 * Last modification information:
 * $Revision: 1.1 $
 * $Date: 2006-05-05 15:44:30 $
 * $Author: scytacki $
 *
 * Licence Information
 * Copyright 2004 The Concord Consortium 
*/
package org.concord.sensor.device.impl;

import org.concord.sensor.DeviceTime;
import org.concord.sensor.ExperimentConfig;
import org.concord.sensor.LoggingRequest;
import org.concord.sensor.device.SensorLoggedRecord;
import org.concord.sensor.impl.ExperimentConfigImpl;

public abstract class AbstractSensorLoggedRecord
    implements SensorLoggedRecord
{
    protected String description;
    protected ExperimentConfig loggedConfig;
    protected int numSamples;
    protected DeviceTime startTime;
    protected int startCondition;
    protected int triggerPosition;
    protected int preTriggerSamples;
    protected int triggerChannel;
    protected float triggerValue;
    protected int numSamplesToTake;
    
    public String getDescription()
    {
        return description;
    }

    public ExperimentConfig getLoggedConfig()
    {
        return loggedConfig;
    }

    public int getNumSamples()
    {
        return numSamples;
    }

    public DeviceTime getStartTime()
    {
        return startTime;
    }

    public int getStartCondition()
    {
        return startCondition;
    }
    
    public void configure(ExperimentConfigImpl expConfig, 
            LoggingRequest request)
    {
        startTime = request.getStartTime();
        startCondition = request.getStartCondition();
        triggerPosition = request.getTriggerPosition();
        preTriggerSamples = request.getPreTriggerSamples();
        triggerChannel = request.getTriggerChannel();
        triggerValue = request.getTriggerValue();
        numSamplesToTake = request.getNumberOfSamples();
        
        // now we have the values from the experiment request parts 
        // these include sensors, and periods
        if(expConfig == null){
            expConfig = new ExperimentConfigImpl();            
        }
        expConfig.setPeriod(request.getPeriod());
        loggedConfig = expConfig;
    }
    

}

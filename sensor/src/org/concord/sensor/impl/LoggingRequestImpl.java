/*
 * Last modification information:
 * $Revision: 1.1 $
 * $Date: 2006-05-05 15:44:30 $
 * $Author: scytacki $
 *
 * Licence Information
 * Copyright 2004 The Concord Consortium 
*/
package org.concord.sensor.impl;

import org.concord.sensor.DeviceTime;
import org.concord.sensor.LoggingRequest;

public class LoggingRequestImpl extends ExperimentRequestImpl
    implements LoggingRequest
{
    protected int startCondition;
    protected DeviceTime startTime;
    protected int triggerPosition;
    protected int preTriggerSamples;
    protected int triggerChannel;
    protected float triggerValue;
    
    public int getStartCondition()
    {
        return startCondition;
    }

    public DeviceTime getStartTime()
    {
        return startTime;
    }

    public int getTriggerPosition()
    {
        return triggerPosition;
    }

    public int getPreTriggerSamples()
    {
        return preTriggerSamples;
    }

    public int getTriggerChannel()
    {
        return triggerChannel;
    }

    public float getTriggerValue()
    {
        return triggerValue;
    }

}

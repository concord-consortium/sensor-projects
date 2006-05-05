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

import java.text.DateFormat;
import java.text.ParseException;
import java.util.Calendar;
import java.util.Date;

import org.concord.sensor.DeviceTime;
import org.concord.sensor.LoggingRequest;

public class OTLoggingRequest extends OTExperimentRequest
    implements LoggingRequest
{
    public static interface ResourceSchema 
        extends OTExperimentRequest.ResourceSchema{
        
        public static int DEFAULT_preTriggerSamples = 0;
        public int getPreTriggerSamples();
        
        public static int DEFAULT_startCondition = 0;
        public int getStartCondition();
        
        public String getStartTime();
        
        public static int DEFAULT_triggerChannel = 0;
        public int getTriggerChannel();
        
        public static int DEFAULT_triggerPosition = 0;
        public int getTriggerPosition();
        
        public static float DEFAULT_triggerValue = 0f;
        public float getTriggerValue();
    };
    private ResourceSchema resources;

    
    public OTLoggingRequest(ResourceSchema resources)
    {
        super(resources);
        this.resources = resources;
    }

    public int getStartCondition()
    {
        return resources.getStartCondition();
    }

    public DeviceTime getStartTime()
    {
        String startTime = resources.getStartTime();
        
        if(startTime == null) {
            return null;            
        }
        
        DateFormat dateFormat = DateFormat.getDateInstance();
        try {
            Date date = dateFormat.parse(startTime);
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(date);
            return new DeviceTime(
                    calendar.get(Calendar.YEAR),
                    calendar.get(Calendar.MONTH),
                    calendar.get(Calendar.DAY_OF_MONTH),
                    calendar.get(Calendar.HOUR_OF_DAY),
                    calendar.get(Calendar.MINUTE),
                    calendar.get(Calendar.SECOND));
        } catch (ParseException e){
            e.printStackTrace();
        }
        // TODO Auto-generated method stub
        return null;
    }

    public int getTriggerPosition()
    {
        return resources.getTriggerPosition();
    }

    public int getPreTriggerSamples()
    {
        return resources.getPreTriggerSamples();
    }

    public int getTriggerChannel()
    {
        return resources.getTriggerChannel();
    }

    public float getTriggerValue()
    {
        return resources.getTriggerValue();
    }

}

/*
 * Last modification information:
 * $Revision: 1.1 $
 * $Date: 2006-05-05 15:44:30 $
 * $Author: scytacki $
 *
 * Licence Information
 * Copyright 2004 The Concord Consortium 
*/
package org.concord.sensor;

public class DeviceTime
{
    int year;
    int month;
    int day;
    int hour;
    int min;
    int sec;
    
    public DeviceTime(int year, int month, int day,
                     int hour, int min, int sec)
    {
        this.year = year;
        this.month = month;
        this.day = day;
        this.hour = hour;
        this.min = min;
        this.sec = sec;

    }

    public String getBasicString()
    {
        return "" + month + "/" + day + "/" + year + " " +
           hour + ":" + min + ":" + sec; 
    }
    
    public int getYear()
    {
        return year;
    }
    
    public int getMonth()
    {
        return month;
    }
    
    public int getDay()
    {
        return day;
    }
    
    public int getHour()
    {
        return hour;
    }
    
    public int getMin()
    {
        return min;
    }
    
    public int getSec()
    {
        return sec;
    }
}

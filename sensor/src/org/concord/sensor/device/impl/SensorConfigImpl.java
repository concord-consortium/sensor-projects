/*
 * Created on Feb 28, 2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package org.concord.sensor.device.impl;

import org.concord.framework.data.DataDimension;
import org.concord.sensor.SensorConfig;

/**
 * @author scott
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class SensorConfigImpl
    implements SensorConfig
{
    private boolean confirmed;
    private int port;
    private int type;
    private String name;
    private String portName;
    private float stepSize;
    private DataDimension unit;

    /* (non-Javadoc)
     * @see org.concord.sensor.SensorConfig#isConfirmed()
     */
    public boolean isConfirmed()
    {
        return confirmed;
    }

    public void setConfirmed(boolean flag)
    {
        confirmed = flag;
    }
    
    /* (non-Javadoc)
     * @see org.concord.sensor.SensorConfig#getType()
     */
    public int getType()
    {
        return type;
    }

    public void setType(int type)
    {
        this.type = type;
    }
    
    /* (non-Javadoc)
     * @see org.concord.sensor.SensorConfig#getStepSize()
     */
    public float getStepSize()
    {
        // TODO Auto-generated method stub
        return stepSize;
    }

    /**
     * @param stepSize The stepSize to set.
     */
    public void setStepSize(float stepSize)
    {
        this.stepSize = stepSize;
    }
    
    /* (non-Javadoc)
     * @see org.concord.sensor.SensorConfig#getPort()
     */
    public int getPort()
    {
        return port;
    }

    /**
     * @param port The port to set.
     */
    public void setPort(int port)
    {
        this.port = port;
    }
    
    /* (non-Javadoc)
     * @see org.concord.sensor.SensorConfig#getPortName()
     */
    public String getPortName()
    {
        // TODO Auto-generated method stub
        return portName;
    }

    /**
     * @param portName The portName to set.
     */
    public void setPortName(String portName)
    {
        this.portName = portName;
    }
    
    /* (non-Javadoc)
     * @see org.concord.sensor.SensorConfig#getName()
     */
    public String getName()
    {
        // TODO Auto-generated method stub
        return name;
    }
    
    /**
     * @param name The name to set.
     */
    public void setName(String name)
    {
        this.name = name;
    }

    /* (non-Javadoc)
     * @see org.concord.sensor.SensorConfig#getUnit()
     */
    public DataDimension getUnit()
    {
        // TODO Auto-generated method stub
        return unit;
    }

    /**
     * @param unit The unit to set.
     */
    public void setUnit(DataDimension unit)
    {
        this.unit = unit;
    }
    
    /* (non-Javadoc)
     * @see org.concord.sensor.SensorConfig#getSensorParam(java.lang.String)
     */
    public String getSensorParam(String key)
    {
        // TODO Auto-generated method stub
        return null;
    }

}

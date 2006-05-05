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

import java.awt.BorderLayout;

import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;

import org.concord.framework.otrunk.OTObject;
import org.concord.framework.otrunk.view.OTObjectView;
import org.concord.framework.otrunk.view.OTViewContainer;
import org.concord.sensor.DeviceTime;
import org.concord.sensor.device.SensorLoggedRecord;
import org.concord.sensor.device.SensorLogger;

public class OTLoggerRecordChooserView 
    implements OTObjectView
{
    protected OTViewContainer viewContainer;
    protected OTLoggerRecordChooser chooser;
    
    public void initialize(OTObject otObject, OTViewContainer viewContainer)
    {
        this.viewContainer = viewContainer;
        chooser = (OTLoggerRecordChooser)otObject;
    }

    public JComponent getComponent(boolean editable)
    {
        // This should return a list of the records
        SensorLogger logger = chooser.getLogger();
        if(logger == null) {
            return new JLabel("null logger");
        }
        SensorLoggedRecord [] records = 
            logger.getAvailableRecords();
        
        if(records == null) {
            return new JLabel("null records");        
        }
        DeviceTime devTime = logger.getLoggerCurrentTime();
        logger.close();

        JPanel recordChooser = new JPanel(new BorderLayout());
        recordChooser.add(new JList(records), BorderLayout.CENTER);
        recordChooser.add(new JLabel("Current Device Time: " + devTime.getBasicString()),
                BorderLayout.NORTH);
        
        
        return recordChooser;
    }

    public void viewClosed()
    {
        // TODO Auto-generated method stub
        
    }
    // need to retrieve a vector of records from the logger
    // add them to a visible list
    // need a logger sensor interface device to query     
}

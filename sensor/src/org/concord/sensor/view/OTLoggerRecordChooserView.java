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

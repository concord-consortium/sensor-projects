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
 * $Revision: 1.6 $
 * $Date: 2007-06-25 18:53:36 $
 * $Author: scytacki $
 *
 * Licence Information
 * Copyright 2004 The Concord Consortium 
*/
package org.concord.sensor.view;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import org.concord.framework.data.stream.DataStoreCollection;
import org.concord.framework.data.stream.DataStoreImporter;
import org.concord.framework.data.stream.DataStreamDescription;
import org.concord.framework.data.stream.WritableArrayDataStore;
import org.concord.framework.otrunk.OTObject;
import org.concord.framework.otrunk.view.OTJComponentView;
import org.concord.sensor.DeviceTime;
import org.concord.sensor.ExperimentConfig;
import org.concord.sensor.SensorConfig;
import org.concord.sensor.device.SensorLoggedRecord;
import org.concord.sensor.device.SensorLogger;
import org.concord.sensor.impl.DataStreamDescUtil;
import org.concord.sensor.state.OTExperimentRequest;

public class OTLoggerImporterView extends JPanel
    implements OTJComponentView, DataStoreImporter
{
	/**
	 * Not intended to be serialized, just added remove compile warning
	 */
	private static final long serialVersionUID = 1L;
	
    protected OTLoggerImporter importer;
    protected DataStoreCollection collection;

    // I believe there is a way to get the frame 
    // that should be used here instead of null
    JDialog dialog = new JDialog((JFrame)null, "Record Chooser");
    JComponent dialogBody;
    JList recordList;
    JButton select;
    
    public JComponent getComponent(OTObject otObject, boolean editable)
    {
        importer = (OTLoggerImporter)otObject;
        
        select = new JButton("Select");
        JButton cancel = new JButton("Cancel");
        
        Box buttonBox = Box.createHorizontalBox();
        buttonBox.add(Box.createGlue());
        buttonBox.add(cancel);
        buttonBox.add(Box.createHorizontalStrut(6));
        buttonBox.add(select);
        Container contentPane = dialog.getContentPane();
        contentPane.add(buttonBox, BorderLayout.SOUTH);
        
        select.addActionListener(new ActionListener(){

            public void actionPerformed(ActionEvent arg0)
            {
                SensorLoggedRecord selectedRecord = 
                    (SensorLoggedRecord)recordList.getSelectedValue();
                dialog.setVisible(false);
                importData(collection, selectedRecord); 
            }            
        });
        
        cancel.addActionListener(new ActionListener(){

            public void actionPerformed(ActionEvent arg0)
            {
                dialog.setVisible(false);
            }            
        });
        
        JButton button = new JButton("Import Record");
        button.addActionListener(new ActionListener(){

            public void actionPerformed(ActionEvent arg0)
            {
                // need to open a dialog to ask about 
                // which record
                Container contentPane = dialog.getContentPane();
 
                if(dialogBody != null) {
                    contentPane.remove(dialogBody);
                }

                // This should return a list of the records
                SensorLogger logger = importer.getLogger();
                if(logger == null){
                    System.out.println("null logger");
                    return;
                }
                SensorLoggedRecord [] records = logger.getAvailableRecords();
                        
                if(records == null) {
                    dialogBody =  new JLabel("null records");
                    recordList = null;
                } else {
                    DeviceTime devTime = logger.getLoggerCurrentTime();
                    System.out.println("dev Time: " + devTime.getBasicString());
                    logger.close();
                    recordList = new JList(records);
                    
                    // disable select button until user
                    // selects an item
                    select.setEnabled(false);
                    recordList.addListSelectionListener(new ListSelectionListener(){

						public void valueChanged(ListSelectionEvent event) {
							if(recordList.getSelectedValue() != null){
								select.setEnabled(true);
							} else {
								select.setEnabled(false);
							}
						}
                    	
                    });
                    JPanel recordChooser = new JPanel(new BorderLayout());
                    recordChooser.add(recordList, BorderLayout.CENTER);
                    recordChooser.add(new JLabel("Current Device Time: " + devTime.getBasicString()),
                            BorderLayout.NORTH);
                    dialogBody = recordChooser;
                }
                
                contentPane.add(dialogBody, BorderLayout.CENTER);

                dialog.setSize(400,400);
                
                dialog.setVisible(true);
            }
            
        });
        
        // This is the usual swing layout hack.  I want to make sure
        // the returned JPanel has similar properties to a button.  
        // it should have the prefered size of the button, but the 
        // container it is being added to will want to stretch it to 
        // the width of the container.  The following doesn't really
        // do that but it will have to do.  SpringLayout might be able
        // to do this correctly.
        this.setLayout(new FlowLayout());
        button.setAlignmentX(0.5f);        		
        this.add(button);
        
        return this;
    }

    public void viewClosed()
    {
        // TODO Auto-generated method stub
        
    }
    // need to retrieve a vector of records from the logger
    // add them to a visible list
    // need a logger sensor interface device to query     

    public void setDataStoreCollection(DataStoreCollection collection)
    {
        this.collection = collection;
    }
    
    public void importData(DataStoreCollection collection, 
            SensorLoggedRecord record)
    {
        
       int numSamples = record.getNumSamples();
       
       OTExperimentRequest request = importer.getRequest();
       
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

}

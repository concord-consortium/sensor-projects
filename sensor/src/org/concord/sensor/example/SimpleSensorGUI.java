/*
 * Created on Dec 10, 2004
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package org.concord.sensor.example;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

import org.concord.framework.data.DataDimension;
import org.concord.framework.data.stream.DataConsumer;
import org.concord.framework.data.stream.DataListener;
import org.concord.framework.data.stream.DataProducer;
import org.concord.framework.data.stream.DataStreamEvent;
import org.concord.framework.text.UserMessageHandler;
import org.concord.sensor.DeviceConfig;
import org.concord.sensor.ExperimentRequest;
import org.concord.sensor.SensorDataManager;
import org.concord.sensor.SensorDataProducer;
import org.concord.sensor.SensorDataConsumer;
import org.concord.sensor.SensorRequest;
import org.concord.sensor.device.impl.DeviceConfigImpl;
import org.concord.sensor.device.impl.InterfaceManager;
import org.concord.sensor.device.impl.JavaDeviceFactory;
import org.concord.sensor.impl.ExperimentRequestImpl;
import org.concord.sensor.contrib.SimpleSensorDataConsumer;
import org.concord.datagraph.engine.DataGraphable;
import org.concord.datagraph.ui.DataGraph;
import org.concord.datagraph.ui.DataGraphActions;

/**
 * @author Informaiton Services
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 * gcc  -o libSetDylibPath.jnilib  SetDylibPath.c   "-arch" "ppc" "-bundle"  build string 
 * install path in the libvernier_ccsd.jnilib should be./libGoIO.dylib
 */
public class SimpleSensorGUI extends JPanel
{
JTextArea            textArea;
SensorDataProducer   dataProducer;
DataGraph           graph;
DataGraphable       dataGraphable = null;
    public SimpleSensorGUI(){
	    initGUI();
		dataProducer = initHardware();
		initGraph();
    }

    public void close(){
        stopSensor();
        if(dataProducer != null) dataProducer.close();
    }

	public static void main(String[] args) 
	{
	    final SimpleSensorGUI sensorComponent = new SimpleSensorGUI();
        try{
            Runtime.getRuntime().addShutdownHook(new Thread(new Runnable(){
                public void run(){
                    try{
                        System.out.println("SHUTDOWNHOOK");
                        sensorComponent.close();
                    }catch(Throwable t){}
                }
            }));
        }catch(Throwable t){}	
	    JFrame frame = new JFrame("Sensor GUI Test");
	    Container container = frame.getContentPane();
	    container.setLayout(new BorderLayout());
	    container.add(sensorComponent,BorderLayout.CENTER);
	    frame.pack();
	    frame.addWindowListener(new WindowAdapter(){
            public void windowClosing(WindowEvent evt){
		        System.exit(0);
            }
	    });
	
	    frame.setVisible(true);
	}

	public void startSensor(){
		if(dataProducer != null) dataProducer.start();
	}

	public void stopSensor(){
		if(dataProducer == null) return;
        dataProducer.stop();
	}

	SensorDataProducer initHardware(){
		//SensorDataConsumer consumer = new SimpleGUIDataConsumer(textArea);
		//return InterfaceManager.getDataProducerForDevice(JavaDeviceFactory.VERNIER_GO_LINK,consumer);
		return InterfaceManager.getDataProducerForDeviceNoConsumer(JavaDeviceFactory.VERNIER_GO_LINK);
	}
	
	void initGraph(){
        graph = new DataGraph();
		if (graph != null) {
		    if(dataProducer != null){
			    dataGraphable = graph.createDataGraphable(dataProducer);
			    dataGraphable.setColor(255, 0, 0);//red
			    dataGraphable.setConnectPoints(true);
			    graph.addDataGraphable(dataGraphable);
			    dataGraphable.setAutoRepaintData(true);
			}
			graph.setLimitsAxisWorld(0, 100, 0, 40);
			
			graph.getGrid().getXGrid().setIntervalFixedDisplay(1);
			graph.getGrid().getYGrid().setIntervalFixedDisplay(1);

			//This won't let the data graphable repaint itself everytime it
			// receives data
		}
	    JScrollPane scrollPane = new JScrollPane(graph);
	    scrollPane.setPreferredSize(new Dimension(300,300));
	    add(scrollPane,BorderLayout.CENTER);
	}
	void initGUI(){

	    setLayout(new BorderLayout());

	    textArea = new JTextArea();
	   /* JScrollPane scrollPane = new JScrollPane(textArea);
	    scrollPane.setPreferredSize(new Dimension(300,300));
	    setLayout(new BorderLayout());
	    add(scrollPane,BorderLayout.CENTER);*/
	    
	    Box buttonBox = Box.createHorizontalBox();
	    JButton clearB = new JButton("Clear");
	    buttonBox.add(clearB);
	    JButton startB = new JButton("Start");
	    buttonBox.add(startB);
	    JButton stopB = new JButton("Stop");
	    buttonBox.add(stopB);
        clearB.addActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent evt){
                SwingUtilities.invokeLater(new Runnable(){
                    public void run(){
                        textArea.setText("");
                    }
                });
            }
        });
        startB.addActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent evt){
                SwingUtilities.invokeLater(new Runnable(){
                    public void run(){
                        startSensor();
                    }
                });
            }
        });
        stopB.addActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent evt){
                SwingUtilities.invokeLater(new Runnable(){
                    public void run(){
                        stopSensor();
                    }
                });
            }
        });

	   add(buttonBox,BorderLayout.SOUTH);
	    
	}
}

class UserMessageHandlerImpl
implements UserMessageHandler
{

	/**
	 * @see org.concord.framework.text.UserMessageHandler#showOptionMessage(java.lang.String, java.lang.String, java.lang.String[], java.lang.String)
	 */
	public int showOptionMessage(String message, String title, String[] options, String defaultOption) {
		System.out.println(title + ": " + message);
		String optionStr = "(";
		for(int i=0; i<options.length; i++) {
			optionStr += " " + options[i];
			if(options[i].equals(defaultOption)){
				optionStr += "+";
			}
		}
		System.out.println(optionStr + " )");
		return 0;
	}

	/**
	 * @see org.concord.framework.text.UserMessageHandler#showMessage(java.lang.String, java.lang.String)
	 */
	public void showMessage(String message, String title) {
		System.out.println(title + ": " + message);
	}
	
}

class SimpleGUIDataConsumer extends SimpleSensorDataConsumer{
JTextArea textArea;
    SimpleGUIDataConsumer(JTextArea textArea){
        this.textArea = textArea;
    }
	public void addDataProducer(DataProducer source) {
	    if(source instanceof SensorDataProducer){
		    setSensorDataProducer((SensorDataProducer)source);
		    dataListener = new SimpleDataListenerGUI();
		    source.addDataListener(dataListener);
        }
	}
    class SimpleDataListenerGUI implements DataListener{
        String eol = System.getProperty("line.separator");
    	public void dataReceived(DataStreamEvent dataEvent)
    	{
    		int numSamples = dataEvent.getNumSamples();
    		float [] data = dataEvent.getData();
    		if(numSamples > 0) {
    		    String newString = "" + numSamples + " " + data[0]+eol;
    		    if(textArea == null){
        			System.out.println(newString);
        			System.out.flush();
        			return;
        	    }
        	    textArea.append(newString);
    		} 
    		else {
    			System.out.println("" + numSamples);
    		}
    	}

    	public void dataStreamEvent(DataStreamEvent dataEvent)
    	{				
    		String eventString;
    		int eventType = dataEvent.getType();
    		
    		if(eventType == 1001) return;
    		
    		switch(eventType) {
    			case DataStreamEvent.DATA_READY_TO_START:
    				eventString = "Ready to start";
    			break;
    			case DataStreamEvent.DATA_STOPPED:
    				eventString = "Stopped";
    			break;
    			case DataStreamEvent.DATA_DESC_CHANGED:
    				eventString = "Description changed";
    			break;
    			default:
    				eventString = "Unknown event type";					
    		}
    		
    		System.out.println("Data Event: " + eventString); 
    	}
    }
}



	/*
 * Created on Jun 18, 2004
 *
 */
package org.concord.sensor.example;

import org.concord.framework.data.stream.*;
import org.concord.framework.text.UserMessageHandler;

import org.concord.sensor.*;
import org.concord.sensor.cc.CCSensorProducer;
//import org.concord.waba.extra.util.PropObject;

/**
 * @author scott
 *
 */
public class Test
{
	public static void main(String [] args)
	{		
		SensorFactory.setTicker(new JavaTicker());
		SensorFactory.setUserMessageHandler(new MyUserMessageHandler());
		
		// This should be done by some system settings 
		SensorFactory.registerProducer(new CCSensorProducer());
		
		SensorProducer [] producers = SensorFactory.getProducers();
		
		for(int i=0; i<producers.length; i++) {
			String [] interfaceNames = producers[i].getInterfaceNames();
			
			for(int j=0; j<interfaceNames.length; j++) {
				System.out.println(interfaceNames[j]);
				
				int interfaceId = producers[i].getInterfaceId(interfaceNames[j]);				
			}
			
			String [] sensorNames = producers[i].getSensorNames();
			for(int k=0; k<sensorNames.length; k++) {
				System.out.println(sensorNames[k]);				
			}
		}
		
		// 2 port interface
		Sensor tempSensor = 
			producers[0].createSensor(CCSensorProducer.SENSOR_THERMAL_COUPLE);

/*
  	// Uncomment this if you want to see
	// The raw data (voltage)

		Sensor tempSensor = 
			producers[0].createSensor(CCSensorProducer.SENSOR_RAW_DATA);
		PropObject channelProp = tempSensor.getProperty("Channel");
		channelProp.setValue("1");
*/		
		InterfaceManager interfaceManager = 
			producers[0].createInterface(CCSensorProducer.INTERFACE_1);
		// version info:
		// version 2 :CCA2D2v..
		// version 1 :CC A2D24v..
		// we also can tell by sending a 'c' and if it responds with a 'C' 
		// or a '?' that will tell us which one it is. 
		
		interfaceManager.addSensor(tempSensor);
		tempSensor.setInterface(interfaceManager);
		
		tempSensor.addDataListener(new DataListener(){
			public void dataReceived(DataStreamEvent dataEvent)
			{
				int numSamples = dataEvent.getNumSamples();
				float [] data = dataEvent.getData();
				if(numSamples > 0) {
					System.out.println("" + numSamples + " " +
								data[0]);
					System.out.flush();
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
		});
		
		MyShutdown sh = new MyShutdown(tempSensor);
        Runtime.getRuntime().addShutdownHook(sh);
		
		tempSensor.startSensor();
	}
}

class MyShutdown extends Thread
{
	Sensor sensor;
	
	public MyShutdown(Sensor s)
	{
		sensor = s;
	}
	
	public void run()
	{
		sensor.stop();
	}
}

class MyUserMessageHandler
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


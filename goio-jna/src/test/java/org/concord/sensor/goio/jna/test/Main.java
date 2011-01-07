package org.concord.sensor.goio.jna.test;

import java.io.IOException;

import org.concord.sensor.goio.jna.GoIOJNALibrary;
import org.concord.sensor.goio.jna.GoIOLibrary;


//Test GoIO
public class Main {
	
	

	public static void main(String[] args) throws IOException {
		
		boolean sweet = false;
		boolean isthere = false;
		GoIOLibrary goio;
		
		goio = new GoIOLibrary();
		
		System.out.println("start main");
		
		if(!goio.initLibrary())
		{
			System.out.println("goIOInterface.initLibrary() failed --bye");
			return;
		}
		
		if(goio.init() != 0)
		{
			System.out.println("goIOInterface.init() failed --bye");
			return;			
		}
		
		GoIOLibrary.GoIOSensor sensor = goio.mkSensor();
		


		isthere = goio.isGolinkAttached();
		System.out.println("Is golink there: "+isthere);		
	
		isthere = goio.getDeviceName(sensor);
		System.out.println("Got device name: "+isthere);
		
		goio.sensorOpen(sensor);

		sweet = goio.sensorSetMeasurementPeriod(sensor,0.040, GoIOJNALibrary.SKIP_TIMEOUT_MS_DEFAULT);
		System.out.println("sensorSetMeasurementPeriod: "+sweet);
		

		goio.sensorStartCollectingData(sensor);
		
		System.out.println("sensorStartCollectingData: "+sweet);
		
		//skulk for ~a sec
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			System.out.println("Bad sleep");
		}
		
		
		//Read from sensor:
		int MAX_NUM_MEASUREMENTS = 100;
	    int []ret = goio.sensorReadRawMeasuements(sensor,MAX_NUM_MEASUREMENTS);	
	    
	    System.out.println("sensorReadRawMeasuements: number of bytes read: "+ret.length);
		
		//print the acquired data:
		int i = 0;
		 
		for(i=0;i<ret.length;i++)
		{
			System.out.println("> "+i+" "+ret[i]);
		}
		
		
		//end
		goio.uninit();
		
		System.out.println("end  main");
	};//end main

}


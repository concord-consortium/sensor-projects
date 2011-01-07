package org.concord.sensor.goio.jna.test;

import java.io.IOException;

import org.concord.sensor.goio.jna.GoIOJNALibrary;
import org.concord.sensor.goio.jna.GoIOLibrary;


//Test GoIO
public class Main {
	
	

	public static void main(String[] args) throws IOException {
		
		boolean sweet = false;
		boolean isthere = false;
		GoIOLibrary goIOLibrary;
		
		goIOLibrary = new GoIOLibrary();
		
		System.out.println("start main");
		
		sweet = goIOLibrary.init();
		

		GoIOLibrary.GoIOSensor sensor = goIOLibrary.mkSensor();
		
		if(!sweet)
		{
			System.out.println("goIOInterface.init() failed --bye");
			return;
		}


		isthere = goIOLibrary.isGolinkAttached();
		System.out.println("Is golink there: "+isthere);		
	
		isthere = goIOLibrary.getDeviceName(sensor);
		System.out.println("Got device name: "+isthere);
		
		goIOLibrary.sensorOpen(sensor);

		sweet = goIOLibrary.sensorSetMeasurementPeriod(sensor,0.040, GoIOJNALibrary.SKIP_TIMEOUT_MS_DEFAULT);
		System.out.println("sensorSetMeasurementPeriod: "+sweet);
		

		goIOLibrary.sensorStartCollectingData(sensor);
		
		System.out.println("sensorStartCollectingData: "+sweet);
		
		//skulk for ~a sec
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			System.out.println("Bad sleep");
		}
		
		
		//Read from sensor:
		int MAX_NUM_MEASUREMENTS = 100;
	    int []ret = goIOLibrary.sensorReadRawMeasuements(sensor,MAX_NUM_MEASUREMENTS);	
	    
	    System.out.println("sensorReadRawMeasuements: number of bytes read: "+ret.length);
		
		//print the acquired data:
		int i = 0;
		 
		for(i=0;i<ret.length;i++)
		{
			System.out.println("> "+i+" "+ret[i]);
		}
		
		
		//end
		goIOLibrary.cleanup();
		
		System.out.println("end  main");
	};//end main

}


package org.concord.sensor.goio.jna.test;

import java.io.IOException;

import org.concord.sensor.goio.jna.*;

//Test GoIO
public class Main {
	
	
//	private static final int SKIP_TIMEOUT_MS_DEFAULT = 0;
//	private static GoIOInterface goIOInterface;

	public static void main(String[] args) throws IOException {
		
		boolean sweet = false;
		boolean isthere = false;
		GoIOInterface goIOInterface;
		
		goIOInterface = new GoIOInterface();
		
		System.out.println("start main");
		
		sweet = goIOInterface.init();
		

		GoIOInterface.GoIOSensor sensor 
		= goIOInterface.mkSensor();
		
		if(!sweet)
		{
			System.out.println("goIOInterface.init() failed --bye");
			return;
		}


		isthere = goIOInterface.isGolinkAttached();
		System.out.println("Is golink there: "+isthere);


		
	
		isthere = goIOInterface.getDeviceName(sensor);
		System.out.println("Got device name: "+isthere);
		

		goIOInterface.sensorOpen(sensor);

		
		sweet = goIOInterface.sensorSetMeasurementPeriod(sensor,0.040, GoIOLibrary.SKIP_TIMEOUT_MS_DEFAULT);
		System.out.println("sensorSetMeasurementPeriod: "+sweet);
		
		
		//end
		goIOInterface.cleanup();
		
		System.out.println("end  main");
	};//end main

}


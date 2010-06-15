package org.concord.sensor.goio.jna.test;

import java.io.IOException;

import org.concord.sensor.goio.jna.*;

import com.sun.jna.Pointer;


//Test GoIO
public class Main {
	
	
//	private static final int SKIP_TIMEOUT_MS_DEFAULT = 0;
	private static GoIOInterface goIOInterface;

	public static void main(String[] args) throws IOException {
		
		boolean sweet = false;
		boolean isthere = false;

		
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


		isthere = goIOInterface.is_golink_attached();
		System.out.println("Is golink there: "+isthere);


		
	
		isthere = goIOInterface.getDeviceName(sensor);
		System.out.println("Got device name: "+isthere);
		
		Pointer hDevice  = goIOInterface.sensorOpen(sensor);

		//System.out.println("Device name: "+ new String(deviceName));
		
		sweet = goIOInterface.sensor_set_measurement_period(hDevice,0.040, GoIOLibrary.SKIP_TIMEOUT_MS_DEFAULT);
		System.out.println("sensor_set_measurement_period: "+sweet);
		
		
		//end
		goIOInterface.cleanup();
		
		System.out.println("end  main");
	};//end main

}


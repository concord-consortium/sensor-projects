package org.concord.sensor.goio.jna.test;

import java.io.IOException;

import org.concord.sensor.goio.jna.*;

import com.sun.jna.Pointer;


//Test GoIO
public class Main {
	
	
	private static final int SKIP_TIMEOUT_MS_DEFAULT = 0;
	private static GoIOInterface goIOInterface;

	public static void main(String[] args) throws IOException {
		
		boolean sweet = false;
		boolean isthere = false;
		
		goIOInterface = new GoIOInterface();
		
		System.out.println("start main");
		
		sweet = goIOInterface.init();
		
		if(!sweet)
		{
			System.out.println("goIOInterface.init() failed --bye");
			return;
		}


		isthere = goIOInterface.is_golink_attached();
		System.out.println("Is golink there: "+isthere);

		char []deviceName = new char[GoIOLibrary.GOIO_MAX_SIZE_DEVICE_NAME];
		int []pVendorId = new int[1];
		int []pProductId = new int[1];
		
		isthere = goIOInterface.get_device_name(deviceName, GoIOLibrary.GOIO_MAX_SIZE_DEVICE_NAME, pVendorId, pProductId);
		//isthere = goIOInterface.is_temperature_probe_attached();
		System.out.println("Got device name: "+isthere);
		
		Pointer hDevice  = goIOInterface.sensor_open(deviceName, pVendorId[0], pProductId[0]);
		//System.out.println("Device name: "+ new String(deviceName));
		
		sweet = goIOInterface.sensor_set_measurement_period(hDevice,0.040, GoIOLibrary.SKIP_TIMEOUT_MS_DEFAULT);
		System.out.println("sensor_set_measurement_period: "+sweet);
		
		
		//end
		goIOInterface.cleanup();
		
		System.out.println("end  main");
	};//end main

}


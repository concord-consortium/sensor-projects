package org.concord.sensor.goio.jna.test;

import java.io.IOException;

import org.concord.sensor.goio.jna.*;


//Test GoIO
public class Main {
	
	
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

		isthere = goIOInterface.is_temperature_probe_attached();
		System.out.println("Is temperature probe there: "+isthere);		
		
		goIOInterface.cleanup();
		
		System.out.println("end  main");
	};//end main

}


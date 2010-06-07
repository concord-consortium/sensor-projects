package org.concord.sensor.goio.jna.test;

import java.io.IOException;

import org.concord.sensor.goio.jna.*;


//Test GoIO
public class Main {
	
	
	private static GoIOInterface goIOInterface;

	public static void main(String[] args) throws IOException {
		
		boolean sweet = false;
		goIOInterface = new GoIOInterface();
		
		System.out.println("start main");
		
		sweet = goIOInterface.init();
		
		if(!sweet)
		{
			System.out.println("goIOInterface.init() failed --bye");
			return;
		}
		
		
		System.out.println("end  main");
	};//end main

}


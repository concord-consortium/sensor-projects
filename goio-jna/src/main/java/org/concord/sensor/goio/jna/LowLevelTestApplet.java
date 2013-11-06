package org.concord.sensor.goio.jna;

import java.awt.Button;
import java.awt.EventQueue;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.security.AccessController;
import java.security.PrivilegedAction;

import javax.swing.JApplet;

public class LowLevelTestApplet extends JApplet {
	private static final long serialVersionUID = 1L;

	@Override
	public void start() {
		super.start();
		
		Button readButton = new Button("Read");
		readButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				readData();
			}
		});
		this.add(readButton);
	}
	
	public void readData() {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				AccessController.doPrivileged(new PrivilegedAction<Void>() {
					public Void run() {
						doRead();
						return null;
					}
				});
			}
		});
	}
	
	private void doRead() {
		
		boolean sweet = false;
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
		
		GoIOSensor sensor = goio.getFirstSensor(); 
		System.out.println("Found goio: " + sensor);		
			
		sensor.open();

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
	}
}

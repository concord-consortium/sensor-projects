package org.concord.sensor.goio.jna.test;

import java.io.IOException;

import org.concord.sensor.goio.jna.*;
import org.concord.sensor.goio.jna.GoIOInterface.GoIOSensor;

import com.sun.jna.Pointer;

//Test GoIO
public class Main {
	
	

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
		
		byte cmd = 0;
		Pointer pParams = null;
		Pointer pRespBuf =null;
		int []pnRespBytes = null;
		
		goIOInterface.sensorSendCmd(sensor,
									GoIOLibrary.SKIP_CMD_ID_START_MEASUREMENTS, 
							 
									pParams, 
									0, //null,
									pRespBuf, //null, 
									pnRespBytes,
									GoIOLibrary.SKIP_TIMEOUT_MS_DEFAULT
									);
/*				
		
		protected boolean sensorSendCmd(
				GoIOSensor goArg,	
				byte cmd,		
				Pointer pParams,			
				int nParamBytes,
				Pointer pRespBuf,			
				int []pnRespBytes,
				int timeoutMs)	
		{
			
			
			int ret = goIOLibrary.GoIO_Sensor_SendCmdAndGetResponse(
					goArg.hDevice,
					cmd,		
					pParams,			
					nParamBytes,
					pRespBuf,			
					pnRespBytes,
					timeoutMs);
			
			return ret==0;
			
		}
		
*/		
		//end
		goIOInterface.cleanup();
		
		System.out.println("end  main");
	};//end main

}


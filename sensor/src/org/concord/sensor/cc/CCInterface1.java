/*
 * Created on Jun 22, 2004
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
package org.concord.sensor.cc;

import org.concord.sensor.device.Sensor;
import org.concord.sensor.device.SensorDeviceMode;

/**
 * @author scott
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
public class CCInterface1 extends CCInterface2 {
	// 0CSO.DDDD  1DDD.DDDD  1Ddd.dddd  1ddd.dddd
	//  -> Channel#, Sign, Overange, 25 Data Bits (MSB to LSB)
	//         (The Format is 'CSOD.DDDD.DDDD.DDDd.dddd.dddd.dddd')
	//  Over   Range Maximum: 'C110.0011.1111.1111.1111.1111.1111'  = +2.812,500,000 V
	//  Over   Range Minimum: 'C110.0000.0000.0000.0000.0000.0000'  = +2.500,000,075 V
	//  Normal Range Maximum: 'C101.1111.1111.1111.1111.1111.1111'  = +2.500,000,000 V
	//  Normal Range Minimum: 'C100.0000.0000.0000.0000.0000.0000'  =  0.000,000,000 V
	//  Under  Range Minimum: 'C011.1111.1111.1111.1111.1111.1111'  = -0.000,000,075 V
	//  Under  Range Maximum: 'C011.1100.0000.0000.0000.0000.0000'  = -0.312,500,000 V
	//   The  LTC2402 specs (24012f.pdf) is available from:
	//      <http://www.linear-tech.com/prod/datasheet.html?datasheet=556>

	public CCInterface1()
	{
	}

	public boolean checkDeviceVersion()
	{
		String cReturn = sendCommand('c');
		if(cReturn.equals("C")) {
			return true;
		}		
		
		return false;
	}
	
	/**
	 * The tune value for the 1 port interface is different
	 * fro the 10 bit modes.  The voltage reference for the
	 * 10 bit converter is different in this interface.
	 */
	public float getTuneValue(int interfaceMode)
	{
		switch (interfaceMode) {
			case A2D_24_MODE:
				return 0.00015f;
			case A2D_10_CH_0_MODE:
			case A2D_10_CH_1_MODE:
			case A2D_10_2_CH_MODE:
				return 3.22f;
		}			

		return 1f;
	}

	public boolean checkMode(Sensor probe, Object modeObj)
	{
		int mode = ((SensorDeviceMode)modeObj).getMode();
		int port = ((SensorDeviceMode)modeObj).getPort();
		
		if(port != 0) {
			return false;
		}
		
		switch(mode) {
			case PAUSED_MODE:
			case A2D_24_MODE:
				// it is always possible to pause a port
				return true;	
				
			case A2D_10_CH_1_MODE:
			case A2D_10_2_CH_MODE:
			case DIG_COUNT_MODE:
				// these are possible with the hardware they just aren't
				// implemented yet
				
			default:
				return false;	
		}
	}	
}

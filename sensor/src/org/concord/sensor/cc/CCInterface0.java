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
public class CCInterface0 extends CCInterface2 {
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

	static {
		VERSION_CHAR = 'i';
	}

	public CCInterface0()
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
	
	public float getTuneValue(int interfaceMode)
	{
		switch (interfaceMode) {
			case A2D_24_MODE:
				return 0.000075f;
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
	
	/*
	 *    Existing commands:
     When not in 'COMMAND' mode:
        'c' - Switch to COMMAND Mode
     When in the 'COMMAND' mode:
        'd' - Switch to 24 Bit A2D DATA Mode
        'i' - Send out Device's version information
        'r' - Read port B and send out as single byte
        'p' - Switch to PWM Mode (not yet implemented)
        'a' - Switch to 10 Bit A2D DATA Mode
        'e' - Switch to Rotary Encoder Mode

   Added 'COMMAND' mode commands:
        '0' - Set EEProm to 'Command' mode (6 flashes a Second) Startup
        '1' - Set EEProm to 'A2D24' mode (1 flash every Second) Startup
        '2' - Set EEProm to 'A2D10' mode (1 flash every 600 mS) Startup
        '3' - Set EEProm to 'Encoder' mode (2 flashes every Second) Startup
        's' - Read Startup mode from EEProm -> returns as 'A','B','C', or 'D'
	 */
	public char getRequestChar(int [] port)
	{
		if(port[1] != PAUSED_MODE) return UNKNOWN_MODE;
		
		if(port[0] == PAUSED_MODE) return 'c';

		if(port[0] == A2D_24_MODE) return 'd';
		
		// This interface only has one 10 bit mode.
		if(port[0] == A2D_10_CH_1_MODE ||
				   port[0] == A2D_10_2_CH_MODE ||
				   port[0] == A2D_10_CH_0_MODE) return 'a';


		if(port[0] == DIG_COUNT_MODE) return 'e';
		
		return UNKNOWN_MODE;
	}

	public void	setDatumFormat(char mode) 
	{
		boolean [] v = validChannels;
	
		switch(mode){

			// 24 bit modes
		case 'd':
			// Port A chan 0 and 1
			stepType = DATUM_24_BIT;
			v[0] = v[1] = true;
			break;
		case 'a': 
			// Port A chan 0 and 1
			stepType = DATUM_MULTI_CH_10_BIT;
			v[0] = v[1] = true;
			dDesc[0].setDt(0.005f);
			break;			

			// Digital count
		case 'e':
			// digital count mode only
			stepType = DATUM_DIG_COUNT;
			v[0] = true;
			break;			
		}		   
	}

	int step24Bit(int numBytes)
	{
		// We don't want to handle partial datums so 
		// modding the numBytes gives the length of
		// all valid datums
		int endPos = (numBytes / datumSize) * datumSize;
		int value;
		int portChannel;

		while(curPos < endPos){
			value = 0;

			tmp = buf[curPos++];
			pos = (byte)(tmp & POS_MASK);
			if(pos != (byte)0x00) return WARN_WRONG_POSITION;
			// This is different from the new one because there
			// is only one bit for the channel and it is located
			// one bit higher.  see the comment at the top of the file
			portChannel = (tmp & 0x40) >> 6;  // 0100 0000
			value |= (tmp & (byte)0x03F) << 21;

			tmp = buf[curPos++];
			pos = (byte)(tmp & POS_MASK);
			if(pos != (byte)0x80) return WARN_WRONG_POSITION;
			value |= (tmp & (byte)0x07F) << 14;

			tmp = buf[curPos++];
			pos = (byte)(tmp & POS_MASK);
			if(pos != (byte)0x80) return WARN_WRONG_POSITION;
			value |= (tmp & (byte)0x07F) << 7;

			tmp = buf[curPos++];
			pos = (byte)(tmp & POS_MASK);
			if(pos != (byte)0x80) return WARN_WRONG_POSITION;
			value |= (tmp & (byte)0x07F);

			if((value & 0x1FFFFFF) == 0) {
				value = 0;
			} else {
				value = value - (int)0x04000000;
			}
			
			if(validChannels[portChannel]){
				// First we round curDataPos to a multiple of valuesPerDatum
				// I don't know what chDenom and chOffset are...
				valueData[(curDataPos / valuesPerDatum ) * valuesPerDatum + 
						  (portChannel - chOffset)/spaceBetweenChannels] = value;
				curDataPos++;
			}

			// keep track of invalid channels too
			channelCounts[portChannel]++;

		}

		return endPos;
	}
}

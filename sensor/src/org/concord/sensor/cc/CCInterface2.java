package org.concord.sensor.cc;

import org.concord.sensor.*;

import waba.sys.Vm;
import waba.io.SerialPort;

import org.concord.framework.data.stream.*;
import org.concord.framework.text.*;

public class CCInterface2 extends InterfaceManager
	implements CCModes
{
	public final static int DATUM_ONE_CH_10_BIT = 0;
	public final static int DATUM_MULTI_CH_10_BIT = 1;
	public final static int DATUM_DIG_COUNT_10_BIT = 2;
	public final static int DATUM_DIG_COUNT = 3;
	
	public final static int DATUM_24_BIT = 4;
	
	public final static int ERROR_GENERAL			= -1;
	public final static int ERROR_PORT				= -2;
	public final static int WARN_WRONG_POSITION	= -3;
	public final static int WARN_SERIAL_ERROR	= -4;

	public final static int DATA_TIME_OUT = 40;
	public static char VERSION_CHAR = 'v';

	final static String [] portNames = {"A", "B"};

	final static char UNKNOWN_MODE = (char)-1;

	final static byte POS_MASK = (byte)(0x80);
	
	protected  SerialPort port;

	public DataStreamEvent dEvent = new DataStreamEvent();
	int 				curStepTime = 0;

	int bufOffset = 0;

	int BUF_SIZE = CCSensorProducer.BUF_SIZE;

	byte [] buf = new byte[BUF_SIZE];
	int []valueData = new int[1 + BUF_SIZE / 2]; 


	int timeWithoutData = 0;

	int 				readSize = 512;

	byte[] curModeCharBuf = new byte[1];

	public DataStreamDescription []	dDesc;
	int [] requestedMode;
	int [] currentMode;
	Sensor [] portSensors;

	String [] okOptions = {"Ok"};
	String [] continueOptions = {"Continue"};
	
	/*
	Command SUMMARY (host to interface) as of v54:
	When not in 'COMMAND' mode:
	'c' - Switch to COMMAND Mode
	In the 'COMMAND' mode:
	'a' - Switch to 24 Bit A2D DATA Mode - Probe A
	'b' - Switch to 24 Bit A2D DATA Mode - Probe B
	'd' - Switch to 24 Bit A2D DATA Mode - Probes A & B
	'e' - Switch to 1 ch 10 Bit A2D DATA Mode - Ch0, Probe A
	'f' - Switch to 1 ch 10 Bit A2D DATA Mode - Ch0, Probe B
	'g' - Switch to 2 ch 10 Bit A2D DATA Mode - Ch0 & CH1, Probe A
	'h' - Switch to 2 ch  10 Bit A2D DATA Mode - Ch0 & CH1, Probe B
	'i' - Switch to 2 ch  10 Bit A2D DATA Mode - Ch0, Probe A & Probe B
	'j' - Switch to 4 ch  10 Bit A2D DATA Mode - Ch0 & CH1, Probe A & Probe B
	'r' - Switch to Rotary Encoder Mode
	's' - Switch to Rotary Encoder(A2D Mode for Probe B Channel 0
	't' - Switch to Rotary Encoder(A2D Mode for Probe B Channel 0
	'v' - Send out Device's version information (ASCII)
	And some commands for hardware testing:
	'1' -> '8' : Toggle this Power control shift register bit
	'0' : Clear the Power control shift register
	'9' : Set the Power control shift register to startup settings
	'x' : Toggle the test routine
	*/

	public CCInterface2(Ticker t, UserMessageHandler messageHandler){
		super(t, messageHandler);

		// With version 0 only one sensor can be used  
		// but leaving this as 2 simplifies the code
		// the second sensor will remain null and paused at all times on version 0
		portSensors = new Sensor [2];
		requestedMode = new int [] { PAUSED_MODE, PAUSED_MODE };
		currentMode = new int [] { UNKNOWN_MODE, UNKNOWN_MODE };
		dDesc = new DataStreamDescription [] {new DataStreamDescription(), new DataStreamDescription()};
					
	}
	
	public float getTuneValue(int interfaceMode)
	{
		switch (interfaceMode) {
			case A2D_24_MODE:
				return 0.00015f;
			case A2D_10_CH_0_MODE:
			case A2D_10_CH_1_MODE:
			case A2D_10_2_CH_MODE:
				return 2.441406f;
		}
		
		return 1f;
	}

	static class CCInterfaceMode
	{
		int port;
		int mode;

		CCInterfaceMode(int port, int mode)
		{
			this.port = port;
			this.mode = mode;
		}
	}

	public static Object getMode(int port, int mode)
	{
		return new CCInterfaceMode(port, mode);
	}


	public void dispose()
	{
		stop();

		for(int i=0; i<portSensors.length; i++){
			portSensors[i].setInterface(null);
			portSensors[i] = null;
		}
	}
	
	public void start(Sensor probe)
	{
		// before we are here the user should have called
		// checkMode and then setMode with the relavent probe

		// now we see if the interface already running
		start();

		// now some how we need to turn on the probe at this
		// if it is not in paused mode.  This will tell us
		// to start send the probe events if we aren't already.
		// also we need to send the 
	  

	}

	public void stop(Sensor probe)
	{
		stop();
	}

	public boolean updateMode(Sensor p)
	{
		// Call some global function to get the interface mode for this probe
		// it needs to be based on the probes properties
		// But we need to watch out for stuff that currently gets set by this function
		CCInterfaceMode mode = (CCInterfaceMode)p.getInterfaceMode();

		int port = mode.port;
		
		// check if it is valid
		if(!checkMode(p, mode)) return false;

		requestedMode[port] = mode.mode;

		return true;
	}

	public boolean addSensor(Sensor probe)
	{
		CCInterfaceMode mode = (CCInterfaceMode)probe.getInterfaceMode();
		if(portSensors[mode.port] != null &&
		   portSensors[mode.port] != probe)
		{
			// there is a probe already assigned to that port
			return false;
		}

		// We have to be careful here not to overwrite an existing port property
		// probe.setPortProperty(new PropObject("Port", "Port", Probe.PROP_PORT, portNames));

		portSensors[mode.port] = probe;
		return updateMode(probe);
	}

	public void removeSensor(Sensor probe)
	{
		for(int i=0; i<portSensors.length; i++) {
			if(portSensors[i] == probe){
				// notify the probe
				// stop it if it is started
				portSensors[i] = null;
			}
		}

		return;
	}

	/*
	 * I'm going to implement both of these just for
	 * interface 2.  And then I'll refactor things
	 * so this isn't such a mess for the two interfaces

	 * The valid modes for ports are:
	 * 
	 * PAUSED_MODE, 
	 A2D_24_MODE, 
	 A2D_10_1_CHANNEL_MODE, 
	 A2D_10_2_CHANNEL_MODE, 
	 DIG_COUNT_MODE (only valid on one port B I think)
	'e' - Switch to 1 ch 10 Bit A2D DATA Mode - Ch0, Probe A
	'f' - Switch to 1 ch 10 Bit A2D DATA Mode - Ch0, Probe B
	'g' - Switch to 2 ch 10 Bit A2D DATA Mode - Ch0 & CH1, Probe A
	'h' - Switch to 2 ch  10 Bit A2D DATA Mode - Ch0 & CH1, Probe B
	'i' - Switch to 2 ch  10 Bit A2D DATA Mode - Ch0, Probe A & Probe B
	'j' - Switch to 4 ch  10 Bit A2D DATA Mode - Ch0 & CH1, Probe A & Probe B
	'r' - Switch to Rotary Encoder Mode (port A)
	's' - Switch to Rotary Encoder(A2D Mode for Probe B Channel 0
	't' - Switch to Rotary Encoder(A2D Mode for Probe B Channel 0
	 */
	public boolean checkMode(Sensor probe, Object modeObj)
	{
		int mode = ((CCInterfaceMode)modeObj).mode;
		int port = ((CCInterfaceMode)modeObj).port;
		int otherPortMode;
						
		otherPortMode = requestedMode[(port+1)%2];;
		
		switch(mode)
		{
		case PAUSED_MODE:
			// it is always possible to pause a port
			return true;	
		case A2D_24_MODE:
			return ( otherPortMode == PAUSED_MODE ||
					 otherPortMode == A2D_24_MODE );
		case A2D_10_CH_0_MODE:
			return ( otherPortMode == PAUSED_MODE ||
					 otherPortMode == A2D_10_CH_0_MODE ||
					 otherPortMode == A2D_10_CH_1_MODE ||
					 otherPortMode == A2D_10_2_CH_MODE ||
					 ( port == PORT_B && otherPortMode == DIG_COUNT_MODE ) );
		case A2D_10_CH_1_MODE:
			return ( otherPortMode == PAUSED_MODE ||
					 otherPortMode == A2D_10_CH_0_MODE ||
					 otherPortMode == A2D_10_CH_1_MODE ||
					 otherPortMode == A2D_10_2_CH_MODE ||
					 ( port == PORT_B && otherPortMode == DIG_COUNT_MODE ) );
		case A2D_10_2_CH_MODE:
			return ( otherPortMode == PAUSED_MODE ||
					 otherPortMode == A2D_10_CH_0_MODE ||
					 otherPortMode == A2D_10_CH_1_MODE ||
					 otherPortMode == A2D_10_2_CH_MODE );
		case DIG_COUNT_MODE:
			return ( port == PORT_A && 
					 ( otherPortMode == PAUSED_MODE ||
					   otherPortMode == A2D_10_CH_0_MODE ||
					   otherPortMode == A2D_10_CH_1_MODE ) );
		default:
			return false;
		}
	}

	public void syncWithInterface()
	{
		if(currentMode[0] != requestedMode[0] ||
		   currentMode[1] != requestedMode[1])
		{
			if(ticker.isTicking()){
				stop();
				start();
			}
		}
	}

	public char getRequestChar(int [] port)
	{
		if(port[0] == PAUSED_MODE && port[1] == PAUSED_MODE) return 'c';

		if(port[1] == PAUSED_MODE) {			
			if(port[0] == A2D_24_MODE) return 'a';
		
			if(port[0] == A2D_10_CH_0_MODE) return 'e';

			if(port[0] == A2D_10_CH_1_MODE ||
			   port[0] == A2D_10_2_CH_MODE) return 'g';

			if(port[0] == DIG_COUNT_MODE) return 'r';
		}

		if(port[0] == PAUSED_MODE) {
			if(port[1] == A2D_24_MODE) return 'b';
		
			if(port[1] == A2D_10_CH_0_MODE) return 'f';

			if(port[1] == A2D_10_CH_1_MODE ||
			   port[1] == A2D_10_2_CH_MODE) return 'h';
		}
			
		if(port[0] == A2D_24_MODE && port[1] == A2D_24_MODE) return 'd';

		if(port[0] == A2D_10_CH_0_MODE &&
		   port[1] == A2D_10_CH_0_MODE) {
			return 'i';
		}
		
		// All the other 10 bit modes
		if( (port[0] == A2D_10_CH_0_MODE ||
			 port[0] == A2D_10_CH_1_MODE ||
			 port[0] == A2D_10_2_CH_MODE ) &&
			(port[1] == A2D_10_CH_0_MODE ||
			 port[1] == A2D_10_CH_1_MODE ||
			 port[1] == A2D_10_2_CH_MODE ) ) {
			return 'j';
		}

		if(port[0] == DIG_COUNT_MODE && port[1] == A2D_10_CH_0_MODE) return 's';

		if(port[0] == DIG_COUNT_MODE && port[1] == A2D_10_CH_1_MODE) return 's';

		return UNKNOWN_MODE;
	}

	// This is the offset of the first valid channel
	int chOffset = 0;
	
	// This is used when the data comes in so if
	// not all the channels are used then the channel
	// index is divided by this number.
	int spaceBetweenChannels = 1;
	boolean [] validChannels = new boolean [4];
	int [] channelCounts = new int[4];
	int stepType;

	public void setByteStreamProperties(char mode)
	{
		for(int i=0; i<4; i++){
			validChannels[i]=false;
			channelCounts[i]=0;
		}

		chOffset = 0; 
		spaceBetweenChannels = 1;

		readSize = 512;

		setDatumFormat(mode);

		// Figure out the offsets and sizes for the two probes
		// For dDesc[0] the offset will always be 0
		// but the 
		valuesPerDatum = 0;
		int chOneValues = 0;
		chOffset=-1;
		dDesc[0].setChannelPerSample(0);
		dDesc[1].setChannelPerSample(0);

		for(int i=0; i<4; i++)
		{
			if(validChannels[i]){
				valuesPerDatum++;
				dDesc[i/2].setChannelPerSample(dDesc[i/2].getChannelPerSample()+1);
				if(chOffset == -1) chOffset = i;
			}
		}


		dDesc[0].setNextSampleOffset(valuesPerDatum);
		dDesc[1].setNextSampleOffset(valuesPerDatum);
		dDesc[1].setDataOffset(dDesc[0].getChannelPerSample());

		// Now do we need to tweak the chPerSample and Offset
		// if the probe only asked for channel 1 and not channel 0?

		switch(stepType)
		{
		case DATUM_ONE_CH_10_BIT:
		case DATUM_MULTI_CH_10_BIT:
			dDesc[0].getChannelDescription().setTuneValue(getTuneValue(A2D_10_CH_0_MODE));
			dDesc[1].getChannelDescription().setTuneValue(getTuneValue(A2D_10_CH_0_MODE));
			datumSize = 2 * valuesPerDatum;

			break;
		case DATUM_DIG_COUNT:
			dDesc[0].setDt(0.01f);
			dDesc[0].getChannelDescription().setTuneValue(1f);
			datumSize = 2 * valuesPerDatum;
			readSize = 100;		   

			break;
		case DATUM_24_BIT:
			dDesc[0].setDt(0.333333f);
			dDesc[1].setDt(0.333333f);
			dDesc[0].getChannelDescription().setTuneValue(getTuneValue(A2D_24_MODE));
			dDesc[1].getChannelDescription().setTuneValue(getTuneValue(A2D_24_MODE));
			datumSize = 4 * valuesPerDatum;

			break;
		case DATUM_DIG_COUNT_10_BIT:
			dDesc[0].setDt(0.01f);
			dDesc[1].setDt(0.01f);
			dDesc[0].getChannelDescription().setTuneValue(1f);
			dDesc[1].getChannelDescription().setTuneValue(getTuneValue(A2D_10_CH_0_MODE));
			datumSize = 4 * valuesPerDatum;			

			break;
		}
	}

	public void	setDatumFormat(char mode) 
	{
		boolean [] v = validChannels;
	
		switch(mode){

			// 24 bit modes
		case 'a':
			// Port A chan 0 and 1
			stepType = DATUM_24_BIT;
			v[0] = v[1] = true;
			break;
		case 'b':
			// Port B chan 0 and 1
			stepType = DATUM_24_BIT;
			v[2] = v[3] = true;
			break;
		case 'd': 
			// Port A and B chan 0 and 1
			stepType = DATUM_24_BIT;
			v[0] = v[1] = v[2] = v[3] = true;
			break;			

			// 10 bit modes
		case 'e':
			// Port A chan 0
			stepType = DATUM_ONE_CH_10_BIT;
			v[0] = true;
			dDesc[0].setDt(0.0025f);
			break;
		case 'f':
			// Port B chan 0
			stepType = DATUM_ONE_CH_10_BIT;
			v[2] = true;
			dDesc[1].setDt(0.0025f);
			break;

		case 'g': 
			// Port A chan 0 and 1
			stepType = DATUM_MULTI_CH_10_BIT;
			v[0] = v[1] = true;
			dDesc[0].setDt(0.005f);
			break;			
		case 'h':
			// Port B chan 0 and 1
			stepType = DATUM_MULTI_CH_10_BIT;
			v[2] = v[3] = true;
			dDesc[1].setDt(0.005f);
			break;
		case 'i':
			// Port A chan 0 and Port B chan 0 channels
			stepType = DATUM_MULTI_CH_10_BIT;
			v[0] = v[2] = true;
			spaceBetweenChannels = 2;
			dDesc[0].setDt(0.005f);
			dDesc[1].setDt(0.005f);
			break;			
		case 'j':
			// four channels
			stepType = DATUM_MULTI_CH_10_BIT;
			v[0] = v[1] = v[2] = v[3] = true;
			dDesc[0].setDt(0.01f);
			dDesc[1].setDt(0.01f);
			break;

			// Digital count
		case 'r':
			// digital count mode only
			stepType = DATUM_DIG_COUNT;
			v[0] = true;
			break;
			
		case 's':
			// digital count with 10 bit a2d2
			stepType = DATUM_DIG_COUNT_10_BIT;
			v[0] = v[2] = true;
			break;
		case 't':
			// digital count with 10 bit a2d2
			stepType = DATUM_DIG_COUNT_10_BIT;
			v[0] = v[3] = true;
			break;
		}		   
	}
	
	public void start()
	{
		// If this fails from some reason we might need to 
		// reset the curMode 

		// Open the port
		port = new SerialPort(0,9600);
		if((port == null) || !port.isOpen()) return;
		port.setFlowControl(false);

		// Make sure the interface is stopped
		// And make sure we have read all the extra characters that it printed
		// on startup
		if(!gotoCommandMode()) {
			messageHandler.showOptionMessage("Error in interface start", "Interface Error", 
					okOptions, okOptions[0]);
		    return;
		}

		// We are changing the mode so set the curMode 
		currentMode[0] = requestedMode[0];
		currentMode[1] = requestedMode[1];

		char startC = getRequestChar(currentMode);
		curModeCharBuf[0] = (byte)startC;

		setByteStreamProperties(startC);

		dEvent.setIntData(valueData);
		
		for(int i=0; i<portSensors.length; i++)
		{
			if(currentMode[i] != PAUSED_MODE ||
			   currentMode[i] != UNKNOWN_MODE &&
			   portSensors[i] != null)
			{
				// This isn't quite right because the dEvent might be different
				// but lets see how the step functions work out before we design 
				// this
				dEvent.setDataDescription(dDesc[i]);
				portSensors[i].startSampling(dEvent);
			}
		}

		int wb = port.writeBytes(curModeCharBuf, 0, 1);
		bufOffset = 0;
		timeWithoutData = 0;

		startTimer = Vm.getTimeStamp();
		ticker.startTicking(getRightMilliseconds());
	}

	/*
	 *  All these function need to be specific to a particular
	 *  port
	 */
	public boolean stopSampling(DataStreamEvent e)
	{
	    return true;
	}

	public void stop(){	
		boolean ticking = ticker.isTicking();
		SerialPort local_port = null;

		if(ticking) {
			ticker.stopTicking();
		}
		
		if(port != null) {
			local_port = port;
			port = null;
		}
		if(local_port != null){
		    buf[0] = (byte)'c';
		    local_port.writeBytes(buf, 0, 1);
			
			// give the port time to send out this byte
			waba.sys.Vm.sleep(100);
			local_port.close();
			local_port = null;
		}
		if(ticking){
			ticker.stopTicking();

			for(int i=0; i<portSensors.length; i++) {
				if(portSensors[i] != null &&
				   currentMode[i] != PAUSED_MODE)
				{
					dEvent.setDataDescription(dDesc[i]);
					portSensors[i].stopSampling(dEvent);
				}

				// We changed the mode so set the curMode 
				currentMode[i] = PAUSED_MODE;
			}			
		}
	}
	
	//we need optimization probably: dynamically calculate getRightMilliseconds
    // This really needs to be figured out
	public static int rightMillis = 50;
	public int getRightMilliseconds(){return rightMillis;}

	public void tick()
	{
	    int ret;

		if((port == null) || !port.isOpen()){
			ret = ERROR_PORT;
		} else {
			ret = step();
		}

		if(ret >= 0){
			if(ret == 0){
				// we didn't get any data.  hmm..
				timeWithoutData++;
				if(timeWithoutData > DATA_TIME_OUT){
					stop();
					messageHandler.showOptionMessage("Serial Read Error: " +
											 "possibly no interface " +
											 "connected", "Interface Error",
											 continueOptions, continueOptions[0]);					
				}
			} else {
				timeWithoutData = 0;
			}
			return;
		} else {
			String message;
			if(ret == WARN_WRONG_POSITION){
				stop();
				message = "Error in byte stream";
			} else if(ret == WARN_SERIAL_ERROR){
				stop();
				message = "Serial Read Error: " +
				 "possibly buffer overflow";
			} else {
				stop();
				message = "Serial Port error"; 
			}
			messageHandler.showOptionMessage(message, "Interface Error",
					continueOptions, continueOptions[0]);
		}		
	}
	
	boolean gotoCommandMode()
	{
		// Let the device wake up a bit
		// But try to stop it as soon as we can
		
		int tmp = 0 ;
		buf[0] = (byte)'c';
		for(int i=0; i<10; i++){
			tmp = port.writeBytes(buf, 0, 1);
			Vm.sleep(100);
		}

		// We are changed the mode so set the curMode 
		currentMode[0] = currentMode[1] = PAUSED_MODE;

		waba.sys.Vm.sleep(200);
		port.setReadTimeout(0);

		tmp = port.readBytes(buf, 0, BUF_SIZE);//workaround 
		if(tmp < 0){
		    // There might have been a line error
		    // Try again
			tmp = port.readBytes(buf, 0, BUF_SIZE);
		    if(tmp < 0){
				port.close();
				port = null;
				return false;
		    }
		}
		
		System.out.println("command mode return: " + tmp + 
				" string: " + new String(buf, 0, tmp));

		if(!checkDeviceVersion()) {
			System.out.println("wrong device connected");
			port.close();
			port = null;
			return false;
		}
		
		System.out.println("Version info: " + getInfoString());
		return true;
	}

	public boolean checkDeviceVersion()
	{
		String cReturn = sendCommand('c');
		if(cReturn.equals("?")) {
			return true;
		}		
		
		return false;
	}

	public String getInfoString()
	{
		return sendCommand(VERSION_CHAR);		
	}
	
	public String sendCommand(char commandChar)
	{
		int tmp = 0 ;
		buf[0] = (byte)commandChar;
		tmp = port.writeBytes(buf, 0, 1);

		Vm.sleep(200);
		port.setReadTimeout(0);

		tmp = port.readBytes(buf, 0, BUF_SIZE);//workaround 
		if(tmp < 0){
		    // There might have been a line error
		    // Try again for safety
			tmp = port.readBytes(buf, 0, BUF_SIZE);
		    if(tmp < 0){
				port.close();
				port = null;
				return "Error: reading version";
		    }
		}
		
		return new String(buf, 0, tmp);
	}
	
	protected void finalize() throws Throwable 
	{
		dispose();
	}
	
	
	/*
	private static void printBinary(int i) {
		for (int sh = 31; sh >= 0; sh--) {
			System.out.print((i >> sh) & 1);
			if(sh % 4 == 0) System.out.print(" ");
		}
	}

	private static void printBinary(byte i) {
		for (int sh = 7; sh >= 0; sh--) {
			System.out.print((i >> sh) & 1);
			if(sh % 4 == 0) System.out.print(" ");
		}
	}
	*/

	/* Internal variable used by the different
	 * inner step loops
	 */
	int ret = -1;
	byte tmp;
	byte pos;
	int i,j;
	int curPos;
	int curDataPos;
	int valuesPerDatum;
	int datumSize;

	int step()
	{
		int totalRead = 0;
		int error = 0;

		ret = -1;

		while(port != null && port.isOpen()){
			// profiling
			dEvent.numPTimes = 0;
			//int startPTime = Vm.getTimeStamp();
			//dEvent.pTimes[dEvent.numPTimes++] = startPTime;

			ret = port.readBytes(buf, bufOffset, readSize - bufOffset);
			if(ret <= 0){
				// Try to write the command char again
				port.writeBytes(curModeCharBuf, 0, 1);
				break; // there are no bytes available
			}

			totalRead += ret;
			ret += bufOffset;	    
			if(ret < 32){
				bufOffset = ret;//too few?
				break;
			}

			// profiling
			// dEvent.pTimes[dEvent.numPTimes++] = ret;

			curPos = 0;
			curDataPos = 0;
		   
			error = 0;

			// Do special loops
			switch(stepType)
			{
			case DATUM_ONE_CH_10_BIT:
				error = step10BitOneChannel(ret);
				break;
			case DATUM_MULTI_CH_10_BIT:
				error = step10BitMultiChannels(ret);
				break;
			case DATUM_DIG_COUNT_10_BIT:
				error = stepDigCount10Bit(ret);
				break;
			case DATUM_DIG_COUNT:
				error = stepDigCount(ret);
				break;				
			case DATUM_24_BIT:
				error = step24Bit(ret);
				break;
			}

			// Round down
			dEvent.numSamples = (curDataPos/valuesPerDatum);
			curStepTime += dEvent.numSamples;

			if(error == WARN_WRONG_POSITION){
				// We could keep track of these errors and report them 
				// at the end but lets ignore them for now				
			}		   

			// Profiling
			// dEvent.pTimes[dEvent.numPTimes++] = Vm.getTimeStamp() - startPTime;
			
			for(int i=0; i<portSensors.length; i++) {
				if(portSensors[i] != null &&
						currentMode[i] != PAUSED_MODE)
				{
					dEvent.setDataDescription(dDesc[i]);
					portSensors[i].dataArrived(dEvent);
				}
			}
					
			if((ret - curPos) > 0){
				// Move all remaing data to the beginning of the array
				for(j=0; j<(ret-curPos); j++){
					buf[j] = buf[curPos + j];
				}
				bufOffset = j;
			} else {
			    bufOffset = 0;
			}

		}

		// Should have a special error condition
		if(ret < 0) return WARN_SERIAL_ERROR;

		for(int i=0; i<portSensors.length; i++) {
			if(portSensors[i] != null &&
					currentMode[i] != PAUSED_MODE)
			{
				dEvent.setDataDescription(dDesc[i]);
				portSensors[i].idle(dEvent);
			}
		}
				
		return totalRead;
	}

	/**
	 * This is an optimized version of the MultiChannel
	 * it doesn't need keep track of ports or channels
	 *
	 * return the number of bytes read
	 */
	int step10BitOneChannel(int numBytes)
	{
		// We don't want to handle partial datums so 
		// modding the numBytes gives the length of
		// all valid datums
		int endPos = (numBytes / 2) * 2;
		int value;

		while(curPos < endPos){
			value = 0;

			tmp = buf[curPos++];
			pos = (byte)(tmp & 0xE0);
			if(pos != (byte)0x20) return WARN_WRONG_POSITION;
			value |= (tmp & (byte)0x003) << 7;

			tmp = buf[curPos++];
			pos = (byte)(tmp & POS_MASK);
			if(pos != (byte)0x80) return WARN_WRONG_POSITION;
			value |= (tmp & (byte)0x07F);

			valueData[curDataPos++] = value;
		}

		return endPos;
	}

	/**
	 * This is a little slower than step10BitOneChannel
	 * because it needs to figure out where to put the 
	 * value in the valueData array.  It also keeps track
	 * of the number of values recieved for each channel.
	 *
	 * This will allow some error checking outside of this
	 * to see if they cooresponend with total number of 
	 * values read.
	 *
	 * return the number of bytes read
	 */
	int step10BitMultiChannels(int numBytes)
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
			pos = (byte)(tmp & 0xE0);
			if(pos != (byte)0x20) return WARN_WRONG_POSITION;
			portChannel = (tmp & 0x018) >> 3;
			value |= (tmp & (byte)0x00F) << 7;

			tmp = buf[curPos++];
			pos = (byte)(tmp & POS_MASK);
			if(pos != (byte)0x80) return WARN_WRONG_POSITION;
			value |= (tmp & (byte)0x07F);

			// Ignore the change bit
			value &= 0x03FF;
			
			/* Ok now we have to figure out where to put
			 * this value
			 * All the data is stored in one array
			 * but it needs to be same order each time
			 * We could trust the interface to send it
			 * back correctly but that seems risky
			 * so before calling this function 
			 * a few variables need to be set
			 */
			if(validChannels[portChannel]){
				valueData[(curDataPos / valuesPerDatum ) * valuesPerDatum + 
						  (portChannel - chOffset)/spaceBetweenChannels] = value;
				curDataPos++;
			}

			// keep track of invalid channels too
			channelCounts[portChannel]++;

		}

		return endPos;
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
			portChannel = (tmp & 0x60) >> 5;
			value |= (tmp & (byte)0x01F) << 21;

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
				value = value - (int)0x2000000;
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

	/**
	 * This is an optimized version of the MultiChannel
	 * it doesn't need keep track of ports or channels
	 *
	 * return the number of bytes read
	 */
	int stepDigCount(int numBytes)
	{
		// We don't want to handle partial datums so 
		// modding the numBytes gives the length of
		// all valid datums
		int endPos = (numBytes / 2) * 2;
		int value;

		while(curPos < endPos){
			value = 0;

			tmp = buf[curPos++];
			pos = (byte)(tmp & 0xE0);
			if(pos != (byte)0x00) return WARN_WRONG_POSITION;
			value |= (tmp & (byte)0x001) << 7;

			tmp = buf[curPos++];
			pos = (byte)(tmp & POS_MASK);
			if(pos != (byte)0x80) return WARN_WRONG_POSITION;
			value |= (tmp & (byte)0x07F);

			if(value > 128) value -= 256;
			valueData[curDataPos++] = value;
		}

		return endPos;
	}

	/**
	 * This is an optimized version of the MultiChannel
	 * it doesn't need keep track of ports or channels
	 *
	 * return the number of bytes read
	 */
	int stepDigCount10Bit(int numBytes)
	{
		// We don't want to handle partial datums so 
		// modding the numBytes gives the length of
		// all valid datums
		int endPos = (numBytes / 4) * 4;
		int value;

		while(curPos < endPos){
			// Read 10 bit packet
			value = 0;

			tmp = buf[curPos++];
			pos = (byte)(tmp & 0xE0);
			if(pos != (byte)0x20) return WARN_WRONG_POSITION;
			value |= (tmp & (byte)0x003) << 7;

			tmp = buf[curPos++];
			pos = (byte)(tmp & POS_MASK);
			if(pos != (byte)0x80) return WARN_WRONG_POSITION;
			value |= (tmp & (byte)0x07F);

			valueData[curDataPos++] = value;

			// Read Dig Count packet
			value = 0;

			tmp = buf[curPos++];
			pos = (byte)(tmp & 0xE0);
			if(pos != (byte)0x00) return WARN_WRONG_POSITION;
			value |= (tmp & (byte)0x001) << 7;

			tmp = buf[curPos++];
			pos = (byte)(tmp & POS_MASK);
			if(pos != (byte)0x80) return WARN_WRONG_POSITION;
			value |= (tmp & (byte)0x07F);

			if(value > 128) value -= 256;
			valueData[curDataPos++] = value;
		}

		return endPos;
	}

}


/*
 *  Copyright (C) 2004  The Concord Consortium, Inc.,
 *  10 Concord Crossing, Concord, MA 01741
 *
 *  Web Site: http://www.concord.org
 *  Email: info@concord.org
 *
 *  This library is free software; you can redistribute it and/or
 *  modify it under the terms of the GNU Lesser General Public
 *  License as published by the Free Software Foundation; either
 *  version 2.1 of the License, or (at your option) any later version.
 *
 *  This library is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *  Lesser General Public License for more details.
 *
 *  You should have received a copy of the GNU Lesser General Public
 *  License along with this library; if not, write to the Free Software
 *  Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 *
 */

package org.concord.probe.meld;

import org.concord.probe.*;

import waba.sys.Vm;
import waba.io.SerialPort;
import waba.ui.Event;
import waba.ui.ControlEvent;
import org.concord.waba.extra.ui.*;

import org.concord.framework.data.stream.*;

public class Meld extends InterfaceManager
{
	protected  SerialPort port;

	public final static int FREQ_1HZ = 0;
	public final static int FREQ_10HZ = 1;
	public final static int A2D_INTERNAL = 2;
	public final static int A2D_EXTERNAL = 3;
	public final static int A2D_INTERNAL_BURST = 4;

	public final static int MELD = 3;

	public DataDesc		dDesc = new DataDesc();
	public DataEvent	dEvent = new DataEvent();
	int 				curStepTime = 0;

	public int 		numBytes = 4;
	public int 		bitsPerByte = 7;
	public byte 		MASK = (byte)(0x0FF << bitsPerByte);
	float 			timeStepSize = (float)0.333333;
	int 				curDataPos = 0;
	int 				readSize = 512;

	int bufOffset = 0;
	public final static int BUF_SIZE = 512;
	byte [] buf = new byte[BUF_SIZE];
	int []valueData = new int[1 + BUF_SIZE / 2]; //0 init time, 1 - deltat, 2 - numb data(total)
	int mode = A2D_INTERNAL;

	public final static int ERROR_GENERAL			= -1;
	public final static int ERROR_PORT				= -2;
	public final static int WARN_WRONG_POSITION	= -3;
	public final static int WARN_SERIAL_ERROR	= -4;

	public float		tuneValue = 1.0f;

	byte commandByte = 0;
	int currentChannel = 0;

	public void start(){
		super.start();

		port = new SerialPort(0,19200);
		if((port == null) || !port.isOpen()) return;
		port.setFlowControl(false);
		
		// Set one second time out
		// we might be able to get by with less
		port.setReadTimeout(1000);

		setCurIntTime(0);
		dDesc.setDt(timeStepSize);
		dDesc.setChPerSample(1);
		dEvent.setDataOffset(0);
		dEvent.setDataDesc(dDesc);
		getCommandByte();
		getCurrentChannel();
		
		dEvent.setTuneValue(tuneValue);
		dEvent.setIntData(valueData);
		
		dEvent.setType(DataEvent.DATA_READY_TO_START);
		startSampling(dEvent);

		dEvent.setType(DataEvent.DATA_RECEIVED);

		startMeld();
		startTimer = Vm.getTimeStamp();
		timeWithoutData = 0;
		timer = addTimer(getRightMilliseconds());
	}

	void getCommandByte()
	{
		switch(mode){
		case FREQ_1HZ:
			commandByte = (byte)8;
			break;
		case FREQ_10HZ:
			commandByte = (byte)12;
			break;
		case A2D_INTERNAL:
			tuneValue = 0.5f;
			commandByte = (byte)16;
			break;
		case A2D_EXTERNAL:
			commandByte = (byte)24;
			break;
		case A2D_INTERNAL_BURST:
			commandByte = (byte)32;
			break;
		}
	}

	boolean getCurrentChannel()
	{
		int numbProbs = probes.getCount();
		if(numbProbs < 1) return false;
		if(numbProbs == 1){
			Probe pr = (Probe)probes.get(0);
			if(pr == null) return false;

			int [] channels = pr.getChannels();
			if(channels == null || channels.length != 0){
				return false;
			}

			currentChannel = channels[0];
		}
		
		return true;
	}

	void startMeld()
	{	
		// Let it wake up a bit 
		Vm.sleep(200);

		// It sounds like we don't have to worry about garbage bytes
		// but I'll believe that when I see it :-)

		// We poll it after this so there is no need
		// to do anything else
	}

	public void stop(){
		if(port != null){
			waba.sys.Vm.sleep(100);
			port.close();
			port = null;
		}
		if(timer != null){
			removeTimer(timer);
			timer = null;
			dEvent.setType(DataEvent.DATA_STOPPED);
		}
	}
	
	//we need optimization probably: dynamically calculate getRightMilliseconds
    // This really needs to be figured out
	public static int rightMillis = 333;
	public int getRightMilliseconds(){return rightMillis;}

	public final static int DATA_TIME_OUT = 40;
	int timeWithoutData = 0;

	public void onEvent(Event event){
		if (event.type==ControlEvent.TIMER){
		    if(!step()){
				// There was an error in the step
				stop();
				Dialog.showMessageDialog(null, "Interface Error",
										 "Serial Read Error:|" +
										 "possibly no interface|" +
										 "connected",
										 "Continue", Dialog.ERR_DIALOG);
			}
		}
	}
	
	boolean step()
	{
		if(port == null) return false;

		buf[0] = (byte)(commandByte + currentChannel);
		int tmp = port.writeBytes(buf, 0, 1);
		
		if(tmp != 1){
			//error
			return false;
		} 

		switch(mode){
		case A2D_INTERNAL:
			// give it some time to return the voltage
			// how much time??
			// I'd guess it it pretty fast but I guess we'll
			// find out
			Vm.sleep(50);
			int ret = port.readBytes(buf,0,2);
			if(ret < 2){
				return false;
			}
			
			int value = ((buf[0] & 0xFF) << 8) | (buf[1] & 0xFF);

			valueData[0] = value;

			dEvent.setNumbSamples(1);
			dEvent.setIntData(valueData);
			dataArrived(dEvent);
			
			dEvent.setType(DataEvent.DATA_COLLECTING);
			dEvent.setIntTime(++curStepTime);
			idle(dEvent);
			dEvent.setType(DataEvent.DATA_RECEIVED);
			return true;
		default:
			return false;
		}
	}

	public float		getCurTime(){return (float)curStepTime*timeStepSize;}
	public void			setCurIntTime(int val){curStepTime = val;}


	public void dispose(){
		stop();
	}
	
	protected void finalize() throws Throwable {
		dispose();
	}
	
    public int getDefaultMode(){return A2D_INTERNAL;}

	public int getMode(){return mode;}
	public void setMode(int mode){
		this.mode = mode;
	}

}

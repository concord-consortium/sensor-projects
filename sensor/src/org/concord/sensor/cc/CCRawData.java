package org.concord.sensor.cc;

import org.concord.framework.data.DecoratedValue;
import org.concord.framework.data.stream.DataStreamDescription;
import org.concord.framework.data.stream.DataStreamEvent;
import org.concord.sensor.device.SensorDeviceMode;
import org.concord.sensor.device.SensorProducer;
import org.concord.waba.extra.util.PropObject;

public class CCRawData extends CCSensor
	implements CCModes
{
	int				firstIndex,secondIndex;

	public final static int		SAMPLING_24BIT_MODE = 0;
	public final static int		SAMPLING_10BIT_MODE = 1;

	String	[]samplingModes =  {"24 Bit","10 Bit"};
    String [] channelNames = {"0", "1"};
    String [] speedNames = {3 + speedUnit};

	PropObject sampProp = new PropObject("Sampling", "Sampling", PROP_SAMPLING, samplingModes);
	PropObject chanProp = new PropObject("Channel #", "Channel", PROP_CHAN_NUM, channelNames);
	PropObject speedProp = new PropObject("Speed", "Speed", PROP_SPEED, speedNames);

    int curChannel = 0;

	CCRawData(boolean init, short type, SensorProducer p){
		super(init, type, p);

		activeChannels = 2;
		defQuantityName = "Voltage";
		unit = "V";

		addProperty(sampProp);
		addProperty(chanProp);
		addProperty(speedProp);

	}

	public String getLabel()
	{
		return "Voltage " + "Ch. " + curChannel;
	}

    public boolean startSampling(DataStreamEvent e)
    {
		// Change to Volts
    	// At one point we were changing the tune value of the
    	// result data description.  Tune values are now being deprecated

    	if(activeChannels == 2){
			firstIndex = (curChannel == 1)?1:0;
			secondIndex = (curChannel == 1)?0:1;
		}else{
			firstIndex = secondIndex = 0;
		}

    	return true;
    }

	public int getQuantityPrecision(int mode)
	{
		if(mode != DEFAULT_OUTPUT_MODE) {
			return DecoratedValue.UNKNOWN_PRECISION;
		}
		
		// This is for the current part of the probe so...
		int modeIndex = sampProp.getIndex();
		if(modeIndex == 0){
			// A2D 24 mode  +/- 0.00015 milliVolts 
			return -5;
		} else { 
			// A2D 10 mode +/- 3 milliVolts
			return -3;
		}

	}

    public int dataArrived(DataStreamEvent e, float result[],
    		int resultOffset, int resultNextSampleOffset)
    {
		DataStreamDescription eDesc = e.getDataDescription();

		int nOffset 		= eDesc.getDataOffset();
		int[] data = e.getIntData();

		// Make this into volts instead of millivolts
		float tuneValue = eDesc.getChannelDescription().getTuneValue()/1000;
		
		int nextSampleOff = eDesc.getNextSampleOffset();

		int		nSamples	= e.getNumSamples();
		int 	ndata 		= nSamples*nextSampleOff;

		int 	v = 0,v1 = 0;

		int j=resultOffset;
		for(int i = nOffset; i < ndata; i+=nextSampleOff){
			if(activeChannels == 1){
				v = data[i];
				result[j] = (float)v*tuneValue;
			}else{
				v = data[i+firstIndex];
				result[j] = (float)v*tuneValue;

				v1 = data[i+secondIndex];
				result[j+1] = (float)v1*tuneValue;
			}

			j += resultNextSampleOffset;
		}

		return nSamples;
    }

	public boolean visValueChanged(PropObject po)
	{
		PropObject sampMode = sampProp;
		PropObject chNum = chanProp;
		PropObject speed = speedProp;

		int index = po.getVisIndex();
		if(po == sampMode){
			if(index == 0){
				String [] newSpeedNames = {3 + speedUnit};
				speed.setVisPossibleValues(newSpeedNames);
			} else if(index == 1){
				if(chNum.getVisIndex() == 0){
					String [] newSpeedNames = {200 + speedUnit, 400 + speedUnit};
					speed.setVisPossibleValues(newSpeedNames);
				} else {
					String [] newSpeedNames = {200 + speedUnit};
					speed.setVisPossibleValues(newSpeedNames);
				}
			}
		} else if(po == chNum){
			if(sampMode.getVisIndex() == 1){
				if(chNum.getVisIndex() == 0){
					String [] newSpeedNames = {200 + speedUnit, 400 + speedUnit};
					speed.setVisPossibleValues(newSpeedNames);
				} else {
					String [] newSpeedNames = {200 + speedUnit};
					speed.setVisPossibleValues(newSpeedNames);
				}				
			}
		}
		return true;
	}

	// need a function this called to setup the probe before
	// it is started
	public Object getInterfaceMode()
	{
		int intMode = A2D_24_MODE;

		int modeIndex = sampProp.getIndex();
		int chIndex = chanProp.getIndex();
		if(modeIndex == 0){
			intMode = A2D_24_MODE;
			if(chIndex == 0){
				curChannel = 0;
				activeChannels = 1;
			} else {
				curChannel = 1;
				activeChannels = 2;
			}
		} else if(modeIndex == 1){

			if(chIndex == 0){
				curChannel = 0;
				intMode = A2D_10_CH_0_MODE;
			} else {
				curChannel = 1;
				activeChannels = 2;
				intMode = A2D_10_CH_1_MODE;
			}
			if(speedProp.getIndex() == 0){
				activeChannels = 2;
				intMode = A2D_10_2_CH_MODE;
			} else {
				activeChannels = 1;
			}
		}

		interfaceMode = new SensorDeviceMode(getInterfacePort(), intMode); 

		return interfaceMode;
	}
}

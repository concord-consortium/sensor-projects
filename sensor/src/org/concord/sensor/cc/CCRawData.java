package org.concord.sensor.cc;

import org.concord.sensor.*;
import org.concord.framework.data.stream.*;
import org.concord.waba.extra.util.*;

public class CCRawData extends Sensor
	implements CCModes
{
	float  			[]rawData = new float[CCSensorProducer.BUF_SIZE];
	int  			[]rawIntData = new int[CCSensorProducer.BUF_SIZE];
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

		dDesc.setChannelPerSample(2);
		dDesc.setDt(0.0f);
		dDesc.setDataOffset(0);
		dEvent.setDataDescription(dDesc);

		dEvent.setNumSamples(1);
		dEvent.setData(rawData);
		dEvent.setIntData(rawIntData);

		addProperty(sampProp);
		addProperty(chanProp);
		addProperty(speedProp);

		unit = "V";
	}

	public String getLabel()
	{
		return "Voltage " + "Ch. " + curChannel;
	}

    public boolean startSampling(DataEvent e){
		dEvent.type = e.type;
		dDesc.setDt(e.getDataDescription().getDt());
		// Change to Volts
		dDesc.setTuneValue(e.getDataDescription().getTuneValue()/1000f);
		if(activeChannels == 2){
			dDesc.setChannelPerSample(2);
			firstIndex = (curChannel == 1)?1:0;
			secondIndex = (curChannel == 1)?0:1;
		}else{
			dDesc.setChannelPerSample(1);
			firstIndex = secondIndex = 0;
		}
		return super.startSampling(dEvent);
	}

	public int getPrecision()
	{
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

    public boolean dataArrived(DataEvent e)
    {
		DataStreamDescription eDesc = e.getDataDescription();

		int nOffset 		= eDesc.getDataOffset();
		int[] data = e.getIntData();

		float tuneValue = dDesc.getTuneValue();
		
		int nextSampleOff = eDesc.getNextSampleOffset();

		int		nSamples	= e.getNumSamples();
		int 	ndata 		= nSamples*nextSampleOff;

		int 	v = 0,v1 = 0;
		dEvent.type = e.type;
		int j=0;
		for(int i = nOffset; i < ndata; i+=nextSampleOff){
			if(activeChannels == 1){
				v = data[i];
				rawIntData[j] = v;
				rawData[j] = (float)v*tuneValue;
				j++;
			}else{
				v = data[i+firstIndex];
				rawIntData[j] = v;
				rawData[j] = (float)v*tuneValue;
				j++;

				v1 = data[i+secondIndex];
				rawIntData[j] = v1;
				rawData[j] = (float)v1*tuneValue;
				j++;
			}
		}
		dEvent.setNumSamples(nSamples);
		return super.dataArrived(dEvent);
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

		interfaceMode = CCInterface2.getMode(getInterfacePort(), intMode);

		return interfaceMode;
	}
}

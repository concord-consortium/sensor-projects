package org.concord.sensor.cc;

import org.concord.framework.data.stream.DataStreamDescription;
import org.concord.framework.data.stream.DataStreamEvent;
import org.concord.sensor.device.CalibrationDesc;
import org.concord.sensor.device.CalibrationParam;
import org.concord.sensor.device.SensorProducer;
import org.concord.waba.extra.util.PropObject;

public class CCLightIntens extends CCSensor
	implements CCModes
{
	/*
	  Lux=(input(mV)-offset(mV))/sensitivity(mV/Lux)

		                    calculated	standard	maximum	
		offset	sensitivity	range	    deviation	deviation	
		5.11	0.0206	    121209	    1.2%	     1.5%	   125k Lux Range
		6.42	0.5655	      4409	    0.8%	     1.3%      4k Lux range
	*/

	// A = 1/sensitivity
	// B = -offset/sensitivity
	float AHigh  = 1f/0.0206f;
	float BHigh  = -5.11f/0.0206f;
	float ALow   = 1f/0.5655f;
	float BLow   = -6.42f/0.5655f;

	public final static int		HIGH_LIGHT_MODE 			= 0;
	public final static int		LOW_LIGHT_MODE 			= 1;
	int				lightMode = HIGH_LIGHT_MODE;

	PropObject rangeProp = new PropObject("Range", "Range", PROP_RANGE, rangeNames);
	PropObject speedProp = new PropObject("Speed", "Speed", PROP_SPEED, speed1Names);

	public static String [] rangeNames = {"Bright Light", "Dim Light"};
	public static String [] speed1Names = {3 + speedUnit, 200 + speedUnit, 400 + speedUnit};
	public static String [] speed2Names = {3 + speedUnit, 200 + speedUnit};

	CCLightIntens(boolean init, short type, SensorProducer p)
	{
		super(init, type, p);

	    activeChannels = 2;
		defQuantityName = "Intensity";
		unit = "lx";
		precision = 0;
		
		addProperty(rangeProp);
		addProperty(speedProp);

		if(init){
			lightMode = 0;

			calibrationDesc = new CalibrationDesc();
			calibrationDesc.addCalibrationParam(new CalibrationParam(0,AHigh));
			calibrationDesc.addCalibrationParam(new CalibrationParam(1,BHigh));
			calibrationDesc.addCalibrationParam(new CalibrationParam(2,ALow));
			calibrationDesc.addCalibrationParam(new CalibrationParam(3,BLow));
		}

	}

	public boolean visValueChanged(PropObject po)
	{
		int index = po.getVisIndex();
		if(po == rangeProp){
			if(index == 0){
				speedProp.setVisPossibleValues(speed1Names);
			} else {
				speedProp.setVisPossibleValues(speed2Names);
			}
		}

		return true;
	}

	public CalibrationDesc getCalibrationDesc()
	{
		int lightMode = rangeProp.getIndex();

		CalibrationParam cp = calibrationDesc.getCalibrationParam(0);
		if(cp != null) cp.setAvailable(lightMode == HIGH_LIGHT_MODE);
		cp = calibrationDesc.getCalibrationParam(1);
		if(cp != null) cp.setAvailable(lightMode == HIGH_LIGHT_MODE);
		cp = calibrationDesc.getCalibrationParam(2);
		if(cp != null) cp.setAvailable(lightMode == LOW_LIGHT_MODE);
		cp = calibrationDesc.getCalibrationParam(3);
		if(cp != null) cp.setAvailable(lightMode == LOW_LIGHT_MODE);

		return calibrationDesc;
	}

	public Object getInterfaceMode()
	{
		int intMode =  A2D_24_MODE;
		int speedIndex = speedProp.getIndex();

		if(speedIndex == 0){
			intMode = A2D_24_MODE;
			activeChannels = 2;
		} else if(speedIndex == 1){
			intMode = A2D_10_2_CH_MODE;
			activeChannels = 2;
		} else if(speedIndex == 2){
			intMode = A2D_10_CH_0_MODE;
			activeChannels = 1;
		}

		interfaceMode = CCInterface2.getMode(getInterfacePort(), intMode);
		return interfaceMode;
	}

	public int  getActiveCalibrationChannels(){return 1;}

	public boolean startSampling(DataStreamEvent e)
	{
		lightMode = rangeProp.getIndex();
		
		return true;
    }

	// Could be handled by a generic linear transform sensor
	public int dataArrived(DataStreamEvent e, float result[],
			int resultOffset, int resultNextSampleOffset)
	{
		DataStreamDescription eDesc = e.getDataDescription();

		int[] data = e.getIntData();
		int nOffset = eDesc.getDataOffset();

		int nextSampleOff = eDesc.getNextSampleOffset();

		int  	chPerSample = eDesc.getChannelsPerSample();
		int ndata = e.getNumSamples()*nextSampleOff;

		if(ndata < nextSampleOff) return -1;
		int dataIndex = resultOffset;	
		for(int i = 0; i < ndata; i+=nextSampleOff){
			if(lightMode == HIGH_LIGHT_MODE){
				int v = data[nOffset+i];
				
				result[dataIndex] = AHigh*eDesc.getChannelDescription().getTuneValue()*(float)v+BHigh;
			}else{
				int v = data[nOffset+i+1];
				result[dataIndex] = ALow*eDesc.getChannelDescription().getTuneValue()*(float)v+BLow;
			}
			
			// I don't know what this is for???
			if(result[dataIndex] < 0f){
				result[dataIndex] = 0f;
			}
			dataIndex += resultNextSampleOffset;
		}

		return e.getNumSamples();
	}

	public void  calibrationDone(float []row1,float []row2,float []calibrated)
	{
		if(row1 == null || calibrated == null) return;
		float x1 = row1[0];
		float x2 = row1[1];
		float y1 = calibrated[0];
		float y2 = calibrated[1];
		float A = (y2 - y1)/(x2 - x1);
		float B = y2 - A*x2;
		if(lightMode == HIGH_LIGHT_MODE){
			AHigh = A;
			BHigh = B;
			if(calibrationDesc != null){
				CalibrationParam p = calibrationDesc.getCalibrationParam(0);
				if(p != null) p.setValue(AHigh);
				p = calibrationDesc.getCalibrationParam(1);
				if(p != null) p.setValue(BHigh);
			}
		}else if(lightMode == LOW_LIGHT_MODE){
			ALow = A;
			BLow = B;
			if(calibrationDesc != null){
				CalibrationParam p = calibrationDesc.getCalibrationParam(2);
				if(p != null) p.setValue(ALow);
				p = calibrationDesc.getCalibrationParam(3);
				if(p != null) p.setValue(BLow);
			}
		}
	}

	public void calibrationDescReady(){
		if(calibrationDesc == null) return;
		CalibrationParam p = calibrationDesc.getCalibrationParam(0);
		if(p != null && p.isValid()){
			AHigh = p.getValue();
		}
		p = calibrationDesc.getCalibrationParam(1);
		if(p != null && p.isValid()){
			BHigh = p.getValue();
		}
		p = calibrationDesc.getCalibrationParam(2);
		if(p != null && p.isValid()){
			ALow = p.getValue();
		}
		p = calibrationDesc.getCalibrationParam(3);
		if(p != null && p.isValid()){
			BLow = p.getValue();
		}
	}
}

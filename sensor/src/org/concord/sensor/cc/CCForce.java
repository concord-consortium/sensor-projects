package org.concord.sensor.cc;

import org.concord.framework.data.DecoratedValue;
import org.concord.framework.data.stream.DataStreamDescription;
import org.concord.framework.data.stream.DataStreamEvent;
import org.concord.sensor.device.CalibrationDesc;
import org.concord.sensor.device.CalibrationParam;
import org.concord.sensor.device.SensorDeviceMode;
import org.concord.sensor.device.SensorEvent;
import org.concord.sensor.device.SensorProducer;
import org.concord.waba.extra.util.PropObject;

public class CCForce extends CCSensor
	implements CCModes
{
	public final static String [] modeNames = {"End of Arm", "Middle of Arm"};
	public final static String [] range1Names = {"+/- 2N", "+/- 20N"};
	public final static String [] range2Names = {"+/- 200N"};
	public final static String [] speed1Names = {3 + speedUnit, 200 + speedUnit, 400 + speedUnit};
	public final static String [] speed2Names = {3 + speedUnit, 200 + speedUnit};

	public int curChannel = 1;
	/*
	  Newtons f(mV input){ return (input - offset) / sensitivity; }
	                        max	    max	    standard	maximum		
      offset	sensitivity	range	range	deviation	deviation		
      1317.2	-49.133	    26.8	-24.1	5.9%	    10.7%	x1 - end of beam	
      1317.2	-4.735	    278	    -250	22.8%	    29.7%	x1 - middle of beam	
      1845.2	-471.735	3.9	    -1.4	22.2%	    27.4%	x10 - end of beam	

	  old values
	  float	A = 0.01734f;
	  float B = -25.31f;
	 */

	// f(input) = A*input + B;
	// A = 1/sensitivity
	// B = -(offset/sensitivity)
	float end_x1_A     = 1f/-49.133f;
	float end_x1_B     = 1317.2f/49.133f;
	float middle_x1_A  = 1f/-4.735f;
	float middle_x1_B  = 1317.2f/4.735f;
	float end_x10_A    = 1f/-471.735f;
	float end_x10_B    = 1845.2f/471.735f;

	float curA, curB;
	int calDescIndex;

	PropObject modeProp = new PropObject("Mode", "Mode", PROP_MODE, modeNames);
	PropObject rangeProp = new PropObject("Range", "Range", PROP_RANGE, range1Names, 1);
	PropObject speedProp = new PropObject("Speed", "Speed", PROP_SPEED, speed1Names);

	protected CCForce(boolean init, short type, SensorProducer p){
		super(init, type, p);

	    activeChannels = 1;
		defQuantityName = "Force";
		unit = "N";
		
		addProperty(modeProp);
		addProperty(rangeProp);
		addProperty(speedProp);

		if(init){	
			calibrationDesc = new CalibrationDesc();
			calibrationDesc.addCalibrationParam(new CalibrationParam(0,end_x1_A));
			calibrationDesc.addCalibrationParam(new CalibrationParam(1,end_x1_B));
			calibrationDesc.addCalibrationParam(new CalibrationParam(2,middle_x1_A));
			calibrationDesc.addCalibrationParam(new CalibrationParam(3,middle_x1_B));
			calibrationDesc.addCalibrationParam(new CalibrationParam(4,end_x10_A));
			calibrationDesc.addCalibrationParam(new CalibrationParam(5,end_x10_B));
		}
	}

	boolean zeroing = false;
	int zeroCount = 0;
	float zeroSum = 0f;
	public void startZero()
	{
		zeroing = true;
		zeroCount = 0;
		zeroSum = 0f;
	}

	public boolean visValueChanged(PropObject po)
	{
		int index = po.getVisIndex();
		if(po == rangeProp){
			if(index == 0){
				speedProp.setVisPossibleValues(speed2Names);
			} else if(index == 1){
				speedProp.setVisPossibleValues(speed1Names);
			}
		} else if(po == modeProp){
			if(index == 0){
				rangeProp.setVisPossibleValues(range1Names);
				rangeProp.setVisIndex(1);
			} else if(index == 1){
				rangeProp.setVisPossibleValues(range2Names);
			}
		} 
		return true;
	}


	public int getQuantityPrecision(int mode)
	{
		if(mode != DEFAULT_OUTPUT_MODE) {
			return DecoratedValue.UNKNOWN_PRECISION;			
		}
		
		int rangeIndex = rangeProp.getIndex();
		int speedIndex = speedProp.getIndex();

		int modeIndex = modeProp.getIndex();
		
		if(speedIndex == 1 ||
		   speedIndex == 2){
			// a2d 10bit TuneValue in 10bit mode is ~ 3 mV
			// In 10bit mode it is the resolution of the step
			// size in the a2d convertor that is the limiting factor
			// so the return is to round down log(A * TuneValue)
			// where A is from Ax + b.
			if(modeIndex == 0){
				// end of arm
				if(rangeIndex == 1){
					// x1
					return -2;
				} else {
					// x10
					return -3;
				}
			} else {
				// middle of arm
				return -1;
			}
		} else if(speedIndex == 0){
			// a2d 24
			// Tune Value is at worse 0.00015f
			// in twenty four bit mode it is noise not
			// steps that cause the problems
			if(modeIndex == 0){
				// end of arm
				// regardless of the range the noise if the 
				// the same
				return -3;
			} else {
				// middle of arm				
				return -2;
			}
		} 

		return DecoratedValue.UNKNOWN_PRECISION;
	}

	/*
	 *  This should cache the mode object so we aren't
	 * creating a new one every time, but in this case it
	 * doesn't mater.
	 */
	public Object getInterfaceMode()
	{
		// Default mode
		int intMode = A2D_24_MODE;

		int rangeIndex = rangeProp.getIndex();
		int speedIndex = speedProp.getIndex();
		int modeIndex = modeProp.getIndex();

		if(modeIndex == 0){
			// end of arm
			curChannel = 1-rangeIndex;
		} else {
			// middle of arm
			curChannel = 0;
		}

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

		interfaceMode = new SensorDeviceMode(getInterfacePort(), intMode);
		return interfaceMode;
	}

	int chPerSample = 2;
	int channelOffset = 0;

	public boolean startSampling(DataStreamEvent e)
	{
		DataStreamDescription eDesc = 
			e.getDataDescription();

		// dEvent.type = e.type;
		// dDesc.setDt(eDesc.getDt());

		chPerSample = eDesc.getChannelsPerSample();
		// dDesc.getChannelDescription().setTuneValue(eDesc.getChannelDescription().getTuneValue());

		// dDesc.setNextSampleOffset(1);
		// dDesc.setChannelsPerSample(1);

		channelOffset = curChannel;
		if(curChannel > activeChannels - 1) channelOffset = activeChannels - 1;

		int rangeIndex = rangeProp.getIndex();
		int modeIndex = modeProp.getIndex();

		if(modeIndex == 0){
			if(rangeIndex == 1){
				calDescIndex = 0;
			} else {
				calDescIndex = 4;
			}
		} else {
			calDescIndex = 2;
		}

		CalibrationParam p = calibrationDesc.getCalibrationParam(calDescIndex);
		if(p == null || !p.isValid()) return false;
		curA = p.getValue();

		p = calibrationDesc.getCalibrationParam(calDescIndex + 1);
		if(p == null || !p.isValid()) return false;
		curB = p.getValue();

		// if(!zeroing) return super.startSampling(dEvent);
		return true;
    }

	public final static int ZEROING_DONE = 0;
	public final static int ZEROING_END_POINT = 10;
	public final static int ZEROING_START_POINT = 4;

	/*
	 * This was need to keep the zeroing data from being sent out 
	 * in the new approach this shouldn't happen
	public boolean idle(DataStreamEvent e){
		if(!zeroing) return super.idle(e);
		return true;
	}
	*/

	/**
	 * If we are zeroing the force sensor then we probably need to 
	 * return NaN instead of nothing.  This way if other sensors are being
	 * handle the data will line up correctly.
	 */
	public int dataArrived(DataStreamEvent inputData, float [] result, 
			int resultOffset, int resultNextSampleOffset)
	{
		DataStreamDescription eDesc = inputData.getDataDescription();
		float v = eDesc.getChannelDescription().getTuneValue();
		
		int nextSampleOff = eDesc.getNextSampleOffset();

		if(zeroing){
			int ndata = inputData.numSamples*nextSampleOff;
			int dOff = eDesc.getDataOffset();
			int data [] = inputData.intData;

			// notice there is a hack in here to skip the first point
			// it seems to be screwed up some how
			for(int i = 0; i < ndata; i+= nextSampleOff){
				if(zeroCount >= ZEROING_START_POINT){
					zeroSum += (float)(data[dOff + i+channelOffset]);
				}
				zeroCount++;			   
				if(zeroCount > ZEROING_END_POINT){
					notifyProbeListeners(new SensorEvent(this, ZEROING_DONE, null));
					float offsetN = -curA*v*(zeroSum/(float)(zeroCount-ZEROING_START_POINT));
					if(calibrationDesc != null){
						// need to find the which calibration this is;
						CalibrationParam p = calibrationDesc.getCalibrationParam(calDescIndex + 1);
						if(p != null) p.setValue(offsetN);
						curB = offsetN;
					}
					zeroing = false;
					break;
				}
			}
			return 0;
		} else{
			int ndata = inputData.numSamples*nextSampleOff;
			int dOff = eDesc.getDataOffset();
			int data [] = inputData.intData;
			int currPos = resultOffset;
			float mult = curA*v;
			for(int i = 0; i < ndata; i+= nextSampleOff){
				// this is probably not needed anymore
				int rawData = data[dOff + i+channelOffset];

				// put this data in the result now instead of our own event
				result[currPos] = mult*rawData+curB;
				currPos += resultNextSampleOffset;
			}
		}
		
		return inputData.numSamples;		
	}

	public void  calibrationDone(float []row1,float []row2,float []calibrated){
		if(row1 == null || calibrated == null) return;
		float x1, x2;
		if(curChannel ==0){
		    x1 = row1[0];
		    x2 = row1[1];
		} else {
		    x1 = row2[0];
		    x2 = row2[1];
		}
		float y1 = calibrated[0];
		float y2 = calibrated[1];
		curA = (y2 - y1)/(x2 - x1);
		curB = y2 - curA*x2;
		if(calibrationDesc != null){
			CalibrationParam p = calibrationDesc.getCalibrationParam(calDescIndex);
			if(p != null) p.setValue(curA);
			p = calibrationDesc.getCalibrationParam(calDescIndex+1);
			if(p != null) p.setValue(curB);
		}
	}
	public void calibrationDescReady(){
		if(calibrationDesc == null) return;

		CalibrationParam p = calibrationDesc.getCalibrationParam(0);
		if(p == null || !p.isValid()) return;
		end_x1_A = p.getValue();

		p = calibrationDesc.getCalibrationParam(1);
		if(p == null || !p.isValid()) return;
		end_x1_B = p.getValue();

		p = calibrationDesc.getCalibrationParam(2);
		if(p == null || !p.isValid()) return;
		middle_x1_A = p.getValue();

		p = calibrationDesc.getCalibrationParam(3);
		if(p == null || !p.isValid()) return;
		middle_x1_B = p.getValue();

		p = calibrationDesc.getCalibrationParam(4);
		if(p == null || !p.isValid()) return;
		end_x10_A = p.getValue();

		p = calibrationDesc.getCalibrationParam(5);
		if(p == null || !p.isValid()) return;
		end_x10_B = p.getValue();
	}
}

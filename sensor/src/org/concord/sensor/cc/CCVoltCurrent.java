package org.concord.sensor.cc;

import org.concord.framework.data.DecoratedValue;
import org.concord.framework.data.stream.DataStreamDescription;
import org.concord.framework.data.stream.DataStreamEvent;
import org.concord.sensor.device.CalibrationDesc;
import org.concord.sensor.device.CalibrationParam;
import org.concord.sensor.device.Sensor;
import org.concord.sensor.device.SensorProducer;
import org.concord.waba.extra.util.PropObject;



public class CCVoltCurrent extends Sensor
	implements CCModes
{
	float				energy = 0.0f;
	public final static int		CURRENT_OUT 			= 0;
	public final static int		VOLTAGE_OUT 			= 1;
	public final static int		POWER_OUT 			= 2;
	public final static int		ENERGY_OUT 			= 3;

	PropObject rangeProp = new PropObject("Range", "Range", PROP_RANGE, rangeNames);
	PropObject speedProp = new PropObject("Speed", "Speed", PROP_SPEED, speedNames);
	PropObject versionProp = new PropObject("Version", "Version", PROP_VERSION, versionNames);

	/*
	    Voltage=(input(mV)-offset(mV))/sensitivity(mV/Volt)
		                    max	    min	    standard	maximum	
	    offset	sensitivity	range	range	deviation	deviation	range
		1252.7	63.13	    19.76	-19.84	4.5%	     7.4%	    "+/- 20V"
	*/
	float					zeroPointVoltage		= 1252.7f;//	
	float					voltageResolution		= 63.13f; //     mV(reading)/(true)V

	/*
		Current=(input(mV)-offset(mV))/sensitivity(mV/Amp)
		                    max	    min	    standard	maximum	    maximum
		offset	sensitivity	range	range	deviation	deviation	range
		1247.1	620.95	    2.02	-2.01	4.1%	    10.2%       "+/- 2A"
	 */
	float					zeroPointCurrent  = 1247.1f; //	
	float					currentResolution = 620.95f; //       mV(reading)/A

    // old values
	//	float					currentResolution		= 271f; //       mV(reading)/A
	//  float					voltageResolution		= 38f; //     mV(reading)/(true)V

	int					outputMode 			= VOLTAGE_OUT;
	public static String [] modeNames = {"Current", "Voltage","Power","Energy"};
	public static String [] rangeNames = {"unknown"};
	public static String [] speedNames = {3 + speedUnit, 200 + speedUnit};
	public static String [] versionNames = {"1.0", "2.0"};
   
	int 				curChannel = 0;

	int version = 1;
	int voltOff = 1;
	int currentOff = 0;

	CCVoltCurrent(boolean init, short type, SensorProducer p){
		super(init, type, p);

		activeChannels = 2;
		quantityNames = modeNames;
		defQuantityName = "Current";

		// addProperty(modeProp);
		addProperty(rangeProp);
		addProperty(speedProp);
		addProperty(versionProp);

		if(init){		
			calibrationDesc = new CalibrationDesc();
			calibrationDesc.addCalibrationParam(new CalibrationParam(0,zeroPointCurrent));
			calibrationDesc.addCalibrationParam(new CalibrationParam(1,currentResolution));
			calibrationDesc.addCalibrationParam(new CalibrationParam(2,zeroPointVoltage));
			calibrationDesc.addCalibrationParam(new CalibrationParam(3,voltageResolution));
		}
	}

	public String getQuantityUnit(int mode)
	{
		switch(mode){
			case CURRENT_OUT:
			case DEFAULT_OUTPUT_MODE:
				return "A";
			case VOLTAGE_OUT:
				return "V";
			case POWER_OUT:
				return "W";
			case ENERGY_OUT:
				return "J";
		}

		return null;		
	}

	public int getQuantityPrecision(int mode)
	{
		int speedIndex = speedProp.getIndex();
		if(speedIndex == 0){
			// A2D 24 mode
			switch(mode){
				case CURRENT_OUT:
				case DEFAULT_OUTPUT_MODE:
					// A2D 24 mode  +/- 4microAmps
					return -6;
				case VOLTAGE_OUT:
					// voltage mode +/- 40 microVolts
					return -5;
				case POWER_OUT:
					// This is tricky because it isn't a linear function
					// for now I'll just guess
					return -5;
				case ENERGY_OUT:
					// same here just a guess
					return -5;
			}
		} else {
			// A2D 10 mode 
			switch(mode){
				case CURRENT_OUT:
				case DEFAULT_OUTPUT_MODE:
					// A2D 10 mode +/- 4 milliAmps
					return -3;					
				case VOLTAGE_OUT:
					// voltage mode +/- 40 microVolts
					return -3;
				case POWER_OUT:
					// This is tricky because it isn't a linear function
					// for now I'll just guess
					return -3;
				case ENERGY_OUT:
					// same here just a guess
					return -3;
			}
		} 
		
		return DecoratedValue.UNKNOWN_PRECISION;
	}

	public CalibrationDesc getCalibrationDesc()
	{
		CalibrationParam cp = calibrationDesc.getCalibrationParam(0);
		if(cp != null) cp.setAvailable(outputMode == CURRENT_OUT);
		cp = calibrationDesc.getCalibrationParam(1);
		if(cp != null) cp.setAvailable(outputMode == CURRENT_OUT);
		cp = calibrationDesc.getCalibrationParam(2);
		if(cp != null) cp.setAvailable(outputMode == VOLTAGE_OUT);
		cp = calibrationDesc.getCalibrationParam(3);
		if(cp != null) cp.setAvailable(outputMode == VOLTAGE_OUT);

		return calibrationDesc;
	}

	public int  getActiveCalibrationChannels(){return 1;}

	public boolean visValueChanged(PropObject po)
	{
		/*
		if(po == modeProp || po == versionProp){
			int mIndex = modeProp.getVisIndex();
			int vIndex = versionProp.getVisIndex();
			   
			if((mIndex == 0 && vIndex == 0)  ||
			   (mIndex == 1 && vIndex == 1)){
				speedProp.setVisPossibleValues(speed1Names);
			} else {
				speedProp.setVisPossibleValues(speed2Names);
			}
		}
		*/

		return true;
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
		} 
		return CCInterface2.getMode(getInterfacePort(), intMode);
	}
	
	public boolean startSampling(DataStreamEvent e)
	{
		DataStreamDescription eDesc = e.getDataDescription();

		outputMode = CURRENT_OUT;
		int vIndex = versionProp.getIndex();
		if(vIndex == 0){
			version = 1;
			voltOff = 1;
			currentOff = 0;
		}else {
			version = 2;
			voltOff = 0;
			currentOff = 1;
		}

		energy = 0.0f;

		// dDesc.setIntChPerSample(2);

		return true;
	}

    public int dataArrived(DataStreamEvent e, float [] result,
    		int resultOffset, int resultNextSampleOffset)
    {
		DataStreamDescription eDesc = e.getDataDescription();

		int nOffset 		= eDesc.getDataOffset();
		int nextSampleOff = eDesc.getNextSampleOffset();
		int ndata 			= e.getNumSamples()*nextSampleOff;

		int[] dataEvent 	= e.getIntData();

		int  	chPerSample = eDesc.getChannelsPerSample();
		int	dataIndex;
		
		for(int modeIndex = 0; modeIndex<outputModes.length; modeIndex++) {
			dataIndex = resultOffset;
			if(outputModes[modeIndex] == CURRENT_OUT) {
				for(int i = 0; i < ndata; i+=nextSampleOff){
					result[dataIndex] = 
						(dataEvent[nOffset+i+currentOff]*eDesc.getChannelDescription().getTuneValue() - zeroPointCurrent)/currentResolution;
					dataIndex += resultNextSampleOffset;
				}
			} else if(outputModes[modeIndex] == VOLTAGE_OUT) {
				for(int i = 0; i < ndata; i+=nextSampleOff){
					result[dataIndex] = (dataEvent[nOffset+i+voltOff]*eDesc.getChannelDescription().getTuneValue() - zeroPointVoltage)/voltageResolution;
					dataIndex += resultNextSampleOffset;
				}				
			} else if(outputModes[modeIndex] == POWER_OUT) {
				for(int i = 0; i < ndata; i+=nextSampleOff){
					int currentInt = dataEvent[nOffset+i+currentOff];
					int voltInt = dataEvent[nOffset+i+voltOff];
					
					float		amper = (currentInt*eDesc.getChannelDescription().getTuneValue() - zeroPointCurrent)/currentResolution;
					float		voltage = (voltInt*eDesc.getChannelDescription().getTuneValue() - zeroPointVoltage)/voltageResolution;
					result[dataIndex] = amper*voltage;
					if(result[dataIndex] < 0f){
						result[dataIndex] = -result[dataIndex];
					}
					dataIndex += resultNextSampleOffset;
				}				
			} else if(outputModes[modeIndex] == ENERGY_OUT) {
				for(int i = 0; i < ndata; i+=nextSampleOff){
					int currentInt = dataEvent[nOffset+i+currentOff];
					int voltInt = dataEvent[nOffset+i+voltOff];

					float		amper = (currentInt*eDesc.getChannelDescription().getTuneValue() - zeroPointCurrent)/currentResolution;
					float		voltage = (voltInt*eDesc.getChannelDescription().getTuneValue() - zeroPointVoltage)/voltageResolution;

					result[dataIndex] = amper*voltage;
					if(result[dataIndex] < 0f){
						result[dataIndex] = -result[dataIndex];
					}
					energy 	+= result[dataIndex]*eDesc.getDt(); 
					result[dataIndex] 	= energy;
					dataIndex += resultNextSampleOffset;
				}				
			}
		}
		
		return e.getNumSamples();
	}
    
	public void  calibrationDone(float []row1,float []row2,float []calibrated){
		if(outputMode != CURRENT_OUT && outputMode != VOLTAGE_OUT) return;
		if(row1 == null  || calibrated == null) return;
		float zeroPoint = (calibrated[0]*row1[1] - calibrated[1]*row1[0])/(calibrated[0] - calibrated[1]);
		float resolution = (row1[0] - row1[1])/(calibrated[0] - calibrated[1]);
		
		if(outputMode == CURRENT_OUT){
			zeroPointCurrent 		= zeroPoint;
			currentResolution 		= resolution;
			if(calibrationDesc != null){
				CalibrationParam p = calibrationDesc.getCalibrationParam(0);
				if(p != null) p.setValue(zeroPointCurrent);
				p = calibrationDesc.getCalibrationParam(1);
				if(p != null) p.setValue(currentResolution);
			}
		}else if(outputMode == VOLTAGE_OUT){
			zeroPointVoltage 		= zeroPoint;
			voltageResolution 		= resolution;
			if(calibrationDesc != null){
				CalibrationParam p = calibrationDesc.getCalibrationParam(2);
				if(p != null) p.setValue(zeroPointVoltage);
				p = calibrationDesc.getCalibrationParam(3);
				if(p != null) p.setValue(voltageResolution);
			}
		}
	}
	public void calibrationDescReady(){
		if(calibrationDesc == null) return;
		CalibrationParam p = calibrationDesc.getCalibrationParam(0);
		if(p != null && p.isValid()){
			zeroPointCurrent = p.getValue();
		}
		p = calibrationDesc.getCalibrationParam(1);
		if(p != null && p.isValid()){
			currentResolution = p.getValue();
		}
		p = calibrationDesc.getCalibrationParam(2);
		if(p != null && p.isValid()){
			zeroPointVoltage = p.getValue();
		}
		p = calibrationDesc.getCalibrationParam(3);
		if(p != null && p.isValid()){
			voltageResolution = p.getValue();
		}
	}

}

package org.concord.sensor.cc;

import org.concord.framework.data.stream.DataStreamDescription;
import org.concord.framework.data.stream.DataStreamEvent;
import org.concord.sensor.waba.CalibrationDesc;
import org.concord.sensor.waba.CalibrationParam;
import org.concord.sensor.waba.SensorDeviceMode;
import org.concord.sensor.waba.SensorProducer;
import org.concord.waba.extra.util.PropObject;

public class CCThermalCouple extends CCSensor
	implements CCModes
{
	public final static int		CELSIUS_TEMP_OUT = 0;
	public final static int		FAHRENHEIT_TEMP_OUT = 1;
	public final static int		KELVIN_TEMP_OUT = 2;

	PropObject modeProp = new PropObject("Output Mode", "Mode", PROP_MODE, 
										 tempModes, CELSIUS_TEMP_OUT);

	int				outputMode = CELSIUS_TEMP_OUT;
	public final static String	[]tempModes =  {"C","F","K"};

	float AC = 17.084f;
	float BC = -0.25863f;
	float CC = 0.011012f;
	float DC = 10f;
	float EC = -50f;
	float FC = 0.0f;

	CCThermalCouple(boolean init, short type, SensorProducer p)
	{
		super(init, type, p);

		interfaceMode = new SensorDeviceMode(PORT_A, A2D_24_MODE);

		activeChannels = 2;
		defQuantityName = "Temperature";
		precision = -1;
		
		addProperty(modeProp);

		if(init){
			calibrationDesc = new CalibrationDesc();
			calibrationDesc.addCalibrationParam(new CalibrationParam(0,FC));			
		}
	}

	public String getQuantityUnit(int mode)
	{
		if(mode != DEFAULT_OUTPUT_MODE) {
			return null;
		}
		
		int oMode = modeProp.getIndex();
		
		switch(oMode){
		case FAHRENHEIT_TEMP_OUT:
			return "F";
		case KELVIN_TEMP_OUT:
			return "K";
		case CELSIUS_TEMP_OUT:
		default:
			return "C";
		}	   

	}

	public Object getInterfaceMode()
	{
		return interfaceMode;
	}

    // This could be handled by a generic cubic calibration sensor
	public int dataArrived(DataStreamEvent e, float result[],
			int resultOffset, int resultNextSampleOffset)
	{
		DataStreamDescription eDesc = e.getDataDescription();

		int nOffset = eDesc.getDataOffset();
		int nextSampleOff = eDesc.getNextSampleOffset();

		int ndata = e.getNumSamples()*nextSampleOff;
		if(ndata == 0) return -1;
		
		int[] data = e.getIntData();
		float tuneValue = eDesc.getChannelDescription().getTuneValue();
		for(int i = 0; i < ndata; i+=nextSampleOff){
			float mV = (float)data[nOffset+i]*tuneValue;
			float ch2 = (float)data[nOffset+i+1]*tuneValue;
			float lastColdJunct = (ch2 / DC) + EC;
			float tempData = mV * (AC + mV * (BC + mV * CC)) + lastColdJunct;
			tempData += FC;
			switch(outputMode){
			case FAHRENHEIT_TEMP_OUT:
				tempData = tempData*1.8f + 32f;
				break;
			case KELVIN_TEMP_OUT:
				tempData += 273.15f;
				break;
			default:
				break;
			}
			result[resultOffset + i*resultNextSampleOffset] = tempData;
		}
		return e.getNumSamples();
	}

	public void  calibrationDone(float []row1,float []row2,float []calibrated)
	{
		if(row1 == null || calibrated == null) return;
		float ch1 = row1[0];
		float ch2 = row2[0];
		float lastColdJunct = (ch2 / DC) + EC;
		float mV = ch1;
		float mV2 = mV * mV;
		float mV3 = mV2 * mV;
		float trueValue = mV * AC + mV2 * BC + mV3 * CC + lastColdJunct;
		float userValue = calibrated[0];
		switch(outputMode){
		case FAHRENHEIT_TEMP_OUT:
			userValue = (userValue - 32f)/1.8f;
			break;
		case KELVIN_TEMP_OUT:
			userValue -= 273.15f;
			break;
		default:
			break;
		}
		FC = userValue - trueValue;
		if(calibrationDesc != null){
			CalibrationParam p = calibrationDesc.getCalibrationParam(0);
			if(p != null) p.setValue(FC);
		}
	}
	public void calibrationDescReady(){
		if(calibrationDesc == null) return;
		CalibrationParam p = calibrationDesc.getCalibrationParam(0);
		if(p == null || !p.isValid()) return;
		FC = p.getValue();
	}
}

package org.concord.sensor.cc;

import org.concord.sensor.*;
import org.concord.waba.extra.util.*;

import org.concord.framework.data.*;
import org.concord.framework.data.stream.*;

public class CCSmartWheel extends Sensor
	implements CCModes
{
float  			[]wheelData 	= new float[CCSensorProducer.BUF_SIZE*2];
int  			[]wheelIntData 	= new int[CCSensorProducer.BUF_SIZE*2];
float  			dtChannel = 0.0f;
int				nTicks = 660;
float				radius = 0.06f;
float				koeff = 2f*Maths.PI;

	/*
	PropObject modeProp = new PropObject("Output Mode", "Mode", PROP_MODE, wheelModes,
										 LIN_POS_MODE_OUT);
	*/

	public final static String	[]wheelModes =  {"Position", "Velocity", "Ang. Velocity"};
	public final static int		ANG_MODE_OUT 		= 0;
	public final static int		LINEAR_MODE_OUT 	= 1;
    public final static int     LIN_POS_MODE_OUT        = 2;
	//	int	 outputMode = LIN_POS_MODE_OUT;

	CCSmartWheel(boolean init, short type, SensorProducer p){
		super(init, type, p);

		quantityNames = wheelModes;
		defQuantityName = wheelModes[2];

		activeChannels = 1;
		interfaceMode = CCInterface2.getMode(PORT_A, DIG_COUNT_MODE);

		dDesc.setNextSampleOffset(1);
		dDesc.setChannelPerSample(1);
		dDesc.setDt(0.01f);
		dDesc.setDataOffset(0);

		dEvent.setDataDescription(dDesc);
		dEvent.setNumSamples(1);
		dEvent.setData(wheelData);
		dEvent.setIntData(wheelIntData);

		//		addProperty(modeProp);

		if(init){
			calibrationDesc = new CalibrationDesc();
			calibrationDesc.addCalibrationParam(new CalibrationParam(0,radius));
		}		
	}
	
	DataListener veloListener = null;
	DataListener posListener = null;
	public DataListener setModeDataListener(DataListener l, int mode)
	{
		DataListener old = null;

		switch(mode){
		case 0:
			old = posListener;
			posListener = l;
			break;
		case 1:
			old = veloListener;
			veloListener = l;
			break;
		}
		return old;
	}

	public String getQuantityUnit(int mode)
	{
		switch(mode){
		case 0:
			return "m";
		case 1:
			return "m/s";
		}
		return null;
	}

	public int getQuantityPrecision(int mode)
	{
		switch(mode){
		case 0:
			return -4;
		case 1:
			return -2;
		}

		return DecoratedValue.UNKNOWN_PRECISION;
	}


	public int getPrecision(){ return -1; }

	// Get the default unit
	public String getUnit()
	{
		return "rad/s";
	}

	public Object getInterfaceMode()
	{
		//		outputMode = modeProp.getIndex();
		return interfaceMode;

	}

    float posOffset = 0f;
    float dt;

	float calFactor = 1f;
	float posCalFactor = 1f;
	float velCalFactor = 1f;

    public boolean startSampling(DataEvent e)
	{
		DataStreamDescription eDesc = 
			e.getDataDescription();
		dEvent.type = e.type;
		dDesc.setDt(eDesc.getDt());

		dDesc.setTuneValue(eDesc.getTuneValue());

		dDesc.setChannelPerSample(1);

		dEvent.setNumSamples(1);
		dtChannel = dDesc.getDt() / (float)dDesc.getChannelPerSample();
		posOffset = 0f;
		dt = dDesc.getDt();
		dEvent.setData(wheelData);
		dEvent.setIntData(wheelIntData);

		calFactor = -(koeff/(float)nTicks/dt);
		float tuneValue = dDesc.getTuneValue();
		posCalFactor = -(koeff/(float)nTicks) * tuneValue  * radius;
		velCalFactor = -(koeff/(float)nTicks/dt) * tuneValue * radius;

		// This will call notifyDataListenersEvent
		return super.startSampling(dEvent);
	}

	public void notifyDataListenersEvent(DataEvent e)
	{
		if(veloListener != null){
			veloListener.dataStreamEvent(e);
		}
		if(posListener != null){
			posListener.dataStreamEvent(e);
		}

		super.notifyDataListenersEvent(e);
	}
    	
	public boolean dataArrived(DataEvent e)
	{
		dEvent.type = e.type;
		DataStreamDescription eDesc = e.getDataDescription();

		int[] data = e.getIntData();
		int nOffset = eDesc.getDataOffset();
		
		int nextSampleOff = eDesc.getNextSampleOffset();
		int ndata = e.getNumSamples()*nextSampleOff;

		if(ndata < nextSampleOff) return false;

		int  	chPerSample = dDesc.getChannelPerSample();

		// note this is the time at the begining of the event
		dEvent.numSamples = e.getNumSamples();
		dEvent.setData(wheelData);

		boolean ret = true;
		if(dataListeners != null){
			for(int i = 0; i < ndata; i+=nextSampleOff){
				wheelIntData[i] = data[nOffset+i];
				wheelData[i] = (float)wheelIntData[i]*calFactor;			    
			}
			ret = super.dataArrived(dEvent);
		}

		if(veloListener != null){
			for(int i = 0; i < ndata; i+=nextSampleOff){
				wheelIntData[i] = data[nOffset+i];
				wheelData[i] = (float)wheelIntData[i]*velCalFactor;
			}
			veloListener.dataReceived(dEvent);
		}

		if(posListener != null){
			for(int i = 0; i < ndata; i+=nextSampleOff){
				wheelIntData[i] = data[nOffset+i];
				wheelData[i] = posOffset = posOffset + (float)wheelIntData[i]*posCalFactor;				
			}
			posListener.dataReceived(dEvent);
		}			

		return ret;
	}

	public void  calibrationDone(float []row1,float []row2,float []calibrated){
		if(row1 == null || calibrated == null) return;
		
		if(Maths.abs(row1[0]) < 1e-5) return;//zero
		radius = calibrated[0] / koeff / koeff / nTicks/ row1[0] / dDesc.getDt();
		if(calibrationDesc != null){
			CalibrationParam p = calibrationDesc.getCalibrationParam(0);
			if(p != null) p.setValue(radius);
		}
		
	}
	public void calibrationDescReady(){
		if(calibrationDesc == null) return;
		CalibrationParam p = calibrationDesc.getCalibrationParam(0);
		if(p == null || !p.isValid()) return;
		radius = p.getValue();
	}
}

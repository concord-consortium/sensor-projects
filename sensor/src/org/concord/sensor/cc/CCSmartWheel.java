package org.concord.sensor.cc;

import org.concord.framework.data.DecoratedValue;
import org.concord.framework.data.stream.DataStreamDescription;
import org.concord.framework.data.stream.DataStreamEvent;
import org.concord.sensor.device.CalibrationDesc;
import org.concord.sensor.device.CalibrationParam;
import org.concord.sensor.device.Sensor;
import org.concord.sensor.device.SensorDeviceMode;
import org.concord.sensor.device.SensorProducer;
import org.concord.waba.extra.util.Maths;

public class CCSmartWheel extends Sensor
	implements CCModes
{
float  			[]wheelData 	= new float[CCSensorProducer.BUF_SIZE*2];
int  			[]wheelIntData 	= new int[CCSensorProducer.BUF_SIZE*2];
int				nTicks = 660;
float				radius = 0.06f;
float				koeff = 2f*Maths.PI;

	/*
	PropObject modeProp = new PropObject("Output Mode", "Mode", PROP_MODE, wheelModes,
										 LIN_POS_MODE_OUT);
	*/

	public final static String	[]wheelModes =  {"Position", "Velocity", "Ang. Velocity"};
	public final static int MODE_LINEAR_POS = 0;
	public final static int MODE_LINEAR_VELO = 1;
	public final static int MODE_ANGULAR_VELO = 2;	
	
	private int[] outputModes;

	CCSmartWheel(boolean init, short type, SensorProducer p){
		super(init, type, p);

		quantityNames = wheelModes;
		defQuantityName = wheelModes[2];

		activeChannels = 1;
		interfaceMode = new SensorDeviceMode(PORT_A, DIG_COUNT_MODE);

		//		addProperty(modeProp);

		if(init){
			calibrationDesc = new CalibrationDesc();
			calibrationDesc.addCalibrationParam(new CalibrationParam(0,radius));
		}		
	}
	
	public void setOutputModes(int [] outputModes)
	{
		this.outputModes = outputModes;
	}
	
	public String getQuantityUnit(int mode)
	{
		switch(mode){
		case MODE_LINEAR_POS:
			return "m";
		case MODE_LINEAR_VELO:
			return "m/s";
		case MODE_ANGULAR_VELO:
		case DEFAULT_OUTPUT_MODE:
			return "rad/s";	
		}
		return null;
	}

	public int getQuantityPrecision(int mode)
	{
		switch(mode){
			case MODE_LINEAR_POS:
				return -4;
			case MODE_LINEAR_VELO:
				return -2;
			case MODE_ANGULAR_VELO:
			case DEFAULT_OUTPUT_MODE:
				return -1;				
			}
		return DecoratedValue.UNKNOWN_PRECISION;
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

    public boolean startSampling(DataStreamEvent e)
	{
		DataStreamDescription eDesc = 
			e.getDataDescription();

		posOffset = 0f;
		dt = eDesc.getDt();
		
		calFactor = -(koeff/(float)nTicks/dt);
		float tuneValue = eDesc.getChannelDescription().getTuneValue();
		posCalFactor = -(koeff/(float)nTicks) * tuneValue  * radius;
		velCalFactor = -(koeff/(float)nTicks/dt) * tuneValue * radius;

		return true;		
	}

    // This could be done as a generic multi linear transform sensor
	public int dataArrived(DataStreamEvent e, float result[],
			int resultOffset, int resultNextSampleOffset)
	{
		DataStreamDescription eDesc = e.getDataDescription();

		int[] data = e.getIntData();
		int nOffset = eDesc.getDataOffset();
		
		int nextSampleOff = eDesc.getNextSampleOffset();
		int ndata = e.getNumSamples()*nextSampleOff;

		if(ndata < nextSampleOff) return -1;

		boolean ret = true;
		for(int modeIndex = 0; modeIndex<outputModes.length; modeIndex++) {
			if(outputModes[modeIndex] == MODE_ANGULAR_VELO) {
				for(int i = 0; i < ndata; i+=nextSampleOff){
					int rawData = data[nOffset+i];
					result[resultOffset + i*resultNextSampleOffset + modeIndex] = 
						(float)rawData*calFactor;			    
				}				
			} else if(outputModes[modeIndex] == MODE_LINEAR_VELO) {
				for(int i = 0; i < ndata; i+=nextSampleOff){
					int rawData = data[nOffset+i];
					result[resultOffset + i*resultNextSampleOffset + modeIndex] = 
						(float)rawData*velCalFactor;			    
				}				
			} else if(outputModes[modeIndex] == MODE_LINEAR_POS) {
				for(int i = 0; i < ndata; i+=nextSampleOff){
					int rawData = data[nOffset+i];
					result[resultOffset + i*resultNextSampleOffset + modeIndex] = 
						(float)rawData*posCalFactor;			    
				}				
			}
		}
		
		return e.getNumSamples();
	}

	public void  calibrationDone(float []row1,float []row2,float []calibrated){
		if(row1 == null || calibrated == null) return;
		
		if(Maths.abs(row1[0]) < 1e-5) return;//zero
		// FIXME: the last value should be the dt of the data
		// I don't know where to get that dt yet.
		radius = calibrated[0] / koeff / koeff / nTicks/ row1[0] / Float.NaN;
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

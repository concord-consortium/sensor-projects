package org.concord.sensor.waba;

import org.concord.framework.data.DecoratedValue;
import org.concord.framework.data.stream.DataChannelDescription;
import org.concord.framework.data.stream.DataStreamEvent;
import org.concord.sensor.device.SensorDevice;
import org.concord.sensor.impl.SensorUnit;
import org.concord.waba.extra.io.DataStream;
import org.concord.waba.extra.util.PropContainer;
import org.concord.waba.extra.util.PropObject;

public abstract class Sensor extends PropContainer
{
	public final static int		CALIBRATION_SENSOR_START 	= 10000;
	public final static int		CALIBRATION_SENSOR_END 		= 10001;
	public final static int		PROPERTIES_SENSOR_START 	= 10002;
	public final static int		PROPERTIES_SENSOR_END 		= 10003;

	public final static int PROP_PORT = 0;
	public final static int PROP_MODE = 1;
	public final static int PROP_RANGE = 2;
	public final static int PROP_SPEED = 3;
	public final static int PROP_SAMPLING = 4;
	public final static int PROP_CHAN_NUM = 5;
	public final static int PROP_VERSION = 6;

	SensorProducer producer = null;

	public 		waba.util.Vector 	probeListeners = null;

	String		name = null;
	protected CalibrationDesc	calibrationDesc;
	public static final String defaultModeName = "Default";

	public String unit = null;

//	public DataStreamEvent	dEvent = new DataStreamEvent();
	public SensorEvent	pEvent = new SensorEvent();

	protected SensorDevice im;

	public	int interfaceType = -1; 
	protected Object interfaceMode = null;

	protected int [] channelArray = {0};
	protected int activeChannels = 1;

	protected short	type = -1;

	PropObject port = null;
	protected static String speedUnit = " per second";

	protected int precision = DecoratedValue.UNKNOWN_PRECISION;

	protected int[] outputModes = null;
	protected static final int DEFAULT_OUTPUT_MODE = -1;
	
	protected Sensor(boolean init, short type, SensorProducer p)
	{
		super("Properties");
		producer = p;
		this.type = type;
		setName(producer.getSensorName(type));
		calibrationDesc = null;
		pEvent.setProbe(this);
	}
   	
	public SensorProducer getProducer()
	{
		return producer;
	}

	public String getSummary()
	{
		String summary;

		summary = "";
		PropObject [] props = getPropArray();
		int i;
		for(i=0; i < props.length-1; i++){
			if(props[i].getId() != 	PROP_PORT){
				summary += props[i].getLabel() + "- " + props[i].getValue() + "; ";
			}
		}
		if(props[i].getId() != 	PROP_PORT){
			summary += props[i].getLabel() + "- " + props[i].getValue();
		}

		return summary;
	}

	
	/**
	 * This method should only be called by the interface manger.  
	 * you should use im.setProbe instead of this method.
	 * 
	 * This function isn't clear when the only way to add probes to an
	 * interface is to specify their port while adding them
	 */
	public void setInterface(SensorDevice im)
	{
		if(this.im != null)
		{
			/*
			 * The interface manager should probably track this so it
			 * can delete its reference to the interface manager instance.
			 *
			 * Also we need to remember to stop the im first 
			 */
			// im.setProbe(null, getInterfacePort());
		}

		this.im = im;
		if(im != null)
		{
			// Add probe might want to set the interface mode of this probe
			// im.addProbe(this);
		}
	}


	public SensorDevice getInterface()
	{
		return im;
	}

	public SensorDevice open()
	{
		if(im != null) return im;

		/*
		 * This could lead to problems is more than one probe
		 * is used at a time.  The getInterface manager should
		 * keep track of interfaces so we don't make two of them
		 * but we then have to "dispose" the interface from
		 * the manager so it doesn't keep extra references around
		 *
		 */
		im = SensorFactory.getInterface(getInterfaceType());
		if(im == null){
			// we've got an invalid or unloaded interface.
			return null;
		}

		setInterface(im);

		return im;
	}

	public void close()
	{
		if(im == null) return;
		
		setInterface(null);
	}
	
	public int 	getInterfaceType(){return interfaceType;}
	public void setInterfaceType(int interfaceType){this.interfaceType =  interfaceType;}
	
	public void setPortProperty(PropObject port)
	{
		this.port = port;
		addProperty(port);
	}

	public int 	getInterfacePort()
	{
		if(port != null){
			return port.getIndex();
		} else {
			return 0;
		}
	}
	public void setInterfacePort(int interfacePort)
	{
		if(port != null){
			port.setIndex(interfacePort);
		}
	}

	public boolean needCalibration(){return ((calibrationDesc != null) && (calibrationDesc.countAvailableParams() > 0));}
	public CalibrationDesc getCalibrationDesc(){return calibrationDesc;}
	public void setCalibrationDesc(CalibrationDesc calibrationDesc){this.calibrationDesc = calibrationDesc;}

	public int [] getChannels(){return channelArray;}

	public int	getActiveChannels()
	{
		if(channelArray == null) return activeChannels;
		return channelArray.length;
	}

	public int	getActiveCalibrationChannels(){return getActiveChannels();}

	public int getGlobalId()
	{
		// watch out for negative numbers here 
		return ((int)producer.getId()) << 16 | ((int)type);
	}

	public short getType()
	{
		return type;	
	}
	
	public void addProbeListener(SensorListener l){
		if(probeListeners == null) probeListeners = new waba.util.Vector();
		if(probeListeners.find(l) < 0) probeListeners.add(l);
	}
	public void removeProbeListener(SensorListener l){
		if(probeListeners == null) return;
		int index = probeListeners.find(l);
		if(index >= 0) probeListeners.del(index);
	}
	public void notifyProbeListeners(SensorEvent e){
		if(probeListeners == null) return;
		for(int i = 0; i < probeListeners.getCount(); i++){
			SensorListener l = (SensorListener)probeListeners.get(i);
			l.probeChanged(e);
		}
	}
	
	public void setOutputModes(int [] outputModes)
	{
		this.outputModes = outputModes;
	}
		
	/**
	 * This description object can change if the mode of the sensor changes
	 * currently it is up to the SensorDevice to do this updating correctly.
	 * 
	 * @param index
	 * @return
	 */
	public DataChannelDescription getChannelDescription(int index)
	{
		int mode = DEFAULT_OUTPUT_MODE;
		if(outputModes != null) {
			// if the index is outside the range of the array then this
			// will throw an out of bounds exception
			mode = outputModes[index];			
		}
		
		DataChannelDescription channelDescription = new DataChannelDescription();		
		channelDescription.setName(getQuantityName(mode));
		channelDescription.setPrecision(getQuantityPrecision(mode));
		channelDescription.setUnit(new SensorUnit(getQuantityUnit(mode)));

		return channelDescription;
	}
	
	
	/**
	 * This method is called before the data starts coming into the
	 * the sensor from the interface
	 * The description describes the data that will be coming in
	 *  
	 * The sensor has a chance to return false which will cancel the
	 * start.
	 * 
	 * @param e
	 * @return
	 */
	public boolean startSampling(DataStreamEvent event)
	{
		return true;
	}

	/**
	 * This notifies the sensor that data has stopped coming in
	 * @return
	 */
	public boolean stopSampling(DataStreamEvent event)
	{
		return true;
	}

	/**
	 * Sensor implementations will override this method to handle
	 * the data coming in from the interface, convert it, and
	 * store it into the result array. 
	 * 
	 * The sensor return the max row idex it changed in the result data
	 * 0 means no rows were changed
	 * -1 means there was an error.
	 * 
	 * @param e
	 * @return the max(changed_row_index) + 1
	 *    0 means no rows have changed
	 *   -1 means there was an error 
	 */
	public abstract int dataArrived(DataStreamEvent inputData, float [] result,
			int resultOffset, int resultNextSampleOffset);
   	
	/**
	 * This is a method used by sensors that know about their interface
	 * @return
	 */
	public abstract Object getInterfaceMode();


	public void setName(String name){this.name = name;}
	public String getName(){return name;}
	
	public void apply()
	{
		super.apply();

		pEvent.setInfo(this);
		notifyProbeListeners(pEvent);
	}

	public void  calibrationDone(float []row1,float []row2,float []calibrated){}
	
	public void writeExternal(DataStream out){
		out.writeInt(interfaceType);
		out.writeInt(CALIBRATION_SENSOR_START);
		out.writeBoolean(calibrationDesc != null);
		if(calibrationDesc != null){
			calibrationDesc.writeExternal(out);
		}
		out.writeInt(CALIBRATION_SENSOR_END);
		out.writeInt(PROPERTIES_SENSOR_START);
		super.writeExternal(out);
		out.writeInt(PROPERTIES_SENSOR_END);
		writeInternal(out);
	}

	protected void writeInternal(DataStream out){
	}
	protected void readInternal(DataStream in){
	}
	
	public void readExternal(DataStream in){
		interfaceType = in.readInt();
		int temp = in.readInt();
		if(temp != CALIBRATION_SENSOR_START) return;
		if(in.readBoolean()){
			if(calibrationDesc == null) calibrationDesc = new CalibrationDesc();
			calibrationDesc.readExternal(in);
			calibrationDescReady();
		}
		in.readInt();//CALIBRATION_PROB_END
		temp = in.readInt();
		if(temp != PROPERTIES_SENSOR_START) return;	
		super.readExternal(in);
		temp = in.readInt();//PROPERTIES_PROB_END
		readInternal(in);
	}
	public void calibrationDescReady(){}

	protected String [] quantityNames = null;
	protected String defQuantityName;
	public String [] getQuantityNames()
	{
		String [] retNames = quantityNames;
		if(retNames == null){
			retNames = new String [1];
			retNames[0] = defQuantityName;
		}
		return retNames;
	}	

	public String getDefQuantityName()
	{
		return defQuantityName;
	}

	public int getQuantityId(String quantityName)
	{
		if(quantityNames == null){
			return 0;
		} else {
			for(int i=0; i<quantityNames.length; i++){
				if(quantityNames[i].equals(quantityName)){
					return i;
				}
			}
			return -1;
		}
	}	

	public String getQuantityUnit(int id){
		if(id == DEFAULT_OUTPUT_MODE) {
			return unit;
		}
		
		return null;
	}

	public int getQuantityPrecision(int id)
	{
		if(id == DEFAULT_OUTPUT_MODE) {
			return precision;
		}
		
		return DecoratedValue.UNKNOWN_PRECISION; 
	}

	public String getQuantityName(int id)
	{
		if(id == DEFAULT_OUTPUT_MODE) {
			return defQuantityName;
		}

		if(quantityNames == null){
			return null;
		}
		if(id >= 0 && id < quantityNames.length){
			return quantityNames[id];
		}
		return null;
	}

}

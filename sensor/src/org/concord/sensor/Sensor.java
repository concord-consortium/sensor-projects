package org.concord.sensor;

import org.concord.waba.extra.io.*;
import org.concord.waba.extra.util.*;

import org.concord.framework.data.*;
import org.concord.framework.data.stream.*;

public abstract class Sensor extends PropContainer
	implements DataProducer
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

	public 		waba.util.Vector 	dataListeners = null;
	public 		waba.util.Vector 	probeListeners = null;

	String		name = null;
	protected CalibrationDesc	calibrationDesc;
	public static final String defaultModeName = "Default";

	public String unit = null;

	public DataStreamDescription dDesc = new DataStreamDescription();
	public DataStreamEvent	dEvent = new DataStreamEvent();
	public SensorEvent	pEvent = new SensorEvent();

	protected InterfaceManager im;

	public	int interfaceType = -1; 
	protected Object interfaceMode = null;

	protected int [] channelArray = {0};
	protected int activeChannels = 1;

	protected short	type = -1;

	PropObject port = null;
	protected static String speedUnit = " per second";

	protected int precision = DecoratedValue.UNKNOWN_PRECISION;

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
	public void setInterface(InterfaceManager im)
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


	public InterfaceManager getInterface()
	{
		return im;
	}

	public InterfaceManager open()
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

	public boolean startSensor()
	{
		boolean synced = im.syncInterfaceWithSensor(this);

		if(!synced) return false;

		im.start(this);

		return true;
	}
	
	public void start()
	{
		startSensor();
	}
	
	/**
	 *  This doesn't really need to do anything if
	 * the sensor isn't storing any cache.
	 */
	public void reset()
	{
		
	}
	
	public void stop()
	{
		im.stop(this);
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
	
	public void addDataListener(DataListener l){
		if(dataListeners == null){ dataListeners = new waba.util.Vector();	   }
		if(dataListeners.find(l) < 0){
			dataListeners.add(l);
		}
	}
	public void removeDataListener(DataListener l){
		if(dataListeners == null) return;
		int index = dataListeners.find(l);
		if(index >= 0) dataListeners.del(index);
		if(dataListeners.getCount() == 0) dataListeners = null;
	}

	public DataListener setModeDataListener(DataListener l, int mode){return null;}

	public void notifyDataListenersEvent(DataStreamEvent e){
		if(dataListeners == null) return;
		for(int i = 0; i < dataListeners.getCount(); i++){
			DataListener l = (DataListener)dataListeners.get(i);
			l.dataStreamEvent(e);
		}
	}

	public void notifyDataListenersReceived(DataStreamEvent e)
	{
		if(dataListeners == null) return;
		for(int i = 0; i < dataListeners.getCount(); i++){
			DataListener l = (DataListener)dataListeners.get(i);
			l.dataReceived(e);
		}
	}

	public boolean startSampling(DataStreamEvent e)
	{
		e.setType(DataStreamEvent.DATA_READY_TO_START);

		notifyDataListenersEvent(e);

		e.setType(DataStreamEvent.DATA_RECEIVED);

		return true;
	}

	public boolean stopSampling(DataStreamEvent e)
	{
		e.setType(DataStreamEvent.DATA_STOPPED);

		notifyDataListenersEvent(e);

		e.setType(DataStreamEvent.DATA_RECEIVED);

		return true;
	}

	public boolean dataArrived(DataStreamEvent e)
	{
		notifyDataListenersReceived(e);
		return true;
	}

	public boolean idle(DataStreamEvent e)
	{
		e.setType(DataStreamEvent.DATA_COLLECTING);
		notifyDataListenersEvent(e);
		e.setType(DataStreamEvent.DATA_RECEIVED);
		return true;
	}
   	

	public abstract Object getInterfaceMode();


    public DataStreamDescription getDataDescription()
    {
		return dDesc;
    }

	public void setName(String name){this.name = name;}
	public String getName(){return name;}
	
	public void apply()
	{
		super.apply();

		pEvent.setInfo(this);
		notifyProbeListeners(pEvent);
	}

	public void  calibrationDone(float []row1,float []row2,float []calibrated){}
	
	public String getUnit()
	{
		return unit;
	}
	public boolean setUnit(String unit)
	{
		this.unit = unit;
		return true;
	}

	public int getPrecision(){return precision; }

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

	public String getQuantityUnit(int id){return null;}

	public int getQuantityPrecision(int id)
	{
		return DecoratedValue.UNKNOWN_PRECISION; 
	}

	public String getQuantityName(int id)
	{
		if(id == 0 && quantityNames == null){
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

package org.concord.sensor.device;


public class SensorEvent
{
	public static final int PROBE_PROPERTY_CHANGED 		= 3000;
	Object info = null;
	int type;
	Sensor target;

	public SensorEvent(){
		this(null,PROBE_PROPERTY_CHANGED,null);
	}
	public SensorEvent(Sensor  target, int type, Object info){
		this.type = type;
		this.target = target;
		this.info = info;
	}
	
	public void setType(int type){this.type = type;}
	public void setInfo(Object info){this.info = info;}
	public void setProbe(Sensor target){this.target = target;}
	
	public int getType(){return type;}
	public Object getInfo(){return info;}
	public Sensor getProbe(){return (Sensor)target;}
	
	
	public String toString(){
		return ("Type: "+type+"; Probe: "+target+"; Info: "+info);
	}
}

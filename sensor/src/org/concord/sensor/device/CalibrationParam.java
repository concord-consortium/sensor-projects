package org.concord.sensor.device;

import org.concord.waba.extra.io.DataStream;

public class CalibrationParam
{
	boolean   valid = false;
	float      value = 0.0f;
	float     defaultValue = 1.0f;
	int		index = 0;
	boolean   available = true;
	public CalibrationParam(){
		this(0,1.0f);
		valid = false;
	}
	public CalibrationParam(int index){
		this(index,1.0f);
	}
	public CalibrationParam(DataStream in){
		valid = true;
		available = true;
		readExternal(in);
	}
	public CalibrationParam(int index,float defaultValue){
		this.index = index;
		this.defaultValue = defaultValue;
		setValueToDefault();
	}
	public int getIndex(){return index;}
	
	public void  clear(){
		valid = false;
		value = 0.0f;
	}
	
	public boolean isValid(){return valid;}
	public boolean isAvailable(){return available;}
	public void setAvailable(boolean available){this.available =  available;}
	
	public float getValue(){return value;}
	public void setValue(float value){
		this.value = value;
		valid = true;
	}
	public void setValueToDefault(){
		setValue(defaultValue);
	}
	public void writeExternal(DataStream out){
		out.writeInt(index);
		out.writeFloat(value);
		out.writeFloat(defaultValue);
	}
	public void readExternal(DataStream in){
		this.index 		= in.readInt();
		this.value 		= in.readFloat();
		this.defaultValue 	= in.readFloat();
	}
	
	
}

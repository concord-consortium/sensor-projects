package org.concord.sensor.device;

import org.concord.waba.extra.io.DataStream;

public class CalibrationDesc
{
	waba.util.Vector params = null;
	public CalibrationDesc(){
		params = new waba.util.Vector();
	}
	
	public int countTotalParams(){
		return params.getCount();
	}
	
	public int countAvailableParams(){
		int retValue = 0;
		for(int i = 0; i < countTotalParams(); i++){
			CalibrationParam cp = (CalibrationParam)params.get(i);
			if(cp == null) continue;
			if(cp.isAvailable()) retValue++;
		}
		return retValue;
	}
	
	public CalibrationParam getCalibrationParam(int index){
		if(countTotalParams() < 1) return null;
		for(int i = 0; i < countTotalParams(); i++){
			CalibrationParam cp = (CalibrationParam)params.get(i);
			if(cp == null) continue;
			if(cp.getIndex() == index){
				return cp;
			}
		}
		return null;
	}
	
	public void addCalibrationParam(CalibrationParam cp){
		params.add(cp);
	}
	public void writeExternal(DataStream out){
		out.writeInt(countTotalParams());
		for(int i = 0; i < countTotalParams(); i++){
			CalibrationParam cp = (CalibrationParam)params.get(i);
			out.writeBoolean(cp != null);
			if(cp == null) continue;
			cp.writeExternal(out);
		}
	}
	public void readExternal(DataStream in){
		int nParam = in.readInt();
		if(nParam < 1) return;
		params = new waba.util.Vector();
		for(int i = 0; i < nParam; i++){
			if(!in.readBoolean()) continue;
			addCalibrationParam(new CalibrationParam(in));
		}
	}
}

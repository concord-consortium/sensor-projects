package org.concord.sensor.pasco;

import org.concord.sensor.SensorConfig;
import org.concord.sensor.device.impl.SensorConfigImpl;
import org.concord.sensor.impl.SensorUnit;
import org.concord.sensor.pasco.datasheet.PasportSensorMeasurement;
import org.concord.sensor.pasco.jna.PascoChannel;

public class PasportSensor extends SensorConfigImpl {
	private PasportSensorMeasurement measurement;
	private PascoChannel channel;

	public PasportSensor(PasportSensorMeasurement measurement) {
		this(null, measurement);
	}
	
	public PasportSensor(PascoChannel channel, PasportSensorMeasurement measurement) {
		this.channel = channel;
		this.measurement = measurement;
		////////////////////////
	    // configure the Sensor settings
		////////////////////////
		
		setPort(measurement.getId());
		setName(measurement.getName());
		setStepSize(measurement.getAccuracy());
		SensorUnit unit = new SensorUnit(measurement.getUnitStr());
		setUnit(unit);
		setConfirmed(true);

		String name = measurement.getName();
		String unitStr = measurement.getUnitStr();
		
		// this is the hard part we have to figure out what type
		// of sensor this is. It seems the only way is to check
		// the name
		if((name.indexOf("Temperature") >= 0) && 
				(unitStr.indexOf("C") > 0)) {
			if(name.indexOf("Probe") >= 0) {
				setType(SensorConfig.QUANTITY_TEMPERATURE_WAND);				
			} else {
				setType(SensorConfig.QUANTITY_TEMPERATURE);	
			} 
		} else if(name.indexOf("Position") >= 0) {
			setType(SensorConfig.QUANTITY_DISTANCE);
		} else if(name.indexOf("Light") >= 0) {
			setType(SensorConfig.QUANTITY_LIGHT);			
		} else if(name.indexOf("Humidity") >=0) {
			setType(SensorConfig.QUANTITY_RELATIVE_HUMIDITY); 				
		} else if((name.indexOf("Force") >= 0) &&
				(unitStr.indexOf("N") >= 0)){
			// need to choose the reversed measurement
			setType(SensorConfig.QUANTITY_FORCE);
		} else if((name.indexOf("Voltage") >= 0) &&
				(unitStr.indexOf("V") >= 0)) {
			setType(SensorConfig.QUANTITY_VOLTAGE);
		} else if((name.indexOf("Current") >= 0) &&
				(unitStr.indexOf("A") >= 0)) {
			setType(SensorConfig.QUANTITY_CURRENT);
		} else if((name.indexOf("Pressure") >=0) &&
				(unitStr.indexOf("kPa") >= 0)){
			setType(SensorConfig.QUANTITY_GAS_PRESSURE);
		} else if(name.indexOf("Sound") >=0){
			setType(SensorConfig.QUANTITY_SOUND_INTENSITY);
		} else {
			setType(SensorConfig.QUANTITY_UNKNOWN);
		}

	}
	
	public PasportSensorMeasurement getMeasurement() {
		return measurement;
	}

	public PascoChannel getChannel() {
		return channel;
	}

}

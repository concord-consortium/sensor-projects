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
		} else if(measurement.getDataSheet().getName().indexOf("EKG") >= 0 &&
				name.indexOf("Voltage") >= 0){
			// So far we are only handling EKG by returning voltage
			// this has to go above the generic voltage other wise it won't match
			setType(SensorConfig.QUANTITY_EKG);
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
		} else if(name.equals("Magnetic Field") && 
				unitStr.indexOf("mT") >= 0){
			setType(SensorConfig.QUANTITY_MAGNETIC_FIELD);
		} else if(name.equals("pH")){
			setType(SensorConfig.QUANTITY_PH);
		} else if(name.indexOf("Conductivity") >= 0){
			// FIXME this will result in two conductivity sensors one for each calibration but really there is only one
			// so if an experiment called for two it would think this sensor is good enough.
			// that happens because the conductivity sensor returns multiple measurements that have Conductivity in the 
			// string.
			// A better solution for this would be to expand the experiment config stuff to have a concept of conversions
			// so a single SensorConfig could have multiple conversions, then this code will have to be smarter about
			// which "raw" measurement is the root input for each non raw measurement.
			setType(SensorConfig.QUANTITY_CONDUCTIVITY);
		} else if(name.indexOf("Salinity") >= 0){
			setType(SensorConfig.QUANTITY_SALINITY);
		} else if(name.indexOf("CO2") >= 0){
			setType(SensorConfig.QUANTITY_CO2_GAS);
		} else if(measurement.getDataSheet().getName().indexOf("UVA") >= 0 &&
				name.indexOf("Intensity") >= 0){
			setType(SensorConfig.QUANTITY_UVA_INTENSITY);
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

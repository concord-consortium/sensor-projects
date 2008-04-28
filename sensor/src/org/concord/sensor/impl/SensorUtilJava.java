package org.concord.sensor.impl;

import java.io.PrintStream;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;

import org.concord.sensor.ExperimentConfig;
import org.concord.sensor.SensorConfig;

/**
 * SensorUtilJava <br>
 * A class to house helper methods for the Java implementations and users of the sensor library.
 * They are "Java" because the library works in waba too, and the methods below use Java specific
 * things like reflection and System.out
 * 
 * <p>
 * Date created: Apr 28, 2008
 * 
 * @author scytacki<p>
 *
 */
public class SensorUtilJava {

	public static String getTypeConstantName(int type) {
		Field[] declaredFields = SensorConfig.class.getDeclaredFields();
		for(int i=0; i<declaredFields.length; i++){
			Field field = declaredFields[i];
			
			// make sure it is static
			int mod = field.getModifiers();
			if(!Modifier.isStatic(mod)){
				continue;
			}
			
			// make sure the name is correct
			String name = field.getName();
			if(!name.startsWith("QUANTITY_")){
				continue;
			}
		
			int fieldValue;
			try {
				fieldValue = field.getInt(null);
				if(fieldValue == type){
					return name;
				}
			} catch (IllegalArgumentException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		return null;
	}

	public static void printExperimentConfig(ExperimentConfig currentConfig) {
		PrintStream out = System.out;
		
		out.println("ExperimentConfig");
		out.println("  deviceName " + currentConfig.getDeviceName());
		out.println("  dataReadPeriod " + currentConfig.getDataReadPeriod());
		out.println("  exactPeriod " + currentConfig.getExactPeriod());
		out.println("  invalid " + currentConfig.isValid());
		out.println("  invalidReason " + currentConfig.getInvalidReason());
		
		SensorConfig[] sensors = currentConfig.getSensorConfigs();
		for (int i=0; i<sensors.length; i++) {
			SensorConfig sensor = sensors[i];
			out.println("  SensorConfig");
			out.println("    name " + sensor.getName());
			out.println("    type " + sensor.getType());
			out.println("    typeConstant " + getTypeConstantName(sensor.getType()));
			out.println("    port " + sensor.getPort());
			out.println("    portName " + sensor.getPortName());
			out.println("    stepSize " + sensor.getStepSize());
			out.println("    confirmed " + sensor.isConfirmed());
			out.println("    unit " + sensor.getUnit().getDimension());
		}	
	}

}

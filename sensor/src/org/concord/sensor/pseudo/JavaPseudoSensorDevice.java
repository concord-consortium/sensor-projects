package org.concord.sensor.pseudo;

public class JavaPseudoSensorDevice extends AbstractPseudoSensorDevice
{

	protected boolean isValidFloat(float val)
	{
		return !Float.isNaN(val);
	}

}

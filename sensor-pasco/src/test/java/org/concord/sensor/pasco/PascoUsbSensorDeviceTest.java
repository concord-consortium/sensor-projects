package org.concord.sensor.pasco;

import org.concord.sensor.device.SensorDeviceTest;

public class PascoUsbSensorDeviceTest extends SensorDeviceTest {

	@Override
	public void setup() {
		device = new PascoUsbSensorDevice();
	}

	@Override
	protected boolean supportsAttachingSingleTemperatureSensor() {
		return false;
	}
	
	@Override
	protected boolean supportsRawValueSensors() {
		return false;
	}
}

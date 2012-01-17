package org.concord.sensor.pasco;

import org.concord.sensor.device.ExampleSensorApp;
import org.concord.sensor.device.impl.DeviceID;

public class PascoUsbSensorExampleApp extends ExampleSensorApp {

	@Override
	public void setup() {
		deviceId = DeviceID.PASCO_USB;
	}

	public static void main(String[] args) {
		PascoUsbSensorExampleApp app = new PascoUsbSensorExampleApp();
		app.testAllConnectedProbes();
	}	
}

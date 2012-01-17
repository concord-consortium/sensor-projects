package org.concord.sensor.pseudo;

import org.concord.sensor.device.ExampleSensorApp;
import org.concord.sensor.device.impl.DeviceID;

public class PseudoSensorExampleApp extends ExampleSensorApp {

	@Override
	public void setup() {
		deviceId = DeviceID.PSEUDO_DEVICE;
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		PseudoSensorExampleApp app = new PseudoSensorExampleApp();
		app.testAllConnectedProbes();
	}

}

package org.concord.sensor.pseudo;

import org.concord.sensor.device.SensorDeviceTest;
import org.concord.sensor.device.impl.DeviceID;
import org.junit.Before;

public class PseudoSensorDeviceTest extends SensorDeviceTest {
	@Before
	public void setup(){
		device = new PseudoSensorDevice();
		deviceId = DeviceID.PSEUDO_DEVICE;
	}

}

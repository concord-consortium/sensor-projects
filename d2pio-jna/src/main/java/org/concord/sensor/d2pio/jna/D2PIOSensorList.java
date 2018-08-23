package org.concord.sensor.d2pio.jna;

import com.sun.jna.Memory;
import com.sun.jna.Pointer;
import com.sun.jna.ptr.IntByReference;
import java.nio.charset.Charset;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;

/**
 * In the D2PIO SDK a device list provides access to individual sensor devices.
 * 
 * @author kswenson
 *
 */
public class D2PIOSensorList {
	protected D2PIOJNALibrary libInstance;
	protected Pointer libHandle;
	protected Pointer deviceList;
	protected int deviceCount;

	D2PIOSensorList(D2PIOJNALibrary iLibInstance, Pointer iLibHandle) {
		libInstance = iLibInstance;
		libHandle = iLibHandle;

		int deviceType = D2PIOJNALibrary.D2PIO_DEVTYPE_GENERIC;
		int transportType = D2PIOJNALibrary.D2PIO_COMM_TRANSPORT_USB;
		IntByReference pDeviceListSignature = new IntByReference();
		int result = libInstance.D2PIO_SearchForDevices(
									libHandle,
									deviceType,
									transportType,
									null,
									pDeviceListSignature);
		if (result != 0) return;

		IntByReference pNumDevices = new IntByReference();
		deviceList = libInstance.D2PIO_OpenDeviceListSnapshot(
									libHandle,
									deviceType,
									transportType,
									pNumDevices,
									pDeviceListSignature);
		if (deviceList != null)
			deviceCount = pNumDevices.getValue();
	}

	public void close() {
		if (deviceList != null) {
			libInstance.D2PIO_CloseDeviceListSnapshot(deviceList);
			deviceList = null;
		}
	}

	protected void finalize() {
		close();
	}
	
	public int getCount() {
		return deviceCount;
	}

	public D2PIOSensorSpec getSensor(int index) {
		int MAX_NAME_SIZE = D2PIOJNALibrary.D2PIO_MAX_SIZE_DEVICE_NAME;
		Pointer pDeviceName = new Memory(MAX_NAME_SIZE);
		Pointer pFriendlyName = new Memory(MAX_NAME_SIZE);
		IntByReference pRSSI_level = new IntByReference();
		int result = libInstance.D2PIO_DeviceListSnapshot_GetNthEntry(
									deviceList, index,
									pDeviceName, MAX_NAME_SIZE,
									pFriendlyName, MAX_NAME_SIZE,
									pRSSI_level);
		if (result == 0) {
			String deviceName = pDeviceName.getString(0, "UTF-8");
			String friendlyName = pFriendlyName.getString(0, "UTF-8");
			System.out.println("Device name: " + deviceName);
			System.out.println("Friendly name: " + friendlyName);
			return new D2PIOSensorSpec(libInstance, libHandle, deviceName, friendlyName);
		}
		return null;
	}
}

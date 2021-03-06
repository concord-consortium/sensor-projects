package org.concord.sensor.vernier.d2pio;

import org.concord.sensor.ExperimentConfig;
import org.concord.sensor.ExperimentRequest;
import org.concord.sensor.SensorConfig;
import org.concord.sensor.device.DeviceReader;
import org.concord.sensor.device.impl.AbstractSensorDevice;
import org.concord.sensor.device.impl.SerialPortParams;
import org.concord.sensor.d2pio.jna.D2PIOLibrary;
import org.concord.sensor.d2pio.jna.D2PIOSensor;
import org.concord.sensor.impl.ExperimentConfigImpl;
import org.concord.sensor.vernier.VernierSensor;
import org.concord.sensor.vernier.VernierSensorDevice;
import org.concord.sensor.impl.Range;
import javax.swing.JOptionPane;

public class D2PIOSensorDevice extends AbstractSensorDevice implements
		VernierSensorDevice
{
	D2PIOLibrary d2pio;
	String errorMessage;
	D2PIOSensor currentGoDirectDevice;
	private static final int MAX_D2PIO_CHANNELS = 32;

	public D2PIOSensorDevice() {
		deviceLabel = "GDX";
		d2pio = new D2PIOLibrary();

		try {
			d2pio.initLibrary();
		} catch (Throwable t) {
			errorMessage = "Can't load d2pio native library";
			d2pio = null;
			t.printStackTrace();
		}
	}

	@Override
	public void log(String message) {
		super.log(message);
	}

	@Override
	protected SerialPortParams getSerialPortParams() {
		return null;
	}

	@Override
	protected boolean initializeOpenPort(String portName) {
		return false;
	}

	/**
	 * Because we are using a api that abstracts the usb and/or serial port connection
	 * we override this to initialize the api instead of opening the port.
	 *
	 *
	 * @see org.concord.sensor.device.impl.AbstractSensorDevice#openPort()
	 */
	@Override
	public void open(String portParams) {
		if(d2pio == null){
			return;
		}

		if(!d2pio.init()) {
			errorMessage = "Can't init d2pio, You have another program using the Go Direct device\n";
		}
	}

	/**
	 * We don't have a port, but openPort is called by the auto configure code.
	 *
	 * @see org.concord.sensor.device.impl.AbstractSensorDevice#openPort()
	 */
	protected boolean openPort()
	{
		return d2pio != null;
	}

	public void close()
	{
		// already closed
		if(d2pio == null){
			return;
		}

		if(currentGoDirectDevice != null){
			currentGoDirectDevice.close();
			currentGoDirectDevice = null;
		}

		d2pio.uninit();
		d2pio = null;
	}


	public boolean canDetectSensors() {
		return true;
	}

	@Override
	protected boolean hasNonAutoIdSensors() {
		return false;
	}

	public ExperimentConfig configure(ExperimentRequest request) {
		ExperimentConfig experimentConfig = autoIdConfigure(request);

		// Because the supported measurement period by the device
		// might be different than the requested period the measurement period is set
		// on the device here
		currentGoDirectDevice.setMeasurementPeriod(experimentConfig.getPeriod());
		((ExperimentConfigImpl)experimentConfig).setPeriod((float) currentGoDirectDevice.getMeasurementPeriod());

		return experimentConfig;
	}

	public ExperimentConfig getCurrentConfig() {
		if(currentGoDirectDevice != null){
			currentGoDirectDevice.close();
			currentGoDirectDevice = null;
		}

		currentGoDirectDevice = openGoDirectDevice();

		if(currentGoDirectDevice == null) {
			// Currently the only way to indicate errors in loading the library or opening the device
			// is to return null here.  In that case getErrorMessage is called.
			return null;
		}
		ExperimentConfigImpl expConfig = new ExperimentConfigImpl();

		expConfig.setDeviceName(currentGoDirectDevice.getDeviceLabel());

		expConfig.setExactPeriod(true);
		expConfig.setPeriod((float)currentGoDirectDevice.getMeasurementPeriod());
		expConfig.setDataReadPeriod(expConfig.getPeriod());

		// TODO we should set the period range since it is known by the device
		//   expConfig.getPeriodRange();

		int numChannels = 0;
		int channelMask = currentGoDirectDevice.getMeasurementChannelAvailabilityMask();

		// Use channel mask to count number of channels
		for (int ch = 0; ch < MAX_D2PIO_CHANNELS; ch++) {
			if (((1 << ch) & channelMask) != 0) {
				numChannels++;
			}
		}

		SensorConfig [] sensorConfigs = new SensorConfig[numChannels];
		expConfig.setSensorConfigs(sensorConfigs);
		int chIndex = 0;
		for (int ch = 0; ch < MAX_D2PIO_CHANNELS; ch++) {
			if (((1 << ch) & channelMask) != 0) {
				// TODO: what should the channel type be?
				// Is it always CHANNEL_TYPE_ANALOG or is it ever CHANNEL_TYPE_DIGITAL?
				int channelType = VernierSensor.CHANNEL_TYPE_ANALOG;
				VernierSensor sensor = new VernierSensor(this, devService, chIndex, channelType);

				// FIXME if the sensor isn't a known sensor then sensorConfig should return
				// an unknown type of sensor.  It isn't clear if this should return a valid experiment config or not.
				int sensorid = currentGoDirectDevice.getAttachedSensorId(ch);

				sensor.setupSensor(currentGoDirectDevice.getAttachedSensorId(ch), null);
				// If we fail to find the sensor in our list of known sensors,
				// read the channels specifics from sensor itself
				if (sensor.getType() == VernierSensor.QUANTITY_UNKNOWN) {
					String sensorUnits = currentGoDirectDevice.getMeasurementChannelSensorUnits(ch);
					sensor.setUnit(sensorUnits);
					String sensorName = currentGoDirectDevice.getMeasurementChannelSensorDescription(ch);
					sensor.setName(sensorName);
					double sensorTypicalStepSize = currentGoDirectDevice.getMeasurementChannelTypicalStepSize(ch);
					sensor.setStepSize((float)sensorTypicalStepSize);
					double sensorMinValue = currentGoDirectDevice.getMeasurementChannelMinValue(ch);
					double sensorMaxValue = currentGoDirectDevice.getMeasurementChannelMaxValue(ch);
					sensor.setValueRange(new Range((float)sensorMinValue,(float)sensorMaxValue));
				}

				sensorConfigs[chIndex] = sensor;
				chIndex++;
			}
		}

		expConfig.setValid(true);

		return expConfig;
	}

	protected D2PIOSensor openGoDirectDevice() {
		if(d2pio == null){
			return null;
		}
    boolean isDeviceAttached = d2pio.isSensorAttached(); //obsolete?

		D2PIOSensor gSensor = d2pio.getFirstSensor();

		if(gSensor == null){
			// Set the error message here because returning null here
			// ought to trigger an error printout, however if isAttached was called
			// first then this shouldn't happen because that ought to return false
			// in this case
			errorMessage = "Cannot find an attached Go Direct device";
			return null;
		}

		// This will lock the device to this thread
		// In the sensor-native code we then unlock it.  It isn't clear why,
		// perhaps so any thread can access it.  A more safe approach would be to use
		// the a single thread delegator to force all access on one thread.
		// or to synchronize the access to the gSensor and have it lock it and unlock it
		gSensor.open();
		return gSensor;
	}

	public String getErrorMessage(int error) {
		if(errorMessage == null){
			return "Unknown Error";
		}

		return errorMessage;
	}

	public String getVendorName() {
		return "Vernier";
	}

	public String getDeviceName() {
		if(currentGoDirectDevice != null){
			return currentGoDirectDevice.getDeviceLabel();
		}

		return "Go Direct";
	}

	public boolean start() {
		// -1, clear all channels
		byte channel = -1;
		currentGoDirectDevice.clearIO(channel);

		currentGoDirectDevice.startMeasurements();
		return true;
	}

	public void stop(boolean wasRunning) {
		currentGoDirectDevice.stopMeasurements();
	}

	@Override
	public boolean isAttached() {
		if(d2pio == null){
			return false;
		}
		return d2pio.isSensorAttached();
	}

	@Override
	protected SensorConfig createSensorConfig(int type, int requestPort)
	{
		VernierSensor config =
			new VernierSensor(this, devService, 0,
					VernierSensor.CHANNEL_TYPE_ANALOG);
    	config.setType(type);
    	return config;
	}

	public int read(float[] values, int offset, int nextSampleOffset,
			DeviceReader reader) {
		int sensorIndex = 0;
		int numMeasurements = 0;
		int channelMask = currentGoDirectDevice.getMeasurementChannelAvailabilityMask();
		for (int ch = 0; ch < MAX_D2PIO_CHANNELS; ch++) {
			if (((1 << ch) & channelMask) != 0) {
				int numericType = currentGoDirectDevice.getMeasurementChannelNumericType(ch);
				if (!currentGoDirectDevice.measurementIsRaw(numericType)) {
					double[] calbMeasurements = currentGoDirectDevice.readMeasurements(ch, 200);
					int numCalbMeasurements = calbMeasurements != null ? calbMeasurements.length: 0;
					if (numCalbMeasurements > 0) {
						if (numMeasurements == 0 || numCalbMeasurements < numMeasurements) {
							numMeasurements = numCalbMeasurements;
						}
						for (int i = 0; i < numCalbMeasurements; i++) {
							float calibratedData = Float.NaN;
							calibratedData = (float)calbMeasurements[i];
							values[offset + sensorIndex + i * nextSampleOffset] = calibratedData;
						}
					}
				} else {
					int[] rawMeasurements = currentGoDirectDevice.readRawMeasurements(ch, 200);
					int numRawMeasurements = rawMeasurements != null ? rawMeasurements.length: 0;
					if (numRawMeasurements > 0) {
						if (numMeasurements == 0 || numRawMeasurements < numMeasurements) {
							numMeasurements = numRawMeasurements;
						}
						for (int i = 0; i < numRawMeasurements; i++) {
							float rawData = Float.NaN;
							rawData = (float)rawMeasurements[i];
							values[offset + sensorIndex + i * nextSampleOffset] = (float)rawData;
						}
					}
				}
				sensorIndex++;
			}
		}
		return numMeasurements;
	}
}

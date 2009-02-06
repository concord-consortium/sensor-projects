/**
 * 
 */
package org.concord.sensor.state;

import java.util.Vector;

import org.concord.framework.data.stream.DataListener;
import org.concord.framework.data.stream.DataProducer;
import org.concord.framework.data.stream.DataStreamDescription;
import org.concord.framework.data.stream.DataStreamEvent;
import org.concord.framework.util.Copyable;
import org.concord.sensor.ExperimentConfig;
import org.concord.sensor.ExperimentRequest;
import org.concord.sensor.SensorDataManager;
import org.concord.sensor.SensorDataProducer;
import org.concord.sensor.impl.DataStreamDescUtil;

/**
 * @author scott
 * 
 */
public class SensorDataProxy implements DataProducer, Copyable {
	private SensorDataManager sensorManager;
	private DataProducer producer = null;
	private SensorDataProducer sensorDataProducer = null;
	private ExperimentRequest experimentRequest = null;

	/**
	 * This is a hack to make this refactoring simple. This should be refactored
	 * again so this class doesn't depend on any OT stuff.
	 */
	private OTZeroSensor zeroSensor = null;

	private Vector dataListeners = new Vector();

	/**
	 * should pass in resources.getRequest(); resources.getZeroSensor();
	 * 
	 * @param sdm
	 * @param request
	 */
	public void setup(SensorDataManager sdm, ExperimentRequest request,
			OTZeroSensor zeroSensor) {

		sensorManager = sdm;
		experimentRequest = request;

		// keep a reference to this zero sensor object so it doesn't
		// get garbage collected before the button is pushed
		this.zeroSensor = zeroSensor;

	}

	public ExperimentRequest getExperimentRequest() {
		return experimentRequest;
	}

	public void setExperimentRequest(ExperimentRequest experimentRequest) {
		this.experimentRequest = experimentRequest;
	}

	public SensorDataManager getSensorManager() {
		return sensorManager;
	}

	public void setSensorManager(SensorDataManager sensorManager) {
		this.sensorManager = sensorManager;
	}

	public OTZeroSensor getZeroSensor() {
		return zeroSensor;
	}

	public void setZeroSensor(OTZeroSensor zeroSensor) {
		this.zeroSensor = zeroSensor;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.concord.framework.data.stream.DataProducer#addDataListener(org.concord.framework.data.stream.DataListener)
	 */
	public void addDataListener(DataListener listener) {
		// people might add data listeners to us
		// before the real sensor data producer is ready
		// so we'll need to proxy these listeners

		// we can either proxy the list of listeners
		// or we can proxy the events themselves.
		// one issue might be the event source. Ifresources.getRequest() someone
		// is testing whether the source matches this object
		// then they will get screwed up unless we change
		// the source.
		if (!dataListeners.contains(listener))
			dataListeners.add(listener);
		if (producer != null) {
			producer.addDataListener(listener);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.concord.framework.data.stream.DataProducer#getDataDescription()
	 */
	public DataStreamDescription getDataDescription() {
		if (producer == null) {
			DataStreamDescription dDesc = new DataStreamDescription();

			DataStreamDescUtil.setupDescription(dDesc, experimentRequest, null);

			// this should return a partially correct description
			// before the real device is ready. some fields
			// will be missing or approximate: period, stepSize
			// series or sequence

			return dDesc;
		}

		return producer.getDataDescription();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.concord.framework.data.stream.DataProducer#removeDataListener(org.concord.framework.data.stream.DataListener)
	 */
	public void removeDataListener(DataListener listener) {
		dataListeners.remove(listener);
		if (producer != null) {
			producer.removeDataListener(listener);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.concord.framework.data.DataFlow#reset()
	 */
	public void reset() {
		if (producer != null) {
			producer.reset();
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.concord.framework.data.DataFlow#start()
	 */
	public void start() {
		// ask the devicemanager to configure the device with
		// our experimentrequest, once the producer from the
		// device is recieved we should start it and pass
		// connect up the currently attached data listeners
		// the datamanager should be careful so it doens't
		// start two requests at once.

		ExperimentRequest request = experimentRequest;
		if (sensorDataProducer == null) {
			sensorDataProducer = sensorManager.createDataProducer();
			producer = sensorDataProducer;
			if (sensorDataProducer == null) {
				// we couldn't create the producer
				return;
			}
			for (int i = 0; i < dataListeners.size(); i++) {
				DataListener listener = (DataListener) dataListeners.get(i);
				DataStreamEvent changeEvent = new DataStreamEvent(
						DataStreamEvent.DATA_DESC_CHANGED);
				changeEvent.setDataDescription(getDataDescription());
				listener.dataStreamEvent(changeEvent);
				producer.addDataListener(listener);
			}
		}
		ExperimentConfig config = sensorDataProducer.configure(request);
		if (config == null || !config.isValid()) {
			return;
		}

		if (zeroSensor != null) {
			// need to setup the taring datafilter and wrap it around the
			// producer

			DataProducer newProducer = zeroSensor
					.setupDataFilter(sensorDataProducer);
			if (producer != newProducer) {
				// need to transfer all the listeners from the old
				// producer to the new one
				for (int i = 0; i < dataListeners.size(); i++) {
					DataListener listener = (DataListener) dataListeners.get(i);
					producer.removeDataListener(listener);
					newProducer.addDataListener(listener);
				}

				producer = newProducer;
			}

		}
		producer.start();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.concord.framework.data.DataFlow#stop()
	 */
	public void stop() {
		// stop the proxied dataProducer
		// the dataProducer might be stopped already this
		// could happen if some other proxy started it.

		// FIXME we might a potential memory leak here unless
		// we clean up these listeners.
		if (producer != null) {
			producer.stop();
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.concord.framework.util.Copyable#getCopy()
	 */
	public Object getCopy() {
		SensorDataProxy copy = new SensorDataProxy();
		copy.setup(sensorManager, experimentRequest, zeroSensor);

		// We might need to copy the name some how here.
		return copy;
	}

	public void close() {
		if (sensorDataProducer != null) {
			sensorDataProducer.close();
		}
	}

	public boolean isRunning() {
		return producer.isRunning();
	}

}

package org.concord.sensor.pasco.jna;

import com.sun.jna.Library;
import com.sun.jna.Memory;
import com.sun.jna.ptr.IntByReference;

/**
 * A JNA interface for the Pasco USB Library. All methods return 0 for failure, 1 for success.
 * @author aunger
 *
 */
public interface PascoUSBLibrary extends Library {
	int PasOpenInterface( );
	int PasCloseInterface( );
	/**
	 * Gets the model of interface connected, and writes the result to id.
	 * @param id
	 * @return
	 */
	int PasGetIfaceType( IntByReference id );
	int PasIsInterfaceConnected( );
	int PasIsSensorConnected( );
	/**
	 * Reads the raw datasheet data, writing <i>count</i> bytes into <i>data</i>.
	 * @param data
	 * @param count
	 * @return
	 */

	int PasReadDatasheet( Memory data, int count);
	/**
	 * Starts the sensor in continuous sampling mode.
	 * @param sampleSize - The size, in bytes, of one sample frame (all the data from all of the sensors)
	 * @param samplePeriodMS - How often, in ms, the sensors should sample.
	 * @return
	 */
	int PasStartSampling( int sampleSize, int samplePeriodMS );
	/**
	 * Read the raw sample data generated when running in continuous sampling mode.
	 * @param data - Memory into which the data will be written.
	 * @param count - Preferred number of bytes to read.
	 * @param actual - The number of actual bytes read will be written into this reference.
	 * @return
	 */
	int PasReadSampleData( Memory data, int count, IntByReference actual );
	int PasStopSampling( );
	/**
	 * Read a single measurement from all attached sensors.
	 * @param data
	 * @param sampleSize
	 * @return
	 */
	int PasReadOneSample( Memory data, int sampleSize );
}

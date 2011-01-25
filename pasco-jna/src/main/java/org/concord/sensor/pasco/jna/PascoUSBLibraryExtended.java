package org.concord.sensor.pasco.jna;


/**
 * Some easier to use methods that wrap harder to use library methods. Note that these are NOT NATIVE,
 * so despite having com.sun.jna.Library as an ancestor, you can't actually use this interface to interact with the native driver.
 * @author aunger
 *
 */
public interface PascoUSBLibraryExtended extends PascoUSBLibrary {
	/**
	 * returns the product id of the current interface
	 * @return
	 */
	public int PasGetIfaceType();
	/**
	 * fills data[] with up to count bytes of data, and returns an int representing how many bytes were actually read
	 * @param data
	 * @param count
	 * @return
	 */
	public int PasReadSampleData(byte[] data, int count);
	public int PasReadOneSample(byte[] data, int sampleSize);
	public int PasReadDatasheet(byte[] data, int count);
}

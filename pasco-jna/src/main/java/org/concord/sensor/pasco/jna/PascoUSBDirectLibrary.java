package org.concord.sensor.pasco.jna;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLDecoder;

import org.concord.sensor.pasco.ByteBufferStreamReversed;

import com.sun.jna.Native;
import com.sun.jna.Platform;
import com.sun.jna.Native.DeleteNativeLibrary;
import com.sun.jna.ptr.IntByReference;

public class PascoUSBDirectLibrary implements PascoUSBLibraryExtended {

	private static final boolean DEBUG = Boolean.getBoolean("org.concord.sensor.pasco.usb.debug");
	
	static {
		File nativeLibFile = getNativeLibraryFromJar();
		String nativeLibPath = nativeLibFile.getAbsolutePath();
		
		Native.register(nativeLibPath);
		System.out.println("Registered library: " + nativeLibPath);
		
		try {
			Thread.sleep(100);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
	
	public native int PasOpenInterface( );
	public native int PasCloseInterface( );
	/**
	 * Gets the model of interface connected, and writes the result to id.
	 * @param id
	 * @return
	 */
	public native int PasGetIfaceType( IntByReference id );
	/**
	 * Gets the interface type and returns -1 if unsuccessful, or an int representing the interface type
	 * @param id
	 * @return
	 */
	public int PasGetIfaceType() {
		IntByReference intRef = new IntByReference();
		PasGetIfaceType(intRef);
		return intRef.getValue();
	}
	public native int PasIsInterfaceConnected( );
	public native int PasIsSensorConnected( );
	/**
	 * Reads the raw datasheet data, writing <i>count</i> bytes into <i>data</i>.
	 * @param data
	 * @param count
	 * @return
	 */
	public native int PasReadDatasheet( byte[] data, int count );
	/**
	 * Starts the sensor in continuous sampling mode.
	 * @param sampleSize - The size, in bytes, of one sample frame (all the data from all of the sensors)
	 * @param samplePeriodMS - How often, in ms, the sensors should sample.
	 * @return
	 */
	public native int PasStartSampling( int sampleSize, int samplePeriodMS );
	/**
	 * Read the raw sample data generated when running in continuous sampling mode.
	 * @param data - Array into which the data will be written.
	 * @param count - Preferred number of bytes to read.
	 * @param actual - The number of actual bytes read will be written into this reference.
	 * @return
	 */
	public native int PasReadSampleData( byte[] data, int count, IntByReference actual );
	/**
	 * Returns the length of data read, or 0 if no data was read, or -1 if there was an error.
	 * @param data
	 * @param count
	 * @return
	 */
	public int PasReadSampleData(byte[] data, int count) {
		IntByReference intRef = new IntByReference();
		PasReadSampleData(data, count, intRef);
		if (DEBUG) {
			System.out.println(ByteBufferStreamReversed.formatDataAsString(data, 0, intRef.getValue()));
		}
		return intRef.getValue();
	}
	public native int PasStopSampling( );
	/**
	 * Read a single measurement from all attached sensors.
	 * @param data
	 * @param sampleSize
	 * @return
	 */
	public native int PasReadOneSample( byte[] data, int sampleSize );
	
    private static File getNativeLibraryFromJar() {
        String libname = getNativeLibraryName();
        String resourceName = getNativeLibraryResourcePath() + "/" + libname;
        URL url = PascoUSBDirectLibrary.class.getResource(resourceName);
                
        if (url == null) {
            throw new UnsatisfiedLinkError("PascoUsbLibrary (" + resourceName 
                                           + ") not found in resource path");
        }
    
        File lib = null;
        if (url.getProtocol().toLowerCase().equals("file")) {
            // NOTE: use older API for 1.3 compatibility
            lib = new File(URLDecoder.decode(url.getPath()));
        }
        else {
            InputStream is = Native.class.getResourceAsStream(resourceName);
            if (is == null) {
                throw new Error("Can't obtain jnidispatch InputStream");
            }
            
            FileOutputStream fos = null;
            try {
                // Suffix is required on windows, or library fails to load
                // Let Java pick the suffix
                lib = File.createTempFile("jna", null);
                lib.deleteOnExit();
                if (Platform.deleteNativeLibraryAfterVMExit()) {
                    Runtime.getRuntime().addShutdownHook(new DeleteNativeLibrary(lib));
                }
                fos = new FileOutputStream(lib);
                int count;
                byte[] buf = new byte[1024];
                while ((count = is.read(buf, 0, buf.length)) > 0) {
                    fos.write(buf, 0, count);
                }
            }
            catch(IOException e) {
                throw new Error("Failed to create temporary file for jnidispatch library: " + e);
            }
            finally {
                try { is.close(); } catch(IOException e) { }
                if (fos != null) {
                    try { fos.close(); } catch(IOException e) { }
                }
            }
        }
        return lib;
    }

    private static String getNativeLibraryName() {
        if (Platform.isWindows()) {
        	// FIXME
        	return "Pasco_lib.dll";
        }
        else if (Platform.isMac()) {
        	return "PascoUsbSensorLib.dylib";
        }
        return null;
    }

    private static String getNativeLibraryResourcePath() {
        String arch = System.getProperty("os.arch").toLowerCase();
        String osPrefix;
        if (Platform.isWindows()) {
            osPrefix = "win32_" + arch;
        }
        else if (Platform.isMac()) {
            osPrefix = "darwin";
        }
        else if (Platform.isLinux()) {
            if ("x86".equals(arch)) {
                arch = "i386";
            }
            else if ("x86_64".equals(arch)) {
                arch = "amd64";
            }
            osPrefix = "linux_" + arch;
        }
        else if (Platform.isSolaris()) {
            osPrefix = "sunos_" + arch;
        }
        else {
            osPrefix = System.getProperty("os.name").toLowerCase();
            int space = osPrefix.indexOf(" ");
            if (space != -1) {
                osPrefix = osPrefix.substring(0, space);
            }
            osPrefix += "-" + arch;
        }
        return "/org/concord/sensor/pasco/jna/" + osPrefix;
    }
}

package org.concord.sensor.pasco.jna;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLDecoder;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.concord.sensor.pasco.ByteBufferStreamReversed;

import com.sun.jna.FunctionMapper;
import com.sun.jna.Library;
import com.sun.jna.Memory;
import com.sun.jna.Native;
import com.sun.jna.Platform;
import com.sun.jna.Native.DeleteNativeLibrary;
import com.sun.jna.ptr.IntByReference;
import com.sun.jna.win32.StdCallFunctionMapper;

public class PascoLibrary implements PascoUSBLibraryExtended {
	private static final Logger logger = Logger.getLogger(PascoLibrary.class.getCanonicalName());
	private static PascoUSBLibrary pascoDelegator;
	private static final boolean DEBUG = Boolean.getBoolean("org.concord.sensor.pasco.usb.debug");

    public PascoLibrary() {
    	super();
    	logger.finer("Created new PascoLibrary object.");
    }
    
    static {
		final SingleThreadDelegator<PascoUSBLibrary> delegator = new SingleThreadDelegator<PascoUSBLibrary>();
		delegator.setDaemon(true);
		delegator.start();
		
		File nativeLibFile = getNativeLibraryFromJar();
		String nativeLibPath = nativeLibFile.getAbsolutePath();
		
		Map<String,Object> options = new HashMap<String, Object>();
		
		if (Platform.isWindows()) {
			FunctionMapper functMapper = new StdCallFunctionMapper();
			options.put(Library.OPTION_FUNCTION_MAPPER, functMapper);
		}
//		options.put(Library.OPTION_STRUCTURE_ALIGNMENT, Structure.ALIGN_NONE);
		PascoUSBLibrary pasco = (PascoUSBLibrary) Native.loadLibrary(nativeLibPath, PascoUSBLibrary.class,options);

		try {
			Method terminateMethod = pasco.getClass().getMethod("PasCloseInterface", (Class[]) null);
			pascoDelegator = delegator.instantiate(pasco, PascoUSBLibrary.class, Exception.class, terminateMethod);
		} catch (Exception e) {
			logger.log(Level.WARNING, "Error instantiating the single thread delegator.", e);
		}

		// This is necessary on windows, before calling certain methods the device needs 
		// some time to wake up.
		try {
			Thread.sleep(100);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		
		if (pascoDelegator == null) {
			throw new RuntimeException("Couldn't load native Pasco USB driver!");
		}

		logger.finer("Loaded Pasco USB Native driver!");
		pascoDelegator.PasCloseInterface();
	}
    
    private static File getNativeLibraryFromJar() {
        String libname = getNativeLibraryName();
        String resourceName = getNativeLibraryResourcePath() + "/" + libname;
        URL url = PascoLibrary.class.getResource(resourceName);
                
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
        	return "PascoUsbSensorLib.dll";
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

	public int PasCloseInterface() {
		return pascoDelegator.PasCloseInterface();
	}

	public int PasGetIfaceType(IntByReference id) {
		return pascoDelegator.PasGetIfaceType(id);
	}
	
	/**
	 * Gets the interface type and returns -1 if unsuccessful, or an int representing the interface type
	 * @param id
	 * @return
	 */
	public int PasGetIfaceType() {
		IntByReference intRef = new IntByReference();
		pascoDelegator.PasGetIfaceType(intRef);
		return intRef.getValue();
	}

	public int PasIsInterfaceConnected() {
		return pascoDelegator.PasIsInterfaceConnected();
	}

	public int PasIsSensorConnected() {
		return pascoDelegator.PasIsSensorConnected();
	}

	public int PasOpenInterface() {
		return pascoDelegator.PasOpenInterface();
	}
	
	public int PasReadDatasheet(Memory data, int count) {
		return pascoDelegator.PasReadDatasheet(data, count);
	}

	public int PasReadDatasheet(byte[] data, int count) {
		Memory buf = new Memory(count);
		int res = pascoDelegator.PasReadDatasheet(buf, count);
		buf.read(0, data, 0, count);
		return res;
	}

	public int PasReadOneSample(Memory data, int sampleSize) {
		return pascoDelegator.PasReadOneSample(data, sampleSize);
	}
	
	public int PasReadOneSample(byte[] data, int sampleSize) {
		Memory buf = new Memory(sampleSize);
		int res = pascoDelegator.PasReadOneSample(buf, sampleSize);
		buf.read(0, data, 0, sampleSize);
		return res;
	}

	public int PasReadSampleData(byte[] data, int count, IntByReference actual) {
		Memory buf = new Memory(count);
		int res = pascoDelegator.PasReadSampleData(buf, count, actual);
		buf.read(0, data, 0, actual.getValue());
		return res;
	}

	public int PasReadSampleData(Memory data, int count, IntByReference actual) {
		return pascoDelegator.PasReadSampleData(data, count, actual);
	}
	
	/**
	 * Returns the length of data read, or 0 if no data was read, or -1 if there was an error.
	 * @param data
	 * @param count
	 * @return
	 */
	public int PasReadSampleData(byte[] data, int count) {
		IntByReference actual = new IntByReference();
		Memory buf = new Memory(count);
		pascoDelegator.PasReadSampleData(buf, count, actual);
		buf.read(0, data, 0, actual.getValue());
		if (DEBUG) {
			System.out.println(ByteBufferStreamReversed.formatDataAsString(data, 0, actual.getValue()));
		}
		return actual.getValue();
	}

	public int PasStartSampling(int sampleSize, int samplePeriodMS) {
		return pascoDelegator.PasStartSampling(sampleSize, samplePeriodMS);
	}

	public int PasStopSampling() {
		return pascoDelegator.PasStopSampling();
	}
}

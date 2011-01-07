/*
 * TBD: 
 * 1) Write proper header comment
 * 2) Add exceptions
 * 3) 
 * 4) Comment
 * 5) Add vendor & type to GoIOInterface
 */



package org.concord.sensor.goio.jna;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLDecoder;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import com.sun.jna.FunctionMapper;
import com.sun.jna.Library;
import com.sun.jna.Native;
import com.sun.jna.NativeLibrary;
import com.sun.jna.Platform;
import com.sun.jna.Pointer;
import com.sun.jna.Structure;
import com.sun.jna.Native.DeleteNativeLibrary;

public class GoIOLibrary
{

	protected GoIOJNALibrary goIOLibrary;

	public class GoIOSensor {
		
		public 
		char []deviceName = new char[GoIOJNALibrary.GOIO_MAX_SIZE_DEVICE_NAME];
		int []pVendorId = new int[1];
		int []pProductId = new int[1];
		public Pointer hDevice = null;
		
		public GoIOSensor() {
			
		}
		
	}
	
	public GoIOSensor mkSensor() {
		
		return new GoIOSensor();
		
	}
	
	
	@SuppressWarnings("unchecked")
	public boolean initLibrary()
	{
		File nativeLibFile = getNativeLibraryFromJar();
		String nativeLibPath = nativeLibFile.getAbsolutePath();
		
		FunctionMapper functMapper = new FunctionMapper(){

			// This actually isn't necessary because the function names are the same
			// but in the future we might want to reduce reduancy and map:
			// abcd  to GoIO_Abcd
			public String getFunctionName(NativeLibrary library, Method method) {
				return method.getName();
				// return "GoIO_" + method.getName().substring(0,1).toUpperCase() + 
				// method.getName().substring(1);
			}
			
		};

		
		
		Map options = new HashMap();
		options.put(Library.OPTION_FUNCTION_MAPPER, functMapper);
		options.put(Library.OPTION_STRUCTURE_ALIGNMENT, Structure.ALIGN_NONE);
		goIOLibrary = (GoIOJNALibrary) Native.loadLibrary(nativeLibPath, 
				GoIOJNALibrary.class, options);

		return goIOLibrary != null;		
	};

	
	public int uninit()
	{
		//System.err.println("GoIOInterface: cleaning up");
		
		int ret = goIOLibrary.GoIO_Uninit();
		goIOLibrary = null;
		return ret;
	}
		

	public int init(){		
		return goIOLibrary.GoIO_Init();
	}
	
	public int updateListOfAvailableGoTemp() {
		return goIOLibrary.GoIO_UpdateListOfAvailableDevices(
				GoIOJNALibrary.VERNIER_DEFAULT_VENDOR_ID,
				GoIOJNALibrary.USB_DIRECT_TEMP_DEFAULT_PRODUCT_ID
				);
	}

	public int updateListOfAvailableGoLinks() {
		return goIOLibrary.GoIO_UpdateListOfAvailableDevices(
				GoIOJNALibrary.VERNIER_DEFAULT_VENDOR_ID,
				GoIOJNALibrary.SKIP_DEFAULT_PRODUCT_ID
				);
	}
	
	public int updateListOfAvailableGoMotion() {
		return goIOLibrary.GoIO_UpdateListOfAvailableDevices(
				GoIOJNALibrary.VERNIER_DEFAULT_VENDOR_ID,
				GoIOJNALibrary.CYCLOPS_DEFAULT_PRODUCT_ID
				);
	}

	public boolean isGolinkAttached() {

		int numDevices = 
			goIOLibrary.GoIO_UpdateListOfAvailableDevices(
					GoIOJNALibrary.VERNIER_DEFAULT_VENDOR_ID,
					GoIOJNALibrary.SKIP_DEFAULT_PRODUCT_ID
					);

		return numDevices>0;
	}
	

	public boolean sensorOpen(GoIOSensor sensor)
	{
		
		sensor.hDevice = goIOLibrary.GoIO_Sensor_Open(sensor.deviceName, sensor.pVendorId[0], sensor.pProductId[0], 0); //last arg 0 in all examples...		
	
		return (null != sensor.hDevice);
	}
	
	public boolean getDeviceName(GoIOSensor sensor)
	{
		
		return getDeviceName(sensor.deviceName, GoIOJNALibrary.GOIO_MAX_SIZE_DEVICE_NAME, sensor.pVendorId, sensor.pProductId);

	}
	
	public boolean sensorSetMeasurementPeriod(GoIOSensor sensor,double desiredPeriod, int timeoutMs)
	{
		int ret = goIOLibrary.GoIO_Sensor_SetMeasurementPeriod(sensor.hDevice,desiredPeriod,timeoutMs);	
		
		return 0 == ret;
	}
	
	
	public boolean sensorSendCmd(
			GoIOSensor sensor,	
			byte cmd,		
			Pointer pParams,			
			int nParamBytes,
			Pointer pRespBuf,			
			int []pnRespBytes,
			int timeoutMs)	
	{
		
		
		int ret = goIOLibrary.GoIO_Sensor_SendCmdAndGetResponse(
				sensor.hDevice,
				cmd,		
				pParams,			
				nParamBytes,
				pRespBuf,			
				pnRespBytes,
				timeoutMs);
		
		return ret==0;
		
	}
	

	public boolean sensorStartCollectingData(
			GoIOSensor sensor)
	{
		boolean ret = false;
		
		Pointer pParams = null;
		Pointer pRespBuf =null;
		int []pnRespBytes = null;
		
		ret = sensorSendCmd(sensor,
									GoIOJNALibrary.SKIP_CMD_ID_START_MEASUREMENTS, 							 
									pParams, 
									0, //null,
									pRespBuf, //null, 
									pnRespBytes, //null
									GoIOJNALibrary.SKIP_TIMEOUT_MS_DEFAULT
									);

		return ret;
		
	}
	
	
	public int[] sensorReadRawMeasuements(
			GoIOSensor sensor,
			int maxCount)
	{
		int [] pMeasurementsBuf = new int[maxCount];
		
		int ngot  = goIOLibrary.GoIO_Sensor_ReadRawMeasurements(
					sensor.hDevice,		//[in] handle to open sensor.
					pMeasurementsBuf,	//[out] ptr to loc to store measurements.
					maxCount);	//[in] maximum number of measurements to copy to pMeasurementsBuf. See warning above.

		int [] retbuf = new int [ngot];
		retbuf = Arrays.copyOfRange(pMeasurementsBuf, 0,ngot);
		return retbuf;	
	}
	
	
	//End API
	//Helper functions:
	
	
	protected boolean getDeviceName(char []deviceName, int nameLength, int []pVendorId, int []pProductId)
	{
		/*
		 * FIX: (Ponder)
		 * This function is more or less from GoIO_DeviceCheck.cpp
		 * It seems weird to check for 4 devices
		 * only to return the one that was found first...
		 * 
		 */
		boolean bFoundDevice = true; //Must be true for code to work
		
		deviceName[0] = 0;
		int VDV_ID = GoIOJNALibrary.VERNIER_DEFAULT_VENDOR_ID;
		
		int numSkips     = updateDeviceListEntry(VDV_ID, GoIOJNALibrary.PROBE_GOLINK); 
		int numJonahs    = updateDeviceListEntry(VDV_ID, GoIOJNALibrary.PROBE_USB_TEMPERATURE);
		int numCyclopses = updateDeviceListEntry(VDV_ID, GoIOJNALibrary.PROBE_GOMOTION);
		int numMiniGCs   = updateDeviceListEntry(VDV_ID, GoIOJNALibrary.MINI_GC_DEFAULT_PRODUCT_ID);

		
		do //not a loop: Used in stead of else if 
		{

			if(numSkips>0)
			{
				pVendorId[0] = GoIOJNALibrary.VERNIER_DEFAULT_VENDOR_ID;
				pProductId[0]= GoIOJNALibrary.PROBE_GOLINK;		
				break;
			}
			
			if(numJonahs>0)
			{
				pVendorId[0] = GoIOJNALibrary.VERNIER_DEFAULT_VENDOR_ID;
				pProductId[0]= GoIOJNALibrary.PROBE_USB_TEMPERATURE;				
				break;
			}
			
			if(numCyclopses>0)
			{
				pVendorId[0] = GoIOJNALibrary.VERNIER_DEFAULT_VENDOR_ID;
				pProductId[0]= GoIOJNALibrary.PROBE_GOMOTION;		
				break;
			}
			
			if(numMiniGCs>0)
			{
				pVendorId[0] = GoIOJNALibrary.VERNIER_DEFAULT_VENDOR_ID;
				pProductId[0]= GoIOJNALibrary.PROBE_MINI_GAS_CHROMATOGRAPH;				
				break;
			}
			
			//default, no device found:
			bFoundDevice = false;
			
		}while(false);
		
		if(bFoundDevice)
			goIOLibrary.GoIO_GetNthAvailableDeviceName(deviceName, nameLength, pVendorId[0], pProductId[0], 0);
		
		return bFoundDevice;
	}
	

	
	protected Pointer sensorOpen(char []pDeviceName, int vendorId, int productId)
	{
		return goIOLibrary.GoIO_Sensor_Open(pDeviceName, vendorId, productId, 0); //last arg 0 in all examples...		
	}

	
	protected int updateDeviceListEntry(int vendor, int device_id)
	{
		return goIOLibrary.GoIO_UpdateListOfAvailableDevices(vendor, device_id);		
	}
	
	//FIX: Copied from LabQuestLibrary, then modified:	
    private static File getNativeLibraryFromJar() {
        String libname = getNativeLibraryName();
        String resourceName = getNativeLibraryResourcePath() + "/" + libname;
        URL url = GoIOLibrary.class.getResource(resourceName);
                
        if (url == null) {
            throw new UnsatisfiedLinkError("GoIO (" + resourceName 
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
    

    //FIX: Copied from LabQuestLibrary, then modified:
    private static String getNativeLibraryName() {
        if (Platform.isWindows()) {
        	return "GoIO_DLL.dll";
        }
        else if (Platform.isMac()) {
        	return "libGoIO_DLL.dylib";
        }
        return null;
    }
    
    //FIX: Copied from LabQuestLibrary
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
        return "/org/concord/sensor/goio/jna/" + osPrefix; //path == package name
    }
    
    
	
}


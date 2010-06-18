/*
 * TBD: 
 * 1) Write proper header comment (this)
 * 2) Add exceptions
 * 3) Formatting: Move protected functions, public functions to own group
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
//import com.sun.jna.ptr.IntByReference;
//import com.sun.jna.ptr.ShortByReference;

public class GoIOInterface
{

	protected GoIOLibrary goIOLibrary;

	public class GoIOSensor {
		
		public 
		char []deviceName = new char[GoIOLibrary.GOIO_MAX_SIZE_DEVICE_NAME];
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
	public boolean init()
	{
		File nativeLibFile = getNativeLibraryFromJar();
		String nativeLibPath = nativeLibFile.getAbsolutePath();
		
		FunctionMapper functMapper = new FunctionMapper(){

			public String getFunctionName(NativeLibrary library, Method method) {
				return method.getName().substring(0,1).toUpperCase() + 
				method.getName().substring(1);
			}
			
		};

		
		
		Map options = new HashMap();
		options.put(Library.OPTION_FUNCTION_MAPPER, functMapper);
		options.put(Library.OPTION_STRUCTURE_ALIGNMENT, Structure.ALIGN_NONE);
		goIOLibrary = (GoIOLibrary) Native.loadLibrary(nativeLibPath, 
				GoIOLibrary.class, options);
		
		int ret = goIOLibrary.GoIO_Init();
		
		return 0 == ret;
		
	};

	
	public void cleanup()
	{
		//System.err.println("GoIOInterface: cleaning up");
		
		int ret = goIOLibrary.GoIO_Uninit();

		if(ret != 0){
			System.err.println("GoIOInterface  GoIO_Uninit() failed");
		}

		goIOLibrary = null;
	}
		


	public boolean isGolinkAttached() {

		int numDevices = 
			goIOLibrary.GoIO_UpdateListOfAvailableDevices(
					GoIOLibrary.VERNIER_DEFAULT_VENDOR_ID,
					GoIOLibrary.SKIP_DEFAULT_PRODUCT_ID
					);

		return numDevices>0;
	}
	
	


	
	protected int updateDeviceListEntry(int vendor, int device_id)
	{
		return goIOLibrary.GoIO_UpdateListOfAvailableDevices(vendor, device_id);		
	}
	
	

	public boolean sensorOpen(GoIOSensor goArg)
	{
		
		goArg.hDevice = goIOLibrary.GoIO_Sensor_Open(goArg.deviceName, goArg.pVendorId[0], goArg.pProductId[0], 0); //last arg 0 in all examples...		
	
		return (null != goArg.hDevice);
	}
	
	public boolean getDeviceName(GoIOSensor goArg)
	{
		
		return getDeviceName(goArg.deviceName, GoIOLibrary.GOIO_MAX_SIZE_DEVICE_NAME, goArg.pVendorId, goArg.pProductId);

	}
	
	public boolean sensorSetMeasurementPeriod(GoIOSensor goArg,double desiredPeriod, int timeoutMs)
	{
		int ret = goIOLibrary.GoIO_Sensor_SetMeasurementPeriod(goArg.hDevice,desiredPeriod,timeoutMs);	
		
		return 0 == ret;
	}
	
	
	public boolean sensorSendCmd(
			GoIOSensor goArg,	
			byte cmd,		
			Pointer pParams,			
			int nParamBytes,
			Pointer pRespBuf,			
			int []pnRespBytes,
			int timeoutMs)	
	{
		
		
		int ret = goIOLibrary.GoIO_Sensor_SendCmdAndGetResponse(
				goArg.hDevice,
				cmd,		
				pParams,			
				nParamBytes,
				pRespBuf,			
				pnRespBytes,
				timeoutMs);
		
		return ret==0;
		
	}
	
	public int sensorReadRawMeasuements(
			GoIOSensor goArg,
			int []pMeasurementsBuf,
			int maxCount)
	{
		int ret  = goIOLibrary.GoIO_Sensor_ReadRawMeasurements(
					goArg.hDevice,		//[in] handle to open sensor.
					pMeasurementsBuf,	//[out] ptr to loc to store measurements.
					maxCount);	//[in] maximum number of measurements to copy to pMeasurementsBuf. See warning above.

		return ret;	
	}
	
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
		int VDV_ID = GoIOLibrary.VERNIER_DEFAULT_VENDOR_ID;
		
		int numSkips     = updateDeviceListEntry(VDV_ID, GoIOLibrary.PROBE_GOLINK); 
		int numJonahs    = updateDeviceListEntry(VDV_ID, GoIOLibrary.PROBE_USB_TEMPERATURE);
		int numCyclopses = updateDeviceListEntry(VDV_ID, GoIOLibrary.PROBE_GOMOTION);
		int numMiniGCs   = updateDeviceListEntry(VDV_ID, GoIOLibrary.MINI_GC_DEFAULT_PRODUCT_ID);

		
		do //not a loop: Used in stead of else if 
		{

			if(numSkips>0)
			{
				pVendorId[0] = GoIOLibrary.VERNIER_DEFAULT_VENDOR_ID;
				pProductId[0]= GoIOLibrary.PROBE_GOLINK;		
				break;
			}
			
			if(numJonahs>0)
			{
				pVendorId[0] = GoIOLibrary.VERNIER_DEFAULT_VENDOR_ID;
				pProductId[0]= GoIOLibrary.PROBE_USB_TEMPERATURE;				
				break;
			}
			
			if(numCyclopses>0)
			{
				pVendorId[0] = GoIOLibrary.VERNIER_DEFAULT_VENDOR_ID;
				pProductId[0]= GoIOLibrary.PROBE_GOMOTION;		
				break;
			}
			
			if(numMiniGCs>0)
			{
				pVendorId[0] = GoIOLibrary.VERNIER_DEFAULT_VENDOR_ID;
				pProductId[0]= GoIOLibrary.PROBE_MINI_GAS_CHROMATOGRAPH;				
				break;
			}
			
			//default, no device found:
			bFoundDevice = false;
			
		}while(false);
		
		if(bFoundDevice)
			goIOLibrary.GoIO_GetNthAvailableDeviceName(deviceName, nameLength, pVendorId[0], pProductId[0], 0);
		
		return bFoundDevice;
	}
	
	
	
	
	


	protected boolean REMOVE(
			Pointer hSensor,	
			byte cmd,		
			Pointer pParams,			
			int nParamBytes,
			Pointer pRespBuf,			
			int []pnRespBytes,
			int timeoutMs)	
	{

		int ret = goIOLibrary.GoIO_Sensor_SendCmdAndGetResponse(
			hSensor,	
			cmd,		
			pParams,			
			nParamBytes,
			pRespBuf,			
			pnRespBytes,
			timeoutMs);
		return 0 == ret;
	}
	
	
	protected Pointer sensorOpen(char []pDeviceName, int vendorId, int productId)
	{
		return goIOLibrary.GoIO_Sensor_Open(pDeviceName, vendorId, productId, 0); //last arg 0 in all examples...		
	}
	
	//FIX: Copied from LabQuestLibrary, then modified:	
    private static File getNativeLibraryFromJar() {
        String libname = getNativeLibraryName();
        String resourceName = getNativeLibraryResourcePath() + "/" + libname;
        URL url = GoIOInterface.class.getResource(resourceName);
                
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


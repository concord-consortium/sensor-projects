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

//import org.concord.sensor.labquest.jna.NGIOLibrary;


import com.sun.jna.FunctionMapper;
import com.sun.jna.Library;
import com.sun.jna.Native;
import com.sun.jna.NativeLibrary;
import com.sun.jna.Platform;
import com.sun.jna.Pointer;
import com.sun.jna.Structure;
import com.sun.jna.Native.DeleteNativeLibrary;
import com.sun.jna.ptr.IntByReference;
import com.sun.jna.ptr.ShortByReference;

public class GoIOInterface
{

	protected GoIOLibrary goIOLibrary;
//	protected Pointer hLibrary;

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
		


	public boolean is_golink_attached() {

		int numDevices = 
			goIOLibrary.GoIO_UpdateListOfAvailableDevices(
					GoIOLibrary.VERNIER_DEFAULT_VENDOR_ID,
					GoIOLibrary.SKIP_DEFAULT_PRODUCT_ID
					);

		return numDevices>0;
	}
	
	
	public boolean is_temperature_probe_attached() {

		int numDevices = 
			goIOLibrary.GoIO_UpdateListOfAvailableDevices(
					GoIOLibrary.VERNIER_DEFAULT_VENDOR_ID,
					GoIOLibrary.SKIP_DEFAULT_PRODUCT_ID
					);

		return numDevices>0;
	}

	
	protected int update_device_list_entry(int vendor, int device_id)
	{
		return goIOLibrary.GoIO_UpdateListOfAvailableDevices(vendor, device_id);		
	}
	
	public boolean get_device_name(char []deviceName, int nameLength, int []pVendorId, int []pProductId)
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
		
		int numSkips     = update_device_list_entry(VDV_ID, GoIOLibrary.PROBE_GOLINK); 
		int numJonahs    = update_device_list_entry(VDV_ID, GoIOLibrary.PROBE_USB_TEMPERATURE);
		int numCyclopses = update_device_list_entry(VDV_ID, GoIOLibrary.PROBE_GOMOTION);
		int numMiniGCs   = update_device_list_entry(VDV_ID, GoIOLibrary.MINI_GC_DEFAULT_PRODUCT_ID);

		
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
	
	
	
	public boolean sensor_set_measurement_period(Pointer hSensor,double desiredPeriod, int timeoutMs)
	{
		int ret = goIOLibrary.GoIO_Sensor_SetMeasurementPeriod(hSensor,desiredPeriod,timeoutMs);	
		
		return 0 == ret;
	}
	
	


	public boolean sensor_send_cmd_n_get_response(
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
	
	
	public Pointer sensor_open(char []pDeviceName, int vendorId, int productId)
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
        	return "libGoIO_DLL";
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


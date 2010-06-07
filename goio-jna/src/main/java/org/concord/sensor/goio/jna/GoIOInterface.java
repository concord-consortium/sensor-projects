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

	protected GoIOLibrary goio;
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
		goio = (GoIOLibrary) Native.loadLibrary(nativeLibPath, 
				GoIOLibrary.class, options);
		
		int ret = goio.GoIO_Init();
		
		return 0 == ret;
		
	};

	
	
	
	
	
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
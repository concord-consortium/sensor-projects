package org.concord.sensor.labprousb.jna;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
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
import com.sun.jna.Structure;

public class LabProUSBLibrary {
	private LabProUSBJNA lpusb;

	public void init() throws IOException, InterruptedException
	{
		NativeHelper.removeTemporaryFiles();
		File nativeLibFile = getNativeLibraryFromJar();
		String nativeLibPath = nativeLibFile.getAbsolutePath();
		
		FunctionMapper functMapper = new FunctionMapper(){

			public String getFunctionName(NativeLibrary library, Method method) {
				return "LabProUSB_" + method.getName().substring(0,1).toUpperCase() + 
				method.getName().substring(1);
			}
			
		};
		
		Map<String, Object> options = new HashMap<String, Object>();
		options.put(Library.OPTION_FUNCTION_MAPPER, functMapper);
		options.put(Library.OPTION_STRUCTURE_ALIGNMENT, Structure.ALIGN_NONE);
		lpusb = (LabProUSBJNA) Native.loadLibrary(nativeLibPath, 
				LabProUSBJNA.class, options);

		// This is necessary on windows, before calling certain methods the device needs 
		// some time to wake up.
		Thread.sleep(100);
		NativeHelper.deleteNativeLibrary(nativeLibFile);
	}

	public void cleanup()
	{
		System.err.println("LabProUSBLibrary: cleaning up");
		if(lpusb == null){
			System.err.println("  lpusb null");
			return;
		}

		short ret = lpusb.close();
		if(ret != 1){
			System.err.println("  close failed");
		}
		lpusb = null;
	}

	public LabProUSB openDevice() throws LabProUSBException
	{
		LabProUSB labPro = new LabProUSBImpl(lpusb);

		SingleThreadDelegator<LabProUSB> singleThreadDelegator = 
			new SingleThreadDelegator<LabProUSB>();
		singleThreadDelegator.setDaemon(true);
		singleThreadDelegator.start();
		
		Method closeMethod = null;
		try {
			closeMethod = LabProUSB.class.getMethod("close");
		} catch (SecurityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NoSuchMethodException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		LabProUSB labPro2 = singleThreadDelegator.instanciate(labPro, LabProUSB.class, 
				LabProUSBException.class, closeMethod);

		labPro2.open();
		
		return labPro2;		
	}
	
    static File getNativeLibraryFromJar() throws IOException, InterruptedException {
    	if(Platform.isWindows()){
    		return getNativeLibraryFromJarWindows();
    	} else if (Platform.isMac()){
    		return getNativeLibraryFromJarMac(); 
    	} else {
    		return null;
    	}
    }
    
    static File getNativeLibraryFromJarWindows() throws IOException, InterruptedException {
        String labProUSBDll = getNativeLibraryResourcePath() + "/LabProUSB.dll";
        File directory = createTmpDirectory();
        return extractResource(labProUSBDll, directory);        
    }

    private static File getNativeLibraryFromJarMac() throws IOException {
		String resourceName = getNativeLibraryResourcePath() + "/LabProUSB.dylib";
		File directory = createTmpDirectory();
		return extractResource(resourceName, directory);
    }

    static File createTmpDirectory() throws IOException {
		File directory = File.createTempFile("labprojna", "");
		directory.delete();
		directory.mkdir();
		return directory;
    }
    
    
	static File extractResource(String resourceName, File directory)
			throws Error, FileNotFoundException {
        URL url = LabProUSBLibrary.class.getResource(resourceName);
                
        if (url == null) {
            throw new FileNotFoundException(resourceName + " not found in resource path");
        }
    
        InputStream is = Native.class.getResourceAsStream(resourceName);
        if (is == null) {
            throw new Error("Can't obtain jnidispatch InputStream");
        }
        
        File resourceFile = null;
        FileOutputStream fos = null;
        try {
        	String fileName = resourceName.substring(resourceName.lastIndexOf('/')+1);
        	resourceFile = new File(directory, fileName);
            fos = new FileOutputStream(resourceFile);
            int count;
            byte[] buf = new byte[1024];
            while ((count = is.read(buf, 0, buf.length)) > 0) {
                fos.write(buf, 0, count);
            }
        }
        catch(IOException e) {
            throw new Error("Failed to create temporary file: " + e);
        }
        finally {
            try { is.close(); } catch(IOException e) { }
            if (fos != null) {
                try { fos.close(); } catch(IOException e) { }
            }
        }
        return resourceFile;
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
        return "/org/concord/sensor/labprousb/jna/" + osPrefix;
    }
    
    private static class NativeHelper {
    	private static void deleteNativeLibrary(File file) {
    		if (file.delete()) {
    			return;
    		}
    		markTemporaryFile(file);
    	}

    	private static void markTemporaryFile(File file) {
    		try {
    			File marker = new File(file.getParentFile(), file.getName() + ".x");
    			marker.createNewFile();
    		}
    		catch(IOException e) { e.printStackTrace(); }
    	}

    	private static void removeTemporaryFiles() {
    		File dir;
    		try {
    			dir = File.createTempFile("jna", ".x").getParentFile();
    			FilenameFilter filter = new FilenameFilter() {
    				public boolean accept(File dir, String name) {
    					return name.endsWith(".x") && name.indexOf("jna") != -1;
    				}
    			};
    			File[] files = dir.listFiles(filter);
    			for (int i=0;files != null && i < files.length;i++) {
    				File marker = files[i];
    				String name = marker.getName();
    				name = name.substring(0, name.length()-2);
    				File target = new File(marker.getParentFile(), name);
    				if (!target.exists() || target.delete()) {
    					marker.delete();
    				}
    			}
    		} catch (IOException e) {
    			// TODO Auto-generated catch block
    			e.printStackTrace();
    		}
    	}
    }
}

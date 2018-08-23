package org.concord.sensor.labquest.jna;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Method;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import com.sun.jna.FunctionMapper;
import com.sun.jna.Library;
import com.sun.jna.Native;
import com.sun.jna.NativeLibrary;
import com.sun.jna.Platform;
import com.sun.jna.Pointer;
import com.sun.jna.Structure;
import com.sun.jna.ptr.IntByReference;
import com.sun.jna.ptr.ShortByReference;

public class LabQuestLibrary 
{
	private static LabQuestLibrary instance;
	private NGIOLibrary ngio;
	private Pointer hLibrary;
	private ArrayList<Integer> initializers = new ArrayList<Integer>();

	public static LabQuestLibrary getInstance() throws IOException, InterruptedException
	{
		if(instance != null){
			return instance;
		}

		instance = new LabQuestLibrary();
		return instance;
	}

	private LabQuestLibrary() throws IOException, InterruptedException
	{
		NativeHelper.removeTemporaryFiles();
		File nativeLibFile = getNativeLibraryFromJar();
		String nativeLibPath = nativeLibFile.getAbsolutePath();
		
		FunctionMapper functMapper = new FunctionMapper(){

			public String getFunctionName(NativeLibrary library, Method method) {
				return "NGIO_" + method.getName().substring(0,1).toUpperCase() + 
				method.getName().substring(1);
			}
			
		};
		
		Map<String, Object> options = new HashMap<String, Object>();
		options.put(Library.OPTION_FUNCTION_MAPPER, functMapper);
		options.put(Library.OPTION_STRUCTURE_ALIGNMENT, Structure.ALIGN_NONE);
		ngio = (NGIOLibrary) Native.loadLibrary(nativeLibPath, 
				NGIOLibrary.class, options);
		NativeHelper.deleteNativeLibrary(nativeLibFile);
	}

	public void init(Object key) throws InterruptedException, LabQuestException
	{
		// keep track of objects that have intialized us, so we don't uninit until all of them
		// have called uninit
		initializers.add(key.hashCode());
		if(hLibrary != null){
			// we were already initialized
			return;
		}

		hLibrary = ngio.init();
		if(hLibrary == null){
			throw new LabQuestException("Cannot initialize native library");
		}

		int lqDebugLevel = Integer.getInteger("labquest.debug.level", -1);
		if(lqDebugLevel != -1){
			ngio.diags_SetDebugTraceThreshold(hLibrary, lqDebugLevel);
		}

		// This is necessary on windows, before calling certain methods the device needs 
		// some time to wake up.
		Thread.sleep(200);
	}

	public void uninit(Object key)
	{
		if(hLibrary == null){
			System.err.println("  hLibrary null");
			return;
		}

		initializers.remove((Integer)key.hashCode());
		if(initializers.size() > 0){
			return;
		}

		int ret = ngio.uninit(hLibrary);
		if(ret != 0){
			System.err.println("  uninit failed");
		}
		hLibrary = null;
	}
	
	public short [] getDLLVersion() throws LabQuestException
	{
		ShortByReference majorVersion = new ShortByReference();
		ShortByReference minorVersion = new ShortByReference();
		int ret = ngio.getDLLVersion(hLibrary, majorVersion, minorVersion);
		if(ret != 0){
			throw new LabQuestException();
		}
		short [] version = new short[2];
		version[0] = majorVersion.getValue();
		version[1] = minorVersion.getValue();
		
		return version;
	}

	public void searchForDevices() throws LabQuestException 
	{
		IntByReference listSig = new IntByReference();
		int ret = -1;
		ret = ngio.searchForDevices(hLibrary, NGIOLibrary.DEVTYPE_LABQUEST, 
				NGIOLibrary.COMM_TRANSPORT_USB, null, listSig);
		if(ret != 0){
			throw new LabQuestException();
		}
		
		ret = ngio.searchForDevices(hLibrary, NGIOLibrary.DEVTYPE_LABQUEST_MINI, 
				NGIOLibrary.COMM_TRANSPORT_USB, null, listSig);
		if(ret != 0){
			throw new LabQuestException();
		}

		ret = ngio.searchForDevices(hLibrary, NGIOLibrary.DEVTYPE_LABQUEST2, 
				NGIOLibrary.COMM_TRANSPORT_USB, null, listSig);
		if(ret != 0){
			throw new LabQuestException();
		}
	}
	
	public void printListOfDevices() throws LabQuestException 
	{
		
		// print labquest devices
		printDeviceListSnapshot(NGIOLibrary.DEVTYPE_LABQUEST, "labquest");
		
		// print labquest mini devices
		printDeviceListSnapshot(NGIOLibrary.DEVTYPE_LABQUEST_MINI, "labquest_mini");

		// print labquest mini devices
		printDeviceListSnapshot(NGIOLibrary.DEVTYPE_LABQUEST2, "labquest_mini");
	}

	private void printDeviceListSnapshot(int deviceType, String deviceTypeName) throws LabQuestException
	{
		IntByReference listSig = new IntByReference();
		IntByReference numDevices = new IntByReference();
		
		// print labquest devices
		Pointer openDeviceListSnapshotHandle = ngio.openDeviceListSnapshot(hLibrary, 
				deviceType, 
				numDevices, listSig);
		if(openDeviceListSnapshotHandle == null){
			System.err.println("got a null snapshot handle");
			return;
		}
		
		System.out.println(deviceTypeName + " num devices: " + numDevices.getValue() + 
				" list sig: " + listSig.getValue());
	
		byte [] devNameBuf = new byte[NGIOLibrary.MAX_SIZE_DEVICE_NAME];
		IntByReference deviceStatusMask = new IntByReference();
		for(int i=0; i<numDevices.getValue(); i++){
			
			ngio.deviceListSnapshot_GetNthEntry(openDeviceListSnapshotHandle, i, 
					devNameBuf, devNameBuf.length, deviceStatusMask);
			String devName = Native.toString(devNameBuf);
			System.out.println("  dev name: " + devName);
		}		
		closeDeviceListSnapshot(openDeviceListSnapshotHandle);
	}
	
	private void closeDeviceListSnapshot(Pointer deviceListSnapshotHandle) throws LabQuestException
	{
		int ret = ngio.closeDeviceListSnapshot(deviceListSnapshotHandle);
		if(ret != 0){
			throw new LabQuestException();
		}		
	}
	
	public String getFirstDeviceName() throws LabQuestException 
	{
		searchForDevices();
		
		String ret = getFirstDeviceName(NGIOLibrary.DEVTYPE_LABQUEST);
		if(ret != null) {
			return ret;
		}
		
		ret = getFirstDeviceName(NGIOLibrary.DEVTYPE_LABQUEST_MINI);
		if(ret != null) {
			return ret;
		}

		ret = getFirstDeviceName(NGIOLibrary.DEVTYPE_LABQUEST2);

		return ret;
	}
	
	private String getFirstDeviceName(int deviceType) throws LabQuestException{
		IntByReference listSig = new IntByReference();
		IntByReference numDevices = new IntByReference();
		int ret;
		Pointer openDeviceListSnapshotHandle = 
			ngio.openDeviceListSnapshot(hLibrary, deviceType, 
				numDevices, listSig);
		
		int num = numDevices.getValue();
		if(num <= 0){
			closeDeviceListSnapshot(openDeviceListSnapshotHandle);
			return null;
		}
		
		byte [] devNameBuf = new byte[NGIOLibrary.MAX_SIZE_DEVICE_NAME];
		IntByReference deviceStatusMask = new IntByReference();
		ret = ngio.deviceListSnapshot_GetNthEntry(openDeviceListSnapshotHandle, 0, 
				devNameBuf, devNameBuf.length, deviceStatusMask);
		if(ret != 0){
			throw new LabQuestException();
		}
		
		closeDeviceListSnapshot(openDeviceListSnapshotHandle);

		return Native.toString(devNameBuf);
	}

	public LabQuest openDevice(String deviceName) throws LabQuestException
	{
		LabQuest labQuest = new LabQuestImpl(ngio);

		SingleThreadDelegator<LabQuest> singleThreadDelegator = 
			new SingleThreadDelegator<LabQuest>();
		singleThreadDelegator.setDaemon(true);
		singleThreadDelegator.start();
		
		Method closeMethod = null;
		try {
			closeMethod = LabQuest.class.getMethod("close");
		} catch (SecurityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NoSuchMethodException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		LabQuest labQuest2 = singleThreadDelegator.instanciate(labQuest, LabQuest.class, 
				LabQuestException.class, closeMethod);

		labQuest2.open(deviceName, hLibrary);
		
		return labQuest2;		
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
        String ngioDll = getNativeLibraryResourcePath() + "/NGIO_lib.dll";
        return extractResource(ngioDll, createTmpDirectory());
    }
    
    private static File getNativeLibraryFromJarMac() throws IOException {
		String resourceName = getNativeLibraryResourcePath() + "/libNGIOUniversal2way.dylib";
		File directory = createTmpDirectory();
		return extractResource(resourceName, directory);
    }

    static File createTmpDirectory() throws IOException {
		File directory = File.createTempFile("jna", "");
		directory.delete();
		directory.mkdir();
		return directory;
    }
    
    
	static File extractResource(String resourceName, File directory)
			throws Error, FileNotFoundException {
        URL url = LabQuestLibrary.class.getResource(resourceName);
                
        if (url == null) {
            throw new FileNotFoundException(resourceName + " not found in resource path");
        }
    
        File resourceFile = null;
        InputStream is = Native.class.getResourceAsStream(resourceName);
        if (is == null) {
            throw new Error("Can't obtain jnidispatch InputStream");
        }
        
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
            osPrefix = "win32";
            if ("x86_64".equals(arch)) {
                osPrefix = "win64";
            }			
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
        return "/org/concord/sensor/labquest/jna/" + osPrefix;
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

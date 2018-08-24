package org.concord.sensor.labprousb.jna;

import java.io.File;

import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;

import com.sun.jna.Platform;

public class LabProUSBTest {

	@Test
	public void testNativeLibraryExtraction() throws Exception {
		File lib = LabProUSBLibrary.getNativeLibraryFromJar();
		if (Platform.isWindows()) {
			String arch = System.getProperty("os.arch").toLowerCase();
			// At present we only have a 32 bit version of the LabPro DLL
			// Add additional unit tests if more DLLs (e.g., 64 bit version) are eventually added
			if ("x86".equals(arch)) {
				Assert.assertNotNull("LabProUSB native library should not be null when making 32 bit build on Windows operating systems.", lib);
				Assert.assertTrue(lib.exists());				
			}
		} else if(Platform.isMac()) {
			Assert.assertNotNull("LabProUSB native library should not be null on Mac operating systems.", lib);
			Assert.assertTrue(lib.exists());
		} else {
			Assert.assertNull("LabProUSB native library should be null on non-Windows operating systems.", lib);
		}
	}
	
	@Test
  @Ignore
	public void testOpenDevice() throws Exception {
			LabProUSBLibrary lib = new LabProUSBLibrary();
			lib.init();
		try {
			lib.openDevice();
			Assert.assertTrue("openDevice should throw an exception when the device isn't present!", false);
		} catch (LabProUSBException e) {
			
		}
	}
}

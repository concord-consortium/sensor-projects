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
			Assert.assertNotNull("LabProUSB native library should not be null on Windows operating systems.", lib);
			Assert.assertTrue(lib.exists());
			
			File dir = lib.getParentFile();
			File dependentLib = new File(dir, "wdapi921.dll");
			Assert.assertTrue("wdapi921.dll should be created from wdapi921_WIN32forOS32.dll or wdapi921_WIN32forOS64.dll", dependentLib.exists());
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
			LabProUSB lpusb = lib.openDevice();
			Assert.assertTrue("openDevice should throw an exception when the device isn't present!", false);
		} catch (LabProUSBException e) {
			
		}
	}
}

package org.concord.sensor.labquest.jna;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

import junit.framework.Assert;

import org.junit.Test;

import com.sun.jna.Platform;


public class LoadNativeLibraryTest {
	@Test
	public void testExtractResource() throws IOException{
		File tmpDirectory = LabQuestLibrary.createTmpDirectory();
		File resourceFile = LabQuestLibrary.extractResource("/org/concord/sensor/labquest/jna/darwin/libNGIO.dylib", tmpDirectory);
		Assert.assertTrue("extracted resource exists", resourceFile.exists());		
		Assert.assertEquals("extracted resource has the correct ending name", "libNGIO.dylib", resourceFile.getName());
	}
		
	@Test
	public void testExtractResourceInvalid() throws IOException{
		try{
			File tmpDirectory = LabQuestLibrary.createTmpDirectory();
			LabQuestLibrary.extractResource("/org/concord/sensor/labquest/jna/wrong/libNGIO.dylib", tmpDirectory);
			Assert.fail("invalid resource path should throw an exception");
		} catch(FileNotFoundException e){			
		}
	}

	@Test
	public void testGetNativeLibraryFromJarWindows() throws IOException, InterruptedException{
		if(Platform.isWindows()){
			@SuppressWarnings("unused")
			File nativeLibraryFromJarWindows = LabQuestLibrary.getNativeLibraryFromJarWindows();
			return;
		}
		
		if(Platform.isMac()){
			try{
				@SuppressWarnings("unused")
				File nativeLibraryFromJarWindows = LabQuestLibrary.getNativeLibraryFromJarWindows();
				Assert.fail("running the windows extraction on Mac should fail");
			} catch(Exception e){				
			}
		}
	}
}

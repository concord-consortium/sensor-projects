package org.concord.sensor.goio.jna;

import com.sun.jna.Library;
import com.sun.jna.Pointer;
import com.sun.jna.Structure;
import com.sun.jna.ptr.ByteByReference;
import com.sun.jna.ptr.DoubleByReference;
import com.sun.jna.ptr.IntByReference;
import com.sun.jna.ptr.ShortByReference;


public interface GoIOLibrary extends Library {
	public final static int STRUCTURE_ALIGNMENT = Structure.ALIGN_NONE; 
	
	
	int GoIO_Init();
	
	int GoIO_Uninit();	
	
}
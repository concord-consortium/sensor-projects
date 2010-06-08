package org.concord.sensor.goio.jna;

import com.sun.jna.Library;
import com.sun.jna.Pointer;
import com.sun.jna.Structure;
import com.sun.jna.ptr.ByteByReference;
import com.sun.jna.ptr.DoubleByReference;
import com.sun.jna.ptr.IntByReference;
import com.sun.jna.ptr.ShortByReference;






public interface GoIOLibrary extends Library {
	
	
	//Some stuff from GVernierUSB.h
	//Constants used by the USB protocol to identify our devices:
	public final static int VERNIER_DEFAULT_VENDOR_ID  = 0x08F7;

	public final static int LABPRO_DEFAULT_PRODUCT_ID = 0x0001;
	public final static int USB_DIRECT_TEMP_DEFAULT_PRODUCT_ID = 0x0002;	//aka GoTemp
	public final static int SKIP_DEFAULT_PRODUCT_ID = 0x0003;				//aka GoLink
	public final static int CYCLOPS_DEFAULT_PRODUCT_ID = 0x0004;			//aka GoMotion
	public final static int NGI_DEFAULT_PRODUCT_ID = 0x0005;				//aka LabQuest
	public final static int LOWCOST_SPEC_DEFAULT_PRODUCT_ID = 0x0006;		//aka CK Spectrometer
	public final static int MINI_GC_DEFAULT_PRODUCT_ID = 0x0007;			//aka Vernier Mini Gas Chromatograph
	public final static int STANDALONE_DAQ_DEFAULT_PRODUCT_ID = 0x0008;
			
	//Some of ^^ in human readable form:
	public final static int GOIO_GOTEMP = USB_DIRECT_TEMP_DEFAULT_PRODUCT_ID; 
	public final static int GOIO_GOLINK = SKIP_DEFAULT_PRODUCT_ID; 
	public final static int GOIO_GOMOTION = CYCLOPS_DEFAULT_PRODUCT_ID;
	public final static int GOIO_LABQUEST = NGI_DEFAULT_PRODUCT_ID; 
	public final static int GOIO_CK_SPECTROMETER = LOWCOST_SPEC_DEFAULT_PRODUCT_ID; 
	public final static int GOIO_VERNIER_MINI_GAS_CHROMATOGRAPH = MINI_GC_DEFAULT_PRODUCT_ID ;			
	
	
	//Some from GoIO_DLL_interface.h
	public final static int STRUCTURE_ALIGNMENT = Structure.ALIGN_NONE; 
	public final static int GOIO_MAX_SIZE_DEVICE_NAME = 255; //FIX: 260 Non Mac OS's
	
	int GoIO_Init();
	
	int GoIO_Uninit();	
	
	//Not tested functions:
	
	int GoIO_UpdateListOfAvailableDevices(
			int vendorId,	//[in]
			int productId);	//[in]
	
}
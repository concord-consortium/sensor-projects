package org.concord.sensor.pasco;

import java.util.logging.Logger;

import org.concord.sensor.pasco.jna.PascoLibrary;
import org.concord.sensor.pasco.jna.PascoUSBLibraryExtended;

public class TestPascoLibrary {
	private static final Logger logger = Logger.getLogger(TestPascoLibrary.class.getCanonicalName());


	/**
	 * @param args
	 */
	public static void main(String[] args) throws InterruptedException {
		int i;
		int success;
		System.out.println( "Test Pasco USB sensor lib" );
		
		int samplePeriod = 100;
		int sampleSize = 2;
		
//		PascoUSBLibraryExtended pasco = new PascoUSBDirectLibrary();
//		PascoUSBLibraryExtended pasco = PascoLibrary.getInstance();
		PascoUSBLibraryExtended pasco = new PascoLibrary();
		
		// Connect to the sensor
		success = pasco.PasOpenInterface( );
		System.out.println( "Open Interface: success=" + success );
		
    	int id = pasco.PasGetIfaceType();
    	System.out.println("Device interface type = " + id);

		// Dump the sensor's datasheet
		byte[] dsHdr = new byte[16];
		// DSHeader dsHdr = new DSHeader();
		// success = pasco.PasReadDatasheet( dsHdr.getPointer(), dsHdr.size() );
		success = pasco.PasReadDatasheet(dsHdr, 16);
		System.out.println( "Read Datasheet Header: success=" + success );
		for (byte b : dsHdr) {
			System.out.print(b);
		}
		System.out.println("");
		if (success == 0) {
			System.out.println("Couldn't read a valid data sheet!");
			return;
		}
		PasportSensorDataSheet ds = readDataSheet(pasco);
//		System.out.println( "SensorID = " + dsHdr.sensorID + ", Max DS Size = " + dsHdr.maxSize + ", DS Size = " + dsHdr.dsSize + ", Ext DS size = " + dsHdr.extDsSize);
		System.out.println( "SensorID = " + ds.id + ", Max DS Size = " + ds.maxDataSheetSize + ", DS Size = " + ds.dataSheetLength + ", Ext DS size = " + ds.extendedDataSheetLength);
		// DataSheetData data = new DataSheetData();
		System.out.println(ds.getStringView());
		byte[] data;
//		data = new byte[dsHdr.dsSize + dsHdr.extDsSize];
//		success = pasco.PasReadDatasheet( data.getPointer(), dsHdr.dsSize + dsHdr.extDsSize );
//		if( success == 1) {
//			dumpBuffer( data, dsHdr.dsSize + dsHdr.extDsSize );
//		}
		
		// Do a few one-shot reads
		for( i = 0; i < 5; i++ )
		{
			data = new byte[sampleSize];
			success = pasco.PasReadOneSample( data, sampleSize );
			System.out.println( String.format("Read sample %d of %d, success=%d\n", i+1, 5, success) );
			dumpBuffer( data, sampleSize );
			Thread.sleep( 1000 );
		}
		
		// Start continuous sampling
		success = pasco.PasStartSampling( sampleSize, samplePeriod );
		System.out.println( String.format("Start continuous sampling, success=%d\n", success) );
		
		// Gather and dump the sample data
		data = new byte[100];
		for( i = 0; i < 50; i++ )
		{
			success = pasco.PasReadSampleData( data, 100 );
			if( success != 0 )
				dumpBuffer( data, success );
			Thread.sleep( 100 );
		}
		
		// Stop continuous sampling
		success = pasco.PasStopSampling( );
		System.out.println( String.format("Stop continuous sampling, success=%d\n", success ));
		
		// Show sensor and interface connection status for the next ten seconds
		for( i = 0; i < 10; i++ )
		{
			System.out.println( String.format("Connection Status: sensor=%d, iface=%d\n", pasco.PasIsSensorConnected( ), pasco.PasIsInterfaceConnected( ) ));
			Thread.sleep( 1000 );
		}
		
		System.out.println( "Done testing Pasco USB sensor lib" );

	    System.exit(0);
	}
	
	private static void dumpBuffer (byte[] pvBuf, int lLen)
	{
	    byte[] pbBuf = pvBuf;
		int lBytes = 1;
		
	    // for each 16 byte row
	    for (int nRow = 0; nRow <= (lLen-1)/16; nRow++)
	    {
	        String pszStr = "";
			
	        // fill in the hex view of the data
	        int nCol;
	        for (nCol = 0; nCol < 16; nCol+=lBytes)
	        {
	            switch (lBytes)
	            {
					case 1: // show as bytes
					default:
						if (nRow*16+nCol < lLen)
							pszStr += String.format("%02x ", pbBuf[nRow*16+nCol]);
						else
							pszStr += String.format("   ");
						break;
						
					case 2: // show as shorts
						if (nRow*16+nCol < lLen)
							pszStr += String.format ("%04x ", pbBuf[nRow*16+nCol]);
						else
							pszStr += String.format("     ");
						break;
						
					case 4: // show as longs
						if (nRow*16+nCol < lLen)
							pszStr += String.format ("%08x ", pbBuf[nRow*16+nCol]);
						else
							pszStr += String.format ("         ");
						break;
	            }
	        }
			
	        // fill in the char view of the data
	        for (nCol = 0; nCol < 16; nCol++)
	        {
	            if (nRow*16+nCol < lLen)
	            {
	                byte ch = pbBuf[nRow*16+nCol];
	                if (ch < 0x20 || ch >= 0x7f)
	                    pszStr += ".";
	                else
	                    pszStr += String.format("%c", ch);
	            }
	            else
	                pszStr += " ";
	        }
			
			System.out.println(String.format( "%05x: %s\n", nRow*0x10, pszStr));
	    }
	}
	
	protected static PasportSensorDataSheet readDataSheet(PascoUSBLibraryExtended pasco) throws InterruptedException
	{				
		int tmp;
		
		// Read the id, total memory size, actual size
        byte[] buf = new byte[8];
		// provider.sleep(1000);
		
		tmp = pasco.PasReadDatasheet(buf, buf.length);
		for (byte b : buf) {
			System.out.print(b);
			System.out.print(" ");
		}
		System.out.println();

		int id = ByteBufferStreamReversed.readUShort(buf, 0);
		int mem = ByteBufferStreamReversed.readUShort(buf, 2);
		int size = ByteBufferStreamReversed.readUShort(buf, 4);
		int extSize = ByteBufferStreamReversed.readUShort(buf, 6);
		
		System.out.println("Id: " + id);
		System.out.println("Mem: " + mem);
		System.out.println("Size: " + size);
		System.out.println("ExtSize: " + size);
		
		buf = new byte[size + extSize];
		
		// Thread.sleep(1000);

 		// Read the whole sheet now
		// this just directly takes the length from the previous
		// read.  I don't know what it will give if there is 
		// no sensor attached.
		tmp = pasco.PasReadDatasheet(buf, buf.length);
		
		if(tmp == 0) {
			// no/bad response from device
			// maybe it isn't attached?
			// or we didn't wait long enough
			logger.info("ds cmd: 0 byte");
			return null;
		}
		
		ByteBufferStreamReversed bb = new ByteBufferStreamReversed(buf, 0, buf.length, null);
		PasportSensorDataSheet pSens = new PasportSensorDataSheet(bb);
			
		System.out.println(pSens.getStringView());
		
		for(int i=0; i<pSens.measurements.length; i++) {
			String measurementStr = pSens.measurements[i].getStringView();
			System.out.println(measurementStr);
		}
		
		return pSens;
	}

}

package org.concord.sensor.pasco.datasheet;

import java.io.FileInputStream;
import java.io.IOException;

public class PrintDataSheetFile {

	/**
	 * @param args
	 * @throws IOException 
	 */
	public static void main(String[] args) throws IOException {
		byte [] buffer = new byte[10000];
		
		// load in the file
		FileInputStream fileInputStream = new FileInputStream(args[0]);
		int offset = 0;
		for(int i=0; i<10; i++){
			int ret = fileInputStream.read(buffer, offset, 1000);
			if(ret == -1){
				break;
			}
			offset += ret;
		}
		
		// make a bytebufferstream reversed
		ByteBufferStreamReversed byteBufferStreamReversed = new ByteBufferStreamReversed(buffer, 0, offset);
		
		//print it
		PasportSensorDataSheet pasportSensorDataSheet = new PasportSensorDataSheet(byteBufferStreamReversed);
		
		Printer p = new Printer("");
		pasportSensorDataSheet.print(p);
		
		p.printToSysout();
	}

}

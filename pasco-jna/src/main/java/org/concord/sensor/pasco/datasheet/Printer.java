/**
 * 
 */
package org.concord.sensor.pasco.datasheet;

public class Printer {
	String result = "";
	String indent;
	private Printer parent;
	public Printer(String indent) {
		this.indent = indent;
	}
	
	public Printer(String indent, Printer parent) {
		this.indent = indent;
		this.parent = parent;
	}

	public void puts(String line) {
		if(parent != null){
			parent.puts(indent + line);
		} else {
			result += indent + line + "\n";
		}
	}
	
	public void printToSysout(){
		System.out.print(result);
	}
	
	public void printBuffer(byte[] pvBuf, int lLen)
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
			
	        puts(String.format( " %05x: %s", nRow*0x10, pszStr));
	    }
	}
}
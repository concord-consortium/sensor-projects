
/*
 *  Copyright (C) 2004  The Concord Consortium, Inc.,
 *  10 Concord Crossing, Concord, MA 01741
 *
 *  Web Site: http://www.concord.org
 *  Email: info@concord.org
 *
 *  This library is free software; you can redistribute it and/or
 *  modify it under the terms of the GNU Lesser General Public
 *  License as published by the Free Software Foundation; either
 *  version 2.1 of the License, or (at your option) any later version.
 *
 *  This library is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *  Lesser General Public License for more details.
 *
 *  You should have received a copy of the GNU Lesser General Public
 *  License along with this library; if not, write to the Free Software
 *  Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 *
 */

/*
 * Created on Feb 16, 2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package org.concord.sensor.pasco;

import java.util.logging.Logger;

import org.concord.sensor.device.DeviceService;


/**
 * @author scott
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class ByteBufferStreamReversed
{
	private static final Logger logger = Logger.getLogger(ByteBufferStreamReversed.class.getCanonicalName());
    byte [] inBuf;
    int offset;
    int endOffset;
    boolean lowNibble = false;
    DeviceService devService;
    
    /*
    public static final float readFloat(byte [] inBuf, int offset)
    {
        int valueInt = ((0xFF & inBuf[offset]) << 24)
                | ((0xFF & inBuf[offset + 1]) << 16)
                | ((0xFF & inBuf[offset + 2]) << 8)
                | (0xFF & inBuf[offset + 3]);
        return Float.intBitsToFloat(valueInt);        
    }
*/
    public static final int readUByte(byte [] inBuf, int offset)
    {
        int value = (inBuf[offset] & 0xFF);
        return value;        
    }    

    
    public static final int readUShort(byte [] inBuf, int offset)
    {
        int value =((inBuf[offset + 1] & 0xFF) << 8) | 
        	(inBuf[offset] & 0xFF);
        return value;        
    }

    public static final short readShort(byte [] inBuf, int offset)
    {
        int value =((inBuf[offset + 1]) << 8) | 
        	(inBuf[offset] & 0xFF);
        return (short)value;
    }

    public static final long readULong(byte [] inBuf, int offset)
    {
        long value = 
        	(inBuf[offset++] & 0xFF) |
        	((inBuf[offset++] & 0xFF) << 8) | 
        	((inBuf[offset++] & 0xFF) << 16) |
        	(((inBuf[offset++] & 0xFFL) << 24))
        	;
        return value;        
    }

    public static final int readInt(byte [] inBuf, int offset)
    {
        int value = 
        	(inBuf[offset++] & 0xFF) |
        	((inBuf[offset++] & 0xFF) << 8) | 
        	((inBuf[offset++] & 0xFF) << 16) |
        	((inBuf[offset++] & 0xFF) << 24)
        	;
        return value;            	
    }
    
    public static float readFixed(byte [] inBuf, int offset)
    {
    	// read the fraction
    	int numerator = readUShort(inBuf, offset);
    	int wholeNumber = readShort(inBuf, offset+2);
    	
    	return wholeNumber + numerator / (float)0xFFFF;
    }

    
    public static final void writeULong(long value, byte [] buf, int offset)
    {
        buf[offset++] = (byte)(value & 0x000000FF);        
        buf[offset++] = (byte)((value & 0x0000FF00) >>  8);
        buf[offset++] = (byte)((value & 0x00FF0000) >> 16);
        buf[offset++]   = (byte)((value & 0xFF000000) >> 24);
    }
    
    public static final void writeUShort(int value, byte [] buf, int offset)    
    {
        buf[offset++] = (byte)(value & 0x000000FF);        
        buf[offset++] = (byte)((value & 0x0000FF00) >>  8);
    }
    
    public static final String formatDataAsString(byte[] data, int offset, int len) {
    	StringBuilder sb = new StringBuilder();
		for (int i = offset; (i-offset) < len; i += 2) {
			sb.append(String.format("%4s ",Integer.toHexString(ByteBufferStreamReversed.readUShort(data, i))));
			if ((i-offset+1) % 50 == 0) {
				sb.append("\n");
			}
		}
		return sb.toString();
	}

    public ByteBufferStreamReversed(byte [] buffer, int offset, int length,
            DeviceService devService)
    {
        inBuf = buffer;
        this.offset = offset;
        this.endOffset = offset+length;
        this.devService = devService;
    }
    
    /*
    public float readFloat()
    {
        if(lowNibble) {
            throw new RuntimeException("unread low nibble");
        }
        float value = readFloat(inBuf, offset);
        offset += 4;
        
        return value;
    }
    */
    
    public float readFixed()
    {
    	checkPosition();
    	float value = readFixed(inBuf, offset);
    	offset += 4;

    	return value;
    }
    
    public int readInt()
    {
    	checkPosition();
        int value = readInt(inBuf, offset);
        offset += 4;

        return value;        
    	
    }
    
    public int readShort()
    {
    	checkPosition();
        int value = readShort(inBuf, offset);
        offset += 2;

        return value;        
    }
    
    public int readUShort()
    {
    	checkPosition();
        int value = readUShort(inBuf, offset);
        offset += 2;

        return value;        
    }
    
    public int readUByte()
    {
    	checkPosition();
    	int value = readUByte(inBuf, offset);
        offset++;
        return value;        
    }    

    public int readUNibble()
    {
        int value;
        
        if(lowNibble){
            value = (inBuf[offset] & 0xF);
            offset++;
            lowNibble = false;
        } else {
            value = ((inBuf[offset] & 0xF0) >> 4);
            // don't increase offset just set lowNibble
            lowNibble = true;           
        }
        
        return value;
    }

    public String readCRTermString()
    {
    	checkPosition();
        String returnStr = "";
        while(offset < endOffset){
            byte currChar = inBuf[offset++];
            if(currChar == 0x0D)  break;

            returnStr += (char)currChar;
        }
        
        return returnStr;
    }
    
    public String readNulTermString()
    {
    	checkPosition();
        String returnStr = "";
        while(offset < endOffset){
            byte currChar = inBuf[offset++];
            if(currChar == 0x00)  break;
            if(currChar == 0xB0) {
            	returnStr += "deg";
            }
            returnStr += (char)currChar;
        }
        
        return returnStr;
    }
    
    public String readNulTermUnitString()
    {
    	checkPosition();
        String returnStr = "";
        while(offset < endOffset){
            int currChar = inBuf[offset++] & 0xFF;
            if(currChar == 0x00)  break;
            
            if(currChar == 0xB0) {
            	returnStr += "deg";
            } else if(currChar == 0xBA) {
            	returnStr += "+/-";
            } else if((currChar & 0xFF) > 127) {
            	// got unknown unit char:
            	if (devService != null) {
            		devService.log("got unknown unit char: " + currChar);
            	} else {
            		logger.warning("got unknown unit char: " + currChar);
            	}
            	returnStr += (char)currChar;
            } else {
            	returnStr += (char)currChar;
            }
        }
        
        return returnStr;
    }

    public String readFixedLengthString(int length)
    {
    	checkPosition();
        String returnStr = "";
        int i;
        for(i=0; i<length; i++) {
            byte currChar = inBuf[offset++];
            
            // break if we get a nul for some reason
            if(currChar == 0x00) {
            	i++;
            	break;
            }
        	
            returnStr += (char)currChar;            
        }
        
        // skip the rest of the bytes
        if(i < length) {
        	offset += length - i;
        }
        
        return returnStr;
    }

    public void skip(int size)
    {
    	offset += size;
    }
    
    public byte [] skipAndSave(int size)
    {
    	if(size <= 0) {
    		return null;
    	}
    	
    	byte [] newBuf = new byte [size];
    	for(int i=0; i<size; i++) {
    		newBuf[i] = inBuf[offset++];
    	}
    	
    	return newBuf;
    }
    
    public int getOffset()
    {
    	return offset;
    }
    
    private void checkPosition()
    {
        if(lowNibble) {
            throw new RuntimeException("unread low nibble");
        }    	
    }
}

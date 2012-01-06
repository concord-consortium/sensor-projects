package org.concord.sensor.pasco.datasheet;

public class ExtendedDataSheetInfo {
	int type;
	int length;
	
	public static class SupportedCommand {
		int command;
		int dataLength;
	}

	SupportedCommand [] supportedCommands;
	private byte[] localizedStringBuf;
	private byte[] englishShortNameBuf;
	
	public ExtendedDataSheetInfo(ByteBufferStreamReversed bb) {
		type = bb.readUByte();
		length = bb.readUShort();
		
		switch(type){
		case 1:
			// Sensor Command Support Extension
			int number = length / 18;
			supportedCommands = new SupportedCommand[number];
			for(int i=0; i< number; i++){
				supportedCommands[i] = new SupportedCommand();
				supportedCommands[i].command = bb.readUByte();
				supportedCommands[i].dataLength = bb.readUByte();
				// there are 16 bits of reserved space for each extended command
				bb.skip(16);
			}
			break;
		case 2:
			localizedStringBuf = bb.skipAndSave(length);
			// Localized Language String Extension
			break;
		case 3:
			englishShortNameBuf = bb.skipAndSave(length);
			// English Short Name Extension
			break;
		}
	}
	
	public int getTotalSize(){
		return length + 3;
	}
	
	protected void print(Printer p)
	{
		p.puts("ExtendedDataSheetInfo");
		p = new Printer("  ", p);
		p.puts("type: " + type);
		p.puts("length: " + length);
		switch(type){
		case 1:
			p.puts("SupportedCommands");
			p = new Printer(" ", p);
			for (SupportedCommand command : supportedCommands) {
				p.puts("command: 0x" + Integer.toHexString(command.command) + "h");
				p.puts("dataLength: " + command.dataLength);
			}
			break;
		case 2:
			p.puts("LocalizedStrings");
			p = new Printer(" ", p);
			p.printBuffer(localizedStringBuf, localizedStringBuf.length);
			break;
		case 3:
			p.puts("English Short Names");
			p = new Printer(" ", p);
			p.printBuffer(englishShortNameBuf, englishShortNameBuf.length);
			break;
		}
	}

}

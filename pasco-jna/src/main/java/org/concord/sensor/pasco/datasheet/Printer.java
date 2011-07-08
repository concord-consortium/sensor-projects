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
	
}
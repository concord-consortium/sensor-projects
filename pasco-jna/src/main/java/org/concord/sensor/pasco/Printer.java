/**
 * 
 */
package org.concord.sensor.pasco;

class Printer {
	String result = "";
	String indent;
	private Printer parent;
	Printer(String indent) {
		this.indent = indent;
	}
	
	Printer(String indent, Printer parent) {
		this.indent = indent;
		this.parent = parent;
	}

	void puts(String line) {
		if(parent != null){
			parent.puts(indent + line);
		} else {
			result += indent + line + "\n";
		}
	}
	
	void printToSysout(){
		System.out.print(result);
	}
	
}
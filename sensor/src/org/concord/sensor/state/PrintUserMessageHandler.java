/*
 * Created on Jan 12, 2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package org.concord.sensor.state;

import org.concord.framework.text.UserMessageHandler;

/**
 * @author Informaiton Services
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class PrintUserMessageHandler 
	implements UserMessageHandler
{

		/**
		 * @see org.concord.framework.text.UserMessageHandler#showOptionMessage(java.lang.String, java.lang.String, java.lang.String[], java.lang.String)
		 */
		public int showOptionMessage(String message, String title, String[] options, String defaultOption) {
			System.out.println(title + ": " + message);
			String optionStr = "(";
			for(int i=0; i<options.length; i++) {
				optionStr += " " + options[i];
				if(options[i].equals(defaultOption)){
					optionStr += "+";
				}
			}
			System.out.println(optionStr + " )");
			return 0;
		}

		/**
		 * @see org.concord.framework.text.UserMessageHandler#showMessage(java.lang.String, java.lang.String)
		 */
		public void showMessage(String message, String title) {
			System.out.println(title + ": " + message);
		}
}

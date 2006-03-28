/*
 * Last modification information:
 * $Revision: 1.1 $
 * $Date: 2006-03-28 23:03:40 $
 * $Author: scytacki $
 *
 * Licence Information
 * Copyright 2004 The Concord Consortium 
*/
package org.concord.sensor.serial;

public class SerialException extends Exception
{
    /**
     * This is reimplemented here instead of using the cause
     * support in Exception because waba doesn't have that cause
     * support.
     */
    Throwable cause;
    
    int error;
    
    public SerialException(String message){
        super(message);
    }

    public SerialException(String message, int error){
        super(message);
        this.error = error;
    }

    public SerialException(String message, Throwable cause){
        super(message);
        this.cause = cause;
    }
    
    public int getPortError(){
        return error;
    }
}

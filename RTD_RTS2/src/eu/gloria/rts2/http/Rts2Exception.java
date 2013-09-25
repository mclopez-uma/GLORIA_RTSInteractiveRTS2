package eu.gloria.rts2.http;

import eu.gloria.rt.exception.RTException;

/**
 * Rts2 base exception.
 * 
 * @author jcabello
 *
 */
public class Rts2Exception extends RTException { 

	
	public Rts2Exception(){
	} 
	
	public Rts2Exception(String message){
		super(message); 
	} 
	
	public Rts2Exception(Throwable cause) { 
		super(cause); 
	}
	
}

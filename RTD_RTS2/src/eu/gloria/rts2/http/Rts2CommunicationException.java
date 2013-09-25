package eu.gloria.rts2.http;

import eu.gloria.rt.exception.RTException;


/**
 * Represents a communication error between the RTS2 system and de java code.
 * 
 * @author jcabello
 *
 */
public class Rts2CommunicationException extends RTException {

	public Rts2CommunicationException(){ 
	} 
	
	public Rts2CommunicationException(String message){
		super(message); 
	} 
	
	public Rts2CommunicationException(Throwable cause) { 
		super(cause); 
	}
}

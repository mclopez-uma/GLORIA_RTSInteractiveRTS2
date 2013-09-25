package eu.gloria.rts2.http;

import eu.gloria.rt.exception.RTException;
import eu.gloria.tools.log.LogUtil;
import eu.gloria.tools.thread.context.ContextSynchronizer;

/**
 * Rts2 Messages Synchronizer.
 * @author jcabello
 *
 */
public class Rts2SynchronizerMessage extends ContextSynchronizer {
	
	private long rts2StartTime;
	private Rts2MessageType messageType;
	private String deviceId;
	private boolean throwError;
	private String msgToSearch;

	public Rts2SynchronizerMessage(long waitTime, long timeout, Rts2MessageType messageType, String deviceId, boolean throwError, String msgToSearch) {
		
		super(waitTime, timeout);
		
		this.rts2StartTime = Rts2Date.now();
		this.messageType = messageType;
		this.deviceId = deviceId;
		this.throwError = throwError;
		this.msgToSearch = msgToSearch;
		
	}
	
	public void takeRts2Time(){
		this.rts2StartTime = Rts2Date.now();
		LogUtil.info(this, "Rts2SynchronizerMessage. Initial RTS2Time=" + this.rts2StartTime);
	}

	@Override
	public boolean isValidContext() throws RTException {
		
		String message;
		
		String[] names = {
				"deviceId"
		};
		
		String[] values = {
				deviceId
		};
		
		LogUtil.info(this, "CameraRTD.isValidContext(). Input=" + LogUtil.getLog(names, values));
		
		//Message recovering..Errors
		if (throwError){
			message = Rts2Messages.getMessageText (Rts2MessageType.error, deviceId, rts2StartTime);
			LogUtil.info(this, "CameraRTD.isValidContext() Rts2MessageError=" + message);
			if (message != null)
				throw new RTException(message);	
		}
		
		if (msgToSearch != null){
			message = Rts2Messages.getMessageText (messageType, deviceId, rts2StartTime);
			LogUtil.info(this, "CameraRTD.isValidContext() Rts2MessageInfo=" + message);
			if (message!= null && message.indexOf(msgToSearch) != -1){
				return true;
			}
		}
		
		
		return false;
	}

}

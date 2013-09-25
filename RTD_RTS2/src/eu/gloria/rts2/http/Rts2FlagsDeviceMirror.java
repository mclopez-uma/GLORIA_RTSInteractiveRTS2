package eu.gloria.rts2.http;

import eu.gloria.rt.entity.device.ActivityStateMirror;

/**
 * Wraps all business logic related to a Mirror device flag.
 * 
 * @author jcabello
 *
 */
public class Rts2FlagsDeviceMirror extends Rts2FlagsDevice {
	
	/**
	 * Activity state
	 */
	private ActivityStateMirror activityState;

	/**
	 * Constructor 
	 * @param flags Rts2 flags
	 * @throws Rts2Exception In error case
	 */
	public Rts2FlagsDeviceMirror(Long flags) throws Rts2Exception {
		super(flags);
		resolveActivityState();
	}
	
	/**
	 * Resolves the activity state analyzing the flags .
	 * @throws Rts2Exception In error case
	 */
	private void resolveActivityState() throws Rts2Exception{
		
		//By default -> OFF
		activityState = ActivityStateMirror.OFF;

		//Checking  MISC STATE
		if (miscValueToCompare == Rts2Constants.RTS2_DEVICE_FLAG_MISCELLANEOUS_SC_CURR){
			activityState = ActivityStateMirror.BUSY;
		}else if (miscValueToCompare == Rts2Constants.RTS2_DEVICE_FLAG_MISCELLANEOUS_NEED_RELOAD){
			activityState = ActivityStateMirror.BUSY;
		}else if (miscValueToCompare == Rts2Constants.RTS2_DEVICE_FLAG_MISCELLANEOUS_STARTUP){
			activityState = ActivityStateMirror.BUSY;
		}else if (miscValueToCompare == Rts2Constants.RTS2_DEVICE_FLAG_MISCELLANEOUS_SHUTDOWN){
			activityState = ActivityStateMirror.BUSY;
		}else if (miscValueToCompare == Rts2Constants.RTS2_DEVICE_FLAG_MISCELLANEOUS_IDLE){
			activityState = ActivityStateMirror.READY;
		}
				
		//Checking  ERROR STATE
		if (errorValueToCompare == Rts2Constants.RTS2_DEVICE_FLAG_ERROR_NO){
			activityState = ActivityStateMirror.READY;
			statusDesc.add("The device is not in error.");
		}else if (errorValueToCompare == Rts2Constants.RTS2_DEVICE_FLAG_ERROR_KILL){
			activityState = ActivityStateMirror.OFF;
			statusDesc.add("The device is off.");
		}else if (errorValueToCompare == Rts2Constants.RTS2_DEVICE_FLAG_ERROR_HW){
			activityState = ActivityStateMirror.ERROR;
			statusDesc.add("There is a HW error.");
		}else if (errorValueToCompare == Rts2Constants.RTS2_DEVICE_FLAG_ERROR_NOT_READY){
			activityState = ActivityStateMirror.BUSY;
			statusDesc.add("The device is not ready.");
		}			
		
		
		//Additional information 
		long movementValueToCompare = (flags & Rts2Constants.RTS2_DEVICE_MIRROR_FLAG_MOVEMENT_MASK);
		if (movementValueToCompare == Rts2Constants.RTS2_DEVICE_MIRROR_FLAG_MOVEMENT_MOVE){
			activityState = ActivityStateMirror.MOVING;
			statusDesc.add("The Mirror is moving.");
		}
	
				
	}
	

	/**
	 * Access method.
	 * @return value
	 */
	public ActivityStateMirror getActivityState() {
		return activityState;
	}

}

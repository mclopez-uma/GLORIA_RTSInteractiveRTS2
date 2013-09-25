package eu.gloria.rts2.http;

import eu.gloria.rt.entity.device.ActivityStateCamera;
import eu.gloria.rt.entity.device.ActivityStateFocuser;


/**
 * Wraps all business logic related to a Focuser device flag.
 * 
 * @author jcabello
 *
 */
public class Rts2FlagsDeviceFocuser extends Rts2FlagsDevice {
	
	/**
	 * Activity state
	 */
	private ActivityStateFocuser activityState;

	/**
	 * Constructor
	 * @param flags Flags
	 * @throws Rts2Exception In error case
	 */
	public Rts2FlagsDeviceFocuser(Long flags) throws Rts2Exception {
		super(flags);
		
		resolveActivityState();
	}
	
	/**
	 * Resolves the activity state analyzing the flags .
	 * @throws Rts2Exception In error case
	 */
	private void resolveActivityState() throws Rts2Exception{
		
		//By default -> OFF
		activityState = ActivityStateFocuser.OFF;
		
		//Checking  MISC STATE
		if (miscValueToCompare == Rts2Constants.RTS2_DEVICE_FLAG_MISCELLANEOUS_SC_CURR){
			activityState = ActivityStateFocuser.BUSY;
		}else if (miscValueToCompare == Rts2Constants.RTS2_DEVICE_FLAG_MISCELLANEOUS_NEED_RELOAD){
			activityState = ActivityStateFocuser.BUSY;
		}else if (miscValueToCompare == Rts2Constants.RTS2_DEVICE_FLAG_MISCELLANEOUS_STARTUP){
			activityState = ActivityStateFocuser.BUSY;
		}else if (miscValueToCompare == Rts2Constants.RTS2_DEVICE_FLAG_MISCELLANEOUS_SHUTDOWN){
			activityState = ActivityStateFocuser.BUSY;
		}else if (miscValueToCompare == Rts2Constants.RTS2_DEVICE_FLAG_MISCELLANEOUS_IDLE){
			activityState = ActivityStateFocuser.READY;
		}
		
		//Checking  ERROR STATE
		if (errorValueToCompare == Rts2Constants.RTS2_DEVICE_FLAG_ERROR_NO){
			activityState = ActivityStateFocuser.READY;
			statusDesc.add("The device is not in error.");
		}else if (errorValueToCompare == Rts2Constants.RTS2_DEVICE_FLAG_ERROR_KILL){
			activityState = ActivityStateFocuser.OFF;
			statusDesc.add("The device is off.");
		}else if (errorValueToCompare == Rts2Constants.RTS2_DEVICE_FLAG_ERROR_HW){
			activityState = ActivityStateFocuser.ERROR;
			statusDesc.add("There is a HW error.");
		}else if (errorValueToCompare == Rts2Constants.RTS2_DEVICE_FLAG_ERROR_NOT_READY){
			activityState = ActivityStateFocuser.BUSY;
			statusDesc.add("The device is not ready.");
		}			
		
		
		//Additional information (READING, EXPOSING)
		if ((flags & Rts2Constants.RTS2_DEVICE_FOCUSER_FLAG_FOCUSSING_MASK) == Rts2Constants.RTS2_DEVICE_FOCUSER_FLAG_FOCUSSING){
			activityState = ActivityStateFocuser.FOCUSING;
			statusDesc.add("The FOCUSER is EXPOSSING.");
		}
		
	}
	
	/**
	 * Access method.
	 * @return value
	 */
	public ActivityStateFocuser getActivityState() {
		return activityState;
	}

}

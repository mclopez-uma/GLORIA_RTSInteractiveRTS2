package eu.gloria.rts2.http;

import eu.gloria.rt.entity.device.ActivityStateRotator;

/**
 * 
 * Wraps all business logic related to a Rotator device flag.
 * 
 * @author mclopez
 *
 */
public class Rts2FlagsDeviceRotator extends Rts2FlagsDevice {

	
	/**
	 * Activity state
	 */
	private ActivityStateRotator activityState;
	
	
	/**
	 * Constructor
	 * @param flags Flags
	 * @throws Rts2Exception In error case
	 */
	public Rts2FlagsDeviceRotator(Long flags) throws Rts2Exception {
		super(flags);
		
		resolveActivityState();
	}

	
	/**
	 * Resolves the activity state analyzing the flags .
	 * @throws Rts2Exception In error case
	 */
	private void resolveActivityState() throws Rts2Exception{
		
		//By default -> OFF
		activityState = ActivityStateRotator.OFF;
		
		//Checking  MISC STATE
		if (miscValueToCompare == Rts2Constants.RTS2_DEVICE_FLAG_MISCELLANEOUS_SC_CURR){
			activityState = ActivityStateRotator.BUSY;
		}else if (miscValueToCompare == Rts2Constants.RTS2_DEVICE_FLAG_MISCELLANEOUS_NEED_RELOAD){
			activityState = ActivityStateRotator.BUSY;
		}else if (miscValueToCompare == Rts2Constants.RTS2_DEVICE_FLAG_MISCELLANEOUS_STARTUP){
			activityState = ActivityStateRotator.BUSY;
		}else if (miscValueToCompare == Rts2Constants.RTS2_DEVICE_FLAG_MISCELLANEOUS_SHUTDOWN){
			activityState = ActivityStateRotator.BUSY;
		}else if (miscValueToCompare == Rts2Constants.RTS2_DEVICE_FLAG_MISCELLANEOUS_IDLE){
			activityState = ActivityStateRotator.READY;
		}
				
		//Checking  ERROR STATE
		if (errorValueToCompare == Rts2Constants.RTS2_DEVICE_FLAG_ERROR_NO){
			activityState = ActivityStateRotator.READY;
			statusDesc.add("The device is not in error.");
		}else if (errorValueToCompare == Rts2Constants.RTS2_DEVICE_FLAG_ERROR_KILL){
			activityState = ActivityStateRotator.OFF;
			statusDesc.add("The device is off.");
		}else if (errorValueToCompare == Rts2Constants.RTS2_DEVICE_FLAG_ERROR_HW){
			activityState = ActivityStateRotator.ERROR;
			statusDesc.add("There is a HW error.");
		}else if (errorValueToCompare == Rts2Constants.RTS2_DEVICE_FLAG_ERROR_NOT_READY){
			activityState = ActivityStateRotator.BUSY;
			statusDesc.add("The device is not ready.");
		}				
		
		
		//Additional information (READING, EXPOSING)
		if ((flags & Rts2Constants.RTS2_DEVICE_ROTATOR_FLAG_ROTATING_MASK) == Rts2Constants.RTS2_DEVICE_ROTATOR_FLAG_ROTATING){
			activityState = ActivityStateRotator.ROTATING;
			statusDesc.add("The ROTATOR is ROTATING.");
		}
		
	}

	/**
	 * Access method.
	 * @return value
	 */
	public ActivityStateRotator getActivityState() {
		return activityState;
	}
	
	
}

package eu.gloria.rts2.http;

import eu.gloria.rt.entity.device.ActivityStateFilter;

/**
 * Wraps all business logic related to a Filter device flag.
 * 
 * @author jcabello
 *
 */
public class Rts2FlagsDeviceFilter extends Rts2FlagsDevice {
	
	/**
	 * Activity state
	 */
	private ActivityStateFilter activityState;

	/**
	 * Constructor
	 * @param flags Rts2 flags
	 * @throws Rts2Exception In error case
	 */
	public Rts2FlagsDeviceFilter(Long flags) throws Rts2Exception {
		super(flags);
		resolveActivityState();
	}
	
	/**
	 * Resolves the activity state analyzing the flags .
	 * @throws Rts2Exception In error case
	 */
	private void resolveActivityState() throws Rts2Exception{
		
		//By default -> OFF
		activityState = ActivityStateFilter.OFF;
		
		//Checking  MISC STATE
		if (miscValueToCompare == Rts2Constants.RTS2_DEVICE_FLAG_MISCELLANEOUS_SC_CURR){
			activityState = ActivityStateFilter.BUSY;
		}else if (miscValueToCompare == Rts2Constants.RTS2_DEVICE_FLAG_MISCELLANEOUS_NEED_RELOAD){
			activityState = ActivityStateFilter.BUSY;
		}else if (miscValueToCompare == Rts2Constants.RTS2_DEVICE_FLAG_MISCELLANEOUS_STARTUP){
			activityState = ActivityStateFilter.BUSY;
		}else if (miscValueToCompare == Rts2Constants.RTS2_DEVICE_FLAG_MISCELLANEOUS_SHUTDOWN){
			activityState = ActivityStateFilter.BUSY;
		}else if (miscValueToCompare == Rts2Constants.RTS2_DEVICE_FLAG_MISCELLANEOUS_IDLE){
			activityState = ActivityStateFilter.READY;
		}
				
		//Checking  ERROR STATE
		if (errorValueToCompare == Rts2Constants.RTS2_DEVICE_FLAG_ERROR_NO){
			activityState = ActivityStateFilter.READY;
			statusDesc.add("The device is not in error.");
		}else if (errorValueToCompare == Rts2Constants.RTS2_DEVICE_FLAG_ERROR_KILL){
			activityState = ActivityStateFilter.OFF;
			statusDesc.add("The device is off.");
		}else if (errorValueToCompare == Rts2Constants.RTS2_DEVICE_FLAG_ERROR_HW){
			activityState = ActivityStateFilter.ERROR;
			statusDesc.add("There is a HW error.");
		}else if (errorValueToCompare == Rts2Constants.RTS2_DEVICE_FLAG_ERROR_NOT_READY){
			activityState = ActivityStateFilter.BUSY;
			statusDesc.add("The device is not ready.");
		}			
		
		
		//Additional information 
		long movementValueToCompare = (flags & Rts2Constants.RTS2_DEVICE_FILTER_FLAG_MOVEMENT_MASK);
		if (movementValueToCompare == Rts2Constants.RTS2_DEVICE_FILTER_FLAG_MOVEMENT_MOVE){
			activityState = ActivityStateFilter.MOVING;
			statusDesc.add("The Mount is moving.");
		}else if (movementValueToCompare == Rts2Constants.RTS2_DEVICE_FILTER_FLAG_MOVEMENT_IDLE){
			activityState = ActivityStateFilter.READY;
			statusDesc.add("The Mount is ready.");
		}
		
		
		
	}
	

	/**
	 * Access method.
	 * @return value
	 */
	public ActivityStateFilter getActivityState() {
		return activityState;
	}

}

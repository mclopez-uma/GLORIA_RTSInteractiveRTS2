package eu.gloria.rts2.http;

import eu.gloria.rt.entity.device.ActivityState;
import eu.gloria.rt.entity.device.AlarmState;

/**
 * Wraps all business logic related to a general device flag.
 * 
 * @author jcabello
 *
 */
public class Rts2FlagsDeviceGeneral extends Rts2FlagsDevice {
	
	/**
	 * Activity state
	 */
	private ActivityState activityState;

	/**
	 * Constructor
	 * @param flags Device state flags
	 * @throws Rts2Exception In error case
	 */
	public Rts2FlagsDeviceGeneral(Long flags) throws Rts2Exception {
		
		super(flags);
		
		resolveActivityState();
	}
	
	/**
	 * Resolves the activity state analyzing the flags .
	 * @throws Rts2Exception In error case
	 */
	private void resolveActivityState() throws Rts2Exception{
		
		//By default --> OFF
		activityState = ActivityState.OFF;
		
		//Checking  MISC STATE
		if (miscValueToCompare == Rts2Constants.RTS2_DEVICE_FLAG_MISCELLANEOUS_SC_CURR){
			activityState = ActivityState.BUSY;
			statusDesc.add("The state is changing");
		}else if (miscValueToCompare == Rts2Constants.RTS2_DEVICE_FLAG_MISCELLANEOUS_NEED_RELOAD){
			activityState = ActivityState.BUSY;
			statusDesc.add("The device must be reloaded.");
		}else if (miscValueToCompare == Rts2Constants.RTS2_DEVICE_FLAG_MISCELLANEOUS_STARTUP){
			activityState = ActivityState.BUSY;
			statusDesc.add("The device is starting up.");
		}else if (miscValueToCompare == Rts2Constants.RTS2_DEVICE_FLAG_MISCELLANEOUS_SHUTDOWN){
			activityState = ActivityState.BUSY;
			statusDesc.add("The device is shutdown.");
		}else if (miscValueToCompare == Rts2Constants.RTS2_DEVICE_FLAG_MISCELLANEOUS_IDLE){
			activityState = ActivityState.READY;
			statusDesc.add("The device is idle.");
		}
				
		//Checking  ERROR STATE
		if (errorValueToCompare == Rts2Constants.RTS2_DEVICE_FLAG_ERROR_NO){
			activityState = ActivityState.READY;
		}else if (errorValueToCompare == Rts2Constants.RTS2_DEVICE_FLAG_ERROR_KILL){
			activityState = ActivityState.OFF;
		}else if (errorValueToCompare == Rts2Constants.RTS2_DEVICE_FLAG_ERROR_HW){
			activityState = ActivityState.ERROR;
		}else if (errorValueToCompare == Rts2Constants.RTS2_DEVICE_FLAG_ERROR_NOT_READY){
			activityState = ActivityState.BUSY;
		}	
		
		//Checking WEATHER STATE
//		if ((flags & Rts2Constants.RTS2_WEATHER_MASK) == Rts2Constants.BAD_WEATHER)
//			activityState = ActivityState.BAD_WEATHER;
				
		
	}

	/**
	 * Access method
	 * @return value
	 */
	public ActivityState getActivityState() {
		return activityState;
	}

	/**
	 * Access method.
	 * @param activityState value
	 */
	public void setActivityState(ActivityState activityState) {
		this.activityState = activityState;
	}

}

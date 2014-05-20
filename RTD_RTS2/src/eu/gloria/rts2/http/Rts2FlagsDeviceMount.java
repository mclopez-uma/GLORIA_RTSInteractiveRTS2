package eu.gloria.rts2.http;

import eu.gloria.rt.entity.device.ActivityStateMount;

/**
 * Wraps all business logic related to a Mount device flag.
 * 
 * @author jcabello
 *
 */
public class Rts2FlagsDeviceMount extends Rts2FlagsDevice  {
	
	/**
	 * Activity state
	 */
	private ActivityStateMount activityState;
	
	/**
	 * Constructor
	 * @param flags Rts2 flags
	 * @throws Rts2Exception In error case
	 */
	public Rts2FlagsDeviceMount(Long flags) throws Rts2Exception {
		
		super(flags);
		resolveActivityState();
			
	}
	
	/**
	 * Resolves the activity state analyzing the flags .
	 * @throws Rts2Exception In error case
	 */
	private void resolveActivityState() throws Rts2Exception{
		
		//By default -> OFF
		activityState = ActivityStateMount.OFF;
		
				
		//Checking  ERROR STATE
		if (errorValueToCompare == Rts2Constants.RTS2_DEVICE_FLAG_ERROR_NO){
			activityState = ActivityStateMount.READY;
			statusDesc.add("The device is not in error.");
		}else if (errorValueToCompare == Rts2Constants.RTS2_DEVICE_FLAG_ERROR_KILL){
			activityState = ActivityStateMount.OFF;
			statusDesc.add("The device is off.");
		}else if (errorValueToCompare == Rts2Constants.RTS2_DEVICE_FLAG_ERROR_HW){
			activityState = ActivityStateMount.ERROR;
			statusDesc.add("There is a HW error.");
		}else if (errorValueToCompare == Rts2Constants.RTS2_DEVICE_FLAG_ERROR_NOT_READY){
			activityState = ActivityStateMount.BUSY;
			statusDesc.add("The device is not ready.");
		}		
		
		if (activityState!=ActivityStateMount.ERROR){
			//Checking  MISC STATE
			if (miscValueToCompare == Rts2Constants.RTS2_DEVICE_FLAG_MISCELLANEOUS_SC_CURR){
				activityState = ActivityStateMount.BUSY;
			}else if (miscValueToCompare == Rts2Constants.RTS2_DEVICE_FLAG_MISCELLANEOUS_NEED_RELOAD){
				activityState = ActivityStateMount.BUSY;
			}else if (miscValueToCompare == Rts2Constants.RTS2_DEVICE_FLAG_MISCELLANEOUS_STARTUP){
				activityState = ActivityStateMount.BUSY;
			}else if (miscValueToCompare == Rts2Constants.RTS2_DEVICE_FLAG_MISCELLANEOUS_SHUTDOWN){
				activityState = ActivityStateMount.BUSY;
			}else if (miscValueToCompare == Rts2Constants.RTS2_DEVICE_FLAG_MISCELLANEOUS_IDLE){
				activityState = ActivityStateMount.READY;
			}					


			//Additional information (OBSERVING, MOVING, PARKED, PARKING). The CUP will not be taken into account (consider as another device)
			long movementValueToCompare = (flags & Rts2Constants.RTS2_DEVICE_MOUNT_FLAG_MOVEMENT_MASK);
			if (movementValueToCompare == Rts2Constants.RTS2_DEVICE_MOUNT_FLAG_MOVEMENT_OBSERVING){
				activityState = ActivityStateMount.STOP;
				statusDesc.add("The Mount is stopped.");
			}else if (movementValueToCompare == Rts2Constants.RTS2_DEVICE_MOUNT_FLAG_MOVEMENT_MOVING){
				activityState = ActivityStateMount.MOVING;
				statusDesc.add("The Mount is moving.");
			}else if (movementValueToCompare == Rts2Constants.RTS2_DEVICE_MOUNT_FLAG_MOVEMENT_PARKED){
				activityState = ActivityStateMount.PARKED;
				statusDesc.add("The Mount is parked.");
			}else if (movementValueToCompare == Rts2Constants.RTS2_DEVICE_MOUNT_FLAG_MOVEMENT_PARKING){
				activityState = ActivityStateMount.PARKING;
				statusDesc.add("The Mount is parking.");
			}

			//TRACKING... -> It is controlled by a mount property. RTS2 doesn't use this flag
//			long trackValueToCompare = (flags & Rts2Constants.RTS2_DEVICE_MOUNT_FLAG_TRACK_MASK);
//			if (trackValueToCompare == Rts2Constants.RTS2_DEVICE_MOUNT_FLAG_TRACK_TRACKING){
//				activityState = ActivityStateMount.TRACKING;
//				statusDesc.add("The Mount is tracking.");
//			}
		}
				
	}
	

	/**
	 * Access method.
	 * @return value
	 */
	public ActivityStateMount getActivityState() {
		return activityState;
	}


}

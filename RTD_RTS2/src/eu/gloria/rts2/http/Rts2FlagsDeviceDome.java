package eu.gloria.rts2.http;

import eu.gloria.rt.entity.device.ActivityStateDome;
import eu.gloria.rt.entity.device.ActivityStateDomeOpening;
import eu.gloria.rt.entity.device.ActivityStateMount;

/**
 * Wraps all business logic related to a Dome device flag.
 * 
 * @author jcabello
 *
 */
public class Rts2FlagsDeviceDome extends Rts2FlagsDevice {
	
	/**
	 * Activity state
	 */
	private ActivityStateDome activityState;
	
	/**
	 * Opening state
	 */
	private ActivityStateDomeOpening activityStateOpening;
	
	
	/**
	 * Synchrnized flag
	 */
	private boolean synchorized;

	/**
	 * Constructor
	 * @param flags RTS2 flags
	 * @throws Rts2Exception In error
	 */
	public Rts2FlagsDeviceDome(Long flags) throws Rts2Exception {
		super(flags);
		
		resolveActivityState();
		
		resolveActivityOpeningState();
		
		resolveSynchronize();
		
	}
	
	/**
	 * Resolves the synchronization with the mount.
	 * @throws Rts2Exception In error case
	 */
	private void resolveSynchronize() throws Rts2Exception{
	
		long movementValueToCompare = (flags & Rts2Constants.RTS2_DEVICE_DOME_FLAG_SYNCH_MASK);
		if (movementValueToCompare == Rts2Constants.RTS2_DEVICE_DOME_FLAG_SYNCH_SYNCH){
			synchorized = true;
		}else if (movementValueToCompare == Rts2Constants.RTS2_DEVICE_DOME_FLAG_SYNCH_NOT_SYNCH){
			synchorized = false;
		} else{
			throw new Rts2Exception("[DOME]. SYNCHRONIZATION unknown.");
		}
		
	}
	
	/**
	 * Resolves the activity state analyzing the flags .
	 * @throws Rts2Exception In error case
	 */
	private void resolveActivityState() throws Rts2Exception{
		
		//By default-> OFF
		activityState = ActivityStateDome.OFF;
		
		//Checking  MISC STATE
		if (miscValueToCompare == Rts2Constants.RTS2_DEVICE_FLAG_MISCELLANEOUS_SC_CURR){
			activityState = ActivityStateDome.BUSY;
		}else if (miscValueToCompare == Rts2Constants.RTS2_DEVICE_FLAG_MISCELLANEOUS_NEED_RELOAD){
			activityState = ActivityStateDome.BUSY;
		}else if (miscValueToCompare == Rts2Constants.RTS2_DEVICE_FLAG_MISCELLANEOUS_STARTUP){
			activityState = ActivityStateDome.BUSY;
		}else if (miscValueToCompare == Rts2Constants.RTS2_DEVICE_FLAG_MISCELLANEOUS_SHUTDOWN){
			activityState = ActivityStateDome.BUSY;
		}else if (miscValueToCompare == Rts2Constants.RTS2_DEVICE_FLAG_MISCELLANEOUS_IDLE){
			activityState = ActivityStateDome.READY;
		}

		//Checking  ERROR STATE
		if (errorValueToCompare == Rts2Constants.RTS2_DEVICE_FLAG_ERROR_NO){
			activityState = ActivityStateDome.READY;
			statusDesc.add("The device is not in error.");
		}else if (errorValueToCompare == Rts2Constants.RTS2_DEVICE_FLAG_ERROR_KILL){
			activityState = ActivityStateDome.OFF;
			statusDesc.add("The device is off.");
		}else if (errorValueToCompare == Rts2Constants.RTS2_DEVICE_FLAG_ERROR_HW){
			activityState = ActivityStateDome.ERROR;
			statusDesc.add("There is a HW error.");
		}else if (errorValueToCompare == Rts2Constants.RTS2_DEVICE_FLAG_ERROR_NOT_READY){
			activityState = ActivityStateDome.BUSY;
			statusDesc.add("The device is not ready.");
		}				
		
		
		//Additional information (OBSERVING, MOVING, PARKED, PARKING). The CUP will not be take into account (consider as another device)
		long movementValueToCompare = (flags & Rts2Constants.RTS2_DEVICE_DOME_FLAG_MOVEMENT_MASK);
		if (movementValueToCompare == Rts2Constants.RTS2_DEVICE_DOME_FLAG_MOVEMENT_MOVING){
			activityState = ActivityStateDome.MOVING;
			statusDesc.add("The Dome is moving.");
		} else if (movementValueToCompare == Rts2Constants.RTS2_DEVICE_DOME_FLAG_MOVEMENT_NOT_MOVING){
			activityState = ActivityStateDome.STOP;
			statusDesc.add("The Dome is stop.");
		}
		
	}
	
	/**
	 * Resolves the activity state analyzing the flags .
	 * @throws Rts2Exception In error case
	 */
	private void resolveActivityOpeningState() throws Rts2Exception{
		
		//By default-> OFF
		activityStateOpening = ActivityStateDomeOpening.OFF;
		
		//Checking  ERROR STATE
		if (errorValueToCompare == Rts2Constants.RTS2_DEVICE_FLAG_ERROR_NO){
			activityStateOpening = ActivityStateDomeOpening.READY;
			statusDesc.add("The device is not in error.");
		}else if (errorValueToCompare == Rts2Constants.RTS2_DEVICE_FLAG_ERROR_KILL){
			activityStateOpening = ActivityStateDomeOpening.OFF;
			statusDesc.add("The device is off.");
		}else if (errorValueToCompare == Rts2Constants.RTS2_DEVICE_FLAG_ERROR_HW){
			activityStateOpening = ActivityStateDomeOpening.ERROR;
			statusDesc.add("There is a HW error.");
		}else if (errorValueToCompare == Rts2Constants.RTS2_DEVICE_FLAG_ERROR_NOT_READY){
			activityStateOpening = ActivityStateDomeOpening.BUSY;
			statusDesc.add("The device is not ready.");
		}
				
		//Checking  MISC STATE
		if (miscValueToCompare == Rts2Constants.RTS2_DEVICE_FLAG_MISCELLANEOUS_SC_CURR){
			activityStateOpening = ActivityStateDomeOpening.BUSY;
		}else if (miscValueToCompare == Rts2Constants.RTS2_DEVICE_FLAG_MISCELLANEOUS_NEED_RELOAD){
			activityStateOpening = ActivityStateDomeOpening.BUSY;
		}else if (miscValueToCompare == Rts2Constants.RTS2_DEVICE_FLAG_MISCELLANEOUS_STARTUP){
			activityStateOpening = ActivityStateDomeOpening.BUSY;
		}else if (miscValueToCompare == Rts2Constants.RTS2_DEVICE_FLAG_MISCELLANEOUS_SHUTDOWN){
			activityStateOpening = ActivityStateDomeOpening.BUSY;
		}else if (miscValueToCompare == Rts2Constants.RTS2_DEVICE_FLAG_MISCELLANEOUS_IDLE){
			activityStateOpening = ActivityStateDomeOpening.READY;
		}
		
		//Additional information (UNKNOWN, CLOSE, OPEN, CLOSING, OPENING). 
		long movementOpeningValueToCompare = (flags & Rts2Constants.RTS2_DEVICE_DOME_FLAG_OPEN_MASK);
		if (movementOpeningValueToCompare == Rts2Constants.RTS2_DEVICE_DOME_FLAG_OPEN_OPENED){
			activityStateOpening = ActivityStateDomeOpening.OPEN;
			statusDesc.add("The Dome is open.");
		} else if (movementOpeningValueToCompare == Rts2Constants.RTS2_DEVICE_DOME_FLAG_OPEN_CLOSED){
			activityStateOpening = ActivityStateDomeOpening.CLOSE;
			statusDesc.add("The Dome is close.");
		} else if (movementOpeningValueToCompare == Rts2Constants.RTS2_DEVICE_DOME_FLAG_OPEN_OPENING){
			activityStateOpening = ActivityStateDomeOpening.OPENING;
			statusDesc.add("The Dome is opening.");
		} else if (movementOpeningValueToCompare == Rts2Constants.RTS2_DEVICE_DOME_FLAG_OPEN_CLOSING){
			activityStateOpening = ActivityStateDomeOpening.CLOSING;
			statusDesc.add("The Dome is closing.");
		} else if (movementOpeningValueToCompare == Rts2Constants.RTS2_DEVICE_DOME_FLAG_OPEN_UNKNOW){
			activityStateOpening = ActivityStateDomeOpening.ERROR;
			statusDesc.add("The Dome is in unknown state.");
		} 
		
	}

	/**
	 * Access method
	 * @return Value
	 */
	public ActivityStateDome getActivityState() {
		return activityState;
	}

	/**
	 * Access method
	 * @param activityState value
	 */
	public void setActivityState(ActivityStateDome activityState) {
		this.activityState = activityState;
	}

	/**
	 * Access method
	 * @return value
	 */
	public ActivityStateDomeOpening getActivityStateOpening() {
		return activityStateOpening;
	}

	/**
	 * Acess method
	 * @param activityStateOpening value
	 */
	public void setActivityStateOpening(
			ActivityStateDomeOpening activityStateOpening) {
		this.activityStateOpening = activityStateOpening;
	}
	
	/**
	 * Access method
	 * @return value
	 */
	public boolean isSynchronized(){
		return this.synchorized;
	}

}

package eu.gloria.rts2.http;

import eu.gloria.rt.entity.device.ActivityStateCamera;

/**
 * Wraps all business logic related to a CCD device flag.
 * 
 * @author jcabello
 *
 */
public class Rts2FlagsDeviceCCD extends Rts2FlagsDevice {
	
	/**
	 * Activity state
	 */
	private ActivityStateCamera activityState;
	
	/**
	 * Flag indicating if the camera has an image.
	 */
	private boolean hasImage;

	/**
	 * Constructor
	 * @param flags Device state flags
	 * @throws Rts2Exception In error case
	 */
	public Rts2FlagsDeviceCCD(Long flags) throws Rts2Exception {
		
		super(flags);
		
		resolveActivityState();
		
		resolveHasImage();
		
	}
	
	/**
	 * Resolves if the camera has an image .
	 * @throws Rts2Exception In error case
	 */
	private void resolveHasImage() throws Rts2Exception{
		
		long hasImageValueToCompare = (flags & Rts2Constants.RTS2_DEVICE_CCD_FLAG_HAS_IMG_MASK);
		hasImage = (hasImageValueToCompare == Rts2Constants.RTS2_DEVICE_CCD_FLAG_HAS_IMG);
	}
	
	/**
	 * Resolves the activity state analyzing the flags .
	 * @throws Rts2Exception In error case
	 */
	private void resolveActivityState() throws Rts2Exception{
		
		//By default ->OFF
		activityState = ActivityStateCamera.OFF;
		
		//Checking  MISC STATE
		if (miscValueToCompare == Rts2Constants.RTS2_DEVICE_FLAG_MISCELLANEOUS_SC_CURR){
			activityState = ActivityStateCamera.BUSY;
		}else if (miscValueToCompare == Rts2Constants.RTS2_DEVICE_FLAG_MISCELLANEOUS_NEED_RELOAD){
			activityState = ActivityStateCamera.BUSY;
		}else if (miscValueToCompare == Rts2Constants.RTS2_DEVICE_FLAG_MISCELLANEOUS_STARTUP){
			activityState = ActivityStateCamera.BUSY;
		}else if (miscValueToCompare == Rts2Constants.RTS2_DEVICE_FLAG_MISCELLANEOUS_SHUTDOWN){
			activityState = ActivityStateCamera.BUSY;
		}else if (miscValueToCompare == Rts2Constants.RTS2_DEVICE_FLAG_MISCELLANEOUS_IDLE){
			activityState = ActivityStateCamera.READY;
		}
				
		//Checking  ERROR STATE
		if (errorValueToCompare == Rts2Constants.RTS2_DEVICE_FLAG_ERROR_NO){
			activityState = ActivityStateCamera.READY;
			statusDesc.add("The device is not in error.");
		}else if (errorValueToCompare == Rts2Constants.RTS2_DEVICE_FLAG_ERROR_KILL){
//			activityState = ActivityStateCamera.OFF;
			activityState = ActivityStateCamera.READY;
			statusDesc.add("Exposure interrupted.");
		}else if (errorValueToCompare == Rts2Constants.RTS2_DEVICE_FLAG_ERROR_HW){
			activityState = ActivityStateCamera.ERROR;
			statusDesc.add("There is a HW error.");
		}else if (errorValueToCompare == Rts2Constants.RTS2_DEVICE_FLAG_ERROR_NOT_READY){
			activityState = ActivityStateCamera.BUSY;
			statusDesc.add("The device is not ready.");
		}	
		
		
		//Additional information (READING, EXPOSING)
		if ((flags & Rts2Constants.RTS2_DEVICE_CCD_FLAG_EXPOSING_MASK) == Rts2Constants.RTS2_DEVICE_CCD_FLAG_EXPOSING){
			activityState = ActivityStateCamera.EXPOSING;
			statusDesc.add("The CCD is EXPOSSING.");
		}else if ((flags & Rts2Constants.RTS2_DEVICE_CCD_FLAG_READING_MASK) == Rts2Constants.RTS2_DEVICE_CCD_FLAG_READING){
			activityState = ActivityStateCamera.READING;
			statusDesc.add("The CCD is READING.");
		}else if ((flags & Rts2Constants.RTS2_DEVICE_CCD_FLAG_SHUTTER_MASK) == Rts2Constants.RTS2_DEVICE_CCD_FLAG_SHUTTER_SET){
			statusDesc.add("SHUTTER SET.");
		}else if ((flags & Rts2Constants.RTS2_DEVICE_CCD_FLAG_SHUTTER_MASK) == Rts2Constants.RTS2_DEVICE_CCD_FLAG_SHUTTER_TRANS){
			statusDesc.add("SHUTTER TRANS.");
		}else if ((flags & Rts2Constants.RTS2_DEVICE_CCD_FLAG_FOCUSSING_MASK) == Rts2Constants.RTS2_DEVICE_CCD_FLAG_FOCUSSING){
			statusDesc.add("The CCD is focussing.");
		}
		
	}

	/**
	 * Access method.
	 * @return value
	 */
	public ActivityStateCamera getActivityState() {
		return activityState;
	}

	/**
	 * Access method.
	 * @param activityState value
	 */
	public void setActivityState(ActivityStateCamera activityState) {
		this.activityState = activityState;
	}
	
	/**
	 * Access method
	 * @return value
	 */
	public boolean hasImage(){
		return this.hasImage;
	}

}

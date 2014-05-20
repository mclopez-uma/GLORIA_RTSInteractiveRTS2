package eu.gloria.rts2.rtd;

import java.util.ArrayList;
import java.util.List;

import eu.gloria.rt.entity.device.ActivityStateMount;
import eu.gloria.rt.entity.device.DeviceMount;
import eu.gloria.rt.entity.device.DeviceProperty;
import eu.gloria.rt.exception.RTException;
import eu.gloria.rtc.DeviceDiscoverer;
import eu.gloria.rts2.http.Rts2Date;
import eu.gloria.rts2.http.Rts2MessageType;
import eu.gloria.rts2.http.Rts2Messages;

/**
 * RTS2 RTDMountInterface implementation for Paramount RTS2 teld device.
 * 
 * @author mclopez
 *
 */
//public class MountOpentplRTD extends MountRTD {
public class MountOpentplRTD extends MountMoonProtectionRTD {

	/**
	 * Constructor
	 */
	public MountOpentplRTD() {
		
	}	
	
	@Override
	public boolean mntCanSetTracking() throws RTException {
		
		return true;
		
	}
	
	/**
	 * {@inheritDoc}
	 * <p>
	 * Operation not supported by RTS2 teld.
	 */
	@Override
	public boolean mntGetTracking() throws RTException {

		DeviceProperty property = this.devGetDeviceProperty("TRACK");
		
		if (property.getValue().get(0).compareTo("3") == 0)
			return true;
		else 
			return false;
	}

	/**
	 * {@inheritDoc}
	 * <p>
	 * Manages tracking property.
	 */
	@Override
	public void mntSetTracking(boolean value)
			throws RTException {

		List<String> valueProp = new ArrayList<String>();
		
		if (value)
			valueProp.add("3"); //Sidereal
		else
			valueProp.add("0");

		long time = Rts2Date.now();
		
		if(!this.devUpdateDeviceProperty("TRACK", valueProp))			
			throw new RTException("Cannot set tracking");	
		//Message recovering
		String message = Rts2Messages.getMessageText (Rts2MessageType.error, getDeviceId(), time);
		if (message != null)
			throw new RTException(message);	
		
	}
	
	

	

}

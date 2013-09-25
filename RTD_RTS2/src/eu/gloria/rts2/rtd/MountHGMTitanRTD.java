package eu.gloria.rts2.rtd;

import java.util.ArrayList;
import java.util.List;

import eu.gloria.rt.entity.device.ActivityStateMount;
import eu.gloria.rt.entity.device.DeviceMount;
import eu.gloria.rt.entity.device.DeviceProperty;
import eu.gloria.rt.exception.RTException;
import eu.gloria.rt.exception.UnsupportedOpException;
import eu.gloria.rtc.DeviceDiscoverer;
import eu.gloria.rts2.http.Rts2Date;
import eu.gloria.rts2.http.Rts2MessageType;
import eu.gloria.rts2.http.Rts2Messages;

/**
 * RTS2 RTDMountInterface implementation for Losmandy HGM Titan Mount.
 * 
 * @author bestebanez
 *
 */

public class MountHGMTitanRTD extends MountRTD  {
	/**
	 * Constructor
	 */
	public MountHGMTitanRTD() {
		
	}	
	
	/**
	 * {@inheritDoc}
	 * <p>
	 * Operation not supported by RTS2 teld.
	 */
	@Override
	public boolean mntGetTracking() throws RTException {

		DeviceProperty property = this.devGetDeviceProperty("MODE");
		
		if (property.getValue().get(0).compareTo("default") == 0) //Sidereal
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
	public void mntSetTracking(boolean value) throws RTException {

		throw new UnsupportedOpException ("Operation not supported");
		
	}

}

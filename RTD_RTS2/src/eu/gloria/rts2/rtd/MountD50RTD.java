package eu.gloria.rts2.rtd;

import java.util.ArrayList;
import java.util.List;

import eu.gloria.rt.entity.device.DeviceProperty;
import eu.gloria.rt.exception.RTException;
import eu.gloria.rts2.http.Rts2Date;
import eu.gloria.rts2.http.Rts2MessageType;
import eu.gloria.rts2.http.Rts2Messages;

/**
 * RTS2 RTDMountInterface implementation for D50 telescope mount.
 * 
 * @author mclopez
 *
 */
public class MountD50RTD extends MountRTD {

	/**
	 * Constructor
	 */
	public MountD50RTD() {
		
	}		
	
	
	@Override
	public boolean mntCanSetTracking() throws RTException {
		
		return true;
		
	}
	
	
	@Override
	public boolean mntGetTracking() throws RTException {

		DeviceProperty property = this.devGetDeviceProperty("TRACKING");
		
		if (property.getValue().get(0).compareTo("1") == 0)
			return true;
		else 
			return false;
	}
	
	
	@Override
	public void mntSetTracking(boolean value)
			throws RTException {

		List<String> valueProp = new ArrayList<String>();
		
		if (value)
			valueProp.add("true"); 
		else
			valueProp.add("false");

		long time = Rts2Date.now();
		
		if(!this.devUpdateDeviceProperty("TRACKING", valueProp))			
			throw new RTException("Cannot set tracking");	
		//Message recovering
		String message = Rts2Messages.getMessageText (Rts2MessageType.error, getDeviceId(), time);
		if (message != null)
			throw new RTException(message);	
		
	}
	
	
	@Override
	public void mntMoveEast() throws RTException {
		
		DeviceProperty property = this.devGetDeviceProperty("block_move");
		
		if (property.getValue().get(0).compareTo("1") == 0)
			throw new RTException("Mount movement blocked");
		else
			super.mntMoveEast();
		
	}
	
	
	@Override
	public void mntMoveSouth() throws RTException {
		
		DeviceProperty property = this.devGetDeviceProperty("block_move");
		
		if (property.getValue().get(0).compareTo("1") == 0)
			throw new RTException("Mount movement blocked");
		else
			super.mntMoveSouth();
		
	}
	
	
	@Override
	public void mntMoveWest() throws RTException {

		DeviceProperty property = this.devGetDeviceProperty("block_move");
		
		if (property.getValue().get(0).compareTo("1") == 0)
			throw new RTException("Mount movement blocked");
		else
			super.mntMoveWest();
		
	}
	
	@Override
	public void mntSlewObject(String object) throws RTException {
		
		DeviceProperty property = this.devGetDeviceProperty("block_move");
		
		if (property.getValue().get(0).compareTo("1") == 0)
			throw new RTException("Mount movement blocked");
		else
			super.mntSlewObject(object);
		
	}
	
	
	@Override
	public void mntSlewToAltAz( double azimuth, double altitude) throws RTException {
		
		DeviceProperty property = this.devGetDeviceProperty("block_move");
		
		if (property.getValue().get(0).compareTo("1") == 0)
			throw new RTException("Mount movement blocked");
		else
			super.mntSlewToAltAz(azimuth, altitude);
		
	}
	
	
	@Override
	public void mntSlewToAltAzAsync(double azimuth,	double altitude) throws RTException {	
		
		DeviceProperty property = this.devGetDeviceProperty("block_move");
		
		if (property.getValue().get(0).compareTo("1") == 0)
			throw new RTException("Mount movement blocked");
		else
			super.mntSlewToAltAzAsync(azimuth, altitude);
	}

	
	@Override
	public void mntSlewToCoordinates(double ascension,double declination) throws RTException {

		DeviceProperty property = this.devGetDeviceProperty("block_move");
		
		if (property.getValue().get(0).compareTo("1") == 0)
			throw new RTException("Mount movement blocked");
		else
			super.mntSlewToCoordinates(ascension, declination);
		
	}
	
	@Override
	public void mntSlewToCoordinatesAsync( double ascension, double declination) throws RTException {		

		DeviceProperty property = this.devGetDeviceProperty("block_move");
		
		if (property.getValue().get(0).compareTo("1") == 0)
			throw new RTException("Mount movement blocked");
		else
			super.mntSlewToCoordinatesAsync(ascension, declination);
				
	}
	
}

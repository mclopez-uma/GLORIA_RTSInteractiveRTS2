package eu.gloria.rts2.rtd;

import java.util.List;

import eu.gloria.rt.entity.device.ActivityStateDome;
import eu.gloria.rt.entity.device.ActivityStateDomeOpening;
import eu.gloria.rt.entity.device.Device;
import eu.gloria.rt.entity.device.DeviceDome;
import eu.gloria.rt.entity.device.DeviceMount;
import eu.gloria.rt.entity.device.DeviceProperty;
import eu.gloria.rt.entity.device.DeviceType;
import eu.gloria.rt.exception.RTException;
import eu.gloria.rt.exception.UnsupportedOpException;
import eu.gloria.rtc.DeviceDiscoverer;
import eu.gloria.rtc.DomeControlInterface;
import eu.gloria.rtd.RTDDomeInterface;
import eu.gloria.rts2.http.Rts2Date;
import eu.gloria.rts2.http.Rts2MessageType;
import eu.gloria.rts2.http.Rts2Messages;

/**
 * RTS2 RTDDomeInterface implementation for generic RTS2 dome and cupola device.
 * 
 * @author mclopez
 *
 */
public class DomeRTD extends DeviceRTD implements RTDDomeInterface{

	//Read-only from GLORIA
		
	/**
	 * Constructor
	 */
	public DomeRTD () {
		
	}
	
	/**
	 * {@inheritDoc}
	 * <p>
	 * Operation not supported by RTS2 dome or cupola.
	 */
	@Override
	public int domGetNumberElement() throws RTException {
		
		throw new UnsupportedOpException ("Operation not supported");
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public boolean domCanSetAltitude() throws RTException {

		return false;
	}

	/**
	 * {@inheritDoc}
	 * <p>
	 * Checks existence of "CUP_AZ" from RTS2 cupola
	 */
	@Override
	public boolean domCanSetAzimuth() throws RTException {
		
		return false;
		

//		try{
//			DeviceProperty property = this.devGetDeviceProperty("CUP_AZ");
//			if (property.isMandatory())
//				return false;
//		}catch (RTException e){
//			if (e.getMessage().contains("The property does not exist"))
//				return false;
//			else
//				throw new RTException (e.getMessage());
//		}
//		return true;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public boolean domCanSetPark() throws RTException {

		return false;
	}

	/**
	 * {@inheritDoc}
	 * <p>
	 * Operation not supported by RTS2 dome or cupola.
	 */
	@Override
	public boolean domIsAtHome() throws RTException {

		throw new UnsupportedOpException ("Operation not supported");
	}

	/**
	 * {@inheritDoc}
	 * <p>
	 * Operation not supported by RTS2 dome or cupola.
	 */
	@Override
	public boolean domIsAtPark() throws RTException {

		throw new UnsupportedOpException ("Operation not supported");
	}

	/**
	 * {@inheritDoc}
	 * <p>
	 * Operation not supported by RTS2 dome or cupola.
	 */
	@Override
	public double domGetAltitude() throws RTException {

		throw new UnsupportedOpException ("Operation not supported");
	}

	/**
	 * {@inheritDoc}
	 * <p>
	 * Recovers "CUP_AZ" from RTS2 cupola.
	 */
	@Override
	public double domGetAzimuth() throws RTException {
		
		//The value is not correct when dome is moving. It is set once the movement has finished. Check in real environment
		if (domCanSetAzimuth()){
			if (((DeviceDome) DeviceDiscoverer.devGetDevice(this.getDeviceId(), false)).getActivityState() != ActivityStateDome.MOVING){

				DeviceProperty property = this.devGetDeviceProperty("CUP_AZ");

				return Double.valueOf(property.getValue().get(0));
			}else{
				throw new RTException ("Cannot get dome azimuth. Dome is moving");
			}
		}else{
			throw new UnsupportedOpException ("Operation not supported");
		}
	}

	/**
	 * {@inheritDoc}
	 * <p>
	 * Execute <i>cmd: open</i> from RTS2 dome.<p>
	 * Parameter <i>element</i> ignored.
	 */
	@Override
	public void domOpen(int element) throws RTException {
		
		throw new UnsupportedOpException ("Operation not supported");
		
		// Open comand to overall device, not for element		
		
//		if (((DeviceDome) DeviceDiscoverer.devGetDevice(this.getDeviceId(), false)).getActivityStateOpening() == ActivityStateDomeOpening.CLOSE){
//			long time = Rts2Date.now();
//			
//			if (!this.devExecuteCmd ("open", true))
//				throw new RTException ("Dome cannot be open");
//			//Message recovering
//			String message = Rts2Messages.getMessageText (Rts2MessageType.error, getDeviceId(), time);
//			if (message != null)
//				throw new RTException(message);	
//		}else{
//			throw new RTException ("Dome is not closed");
//		}
		
	}

	/**
	 * {@inheritDoc}
	 * <p>
	 * Execute <i>cmd: close</i> from RTS2 dome.<p>
	 * Parameter <i>element</i> ignored.
	 */
	@Override
	public void domClose(int element) throws RTException {
		
		//throw new UnsupportedOpException ("Operation not supported");
		
		// Close comand to overall device, not for element
		
		if (((DeviceDome) DeviceDiscoverer.devGetDevice(this.getDeviceId(), false)).getActivityStateOpening() == ActivityStateDomeOpening.OPEN){
			long time = Rts2Date.now();
			
			if (!this.devExecuteCmd ("close", true))
				throw new RTException ("Dome cannot be closed");
			//Message recovering
			String message = Rts2Messages.getMessageText (Rts2MessageType.error, getDeviceId(), time);
			if (message != null)
				throw new RTException(message);	
		}else{
			throw new RTException ("Dome is not open");
		}

	}

	/**
	 * {@inheritDoc}
	 * <p>
	 * Operation not supported by RTS2 dome or cupola.
	 */
	@Override
	public void domGoHome() throws RTException {

		throw new UnsupportedOpException ("Operation not supported");
		
	}

	/**
	 * {@inheritDoc}
	 * <p>
	 * Operation not supported by RTS2 dome or cupola.
	 */
	@Override
	public void domSetPark(double altitude, double azimuth)
			throws RTException {

		throw new UnsupportedOpException ("Operation not supported");
		
	}

	/**
	 * {@inheritDoc}
	 * <p>
	 * Operation not supported by RTS2 dome or cupola.
	 */
	@Override
	public void domPark() throws RTException {

		throw new UnsupportedOpException ("Operation not supported");
		
	}

	/**
	 * {@inheritDoc}
	 * <p>
	 * Recovers "tar_dec" from RTS2 cupola.
	 * Execute <i>cmd: move</i> from RTS2 cupola
	 */
	@Override
	public void domMoveAzimuth(double azimuth)
			throws RTException {
		
		if (domCanSetAzimuth()){
			DeviceProperty property = this.devGetDeviceProperty("CUP_AZ");		

			long time = Rts2Date.now();

			if (!this.devExecuteCmd ("move+"+String.valueOf(azimuth)+"+"+property.getValue().get(0), false))
				throw new RTException ("Dome cannot be moved");
			//Message recovering
			String message = Rts2Messages.getMessageText (Rts2MessageType.error, getDeviceId(), time);
			if (message != null)
				throw new RTException(message);	
		}
		
	}

	/**
	 * {@inheritDoc}
	 * <p>
	 * Operation not supported by RTS2 dome or cupola.
	 */
	@Override
	public void domMoveAltitude(double altitude)
			throws RTException {

		throw new UnsupportedOpException ("Operation not supported");
		
	}

	@Override
	public void domSetTracking(boolean value) throws RTException {

		throw new UnsupportedOpException ("Operation not supported");
		
	}

	@Override
	public boolean domGetTracking() throws RTException {

		throw new UnsupportedOpException ("Operation not supported");
		
	}

	@Override
	public void domSlewObject(String object) throws RTException {

		throw new UnsupportedOpException ("Operation not supported");
		
	}
	
}

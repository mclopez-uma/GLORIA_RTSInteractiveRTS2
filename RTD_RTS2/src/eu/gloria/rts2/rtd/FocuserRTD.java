package eu.gloria.rts2.rtd;

import java.util.ArrayList;
import java.util.List;

import org.codehaus.jackson.map.ObjectMapper;

import eu.gloria.rt.entity.device.Device;
import eu.gloria.rt.entity.device.DeviceProperty;
import eu.gloria.rt.exception.RTException;
import eu.gloria.rt.exception.UnsupportedOpException;
import eu.gloria.rtc.DeviceDiscoverer;
import eu.gloria.rtd.RTDFocuserInterface;
import eu.gloria.rts2.http.Rts2Cmd;
import eu.gloria.rts2.http.Rts2CmdType;
import eu.gloria.rts2.http.Rts2Date;
import eu.gloria.rts2.http.Rts2MessageType;
import eu.gloria.rts2.http.Rts2Messages;

/**
 * RTS2 RTDFocuserInterface implementation for generic RTS2 focusd device.
 * 
 * @author mclopez
 *
 */
public class FocuserRTD extends DeviceRTD implements RTDFocuserInterface {
	
		
	/**
	 * Constructor
	 */
	public FocuserRTD () {
		
	}
	
	/**
	 * {@inheritDoc}
	 * <p>
	 * RTS2 always manages focusers as absolutes.
	 */
	@Override
	public boolean focIsAbsolute() throws RTException {		
		
		return true;
	}

	/**
	 * {@inheritDoc}
	 * <p>
	 * Operation not supported by RTS2 focusd.
	 */
	@Override
	public double focGetStepSize() throws RTException {
		
		throw new UnsupportedOpException ("Operation not supported");
	}
	
	/**
	 * {@inheritDoc}
	 * <p>
	 * Operation not supported by RTS2 focusd.
	 */
	@Override
	public long focGetMaxIncrement() throws RTException {
		
		throw new UnsupportedOpException ("Operation not supported");
	}

	/**
	 * {@inheritDoc}
	 * <p>
	 * Operation not supported by RTS2 focusd.
	 */
	@Override
	public long focGetMaxStep() throws RTException {

		DeviceProperty property = this.devGetDeviceProperty("FOC_TAR");
		
		return Math.round((Double.valueOf(property.getMinmax().get(1))));
		

	}
	
	

	
	/**
	 * {@inheritDoc} 
	 * <p>
	 * Recovers "FOC_POS" from RTS2 focusd.
	 */
	@Override
	public long focGetPosition() throws RTException {
		
		DeviceProperty property = this.devGetDeviceProperty("FOC_POS");		
		
		return (Double.valueOf(property.getValue().get(0))).longValue();
	}

	/**
	 * {@inheritDoc}
	 * <p>
	 * Checks existence of "FOC_TEMP" from RTS2 focusd.
	 */
	@Override
	public boolean focIsTempCompAvailable() throws RTException {

		try{
			DeviceProperty property = this.devGetDeviceProperty("FOC_TEMP");
		}catch (RTException e){
			if (e.getMessage().contains("The property does not exist"))
				return false;
			else
				throw new RTException (e.getMessage());
		}
		return true;
	}

	/**
	 * {@inheritDoc} 
	 * <p>
	 * Recovers "FOC_TEMP" from RTS2 focusd.
	 */
	@Override
	public double focGetTemperature() throws RTException {

		if (focIsTempCompAvailable()){
		
			DeviceProperty property = this.devGetDeviceProperty("FOC_TEMP");

			return Double.valueOf(property.getValue().get(0));
		}else{
			throw new UnsupportedOpException ("Operation not supported");
		}
	}

	/**
	 * {@inheritDoc}
	 * <p>
	 * Operation not supported by RTS2 focusd.
	 */
	@Override
	public void focSetTempComp( boolean trackingMode)
			throws RTException {

		throw new UnsupportedOpException ("Operation not supported");
	}
	
	/**
	 * {@inheritDoc}
	 * <p>
	 * Operation not supported by RTS2 focusd.
	 */
	@Override
	public void focHalt() throws RTException {
		
		throw new UnsupportedOpException ("Operation not supported");
	}

	/**
	 * {@inheritDoc} 
	 * <p>
	 * Sets "FOC_TAR" from RTS2 focusd.
	 */
	@Override
	public void focMove(long position) throws RTException {
		
		List<String> valueProp = new ArrayList<String>();
		valueProp.add(String.valueOf(position));
		
		long time = Rts2Date.now();
			
		if(!this.devUpdateDeviceProperty("FOC_TAR", valueProp))			
			throw new RTException("Cannot move");
		//Message recovering
		String message = Rts2Messages.getMessageText (Rts2MessageType.error, getDeviceId(), time);
		if (message != null)
			throw new RTException(message);	
	}

	/**
	 * {@inheritDoc} 
	 * <p>
	 * Search within the "focuser" property of all camd devices.
	 */
	@Override
	public String focGetCamera() throws RTException {
		
		List <String> removeDevices = new ArrayList<String> ();

		Rts2Cmd cmd = Rts2Cmd.getNewCmd(Rts2CmdType.devbytype);
		
		cmd.getParameters().put("t", "3");
		
		try {
			String jsonContent = cmd.execute();
			
			ObjectMapper mapper = new ObjectMapper();
			ArrayList<String> values = (ArrayList<String>) mapper.readValue(jsonContent, Object.class);
			
			//Removing of devices not in xml
			for (String deviceId: values){
				eu.gloria.rt.entity.environment.config.device.Device dev = configDeviceManager.getDevice(deviceId);
				if (dev == null){
					removeDevices.add(deviceId);
				}
			}			
			values.removeAll(removeDevices);
			
			DeviceProperty prop;
			for (String dev: values){	
				try{
					prop =  ((CameraRTD) DeviceDiscoverer.getRTD(dev)).devGetDeviceProperty("focuser");
					if (prop.getValue().get(0).equals(this.getDeviceId())){
						return dev;
					}	
				} catch (Exception e) {
					if (!e.getMessage().contains("The property does not exist")){
						throw new RTException(e.getMessage());
					}
				}
			}
			throw new RTException("No camera attached. ");
		
		} catch (Exception e) {
			throw new RTException("No camera attached. " + e.getMessage());
		}
	}

	@Override
	public long focGetMinStep() throws RTException {

		DeviceProperty property = this.devGetDeviceProperty("FOC_TAR");
		
		return Math.round((Double.valueOf(property.getMinmax().get(0))));
	}
	

}

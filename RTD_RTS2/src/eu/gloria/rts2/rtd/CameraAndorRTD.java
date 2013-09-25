package eu.gloria.rts2.rtd;

import java.util.ArrayList;
import java.util.List;

import eu.gloria.rt.entity.device.DeviceProperty;
import eu.gloria.rt.exception.RTException;
import eu.gloria.rt.exception.UnsupportedOpException;
import eu.gloria.rts2.http.Rts2Date;
import eu.gloria.rts2.http.Rts2MessageType;
import eu.gloria.rts2.http.Rts2Messages;

/**
 * RTS2 RTDCameraInterface implementation for Andor RTS2 camd device.
 * 
 * @author mclopez
 *
 */
public class CameraAndorRTD extends CameraRTD {
	
	/**
	 * Constructor
	 */
	public CameraAndorRTD (){
		
	}	
	
	
	@Override
	public boolean camHasGain() throws RTException {

		try{
			DeviceProperty property = this.devGetDeviceProperty("EMCCDGAIN");
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
	 * 
	 * Sets "EMCCDGAIN" from RTS2 andor camd. Value within [1,255].
	 */
	@Override
	public void camSetGain(long value) throws RTException {

		if (camHasGain()){
			if ((value>=1) & (value <=255)){

				List<String> valueProp = new ArrayList<String>();
				valueProp.add(String.valueOf(value));	

				long time = Rts2Date.now();

				if (!this.devUpdateDeviceProperty("EMCCDGAIN", valueProp))			
					throw new RTException("Cannot set Gain");
				//Message recovering
				String message = Rts2Messages.getMessageText (Rts2MessageType.error, getDeviceId(), time);
				if (message != null)
					throw new RTException(message);	
			}else{
				throw new RTException("Gain value not enabled.");
			}
		}else{
			throw new UnsupportedOpException ("Operation not supported");
		}

	}
	
	/**
	 * {@inheritDoc}
	 * <p>
	 * 
	 * Recovers "EMCCDGAIN" from RTS2 andor camd. 
	 */
	@Override
	public long camGetGain() throws RTException {
		
		if (camHasGain()){
			DeviceProperty property = this.devGetDeviceProperty("EMCCDGAIN");

			return Long.valueOf(property.getValue().get(0));
		}else{
			throw new UnsupportedOpException ("Operation not supported");
		}
	}	
	
	/**
	 * {@inheritDoc}
	 * <p>
	 * Recovers "ADCHANEL" from RTS2 andor camd.
	 */
	@Override
	public int camGetBitDepth() throws RTException {
		
		DeviceProperty property = this.devGetDeviceProperty("ADCHANEL");
		
		String[] values = property.getValue().get(0).split("bit");
		
		return (Integer.valueOf(values[0]));
	}


	/**
	 * {@inheritDoc}
	 * <p>
	 * Sets "ADCHANEL" from RTS2 andor camd. Posible values: 14, 16.
	 */
	@Override
	public void camSetBitDepth(int bits) throws RTException {
				
		List<String> valueProp = new ArrayList<String>();
		valueProp.add(String.valueOf(bits)+"bit");
		
		long time = Rts2Date.now();
		
		if(!this.devUpdateDeviceProperty("ADCHANEL", valueProp))
			throw new RTException("Cannot change Bit Depth");
		
		//Message recovering
		String message = Rts2Messages.getMessageText (Rts2MessageType.error, getDeviceId(), time);
		if (message != null)
			throw new RTException(message);	
	}
	
	
	
}

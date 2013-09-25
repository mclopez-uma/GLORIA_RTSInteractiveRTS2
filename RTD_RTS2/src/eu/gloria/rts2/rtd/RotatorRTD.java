package eu.gloria.rts2.rtd;

import java.util.ArrayList;
import java.util.List;

import eu.gloria.rt.entity.device.DeviceProperty;
import eu.gloria.rt.exception.RTException;
import eu.gloria.rtd.RTDRotatorInterface;
import eu.gloria.rts2.http.Rts2Date;
import eu.gloria.rts2.http.Rts2MessageType;
import eu.gloria.rts2.http.Rts2Messages;

/**
 * RTS2 RTDRotatorInterface implementation for generic RTS2 rotad device.
 * 
 * @author mclopez
 *
 */
public class RotatorRTD extends DeviceRTD implements RTDRotatorInterface{

	/**
	 * Constructor
	 */
	public RotatorRTD() {
		
	}

	
	/**
	 * {@inheritDoc}
	 * <p>
	 * Recovers "CUR_POS" from RTS2 rotad.
	 */
	@Override
	public double rttGetCurrentPosition() throws RTException {

		DeviceProperty property = this.devGetDeviceProperty("CUR_POS");
		
		return Double.valueOf(property.getValue().get(0));
	}

	
	/**
	 * {@inheritDoc}
	 * <p>
	 * Sets "TAR_POS" from RTS2 rotad.
	 */
	@Override
	public void rttSetTargetPosition(double position) throws RTException {
		
		List<String> valueProp = new ArrayList<String>();
		valueProp.add(String.valueOf(position));

		long time = Rts2Date.now();
		
		if(!this.devUpdateDeviceProperty("TAR_POS", valueProp))
			throw new RTException("Cannot change target position");
		
		//Message recovering
		String message = Rts2Messages.getMessageText (Rts2MessageType.error, getDeviceId(), time);
		if (message != null)
			throw new RTException(message);	
		
	}

}

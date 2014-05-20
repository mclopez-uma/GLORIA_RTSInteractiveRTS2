package eu.gloria.rts2.rtd;

import eu.gloria.rt.entity.device.DeviceProperty;
import eu.gloria.rt.exception.RTException;

public class CameraBootesAndorRTD extends CameraAndorRTD {

	@Override
	public boolean camHasGain() throws RTException {
		return false;
	}
	
	@Override
	public long camGetGain() throws RTException {
		
		
		DeviceProperty property = this.devGetDeviceProperty("EMCCDGAIN");

		return Long.valueOf(property.getValue().get(0));
		
	}	
}

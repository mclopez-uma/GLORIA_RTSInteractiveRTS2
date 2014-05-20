package eu.gloria.rts2.rtd;

import eu.gloria.rt.exception.RTException;

public class CameraBartRTD extends CameraRTD {

	@Override
	public boolean camCanSetCooler() throws RTException {
		
		//This class is only used in BART telescope. In this telescope, although the cooling can be set,
		//the responsible doesn't want to allow GLORIA to set it.
		
		return false;
	}
	
	
	@Override
	public boolean camCanSetCCDTemperature() throws RTException {

		//This class is only used in BART telescope. In this telescope, although the CCD temperature can be set,
		//the responsible doesn't want to allow GLORIA to set it.
		
		return false;
	}
	
}

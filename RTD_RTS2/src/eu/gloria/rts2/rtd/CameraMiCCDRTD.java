package eu.gloria.rts2.rtd;

import eu.gloria.rt.entity.device.DeviceProperty;
import eu.gloria.rt.exception.RTException;

public class CameraMiCCDRTD extends CameraRTD {
	
	
	public CameraMiCCDRTD (){
		
	}	
	
	/**
	 * {@inheritDoc}
	 * <p>
	 * Recovers "GAIN" from RTS2 camd miccd
	 */
	@Override
	public double camGetElectronsPerADU() throws RTException {

		DeviceProperty property = this.devGetDeviceProperty("GAIN");
		
		return Double.valueOf(property.getValue().get(0));
		
	}
	

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public boolean camCanGetCoolerPower() throws RTException {
		
		return true;
	}
	
	
	/**
	 * {@inheritDoc}
	 * <p>
	 * Recovers "TEMPPWR" from RTS2 camd miccd
	 */
	@Override
	public float camGetCoolerPower() throws RTException {
		
		if (this.camIsCoolerOn()){
			DeviceProperty property = this.devGetDeviceProperty("TEMPPWR");

			return Float.valueOf(property.getValue().get(0));
		}else{
			return 0;
		}
		
	}
	
	
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

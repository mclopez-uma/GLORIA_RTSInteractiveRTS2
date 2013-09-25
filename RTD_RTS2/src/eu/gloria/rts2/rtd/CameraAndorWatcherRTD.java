package eu.gloria.rts2.rtd;

import eu.gloria.rt.exception.RTException;
import eu.gloria.rt.exception.UnsupportedOpException;
import eu.gloria.rts2.rtd.CameraAndorRTD;

public class CameraAndorWatcherRTD extends CameraAndorRTD {
	
	@Override
	public boolean camHasGain() throws RTException {
		
		return false;
		
	}
	
	@Override
	public int camGetBitDepth() throws RTException {
		
		throw new UnsupportedOpException ("Operation not supported");
	}

	
	@Override
	public void camSetBitDepth(int bits) throws RTException {

		throw new UnsupportedOpException ("Operation not supported");
	}
	
	
	@Override
	public void camSetBinX(int value) throws RTException {

		throw new UnsupportedOpException ("Operation not supported");
	}
	
	@Override
	public void camSetBinY(int value) throws RTException {

		throw new UnsupportedOpException ("Operation not supported");
	}
	
	@Override
	public boolean camCanSetCCDTemperature() throws RTException {
		
		return false;
	}
	
	@Override
	public boolean camCanSetCooler() throws RTException {
		
		return false;
	}
	
	@Override
	public void camSetROINumX(int value) throws RTException {
		
		throw new UnsupportedOpException ("Operation not supported");
	}
	
	@Override
	public void camSetROINumY(int value) throws RTException {
		
		throw new UnsupportedOpException ("Operation not supported");
	}
	
	@Override
	public void camSetROIStartX(int ROIStartX) throws RTException{

		throw new UnsupportedOpException ("Operation not supported");
	}
	
	@Override
	public void camSetROIStartY(int ROIStartX) throws RTException{

		throw new UnsupportedOpException ("Operation not supported");
	}
	
	@Override
	public boolean camCanControlTemperature() throws RTException {
		
		return false;
	}
	
	
	
}

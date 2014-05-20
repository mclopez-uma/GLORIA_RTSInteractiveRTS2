package eu.gloria.rts2.rtd;

import ij.IJ;
import ij.ImagePlus;
import ij.plugin.JpegWriter;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import javax.persistence.EntityManager;
import javax.servlet.http.HttpServletResponse;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;

import org.eclipse.jetty.util.log.Log;
import org.eso.fits.FitsData;
import org.eso.fits.FitsHDUnit;
import org.eso.fits.FitsHeader;
import org.eso.fits.FitsKeyword;

import eu.gloria.rt.catalogue.Catalogue;
import eu.gloria.rt.catalogue.ObjCategory;
import eu.gloria.rt.catalogue.ObjInfo;
import eu.gloria.rt.db.scheduler.ObservingPlanManager;
import eu.gloria.rt.db.util.DBUtil;
import eu.gloria.rt.entity.db.FileContentType;
import eu.gloria.rt.entity.db.FileFormat;
import eu.gloria.rt.entity.db.FileType;
import eu.gloria.rt.entity.db.ObservingPlanOwner;
import eu.gloria.rt.entity.db.ObservingPlanType;
import eu.gloria.rt.entity.db.UuidType;
import eu.gloria.rt.entity.device.ActivityContinueStateCamera;
import eu.gloria.rt.entity.device.ActivityStateCamera;
import eu.gloria.rt.entity.device.CameraAcquisitionMode;
import eu.gloria.rt.entity.device.CameraDigitizingMode;
import eu.gloria.rt.entity.device.CameraType;
import eu.gloria.rt.entity.device.Device;
import eu.gloria.rt.entity.device.DeviceCamera;
import eu.gloria.rt.entity.device.DeviceProperty;
import eu.gloria.rt.entity.device.Image;
import eu.gloria.rt.entity.device.ImageContent;
import eu.gloria.rt.entity.device.ImageContentType;
import eu.gloria.rt.entity.device.ImageFormat;
import eu.gloria.rt.entity.environment.config.device.DeviceType;
import eu.gloria.rt.exception.CommunicationException;
import eu.gloria.rt.exception.RTException;
import eu.gloria.rt.exception.UnsupportedOpException;
import eu.gloria.rt.tools.ssh.SshCmd;
import eu.gloria.rt.unit.Radec;
import eu.gloria.rtc.DeviceDiscoverer;
import eu.gloria.rtc.op.OpManager;
import eu.gloria.rtd.RTDCameraInterface;
import eu.gloria.rtd.RTDFilterWheelInterface;
import eu.gloria.rtd.RTDMountInterface;
import eu.gloria.rti_db.tools.RTIDBProxyConnection;
import eu.gloria.rts2.http.Rts2Cmd;
import eu.gloria.rts2.http.Rts2CmdGetResponse;
import eu.gloria.rts2.http.Rts2CmdType;
import eu.gloria.rts2.http.Rts2Constants;
import eu.gloria.rts2.http.Rts2Date;
import eu.gloria.rts2.http.Rts2GatewayDeviceManager;
import eu.gloria.rts2.http.Rts2GatewayDevicePropertiesRequest;
import eu.gloria.rts2.http.Rts2ImageCmdType;
import eu.gloria.rts2.http.Rts2MessageType;
import eu.gloria.rts2.http.Rts2Messages;
import eu.gloria.rts2.http.Rts2SynchronizerMessage;
import eu.gloria.rts2.http.Rts2GatewayDevicePropertiesRequest.RequestType;
import eu.gloria.tools.configuration.Config;
import eu.gloria.tools.conversion.DegreeFormat;
import eu.gloria.tools.log.LogUtil;
import eu.gloria.tools.runtime.ExecResult;
import eu.gloria.tools.runtime.Executor;
import eu.gloria.tools.thread.context.ContextSynchronizer;
import eu.gloria.tools.time.DateTools;
import eu.gloria.tools.time.TimeOut;
import eu.gloria.tools.uuid.UUIDGenerator;

/**
 * RTDCameraInterface implementation for generic RTS2 camd device.
 * 
 * @author mclopez
 *
 */
public class CameraRTD extends DeviceRTD implements	RTDCameraInterface{

	private double exposureTime = -1;
	
	private ExposureContext imageContext = null;
	
	private Task task = null;
	private Timer exposureTimer = null;
	
	private static String UUID_IMG_CONTINUE_MODE = "UUID_IMG_CONTINUE_MODE_DEFAULT";
	
	private boolean exposureContinueModeOn;
	
	private RTIDBProxyConnection dbProxy;
	
	private double planetMin;
	private double planetMax;
	private double mMin;
	private double mMax;
	private double ngcMin;
	private double ngcMax;
	
	
	/**
	 * Testing purpose.
	 * @param args Arguments
	 * @throws Exception In error case
	 */
	public static void main(String[] args) throws Exception {
	
		//Generates the JPG from FITS
//		String fitsFilefullPath = "C:\\Users\\bestebanez\\Desktop\\prueba\\om.fits";
//		String jpgFilefullPath = "C:\\Users\\bestebanez\\Desktop\\prueba\\om.jpg";
//		
//		try{
//			ImagePlus imp = IJ.openImage(jpgFilefullPath);
//			ImageConverter converter = new ImageConverter(imp);
//			converter.convertToGray32();
//			IJ.saveAs(imp,"FITS", fitsFilefullPath);
//			String[] headerfile = FITS_Writer.getHeader(imp);
//			
//			//JpegWriter.save(imp, jpgFilefullPath, 100);
//		}catch(Exception ex){
//			String[] names = {"file_fits", "file_jpg"};
//			String[] values = {fitsFilefullPath, jpgFilefullPath};
//			throw new RTException("Error Creating jpg archive." + LogUtil.getLog(names, values) + ". " + ex.getMessage());
//		}
		
		
		CameraRTD camera = new CameraRTD();
		camera.setDeviceId("C0");
		camera.fitsHeadModification("C:\\Users\\mclopez.ISA\\Downloads\\0000000e0000000120140307000001449bff27c6v001.fits", "0000000e0000000120140307000001449bff27c6v001.fits");
//		camera.exposureTime = 1.0;
//		String UUID = camera.camStartExposure(true);
//		Thread.sleep(30000);
		
		
	}
	
	
	
	
	

	/**
	 * Constructor
	 */
	public CameraRTD () {
		
		String proxyHost = Config.getProperty("rt_config","proxyHost");
		String proxyPort = Config.getProperty("rt_config","proxyPort");
		String proxyAppName = Config.getProperty("rt_config","proxyAppName");
		String proxyUser = Config.getProperty("rt_config","proxyUser");
		String proxyPw = Config.getProperty("rt_config","proxyPw");
		boolean proxyHttps = Config.getPropertyBoolean("rt_config","proxyHttps",false);
		String proxyCertRep = Config.getProperty("rt_config","proxyCertRep");
		
		dbProxy = new RTIDBProxyConnection(proxyHost, proxyPort, proxyAppName, proxyUser, proxyPw, proxyHttps, proxyCertRep);
		
		
		imageContext = new ExposureContext();
		
		exposureContinueModeOn = false;
				
		
		
	}

	private String generateUUID () throws Exception{

		return UUIDGenerator.singleton.getUUID().getValue();

	}
	
	/**
	 * This method changes the contueStateCamera taking into account a local flag.
	 * 
	 * Implementation for Bruselas presentation. After, GLORIA site and this RTD have to be changed to manage the continue mode.
	 * 
	 */
	@Override
	public Device devGetDevice(boolean allProperties)  throws RTException{
		
		//Access to parent method
		Device result = super.devGetDevice(allProperties);
		DeviceCamera devCamera = (DeviceCamera) result;
		
		devCamera.setHasImage(this.camImageReady());
		
		if (exposureContinueModeOn){
			devCamera.setActivityContinueState(ActivityContinueStateCamera.EXPOSING);
		}else{
			devCamera.setActivityContinueState(ActivityContinueStateCamera.OFF);
		}
		return result;
		
	}
	
	/**
	 * This method changes the contueStateCamera taking into account a local flag.
	 * 
	 * Implementation for Bruselas presentation. After, GLORIA site and this RTD have to be changed to manage the continue mode.
	 * 
	 */
	@Override
	public Device getDevice(List<String> propertyNames) throws RTException {
	
		//Access to parent method
		Device result = super.getDevice(propertyNames);
		DeviceCamera devCamera = (DeviceCamera) result;
				
		devCamera.setHasImage(this.camImageReady());
		
		if (exposureContinueModeOn){
			devCamera.setActivityContinueState(ActivityContinueStateCamera.EXPOSING);
		}else{
			devCamera.setActivityContinueState(ActivityContinueStateCamera.OFF);
		}
		return result;
	}

	/**
	 * {@inheritDoc}
	 * <p>
	 * Operation not supported by RTS2 camd.
	 */
	@Override
	public CameraType camGetCameraType() throws RTException {
		
		 throw new UnsupportedOpException ("Operation not supported");
	}

	
	/**
	 * {@inheritDoc}
	 * <p>
	 * Recovers "SIZE" from RTS2 camd.
	 */
	@Override
	public int camGetXsize() throws RTException {
				
		DeviceProperty property = this.devGetDeviceProperty("SIZE");
		
		return Integer.valueOf(property.getValue().get(2));
	}

	
	/**
	 * {@inheritDoc}
	 * <p>
	 * Recovers "SIZE" from RTS2 camd.
	 */
	@Override
	public int camGetYSize() throws RTException {
		
		DeviceProperty property = this.devGetDeviceProperty("SIZE");
		
		return Integer.valueOf(property.getValue().get(3));
	}
	
	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public boolean camCanAbortExposure() throws RTException {
		// RTS2 Cmd has this command, check in real environment
		return true;
	}
	
	
	/**
	 * {@inheritDoc}
	 * <p>
	 * Recovers "binning" from RTS2 camd
	 */
	@Override
	public boolean camCanAsymetricBin() throws RTException {

		DeviceProperty property = this.devGetDeviceProperty("binning");
		
		List<String> binnings = property.getPossibleValue();
		
		//Asymmetric check
		for (String binning: binnings){
			String[] values = binning.split("x");
			if (Integer.valueOf(values[0]) != Integer.valueOf(values[1]))
				return true;
		}
		
		return false;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public boolean camCanGetCoolerPower() throws RTException {
		
		return false;
	}
	
	/**
	 * {@inheritDoc}
	 * <p>
	 * Check existence of CCD_SET from RTS2 camd
	 */
	@Override
	public boolean camCanSetCCDTemperature() throws RTException {

		try{
			DeviceProperty property = this.devGetDeviceProperty("CCD_SET");
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
	 * Check existence of CCD_TEMP from RTS2 camd
	 */
	@Override
	public boolean camCanControlTemperature()
			throws RTException {

		//Heat sink temperature?
				
		try{
			DeviceProperty property = this.devGetDeviceProperty("CCD_TEMP");
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
	 * 
	 */
	@Override
	public boolean camCanStopExposure() throws RTException {
		// RTS2 API allows this action
		
		return false;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public float camGetCoolerPower() throws RTException {
		
		if (!camCanGetCoolerPower())
			throw new UnsupportedOpException ("Operation not supported");
		return 0;
	}

	/**
	 * {@inheritDoc}
	 * <p>
	 * Operation not supported by RTS2 camd.
	 */
	@Override
	public double camGetElectronsPerADU() throws RTException {

		throw new UnsupportedOpException ("Operation not supported");
		
	}

	/**
	 * {@inheritDoc}
	 * <p>
	 * Operation not supported by RTS2 camd.
	 */
	@Override
	public double camGetFullWellCapacity() throws RTException {
		
		throw new UnsupportedOpException ("Operation not supported");
	}

	/**
	 * {@inheritDoc}
	 * <p>
	 * Checks existence of "SHUTTER" from RTS2 camd
	 */
	@Override
	public boolean camHasShutter() throws RTException {
		
		try{
			DeviceProperty property = this.devGetDeviceProperty("SHUTTER");
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
	 * Operation not supported by RTS2 camd.
	 */
	@Override
	public boolean camHasBrightness() throws RTException {
		
		return false;
	}

	/**
	 * {@inheritDoc}
	 * <p>
	 * Operation not supported by RTS2 camd.
	 */
	@Override
	public boolean camHasConstrast() throws RTException {
		
		return false;
	}

	/**
	 * {@inheritDoc}
	 * <p>
	 * Operation not supported by RTS2 camd.
	 */
	@Override
	public boolean camHasGain() throws RTException {

		return false;
	}

	/**
	 * {@inheritDoc}
	 * <p>
	 * Operation not supported by RTS2 camd.
	 */
	@Override
	public boolean camHasGamma() throws RTException {

		return false;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public boolean camHasSubframe() throws RTException {
		// RTS2 API allows this action
		
		return true;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public boolean camHasExposureTime() throws RTException {
		// RTS2 API allows this action
		
		return true;
	}

	/**
	 * {@inheritDoc}
	 * <p>
	 * Operation not supported by RTS2 camd.
	 */
	@Override
	public double camHeatSinkTemperature() throws RTException {

		throw new UnsupportedOpException ("Operation not supported");
	}

	/**
	 * {@inheritDoc}
	 * <p>
	 * Operation not supported by RTS2 camd.
	 */
	@Override
	public boolean camIsPulseGuiding() throws RTException {

		throw new UnsupportedOpException ("Operation not supported");
	}

	/**
	 * {@inheritDoc}
	 * <p>
	 * Operation not supported by RTS2 camd.
	 */
	@Override
	public String camGetLastError() throws RTException {

		throw new UnsupportedOpException ("Operation not supported");
	}

	/**
	 * {@inheritDoc}
	 * <p>
	 * Operation not supported by RTS2 camd.
	 */
	@Override
	public double camGetLastExposureDuration()
			throws RTException {

		throw new UnsupportedOpException ("Operation not supported");
	}

	/**
	 * {@inheritDoc}
	 * <p>
	 * Operation not supported by RTS2 camd.
	 */
	@Override
	public Date camGetLastExposureStart() throws RTException {

		throw new UnsupportedOpException ("Operation not supported");
	}

	/**
	 * {@inheritDoc}
	 * <p>
	 * Operation not supported by RTS2 camd.
	 */
	@Override
	public long camGetMaxAdu() throws RTException {

		throw new UnsupportedOpException ("Operation not supported");
	}
	
	/**
	 * {@inheritDoc}
	 * <p>
	 * Recovers "binning" from RTS2 camd
	 */
	@Override
	public int camGetMaxBinX() throws RTException {
		
		DeviceProperty property = this.devGetDeviceProperty("binning");
		
		List<String> binnings = property.getPossibleValue();
		
		//Check maximum
		int max = 1;
		for (String binning: binnings){
			String[] values = binning.split("x");
			if (Integer.valueOf(values[0]) > max)
				max = Integer.valueOf(values[0]);
		}
		return max;
	}

	
	/**
	 * {@inheritDoc}
	 * <p>
	 * Recovers "binning" from RTS2 camd
	 */
	@Override
	public int camGetMaxBinY() throws RTException {
		
		DeviceProperty property = this.devGetDeviceProperty("binning");
		
		List<String> binnings = property.getPossibleValue();
		
		//Check maximum
		int max = 1;
		for (String binning: binnings){
			String[] values = binning.split("x");
			if (Integer.valueOf(values[1]) > max)
				max = Integer.valueOf(values[0]);
		}
		return max;
	}

	/**
	 * {@inheritDoc}
	 * <p>
	 * Operation not supported by RTS2 camd.
	 */
	@Override
	public int camGetPixelSizeX() throws RTException {

		throw new UnsupportedOpException ("Operation not supported");
		
	}

	/**
	 * {@inheritDoc}
	 * <p>
	 * Operation not supported by RTS2 camd.
	 */
	@Override
	public int camGetPixelSizeY() throws RTException {

		throw new UnsupportedOpException ("Operation not supported");
		
	}

	/**
	 * {@inheritDoc}
	 * <p>
	 * Operation not supported by RTS2 camd.
	 */
	@Override
	public CameraAcquisitionMode camGetAcquisitionMode()
			throws RTException {
		
		return CameraAcquisitionMode.ONE_SHOT;
	}

	/**
	 * {@inheritDoc}
	 * <p>
	 * Operation not supported by RTS2 camd.
	 */
	@Override
	public float camGetFPS() throws RTException {

		if (camGetAcquisitionMode() == CameraAcquisitionMode.ONE_SHOT)
			throw new UnsupportedOpException ("Operation not supported");
		
		throw new UnsupportedOpException ("Operation not supported");
	}

	/**
	 * {@inheritDoc}
	 * <p>
	 * Operation not supported by RTS2 camd.
	 */
	@Override
	public CameraDigitizingMode camGetDigitilizingMode()
			throws RTException {
		
		throw new UnsupportedOpException ("Operation not supported");
	}

	/**
	 * {@inheritDoc}
	 * <p>
	 * Recovers "BINX" from RTS2 camd
	 */
	@Override
	public int camGetBinX() throws RTException {

		DeviceProperty property = this.devGetDeviceProperty("BINX");
		
		return Integer.valueOf(property.getValue().get(0));
	}

	
	/**
	 * {@inheritDoc}
	 * <p>
	 * Sets "binning" from RTS2 camd. Needs to recovers "BINY" from RTS2 camd.
	 */
	@Override
	public void camSetBinX(int value) throws RTException {
		
		
		DeviceProperty property = this.devGetDeviceProperty("binning");
		
		List<String> binnings = property.getPossibleValue();
		
		//Check if binX value is supported
		boolean correctValue = false;
		for (String binning: binnings){
			String[] values = binning.split("x");
			if (Integer.valueOf(values[0]) == value){
				correctValue = true;
				break;
			}
		}
		

		if (!correctValue){
			throw new RTException("The BinX is not correct.");
		}else{
			//Recovers BINY
			property = this.devGetDeviceProperty("BINY");

			List<String> valueProp = new ArrayList<String>();
			valueProp.add(String.valueOf(value)+"x"+property.getValue().get(0));

			long time = Rts2Date.now();
			//Try to update binning			
			try{
				if(!this.devUpdateDeviceProperty("binning", valueProp))
					throw new RTException("Cannot change BinX");
			}catch (RTException e){
				if (e.getMessage().contains("Invalid basic value")){
					throw new RTException ("BinX cannot be set with current BinY");
				}else{
					throw new RTException (e.getMessage());
				}
			}
			//Message recovering
			String message = Rts2Messages.getMessageText (Rts2MessageType.error, getDeviceId(), time);
			if (message != null)
				throw new RTException(message);	
		}		
		

	}

	
	/**
	 * {@inheritDoc}
	 * <p>
	 * Recovers "BINY" from RTS2 camd
	 */
	@Override
	public int camGetBinY() throws RTException {

		DeviceProperty property = this.devGetDeviceProperty("BINY");
		
		return Integer.valueOf(property.getValue().get(0));
	}
	
	/**
	 * {@inheritDoc}
	 * <p>
	 * Sets "binning" from RTS2 camd. Needs to recovers "BINX" from RTS2 camd.
	 */
	@Override
	public void camSetBinY(int value) throws RTException {

		DeviceProperty property = this.devGetDeviceProperty("binning");
		
		List<String> binnings = property.getPossibleValue();
		
		//Check if binY value is supported
		boolean correctValue = false;
		for (String binning: binnings){
			String[] values = binning.split("x");
			if (Integer.valueOf(values[1]) == value){
				correctValue = true;
				break;
			}
		}
		

		if (!correctValue){
			throw new RTException("The BinY is not correct.");
		}else{
			//Recovers BINX
			property = this.devGetDeviceProperty("BINX");

			List<String> valueProp = new ArrayList<String>();
			valueProp.add(property.getValue().get(0)+"x"+String.valueOf(value));

			//Try to update binning
			long time = Rts2Date.now();
			
			try{
				if(!this.devUpdateDeviceProperty("binning", valueProp))
					throw new RTException("Cannot change BinY");
			}catch (RTException e){
				if (e.getMessage().contains("Invalid basic value")){
					throw new RTException ("BinY cannot be set with current BinX");
				}else{
					throw new RTException (e.getMessage());
				}
			}
			//Message recovering
			String message = Rts2Messages.getMessageText (Rts2MessageType.error, getDeviceId(), time);
			if (message != null)
				throw new RTException(message);	
		}

	}

	
	/**
	 * {@inheritDoc}
	 * <p>
	 * Recovers "COOLING" from RTS2 camd.
	 */
	@Override
	public boolean camIsCoolerOn() throws RTException {

//		if (camCanSetCooler()){
		
		try{
			DeviceProperty property = this.devGetDeviceProperty("COOLING");
			
			if (property.getValue().get(0).compareTo("1") == 0)
				return true;
			else 
				return false;
			
		}catch (RTException e){
			if (e.getMessage().contains("The property does not exist"))
				throw new UnsupportedOpException ("Operation not supported");
		}
		
		throw new UnsupportedOpException ("Operation not supported");

			
//		}else{
//			throw new RTException ("The cooler status cannot be read");
//		}
		
	}

	
	/**
	 * {@inheritDoc}
	 * <p>
	 * Recovers "COOLING" from RTS2 camd.
	 */
	@Override
	public void camSetCoolerOn(boolean value)
			throws RTException {

		if (camCanSetCooler()){			
			
			List<String> valueProp = new ArrayList<String>();
			
			if (value)
				valueProp.add("true");
			else
				valueProp.add("false");

			long time = Rts2Date.now();
			
			if(!this.devUpdateDeviceProperty("COOLING", valueProp))			
				throw new RTException("Cannot set Cooler");	
			//Message recovering
			String message = Rts2Messages.getMessageText (Rts2MessageType.error, getDeviceId(), time);
			if (message != null)
				throw new RTException(message);	
		}else{
			throw new UnsupportedOpException ("Operation not supported");
		}
		
				

	}

	
	/**
	 * {@inheritDoc}
	 * <p>
	 * Recovers "WINDOW" and "BINX" from RTS2 camd.
	 */
	@Override
	public int camGetROINumX() throws RTException {

		DeviceProperty binX = this.devGetDeviceProperty("BINX");
		
		DeviceProperty property = this.devGetDeviceProperty("WINDOW");
		
		return Integer.valueOf(property.getValue().get(2))/Integer.valueOf(binX.getValue().get(0));
	}

	
	/**
	 * {@inheritDoc}
	 * <p>
	 * Sets "WINDOW" from RTS2 camd. Needs to recovers "BINX" from RTS2 camd.
	 */
	@Override
	public void camSetROINumX(int value) throws RTException {

		DeviceProperty binX = this.devGetDeviceProperty("BINX");
		
		DeviceProperty property = this.devGetDeviceProperty("WINDOW");
		
		List<String> valueProp = new ArrayList<String>();
		valueProp.add(property.getValue().get(0));
		valueProp.add(property.getValue().get(1));
		valueProp.add(String.valueOf(value*Integer.valueOf(binX.getValue().get(0))));
		valueProp.add(property.getValue().get(3));
		
		long time = Rts2Date.now();
		
		if(!this.devUpdateDeviceProperty("WINDOW", valueProp))			
			throw new RTException("Cannot set ROI X");
		//Message recovering
		String message = Rts2Messages.getMessageText (Rts2MessageType.error, getDeviceId(), time);
		if (message != null)
			throw new RTException(message);	

	}

	
	/**
	 * {@inheritDoc}
	 * <p>
	 * Sets "WINDOW" from RTS2 camd. Needs to recovers "BINY" from RTS2 camd.
	 */
	@Override
	public void camSetROINumY(int value) throws RTException {

		DeviceProperty binY = this.devGetDeviceProperty("BINY");
		
		DeviceProperty property = this.devGetDeviceProperty("WINDOW");
		
		List<String> valueProp = new ArrayList<String>();
		valueProp.add(property.getValue().get(0));
		valueProp.add(property.getValue().get(1));
		valueProp.add(property.getValue().get(2));
		valueProp.add(String.valueOf(value*Integer.valueOf(binY.getValue().get(0))));
		
		
		long time = Rts2Date.now();
		
		if(!this.devUpdateDeviceProperty("WINDOW", valueProp))			
			throw new RTException("Cannot set ROI Y");
		//Message recovering
		String message = Rts2Messages.getMessageText (Rts2MessageType.error, getDeviceId(), time);
		if (message != null)
			throw new RTException(message);	
		
	}

	
	/**
	 * {@inheritDoc}
	 * <p>
	 * Recovers "WINDOW" and "BINY" from RTS2 camd.
	 */
	@Override
	public int camGetROINumY() throws RTException {

		DeviceProperty binY = this.devGetDeviceProperty("BINY");
		
		DeviceProperty property = this.devGetDeviceProperty("WINDOW");
		
		return Integer.valueOf(property.getValue().get(3))/Integer.valueOf(binY.getValue().get(0));
	}

	
	/**
	 * {@inheritDoc}
	 * <p>
	 * Sets "WINDOW" from RTS2 camd. Needs to recovers "BINX" from RTS2 camd.
	 */
	@Override
	public void camSetROIStartX(int ROIStartX)
			throws RTException {

		DeviceProperty binX = this.devGetDeviceProperty("BINX");
		
		DeviceProperty property = this.devGetDeviceProperty("WINDOW");
		
		List<String> valueProp = new ArrayList<String>();
		valueProp.add(String.valueOf(ROIStartX*Integer.valueOf(binX.getValue().get(0))));
		valueProp.add(property.getValue().get(1));
		valueProp.add(property.getValue().get(2));
		valueProp.add(property.getValue().get(3));		
		
		long time = Rts2Date.now();
		
		if(!this.devUpdateDeviceProperty("WINDOW", valueProp))			
			throw new RTException("Cannot set ROI StartX");
		//Message recovering
		String message = Rts2Messages.getMessageText (Rts2MessageType.error, getDeviceId(), time);
		if (message != null)
			throw new RTException(message);	
	}

	
	/**
	 * {@inheritDoc}
	 * <p>
	 * Recovers "WINDOW" and "BINX" from RTS2 camd.
	 */
	@Override
	public int camGetROIStartX() throws RTException {

		DeviceProperty binX = this.devGetDeviceProperty("BINX");
		
		DeviceProperty property = this.devGetDeviceProperty("WINDOW");
		
		return Integer.valueOf(property.getValue().get(0))/Integer.valueOf(binX.getValue().get(0));
	}

	
	/**
	 * {@inheritDoc}
	 * <p>
	 * Sets "WINDOW" from RTS2 camd. Needs to recovers "BINY" from RTS2 camd.
	 */
	@Override
	public void camSetROIStartY(int value)	throws RTException {

		DeviceProperty binY = this.devGetDeviceProperty("BINY");
		
		DeviceProperty property = this.devGetDeviceProperty("WINDOW");
		
		List<String> valueProp = new ArrayList<String>();
		valueProp.add(property.getValue().get(0));		
		valueProp.add(String.valueOf(value*Integer.valueOf(binY.getValue().get(0))));
		valueProp.add(property.getValue().get(2));
		valueProp.add(property.getValue().get(3));		
		
		long time = Rts2Date.now();

		if(!this.devUpdateDeviceProperty("WINDOW", valueProp))			
			throw new RTException("Cannot set ROI StartY");
		//Message recovering
		String message = Rts2Messages.getMessageText (Rts2MessageType.error, getDeviceId(), time);
		if (message != null)
			throw new RTException(message);	

	}

	
	/**
	 * {@inheritDoc}
	 * <p>
	 * Recovers "WINDOW" and "BINY" from RTS2 camd.
	 */
	@Override
	public int camGetROIStartY() throws RTException {

		DeviceProperty binY = this.devGetDeviceProperty("BINY");
		
		DeviceProperty property = this.devGetDeviceProperty("WINDOW");
		
		return Integer.valueOf(property.getValue().get(1))/Integer.valueOf(binY.getValue().get(0));
	}
	
	/**
	 * {@inheritDoc}
	 * <p>
	 * Operation not supported by RTS2 camd.
	 */
	@Override
	public void camSetBrightness(long value)
			throws RTException {
		
		throw new UnsupportedOpException ("Operation not supported");

	}

	/**
	 * {@inheritDoc}
	 * <p>
	 * Operation not supported by RTS2 camd.
	 */
	@Override
	public long camGetBrightness() throws RTException {

		throw new UnsupportedOpException ("Operation not supported");
	}

	/**
	 * {@inheritDoc}
	 * <p>
	 * Operation not supported by RTS2 camd.
	 */
	@Override
	public void camSetContrast(long value) throws RTException {

		throw new UnsupportedOpException ("Operation not supported");

	}

	/**
	 * {@inheritDoc}
	 * <p>
	 * Operation not supported by RTS2 camd.
	 */
	@Override
	public long camGetContrast() throws RTException {

		throw new UnsupportedOpException ("Operation not supported");
	}

	/**
	 * {@inheritDoc}
	 * <p>
	 * Operation not supported by RTS2 camd.
	 */
	@Override
	public void camSetGain(long value) throws RTException {

		throw new UnsupportedOpException ("Operation not supported");

	}

	/**
	 * {@inheritDoc}
	 * <p>
	 * Operation not supported by RTS2 camd.
	 */
	@Override
	public long camGetGain() throws RTException {

		throw new UnsupportedOpException ("Operation not supported");
	}

	/**
	 * {@inheritDoc}
	 * <p>
	 * Operation not supported by RTS2 camd.
	 */
	@Override
	public void camSetGamma(long value) throws RTException {

		throw new UnsupportedOpException ("Operation not supported");

	}

	/**
	 * {@inheritDoc}
	 * <p>
	 * Operation not supported by RTS2 camd.
	 */
	@Override
	public long camGetGamma() throws RTException {

		throw new UnsupportedOpException ("Operation not supported");
	}

	
	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public void camSetExposureTime(double value)
			throws RTException {

		exposureTime = value;
		
	}

	
	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public Double camGetExposureTime() throws RTException {

		if (exposureTime != -1){
			return exposureTime;
		}else{
			DeviceProperty property = this.devGetDeviceProperty("exposure");
			return Double.valueOf(property.getValue().get(0));
		}
			
	}

	
	/**
	 * {@inheritDoc}
	 * <p>
	 * Sets "CCD_SET" from RTS2 camd.
	 */
	@Override
	public void camSetCCDTemperature(float value)
			throws RTException {

		if (camCanSetCCDTemperature()){
		
			List<String> valueProp = new ArrayList<String>();
			valueProp.add(String.valueOf(kelvinToCelsius(value)));

			long time = Rts2Date.now();

			if(!this.devUpdateDeviceProperty("CCD_SET", valueProp))			
				throw new RTException("Cannot set CCD temperature");
			//Message recovering
			String message = Rts2Messages.getMessageText (Rts2MessageType.error, getDeviceId(), time);
			if (message != null)
				throw new RTException(message);	
		}else{
			throw new UnsupportedOpException ("Operation not supported");
		}
	}

	
	/**
	 * {@inheritDoc}
	 * <p>
	 * Recovers "CCD_SET" from RTS2 camd.
	 */
	@Override
	public float camGetCCDTemperature() throws RTException {

		if (camCanSetCCDTemperature()){

			DeviceProperty property = this.devGetDeviceProperty("CCD_SET");

			return celsiusToKelvin(Float.valueOf(property.getValue().get(0)));
		}else{
			throw new UnsupportedOpException ("Operation not supported");
		}
		
	}
	
	/**
	 * {@inheritDoc}
	 * <p>
	 * Recovers "CCD_TEMP" from RTS2 camd.
	 */
	@Override
	public float camGetCCDCurrentTemperature() throws RTException {
		
		if (camCanControlTemperature()){
			DeviceProperty property = this.devGetDeviceProperty("CCD_TEMP");

			return celsiusToKelvin(Float.valueOf(property.getValue().get(0)));
		}else{
			throw new UnsupportedOpException ("Operation not supported");
		}
	}

	
	/**
	 * {@inheritDoc}
	 * <p>
	 * Execute <i>cmd: stopexpo</i> from RTS2 camd
	 */
	@Override
	public synchronized void camAbortExposure() throws RTException {
		
		if (camCanAbortExposure() && this.imageContext.getUuid() != null && !this.imageContext.isTransfered()){
			
			this.imageContext.reset();
			
			long time = Rts2Date.now();
			
			if (!this.devExecuteCmd ("stopexpo", true))
				throw new RTException ("Cannot abort exposure");
			
			//Message recovering
			String message = Rts2Messages.getMessageText (Rts2MessageType.error, getDeviceId(), time);
			if (message != null)
				throw new RTException(message);	
		}else{
			throw new UnsupportedOpException ("Operation not supported");
		}
	}

	/**
	 * {@inheritDoc}
	 * <p>
	 * Operation not supported by RTS2 camd.
	 */
	@Override
	public void camPulseGuide(int direction, long duration)
			throws RTException {

		throw new UnsupportedOpException ("Operation not supported");

	}

	
	/**
	 * {@inheritDoc}
	 * <p>
	 * Execute <i>expose</i> form RTS2 API
	 */
	@Override
	public synchronized String camStartExposure(boolean light) throws RTException {	
		
		if (exposureTime == -1){
			throw new RTException("No exposure time specified");
		}else if (imageContext.isExposing()){
			throw new RTException ("The previous exposure hasn't stoped yet.");
		}else{
									
			//Start exposure
			List<String> valueProp = new ArrayList<String>();
			valueProp.add(String.valueOf(exposureTime));
			this.devUpdateDeviceProperty("exposure", valueProp);
			
			
			Rts2GatewayDeviceManager gatewayDevManager = new Rts2GatewayDeviceManager();
			Rts2Cmd cmd = Rts2Cmd.getNewCmd(Rts2CmdType.expose);
			cmd.getParameters().put("ccd", getDeviceId());
			cmd.getParameters().put("fe", "XMLRPC_"+getDeviceId()+".fits");
			cmd.getParameters().put("overwrite", "1");
			
			
//			Rts2GatewayDeviceManager gatewayDevManager = new Rts2GatewayDeviceManager();
//			Rts2Cmd cmd = Rts2Cmd.getNewCmd(Rts2CmdType.runscript);
//
//			cmd.getParameters().put("d", this.getDeviceId());
//			
//			if (this.camHasShutter()){
//				if (light){
//					cmd.getParameters().put("s", "E "+String.valueOf(exposureTime));
//				}else{
//					cmd.getParameters().put("s", "D "+String.valueOf(exposureTime));
//				}
//			}else{
//				cmd.getParameters().put("s", "E "+String.valueOf(exposureTime));
//			}
			
			eu.gloria.rt.entity.environment.config.device.DeviceProperty checkingMethod = DeviceRTD.configDeviceManager.getProperty(getDeviceId(), "EXPOSURE_START_CHECK");
			String jsonContent;
			
			if (checkingMethod.getDefaultValue().equals("XMLRPC_PROPERTY")){
					
			//Check exposure start: XMLRPCD.scriptrunning based			
				SynchronizerExposeStart synchExposureStart = new SynchronizerExposeStart(Math.round(exposureTime/3)/*periodWaitTime*/, 10000 /*timeout*/);
				
				try {
					
					long time = Rts2Date.now();
					synchExposureStart.takeRts2Time(); //Take the rts2 time before starting the process.
					
					jsonContent = cmd.execute();
					
					LogUtil.info(this, "CameraRTD.camStartExposure():: Waiting for Rts2Message: 'exposure for'");									
						
					
					synchExposureStart.check(); //sleep until: startExposure or error or timeout
					LogUtil.info(this, "CameraRTD.camStartExposure():: camera is exposing....");
					
				} catch (Exception e) {
					LogUtil.severe(this, "CameraRTD.camStartExposure():: Error" + e.getMessage());
					throw new RTException("Cannot start exposure " + e.getMessage());
				}
			
			}else if (checkingMethod.getDefaultValue().equals("MESSAGES")){
			//Check exposure start: Message based
				Rts2SynchronizerMessage synchExposureStart = new Rts2SynchronizerMessage(2000 /*periodWaitTime*/, 10000 /*timeout*/, Rts2MessageType.info, getDeviceId(), false, "exposure for");
//				Rts2SynchronizerMessage synchExposureStart = new Rts2SynchronizerMessage(Math.round(exposureTime/3) /*periodWaitTime*/, 10000 /*timeout*/, Rts2MessageType.info, getDeviceId(), false, "exposure for");
									
				try {
					
					long time = Rts2Date.now();
					synchExposureStart.takeRts2Time(); //Take the rts2 time before starting the process.
					
					jsonContent = cmd.execute();
					
					LogUtil.info(this, "CameraRTD.camStartExposure():: Waiting for Rts2Message: 'exposure for'");
									
					
					Rts2Messages.handleErrorMessages("camera_start_exposure",Rts2MessageType.error, getDeviceId(), time);		
					
					synchExposureStart.check(); //sleep until: startExposure or error or timeout
					LogUtil.info(this, "CameraRTD.camStartExposure():: camera is exposing....");
					
				} catch (Exception e) {
					LogUtil.severe(this, "CameraRTD.camStartExposure():: Error" + e.getMessage());
					throw new RTException("Cannot start exposure " + e.getMessage());
				}
			}
			
			
			//At this point the camera started the exposure!!!!
		
			try {
				
				imageContext.exposeStart(dbProxy.getProxy().uuidCreate(UuidType.FILE));
				
				exposureTimer = new Timer(true);
				exposureTimer.schedule(new Task(exposureTime), 0, 5000);	
				
				LogUtil.info(this, "CameraRTD.camStartExposure():: returning UUID:" + imageContext.getUuid() );
				return imageContext.getUuid();
				
			} catch (Exception e) {				
				throw new RTException ("Error. " + e.getMessage());
			}
		}


	}
	
	/**
	 * Removes previous fits image using a SSH connection.
	 */
	private void sshRemovePreviousFitsImage() throws RTException{
		
		try{
			
			eu.gloria.rt.entity.environment.config.device.DeviceProperty sshUser = DeviceRTD.configDeviceManager.getProperty(this.getDeviceId(), "SSH_USER");
			eu.gloria.rt.entity.environment.config.device.DeviceProperty sshPw = DeviceRTD.configDeviceManager.getProperty(this.getDeviceId(), "SSH_PW");
			eu.gloria.rt.entity.environment.config.device.DeviceProperty sshHost = DeviceRTD.configDeviceManager.getProperty(this.getDeviceId(), "SSH_HOST");
			eu.gloria.rt.entity.environment.config.device.DeviceProperty sshPort = DeviceRTD.configDeviceManager.getProperty(this.getDeviceId(), "SSH_PORT");
			if (sshUser != null && sshPw != null && sshHost!=null && sshPort!=null){
				
				SshCmd cmd = new SshCmd();
				cmd.setUserLogin(sshUser.getDefaultValue());
				cmd.setUserPw(sshPw.getDefaultValue());
				cmd.setSudoPw(null);
				cmd.setHost(sshHost.getDefaultValue());
				cmd.setPort(sshPort.getDefaultValue());
				
				cmd.execute("/usr/share/gloria/rts/cmds/remove_current_fits_file.sh");
			}
			
		}catch(Exception ex){
			ex.printStackTrace();
			throw new RTException(ex);
		}
		
		
	}

	/**
	 * {@inheritDoc}
	 * <p>
	 * Operation not supported by RTS2 camd.
	 */
	@Override
	public void camStopExposure() throws RTException {
		
		throw new UnsupportedOpException ("Operation not supported");

	}

	
	/**
	 * {@inheritDoc}
	 * <p>
	 * Execute <i>lastimage</i> form RTS2 API
	 */
	@Override
	public Image camGetImage(ImageFormat format) throws RTException {
		
		//Check if there is a image to recover
		if (camImageReady()){
			
			if (format == ImageFormat.JPG){
				throw new UnsupportedOpException ("Up to now, only FITS content can be retrieved using this method.");
			}else{

				Image img = new Image();
				ImageContent imgCont = new ImageContent();

				//RTS2 images have only one plane ¿?
				img.setNumPlanes(1);

				//ROI
				img.setNumX(camGetROINumX());
				img.setNumY(camGetROINumY());

				try{
					//Image Data Type (LONG, DOUBLE y SHORT supported)
					imgCont.setContentType(camGetImageDataType());
				}catch (Exception e){
					throw new RTException ("Data type not supported");
				}


				//Image recovering
				Rts2GatewayDeviceManager gatewayDevManager = new Rts2GatewayDeviceManager();
				Rts2Cmd cmd = Rts2Cmd.getNewCmd(Rts2CmdType.lastimage);		

				cmd.getParameters().put("ccd", this.getDeviceId());
				byte[] response;

				try {
					long time = Rts2Date.now();
				
					response = cmd.executeBinary();		
					//Message recovering
					String message = Rts2Messages.getMessageText (Rts2MessageType.error, getDeviceId(), time);
					if (message != null)
						throw new RTException(message);	
										
				
					ByteBuffer buf = ByteBuffer.wrap(response);			

					//Image content in correct format
					if (imgCont.getContentType() == ImageContentType.DOUBLE){
						List<Double> content = imgCont.getContentDouble();				

						for (int i = 0; i<(response.length/getPixelByteSize(imgCont.getContentType()));i++){
							content.add(i,buf.getDouble());
						}
					}else if (imgCont.getContentType() == ImageContentType.LONG){
						List<Long> content = imgCont.getContentLong();

						for (int i = 0; i<(response.length/getPixelByteSize(imgCont.getContentType()));i++){
							content.add(i,buf.getLong());
						}
					}else if (imgCont.getContentType() == ImageContentType.SHORT){
						List<Short> content = imgCont.getContentShort();

						for (int i = 0; i<(response.length/getPixelByteSize(imgCont.getContentType()));i++){
							content.add(i,buf.getShort());
						}
					}			

					img.setImageContent(imgCont);
					return img;
				} catch (Exception e) {
					throw new RTException("Error getting  the image" + e.getMessage());
				}	
			}
		}else{
			throw new RTException("There is not an available image");
		}
		
	}
	
	
	/**
	 * {@inheritDoc}
	 * <p>
	 * Execute <i>hasimage</i> form RTS2 API
	 */
	@Override
	public boolean camImageReady() throws RTException {
		
		
		if ((imageContext.getUuid() != null) && (imageContext.isTransfered())){
			return true;
		}else{
			return false;
		}
			
			
//		Rts2GatewayDeviceManager gatewayDevManager = new Rts2GatewayDeviceManager();
//		Rts2Cmd cmd = Rts2Cmd.getNewCmd(Rts2CmdType.hasimage);		
//		
//		cmd.getParameters().put("ccd", this.getDeviceId());
//		String jsonContent;
//		
//		try {
//			long time = Rts2Date.now();
//			
//			jsonContent = cmd.execute();
//			//Message recovering
//			String message = Rts2Messages.getMessageText (Rts2MessageType.error, getDeviceId(), time);
//			if (message != null)
//				throw new RTException(message);	
//			
//			ObjectMapper mapper = new ObjectMapper();
//			HashMap<String, Object> info = (HashMap<String, Object>) mapper.readValue(jsonContent, Object.class);
//			Map<String, Object> map = (Map<String, Object>) info;
//			if (map.get("hasimage").toString().equals("true"))
//				return true;
//			else
//				return false;
//		} catch (Exception e) {
//			throw new RTException("Error checking ImageReady " + e.getMessage());
//		}
		
	}	
	
	
	/**
	 * {@inheritDoc}
	 * <p>
	 * Recovers "data_type" from RTS2 camd.
	 */
	@Override
	public ImageContentType camGetImageDataType()
			throws RTException {

		DeviceProperty property = this.devGetDeviceProperty("data_type");
		
		List<String> types = property.getPossibleValue();
		
		try{
			if (property.getValue().get(0).equals("LONG LONG"))
				return (ImageContentType.LONG);
			else
				return ImageContentType.fromValue(property.getValue().get(0));
		}catch (Exception e){
			throw new RTException("Type not supported.");
		}		
	}

	
	/**
	 * {@inheritDoc}
	 * <p>
	 * Sets "data_type" from RTS2 camd if writable.
	 */
	/*@Override
	public void camSetImageDataType(ImageContentType type)
			throws RTException {
		
		DeviceProperty property = this.devGetDeviceProperty("data_type");
		if (!property.isReadonly()){

			//List<String> types = property.getPossibleValue();

			List<String> valueProp = new ArrayList<String>();
			if(type == ImageContentType.LONG){
				valueProp.add("LONG LONG");
			}else{
				valueProp.add(type.value());		
			}
			
			long time = Rts2Date.now();

			if(!this.devUpdateDeviceProperty("data_type", valueProp))			
				throw new RTException("Cannot set the image data type");
			//Message recovering
			String message = Rts2Messages.  (Rts2MessageType.error, getDeviceId(), time);
			if (message != null)
				throw new RTException(message);	
		}else{
			throw new RTException("Operation not supported.");
		}
		
	}*/
	
	
	/**
	 * {@inheritDoc}
	 * <p>
	 * Check existence of COOLING from RTS2 camd
	 */
	@Override
	public boolean camCanSetCooler() throws RTException {

		try{
			DeviceProperty property = this.devGetDeviceProperty("COOLING");
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
	 * Count the number of "wheelx" properties of camd devices.
	 */
	@Override
	public List<String> camGetFilters() throws RTException {
		
		DeviceProperty prop;
		String propPre = "wheel";
		String propName = "wheelA";
		char propSuf = 'B';
		List<String> filters = new ArrayList<String>();
		
		while (true){
			try{
				prop =  ((CameraRTD) DeviceDiscoverer.getRTD(this.getDeviceId())).devGetDeviceProperty(propName);
								
				if (DeviceRTD.configDeviceManager.getDevice(prop.getValue().get(0)) != null)
					filters.add(prop.getValue().get(0));
				else
					LogUtil.info(this, "CameraRTD.camGetFilters() Filter: " + prop.getValue().get(0) + " isn't in environmet file");
								
				propName = propPre + propSuf;			
				propSuf++;
			}catch (Exception e){
				if (e.getMessage().contains("The property does not exist")){
					return filters;
				}else{
					throw new RTException (e.getMessage());
				}
			}
		}
	}
	
	/**
	 * {@inheritDoc} 
	 * <p>
	 * Checks and recovers "focuser" from RTS2 camd.
	 */
	@Override
	public String camGetFocuser() throws RTException {

		try{
			DeviceProperty property = this.devGetDeviceProperty("focuser");
			return (property.getValue().get(0));
		}catch (Exception e){
			if (e.getMessage().contains("The property does not exist")){
				return null;
			}			
		}
		return null;		
	}	
	

	/**
	 * {@inheritDoc}
	 * <p>
	 * Operation not supported by RTS2 camd.
	 */
	@Override
	public int camGetBitDepth() throws RTException {
		
		throw new UnsupportedOpException ("Operation not supported");
	}

	/**
	 * {@inheritDoc}
	 * <p>
	 * Operation not supported by RTS2 camd.
	 */
	@Override
	public void camSetBitDepth(int bits) throws RTException {

		throw new UnsupportedOpException ("Operation not supported");		
	}

	/**
	 * Conversion from Celsius degrees to Kelvin degrees
	 * 
	 * @param celsius degrees
	 * @return Kelvin degrees
	 */
	private float celsiusToKelvin (float celsius){
		return celsius + 273;
	}
	
	/**
	 * Conversion from Kelvin degrees to Celsius degrees
	 * 
	 * @param kelvin degrees
	 * @return Celsius degrees
	 */
	private float kelvinToCelsius (float kelvin){
		return kelvin -273;
	}
	
	/**
	 * Compute the number of byte in an image pixel
	 * 
	 * @param dataType {@link ImageContentType}
	 * @return number of bytes
	 */
	private int getPixelByteSize (ImageContentType dataType){
				
		int data = 0;
		if (dataType == ImageContentType.DOUBLE)
			data = Rts2Constants.RTS2_DATA_DOUBLE;
		else if (dataType == ImageContentType.SHORT)
			data = Rts2Constants.RTS2_DATA_SHORT;
		else if (dataType == ImageContentType.LONG)
				data = Rts2Constants.RTS2_DATA_LONGLONG;
				
		if (data == Rts2Constants.RTS2_DATA_ULONG)
			return 4;
		
		return (Math.abs(data) / 8);
	}

	@Override
	public int camGetContinueModeQuality() throws RTException {

		throw new UnsupportedOpException ("Operation not supported");
	}

	@Override
	public void camSetContinueModeQuality(int value) throws RTException {

		throw new UnsupportedOpException ("Operation not supported");
		
	}

	@Override
	public int camGetOneShotModeQuality() throws RTException {

		throw new UnsupportedOpException ("Operation not supported");
	}

	@Override
	public void camSetOneShotModeQuality(int value) throws RTException {

		throw new UnsupportedOpException ("Operation not supported");
		
	}

	@Override
	public String camGetContinueModeImagePath() throws RTException {

		throw new UnsupportedOpException ("Operation not supported");
	}
	

	@Override
	public boolean camGetAutoGain() throws RTException {

		throw new UnsupportedOpException ("Operation not supported");
	}

	@Override
	public void camSetAutoGain(boolean value) throws RTException {

		throw new UnsupportedOpException ("Operation not supported");
		
	}

	@Override
	public boolean camGetAutoExposureTime() throws RTException {

		throw new UnsupportedOpException ("Operation not supported");
	}

	@Override
	public void camSetAutoExposureTime(boolean value) throws RTException {

		throw new UnsupportedOpException ("Operation not supported");
		
	}

	@Override
	public String camStartContinueMode() throws RTException {
		
		throw new UnsupportedOpException ("Operation not supported");
		
//		this.exposureContinueModeOn = true;
//
//		return UUID_IMG_CONTINUE_MODE;
		
	}

	@Override
	public void camStopContinueMode() throws RTException {
		
		throw new UnsupportedOpException ("Operation not supported");

//		this.exposureContinueModeOn = false;
		
	}

	@Override
	public String camGetImageURL(String uid, ImageFormat format) throws RTException {
			
		//check the Image Format
		if (format != ImageFormat.JPG && format != ImageFormat.FITS ){
			throw new UnsupportedOpException ("Unsupported Format:" + format);
		}				

		if (uid == null)
			throw new RTException ("No UUID provided");
		
		eu.gloria.rt.entity.environment.config.device.DeviceProperty publicServletPath = DeviceRTD.configDeviceManager.getProperty(this.getDeviceId(), "PUBLIC_SERVLET_PATH");

		if (this.imageContext.getUuid() != null  && this.imageContext.getUuid().equals(uid)){ //Requesting current image
			
			if (imageContext.getTransfer() == TransferStatus.FINISHED){				
				if (format == ImageFormat.JPG){
					return publicServletPath.getDefaultValue()+ uid + "&format=JPG";
				}else{
					return publicServletPath.getDefaultValue()+ uid + "&format=FITS";
				}				
			}else{
				if (imageContext.getTransfer() == TransferStatus.FAILED){
					throw new RTException(imageContext.getTransfer().toString());
				}else{
					throw new RTException("NOT_AVAILABLE"); //<- 
				}
			}
			
		} else { //historical image			
			
			String path = null;

			if (format == ImageFormat.JPG){
				path = publicServletPath.getDefaultValue()+ uid + "&format=JPG";
			}else{
				path = publicServletPath.getDefaultValue()+ uid + "&format=FITS";
			}
				
			try {
				URL url = new URL(path+"&exist=1");
				URLConnection urlConnection = url.openConnection();	
				HttpURLConnection httpUrlConnection = (HttpURLConnection)urlConnection;
				if (httpUrlConnection.getResponseCode() == HttpServletResponse.SC_OK){
					
					return path;
					
				}else{
					throw new RTException("NOT_AVAILABLE");
				}
				
			} catch (MalformedURLException e) {
				e.printStackTrace();
				throw new RTException("NOT_AVAILABLE");
			} catch (IOException e) {
				e.printStackTrace();
				throw new RTException("NOT_AVAILABLE");
			}	
			
		}
		
//		if (UUID_IMG_CONTINUE_MODE.equals(uid)){ //Continue mode -> last image
//			if (format != ImageFormat.JPG){
//				throw new UnsupportedOpException("UNSUPPORTED continue image format: " + format);
//			}
//			eu.gloria.rt.entity.environment.config.device.DeviceProperty lastImageURL = DeviceRTD.configDeviceManager.getProperty(this.getDeviceId(), "DEFAULT_CONTINUE_MODE_IMG_URL");
//			return lastImageURL.getDefaultValue();		
//		}
//		
//		eu.gloria.rt.entity.environment.config.device.DeviceProperty privatePath = DeviceRTD.configDeviceManager.getProperty(this.getDeviceId(), "PRIVATE_PATH");
//		String formatExt = null;
//		if (format == ImageFormat.JPG){
//			formatExt = "jpg";
//		}else{
//			formatExt = "fits";
//		}
//		
//		String fullFileName = privatePath.getDefaultValue() + uid + "." + formatExt;
//		
//		
//		File file = new File(fullFileName);
//		if (file.exists()){
//			
//			eu.gloria.rt.entity.environment.config.device.DeviceProperty publicPath = DeviceRTD.configDeviceManager.getProperty(this.getDeviceId(), "PUBLIC_PATH");
//			String url = publicPath.getDefaultValue() + uid + "." + formatExt;
//			return url;
//			
//		} else {
//			throw new RTException("NOT_AVAILABLE");
//		}
		
		
	}

	@Override
	public String camGetOneShotModeImagePath() throws RTException {
		
		eu.gloria.rt.entity.environment.config.device.DeviceProperty publicPath = DeviceRTD.configDeviceManager.getProperty(this.getDeviceId(), "PUBLIC_SERVLET_PATH");

		return publicPath.getDefaultValue();
	}
	
	@Override
	public List<Double> camGetObjectExposureTime(String filter, String object) throws RTException {
		
		List<Double> result = null;
		
		double longitude = Config.getPropertyDouble("rt_config", "rts_longitude");
		double latitude = Config.getPropertyDouble("rt_config", "rts_latitude");		

		Catalogue catalogue = new Catalogue(longitude, latitude, 0);
		ObjInfo objInfo = catalogue.getObject(object);
		if (objInfo == null){
			
			LogUtil.info(this, "Camera. Catalogue:: Object NOT found:" + object);
			throw new RTException("Not Object Found");
			
		}else{
			
			LogUtil.info(this, "Mount. Catalogue:: Object found:" + object);				
			
			String general = null;
			double generalMin = 0;
			double generalMax = 0;
			try{
				general = DeviceRTD.configDeviceManager.getProperty(getDeviceId(), "GENERAL_EXP").getDefaultValue();
				String[] dataGeneral = general.split(":");		
				generalMin = Double.valueOf(dataGeneral[0]);
				generalMax = Double.valueOf(dataGeneral[1]);
			} catch (Exception e){
				
			}
			
					
			if (objInfo.getCategory() == ObjCategory.MajorPlanetAndMoon){
				String planet = null;
				eu.gloria.rt.entity.environment.config.device.DeviceProperty planetProperty = DeviceRTD.configDeviceManager.getProperty(getDeviceId(), "PLANET_EXP");

				if (planetProperty == null){
					if (general != null){
						result = new ArrayList <Double> ();
						
						result.add(generalMin);
						result.add(generalMax);
					}else
						return result;
				}
				
				planet = planetProperty.getDefaultValue();
				String[] dataPlanet = planet.split(":");		
				planetMin = Double.valueOf(dataPlanet[0]);
				planetMax = Double.valueOf(dataPlanet[1]);
				
				result = new ArrayList <Double> ();
				
				result.add(planetMin);
				result.add(planetMax);
				
			}else if (objInfo.getCategory() == ObjCategory.OutsideSSystemObj){
				String[] data = object.split(" ");
				
				int index = 0;
				for (String a : data){
					if (a.isEmpty())
						index++;
					else
						break;
				}
				
				if (data[index].toUpperCase().startsWith("M")){	//Messier
					String messier = null ;
					eu.gloria.rt.entity.environment.config.device.DeviceProperty messierProperty = DeviceRTD.configDeviceManager.getProperty(getDeviceId(), "MESSIER_EXP");
					if (messierProperty == null){
						if (general != null){
							result = new ArrayList <Double> ();
							
							result.add(generalMin);
							result.add(generalMax);
						}
						return result;
					}
					
					messier = messierProperty.getDefaultValue();
					String[] dataMessier = messier.split(":");		
					mMin = Double.valueOf(dataMessier[0]);
					mMax = Double.valueOf(dataMessier[1]);

					result = new ArrayList <Double> ();

					result.add(mMin);
					result.add(mMax);
					
					
				}else if (data[index].toUpperCase().startsWith("NGC")){	//NGC
					String ngc = null;
					eu.gloria.rt.entity.environment.config.device.DeviceProperty ngcProperty = DeviceRTD.configDeviceManager.getProperty(getDeviceId(), "NGC_EXP");
					if (ngcProperty == null){	
						
						if (general != null){
							result = new ArrayList <Double> ();
							
							result.add(generalMin);
							result.add(generalMax);
						}
						return result;
					}
					
					ngc = ngcProperty.getDefaultValue();
					String[] dataNgc = ngc.split(":");		
					ngcMin = Double.valueOf(dataNgc[0]);
					ngcMax = Double.valueOf(dataNgc[1]);

					result = new ArrayList <Double> ();

					result.add(ngcMin);
					result.add(ngcMax);
					
				}else if (general != null){
					result = new ArrayList <Double> ();
					
					result.add(generalMin);
					result.add(generalMax);
				}
				
			}
			
			
		}
		
		return result;
	}
	

	private void transferFileBasedOnXMLRPC (String UUID) throws RTException{
		
		String localPath;
		
		try {
			
			eu.gloria.rt.entity.environment.config.device.DeviceProperty xmlRpcDevId = DeviceRTD.configDeviceManager.getProperty(this.getDeviceId(), "DEV_ID_XMLRPC");
			
			Rts2Cmd cmd = Rts2Cmd.getNewCmd(Rts2CmdType.get);
			cmd.getParameters().put("d", xmlRpcDevId.getDefaultValue());
			cmd.getParameters().put("e", "1");
			
			String jsonContent = cmd.execute();
			
			List<String> valueProp = new ArrayList<String>();
			valueProp.add(this.getDeviceId()+"_lastimage");
			Rts2GatewayDevicePropertiesRequest propReq = new Rts2GatewayDevicePropertiesRequest(RequestType.CUSTOM, valueProp);
			Rts2CmdGetResponse resp = new Rts2CmdGetResponse(jsonContent, propReq);				
			
			localPath = resp.getVars().get(0).getValue().get(0);
			
			eu.gloria.rt.entity.environment.config.device.DeviceProperty rts2ImgUtrPort = DeviceRTD.configDeviceManager.getProperty(this.getDeviceId(), "RTS2_IMG_URL_PORT");
			//cmd = Rts2Cmd.getNewImageCmd(Rts2ImageCmdType.fits);
			cmd = Rts2Cmd.getNewImageCmd(Rts2ImageCmdType.fits);
			if (rts2ImgUtrPort != null) {
				cmd.setPort(rts2ImgUtrPort.getDefaultValue());
			}
			cmd.setImagPath(localPath);
			
			byte[] response;

			long time = Rts2Date.now();

			response = cmd.executeBinary();		
			//Message recovering
/*	ERR001		String message = Rts2Messages.getMessageText (Rts2MessageType.error, getDeviceId(), time);
			if (message != null)
				throw new RTException(message);	
*/			
			eu.gloria.rt.entity.environment.config.device.DeviceProperty privatePath = DeviceRTD.configDeviceManager.getProperty(this.getDeviceId(), "PRIVATE_PATH");
			
			File newfile = new File(privatePath.getDefaultValue()+ UUID + ".fits");
			OutputStream out = new FileOutputStream(newfile);
		
			out.write(response);
			out.close();
			
			//Generates the JPG from FITS
			generateFormat(ImageFormat.JPG, UUID);
		
		}catch (Exception e) {
			throw new RTException("Image path cannot be recovered. " + e.getMessage());
		}
	}
	
	private void transferFileBasedOnXMLRPC2 (String UUID) throws RTException{
				
		String localPath;
		
		imageContext.setTransfer(TransferStatus.STARTED);
		
		try {
			
			String imgAppName = Config.getProperty("rtd_rts2", "rts2.http.img.appname");
			if (!imgAppName.isEmpty()){
				imgAppName = imgAppName +"/";
			}
			
			eu.gloria.rt.entity.environment.config.device.DeviceProperty xmlRpcDevId = DeviceRTD.configDeviceManager.getProperty(this.getDeviceId(), "DEV_ID_XMLRPC");
			
			Rts2Cmd cmd = Rts2Cmd.getNewCmd(Rts2CmdType.get);
			cmd.getParameters().put("d", xmlRpcDevId.getDefaultValue());
			cmd.getParameters().put("e", "1");
			
			String jsonContent = cmd.execute();			
			
			
			List<String> valueProp = new ArrayList<String>();
			valueProp.add(this.getDeviceId()+"_lastimage");
			Rts2GatewayDevicePropertiesRequest propReq = new Rts2GatewayDevicePropertiesRequest(RequestType.CUSTOM, valueProp);
			Rts2CmdGetResponse resp = new Rts2CmdGetResponse(jsonContent, propReq);				
			
			localPath = resp.getVars().get(0).getValue().get(0).replaceFirst("/", "");
			
			eu.gloria.rt.entity.environment.config.device.DeviceProperty privatePath = DeviceRTD.configDeviceManager.getProperty(this.getDeviceId(), "PRIVATE_PATH");
			
			//JPEG first (the .fits needed will be move when fits transfer to repository
			cmd = Rts2Cmd.getNewImageCmd(Rts2ImageCmdType.fits);
			cmd.setImagPath(localPath);
			cmd.setAppName(imgAppName + "jpeg");
			cmd.getParameters().put("lb", "");
			
			long time = Rts2Date.now();

			byte[] response = cmd.executeBinary();
			
			LogUtil.info(this, "CameraRTD.transferFileBasedOnXMLRPC2 jpg received");
			File newfile = new File(privatePath.getDefaultValue()+ UUID + ".jpg");
			OutputStream out = new FileOutputStream(newfile);
		
			out.write(response);
			out.close();
			
			LogUtil.info(this, "CameraRTD.transferFileBasedOnXMLRPC2 temporal jpg file created");
			
			fileToRepository(privatePath.getDefaultValue()+ UUID + ".jpg");
			
			LogUtil.info(this, "CameraRTD.transferFileBasedOnXMLRPC2 jpg imagen in repository");
		
			
			Boolean rtdLocal = Boolean.parseBoolean(Config.getProperty("rtd_rts2","rtd.local"));
			
			if (rtdLocal){		
				localPath = resp.getVars().get(0).getValue().get(0);
				
				try{
					fitsHeadModification(localPath, UUID + ".fits");
				}catch(Exception ex){
					ex.printStackTrace();
					LogUtil.severe(this, "CameraRTD.transferFileBasedOnXMLRPC2(). Error changing fits head=[" + localPath + "]");
				}
				
				
				fileToRepository(localPath);
			}else{
			
				//*****************************************Recover the fits file from XMLRPC*************************************************************************
				//cmd = Rts2Cmd.getNewImageCmd(Rts2ImageCmdType.fits);
				cmd = Rts2Cmd.getNewImageCmd(Rts2ImageCmdType.fits);
				cmd.setImagPath(localPath);
				cmd.setAppName(imgAppName + "fits"); //jpeg for jpeg image e.g.: http://host:port/fits/tmp/xxxxx.fits,http://host:port/jpeg/tmp/xxxxx.fits  

				
				time = Rts2Date.now();

				response = cmd.executeBinary();		


				//Message recovering
				/*	ERR001		String message = Rts2Messages.getMessageText (Rts2MessageType.error, getDeviceId(), time);
			if (message != null)
				throw new RTException(message);	
				 */		
				
				newfile = new File(privatePath.getDefaultValue()+ UUID + ".fits");
				out = new FileOutputStream(newfile);

				out.write(response);
				out.close();
				
				try{
					fitsHeadModification(privatePath.getDefaultValue()+UUID + ".fits", UUID + ".fits");
				}catch(Exception ex){
					ex.printStackTrace();
					LogUtil.severe(this, "CameraRTD.transferFileBasedOnXMLRPC2(). Error changing fits head=[" + privatePath.getDefaultValue() + "]");
				}

				fileToRepository(privatePath.getDefaultValue()+ UUID + ".fits");
			
			}			
			
			imageContext.setTransfer(TransferStatus.FINISHED);
		
		}catch (Exception e) {
			e.printStackTrace();
			imageContext.setTransfer(TransferStatus.FAILED);
			throw new RTException("Image path cannot be recovered. " + e.getMessage());
		}
	}
	

	
	private void fileToRepository (String path) throws RTException{
				
		LogUtil.info(this, "CameraRTD.fileToRepository");
		
		String idOp = OpManager.getOpManager().getExtExecInfo().getUuidOp();
		
		EntityManager em = DBUtil.getEntityManager();
		ObservingPlanManager manager = new ObservingPlanManager();
		
//		ObservingPlan dbOp = null;
		
		try{
			
			DBUtil.beginTransaction(em);
			
//			dbOp = manager.get(em, idOp);
//			
//			if (dbOp != null){				
				
				//DBRepository->Create the Observing Plan
				eu.gloria.rt.entity.db.ObservingPlan repOP = new eu.gloria.rt.entity.db.ObservingPlan();
				repOP.setOwner(ObservingPlanOwner.USER);
				repOP.setType(ObservingPlanType.OBSERVATION);
				repOP.setUser(Config.getProperty("rt_config", "rts_name", "RTS_DEFAULT"));
				repOP.setUuid(idOp);
				
				try{
					String uuid = dbProxy.getProxy().opCreate(repOP);
					repOP = dbProxy.getProxy().opGet(uuid);
					
					LogUtil.info(this, "CameraRTD.fileToRepository(" + idOp + "). DBRepository OP created. UUID= " + uuid);
					
				}catch(Exception ex){					
					if (!ex.getMessage().contains("The Observing Plan already exists into the Repository database."))
						throw new RTException("Error registering the Observing Plan into the DBRepository.");
				}
				
				eu.gloria.rt.entity.db.File file = null;
				
				//Resolve the file format.
            	FileFormat fileFormat = FileFormat.FITS;
            	if (path.endsWith("jpg")){
            		fileFormat = FileFormat.JPG;
            	}
            	
				//DBRepository->Create the File information
				try{
						file = new eu.gloria.rt.entity.db.File();
						file.setContentType(FileContentType.OBSERVATION);
						file.setDate(getDate(new Date()));
						file.setType(FileType.IMAGE);
						file.setUuid(imageContext.getUuid());
						
						if (fileFormat == FileFormat.JPG)
							dbProxy.getProxy().fileCreate(idOp, file);

						LogUtil.info(this, "CameraRTD.fileToRepository(" + idOp + "). CREATED GLORIA file UUID=" + file.getUuid());

					}catch(Exception ex){
						imageContext.setTransfer(TransferStatus.FAILED);
						throw new Exception("Error registering a file into the DBRepository.");
					}
				
				
				
				//Creates the format
            	String urlSource = "file://" + path;
            	            	
            	try{
        			dbProxy.getProxy().fileAddFormat(file.getUuid(), fileFormat, urlSource);
        			
        			LogUtil.info(this, "CameraRTD.fileToRepository(" + idOp + "). UPLOADED file format. url=" + urlSource);
        		}catch(Exception ex){
					throw new Exception("Error adding a file format to a file into the DBRepository. urlSourcefile=" + urlSource);
				}
            	
//			}
			
		} catch (Exception ex) {
			ex.printStackTrace();
			DBUtil.rollback(em);	
			LogUtil.severe(this, "CameraRTD.fileToRepository  ERROR" +ex.getMessage());
			throw new RTException(ex.getMessage());
			
		} finally {
			DBUtil.close(em);
		}
		
	}
	
	private XMLGregorianCalendar getDate(Date date) throws Exception{
    	GregorianCalendar c = new GregorianCalendar();
		c.setTime(date);
		XMLGregorianCalendar xmlCalendar = DatatypeFactory.newInstance().newXMLGregorianCalendar(c);
		return xmlCalendar;
    }
	
	private void transferFileBasedOnLastImage(String UUID) throws RTException{
		
		String localPath;
		
		byte[] response = null;
		try {
			
			//Image recovering....http://XXXXXX/api/lastimage?ccd=andor
			Rts2Cmd cmd = Rts2Cmd.getNewCmd(Rts2CmdType.lastimage);		
			cmd.getParameters().put("ccd", this.getDeviceId());

			long time = Rts2Date.now();
			URL source = cmd.executeBinaryURL();	
			
			//Checks possible errors.
			String message = Rts2Messages.getMessageText (Rts2MessageType.error, getDeviceId(), time);
			if (message != null)
				throw new RTException(message);	
			
			//Write the content to a file.
			eu.gloria.rt.entity.environment.config.device.DeviceProperty privatePath = DeviceRTD.configDeviceManager.getProperty(this.getDeviceId(), "PRIVATE_PATH");
			String rawFileFullPath = privatePath.getDefaultValue()+ UUID + ".raw";
			File newfile = new File(rawFileFullPath);
			
			OutputStream out = null;
			InputStream is = null;
			try{
				
				//Conection
				URLConnection urlCon = source.openConnection();
				
				//Steams
				is = urlCon.getInputStream();
				out = new FileOutputStream(newfile);

				//write the content
				byte[] array = new byte[20000]; 
				int read = is.read(array);
				while (read > 0) {
					out.write(array, 0, read);
					read = is.read(array);
				}

			}finally{
				
				if (is != null){
					try{
						is.close();
					}catch(Exception ex){
						throw new RTException("Impossible to close the input stream for the url [" + source.toString() + "]. "+ ex.getMessage());
					}
				}
				
				if (out != null){
					try{
						out.close();
					}catch(Exception ex){
						throw new RTException("Impossible to close the streaming for the output file [" + rawFileFullPath + "]. "+ ex.getMessage());
					}
				}
				
			}
			
			
			//Delete the raw head and generates fit file
			eu.gloria.rt.entity.environment.config.device.DeviceProperty scriptGeneratorFitsFullpath = DeviceRTD.configDeviceManager.getProperty(this.getDeviceId(), "SCRIPT_GENERATOR_FITS_FROM_RAW");
			String pyCmd = scriptGeneratorFitsFullpath.getDefaultValue();
			String fitsFilefullPath = privatePath.getDefaultValue()+ UUID + ".fits";
			String[] pyCmdArguments = {
					rawFileFullPath,
					fitsFilefullPath
			};
			
			Executor executor = new Executor();
			ExecResult execResult = executor.execute(pyCmd, pyCmdArguments);
			
			if (execResult.getCode() != 0){
				String[] names = {"file_raw", "file_fits"};
				String[] values = {rawFileFullPath, fitsFilefullPath};
				throw new Exception("Error Creating fits archive." + LogUtil.getLog(names, values) + " " + execResult.getOutput());
			}
			
			//Generates the JPG from FITS
			/*eu.gloria.rt.entity.environment.config.device.DeviceProperty scriptGeneratorJpgFullpath = DeviceRTD.configDeviceManager.getProperty(this.getDeviceId(), "SCRIPT_GENERATOR_JPG_FROM_FITS");
			String pyCmd2 = scriptGeneratorJpgFullpath.getDefaultValue();
			String jpgFilefullPath = privatePath.getDefaultValue()+ UUID + ".jpg";
			String[] pyCmd2Arguments = {
					fitsFilefullPath,
					jpgFilefullPath
			};
			
			execResult = executor.execute(pyCmd2, pyCmd2Arguments);
			
			if (execResult.getCode() != 0){
				String[] names = {"file_fits", "file_jpg"};
				String[] values = {fitsFilefullPath, jpgFilefullPath};
				throw new Exception("Error Creating jpg archive." + LogUtil.getLog(names, values) + " " + execResult.getOutput());
			}*/
			
			//Generates the JPG from FITS
			generateFormat(ImageFormat.JPG, UUID);
			
		}catch (Exception e) {
			String[] names = {"UUID"};
			String[] values = {UUID};
			String msg = "Image path cannot be recovered." + LogUtil.getLog(names, values) + e.getMessage();
			LogUtil.severe(this, msg);
			throw new RTException(msg);
		}
	}
	
	/**
	 * Generates an image in a proper format from FITS format.
	 * @param targetFormat Target format
	 * @param UUID Image UUID
	 * @throws RTException In error case.
	 */
	private void generateFormat(ImageFormat targetFormat, String UUID) throws RTException{
		
		eu.gloria.rt.entity.environment.config.device.DeviceProperty privatePath = DeviceRTD.configDeviceManager.getProperty(this.getDeviceId(), "PRIVATE_PATH");
		
		if (targetFormat == ImageFormat.JPG){
			
			//Generates the JPG from FITS
			String fitsFilefullPath = privatePath.getDefaultValue() + UUID + ".fits";
			String jpgFilefullPath = privatePath.getDefaultValue() + UUID + ".jpg";
			try{
				
				ImagePlus imp = IJ.openImage(fitsFilefullPath);
				JpegWriter.save(imp, jpgFilefullPath, 100);
			}catch(Exception ex){
				String[] names = {"file_fits", "file_jpg"};
				String[] values = {fitsFilefullPath, jpgFilefullPath};
				throw new RTException("Error Creating jpg archive." + LogUtil.getLog(names, values) + ". " + ex.getMessage());
			}
			
		} else if (targetFormat == ImageFormat.FITS){
			//By default is this format...all right!!!
		} else {
			throw new UnsupportedOpException ("Unssuported Conversion Image format:" + targetFormat);
		}
			
		
	}
	
	@Override
	public List<ImageFormat> camGetOneShotModeImageFormats() throws RTException {
		
		List<ImageFormat> result = new ArrayList<ImageFormat>();
		result.add(ImageFormat.JPG);
		result.add(ImageFormat.FITS);
		return result;
	}
	
	@Override
	public List<ImageFormat> camGetContinueModeImageFormats() throws RTException {
		
		List<ImageFormat> result = new ArrayList<ImageFormat>();
		result.add(ImageFormat.JPG);
		return result;
	}
	

	private void fitsHeadModification(String fitsFilefullPath, String fitsFileName) throws Exception{
		
		//Get the header in order to transform it.
		DataInputStream dis = new DataInputStream( new BufferedInputStream( new FileInputStream(fitsFilefullPath) ) );
		FitsHeader headerfile = new FitsHeader(dis);
		
		//It is already in RTS2 fits
//		headerfile.insertKeywordAt(new FitsKeyword("SIMPLE", true, "FITS Standard"), 5);
		
		headerfile.insertKeywordAt(new FitsKeyword("NAME", fitsFileName, "Name file"), 5);
		
		//Telescope
		String telescopeName = Config.getProperty("rt_config","rts_name");
		headerfile.insertKeywordAt(new FitsKeyword("TELESCOP", telescopeName, "Telescope name"), 6);
				
		//user
		eu.gloria.rtc.op.OpInfo opInfo = OpManager.getOpManager().getOp();
		String user = opInfo.getUser();
		if (user== null || user.isEmpty()) user= "GLORIA";
		headerfile.insertKeywordAt(new FitsKeyword("OBSERVER", user, "User name"), 7);
		
		//Object
		List<eu.gloria.rt.entity.environment.config.device.Device> mountIds = DeviceRTD.configDeviceManager.getDeviceByType (DeviceType.MOUNT);
		if (mountIds.size() > 1)
			throw new Exception ("CameraRTD.fitsHeadModification().Error recovering the mount.");
		
		RTDMountInterface mount = (RTDMountInterface) DeviceDiscoverer.getRTD(mountIds.get(0).getShortName());
		String pointedObject = mount.mntGetPointedObject();
		LogUtil.info(this, "CameraRTD.fitsHeadModification(). Pointed Object Name:" + pointedObject);
		headerfile.insertKeywordAt(new FitsKeyword("OBJECT", pointedObject, "Object name given by the user"), 8);
		
		//FilterWheel		
		List<String> filters = camGetFilters();
		int index = 9;
		if (filters.isEmpty()){
			headerfile.insertKeywordAt(new FitsKeyword("FILTER", "OPEN", "Type of filter used"), index);
			index++;
		}else{	
			char prefix = ' ';
			for (String filter : filters){
				RTDFilterWheelInterface filterRTD = (RTDFilterWheelInterface) DeviceDiscoverer.getRTD(filter);
				if (prefix == ' '){
					headerfile.insertKeywordAt(new FitsKeyword("FILTER", filterRTD.fwGetFilterKind(), "Type of filter used"), index);				
					prefix='A';
				}else{
					headerfile.insertKeywordAt(new FitsKeyword("FILTER"+prefix, filterRTD.fwGetFilterKind(), "Type of filter used"), index);
				}
				prefix++;			
				index++;
			}		
		}
		
		//Exposure time
		double exposureTime = camGetExposureTime();
		headerfile.insertKeywordAt(new FitsKeyword("EXPTIME", exposureTime, "Exposure time in seconds"), index);
		index++;
		
		//DateTime 
		Date now = new Date();
		Date gmtNow = DateTools.getGMT(now);
		String date = DateTools.getDate(gmtNow, "yyyy-MM-dd");
		String time = DateTools.getDate(gmtNow, "HH:mm:ss");
		String dateTime = date + "T" + time;
		headerfile.insertKeywordAt(new FitsKeyword("DAT-OBS", dateTime, "UT start of exposure"), index);
		index++;
		
		//Observatory
		headerfile.insertKeywordAt(new FitsKeyword("LAT", Config.getProperty("rt_config", "rts_latitude"), "Latitude"), index);
		index++;
		headerfile.insertKeywordAt(new FitsKeyword("LNG", Config.getProperty("rt_config", "rts_longitude"), "Longitude"), index);
		index++;
		
		//RADEC position
		String ra = null;
		String dec = null;
		try{
			ra = String.valueOf(mount.mntGetPosAxis1());
			dec = String.valueOf(mount.mntGetPosAxis2());
		}catch (RTException e){
			ra = "unknown";
			dec = "unknown";
			LogUtil.info(this, "CameraRTD.fitsHeadModification(). Error recovering the RADEC position");
		}
		headerfile.insertKeywordAt(new FitsKeyword("RA", ra, "RA coordenate"), index);
		index++;
		headerfile.insertKeywordAt(new FitsKeyword("DEC", dec, "DEC coordenate"), index);
		index++;
		
		//TYPE
		headerfile.insertKeywordAt(new FitsKeyword("IMAGETYP", "SKY", "Type of Image"), index);
		index++;		
		
		
					
		// Obtain the rest of the image data.
		FitsData v = new FitsData(headerfile,dis,true);
		dis.close();
		FitsHDUnit p = new FitsHDUnit(headerfile,v);
		DataOutputStream dos = new DataOutputStream( new BufferedOutputStream( new FileOutputStream(fitsFilefullPath) ) );
		
		// Write a new file with the header modified and the same image data.
		p.writeFile(dos);
		dos.close();
		
	}
	
	/**
	 * Blocks until RTS2 camera starts the exposure process. KK
	 * @author jcabello
	 *
	 */
	public class SynchronizerExposeStart extends ContextSynchronizer{
		
		private long rts2StartTime;
		

		public SynchronizerExposeStart(long waitTime, long timeout) {
			
			super(waitTime, timeout);
			
			this.rts2StartTime = Rts2Date.now();
			
		}
		
		public void takeRts2Time(){
			this.rts2StartTime = Rts2Date.now();
		}

		@Override
		public boolean isValidContext() throws RTException {
			
			//Message recovering..Errors
//			String message = Rts2Messages.getMessageText (Rts2MessageType.error, getDeviceId(), rts2StartTime);
//			if (message != null)
//				throw new RTException(message);	
//			
//			message = Rts2Messages.getMessageText (Rts2MessageType.info, getDeviceId(), rts2StartTime);
//			if (message!= null && message.indexOf("exposure start....") != -1){ //TODO
//				return true;
//			}
//			
//			return false;
			
			
//			eu.gloria.rt.entity.environment.config.device.DeviceProperty xmlRpcDevId = DeviceRTD.configDeviceManager.getProperty(getDeviceId(), "DEV_ID_XMLRPC");
//			
//			Rts2Cmd cmd = Rts2Cmd.getNewCmd(Rts2CmdType.get);
//			cmd.getParameters().put("d", xmlRpcDevId.getDefaultValue());
//			cmd.getParameters().put("e", "1");
//			
//			String jsonContent = cmd.execute();
//			
//			List<String> valueProp = new ArrayList<String>();
//			valueProp.add(getDeviceId()+"_scriptrunning");
//			Rts2GatewayDevicePropertiesRequest propReq = new Rts2GatewayDevicePropertiesRequest(RequestType.CUSTOM, valueProp);
//			Rts2CmdGetResponse resp = new Rts2CmdGetResponse(jsonContent, propReq);				
//			
//			Boolean scriptRunning = null;
//			
//			if ((resp.getVars().get(0).getValue().get(0)).equals("1"))
//				scriptRunning =  true;
//			else
//				scriptRunning = false;	
//			
//			return scriptRunning;
			
			if (((DeviceCamera) DeviceDiscoverer.devGetDevice(getDeviceId(), false)).getActivityState() == ActivityStateCamera.EXPOSING)
				return true;
			else
				return false;	
			
		}
		
	}
	
	public class Task extends TimerTask {
		
		private TimeOut timeOut;
		private boolean running;
		
		public Task(double exposureTime){
			
			int exposureTimeSegs = ((int) exposureTime) + 1;
			this.timeOut = new TimeOut((exposureTimeSegs * 1000) + 40000); //Exposure time + 40 segs.
			this.running = false;
			
		}
		
		@Override
		public void run() {
			
			if (running) return;
			running = true;
			
			try {
				
				LogUtil.info(this, "CameraRTD.Task.run()::BEGIN!!!");
					
				if (((DeviceCamera) DeviceDiscoverer.devGetDevice(getDeviceId(), false)).getActivityState() == ActivityStateCamera.READY){
						
					//transferFileBasedOnLastImage(imageContext.getUuid());
					transferFileBasedOnXMLRPC2(imageContext.getUuid());
						
					imageContext.exposeDone();
					exposureTimer.cancel(); 
						
					LogUtil.info(this, "CameraRTD.Task.run():: 		Image transfered!!!!!");
						
				}else if (timeOut.timeOut()){
							
					imageContext.exposeCancel();
					exposureTimer.cancel(); 

					LogUtil.severe(this, "CameraRTD.Task.run():: 		Timeout!!!!");
				}
					
					
			} catch (Exception e) {
				
				imageContext.exposeCancel();
				exposureTimer.cancel(); 

				LogUtil.severe(this, "CameraRTD.Task.run():: 		ERROR...aborting image transfer process!!!. Error = " + e.getMessage());
				e.printStackTrace();
				
			}finally{
				running = false;
			}
			
			LogUtil.info(this, "CameraRTD.Task.run()::END!!!");
				
		}		
	}
	
	public class ExposureContext{
		
		private boolean exposing;
		private boolean transfered;	
		private String uuid;
		private TransferStatus transfer;
		
		public ExposureContext(){
			reset();
			this.transfer = TransferStatus.NOT_STARTED;
		}
		
		public synchronized void exposeStart(String uuid){
			
			this.uuid = uuid;
			this.exposing = true;
			this.transfered = false;
			
			this.transfer = TransferStatus.NOT_STARTED;
		}
		
		public synchronized void exposeCancel(){
			reset();
		}
		
		public synchronized void exposeDone(){
			this.exposing = false;
			this.transfered = true;			
		}
		
		public synchronized void reset(){
			this.uuid = null;
			this.exposing = false;
			this.transfered = false;
		}
		
		public synchronized boolean isTransfered() {
			return transfered;
		}

		public synchronized void setTransfered(boolean transfered) {
			this.transfered = transfered;
		}
		
		public synchronized String getUuid() {
			return uuid;
		}

		public synchronized void setUuid(String uuid) {
			this.uuid = uuid;
		}
		
		public synchronized boolean isExposing() {
			return exposing;
		}

		public synchronized void setExposing(boolean value) {
			this.exposing = value;
		}

		public TransferStatus getTransfer() {
			return transfer;
		}

		public void setTransfer(TransferStatus transfer) {
			this.transfer = transfer;
		}
		
		
		
	}
	
	public enum TransferStatus{
		
		NOT_STARTED,
		STARTED,
		FINISHED,
		FAILED
	}

	

}

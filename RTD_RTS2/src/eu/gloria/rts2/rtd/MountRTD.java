package eu.gloria.rts2.rtd;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import eu.gloria.rt.catalogue.Catalogue;
import eu.gloria.rt.catalogue.ObjInfo;
import eu.gloria.rt.entity.device.ActivityStateMount;
import eu.gloria.rt.unit.Altaz;
import eu.gloria.rt.unit.Radec;
import eu.gloria.rt.entity.device.AxisRateType;
import eu.gloria.rt.entity.device.DeviceMount;
import eu.gloria.rt.entity.device.DeviceProperty;
import eu.gloria.rt.entity.device.MountPointingModel;
import eu.gloria.rt.entity.device.TrackingRateType;
import eu.gloria.rt.exception.RTException;
import eu.gloria.rt.exception.UnknownDataException;
import eu.gloria.rt.exception.UnsupportedOpException;
import eu.gloria.rtc.DeviceDiscoverer;
import eu.gloria.rtd.RTDMountInterface;
import eu.gloria.rts2.http.Rts2Cmd;
import eu.gloria.rts2.http.Rts2CmdType;
import eu.gloria.rts2.http.Rts2Date;
import eu.gloria.rts2.http.Rts2MessageType;
import eu.gloria.rts2.http.Rts2Messages;
import eu.gloria.tools.cache.CacheManager;
import eu.gloria.tools.configuration.Config;
import eu.gloria.tools.conversion.DegreeFormat;
import eu.gloria.tools.log.LogUtil;

/**
 * RTS2 RTDMountInterface implementation for generic RTS2 teld device.
 * 
 * @author mclopez
 *
 */
public class MountRTD extends DeviceRTD implements RTDMountInterface {
		
	
	
	/**
	 * Constructor
	 */
	public MountRTD () {		
		
		
	}

	/**
	 * {@inheritDoc}
	 * <p>
	 * Operation not supported by RTS2 teld.
	 */
	@Override
	public Date mntGetUtcClock() throws RTException {
		
		throw new UnsupportedOpException ("Operation not supported");
	}

	/**
	 * {@inheritDoc}
	 * <p>
	 * Operation not supported by RTS2 teld.
	 */
	@Override
	public void mntSetUtcClock(Date date) throws RTException {

		throw new UnsupportedOpException ("Operation not supported");
		
	}

	
	/**
	 * {@inheritDoc} 
	 * <p>
	 * Recovers "LST" from RTS2 teld.
	 */
	@Override
	public double mntGetSiderealDate() throws RTException {
		
		DeviceProperty property = this.devGetDeviceProperty("LST");
		
		
		return Double.valueOf(property.getValue().get(0));
	}

	@Override
	public boolean mntIsAtHome() throws RTException {

		throw new UnsupportedOpException ("Operation not supported");
	}

	
	/**
	 * {@inheritDoc} 
	 * <p>
	 * Recovers the Mount RTS2 state.
	 */
	@Override
	public boolean mntIsParked() throws RTException {
		
		if (((DeviceMount) DeviceDiscoverer.devGetDevice(this.getDeviceId(), false)).getActivityState() == ActivityStateMount.PARKED){
			CachedDeviceInfo deviceInfo = (CachedDeviceInfo)CacheManager.getObject(CACHE_DEVICE_INFO, this.getDeviceId(), null);
			Altaz altaz = (Altaz)deviceInfo.getAttribute("park_position");	
			if (altaz == null){
				DeviceProperty property = this.devGetDeviceProperty("TEL_");
				
				if (property.getValue().size() != 0){
					altaz = new Altaz(Double.valueOf(property.getValue().get(0)), Double.valueOf(property.getValue().get(1)));				
					deviceInfo.putAttribute("park_position", altaz);
				}
			}
			return true;
		}else{
			return false;
		}
				
	}

	
	/**
	 * {@inheritDoc} 
	 * <p>
	 * Recovers "TAR" from RTS2 teld.
	 */
	@Override
	public double mntGetTargetRightAscension()	throws RTException {

		//No se tiene en cuenta el offset, si hay q tenerlo en cuenta obtener tel_target
		DeviceProperty property = this.devGetDeviceProperty("TAR");
		
		return Double.valueOf(property.getValue().get(0));
	}

	/**
	 * {@inheritDoc}
	 * <p>
	 * Operation not supported by RTS2 teld.
	 */
	@Override
	public double mntGetTrackingDeclinationRate()
			throws RTException {

		throw new UnsupportedOpException ("Operation not supported");
	}

	/**
	 * {@inheritDoc}
	 * <p>
	 * Operation not supported by RTS2 teld.
	 */
	@Override
	public double mntGetTrackingAscensionRate()
			throws RTException {

		throw new UnsupportedOpException ("Operation not supported");
	}

	/**
	 * {@inheritDoc}
	 * <p>
	 * 
	 */
	@Override
	public TrackingRateType mntGetTrackingRate() throws RTException {

		if (mntCanSetTracking())
			return  TrackingRateType.DRIVE_SIDEREAL;
		else
			throw new UnsupportedOpException ("Operation not supported");
	}

	/**
	 * {@inheritDoc}
	 * <p>
	 * Operation not supported by RTS2 teld.
	 */
	@Override
	public void mntSetTrackingRate(TrackingRateType rate)
			throws RTException {

		if (mntCanSetTracking()){
			if (rate != TrackingRateType.DRIVE_SIDEREAL){
				throw new UnsupportedOpException ("This tracking rate is unsupported, only SIDEREAL is allowed.");
			}
		}else
			throw new UnsupportedOpException ("Operation not supported");
	}

	/**
	 * {@inheritDoc}
	 * <p>
	 * Operation not supported by RTS2 teld.
	 */
	@Override
	public boolean mntGetTracking() throws RTException {
		
		if (mntCanSetTracking()){

			DeviceProperty property = this.devGetDeviceProperty("tracking");

			if (property.getValue().get(0).compareTo("1") == 0)
				return true;
			else 
				return false;
		}else{
			throw new UnsupportedOpException ("Operation not supported");
		}
	}

	/**
	 * {@inheritDoc}
	 * <p>
	 * Manages tracking property.
	 */
	@Override
	public void mntSetTracking(boolean value)
			throws RTException {

		if (mntCanSetTracking()){
			List<String> valueProp = new ArrayList<String>();

			if (value)
				valueProp.add("true");
			else
				valueProp.add("false");

			long time = Rts2Date.now();

			if(!this.devUpdateDeviceProperty("tracking", valueProp))			
				throw new RTException("Cannot set tracking");	
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
	 * Operation not supported by RTS2 teld.
	 */
	@Override
	public double mntGetGuideRateDeclination()
			throws RTException {

		throw new UnsupportedOpException ("Operation not supported");
	}

	/**
	 * {@inheritDoc}
	 * <p>
	 * Operation not supported by RTS2 teld.
	 */
	@Override
	public double mntGetDeclinationRateRightAscension()
			throws RTException {

		throw new UnsupportedOpException ("Operation not supported");
	}

	
	/**
	 * {@inheritDoc} 
	 * <p>
	 * Recovers the Mount RTS2 state
	 */
	@Override
	public boolean mntIsSlewing() throws RTException {		
		
		return (((DeviceMount) DeviceDiscoverer.devGetDevice(this.getDeviceId(), false)).getActivityState() == ActivityStateMount.MOVING);
	}

	
	/**
	 * {@inheritDoc} 
	 * <p>
	 * Recovers "TEL" from RTS2 teld.
	 */
	@Override
	public double mntGetPosAxis1() throws RTException {

		DeviceProperty property = this.devGetDeviceProperty("TEL");
		
		return Double.valueOf(property.getValue().get(0));
	}

	
	/**
	 * {@inheritDoc} 
	 * <p>
	 * Recovers "TEL" from RTS2 teld.
	 */
	@Override
	public double mntGetPosAxis2() throws RTException {

		DeviceProperty property = this.devGetDeviceProperty("TEL");
		
		return Double.valueOf(property.getValue().get(1));
	}

	/**
	 * {@inheritDoc}
	 * <p>
	 * Operation not supported by RTS2 teld.
	 */
	@Override
	public double mntGetPosAxis3() throws RTException {

		throw new UnsupportedOpException ("Operation not supported");
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public boolean mntCanPulseGuide() throws RTException {

		return false;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public boolean mntCanSetGuideRates() throws RTException {

		return false;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public boolean mntCanSetPark() throws RTException {

		return false;
	}

	/**
	 * {@inheritDoc}
	 * <p>
	 * Operation not supported by RTS2 teld.
	 */
	@Override
	public void mntSetPark(double ascension, double declination)
			throws RTException {

		throw new UnsupportedOpException ("Operation not supported");
		
	}

	/**
	 * {@inheritDoc}
	 * <p>
	 * If park position has not been recovered and mount is parked, it gets "TEL_" from RTS2 teld
	 */
	@Override
	public double mntGetALTParkPos() throws RTException {
		CachedDeviceInfo deviceInfo = (CachedDeviceInfo)CacheManager.getObject(CACHE_DEVICE_INFO, this.getDeviceId(), null);
		Altaz altaz = (Altaz)deviceInfo.getAttribute("park_position");
		
		if (altaz != null){
			return altaz.getAltDecimal();
		}else{
			if (this.mntIsParked()){				
				altaz = (Altaz)deviceInfo.getAttribute("park_position");
				return altaz.getAltDecimal();
			}else{
				throw new UnknownDataException ("Unknown parked position");
			}
		}
	}

	/**
	 * {@inheritDoc}
	 * <p>
	 * If park position has not been recovered and mount is parked, it gets "TEL" from RTS2 teld
	 */
	@Override
	public double mntGetAZParkPos() throws RTException {	
		
		CachedDeviceInfo deviceInfo = (CachedDeviceInfo)CacheManager.getObject(CACHE_DEVICE_INFO, this.getDeviceId(), null);
		Altaz altaz = (Altaz) deviceInfo.getAttribute("park_position");
		
		if (altaz != null){
			return altaz.getAzDecimal();
		}else{
			if (this.mntIsParked()){
				altaz = (Altaz)deviceInfo.getAttribute("park_position");				
				return altaz.getAzDecimal();
			}else{
				throw new UnknownDataException ("Unknown parked position");
			}
		}
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public boolean mntCanSetTracking() throws RTException {

		try{
			DeviceProperty property = this.devGetDeviceProperty("tracking");
			if (property.isMandatory())
				return false;
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
	public boolean mntCanSetTrackingRate() throws RTException {

		return mntCanSetTracking();
	}

	

	/**
	 * {@inheritDoc}
	 * <p>
	 * RTS2 always allows synchronous slewing to equatorial coordinates
	 */
	@Override
	public boolean mntCanSlewCoordinates() throws RTException {
				
		return true;
	}

	/**
	 * {@inheritDoc}
	 * <p>
	 * RTS2 always allows asynchronous slewing to equatorial coordinates
	 */
	@Override
	public boolean mntCanSlewCoordinatesAsync()
			throws RTException {
				
		return true;
	}
	
	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public boolean mntCanSlewObject() throws RTException {
		return true;
	}

	/**
	 * {@inheritDoc}
	 * <p>
	 * RTS2 always allows synchronous slewing to local horizontal coordinates
	 */
	@Override
	public boolean mntCanSlewAltAz() throws RTException {
		
		return true;
	}

	/**
	 * {@inheritDoc}
	 * <p>
	 * RTS2 always allows asynchronous slewing to local horizontal coordinates
	 */
	@Override
	public boolean mntCanSlewAzAsync() throws RTException {
				
		return true;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public boolean mntCanMoveAzis() throws RTException {
		
		return false;
	}

	/**
	 * {@inheritDoc}
	 * <p>
	 * Operation not supported by RTS2 teld.
	 */
	@Override
	public List<AxisRateType> mntAxisRate() throws RTException {

		throw new UnsupportedOpException ("Operation not supported");
	}

	/**
	 * {@inheritDoc}
	 * <p>
	 * Operation not supported by RTS2 teld.
	 */
	@Override
	public List<TrackingRateType> mntTrackingRates() throws RTException {

		if (mntCanSetTracking()){
			List<TrackingRateType> result = new ArrayList<TrackingRateType>();

			result.add(TrackingRateType.DRIVE_SIDEREAL);

			return result;
		}else{
			throw new UnsupportedOpException ("Operation not supported");
		}
	}

	/**
	 * {@inheritDoc}
	 * <p>
	 * Operation not supported by RTS2 teld.
	 */
	@Override
	public void mntGoHome() throws RTException {

		throw new UnsupportedOpException ("Operation not supported");
		
	}

	/**
	 * {@inheritDoc}
	 * <p>
	 * Execute <i>cmd: park</i> from RTS2 teld
	 */
	@Override
	public void mntPark() throws RTException {
		
		long time = Rts2Date.now();
		
		if (!this.devExecuteCmd ("park", true))
			throw new RTException ("Mount cannot be parked");
		
		//Message recovering
		String message = Rts2Messages.getMessageText (Rts2MessageType.error, getDeviceId(), time);
		if (message != null)
			throw new RTException(message);	
		
		
	}

	/**
	 * {@inheritDoc}
	 * <p>
	 * Operation not supported by RTS2 teld.
	 */
	@Override
	public void mntUnpark() throws RTException {

		//throw new UnsupportedOpException ("Operation not supported");
		
		long time = Rts2Date.now();
		
		double ra = mntGetPosAxis1();
		double dec = mntGetPosAxis2();
		
		
		mntSlewToCoordinates(ra, dec);
		
	}

	/**
	 * {@inheritDoc}
	 * <p>
	 * Execute <i>cmd: altaz</i> from RTS2 teld
	 */
	@Override
	public void mntSlewToAltAz( double azimuth, double altitude) throws RTException {	
						
		long time = Rts2Date.now();
		
		if (this.devExecuteCmd ("altaz+"+String.valueOf(altitude)+"+"+String.valueOf(azimuth),false)){
			//Message recovering
			String message = Rts2Messages.getMessageText (Rts2MessageType.error, getDeviceId(), time);
			if (message != null)
				throw new RTException(message);
			
		}else{
			throw new RTException("Cannot slew");
		}
			
		
		
	}

	/**
	 * {@inheritDoc}
	 * <p>
	 * Execute <i>cmd: altaz</i> from RTS2 teld
	 */
	@Override
	public void mntSlewToAltAzAsync(double azimuth,
			double altitude) throws RTException {		
		
		long time = Rts2Date.now();
		
		
		if (this.devExecuteCmdAsync ("altaz+"+String.valueOf(altitude)+"+"+String.valueOf(azimuth),false)){	
			//Message recovering
			String message = Rts2Messages.getMessageText (Rts2MessageType.error, getDeviceId(), time);
			if (message != null)
				throw new RTException(message);	
		}else{
			throw new RTException("Cannot slew");
		}				
	}

	/**
	 * {@inheritDoc}
	 * <p>
	 * Execute <i>cmd: move</i> from RTS2 teld
	 */
	@Override
	public void mntSlewToCoordinates(double ascension,
			double declination) throws RTException {
		
		long time = Rts2Date.now();
		
		
		if (this.devExecuteCmdAsync ("move+"+String.valueOf(ascension)+"+"+String.valueOf(declination),false)){
			//Message recovering --> due to serveral errors in bootes2
//			String message = Rts2Messages.getMessageText (Rts2MessageType.error, getDeviceId(), time);
//			if (message != null)
//				throw new RTException(message);	
		}else{
			throw new RTException("Cannot slew");
		}			
		
	}

	/**
	 * {@inheritDoc}
	 * <p>
	 * Execute <i>cmd: move</i> from RTS2 teld
	 */
	@Override
	public void mntSlewToCoordinatesAsync( double ascension,
			double declination) throws RTException {		
	
		long time = Rts2Date.now();
		
	
		if (this.devExecuteCmdAsync ("move+"+String.valueOf(ascension)+"+"+String.valueOf(declination),false)){
			//Message recovering
			String message = Rts2Messages.getMessageText (Rts2MessageType.error, getDeviceId(), time);
			if (message != null)
				throw new RTException(message);	
		}else{
			throw new RTException("Cannot slew");
		}	

		
	}

	/**
	 * {@inheritDoc}
	 * <p>
	 * Operation not supported by RTS2 teld.
	 */
	@Override
	public void mntMoveAxis(int axisType, double rate)
			throws RTException {

		throw new UnsupportedOpException ("Operation not supported");
		
	}

	/**
	 * {@inheritDoc}
	 * <p>
	 * Operation not supported by RTS2 teld.,
	 */
	@Override
	public void mntPulseGuide(int guideDirection, int duration)
			throws RTException {

		throw new UnsupportedOpException ("Operation not supported");
		
	}

	/**
	 * {@inheritDoc}
	 * <p>
	 * Operation not supported by RTS2 teld.
	 */
	@Override
	public void mntStopSlew(int axisType) throws RTException {

		throw new UnsupportedOpException ("Operation not supported");
		
	}

	/**
	 * {@inheritDoc}
	 * <p>
	 * Execute <i>cmd: stop</i> from RTS2 teld
	 */
	@Override
	public void mntStopSlew() throws RTException {
		
		long time = Rts2Date.now();
		
		if (!this.devExecuteCmd ("stop",true))
			throw new RTException ("Slew cannot be stopped");
		//Message recovering
		String message = Rts2Messages.getMessageText (Rts2MessageType.error, getDeviceId(), time);
		if (message != null)
			throw new RTException(message);	
		
	}

	/**
	 * {@inheritDoc}
	 * <p>
	 * Recovers "TAR" from RTS2 teld
	 */
	@Override
	public double mntGetTargetDeclination() throws RTException {

		//No se tiene en cuenta el offset, si hay q tenerlo en cuenta obtener tel_target
		
		DeviceProperty property = this.devGetDeviceProperty("TAR");
		
		return Double.valueOf(property.getValue().get(1));
	}


	/**
	 * {@inheritDoc}
	 * <p>
	 * Recovers "pointing" from RTS2 teld
	 */
	@Override
	public MountPointingModel mntGetPointingModel()
			throws RTException {
		
		DeviceProperty property = this.devGetDeviceProperty("pointing");		
		
		return MountPointingModel.fromValue(property.getValue().get(0));
	}

	@Override
	public void mntMoveNorth() throws RTException {

		DeviceProperty property = this.devGetDeviceProperty("OFFS");
		
		List<String> valueProp = new ArrayList<String>();
		valueProp.add(property.getValue().get(0));
		valueProp.add(String.valueOf(Double.valueOf(property.getValue().get(1))+0.2));
		
		long time = Rts2Date.now();
		
		this.devUpdateDeviceProperty("OFFS", valueProp);
		
		//Message recovering
		String message = Rts2Messages.getMessageText (Rts2MessageType.error, getDeviceId(), time);
		if (message!= null){
			if ( message.contains("cannot move"))
				throw new RTException(message);
		}
		
	}

	@Override
	public void mntMoveEast() throws RTException {

		DeviceProperty property = this.devGetDeviceProperty("OFFS");
		
		List<String> valueProp = new ArrayList<String>();
		valueProp.add(String.valueOf(Double.valueOf(property.getValue().get(0))+0.2));
		valueProp.add(property.getValue().get(1));
		
		long time = Rts2Date.now();
		
		this.devUpdateDeviceProperty("OFFS", valueProp);
		
		//Message recovering
		String message = Rts2Messages.getMessageText (Rts2MessageType.error, getDeviceId(), time);
		if (message!= null){
			if ( message.contains("cannot move"))
				throw new RTException(message);
		}
		
	}

	@Override
	public void mntMoveSouth() throws RTException {

		DeviceProperty property = this.devGetDeviceProperty("OFFS");
		
		List<String> valueProp = new ArrayList<String>();
		valueProp.add(property.getValue().get(0));
		valueProp.add(String.valueOf(Double.valueOf(property.getValue().get(1))-0.2));
		
		long time = Rts2Date.now();
		
		this.devUpdateDeviceProperty("OFFS", valueProp);
		
		//Message recovering
		String message = Rts2Messages.getMessageText (Rts2MessageType.error, getDeviceId(), time);
		if (message!= null){
			if ( message.contains("cannot move"))
				throw new RTException(message);
		}
		
	}

	@Override
	public void mntMoveWest() throws RTException {

		DeviceProperty property = this.devGetDeviceProperty("OFFS");
		
		List<String> valueProp = new ArrayList<String>();
		valueProp.add(String.valueOf(Double.valueOf(property.getValue().get(0))-0.2));
		valueProp.add(property.getValue().get(1));
		
		long time = Rts2Date.now();
		
		this.devUpdateDeviceProperty("OFFS", valueProp);
		
		//Message recovering
		String message = Rts2Messages.getMessageText (Rts2MessageType.error, getDeviceId(), time);
		if (message!= null){
			if ( message.contains("cannot move"))
				throw new RTException(message);
		}
		
	}

	@Override
	public void mntSetSlewRate(String rate) throws RTException {

		throw new UnsupportedOpException ("Operation not supported");
	}

	@Override
	public void mntSlewObject(String object) throws RTException {
		
		double longitude = Config.getPropertyDouble("rt_config", "rts_longitude");
		double latitude = Config.getPropertyDouble("rt_config", "rts_latitude");
		
//		double longitude = Double.valueOf(this.devGetDeviceProperty("longitude").getValue().get(0));
//		double latitude = Double.valueOf(this.devGetDeviceProperty("latitude").getValue().get(0));

		Catalogue catalogue = new Catalogue(longitude, latitude, 0);
		ObjInfo objInfo = catalogue.getObject(object);
		if (objInfo == null){
			
			LogUtil.info(this, "Mount. Catalogue:: Object NOT found:" + object);
			throw new RTException("Not Object Found");
			
		}else{
			Radec pos = objInfo.getPosition();
			double dec = pos.getDecDecimal();
			double ra = pos.getRaDecimal();
			
			LogUtil.info(this, "Mount. Catalogue:: Object found:" + object);
			String[] names = {
					"ra_double",
					"dec_double",
					"ra",
					"dec"
			};
			
			String[] values = {
					String.valueOf(pos.getRaDecimal()),
					String.valueOf(pos.getDecDecimal()),
					String.valueOf(pos.getRaString(DegreeFormat.HHMMSS)),
					String.valueOf(pos.getDecString(DegreeFormat.DDMMSS))
			};
			
			LogUtil.info(this,  "Mount. Catalogue:: Object found Data: "  + LogUtil.getLog(names, values));
			
			mntSlewToCoordinates(ra, dec);
			
		}
		
	}

	@Override
	public String mntGetSlewRate() throws RTException {

		throw new UnsupportedOpException ("Operation not supported");
	}

	@Override
	public boolean mntIsPointingAtObject(String object, double raError,
			double decError) throws RTException {
		
		try{
			
			double longitude = Config.getPropertyDouble("rt_config", "rts_longitude");
			double latitude = Config.getPropertyDouble("rt_config", "rts_latitude");
			
//			double longitude = Double.valueOf(this.devGetDeviceProperty("longitude").getValue().get(0));
//			double latitude = Double.valueOf(this.devGetDeviceProperty("latitude").getValue().get(0));

			Catalogue catalogue = new Catalogue(longitude, latitude, 0);
			ObjInfo objInfo = catalogue.getObject(object);
			if (objInfo == null){
				
				LogUtil.info(this, "Mount.mntIsPointingAtObject(). Catalogue:: Object NOT found:" + object);
				throw new RTException("Not Object Found");
				
			}else{
				
				Radec pos = objInfo.getPosition();
				
				LogUtil.info(this, "Mount.mntIsPointingAtObject(). Catalogue:: Object found:" + object);
				String[] names = {
						"ra_double",
						"dec_double",
						"ra",
						"dec"
				};
				
				String[] values = {
						String.valueOf(pos.getRaDecimal()),
						String.valueOf(pos.getDecDecimal()),
						String.valueOf(pos.getRaString(DegreeFormat.HHMMSS)),
						String.valueOf(pos.getDecString(DegreeFormat.DDMMSS))
				};
				
				LogUtil.info(this,  "Mount.mntIsPointingAtObject(). Catalogue:: Object found Data: "  + LogUtil.getLog(names, values));
				
				return mntIsPointingAtCoordinates(pos.getRaDecimal(), pos.getDecDecimal(), raError, decError);
				
			}
			
		}catch(RTException ex){
			LogUtil.severe(this, ex.getMessage() + ". Error code=" + ex.getErrorCode().toString());
			throw ex;
		}catch(Exception ex){
			LogUtil.severe(this, ex.getMessage());
			throw new RTException(ex.getMessage());
		}
	}

	@Override
	public boolean mntIsPointingAtCoordinates(double ra, double dec,
			double raError, double decError) throws RTException {
		
		try{
			
			double raCurrent = mntGetPosAxis1();
			double decCurrent = mntGetPosAxis2();
			
			String[] names = {
					"raCurrent",
					"decCurrent",
					"ra",
					"dec",
					"raError",
					"decError",
					"pointing"
			};
			
			String[] values = {
					String.valueOf(raCurrent),
					String.valueOf(decCurrent),
					String.valueOf(ra),
					String.valueOf(dec),
					String.valueOf(raError),
					String.valueOf(decError),
					"UNKOWN"
			};
			
			LogUtil.info(this,  "Mount.mntIsPointingAtCoordinates(). Evaluating: "  + LogUtil.getLog(names, values));
			
			if (ra < 0 || ra >= 360) throw new RTException("Invalid RA value [0, 360). Value=" + ra);
			if (dec < -90 || dec > 90) throw new RTException("Invalid DEC value [-90, +90]. Value=" + dec);
			
			Radec radec = new Radec(raCurrent, decCurrent);
			boolean result = radec.equals(ra, dec, raError, decError);
			
			values[6] = String.valueOf(result);
			
			LogUtil.info(this,  "Mount.mntIsPointingAtCoordinates(). Evaluated: "  + LogUtil.getLog(names, values));
			
			return result;
			
		}catch(RTException ex){
			LogUtil.severe(this, ex.getMessage() + ". Error code=" + ex.getErrorCode().toString());
			throw ex;
		}catch(Exception ex){
			LogUtil.severe(this, ex.getMessage());
			throw new RTException(ex.getMessage());
		}
	}
		
	
	
}

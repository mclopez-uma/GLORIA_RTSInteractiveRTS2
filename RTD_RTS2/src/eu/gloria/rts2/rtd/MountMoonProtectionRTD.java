package eu.gloria.rts2.rtd;

import java.util.Date;

import eu.gloria.rt.catalogue.Catalogue;
import eu.gloria.rt.catalogue.ObjInfo;
import eu.gloria.rt.catalogue.libnova.GmtOffset;
import eu.gloria.rt.catalogue.libnova.LibNovaAltaz;
import eu.gloria.rt.catalogue.libnova.LibNovaAngularDistance;
import eu.gloria.rt.catalogue.libnova.LibNovaJNI;
import eu.gloria.rt.catalogue.libnova.LibNovaObserver;
import eu.gloria.rt.catalogue.libnova.LibNovaRaDecJ2000;
import eu.gloria.rt.catalogue.libnova.LibNovaReturnedInfo;
import eu.gloria.rt.catalogue.libnova.LibNovaZoneDate;
import eu.gloria.rt.entity.device.DeviceProperty;
import eu.gloria.rt.exception.RTException;
import eu.gloria.rt.unit.Radec;
import eu.gloria.tools.configuration.Config;
import eu.gloria.tools.conversion.DegreeFormat;
import eu.gloria.tools.log.LogUtil;

public class MountMoonProtectionRTD extends MountRTD {


	@Override
	public void mntSlewObject(String object) throws RTException {
		
		DeviceProperty property = this.devGetDeviceProperty("block_move");
		
		if (property.getValue().get(0).compareTo("1") == 0)
			throw new RTException("Mount movement blocked");
		else{
			double longitude = Config.getPropertyDouble("rt_config", "rts_longitude");
			double latitude = Config.getPropertyDouble("rt_config", "rts_latitude");
		
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
			
			
				mntSlewToCoordinates (ra, dec);
						
			}
		}
		
	}
	
	
	@Override
	public void mntSlewToAltAz(double azimuth, double altitude) throws RTException {
				
		if (!targetClosedToAltAzMoon(altitude, azimuth))
			super.mntSlewToAltAz(azimuth, altitude);
		else
			throw new RTException("Target too CLOSE to Moon");
			
		
	}
	
	
	@Override
	public void mntSlewToAltAzAsync(double azimuth,	double altitude) throws RTException {	
							
		if (!targetClosedToAltAzMoon(altitude, azimuth))
			super.mntSlewToAltAzAsync(azimuth, altitude);
		else
			throw new RTException("Target too CLOSE to Moon");
		
	}

	
	@Override
	public void mntSlewToCoordinates(double ascension,double declination) throws RTException {


		if (!targetClosedToRadecMoon(ascension, declination))
			super.mntSlewToCoordinates(ascension, declination);
		else
			throw new RTException("Target too CLOSE to Moon");
		
		
	}
	
	@Override
	public void mntSlewToCoordinatesAsync(double ascension, double declination) throws RTException {		


		if (!targetClosedToRadecMoon(ascension, declination))
			super.mntSlewToCoordinatesAsync(ascension, declination);
		else
			throw new RTException("Target too CLOSE to Moon");
		
				
	}
	
	private LibNovaRaDecJ2000 getMoonRadec() throws RTException{
		
		double longitude = Config.getPropertyDouble("rt_config", "rts_longitude");
		double latitude = Config.getPropertyDouble("rt_config", "rts_latitude");
		
		Catalogue catalogue = new Catalogue(longitude, latitude, 0);
		ObjInfo objInfo = catalogue.getObject("moon");
		if (objInfo == null){
			
			LogUtil.info(this, "Mount. Catalogue:: Object NOT found:" + "moon");
			throw new RTException("Not Object Found");
			
		}else{
			Radec posMoon = objInfo.getPosition();
			double decMoon = posMoon.getDecDecimal();
			double raMoon = posMoon.getRaDecimal();
			
			LogUtil.info(this, "Mount. Catalogue:: Object found:" + "moon");
			String[] names = {
					"ra_double",
					"dec_double",
					"ra",
					"dec"
			};
			
			String[] values = {
					String.valueOf(posMoon.getRaDecimal()),
					String.valueOf(posMoon.getDecDecimal()),
					String.valueOf(posMoon.getRaString(DegreeFormat.HHMMSS)),
					String.valueOf(posMoon.getDecString(DegreeFormat.DDMMSS))
			};
			
			LogUtil.info(this,  "Mount. Catalogue:: Object found Data: "  + LogUtil.getLog(names, values));
			
			LibNovaRaDecJ2000 moonRadec = new LibNovaRaDecJ2000();
			moonRadec.dec = decMoon; moonRadec.ra = raMoon;
			
			return moonRadec;
		}
	}
	
	private boolean targetClosedToAltAzMoon (double alt, double az) throws RTException{
		
//		Double distance = (ascension - raMoon)*(ascension - raMoon) + (declination - decMoon)*(declination - decMoon);
		
		LibNovaJNI jni = new LibNovaJNI();
		LibNovaRaDecJ2000 moonRadec = getMoonRadec();
		
		LibNovaAltaz moonAltAz = toAltaz (moonRadec.ra, moonRadec.dec);
		
		double distance = (alt - moonAltAz.alt)*(alt - moonAltAz.alt) + ((az - moonAltAz.az)*Math.cos(alt))*((az - moonAltAz.az)*Math.cos(alt));
	
		
		if (distance > 25){
			LogUtil.info(this, "Mount. Target NOT close to Moon " + alt + "  "+ az);
			return false;
		}else{
			LogUtil.info(this, "Mount. Target CLOSE to Moon " + alt + "  "+ az);
			return true;
		}
	
	}
	
	private boolean targetClosedToRadecMoon (double ascension, double declination) throws RTException{
			
//			Double distance = (ascension - raMoon)*(ascension - raMoon) + (declination - decMoon)*(declination - decMoon);
			
			LibNovaJNI jni = new LibNovaJNI();
			LibNovaRaDecJ2000 moonRadec = getMoonRadec();
			LogUtil.info(this, "Mount. moon RADEC " + moonRadec.ra + "  "+ moonRadec.dec);
			
			LibNovaRaDecJ2000 targetRadec = new LibNovaRaDecJ2000();
			targetRadec.dec = declination; targetRadec.ra = ascension;
			
			LibNovaAngularDistance distance = new LibNovaAngularDistance();
			LibNovaReturnedInfo returnedInfo = new LibNovaReturnedInfo();
			
			jni.getAngularDistance(moonRadec, targetRadec, distance, returnedInfo);
			LogUtil.info(this, "Mount. getAngularDistance. Distance " + distance.degrees + "  returnedInfo "+ returnedInfo.desc);
			
			if (distance.degrees > 5){
				LogUtil.info(this, "Mount. Target NOT close to Moon " + ascension + "  "+ declination);
				return false;
			}else{
				LogUtil.info(this, "Mount. Target CLOSE to Moon " + ascension + "  "+ declination);
				return true;
			}
		
	}
	
	private LibNovaAltaz toAltaz (double ra, double dec) throws RTException{
		
		double longitude = Config.getPropertyDouble("rt_config", "rts_longitude");
		double latitude = Config.getPropertyDouble("rt_config", "rts_latitude");
		
		LibNovaObserver novaObserver = new LibNovaObserver();
		novaObserver.latitude = latitude;
		novaObserver.longitude = longitude;
		
		LibNovaJNI jni = new LibNovaJNI();
		GmtOffset utcOffset;
		
		try {
			utcOffset = new GmtOffset(new Date());
		} catch (Exception e) {
			throw new RTException (e.getMessage());
		}
		
		LibNovaRaDecJ2000 targetRadec = new LibNovaRaDecJ2000();
		targetRadec.ra=ra;
		targetRadec.dec=dec;
		
		LibNovaZoneDate novaZoneDate = new LibNovaZoneDate();
		
		
		Date currentDate = new Date();
		
		novaZoneDate.gmtOff = utcOffset.getOffsetSeconds(); // 3600 segs == 1 hour
		novaZoneDate.year = currentDate.getYear() + 1900;
		novaZoneDate.month = currentDate.getMonth()+1;
		novaZoneDate.day = currentDate.getDate();
		novaZoneDate.hours = currentDate.getHours();
		novaZoneDate.minutes = currentDate.getMinutes();
		novaZoneDate.seconds = currentDate.getSeconds();
		
		LibNovaAltaz novaAltaz = new LibNovaAltaz();
		LibNovaReturnedInfo novaReturnedInfo = new LibNovaReturnedInfo();
		
				
		LogUtil.info(this, "Mount. getAltazByRadec. targetRadec " + targetRadec.ra + " "+targetRadec.dec+" returnedInfo "+ novaReturnedInfo.desc+" altaz "+ novaAltaz.alt +" " +novaAltaz.az);
		
		jni.getAltazByRadec(novaObserver, novaZoneDate, targetRadec, novaAltaz, novaReturnedInfo);
		
		LogUtil.info(this, "Mount. getAltazByRadec. targetRadec " + targetRadec.ra + " "+targetRadec.dec+" returnedInfo "+ novaReturnedInfo.desc+" altaz "+ novaAltaz.alt +" " +novaAltaz.az);
		
		return novaAltaz;
		
	}
}

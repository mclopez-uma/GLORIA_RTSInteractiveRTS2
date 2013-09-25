package eu.gloria.rts2.rtd;

import java.util.ArrayList;
import java.util.List;

import eu.gloria.rt.entity.device.ActivityStateDome;
import eu.gloria.rt.entity.device.DeviceDome;
import eu.gloria.rt.entity.device.DeviceProperty;
import eu.gloria.rt.entity.device.DeviceType;
import eu.gloria.rt.exception.RTException;
import eu.gloria.rt.exception.UnsupportedOpException;
import eu.gloria.rtc.DeviceDiscoverer;
import eu.gloria.rts2.http.Rts2GatewayTools;


/**
 * RTS2 RTDDometInterface implementation for Zelio RTS2 teld device.
 * 
 * @author mclopez
 *
 */
public class DomeZelioRTD extends DomeRTD implements RTDAssociatedDevInterface{	
	
	private List<String> associatedDeviceIds = new ArrayList <String>();
	
	/**
	 * Constructor
	 */
	public DomeZelioRTD() {
		
	}
	
	@Override
	public double domGetAzimuth() throws RTException {
		throw new UnsupportedOpException ("Operation not supported");
	}
	
	
	
	@Override
	public int domGetNumberElement() throws RTException {
		
		return 1;
	}	
	
	
	@Override
	public List<String> getAssociatedDevices() {		
		
		String name = this.getDeviceId() + "_" + DeviceRTDPrefix.SENSOR_RAIN;
		int sufix = 1;
		
		try {
			while (Rts2GatewayTools.existDeviceName(name)){
				name = this.getDeviceId() + "_" + DeviceRTDPrefix.SENSOR_RAIN + String.valueOf(sufix);
				sufix++;
			}
		} catch (RTException e) {			
			e.printStackTrace();
		}
		associatedDeviceIds.add (name);
		
		return associatedDeviceIds;
	}

	@Override
	public DeviceAssociationType getRelation() {
		
		return DeviceAssociationType.Independent;
	}
	

	@Override
	public DeviceType getDeviceType(String deviceId) {
		
		if (associatedDeviceIds.contains(deviceId))		
			return DeviceType.RAIN_SENSOR;
		else
			return DeviceType.UNKNOWN;
	}
	
	public String getDeviceSubType (String deviceId){
		
		return "ZELIO";
	}
	
}

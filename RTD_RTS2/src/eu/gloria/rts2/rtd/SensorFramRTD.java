package eu.gloria.rts2.rtd;

import java.util.ArrayList;
import java.util.List;

import eu.gloria.rt.entity.device.DeviceType;
import eu.gloria.rt.exception.RTException;
import eu.gloria.rts2.http.Rts2GatewayTools;



/**
 * RTS2 Fram-weather sensor
 * 
 * @author mclopez
 *
 */
public class SensorFramRTD extends DeviceRTD implements RTDAssociatedDevInterface {
	
	private List<String> associatedDeviceIds = new ArrayList <String>();
	
	
	public List<String> getAssociatedDevices() throws RTException{
		
		String name = null;
		int sufix = 1;
		
		//Rain sensor
		name = this.getDeviceId() + "_" + DeviceRTDPrefix.SENSOR_RAIN;
		sufix = 1;

		try {
			while (Rts2GatewayTools.existDeviceName(name)){
				name = this.getDeviceId() + "_" + DeviceRTDPrefix.SENSOR_RAIN + String.valueOf(sufix);
				sufix++;
			}
		} catch (RTException e) {			
			e.printStackTrace();
		}
		
		associatedDeviceIds.add (name);
		
		//Wind sensor
		name = this.getDeviceId() + "_" + DeviceRTDPrefix.SENSOR_WIND_SPEED;
		sufix = 1;
		
		try {
			while (Rts2GatewayTools.existDeviceName(name)){
				name = this.getDeviceId() + "_" + DeviceRTDPrefix.SENSOR_WIND_SPEED + String.valueOf(sufix);
				sufix++;
			}
		} catch (RTException e) {			
			e.printStackTrace();
		}
		
		return associatedDeviceIds;
	}

	
	@Override
	public DeviceAssociationType getRelation() {

		return DeviceAssociationType.Composition;
	}
	
	@Override
	public DeviceType getDeviceType(String deviceId) {

		if (associatedDeviceIds.contains(deviceId)){		
			if (deviceId.contains(DeviceRTDPrefix.SENSOR_RAIN))
				return DeviceType.RAIN_SENSOR;			
			else if (deviceId.contains(DeviceRTDPrefix.SENSOR_WIND_SPEED))
				return DeviceType.WIND_SPEED_SENSOR;
			else
				return DeviceType.UNKNOWN;
		}else{
			return DeviceType.UNKNOWN;
		}
	}

	@Override
	public String getDeviceSubType(String deviceId) {

		return "FRAM";
	}
}

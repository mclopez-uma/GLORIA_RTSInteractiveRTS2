package eu.gloria.rts2.rtd;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import eu.gloria.rt.entity.device.DeviceProperty;
import eu.gloria.rt.entity.device.DeviceType;
import eu.gloria.rt.exception.RTException;
import eu.gloria.rts2.http.Rts2GatewayTools;

/**
 * RTS2 Bart/D50 weather sensor
 * 
 * @author mclopez
 *
 */

public class SensorBartD50RTD extends DeviceRTD implements RTDAssociatedDevInterface {
	
	private List<String> associatedDeviceIds = new ArrayList <String>();
	private HashMap<String, String> deviceSubtype = new HashMap();
	
	@Override
	public List<String> getAssociatedDevices () throws RTException{
		
		DeviceProperty property = null;
		String name = null;
		int sufix = 1;
		
		//Simple temperature sensor
		try {
			property = this.devGetDeviceProperty("SENSOR_TEMP_ENABLE");
		} catch (RTException e1) {
			throw new RTException (e1.getMessage());
		}
		
		if (property.getValue().get(0).equals("1")){
			name = this.getDeviceId() + "_" + DeviceRTDPrefix.SENSOR_TEMPERATURE;
			sufix = 1;
			
			//try {
				while (associatedDeviceIds.contains(name)){
					name = this.getDeviceId() + "_" + DeviceRTDPrefix.SENSOR_TEMPERATURE + String.valueOf(sufix);
					sufix++;
				}
//			} catch (RTException e) {			
//				e.printStackTrace();
//			}
			
			associatedDeviceIds.add (name);
			deviceSubtype.put(name, "BARTD50_Simple");
		}
		
		//Temperature/humidity sensor 1
		try {
			property = this.devGetDeviceProperty("SENSOR_TEMPHUM1_ENABLE");
		} catch (RTException e1) {
			throw new RTException (e1.getMessage());
		}

		if (property.getValue().get(0).equals("1")){
			//Temperature
			name = this.getDeviceId() + "_" + DeviceRTDPrefix.SENSOR_TEMPERATURE;
			sufix = 1;

//			try {
				while (associatedDeviceIds.contains(name)){
					name = this.getDeviceId() + "_" + DeviceRTDPrefix.SENSOR_TEMPERATURE + String.valueOf(sufix);
					sufix++;
				}
//			} catch (RTException e) {			
//				e.printStackTrace();
//			}

			associatedDeviceIds.add (name);
			deviceSubtype.put(name, "BARTD50_Dome");
			
			//Humidity
			name = this.getDeviceId() + "_" + DeviceRTDPrefix.SENSOR_RH;
			sufix = 1;

//			try {
				while (associatedDeviceIds.contains(name)){
					name = this.getDeviceId() + "_" + DeviceRTDPrefix.SENSOR_RH + String.valueOf(sufix);
					sufix++;
				}
//			} catch (RTException e) {			
//				e.printStackTrace();
//			}

			associatedDeviceIds.add (name);
			deviceSubtype.put(name, "BARTD50_Dome");
		}
		
		//Temperature/humidity sensor 2
		try {
			property = this.devGetDeviceProperty("SENSOR_TEMPHUM2_ENABLE");
		} catch (RTException e1) {
			throw new RTException (e1.getMessage());
		}

		if (property.getValue().get(0).equals("1")){
			//Temperature
			name = this.getDeviceId() + "_" + DeviceRTDPrefix.SENSOR_TEMPERATURE;
			sufix = 1;

//			try {
				while (associatedDeviceIds.contains(name)){
					name = this.getDeviceId() + "_" + DeviceRTDPrefix.SENSOR_TEMPERATURE + String.valueOf(sufix);
					sufix++;
				}
//			} catch (RTException e) {			
//				e.printStackTrace();
//			}

			associatedDeviceIds.add (name);
			deviceSubtype.put(name, "BARTD50_Out");

			//Humidity
			name = this.getDeviceId() + "_" + DeviceRTDPrefix.SENSOR_RH;
			sufix = 1;

//			try {
				while (associatedDeviceIds.contains(name)){
					name = this.getDeviceId() + "_" + DeviceRTDPrefix.SENSOR_RH + String.valueOf(sufix);
					sufix++;
				}
//			} catch (RTException e) {			
//				e.printStackTrace();
//			}

			associatedDeviceIds.add (name);
			deviceSubtype.put(name, "BARTD50_Out");
		}
		
		//Anemometer sensor
		try {
			property = this.devGetDeviceProperty("SENSOR_BAR_ANEMO_ENABLE");
		} catch (RTException e1) {
			throw new RTException (e1.getMessage());
		}

		if (property.getValue().get(0).equals("1")){
			name = this.getDeviceId() + "_" + DeviceRTDPrefix.SENSOR_WIND_SPEED;
			sufix = 1;

//			try {
				while (associatedDeviceIds.contains(name)){
					name = this.getDeviceId() + "_" + DeviceRTDPrefix.SENSOR_WIND_SPEED + String.valueOf(sufix);
					sufix++;
				}
//			} catch (RTException e) {			
//				e.printStackTrace();
//			}

			associatedDeviceIds.add (name);
			deviceSubtype.put(name, "BARTD50");
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
			if (deviceId.contains(DeviceRTDPrefix.SENSOR_RH))
				return DeviceType.RH_SENSOR;
			else if (deviceId.contains(DeviceRTDPrefix.SENSOR_WIND_SPEED))
				return DeviceType.WIND_SPEED_SENSOR;			
			else if (deviceId.contains(DeviceRTDPrefix.SENSOR_TEMPERATURE))
				return DeviceType.TEMPERATURE_SENSOR;
			else
				return DeviceType.UNKNOWN;
		}else{
			return DeviceType.UNKNOWN;
		}
	}
	
	@Override
	public String getDeviceSubType(String deviceId) {

		return deviceSubtype.get(deviceId);
	}

}

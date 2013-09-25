package eu.gloria.rts2.rtd;

import java.util.ArrayList;
import java.util.List;

import eu.gloria.rt.entity.device.DeviceProperty;
import eu.gloria.rt.entity.device.DeviceType;
import eu.gloria.rt.exception.RTException;
import eu.gloria.rts2.http.Rts2GatewayTools;

/**
 * RTS2 Davis sensor
 * 
 * @author mclopez
 *
 */
public class SensorDavisRTD extends DeviceRTD implements RTDAssociatedDevInterface {

	private List<String> associatedDeviceIds = new ArrayList <String>();
	
	
	@Override
	public List<String> getAssociatedDevices() throws RTException {
		
		DeviceProperty property = null;
		String name = null;
		int sufix = 1;
		
		//Wind sensor
		try {
			property = this.devGetDeviceProperty("AVGWIND");
		} catch (RTException e1) {
			throw new RTException (e1.getMessage());
		}
		
		if (property.getValue().isEmpty()){
			try {
				property = this.devGetDeviceProperty("PEEKWIND");
			} catch (RTException e1) {
				throw new RTException (e1.getMessage());
			}
		}		
		
		if (!property.getValue().isEmpty()){
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
			
			associatedDeviceIds.add (name);
		}
		
		
		
		//Humidity sensor
		try {
			property = this.devGetDeviceProperty("DOME_HUM");
		} catch (RTException e1) {
			throw new RTException (e1.getMessage());
		}

		
		if (!property.getValue().isEmpty()){

			name = this.getDeviceId() + "_" + DeviceRTDPrefix.SENSOR_RH;
			sufix = 1;

			try {
				while (Rts2GatewayTools.existDeviceName(name)){
					name = this.getDeviceId() + "_" + DeviceRTDPrefix.SENSOR_RH + String.valueOf(sufix);
					sufix++;
				}
			} catch (RTException e) {			
				e.printStackTrace();
			}

			associatedDeviceIds.add (name);
		}
	
		
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
		
		//Barometer
		try {
			property = this.devGetDeviceProperty("BAR_PRESS");
		} catch (RTException e1) {
			throw new RTException (e1.getMessage());
		}
		
		if (!property.getValue().isEmpty()){
			
			name = this.getDeviceId() + "_" + DeviceRTDPrefix.SENSOR_BAROMETER;
			sufix = 1;

			try {
				while (Rts2GatewayTools.existDeviceName(name)){
					name = this.getDeviceId() + "_" + DeviceRTDPrefix.SENSOR_BAROMETER + String.valueOf(sufix);
					sufix++;
				}
			} catch (RTException e) {			
				e.printStackTrace();
			}

			associatedDeviceIds.add (name);
		}
			
		
		//TEMPERATURE
		try {
			property = this.devGetDeviceProperty("DOME_TMP");
		} catch (RTException e1) {
			throw new RTException (e1.getMessage());
		}
		
		if (!property.getValue().isEmpty()){
			
			name = this.getDeviceId() + "_" + DeviceRTDPrefix.SENSOR_TEMPERATURE;
			sufix = 1;

			try {
				while (Rts2GatewayTools.existDeviceName(name)){
					name = this.getDeviceId() + "_" + DeviceRTDPrefix.SENSOR_TEMPERATURE + String.valueOf(sufix);
					sufix++;
				}
			} catch (RTException e) {			
						e.printStackTrace();
			}

			associatedDeviceIds.add (name);
			
		}		
		
		
		//Cloud sensor. It hasn't been test!!
		try{
			property = this.devGetDeviceProperty("CLOUD_S");
		}catch (RTException e){
			if (e.getMessage().contains("The property does not exist")){
				return associatedDeviceIds;
			}else{
				name = this.getDeviceId() + "_" + DeviceRTDPrefix.SENSOR_CLOUD;
				sufix = 1;
				
				try {
					while (Rts2GatewayTools.existDeviceName(name)){
						name = this.getDeviceId() + "_" + DeviceRTDPrefix.SENSOR_CLOUD + String.valueOf(sufix);
						sufix++;
					}
				} catch (RTException ex) {			
					ex.printStackTrace();
				}				
				associatedDeviceIds.add (name);
			}				
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
			else if (deviceId.contains(DeviceRTDPrefix.SENSOR_RH))
				return DeviceType.RH_SENSOR;
			else if (deviceId.contains(DeviceRTDPrefix.SENSOR_WIND_SPEED))
				return DeviceType.WIND_SPEED_SENSOR;
			else if (deviceId.contains(DeviceRTDPrefix.SENSOR_CLOUD))
				return DeviceType.CLOUD_DETECTOR;
			else if (deviceId.contains(DeviceRTDPrefix.SENSOR_BAROMETER))
				return DeviceType.BAROMETER;
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

		return "DAVIS";
	}

}

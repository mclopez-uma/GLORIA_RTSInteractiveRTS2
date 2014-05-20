package eu.gloria.rts2.rtd;

import java.util.ArrayList;
import java.util.List;

import eu.gloria.rt.entity.device.ActivityState;
import eu.gloria.rt.entity.device.AlarmState;
import eu.gloria.rt.entity.device.BlockState;
import eu.gloria.rt.entity.device.Device;
import eu.gloria.rt.entity.device.DeviceGeneral;
import eu.gloria.rt.entity.device.DeviceProperty;
import eu.gloria.rt.entity.device.DeviceType;
import eu.gloria.rt.entity.device.MeasureUnit;
import eu.gloria.rt.entity.device.SensorStateIntervalDouble;
import eu.gloria.rt.exception.RTException;
import eu.gloria.rt.exception.UnsupportedOpException;
import eu.gloria.rtd.RTDWindSpeedInterface;
import eu.gloria.rts2.http.Rts2Date;
import eu.gloria.rts2.http.Rts2GatewayDeviceManager;
import eu.gloria.rts2.http.Rts2MessageType;
import eu.gloria.rts2.http.Rts2Messages;

/**
 * RTS2 Wind Speed Sensor associated to a Davis sensor
 * 
 * @author mclopez
 *
 */
public class BARTD50WindSpeedSensorRTD extends DeviceRTD implements RTDWindSpeedInterface {

	private AlarmState previousAlarm = null;
	
	@Override
	public MeasureUnit wspGetMeasureUnit() throws RTException {
		return MeasureUnit.KM_H;
	}

	@Override
	public double wspGetMeasure() throws RTException {

		double result = 0;
		DeviceProperty property = null;
		
		try {
			property = this.devGetDeviceProperty("WIND_SPEED");
		} catch (RTException e1) {
			throw new RTException (e1.getMessage());
		}
		
		result =  Double.valueOf(property.getValue().get(1));		
		//result is in m/segs -> Km/h
		result = (result / 1000) * 3600;
		
		return result;
	}

	
	@Override
	public void wspSetMeasureStates(List<SensorStateIntervalDouble> states)	throws RTException {

		


	}

	@Override
	public List<SensorStateIntervalDouble> wspGetMeasureStates() throws RTException {
				
		return  null;
		
	}
	
	@Override
	public Device devGetDevice(boolean allProperties)  throws RTException{
		
		DeviceGeneral dev = new DeviceGeneral();
		
		//sets the type
		dev.setType(DeviceType.WIND_SPEED_SENSOR);
		//Description
		dev.setDescription(this.devGetDeviceProperty("WIND_SPEED").getDescription());
		//Info
		dev.setInfo("RTS2-unavailable");
		//ShortName
		dev.setShortName(getDeviceId());
		//Version
		dev.setVersion("RTS2-unavailable");
		
		//Recover the parent device information
		Rts2GatewayDeviceManager manager = new Rts2GatewayDeviceManager();
		DeviceGeneral parent = (DeviceGeneral) manager.getDevice(getParentDeviceId(), null);
		
		dev.setBlockState(BlockState.UNBLOCK);	//Weather sensors are not blocked
		if (parent.getActivityState() != ActivityState.ERROR){
			if (parent.getAlarmState()== AlarmState.NONE){
				AlarmState alarm = getAlarmState();	
				if (alarm != null){
					dev.setAlarmState(getAlarmState());
					previousAlarm = alarm;
				}else{
					if (previousAlarm == null) //First time value within margins
						dev.setAlarmState(AlarmState.NONE);
					else
						dev.setAlarmState(previousAlarm);
				}
			}else{
				dev.setAlarmState(parent.getAlarmState());
			}		
		}else{
			dev.setAlarmState(parent.getAlarmState());
		}
		
		dev.setActivityState(parent.getActivityState());
		dev.setCommunicationState(parent.getCommunicationState());
		dev.setActivityStateDesc(parent.getActivityStateDesc());
		
		//Properties
		if (dev.getActivityState() != ActivityState.ERROR){
			if (dev.getAlarmState() == AlarmState.NONE){
				if (allProperties){

					List <DeviceProperty> devProperties = new ArrayList<DeviceProperty>();

					DeviceProperty devProperty = new DeviceProperty();
					devProperty = devGetDeviceProperty("WIND_SPEED");
					devProperties.add(devProperty);

					dev.getProperties().addAll(devProperties);

				}
			}
		}
		return dev;
	}
	
	
	@Override
	public Device getDevice(List<String> propertyNames) throws RTException {
		
		return devGetDevice(true);
		
	}
	
	private AlarmState getAlarmState() throws RTException{
		
		DeviceProperty trigBad = this.devGetDeviceProperty("WIND_TRIGBAD");
		DeviceProperty trigGood = this.devGetDeviceProperty("WIND_TRIGGOOD");
		double measure = Double.valueOf(this.devGetDeviceProperty("WIND_SPEED").getValue().get(1));
		
		if (measure > Double.parseDouble(trigBad.getValue().get(0))){
			return AlarmState.WEATHER;
		}else if (measure < Double.parseDouble(trigGood.getValue().get(0))){
			return AlarmState.NONE;
		}
		
		return null;
	}

}

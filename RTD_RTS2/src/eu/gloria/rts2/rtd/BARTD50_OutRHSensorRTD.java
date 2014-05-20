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
import eu.gloria.rtd.RTDRHSensorInterface;
import eu.gloria.rts2.http.Rts2Date;
import eu.gloria.rts2.http.Rts2GatewayDeviceManager;
import eu.gloria.rts2.http.Rts2MessageType;
import eu.gloria.rts2.http.Rts2Messages;

/**
 * RTS2 Humidity Sensor associated to a Davis sensor
 * 
 * @author mclopez
 *
 */
public class BARTD50_OutRHSensorRTD extends DeviceRTD implements RTDRHSensorInterface {

	public static void main(String[] args){	
		
		BARTD50_OutRHSensorRTD dev = new BARTD50_OutRHSensorRTD();
		try {
			dev.devGetDevice(false);
		} catch (RTException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	private AlarmState previousAlarm = null;
	
	@Override
	public MeasureUnit rhsGetMeasureUnit() throws RTException {
		
		return MeasureUnit.PERCENT;
	}

	@Override
	public double rhsGetMeasure() throws RTException {

		DeviceProperty property = this.devGetDeviceProperty("HUM_OUT");
		
		return Double.valueOf(property.getValue().get(1));
	}

	@Override
	public void rhsSetMeasureStates(List<SensorStateIntervalDouble> states)	throws RTException {
	


	}

	@Override
	public List<SensorStateIntervalDouble> rhsGetMeasureStates() throws RTException {

		return null;
	}
	
	@Override
	public Device devGetDevice(boolean allProperties)  throws RTException{
		
		DeviceGeneral dev = new DeviceGeneral();
		
		//sets the type
		dev.setType(DeviceType.RH_SENSOR);
		//Description
		dev.setDescription(this.devGetDeviceProperty("HUM_OUT").getDescription());
		//Info
		dev.setInfo("RTS2-unavailable");
		//ShortName
		dev.setShortName(getDeviceId());
		//Version
		dev.setVersion("RTS2-unavailable");
		
		//Recover the parent device information
		Rts2GatewayDeviceManager manager = new Rts2GatewayDeviceManager();
		DeviceGeneral parent = (DeviceGeneral) manager.getDevice(getParentDeviceId(), null);
		
		dev.setBlockState(BlockState.UNBLOCK);	//Weather sensor are not blocked
		
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
					devProperty = devGetDeviceProperty("HUM_OUT");
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
		
		DeviceProperty trigBad = this.devGetDeviceProperty("HUM_TRIGBAD");
		if (!trigBad.getValue().isEmpty()){
			DeviceProperty trigGood = this.devGetDeviceProperty("HUM_TRIGGOOD");
			if (!trigGood.getValue().isEmpty()){
				double measure = rhsGetMeasure();

				if (measure > Double.parseDouble(trigBad.getValue().get(0))){
					return AlarmState.WEATHER;
				}else if (measure < Double.parseDouble(trigGood.getValue().get(0))){
					return AlarmState.NONE;
				}
			}
		}
		return null;
	}
}

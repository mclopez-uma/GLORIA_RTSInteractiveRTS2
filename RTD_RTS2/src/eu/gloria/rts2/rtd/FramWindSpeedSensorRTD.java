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
import eu.gloria.rtd.RTDWindSpeedInterface;
import eu.gloria.rts2.http.Rts2Date;
import eu.gloria.rts2.http.Rts2GatewayDeviceManager;
import eu.gloria.rts2.http.Rts2MessageType;
import eu.gloria.rts2.http.Rts2Messages;

/**
 * RTS2 Wind Speed Sensor associated to a Fram-weather sensor
 * 
 * @author mclopez
 *
 */
public class FramWindSpeedSensorRTD extends DeviceRTD implements RTDWindSpeedInterface {

	@Override
	public MeasureUnit wspGetMeasureUnit() throws RTException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public double wspGetMeasure() throws RTException {

		DeviceProperty property = this.devGetDeviceProperty("windspeed");

		return Double.valueOf(property.getValue().get(0));
	}

	@Override
	public void wspSetMeasureStates(List<SensorStateIntervalDouble> states)	throws RTException {
		
		//NO SE HA PROBADO!!!

		String value[] = new String[2];		


		if (states.size() != 2)
			throw new RTException ("Number of interval is not correct");

		int k, j,i;
		i=0;j=1;
		for (k=0 ; k<states.size() ; k++){
			if (states.get(i).isAlarm()){
				if (states.get(j).isAlarm())
					throw new RTException ("Interval specification is not correct");

				if (Math.abs(states.get(i).getBeginValue()) != 10000){
					if (Math.abs(states.get(i).getEndValue()) != 10000)
						throw new RTException ("Interval specification is not correct");


					value[i] = String.valueOf(states.get(i).getBeginValue());	
				}else{
					if (Math.abs(states.get(i).getEndValue()) == 10000)
						throw new RTException ("Interval specification is not correct");

					value[i] = String.valueOf(states.get(i).getEndValue());
				}				

			}else{
				if (!states.get(j).isAlarm())
					throw new RTException ("Interval specification is not correct");

				if (Math.abs(states.get(i).getBeginValue()) != 10000){
					if (Math.abs(states.get(i).getEndValue()) != 10000)
						throw new RTException ("Interval specification is not correct");


					value[i] = String.valueOf(states.get(i).getBeginValue());	
				}else{
					if (Math.abs(states.get(i).getEndValue()) == 10000)
						throw new RTException ("Interval specification is not correct");

					value[i] = String.valueOf(states.get(i).getEndValue());
				}				

			}
			i=1;j=0;
		}

		if (value[0].compareTo(value[1])!=0)
			throw new RTException ("Interval specification is not correct");

		List<String> valueProp = new ArrayList<String>();
		valueProp.add(value[0]);
		long time = Rts2Date.now();

		this.devUpdateDeviceProperty("max_windspeed", valueProp);
		//Message recovering
		String message = Rts2Messages.getMessageText (Rts2MessageType.error, getDeviceId(), time);
		if (message != null)
			throw new RTException(message);		


	}

	@Override
	public List<SensorStateIntervalDouble> wspGetMeasureStates() throws RTException {

		List<SensorStateIntervalDouble> intervals = new ArrayList<SensorStateIntervalDouble>();
		SensorStateIntervalDouble interval = new SensorStateIntervalDouble();
		DeviceProperty property = this.devGetDeviceProperty("max_windspeed");
		
		interval = new SensorStateIntervalDouble();
		interval.setBeginValue(10000);	//Infinite
		interval.setBeginClosed(false);
		interval.setAlarm(true);		
		interval.setEndValue(Double.valueOf(property.getValue().get(0)));
		interval.setEndClosed(false);
		intervals.add(interval);
		
		interval.setBeginValue(-10000);	//Infinite
		interval.setBeginClosed(false);
		interval.setAlarm(false);		
		interval.setEndValue(Double.valueOf(property.getValue().get(0)));
		interval.setEndClosed(true);
		intervals.add(interval);
		
		return  intervals;
	}
	
	@Override
	public Device devGetDevice(boolean allProperties)  throws RTException{
		
		DeviceGeneral dev = new DeviceGeneral();
		
		//sets the type
		dev.setType(DeviceType.WIND_SPEED_SENSOR);
		//Description
		dev.setDescription("RTS2-unavailable");
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
		dev.setAlarmState(parent.getAlarmState());		
		dev.setActivityState(parent.getActivityState());
		dev.setCommunicationState(parent.getCommunicationState());
		dev.setActivityStateDesc(parent.getActivityStateDesc());
		
		//Properties
		if (dev.getActivityState() != ActivityState.ERROR){
			if (dev.getAlarmState() == AlarmState.NONE){
				if (allProperties){

					List <DeviceProperty> devProperties = null;

					DeviceProperty devProperty = new DeviceProperty();
					devProperty = devGetDeviceProperty("windspeed");
					devProperties.add(devProperty);

					devProperty = devGetDeviceProperty("max_windspeed");
					devProperties.add(devProperty);


					dev.getProperties().addAll(devProperties);

				}
			}
		}

		return dev;
	}
	
	
	@Override
	public Device getDevice(List<String> propertyNames) throws RTException {
		
         DeviceGeneral dev = new DeviceGeneral();
		
		//sets the type
		dev.setType(DeviceType.WIND_SPEED_SENSOR);
		//Description
		dev.setDescription("RTS2-unavailable");
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
		dev.setAlarmState(parent.getAlarmState());		
		dev.setActivityState(parent.getActivityState());
		dev.setCommunicationState(parent.getCommunicationState());
		dev.setActivityStateDesc(parent.getActivityStateDesc());
		
		//Properties
		if (dev.getActivityState() != ActivityState.ERROR){
			List <DeviceProperty> devProperties = null;
			DeviceProperty devProperty = new DeviceProperty();

			if (propertyNames.contains("windspeed")){

				devProperty = devGetDeviceProperty("windspeed");
				devProperties.add(devProperty);

			}else if (propertyNames.contains("max_windspeed")){	

				devProperty = devGetDeviceProperty("max_windspeed");
				devProperties.add(devProperty);				
			}

			dev.getProperties().addAll(devProperties);
		}
		
		return dev;
		
	}

}

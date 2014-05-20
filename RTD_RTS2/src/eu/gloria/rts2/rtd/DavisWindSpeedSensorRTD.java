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
public class DavisWindSpeedSensorRTD extends DeviceRTD implements RTDWindSpeedInterface {

	private String windProperty = null;
	
	@Override
	public MeasureUnit wspGetMeasureUnit() throws RTException {
		return MeasureUnit.KM_H;
	}

	@Override
	public double wspGetMeasure() throws RTException {

		double result = 0;
		DeviceProperty property = null;
		
		try {
			property = this.devGetDeviceProperty("AVGWIND");
		} catch (RTException e1) {
			throw new RTException (e1.getMessage());
		}
		
		if (!property.getValue().isEmpty()){
			
			windProperty = "max_windspeed";
			//property = this.devGetDeviceProperty("AVGWIND");
			
			result =  Double.valueOf(property.getValue().get(0));
			
		}else{
			
			property = this.devGetDeviceProperty("PEEKWIND");
			
			if (!property.getValue().isEmpty()){
				windProperty = "max_peek_windspeed";
				//property = this.devGetDeviceProperty("PEEKWIND");

				result = Double.valueOf(property.getValue().get(0));

			}else{
				
				throw new UnsupportedOpException ("Operation not supported");
			}
		}
		
		//result is in m/segs -> Km/h
		result = (result / 1000) * 3600;
		
		return result;
	}

	
	@Override
	public void wspSetMeasureStates(List<SensorStateIntervalDouble> states)	throws RTException {

		//NO SE HA PROBADO!!!

		String value[] = new String[2];		


		if (states.size() != 2)
			throw new RTException ("Number of interval is not correct");

		int k, j, i;
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

		this.devUpdateDeviceProperty(windProperty, valueProp);
		//Message recovering
		String message = Rts2Messages.getMessageText (Rts2MessageType.error, getDeviceId(), time);
		if (message != null)
			throw new RTException(message);		


	}

	@Override
	public List<SensorStateIntervalDouble> wspGetMeasureStates() throws RTException {

		List<SensorStateIntervalDouble> intervals = new ArrayList<SensorStateIntervalDouble>();
		SensorStateIntervalDouble interval = new SensorStateIntervalDouble();
		DeviceProperty property = this.devGetDeviceProperty(windProperty);
		
		if (!property.getValue().isEmpty()){
			interval = new SensorStateIntervalDouble();
			interval.setBeginValue(10000);	//Infinite
			interval.setBeginClosed(false);
			interval.setAlarm(true);		
			interval.setEndValue(Double.valueOf(property.getValue().get(0)));
			interval.setEndClosed(false);
			intervals.add(interval);

			interval = new SensorStateIntervalDouble();
			interval.setBeginValue(-10000);	//Infinite
			interval.setBeginClosed(false);
			interval.setAlarm(false);		
			interval.setEndValue(Double.valueOf(property.getValue().get(0)));
			interval.setEndClosed(true);
			intervals.add(interval);
		}else{
			throw new UnsupportedOpException ("Operation not supported");
		}
		
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
				
		dev.setActivityState(parent.getActivityState());
		dev.setCommunicationState(parent.getCommunicationState());
		dev.setActivityStateDesc(parent.getActivityStateDesc());
		
		//Properties
		if (parent.getActivityState() != ActivityState.ERROR){
			if (parent.getAlarmState() == AlarmState.NONE){

				Double value, max;
				List <DeviceProperty> devProperties = new ArrayList<DeviceProperty>();
				//Always to be recovered due to alarm
				if (windProperty == "max_windspeed"){
					DeviceProperty devProperty = new DeviceProperty();					
					devProperty = devGetDeviceProperty("AVGWIND");
					devProperties.add(devProperty);
					value = Double.valueOf(devProperty.getValue().get(0));

					devProperty = devGetDeviceProperty("max_windspeed");
					devProperties.add(devProperty);

					dev.getProperties().addAll(devProperties);

					if (!devProperty.getValue().isEmpty()){
						max = Double.valueOf(devProperty.getValue().get(0));
						if (value > max)
							dev.setAlarmState(AlarmState.WEATHER);
					}

				}else if (windProperty == "max_peek_windspeed"){
					DeviceProperty devProperty = new DeviceProperty();					
					devProperty = devGetDeviceProperty("PEEKWIND");
					devProperties.add(devProperty);
					value = Double.valueOf(devProperty.getValue().get(0));

					devProperty = devGetDeviceProperty("max_peek_windspeed");
					devProperties.add(devProperty);					

					dev.getProperties().addAll(devProperties);

					if (!devProperty.getValue().isEmpty()){
						max = Double.valueOf(devProperty.getValue().get(0));
						if (value > max)
							dev.setAlarmState(AlarmState.WEATHER);
					}
				}

			}else{
				dev.setAlarmState(parent.getAlarmState());
			}
		}else{
			dev.setAlarmState(parent.getAlarmState());
		}
		return dev;
	}
	
	
	@Override
	public Device getDevice(List<String> propertyNames) throws RTException {
		
		DeviceGeneral dev = (DeviceGeneral) devGetDevice(false);
		
		return dev;
		
	}

}

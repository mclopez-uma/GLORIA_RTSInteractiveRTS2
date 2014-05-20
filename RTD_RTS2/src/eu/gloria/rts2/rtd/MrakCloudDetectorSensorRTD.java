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
import eu.gloria.rtd.RTDCloudDetectorInterface;
import eu.gloria.rts2.http.Rts2Date;
import eu.gloria.rts2.http.Rts2GatewayDeviceManager;
import eu.gloria.rts2.http.Rts2MessageType;
import eu.gloria.rts2.http.Rts2Messages;

/**
 * RTS2 mrak Cloud Detector Sensor 
 * 
 * @author mclopez
 *
 */
public class MrakCloudDetectorSensorRTD extends DeviceRTD implements RTDCloudDetectorInterface {

	private AlarmState previousAlarm = null;
	
	@Override
	public MeasureUnit cldGetMeasureUnit() throws RTException {
		
		return MeasureUnit.DEGREE;
	}

	/**
	 * {@inheritDoc}
	 * <p>
	 * This sensor measures the temperature difference between in and outside.
	 */
	@Override
	public double cldGetMeasure() throws RTException {

		DeviceProperty property = this.devGetDeviceProperty("TEMP_DIFF");
		
		return Double.valueOf(property.getValue().get(1));
	}

	/**
	 * {@inheritDoc}
	 * <p>
	 * Value of +/- 10000 represents +/- infinite
	 */
	@Override
	public void cldSetMeasureStates(List<SensorStateIntervalDouble> states)	throws RTException {
		
		//NO SE HA PROBADO!!!
		
		String value = null;
		String property = null;
		
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


					value = String.valueOf(states.get(i).getBeginValue());	
				}else{
					if (Math.abs(states.get(i).getEndValue()) == 10000)
						throw new RTException ("Interval specification is not correct");

					value = String.valueOf(states.get(i).getEndValue());
				}
				
				property = "TRIGBAD";
			}else{
				if (!states.get(j).isAlarm())
					throw new RTException ("Interval specification is not correct");
				
				if (Math.abs(states.get(i).getBeginValue()) != 10000){
					if (Math.abs(states.get(i).getEndValue()) != 10000)
						throw new RTException ("Interval specification is not correct");


					value = String.valueOf(states.get(i).getBeginValue());	
				}else{
					if (Math.abs(states.get(i).getEndValue()) == 10000)
						throw new RTException ("Interval specification is not correct");

					value = String.valueOf(states.get(i).getEndValue());
				}
				
				property = "TRIGGOOD";
			}
			i=1;j=0;
		}
		
		
		List<String> valueProp = new ArrayList<String>();
		valueProp.add(value);
		long time = Rts2Date.now();
		
		this.devUpdateDeviceProperty(property, valueProp);
		//Message recovering
		String message = Rts2Messages.getMessageText (Rts2MessageType.error, getDeviceId(), time);
		if (message != null)
			throw new RTException(message);			
	}

	/**
	 * {@inheritDoc}
	 * <p>
	 * Value of +/- 10000 represents +/- infinite
	 */
	@Override
	public List<SensorStateIntervalDouble> cldGetMeasureStates() throws RTException {
		
		List<SensorStateIntervalDouble> intervals = new ArrayList<SensorStateIntervalDouble>();
		SensorStateIntervalDouble interval = new SensorStateIntervalDouble();
		
		interval = new SensorStateIntervalDouble();
		interval.setBeginValue(10000);	//Infinite
		interval.setBeginClosed(false);
		interval.setAlarm(false);
		DeviceProperty property = this.devGetDeviceProperty("TRIGGOOD");
		interval.setEndValue(Double.valueOf(property.getValue().get(0)));
		interval.setEndClosed(false);
		intervals.add(interval);
		
		interval.setBeginValue(-10000);	//Infinite
		interval.setBeginClosed(false);
		interval.setAlarm(true);
		property = this.devGetDeviceProperty("TRIGBAD");
		interval.setEndValue(Double.valueOf(property.getValue().get(0)));
		interval.setEndClosed(false);
		intervals.add(interval);
		
		return  intervals;
	}
	

	public Device devGetDevice(boolean allProperties)  throws RTException{

		DeviceGeneral dev = null;
		
		if (!allProperties){
			List<String> propertyNames = new ArrayList<String> ();
			propertyNames.add("TRIGBAD");
			propertyNames.add("TRIGGOOD");
			propertyNames.add("TEMP_DIFF");
			
			dev = (DeviceGeneral) super.getDevice(propertyNames);		
		}else{
			dev = (DeviceGeneral) super.devGetDevice(allProperties);
		}
	
		
		if (dev.getActivityState() != ActivityState.ERROR){
			AlarmState alarm = getAlarmState(dev.getProperties());	
			if (alarm != null){
				dev.setAlarmState(getAlarmState(dev.getProperties()));
				previousAlarm = alarm;
			}else{
				if (previousAlarm == null) //First time value within margins
					dev.setAlarmState(AlarmState.NONE);
				else
					dev.setAlarmState(previousAlarm);
			}
		}
		
		
		
		return dev;

	}
	
	
	@Override
	public Device getDevice(List<String> propertyNames) throws RTException {

		Device dev = devGetDevice(false);
		
		//if (dev.getAlarmState() == AlarmState.NONE){
			AlarmState alarm = getAlarmState(dev.getProperties());	
			if (alarm != null){
				dev.setAlarmState(getAlarmState(dev.getProperties()));
				previousAlarm = alarm;
			}else{
				if (previousAlarm == null) //First time value within margins
					dev.setAlarmState(AlarmState.NONE);
				else
					dev.setAlarmState(previousAlarm);
			}
		//}
		
		return dev;
	}
	
	private AlarmState getAlarmState(List<DeviceProperty> properties) throws RTException{
		
		Double trigBad=null, trigGood=null, measure=null;
		for (DeviceProperty prop: properties){				
			if (prop.getName().equals("TRIGBAD")){
				if (!prop.getValue().isEmpty()){
					trigBad=Double.parseDouble(prop.getValue().get(0));
				}else{
					return null;
				}
			}
			if (prop.getName().equals("TRIGGOOD")){
				if (!prop.getValue().isEmpty()){
					trigGood=Double.parseDouble(prop.getValue().get(0));
				}else{
					return null;
				}
			}
			if (prop.getName().equals("TEMP_DIFF")){
				if (!prop.getValue().isEmpty()){
					measure=Double.parseDouble(prop.getValue().get(1));
				}else{
					return null;
				}
			}
		}			
		
		if (measure < trigBad){
			return AlarmState.WEATHER;
		}else if (measure > trigGood){
			return AlarmState.NONE;
		}
		
		return null;
	}
}

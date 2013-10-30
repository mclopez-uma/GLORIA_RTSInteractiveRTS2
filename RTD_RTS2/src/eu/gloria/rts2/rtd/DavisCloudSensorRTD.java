package eu.gloria.rts2.rtd;

import java.util.ArrayList;
import java.util.List;

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
 * RTS2 Cloud Sensor associated to a Davis sensor
 * 
 * @author mclopez
 *
 */
public class DavisCloudSensorRTD extends DeviceRTD implements RTDCloudDetectorInterface {

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
		
		DeviceProperty property = this.devGetDeviceProperty("CLOUD_S");
		
		return Integer.valueOf(property.getValue().get(0));
	}

	@Override
	public void cldSetMeasureStates(List<SensorStateIntervalDouble> states)	throws RTException {
		
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
		
		this.devUpdateDeviceProperty("cloud_bad", valueProp);
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
		DeviceProperty property = this.devGetDeviceProperty("cloud_bad");
		
		interval = new SensorStateIntervalDouble();
		interval.setBeginValue(-10000);	//Infinite
		interval.setBeginClosed(false);
		interval.setAlarm(true);		
		interval.setEndValue(Double.valueOf(property.getValue().get(0)));
		interval.setEndClosed(true);
		intervals.add(interval);
		
		interval.setEndValue(10000);	//Infinite
		interval.setEndClosed(false);
		interval.setAlarm(false);		
		interval.setBeginValue(Double.valueOf(property.getValue().get(0)));
		interval.setBeginClosed(false);
		intervals.add(interval);
		
		return  intervals;
	}

	@Override
	public Device devGetDevice(boolean allProperties)  throws RTException{
		
		DeviceGeneral dev = new DeviceGeneral();
		
		//sets the type
		dev.setType(DeviceType.CLOUD_DETECTOR);
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
		if (dev.getAlarmState() == AlarmState.NONE){
			if (allProperties){

				List <DeviceProperty> devProperties = new ArrayList<DeviceProperty>();;

				DeviceProperty devProperty = new DeviceProperty();
				devProperty = devGetDeviceProperty("CLOUD_S");
				devProperties.add(devProperty);

				devProperty = devGetDeviceProperty("cloud_bad");
				devProperties.add(devProperty);			

				dev.getProperties().addAll(devProperties);

			}
		}
		
		return dev;
	}
	
	
	@Override
	public Device getDevice(List<String> propertyNames) throws RTException {
		
		DeviceGeneral dev = new DeviceGeneral();
		
		//sets the type
		dev.setType(DeviceType.CLOUD_DETECTOR);
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
		List <DeviceProperty> devProperties = new ArrayList<DeviceProperty>();;
		DeviceProperty devProperty = new DeviceProperty();
		
		if (propertyNames.contains("CLOUD_S")){
			
			devProperty = devGetDeviceProperty("CLOUD_S");
			devProperties.add(devProperty);
		
		}else if (propertyNames.contains("cloud_bad")){	
			
			devProperty = devGetDeviceProperty("cloud_bad");
			devProperties.add(devProperty);				
		}
		
		dev.getProperties().addAll(devProperties);
		
		return dev;
	}

}

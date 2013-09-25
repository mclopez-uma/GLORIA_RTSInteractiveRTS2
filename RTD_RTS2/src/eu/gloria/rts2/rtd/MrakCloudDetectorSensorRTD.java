package eu.gloria.rts2.rtd;

import java.util.ArrayList;
import java.util.List;

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
	
//	@Override
//	public Device devGetDevice(boolean allProperties)  throws RTException{
//		
//		DeviceGeneral dev = new DeviceGeneral();
//		
//		//sets the type
//		dev.setType(DeviceType.CLOUD_DETECTOR);
//		//Description
//		dev.setDescription("RTS2-unavailable");
//		//Info
//		dev.setInfo("RTS2-unavailable");
//		//ShortName
//		dev.setShortName(getDeviceId());
//		//Version
//		dev.setVersion("RTS2-unavailable");
//		
//		//Recover the parent device information
//		Rts2GatewayDeviceManager manager = new Rts2GatewayDeviceManager();
//		DeviceGeneral parent = (DeviceGeneral) manager.getDevice(getParentDeviceId(), null);
//		
//		dev.setBlockState(BlockState.UNBLOCK);	//Weather sensor are not blocked
//		dev.setAlarmState(parent.getAlarmState());		
//		dev.setActivityState(parent.getActivityState());
//		dev.setCommunicationState(parent.getCommunicationState());
//		dev.setActivityStateDesc(parent.getActivityStateDesc());
//		
//		//Properties
//		if (allProperties){
//			
//			DeviceProperty devProperty = new DeviceProperty();
//			devProperty = devGetDeviceProperty("TEMP_DIFF");
//			
//			dev.getProperties().add(devProperty);
//			
//			devProperty = new DeviceProperty();
//			devProperty = devGetDeviceProperty("TRIGGOOD");
//			
//			dev.getProperties().add(devProperty);
//			
//			devProperty = new DeviceProperty();
//			devProperty = devGetDeviceProperty("TRIGBAD");
//			
//			dev.getProperties().add(devProperty);
//			
//		}
//		
//		return dev;
//	}
	
	
//	@Override
//	public Device getDevice(List<String> propertyNames) throws RTException {
//		
//		DeviceGeneral dev = new DeviceGeneral();
//		
//		//sets the type
//		dev.setType(DeviceType.CLOUD_DETECTOR);
//		//Description
//		dev.setDescription("RTS2-unavailable");
//		//Info
//		dev.setInfo("RTS2-unavailable");
//		//ShortName
//		dev.setShortName(getDeviceId());
//		//Version
//		dev.setVersion("RTS2-unavailable");
//		
//		//Recover the parent device information
//		Rts2GatewayDeviceManager manager = new Rts2GatewayDeviceManager();
//		DeviceGeneral parent = (DeviceGeneral) manager.getDevice(getParentDeviceId(), null);
//		
//		dev.setBlockState(BlockState.UNBLOCK);	//Weather sensor are not blocked
//		dev.setAlarmState(parent.getAlarmState());		
//		dev.setActivityState(parent.getActivityState());
//		dev.setCommunicationState(parent.getCommunicationState());
//		dev.setActivityStateDesc(parent.getActivityStateDesc());
//		
//		//Properties
//		if (propertyNames.contains("TEMP_DIFF")){
//			
//			DeviceProperty devProperty = new DeviceProperty();
//			devProperty = devGetDeviceProperty("TEMP_DIFF");
//			
//			dev.getProperties().add(devProperty);
//			
//		}else if (propertyNames.contains("TRIGGOOD")){
//			
//			DeviceProperty devProperty = new DeviceProperty();
//			devProperty = devGetDeviceProperty("TRIGGOOD");
//			
//			dev.getProperties().add(devProperty);
//			
//		}else if (propertyNames.contains("TRIGBAD")){
//			
//			DeviceProperty devProperty = new DeviceProperty();
//			devProperty = devGetDeviceProperty("TRIGBAD");
//			
//			dev.getProperties().add(devProperty);
//			
//		}
//		
//		return dev;
//	}
}

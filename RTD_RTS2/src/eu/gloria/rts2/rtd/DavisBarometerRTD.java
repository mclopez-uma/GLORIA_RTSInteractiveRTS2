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
import eu.gloria.rtd.RTDBarometerInterface;
import eu.gloria.rts2.http.Rts2GatewayDeviceManager;

/**
 * RTS2 - DAVIS - Barometer.
 * 
 * @author jcabello
 *
 */
public class DavisBarometerRTD  extends DeviceRTD implements RTDBarometerInterface{

	@Override
	public MeasureUnit barGetMeasureUnit() throws RTException {
		return MeasureUnit.PASCAL;
	}

	@Override
	public double barGetMeasure() throws RTException {
		
		DeviceProperty property = this.devGetDeviceProperty("BAR_PRESS");
		
		//millibars
		double result = Double.valueOf(property.getValue().get(0));
		
		//convert to pascals (100 pascal = 1 millibar; 1 millibar = 1000 bar)
		result = result * 100;
		
		return result;
	}

	@Override
	public void barSetMeasureStates(List<SensorStateIntervalDouble> states)
			throws RTException {
		
		//TODO
		
	}

	@Override
	public List<SensorStateIntervalDouble> barGetMeasureStates()
			throws RTException {
		
		//TODO
		return null;
	}
	
	@Override
	public Device devGetDevice(boolean allProperties)  throws RTException{
		
		DeviceGeneral dev = new DeviceGeneral();
		
		//sets the type
		dev.setType(DeviceType.BAROMETER);
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

					List <DeviceProperty> devProperties = new ArrayList <DeviceProperty>();

					DeviceProperty devProperty = new DeviceProperty();
					devProperty = devGetDeviceProperty("BAR_PRESS");
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

}

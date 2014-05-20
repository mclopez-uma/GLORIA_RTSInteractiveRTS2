package eu.gloria.rts2.rtd;

import java.util.ArrayList;
import java.util.List;

import eu.gloria.rt.entity.device.ActivityState;
import eu.gloria.rt.entity.device.ActivityStateDome;
import eu.gloria.rt.entity.device.ActivityStateDomeOpening;
import eu.gloria.rt.entity.device.AlarmState;
import eu.gloria.rt.entity.device.BlockState;
import eu.gloria.rt.entity.device.Device;
import eu.gloria.rt.entity.device.DeviceDome;
import eu.gloria.rt.entity.device.DeviceGeneral;
import eu.gloria.rt.entity.device.DeviceProperty;
import eu.gloria.rt.entity.device.DeviceType;
import eu.gloria.rt.entity.device.MeasureUnit;
import eu.gloria.rt.entity.device.SensorStateIntervalDouble;
import eu.gloria.rt.exception.RTException;
import eu.gloria.rt.exception.UnsupportedOpException;
import eu.gloria.rtd.RTDRainDetectorInterface;
import eu.gloria.rts2.http.Rts2GatewayDeviceManager;

/**
 * RTS2 Rain Sensor 
 * 
 * @author mclopez
 *
 */
public class BartRainSensorRTD extends DeviceRTD implements RTDRainDetectorInterface {

	public static void main(String[] args){	
		
		BartRainSensorRTD dev = new BartRainSensorRTD();
		try {
			dev.devGetDevice(false);
		} catch (RTException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public BartRainSensorRTD() {
		// TODO Auto-generated constructor stub
	}

	@Override
	public MeasureUnit rndGetMeasureUnit() throws RTException {
		
		throw new UnsupportedOpException ("Operation not supported");
	}

	@Override
	public double rndGetMeasure() throws RTException {
		
		throw new UnsupportedOpException ("Operation not supported");
	}

	@Override
	public void rndSetMeasureStates(List<SensorStateIntervalDouble> states)
			throws RTException {
		
		throw new UnsupportedOpException ("Operation not supported");
	}

	@Override
	public List<SensorStateIntervalDouble> rndGetMeasureStates() throws RTException {
		
		throw new UnsupportedOpException ("Operation not supported");
	}

	@Override
	public boolean rndIsRaining() throws RTException {
		
		DeviceProperty property = this.devGetDeviceProperty("rain");
		
		if ((property.getValue().get(0)).equals("1"))
			return true;
		else
			return false;
		
		
	
		
	}
	
	
	public Device devGetDevice(boolean allProperties)  throws RTException{
		
		DeviceGeneral dev = null;
		
		if (!allProperties){
			List<String> propertyNames = new ArrayList<String> ();
			propertyNames.add("rain");
			
			dev = (DeviceGeneral) super.getDevice(propertyNames);		
		}else{
			dev = (DeviceGeneral) super.devGetDevice(allProperties);
		}
		
		if (dev.getActivityState() != ActivityState.ERROR){
			if (dev.getAlarmState() == AlarmState.NONE){
				for (DeviceProperty prop: dev.getProperties()){
					if (prop.getName().equals("rain"))
						if (prop.getValue().get(0).equals("1"))
							dev.setAlarmState(AlarmState.WEATHER);
				}			
			}
		}
		
		
		return dev;
	}
	
	
	
	public Device getDevice(List<String> propertyNames) throws RTException {
		
		if (!propertyNames.contains("rain"))
			propertyNames.add("rain");
		
		DeviceGeneral dev = (DeviceGeneral) super.getDevice(propertyNames);
		
		if (dev.getActivityState() != ActivityState.ERROR){
			if (dev.getAlarmState() == AlarmState.NONE){
				for (DeviceProperty prop: dev.getProperties()){
					if (prop.getName().equals("rain"))
						if (prop.getValue().equals("1"))
							dev.setAlarmState(AlarmState.WEATHER);
				}			
			}
		}
		
		return dev;
	}

}

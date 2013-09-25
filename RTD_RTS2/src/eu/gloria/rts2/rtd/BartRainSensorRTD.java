package eu.gloria.rts2.rtd;

import java.util.List;

import eu.gloria.rt.entity.device.ActivityState;
import eu.gloria.rt.entity.device.ActivityStateDome;
import eu.gloria.rt.entity.device.ActivityStateDomeOpening;
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
	
	/*@Override
	public Device devGetDevice(boolean allProperties)  throws RTException{
		
		DeviceGeneral dev = new DeviceGeneral();
		
		//sets the type
		dev.setType(DeviceType.RAIN_SENSOR);
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
		DeviceGeneral parent = (DeviceGeneral) manager.getDevice(getDeviceId(), null);
		//DeviceGeneral parent = (DeviceGeneral) manager.getDevice(getParentDeviceId(), null);
		
		dev.setBlockState(BlockState.UNBLOCK);	//Weather sensor are not blocked
		dev.setAlarmState(parent.getAlarmState());		
		dev.setActivityState(parent.getActivityState());
		dev.setCommunicationState(parent.getCommunicationState());
		dev.setActivityStateDesc(parent.getActivityStateDesc());
		
		
		//Properties
		if (allProperties){
			
			List <DeviceProperty> devProperties = null;
			
			DeviceProperty devProperty = new DeviceProperty();
			devProperty = devGetDeviceProperty("rain");
			devProperties.add(devProperty);			
				
			
			dev.getProperties().addAll(devProperties);
			
		}
		
		return dev;
	}*/
	
	
	/*@Override
	public Device getDevice(List<String> propertyNames) throws RTException {
		
		DeviceGeneral dev = new DeviceGeneral();
		
		//sets the type
		dev.setType(DeviceType.RAIN_SENSOR);
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
		DeviceGeneral parent = (DeviceGeneral) manager.getDevice(getDeviceId(), null);
		//DeviceGeneral parent = (DeviceGeneral) manager.getDevice(getParentDeviceId(), null);
				
		dev.setBlockState(BlockState.UNBLOCK);	//Weather sensor are not blocked
		dev.setAlarmState(parent.getAlarmState());		
		dev.setActivityState(parent.getActivityState());
		dev.setCommunicationState(parent.getCommunicationState());
		dev.setActivityStateDesc(parent.getActivityStateDesc());
				
		//Properties
		List <DeviceProperty> devProperties = null;
		DeviceProperty devProperty = new DeviceProperty();
		
		if (propertyNames.contains("rain")){
			
			devProperty = devGetDeviceProperty("rain");
			devProperties.add(devProperty);
		
		}
		
		dev.getProperties().addAll(devProperties);
		
		return dev;
	}*/

}

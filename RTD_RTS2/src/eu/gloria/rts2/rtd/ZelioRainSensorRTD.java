package eu.gloria.rts2.rtd;

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
 * RTS2 Rain Sensor associated to a Zelio dome
 * 
 * @author mclopez
 *
 */
public class ZelioRainSensorRTD extends DeviceRTD implements RTDRainDetectorInterface {

	public ZelioRainSensorRTD() {
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
	
	@Override
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
		DeviceDome parent = (DeviceDome) manager.getDevice(getParentDeviceId(), null);

		dev.setBlockState(BlockState.UNBLOCK);	//Weather sensor are not blocked
		

		if (parent.getActivityStateOpening() == ActivityStateDomeOpening.CLOSE)
			dev.setActivityState(ActivityState.READY);
		else if (parent.getActivityStateOpening() == ActivityStateDomeOpening.CLOSING)
			dev.setActivityState(ActivityState.READY);
		else if (parent.getActivityStateOpening() == ActivityStateDomeOpening.OPEN)
			dev.setActivityState(ActivityState.READY);
		else if (parent.getActivityStateOpening() == ActivityStateDomeOpening.OPENING)
			dev.setActivityState(ActivityState.READY);
		else if (parent.getActivityStateOpening() == ActivityStateDomeOpening.PARTS_COMPOSITION)
			dev.setActivityState(ActivityState.READY);
		else
			dev.setActivityState(ActivityState.valueOf(parent.getActivityStateOpening().toString()));				


		dev.setCommunicationState(parent.getCommunicationState());
		dev.setActivityStateDesc(parent.getActivityStateDesc());
		
		//Properties	
		if (parent.getActivityState() != ActivityStateDome.ERROR){
			if (parent.getAlarmState() == AlarmState.NONE){

				DeviceProperty devProperty = new DeviceProperty();
				devProperty = devGetDeviceProperty("rain");

				if (devProperty.getValue().get(0).equals("1"))
					dev.setAlarmState(AlarmState.WEATHER);

				dev.getProperties().add(devProperty);

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
		
		return devGetDevice(false);
		
	}

}

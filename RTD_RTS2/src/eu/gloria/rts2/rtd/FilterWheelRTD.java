package eu.gloria.rts2.rtd;

import java.util.ArrayList;
import java.util.List;

import org.codehaus.jackson.map.ObjectMapper;

import eu.gloria.rt.entity.device.DeviceProperty;
import eu.gloria.rt.entity.device.FilterType;
import eu.gloria.rt.exception.RTException;
import eu.gloria.rt.exception.UnsupportedOpException;
import eu.gloria.rtc.DeviceDiscoverer;
import eu.gloria.rtd.RTDFilterWheelInterface;
import eu.gloria.rts2.http.Rts2Cmd;
import eu.gloria.rts2.http.Rts2CmdType;
import eu.gloria.rts2.http.Rts2Date;
import eu.gloria.rts2.http.Rts2IntervalPropertyConverter;
import eu.gloria.rts2.http.Rts2MessageType;
import eu.gloria.rts2.http.Rts2Messages;
import eu.gloria.tools.log.LogUtil;

/**
 * RTS2 RTDFilterWheelInterface implementation for generic RTS2 filterd device.
 * 
 * @author mclopez
 *
 */
public class FilterWheelRTD extends DeviceRTD implements RTDFilterWheelInterface {
	
	protected List<String> filtersLocalName;
	protected List<String> filtersGlobalName;
	
	/**
	 * Constructor
	 */
	public FilterWheelRTD () {
	
	}

	
	/**
	 * Initialization when the identifier is known.
	 */
	private void init () {
		
		//Process the mapping...
		//<filterLocalName=FilterGlobalName;filterLocalName=filterGlobalName....>
		try{
			
			filtersLocalName = new ArrayList<String>();
			filtersGlobalName = new ArrayList<String>();
			
			eu.gloria.rt.entity.environment.config.device.DeviceProperty mapping = DeviceRTD.configDeviceManager.getProperty(this.getDeviceId(), "FILTER_MAPPING");
			String filterMapping =  mapping.getDefaultValue();
			LogUtil.info(this, "FilterWheelRTD.Contructor(). original mapping=" + filterMapping);
			if (filterMapping != null){
				
				String[] pairs = filterMapping.split(";");
				for (int pair = 0 ; pair < pairs.length; pair++){
					String[] values = pairs[pair].split("=");
					
					try{
						
						FilterType filtrer = FilterType.fromValue(values[1]);
						filtersLocalName.add(values[0]);
						filtersGlobalName.add(values[1]);
						
					}catch(Exception ex){
						String[] namesLog = {
							"FilterLocalName",
							"FilterGlobalName"	,
							"message"
						};
						String[] valuesLog = {
								values[0],
								values[1],
								"Unknown Global Filter Type"
						};
						LogUtil.severe(this, "FilterWheelRTD.Contructor(). Loading filterMapping..." + LogUtil.getLog(namesLog, valuesLog));
					}
					
				}
				
			}
			
			
		}catch(Exception ex){
			LogUtil.severe(this, "FilterWheelRTD.Contructor(). Error reading the configuration parameter: FILTER_MAPPING. Error=" + ex.getMessage());
		}
		
	}
	
	/**
	 * {@inheritDoc} 
	 * <p>
	 * Recover from RTS2 the available filters.
	 */
	@Override
	public void setDeviceId(String deviceId) {
		
		super.setDeviceId(deviceId);
		
		this.init();
		
	}
	
	
	/**
	 * {@inheritDoc} 
	 * <p>
	 * Recovers "filter" from RTS2 filterd.
	 */
	@Override
	public int fwGetPositionNumber() throws RTException {

		DeviceProperty property = this.devGetDeviceProperty("filter");
		
		String currentValue = property.getValue().get(0);
		
		int index = this.filtersLocalName.indexOf(currentValue);
		if (index == -1){
			throw new RTException("Unknown filter value for mapping. value=" + currentValue);
		}
		
		return index;
		
	}

	/**
	 * {@inheritDoc}
	 * <p>
	 * Operation not supported by RTS2 filterd.
	 */
	@Override
	public int fwGetSpeedSwitching() throws RTException {

		throw new UnsupportedOpException ("Operation not supported");
	}

	/**
	 * {@inheritDoc}
	 * <p>
	 * Operation not supported by RTS2 filterd.
	 */
	@Override
	public float fwGetFilterSize() throws RTException {

		throw new UnsupportedOpException ("Operation not supported");
	}

	/**
	 * {@inheritDoc} 
	 * <p>
	 * Recovers "filter" from RTS2 filterd.
	 */
	@Override
	public String fwGetFilterKind() throws RTException {
		
		DeviceProperty property = this.devGetDeviceProperty("filter");
		
		String currentValue = property.getValue().get(0);
		
		int index = this.filtersLocalName.indexOf(currentValue);
		if (index == -1){
			throw new RTException("Unknown filter value for mapping. value=" + currentValue);
		}
		
		return this.filtersGlobalName.get(index);
		
	}

	/**
	 * {@inheritDoc}
	 * <p>
	 * Checks if filter wheel is at first position
	 */
	@Override
	public boolean fwIsAtHome() throws RTException {
		
		return (this.fwGetPositionNumber()==0 ? true : false);
	}

	/**
	 * {@inheritDoc} 
	 * <p>
	 * Gets "wheelx" from RTS2 camd.
	 * Sets "filter_offsets_x" from RTS2 camd.
	 */
	@Override
	public void fwSetOffset(List<Integer> positions) throws RTException {
		
		throw new UnsupportedOpException ("Operation not supported");
		
		/*
		if (positions.size() != this.filtersGlobalName.size()){
			throw new RTException("Invalid positions size. The right size is: " + this.filtersGlobalName.size());
		}
		
		DeviceProperty property = this.devGetDeviceProperty("filter");
		int rts2PositionLenght = property.getPossibleValue().size(); //Real RTS2 filters

		String cam = fwGetCamera();
		
		String propPre = "wheel";
		char propSuf = 'A';
		DeviceProperty prop = ((CameraRTD) DeviceDiscoverer.getRTD(cam)).devGetDeviceProperty(propPre+propSuf);
		while (!prop.getValue().get(0).equals(this.getDeviceId())){
			propSuf++;
			prop =  ((CameraRTD) DeviceDiscoverer.getRTD(cam)).devGetDeviceProperty(propPre+propSuf);
		}
		
		propPre = "filter_offsets_";
		List<String> valueProp = new ArrayList<String>();
		
		for (int rts2count = 0; rts2count < rts2PositionLenght; rts2count++ ){//Iterates over the real rts2 size.
		
			int indexLocalMapping = this.filtersLocalName.indexOf(property.getPossibleValue().get(rts2count));
			
			if ( indexLocalMapping != -1){ //The possible filter kind exists... and it's mapped.
				
				valueProp.add(String.valueOf(positions.get(indexLocalMapping)));
				
			}else{ //The possible filter kind does not exist (it's not mapped) adds default offset --> 0, it does not come in the call.
				
				valueProp.add(String.valueOf(0));
				
			}
		}
		
		long time = Rts2Date.now();
		
		((CameraRTD) DeviceDiscoverer.getRTD(cam)).devUpdateDeviceProperty(propPre+propSuf, valueProp);
		
		//Message recovering
		String message = Rts2Messages.getMessageText (Rts2MessageType.error, cam, time);
		if (message != null)
			throw new RTException(message);	*/
		
	}

	/**
	 * {@inheritDoc} 
	 * <p>
	 * Sets "filter" from RTS2 filterd.
	 */
	@Override
	public void fwSelectFilterKind(String kind) throws RTException {
		
		int index = this.filtersGlobalName.indexOf(kind);
		if (index == -1){
			throw new RTException("Unknown filter value for mapping. value=" + kind);
		}
		
		String localName = this.filtersLocalName.get(index);
		

		List<String> valueProp = new ArrayList<String>();
		valueProp.add(localName);
		
//		String camera = fwGetCamera();
//
//		DeviceProperty prop;
//		String propPre = "wheel";
//		String propName = "wheelA";
//		char propSuf = 'A';
//		prop =  ((CameraRTD) DeviceDiscoverer.getRTD(camera)).devGetDeviceProperty(propName);
//		while (!prop.getValue().get(0).equals(this.getDeviceId())){
//			propName = propPre + propSuf;
//			prop =  ((CameraRTD) DeviceDiscoverer.getRTD(camera)).devGetDeviceProperty(propName);
//			propSuf++;
//		}
		
		String camera = fwGetCamera();

		DeviceProperty prop;
		String propPre = "wheel";
		String propName = "wheelA";
		char propSuf = 'A';
		prop =  ((CameraRTD) DeviceDiscoverer.getRTD(camera)).devGetDeviceProperty(propName);
		while (!prop.getValue().get(0).equals(this.getDeviceId())){
			propSuf++;
			propName = propPre + propSuf;
			prop =  ((CameraRTD) DeviceDiscoverer.getRTD(camera)).devGetDeviceProperty(propName);			
		}
		
		long time = Rts2Date.now();
		
		((CameraRTD) DeviceDiscoverer.getRTD(camera)).devUpdateDeviceProperty("FILT"+propSuf, valueProp);
		
//		if (!this.devUpdateDeviceProperty("filter", valueProp))
//			throw new RTException ("Cannot change filter");
		//Message recovering
		String message = Rts2Messages.getMessageText (Rts2MessageType.error, getDeviceId(), time);
		if (message != null)
			throw new RTException(message);	
	}

	/**
	 * {@inheritDoc} 
	 * <p>
	 * Gets and sets "filter" from RTS2 filterd.
	 */
	@Override
	public void fwSelectFilterPosition(int position) throws RTException {
		
		if (this.filtersGlobalName.size() == 0){
			throw new RTException ("No filter available.");
		}
		
		if (position >= this.filtersGlobalName.size()){
			throw new RTException ("Invalid position.");
		}

		/*List<String> valueProp = new ArrayList<String>();
		valueProp.add(this.filtersLocalName.get(position));
		
		long time = Rts2Date.now();
		
		this.devUpdateDeviceProperty("filter", valueProp);
		//Message recovering
		String message = Rts2Messages.getMessageText (Rts2MessageType.error, getDeviceId(), time);
		if (message != null)
			throw new RTException(message);	
			*/
		
		String camera = fwGetCamera();

		DeviceProperty prop;
		String propPre = "wheel";
		String propName = "wheelA";
		char propSuf = 'A';
		prop =  ((CameraRTD) DeviceDiscoverer.getRTD(camera)).devGetDeviceProperty(propName);
		while (!prop.getValue().get(0).equals(this.getDeviceId())){
			propSuf++;
			propName = propPre + propSuf;
			prop =  ((CameraRTD) DeviceDiscoverer.getRTD(camera)).devGetDeviceProperty(propName);			
		}
		
		List<String> valueProp = new ArrayList<String>();
		valueProp.add(this.filtersLocalName.get(position));
		
		long time = Rts2Date.now();
		
		((CameraRTD) DeviceDiscoverer.getRTD(camera)).devUpdateDeviceProperty("FILT"+propSuf, valueProp);
		//Message recovering
		String message = Rts2Messages.getMessageText (Rts2MessageType.error, getDeviceId(), time);
		if (message != null)
			throw new RTException(message);	
		
		
	}

	/**
	 * {@inheritDoc}
	 * <p>
	 * Selects filter number 0.
	 */
	@Override
	public void fwGoHome() throws RTException {

		this.fwSelectFilterPosition(0);
		
	}

	/**
	 * {@inheritDoc} 
	 * <p>
	 * Search within the "wheelx" properties of all camd devices.
	 */
	@Override
	public String fwGetCamera() throws RTException {
		List <String> removeDevices = new ArrayList<String> ();
		
		Rts2Cmd cmd = Rts2Cmd.getNewCmd(Rts2CmdType.devbytype);
		
		cmd.getParameters().put("t", "3");
		
		try {
			String jsonContent = cmd.execute();
			
			ObjectMapper mapper = new ObjectMapper();
			ArrayList<String> values = (ArrayList<String>) mapper.readValue(jsonContent, Object.class);
			
			//Removing of devices not in xml
			for (String deviceId: values){
				eu.gloria.rt.entity.environment.config.device.Device dev = configDeviceManager.getDevice(deviceId);
				if (dev == null){
					removeDevices.add(deviceId);
				}
			}			
			values.removeAll(removeDevices);
			
			DeviceProperty prop;
			String propPre = "wheel";
			String propName = "wheel";
			char propSuf = 'A';
			
			for (String dev: values){	
				try{
					prop =  ((CameraRTD) DeviceDiscoverer.getRTD(dev)).devGetDeviceProperty(propName);
					while (!prop.getValue().get(0).equals(this.getDeviceId())){
						propSuf++;
						propName = propPre + propSuf;
						prop =  ((CameraRTD) DeviceDiscoverer.getRTD(dev)).devGetDeviceProperty(propName);					
					}					
					return dev;					
				} catch (Exception e) {
					if (!e.getMessage().contains("The property does not exist")){
						throw new RTException(e.getMessage());
					}
				}
			}
			
//			for (String dev: values){				 
//				prop =  ((CameraRTD) DeviceDiscoverer.getRTD(dev)).devGetDeviceProperty(propName);
//				while (!prop.getValue().get(0).equals(this.getDeviceId())){
//					propSuf++;
//					propName = propPre + propSuf;
//					prop =  ((CameraRTD) DeviceDiscoverer.getRTD(dev)).devGetDeviceProperty(propName);					
//				}
//				return dev;
//			}
			
			
			throw new RTException("No camera attached. ");
		
		} catch (Exception e) {
			throw new RTException("No camera attached. " + e.getMessage());
		}
		
		
		
		//¿Comprobar tb photometer?
	}


	@Override
	public List<String> fwGetFilterList() throws RTException {
		
		return filtersGlobalName;
	}
	
	

	

}

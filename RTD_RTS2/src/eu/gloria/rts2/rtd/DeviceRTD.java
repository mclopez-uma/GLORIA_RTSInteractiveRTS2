package eu.gloria.rts2.rtd;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


import eu.gloria.rt.entity.device.ActivityState;
import eu.gloria.rt.entity.device.AlarmState;
import eu.gloria.rt.entity.device.BlockState;
import eu.gloria.rt.entity.device.Device;
import eu.gloria.rt.entity.device.DeviceProperty;
import eu.gloria.rt.entity.device.DevicePropertyBasicType;
import eu.gloria.rt.entity.device.DeviceType;
import eu.gloria.rt.exception.RTException;
import eu.gloria.rt.exception.UnsupportedOpException;
import eu.gloria.rtc.DeviceDiscoverer;
import eu.gloria.rtc.environment.config.device.ConfigDeviceManager;
import eu.gloria.rtd.RTDDeviceInterface;
import eu.gloria.rts2.http.Rts2Constants;
import eu.gloria.rts2.http.Rts2DevicePropertyValidator;
import eu.gloria.rts2.http.Rts2GatewayDeviceManager;
import eu.gloria.rts2.http.Rts2GatewayDevicePropertiesRequest;
import eu.gloria.rts2.http.Rts2IntervalPropertyConverter;
import eu.gloria.rts2.http.Rts2GatewayDevicePropertiesRequest.RequestType;
import eu.gloria.tools.cache.CacheManager;
import eu.gloria.tools.log.LogUtil;


/**
 * Base class for all RTS2 devices.
 * 
 * @author jcabello
 *
 */
public class DeviceRTD implements RTDDeviceInterface, RTDAssociatedDevInterface	 {
	
	
	public static String CACHE_DEVICE_INFO = "CACHE_DEVICE_INFO";
	
	/**
	 * Device configuration list manager.
	 */
	protected static ConfigDeviceManager configDeviceManager;
	
		
	/**
	 * Main method for testing purpose.
	 * @param args arguments
	 */
	public static void main(String[] args) throws Exception{
		
		DeviceRTD device = new DeviceRTD("DAVIS_rhs");
		device.associatedDevice=true;
		device.parentDeviceId="DAVIS";
		
		List<String> valueProp = new ArrayList<String>();
		valueProp.add("90.0");
				
		device.devUpdateDeviceProperty("max_humidity", valueProp);
		//Device dev = disc.getDevice("DOME1B_rnd", false);
		//System.out.print(dev.getType());
		//System.out.print(disc.getDevices(false));
		//System.out.print(disc.getDevice("DAVIS", false));

	}
	
	static{
		
		CacheManager.createCache(CACHE_DEVICE_INFO, -1, new CachedDeviceInfo());
		
		try{
			
			configDeviceManager = new ConfigDeviceManager();
			
		}catch(Exception ex){
			LogUtil.severe(null, "DeviceRTD. static initialization. Error loading the device list XML." + ex.getMessage());
			ex.printStackTrace();
		}
	}
	
	private String deviceId = null;
	private String parentDeviceId = null;
	private boolean associatedDevice;
	
	private RTS2DeviceBlockPolicy blockPolicy;
	
	/**
	 * Constructor
	 * @param deviceId
	 */
	public DeviceRTD(String deviceId){
		this.deviceId = deviceId;
	}
	
	/**
	 * Returns the Device entity
	 * 
	 */
	public Device devGetDevice(boolean allProperties)  throws RTException{
		
		try{
			Rts2GatewayDeviceManager manager = new Rts2GatewayDeviceManager();
			Rts2GatewayDevicePropertiesRequest propReq = null;
			if (allProperties){
				propReq = new Rts2GatewayDevicePropertiesRequest(RequestType.ALL_PROPERTIES, null);
			}else{
				propReq = new Rts2GatewayDevicePropertiesRequest(RequestType.NO_PROPERTY, null);
			}			
			
			Device device =  manager.getDevice(deviceId, propReq);
			resolveBlockDependencies(device);
			return device;

		}catch(Exception e){
			if (e.getMessage().contains("Server returned HTTP response code: 400")){
				
				String cacheKey = deviceId + ".device.type";
				Map<String, Object> cacheParams = new HashMap<String, Object>();
				cacheParams.put("DEV_ID", deviceId);
				DeviceType devType;
				try{
					 devType = (DeviceType) CacheManager.getObject("CACHE_RTS2_DEVICE_TYPE", cacheKey, cacheParams);
				}catch(Exception ex){
					throw new RTException(ex.getMessage());
				}
				
				Rts2GatewayDeviceManager manager = new Rts2GatewayDeviceManager();
				return (manager.getDeviceEntity(deviceId, devType, Rts2Constants.RTS2_DEVICE_FLAG_ERROR_HW));
				
			}	
			throw new RTException(e.getMessage());
		}
		
	}
	
	/**
	 * Returns the device information
	 * 
	 * @param deviceId Device Identifier.
	 * @param propertyNames List of properties to recover.
	 * @return {@link Device} Current device data.
	 * @throws RTException In error case.
	 */	
	public Device getDevice(List<String> propertyNames) throws RTException {
		
		try{
			
			Rts2GatewayDeviceManager manager = new Rts2GatewayDeviceManager();
			Rts2GatewayDevicePropertiesRequest propReq =  new Rts2GatewayDevicePropertiesRequest(RequestType.CUSTOM, propertyNames);
			Device device =  manager.getDevice(deviceId, propReq);
			resolveBlockDependencies(device);
			return device;
			
		}catch(Exception e){
			if (e.getMessage().contains("Server returned HTTP response code: 400")){
				
				String cacheKey = deviceId + ".device.type";
				Map<String, Object> cacheParams = new HashMap<String, Object>();
				cacheParams.put("DEV_ID", deviceId);
				DeviceType devType;
				try{
					 devType = (DeviceType) CacheManager.getObject("CACHE_RTS2_DEVICE_TYPE", cacheKey, cacheParams);
				}catch(Exception ex){
					throw new RTException(ex.getMessage());
				}
				
				Rts2GatewayDeviceManager manager = new Rts2GatewayDeviceManager();
				return (manager.getDeviceEntity(deviceId, devType, Rts2Constants.RTS2_DEVICE_FLAG_ERROR_HW));
				
			}	
			throw new RTException(e.getMessage());
		}
		
	}
	
	/**
	 * Resolves the block dependencies.
	 * @param dev Device data
	 * @throws RTException In error case
	 */
	private void resolveBlockDependencies(Device dev) throws RTException{
		
		if (dev.getBlockState() == BlockState.UNBLOCK){// NON BLOCKED
			RTS2DeviceBlockPolicy blockPolicy = new RTS2DeviceBlockPolicy();
			BlockState dependencyBlockState = blockPolicy.getDependencyBlockState(dev.getShortName());
			if (dependencyBlockState != BlockState.UNBLOCK){
				dev.setBlockState(dependencyBlockState);
			}
		}
		
	}
	
	public String getParentDeviceId() {
		return parentDeviceId;
	}


	public void setParentDeviceId(String parentDeviceId) {
		this.parentDeviceId = parentDeviceId;
	}


	public boolean isAssociatedDevice() {
		return associatedDevice;
	}


	public void setAssociatedDevice(boolean associatedDevice) {
		this.associatedDevice = associatedDevice;
	}


	/**
	 * Access method
	 * @return
	 */
	public String getDeviceId() {
		return deviceId;
	}


	/**
	 * Access method
	 * @return
	 */
	public void setDeviceId(String deviceId) {
		this.deviceId = deviceId;
	}

	
	/**
	 * Constructor
	 */
	public DeviceRTD(){
		
		this.blockPolicy = new RTS2DeviceBlockPolicy();
	}
	
	private boolean existDevice() throws RTException  {
		
		try {
			
			List<String> devNames = DeviceDiscoverer.devGetDeviceIds();
			for (String dev: devNames){
				if (dev.equals(deviceId))
					return true;
			}
			return false;
		} catch (Exception ex) {
			RTException newEx = new RTException(ex);
			throw newEx;
		}
		
	}
	
	/**
	 * Execute a RTS2 command in a synchronous mode. Synchronous 
	 * calls return after value is confirmed by the device driver.
	 * 
	 * @param comand RTS2 command
	 * @param escape
	 * @return True if execution finishes correctly
	 * @throws RTException
	 */
	public boolean devExecuteCmd (String comand, boolean escape) throws RTException {
		String id;
		if (associatedDevice)
			id = getParentDeviceId();
		else
			id = getDeviceId();
		
		try{			
			Rts2GatewayDeviceManager gatewayDevManager = new Rts2GatewayDeviceManager();
			return gatewayDevManager.executeCmd (id,  comand, escape);
		}catch(Exception ex){
			if (ex.getMessage().contains("not authorized to write to the device")){
				UnsupportedOpException newEx = new UnsupportedOpException ("Operation not supported");
				throw newEx;
			}else{			
				RTException newEx = new RTException(ex);
				throw newEx;
			}
		}	
		
	}
	
	/**
	 * Execute a RTS2 command in an asynchronous mode. Asynchronous calls return 
	 * before value is confirmed  by the device driver.
	 * 
	 * @param comand RTS2 command
	 * @param escape
	 * @return True if execution finishes correctly
	 * @throws RTException
	 */
	public boolean devExecuteCmdAsync (String comand, boolean escape) throws RTException {
		String id;
		if (associatedDevice)
			id = getParentDeviceId();
		else
			id = getDeviceId();
		
		try{			
			Rts2GatewayDeviceManager gatewayDevManager = new Rts2GatewayDeviceManager();
			return gatewayDevManager.executeCmd (id,  comand, escape);
		}catch(Exception ex){
			
			if (ex.getMessage().contains("not authorized to write to the device")){
				UnsupportedOpException newEx = new UnsupportedOpException ("Operation not supported");
				throw newEx;
			}else{			
				RTException newEx = new RTException(ex);
				throw newEx;
			}
			
		}	

	}
		

	@Override
	public List<DeviceProperty> devGetDeviceProperties() throws RTException {
		String id;
		if (associatedDevice)
			id = getParentDeviceId();
		else
			id = getDeviceId();
		
		try{
			
			Device dev = DeviceDiscoverer.devGetDevice(id, true);
			if (dev == null) throw new Exception("Impossible to retrieve the device information.");
			return dev.getProperties();		
			
		}catch(Exception ex){
			RTException newEx = new RTException(ex);
			throw newEx;
		}
	}

	@Override
	public boolean devUpdateDeviceProperty(String name,	List<String> value) throws RTException {
		String id;
		if (associatedDevice)
			id = getParentDeviceId();
		else
			id = getDeviceId();
		
		try{
						
			//1) Recover the device information.
			DeviceProperty searchProp = null;
			searchProp = devGetDeviceProperty(name);
			
			//2) Verify if it is writable.
			if (searchProp.isReadonly()) throw new Exception("The property is readonly.");
			
			//3) Validate the values.
			Rts2DevicePropertyValidator validator = new Rts2DevicePropertyValidator(name, searchProp.getBasicType(), searchProp.getComplexType(), searchProp.getPossibleValue(), searchProp.getMinmax(), value);
			validator.validate();
			
			//4) If an interval property...changes values to ordinal values.
			if (searchProp.getBasicType() == DevicePropertyBasicType.SELECTION){
				Rts2IntervalPropertyConverter converter = new Rts2IntervalPropertyConverter(value, searchProp.getPossibleValue(), false);
				converter.transformToOrdinalValues(); //Implicitly...change the values List.
			}
			
			//5) Execute the command.
			Rts2GatewayDeviceManager gatewayDevManager = new Rts2GatewayDeviceManager();
			return (gatewayDevManager.updateDeviceProperty(id, name, value, false));
			
		}catch(Exception ex){
			if (ex.getMessage().contains("not authorized to write to the device")){
				UnsupportedOpException newEx = new UnsupportedOpException ("Operation not supported");
				throw newEx;
			}else{			
				RTException newEx = new RTException(ex);
				throw newEx;
			}
		}

	}

	@Override
	public boolean devUpdateDevicePropertyAsync( String name, List<String> value) throws RTException {
		String id;
		if (associatedDevice)
			id = getParentDeviceId();
		else
			id = getDeviceId();
		
		try{
			
			//1) Recover the device information.
			DeviceProperty searchProp = null;
			searchProp = devGetDeviceProperty(name);
			
			//2) Verify if it is writable.
			if (searchProp.isReadonly()) throw new Exception("The property is readonly.");
			
			//3) Validate the values.
			Rts2DevicePropertyValidator validator = new Rts2DevicePropertyValidator(name, searchProp.getBasicType(), searchProp.getComplexType(), searchProp.getPossibleValue(), searchProp.getMinmax(), value);
			//if (!validator.isValid()) throw new Exception("The property value is not valid.");
			validator.validate();
			
			//4) If an interval property...changes values to ordinal values.
			if (searchProp.getBasicType() == DevicePropertyBasicType.SELECTION){
				Rts2IntervalPropertyConverter converter = new Rts2IntervalPropertyConverter(value, searchProp.getPossibleValue(), false);
				converter.transformToOrdinalValues(); //Implicitly...change the values List.
			}
			
			//5) Execute the command.
			Rts2GatewayDeviceManager gatewayDevManager = new Rts2GatewayDeviceManager();
			return (gatewayDevManager.updateDeviceProperty(id, name, value, true));
			
		}catch(Exception ex){
			if (ex.getMessage().contains("not authorized to write to the device")){
				UnsupportedOpException newEx = new UnsupportedOpException ("Operation not supported");
				throw newEx;
			}else{			
				RTException newEx = new RTException(ex);
				throw newEx;
			}
		}

	}
	@Override
	public boolean devIsConnected() throws RTException {
		try{
			CachedDeviceInfo devInfo = (CachedDeviceInfo)CacheManager.getObject(deviceId, CACHE_DEVICE_INFO, null);
			return devInfo.isConnected();
		}catch(Exception ex){
			RTException newEx = new RTException(ex);
			throw newEx;
		}
	}

	@Override
	public void devDisconnect() throws RTException {
		try{
			CachedDeviceInfo devInfo = (CachedDeviceInfo)CacheManager.getObject(deviceId, CACHE_DEVICE_INFO, null);
			devInfo.setConnected(false);
		}catch(Exception ex){
			RTException newEx = new RTException(ex);
			throw newEx;
		}

	}
	
	@Override
	public void devConnect() throws RTException{
		CachedDeviceInfo devInfo = null;
		try{
			
			devInfo = (CachedDeviceInfo)CacheManager.getObject(deviceId, CACHE_DEVICE_INFO, null);
			
			Device device = DeviceDiscoverer.devGetDevice(deviceId, false);
			if (device.getError().getCod() != 0) {
				throw new Exception("The device is in Error state.");
			}
						
			devInfo.setConnected(true);
			
		}catch(Exception ex){
			devInfo.setConnected(false);
			RTException newEx = new RTException(ex);
			throw newEx;
		}
	}

	@Override
	public String devGetConfiguration() throws RTException {
		
		try{			
			Rts2GatewayDeviceManager gatewayDevManager = new Rts2GatewayDeviceManager();
			Device dev = gatewayDevManager.getDevice(deviceId, null);
			if (dev == null) throw new Exception("Impossible to retrieve the device information.");
			return dev.getConfiguration();
			
		}catch(Exception ex){
			RTException newEx = new RTException(ex);
			throw newEx;
		}
	}

	@Override
	public DeviceProperty devGetDeviceProperty(String name) throws RTException {

		String id;
		if (associatedDevice)
			id = getParentDeviceId();
		else
			id = getDeviceId();
		
		try{
			//1) Recover the device information.
			List<String> propNames = new ArrayList<String>();
			propNames.add(name);
			Device dev = DeviceDiscoverer.devGetDevice(id, propNames);
			if (dev == null){
				throw new Exception("The device does not exist [" +name + "]");
			}
					
			if (dev.getAlarmState()==AlarmState.DRIVER_HW){
				throw new Exception("The device does not exist [" +name + "]. (in AlarmState.DRIVER_HW)");
			}
				

			//2) Recover the specified property
			List<DeviceProperty> props = dev.getProperties();
			DeviceProperty searchProp = null;
			if (props == null) throw new Exception("The property does not exist: " + name);
			for (DeviceProperty deviceProperty : props) {
				if (deviceProperty.getName().equals(name)){
					searchProp = deviceProperty;
					break;
				}
			}

			if (searchProp == null) throw new Exception("The property does not exist: " + name );

			return searchProp;
		}catch(Exception ex){
			throw new RTException (ex);
		}
	}

	/**
	 * {@inheritDoc}
	 * <p>
	 * 
	 */
	@Override
	public boolean devIsBlocked() throws RTException {
		return (DeviceDiscoverer.devGetDevice(this.deviceId, false).getBlockState() != BlockState.UNBLOCK);
	}


	/**
	 * Returns the device identifier list of all the associated devices.
	 * 
	 * @return Device identifier list.
	 */
	@Override
	public List<String> getAssociatedDevices() throws RTException{
		return new ArrayList<String>(); //Empty list
	}


	/**
	 * Return the relation between the parent device and its associated devices.
	 * 
	 * @return {@link DeviceAssociationType}
	 */
	@Override
	public DeviceAssociationType getRelation() {
		
		return DeviceAssociationType.Independent;
	}

	/**
	 * Return the device type of the specified device identifier
	 * 
	 * @param deviceId Device identifier 
	 * @return {@link DeviceType}
	 */
	@Override
	public DeviceType getDeviceType(String deviceId) {
		
		return DeviceType.UNKNOWN;
	}


	@Override
	public String getDeviceSubType(String deviceId) {
		
		return null;
	}

}

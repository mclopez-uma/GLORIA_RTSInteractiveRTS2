package eu.gloria.rts2.http;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;

import eu.gloria.rt.entity.device.ActivityContinueStateCamera;
import eu.gloria.rt.entity.device.Device;
import eu.gloria.rt.entity.device.DeviceCamera;
import eu.gloria.rt.entity.device.DeviceDome;
import eu.gloria.rt.entity.device.DeviceFilter;
import eu.gloria.rt.entity.device.DeviceFocuser;
import eu.gloria.rt.entity.device.DeviceGeneral;
import eu.gloria.rt.entity.device.DeviceMirror;
import eu.gloria.rt.entity.device.DeviceMount;
import eu.gloria.rt.entity.device.DeviceProperty;
import eu.gloria.rt.entity.device.DevicePropertyBasicType;
import eu.gloria.rt.entity.device.DevicePropertyComplexType;
import eu.gloria.rt.entity.device.DeviceRotator;

import eu.gloria.rt.entity.device.DeviceType;
import eu.gloria.rt.exception.RTException;
import eu.gloria.rts2.http.Rts2GatewayDevicePropertiesRequest.RequestType;
import eu.gloria.tools.cache.CacheManager;
import eu.gloria.tools.log.LogUtil;

/**
 * Provides methods related to RTS2 devices.
 * 
 * @author jcabello
 *
 */
public class Rts2GatewayDeviceManager {
	
	private static String CACHE_RTS2_SELVAL = "CACHE_RTS2_SELVAL";
	private static String CACHE_RTS2_DEVICE_TYPE = "CACHE_RTS2_DEVICE_TYPE";
	
	static{
		CacheManager.createCache(CACHE_RTS2_SELVAL, -1, new Rts2GatewaySelValCacheRetriever());
		CacheManager.createCache(CACHE_RTS2_DEVICE_TYPE, -1, new Rts2GatewayDeviceTypeCacheRetriever());
	}
	
	/***
	 * Returns the Devices data list.
	 * @param allProperties true-> retrieves all properties.
	 * @return Data
	 * @throws Rts2CommunicationException In error case
	 */
	public List<Device> getDevices(boolean allProperties) throws Rts2CommunicationException, Rts2Exception{
		
		LogUtil.info(this, ">>>>>>>>>>>>>>Recovering the devices names. BEGIN");
		ArrayList<String> devNames = getDevicesNames();
		LogUtil.info(this, ">>>>>>>>>>>>>>Recovering the devices names. END");
		
		Rts2GatewayDevicePropertiesRequest propReq = null;
		if (allProperties){
			propReq = new Rts2GatewayDevicePropertiesRequest(RequestType.ALL_PROPERTIES, null);
		}else{
			propReq = new Rts2GatewayDevicePropertiesRequest(RequestType.NO_PROPERTY, null);
		}
		
		List<Rts2GatewayDevicePropertiesRequest> propReqs = new ArrayList<Rts2GatewayDevicePropertiesRequest>();
		for (int x = 0; x < devNames.size(); x++){
			propReqs.add(propReq);
		}
		
		
		return getDevices(devNames, propReqs);
	}
	
	/**
	 * Returns the Devices data list.
	 * @param deviceName Device Identifier
	 * @param propReq List of properties to return .
	 * @return Data
	 * @throws Rts2CommunicationException In error case.
	 * @throws Rts2Exception In error case
	 */
	public Device getDevice(String deviceName, Rts2GatewayDevicePropertiesRequest propReq) throws Rts2CommunicationException, Rts2Exception{
		
		ArrayList<String> devName = new ArrayList<String>();
		devName.add(deviceName);
		List<Rts2GatewayDevicePropertiesRequest> propertiesList = new ArrayList<Rts2GatewayDevicePropertiesRequest>();
		propertiesList.add(propReq);
		List<Device> result = getDevices(devName, propertiesList);
		if (result != null && result.size() > 0){
			return result.get(0);
		}else{
			throw new Rts2Exception("Impossible to retrieve the information for the device: " + deviceName);
		}

	}
	
	/**
	 * Returns the device names list.
	 * 
	 * @return names list.
	 * @throws Rts2CommunicationException In error case
	 */
	public ArrayList<String> getDevicesNames() throws Rts2CommunicationException{
		
		try{
		
			Rts2Cmd cmd = Rts2Cmd.getNewCmd(Rts2CmdType.devices);
		
			String jsonContent = cmd.execute();
		
			ObjectMapper mapper = new ObjectMapper();
			ArrayList<String> list = (ArrayList<String>) mapper.readValue(jsonContent, Object.class);
		
			//ArrayList<String> list = (ArrayList<String>) JsonObj.fromJson(jsonContent);
		
			return list;
			
		}catch(Exception ex){
			throw new Rts2CommunicationException("Error recovering decives list. " + ex.getMessage());
		}
		
	}
	
	/**
	 * Returns the device names list.
	 * 
	 * @param type Device type
	 * @return names list of the device type.
	 * @throws Rts2CommunicationException In error case
	 */
	public ArrayList<String> getDevicesNames (DeviceType type) throws Rts2CommunicationException{
		
		try{
		
			Rts2Cmd cmd = Rts2Cmd.getNewCmd(Rts2CmdType.devbytype);
			cmd.getParameters().put("t", String.valueOf(type.ordinal()));
		
			String jsonContent = cmd.execute();
		
			ObjectMapper mapper = new ObjectMapper();
			ArrayList<String> list = (ArrayList<String>) mapper.readValue(jsonContent, Object.class);
		
			return list;
			
		}catch(Exception ex){
			throw new Rts2CommunicationException("Error recovering decives list. " + ex.getMessage());
		}
		
	}
	
	/**
	 * Updates a device property
	 * @param devId Device identifier
	 * @param propName Property name
	 * @param value Values
	 * @throws Rts2CommunicationException In error case
	 */
	public boolean updateDeviceProperty(String devId, String propName, List<String> value, boolean bool) throws Rts2CommunicationException{
		
		try{
			
			Rts2Cmd cmd = Rts2Cmd.getNewCmd(Rts2CmdType.set);
			cmd.getParameters().put("d", devId);
			cmd.getParameters().put("n", propName);
			StringBuilder sb = new StringBuilder();
			for (int x = 0; x <  value.size(); x++){
				if (x > 0) sb.append(" ");
				sb.append(value.get(x));
			}
			
			cmd.getParameters().put("v", sb.toString());
			
			
			cmd.getParameters().put("async", bool?"1":"0");
		
			String jsonContent = cmd.execute();
			return (retControl (jsonContent));
		
			
		}catch(Exception ex){
			throw new Rts2CommunicationException("Error updating a device property. " + ex.getMessage());
		}
		
	}
	
	/**
	 * Returns the information of the devices names in the input parameter.
	 * @param devNames List of devices identifiers.
	 * @param propertiesLists List of properties list.
	 * @return List of Devices data.
	 * @throws Rts2CommunicationException In error case
	 * @throws Rts2Exception In error case.
	 */
	private List<Device> getDevices(List<String> devNames, List<Rts2GatewayDevicePropertiesRequest> propReqs) throws Rts2CommunicationException, Rts2Exception{
		
		//Auxiliar objects.
		Map<String, Object> cacheParams = new HashMap<String, Object>();
		String cacheKey = null;
	
		ArrayList<Device> devList = new ArrayList<Device>();
		
		if (devNames != null){
			
			int countBucle = 0;
			for (String devName : devNames) {
				
				if (devName != null && !devName.isEmpty()){					
					
					
					cacheKey = devName + ".device.type";
					cacheParams.clear();
					cacheParams.put("DEV_ID", devName);
					DeviceType devType;
					try{
						 devType = (DeviceType) CacheManager.getObject(CACHE_RTS2_DEVICE_TYPE, cacheKey, cacheParams);
					}catch(Exception ex){
						throw new Rts2Exception("Error recovering the device type. " + ex.getMessage());
					}
					
					//2) Recovers the RTS2 info (properties)
					LogUtil.info(this, ">>>>>>>>>>>>>>Recovering RTS2 device DATA. BEGIN");
					Rts2Cmd cmd = Rts2Cmd.getNewCmd(Rts2CmdType.get);
					cmd.getParameters().put("d", devName);
					cmd.getParameters().put("e", "1"); //No extended format
				
					String jsonContent = null;
					Device dev = null;
					try{
						jsonContent = cmd.execute();
						LogUtil.info(this, ">>>>>>>>>>>>>>Recovering RTS2 device DATA. END");
					}catch(Exception e){
						if (e.getMessage().contains("Server returned HTTP response code: 400")){
							
//							String cacheKey = deviceId + ".device.type";
//							Map<String, Object> cacheParams = new HashMap<String, Object>();
							cacheParams.put("DEV_ID", devName);
//							DeviceType devType;
							try{
								 devType = (DeviceType) CacheManager.getObject("CACHE_RTS2_DEVICE_TYPE", cacheKey, cacheParams);
							}catch(Exception ex){
								throw new Rts2Exception(ex.getMessage());
							}
							
							Rts2GatewayDeviceManager manager = new Rts2GatewayDeviceManager();
							dev = (manager.getDeviceEntity(devName, devType, Rts2Constants.RTS2_DEVICE_FLAG_ERROR_HW));
							
						}	
//						throw new Rts2Exception(e.getMessage());
					}
					
					if (jsonContent!=null){

						Rts2GatewayDevicePropertiesRequest propReq = null;
						if (propReqs != null){
							propReq = propReqs.get(countBucle);
						}

						Rts2CmdGetResponse resp = new Rts2CmdGetResponse(jsonContent, propReq);

						//3 iterates the properties
						List<DeviceProperty> properties = new ArrayList<DeviceProperty>();
						List<JsonVariable> jsonVars = resp.getVariables(JsonVariable.PATH_SEPARATOR + "d");

						//4 Build the device entity instance
						dev = getDeviceEntity(devName, devType, resp.getState());

						for (JsonVariable jsonVariable : jsonVars) {
							DeviceProperty devProperty = new DeviceProperty();

							// Name & values
							devProperty.setName(jsonVariable.getName());
							devProperty.getValue().addAll(jsonVariable.getValue());
							devProperty.setDescription(jsonVariable.getDesc());
							//devProperty.setValue(jsonVariable.getValue().toArray(new String[jsonVariable.getValue().size()]));

							// Calculate - Basic an Complex types
							Rts2FlagsDeviceProperty flagsProp = new Rts2FlagsDeviceProperty(jsonVariable.getFlags());
							DevicePropertyBasicType basicType = flagsProp.getBasicType();
							DevicePropertyComplexType complexType = flagsProp.getComplexType();
							devProperty.setBasicType(basicType);
							devProperty.setComplexType(complexType);

							// Calculate - possible values (cmd selval con mask)
							if (devProperty.getBasicType().equals(DevicePropertyBasicType.SELECTION)){

								try{
									cacheKey = devName + ".selval." + devProperty.getName();
									cacheParams.clear();
									cacheParams.put("DEV_ID", devName);
									cacheParams.put("DEV_PROPERTY_NAME", devProperty.getName());
									List<String> values = (List<String>)CacheManager.getObject(CACHE_RTS2_SELVAL, cacheKey, cacheParams);

									//In BART.CWF1 there a "filter" property with no values and useless
									if (values.isEmpty())
										break;

									devProperty.getPossibleValue().addAll(values);
								}catch(Exception ex){
									throw new Rts2Exception("Error recovering the possible values for a property. " + ex.getMessage());
								}

								//Translates the values to the enumerate values (The values from RTS2 is a number: 0...N)
								Rts2IntervalPropertyConverter converter = new Rts2IntervalPropertyConverter(devProperty.getValue(), devProperty.getPossibleValue(), true);
								converter.transformToEnumValues(); //Implicitly...change the values List.

							}


							// Calculate - read/write
							devProperty.setReadonly(!flagsProp.isValueWritable());

							// Mandatory = false;
							devProperty.setMandatory(false);
							// DefaultValue = null;
							devProperty.setDefaultValue(null);

							//minmax
							JsonGenericVariable minmax =  resp.getMinmax(jsonVariable.getName());
							if (minmax != null){

								//String[] minMaxString = new String[minmax.getValue().size()];
								for (int x = 0; x < minmax.getValue().size(); x++ ){
									//minMaxString[x] = minmax.getValue().get(x).toString();
									if (minmax.getValue().get(x) != null){
										devProperty.getMinmax().add(minmax.getValue().get(x).toString());
									}else{
										devProperty.getMinmax().add(null);
									}
								}
								//devProperty.setMinmax(minMaxString);
							}

							properties.add(devProperty);

						}

						//5) Assign the properties.
						//dev.setProperties(properties.toArray(new DeviceProperty[properties.size()]));
						dev.getProperties().addAll(properties);
						dev.setConfiguration(getDeviceConfigurationString(properties));

						//10) Add the new device
					}
					devList.add(dev);
				}
			
				countBucle++;
			}
		}
		
			
		Device[] result = new Device[devList.size()];
		return devList;
		
	}
	
	/**
	 * Builds a DeviceEntity
	 * @param devType Device Type
	 * @param stateFlags State flags
	 * @return Device Entity object
	 * @throws Rts2Exception In error case
	 */
	public Device getDeviceEntity(String devName, DeviceType devType, long stateFlags) throws Rts2Exception{
		
		Device result = null;
		Rts2FlagsDevice flagsDev = null;
		
		if (devType == DeviceType.CCD){
			
			DeviceCamera devCamera = new DeviceCamera();
			Rts2FlagsDeviceCCD flags = new Rts2FlagsDeviceCCD(stateFlags);
			devCamera.setBlockState(flags.getBlockState());
			devCamera.setAlarmState(flags.getAlarmState());
			devCamera.setActivityState(flags.getActivityState());
			devCamera.setCommunicationState(flags.getCommunicationState());
			devCamera.setActivityStateDesc(flags.getStateDesc());
			devCamera.setHasImage(flags.hasImage());
			
			devCamera.setActivityContinueState(ActivityContinueStateCamera.OFF); //This state doesn't exist in RTS2
			
			result = devCamera;
			flagsDev = flags;
			
			
		} else if ((devType == DeviceType.DOME) | (devType == DeviceType.CUPOLA)){
			
			DeviceDome devDome = new DeviceDome();
			Rts2FlagsDeviceDome flags = new Rts2FlagsDeviceDome(stateFlags);
			devDome.setBlockState(flags.getBlockState());
			devDome.setAlarmState(flags.getAlarmState());
			devDome.setActivityState(flags.getActivityState());
			devDome.setCommunicationState(flags.getCommunicationState());
			devDome.setActivityStateDesc(flags.getStateDesc());
			devDome.setActivityStateOpening(flags.getActivityStateOpening());
			devDome.setSynch(flags.isSynchronized());
			
			result = devDome;
			flagsDev = flags;			
		
		} else if (devType == DeviceType.FW){
			
			DeviceFilter devFilter = new DeviceFilter();
			Rts2FlagsDeviceFilter flags = new Rts2FlagsDeviceFilter(stateFlags);
			devFilter.setBlockState(flags.getBlockState());
			devFilter.setAlarmState(flags.getAlarmState());
			devFilter.setActivityState(flags.getActivityState());
			devFilter.setCommunicationState(flags.getCommunicationState());
			devFilter.setActivityStateDesc(flags.getStateDesc());
						
			result = devFilter;
			flagsDev = flags;
			
		} else if (devType == DeviceType.FOCUS){
			
			DeviceFocuser devFocuser = new DeviceFocuser();
			Rts2FlagsDeviceFocuser flags = new Rts2FlagsDeviceFocuser(stateFlags);
			devFocuser.setBlockState(flags.getBlockState());
			devFocuser.setAlarmState(flags.getAlarmState());
			devFocuser.setActivityState(flags.getActivityState());
			devFocuser.setCommunicationState(flags.getCommunicationState());
			devFocuser.setActivityStateDesc(flags.getStateDesc());
						
			result = devFocuser;
			flagsDev = flags;
			
			
		} else if (devType == DeviceType.MIRROR){
			
			DeviceMirror devMirror = new DeviceMirror();
			Rts2FlagsDeviceMirror flags = new Rts2FlagsDeviceMirror(stateFlags);
			devMirror.setBlockState(flags.getBlockState());
			devMirror.setAlarmState(flags.getAlarmState());
			devMirror.setActivityState(flags.getActivityState());
			devMirror.setCommunicationState(flags.getCommunicationState());
			devMirror.setActivityStateDesc(flags.getStateDesc());
						
			result = devMirror;
			flagsDev = flags;
			
		} else if (devType == DeviceType.MOUNT){
			
			DeviceMount devMount = new DeviceMount();
			Rts2FlagsDeviceMount flags = new Rts2FlagsDeviceMount(stateFlags);
			devMount.setBlockState(flags.getBlockState());
			devMount.setAlarmState(flags.getAlarmState());
			devMount.setActivityState(flags.getActivityState());
			devMount.setCommunicationState(flags.getCommunicationState());
			devMount.setActivityStateDesc(flags.getStateDesc());
						
			result = devMount;
			flagsDev = flags;
		} else if (devType == DeviceType.ROTATOR){
			
			DeviceRotator devRotator = new DeviceRotator();
			Rts2FlagsDeviceRotator flags = new Rts2FlagsDeviceRotator(stateFlags);
			devRotator.setBlockState(flags.getBlockState());
			devRotator.setAlarmState(flags.getAlarmState());
			devRotator.setActivityState(flags.getActivityState());
			devRotator.setCommunicationState(flags.getCommunicationState());
			devRotator.setActivityStateDesc(flags.getStateDesc());
						
			result = devRotator;
			flagsDev = flags;	
		} else { //General
			
			DeviceGeneral devGeneral = new DeviceGeneral();
			Rts2FlagsDeviceGeneral flags = new Rts2FlagsDeviceGeneral(stateFlags);
			devGeneral.setBlockState(flags.getBlockState());
			devGeneral.setAlarmState(flags.getAlarmState());
			devGeneral.setActivityState(flags.getActivityState());
			devGeneral.setCommunicationState(flags.getCommunicationState());
			devGeneral.setActivityStateDesc(flags.getStateDesc());
			
			result = devGeneral;
			flagsDev = flags;
			
		}
		
		//sets the type
		result.setType(devType);
		
		//Error
		result.setError(flagsDev.getError());
		
		//Description
		result.setDescription("RTS2-unavailable");
		
		//Info
		result.setInfo("RTS2-unavailable");
		
		//ShortName
		result.setShortName(devName);
		
		//Version
		result.setVersion("RTS2-unavailable");
		
		return result;
	}
	
	
	/**
	 * Builds a String containing the device configuration
	 * @param properties Properties
	 * @return String
	 */
	private String getDeviceConfigurationString(List<DeviceProperty> properties){
		
		StringBuilder sb = new StringBuilder();
		for (DeviceProperty deviceProperty : properties) {
			sb.append("[");
			sb.append(deviceProperty.getName()).append("=");
			List<String> values = deviceProperty.getValue();
			if (values != null){
				for (int x = 0; x < values.size(); x++){
					if (x > 0) sb.append(",");
					sb.append(values.get(x));
				}
			}
			sb.append("]");
		}
		return sb.toString();
		
	}
	
	/**
	 * Execute a command to the specified device. 
	 * @param deviceId Device identifier
	 * @param comand Command to be executed
	 * @throws Rts2CommunicationException
	 * @return boolean 
	 */
	public boolean executeCmd (String deviceId, String comand, boolean escape) throws Rts2CommunicationException{
		try{
						
			Rts2Cmd cmd = Rts2Cmd.getNewCmd(Rts2CmdType.cmd);			
			
			cmd.setEscape(escape);
			
			cmd.getParameters().put("c", comand);
			cmd.getParameters().put("d", deviceId);
			String jsonContent = cmd.execute();
			
			return (retControl (jsonContent));	 		
			
		}catch(Exception ex){
			throw new Rts2CommunicationException("Error executing a comand. " + ex.getMessage());
		}
	}
	
	/**
	 * Execute a command to the specified device asynchornously. 
	 * @param deviceId Device identifier
	 * @param comand Command to be executed
	 * @throws Rts2CommunicationException
	 * @return boolean 
	 */
	public boolean executeCmdAsync (String deviceId, String comand, boolean escape) throws Rts2CommunicationException{
		try{
						
			Rts2Cmd cmd = Rts2Cmd.getNewCmd(Rts2CmdType.cmd);			
			
			cmd.setEscape(escape);
			
			cmd.getParameters().put("c", comand);
			cmd.getParameters().put("d", deviceId);
			cmd.getParameters().put("async", "1");
			String jsonContent = cmd.execute();
			
			return (retControl (jsonContent));	 		
			
		}catch(Exception ex){
			throw new Rts2CommunicationException("Error executing a comand. " + ex.getMessage());
		}
	}
	
	/**
	 * Check return status 
	 * 
	 * @param jsonContent
	 * @return boolean
	 * @throws IOException 
	 * @throws JsonMappingException 
	 * @throws JsonParseException 
	 */
	public boolean retControl (String jsonContent) throws JsonParseException, JsonMappingException, IOException{
		
		ObjectMapper mapper = new ObjectMapper();
		HashMap<String, Object> info = (HashMap<String, Object>) mapper.readValue(jsonContent, Object.class);
		Map<String, Object> map = (Map<String, Object>) info;
		if (map.get("ret").toString().equals("0")) 
			return true;
		else 
			return false;

	}
	

}

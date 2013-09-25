package eu.gloria.rts2.rtd;

import java.io.IOException;
import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.HierarchicalINIConfiguration;
import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;

import eu.gloria.rt.entity.device.BlockState;
import eu.gloria.rt.entity.device.Device;
import eu.gloria.rt.entity.device.DeviceProperty;
import eu.gloria.rt.entity.device.DeviceType;
import eu.gloria.rt.exception.RTException;
import eu.gloria.rtc.DeviceDiscovererInterface;
import eu.gloria.rtd.RTDDeviceInterface;
import eu.gloria.rts2.http.Rts2Cmd;
import eu.gloria.rts2.http.Rts2CmdType;
import eu.gloria.rts2.http.Rts2CommunicationException;
import eu.gloria.rts2.http.Rts2Constants;
import eu.gloria.rts2.http.Rts2Exception;
import eu.gloria.rts2.http.Rts2GatewayDeviceManager;
import eu.gloria.rts2.http.Rts2GatewayDevicePropertiesRequest;
import eu.gloria.rts2.http.Rts2GatewayDevicePropertiesRequest.RequestType;
import eu.gloria.tools.cache.CacheManager;

/**
 * Devices discoverer implementation for RTS2.
 * 
 * @author jcabello
 *
 */
public class RTS2DeviceDiscovery implements DeviceDiscovererInterface {
	
	/**
	 * Cache name to caching RTDs.
	 */
	private static final String CACHE_NAME_RTDS = "CACHE_NAME_RTDS";
	
	static{
		CacheManager.createCache(CACHE_NAME_RTDS, -1, null);
	}
	
	/**
	 * Cached list device ids.
	 */
	private List <String> deviceIds = null;
	

	/**
	 * Main method for testing purpose.
	 * @param args arguments
	 */
	public static void main(String[] args) throws Exception{
		
		RTS2DeviceDiscovery disc = new RTS2DeviceDiscovery();
		
				
//		System.out.print(disc.getDeviceIds());
		System.out.print(disc.getDevice("C1",false));

	}
	
	/**
	 * Returns the full devices list.
	 * 
	 * @param allProperties true->retrieves all properties.
	 * @return {@link Device} Current devices list.
	 * @throws RTException In error case.
	 */
	@Override
	public List<Device> getDevices(boolean allProperties) throws RTException {
		
		List<Device> result = new ArrayList <Device>();
		
		try{
			
			if (this.deviceIds == null){ //No cached.
				this.deviceIds = getDeviceIds();
			}
			
			for (String deviceId: deviceIds){
				
				result.add(((DeviceRTD) getRTD(deviceId)).devGetDevice(allProperties));
				
			}
			
			return result;
			
		}catch(Exception ex){
			throw new RTException(ex.getMessage());
		}
		
	}

	/**
	 * Returns the device information
	 * 
	 * @param deviceId Device Identifier.
	 * @param allProperties true->with all properties, false->no property
	 * @return Current device data.
	 * @throws RTException In error case.
	 */
	@Override
	public Device getDevice(String deviceId, boolean allProperties) throws RTException {
		
		try{
			DeviceRTD dev =  (DeviceRTD) getRTD(deviceId);
			return dev.devGetDevice(allProperties);
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
	@Override
	public Device getDevice(String deviceId, List<String> propertyNames) throws RTException {
		
		try{
			DeviceRTD dev =  (DeviceRTD) getRTD(deviceId);
			return dev.getDevice(propertyNames);
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
	 * Recovers the proper RTDDevice Object based on the device id.
	 * 
	 * @param deviceId Device identifier
	 * @return RTDDevice RTD Device.
	 * @throws RTException In error case.
	 */	
	@Override
	public synchronized RTDDeviceInterface getRTD(String deviceId) throws RTException {
		
		DeviceRTD genericRTD = null;
		boolean associatedDev = false;
		String[] parentDevName = null;
		String jsonContent = null;
		Device device = null;
		String className = null;
		int type = 0;
		
		try{
			
			RTDDeviceInterface cachedRtd = (RTDDeviceInterface)CacheManager.getCache(CACHE_NAME_RTDS).getObject(deviceId, null);
			if (cachedRtd != null){
				return cachedRtd;
			}
		
			//Recover the device information
			Rts2GatewayDeviceManager manager = new Rts2GatewayDeviceManager();
			
			List<String> propertyNames = new ArrayList<String> ();
			
			try{				
				type = getDeviceType(deviceId);
			}catch (Exception e){
				if (e.getMessage().contains("Server returned HTTP response code: 400")){	//Possible associated device
					associatedDev = true;
					
					parentDevName = deviceId.split("_");
					type = getDeviceType(parentDevName[0]);					
					
				}
			}			
			
			switch (type){
				case 3:{
					propertyNames.add("CCD_TYPE");
				}break;
				case 10:{
					propertyNames.add("FOC_TYPE");
				}break;
			}
			Rts2GatewayDevicePropertiesRequest propReq =  new Rts2GatewayDevicePropertiesRequest(RequestType.CUSTOM, propertyNames);
			
			if (associatedDev)
				device = manager.getDevice(parentDevName[0], propReq);
			else
				device = manager.getDevice(deviceId, propReq);
			
			//resolveBlockDependencies(device);	
			
			
			List <DeviceProperty> properties = device.getProperties();
			
			HierarchicalINIConfiguration iniConfObj = null;

			try {
				iniConfObj = new HierarchicalINIConfiguration("rtd_rts2_discovery.ini");
			} catch (ConfigurationException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			if ((propertyNames.size() != 0) &&  (properties.get(0).getValue().get(0).isEmpty()))
				properties.get(0).getValue().set(0, " ");

			if (propertyNames.size() != 0)
				className = (String) iniConfObj.getProperty(device.getType().toString()+"."+properties.get(0).getValue().get(0));
			else	//Provisional, until complete solution of type variables
				className = (String) iniConfObj.getProperty(device.getType().toString()+".Dummy");

			//Build the instance.
			try{
				Class<?> cls = Class.forName(className);
				Constructor<?> ct = cls.getConstructor();			
				genericRTD = (DeviceRTD) ct.newInstance();

				if (associatedDev)
					genericRTD.setDeviceId(parentDevName[0]);
				else
					genericRTD.setDeviceId(deviceId);
				
				if (associatedDev){
					List<String> deviceNames = genericRTD.getAssociatedDevices();
					if (deviceNames.contains(deviceId)){	//Correct device parent
						className = (String) iniConfObj.getProperty(genericRTD.getDeviceType(deviceId)+"."+genericRTD.getDeviceSubType(deviceId));
						
						cls = Class.forName(className);
						ct = cls.getConstructor();			
						genericRTD = (DeviceRTD) ct.newInstance();

						genericRTD.setDeviceId(deviceId);
						genericRTD.setParentDeviceId(parentDevName[0]);						
					}
				}
				
				genericRTD.setAssociatedDevice(associatedDev);
				
			}catch (Exception e){				
				throw new RTException("Error reflexing the RTD class"+ e.getCause().getMessage());
			}
			
			CacheManager.getCache(CACHE_NAME_RTDS).putObject(deviceId, genericRTD);
			
		}catch(Exception ex){
			throw new RTException(ex.getMessage());
		}		
		
		
		return genericRTD;
	}
	
	
	
//	/**
//	 * Resolves the block dependencies.
//	 * @param dev Device data
//	 * @throws RTException In error case
//	 */
//	private void resolveBlockDependencies(Device dev) throws RTException{
//		
//		if (dev.getBlockState() == BlockState.UNBLOCK){// NON BLOCKED
//			RTS2DeviceBlockPolicy blockPolicy = new RTS2DeviceBlockPolicy();
//			BlockState dependencyBlockState = blockPolicy.getDependencyBlockState(dev.getShortName());
//			if (dependencyBlockState != BlockState.UNBLOCK){
//				dev.setBlockState(dependencyBlockState);
//			}
//		}
//		
//	}

	@Override
	public synchronized List<String> getDeviceIds() throws RTException {
			
		try {
			
			if (this.deviceIds == null){ //No cached.
				
				Rts2GatewayDeviceManager gatewayDevManager = new Rts2GatewayDeviceManager();	
				this.deviceIds = gatewayDevManager.getDevicesNames();
				
			
				this.deviceIds =  resolveDevices(this.deviceIds);
			}
			
			return this.deviceIds;
			
		} catch (Rts2CommunicationException e) {
			throw new RTException(e);
		}
	}

	/**
	 * Create the identification device list for GLORIA system. 
	 * 
	 * @param names RTS2 Device/services list
	 * @return List<String> Proper device list
	 * @throws RTException In error case.
	 */
	private List <String> resolveDevices (List<String> names) throws RTException{
		
		List <String> removeDevices = new ArrayList<String> ();
		List <String> addDevices = new ArrayList<String> ();
		
		Logger.getLogger(this.getClass().getName()).info("Resolving associated devices. BEGIN");

		DeviceRTD genericRTD = null;

		Rts2GatewayDeviceManager manager = new Rts2GatewayDeviceManager();

		HierarchicalINIConfiguration iniConfObj = null;

		try {
			iniConfObj = new HierarchicalINIConfiguration("rtd_rts2_discovery.ini");
		} catch (ConfigurationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}


		for (String deviceId: names){
			if (!deviceId.isEmpty()){
				
				genericRTD = (DeviceRTD) CacheManager.getCache(CACHE_NAME_RTDS).getObject(deviceId, null);

				List<String> propertyNames = new ArrayList<String> ();

				int type = getDeviceType(deviceId);
				if (isProperDevice(type)){
					if (genericRTD == null){						
						switch (type){
						case 3:{
							propertyNames.add("CCD_TYPE");
						}break;
						case 10:{
							propertyNames.add("FOC_TYPE");
						}break;
						}
						Rts2GatewayDevicePropertiesRequest propReq =  new Rts2GatewayDevicePropertiesRequest(RequestType.CUSTOM, propertyNames);
						Device device = manager.getDevice(deviceId, propReq);					

						List <DeviceProperty> properties = device.getProperties();

												
						String className;

						if (propertyNames.size() != 0)
							className = (String) iniConfObj.getProperty(device.getType().toString()+"."+properties.get(0).getValue().get(0));
						else	//Provisional, until complete solution of type variables
							className = (String) iniConfObj.getProperty(device.getType().toString()+".Dummy");

						if (className.isEmpty()){
							removeDevices.add(deviceId);
							continue;
						}
						
						//Build the instance.
						try{
							Class<?> cls = Class.forName(className);
							Constructor<?> ct = cls.getConstructor();			
							genericRTD = (DeviceRTD) ct.newInstance();
							genericRTD.setDeviceId(deviceId);
							
							CacheManager.getCache(CACHE_NAME_RTDS).putObject(deviceId, genericRTD);
						}catch (Exception e){
							throw new RTException("Error reflexing the RTD class"+e.getMessage());
						}
					}
					List<String> deviceNames = genericRTD.getAssociatedDevices();
					if (deviceNames.size() != 0){
						switch (genericRTD.getRelation()){
						case Composition:{
							removeDevices.add(deviceId);
							addDevices.addAll(deviceNames);
						}break;
						case Independent:{
							addDevices.addAll(deviceNames);
						}break;
						}
					}	
				}else{
					removeDevices.add(deviceId);
				}
			}else{
				removeDevices.add(deviceId);
			}
		}
		names.removeAll(removeDevices);
		names.addAll(addDevices);
		
		Logger.getLogger(this.getClass().getName()).info("Resolving associated devices. END");
		return names;
	}
	
	/**
	 * Checks if the identification is of a proper device.
	 * 
	 * @param deviceType Device type
	 * @return True if a proper device is found.
	 * @throws RTException In error case.
	 */
	private boolean isProperDevice (int deviceType) throws RTException{
						
		if ((deviceType > 1) && (deviceType < 8)){
			return true;
		}else if ((deviceType == 10) || (deviceType == 15)){
			return true;
		}else if ((deviceType > 11) && (deviceType < 14)){
			return true;
		}else{
			return false;
		}
	}
	
	
	/**
	 * Retrieve the device type
	 * 
	 * @param deviceId Device identification
	 * @return device type.
	 * @throws RTException In error case.
	 */
	private int getDeviceType (String deviceId) throws RTException{
		
		Rts2Cmd cmd = Rts2Cmd.getNewCmd(Rts2CmdType.deviceinfo);
		cmd.getParameters().put("d", deviceId);

		String jsonContent;
		try {
			jsonContent = cmd.execute();
		} catch (Rts2CommunicationException e) {
			throw new RTException(e);
		}

		ObjectMapper mapper = new ObjectMapper();
		HashMap<String, Object> info = null;
		try {
			info = (HashMap<String, Object>) mapper.readValue(jsonContent, Object.class);
		} catch (Exception e1) {
			e1.printStackTrace();
		}
		
		return ((Integer) info.get("type"));
	}

}

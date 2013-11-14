package eu.gloria.rts2.rtd;

import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.logging.Logger;

import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.HierarchicalINIConfiguration;
import org.codehaus.jackson.map.ObjectMapper;

import eu.gloria.rt.entity.device.BlockState;
import eu.gloria.rt.entity.device.Device;
import eu.gloria.rt.exception.RTException;
import eu.gloria.rtc.DeviceDiscovererInterface;
import eu.gloria.rtc.environment.config.device.ConfigDeviceManager;
import eu.gloria.rtd.RTDDeviceInterface;
import eu.gloria.rts2.http.Rts2Cmd;
import eu.gloria.rts2.http.Rts2CmdType;
import eu.gloria.rts2.http.Rts2CommunicationException;
import eu.gloria.tools.cache.CacheManager;
import eu.gloria.tools.log.LogUtil;

/**
 * Generic Device Discoverer based on Xml configuration.
 * 
 * @author jcabello
 *
 */
public class DeviceDiscoverer implements DeviceDiscovererInterface {
	
	/**
	 * Cache name to caching RTDs.
	 */
	private static final String CACHE_NAME_RTDS = "CACHE_NAME_RTDS";
	
	/**
	 * Device configuration list manager.
	 */
	private static ConfigDeviceManager configDeviceManager;
	
	/**
	 * Cached list device ids.
	 */
	private List <String> deviceIds = null;
	
	/**
	 * Static initialization.
	 */
	static{
		
		CacheManager.createCache(CACHE_NAME_RTDS, -1, null);
		
		try{
			
			configDeviceManager = new ConfigDeviceManager();
			
		}catch(Exception ex){
			LogUtil.severe(null, "DeviceDiscoverer. static initialization. Error loadin the device list XML." + ex.getMessage());
			ex.printStackTrace();
		}
	}
	
	/**
	 * Main method for testing purpose.
	 * @param args arguments
	 */
	public static void main(String[] args) throws Exception{
		
		DeviceDiscoverer disc = new DeviceDiscoverer();
		
				
//		System.out.print(disc.getDevices(false));
		Device dev = disc.getDevice("DOME1A", false);
		System.out.print(dev.getType());
		//System.out.print(disc.getDevices(false));
		//System.out.print(disc.getDevice("DAVIS", false));

	}

	/**
	 * {@inheritDoc} 
	 *
	 */
	@Override
	public List<String> getDeviceIds() throws RTException {
		List<String> result = new ArrayList<String>();
		if (DeviceDiscoverer.configDeviceManager.getDeviceList() != null && configDeviceManager.getDeviceList().getDevice() != null){
			
			for (eu.gloria.rt.entity.environment.config.device.Device dev : configDeviceManager.getDeviceList().getDevice()) {
				result.add(dev.getShortName());
			}
			
			this.deviceIds =  resolveDevices(result);
		}
		return result;
	}

	/**
	 * {@inheritDoc} 
	 *
	 */
	@Override
	public List<Device> getDevices(boolean allProperties) throws RTException {
		
		List<Device> result = new ArrayList <Device>();
		
		try{
			
			if (this.deviceIds == null){ //No cached.
				this.deviceIds = getDeviceIds();
			}
			
			for (String deviceId: deviceIds){
				
				try{
				
					DeviceRTD dev = (DeviceRTD) getRTD(deviceId);
					if (dev == null){
						String[] names = {"deviceId"}; 
						String[] values = {deviceId};
						LogUtil.severe(this, deviceId + "DeviceDiscoverer.getDevices::Impossible to resolve the deviceId." + LogUtil.getLog(names, values));
					}else{
						result.add(dev.devGetDevice(allProperties));
					}
				
				}catch(Exception ex){
					String[] names = {"deviceId"}; 
					String[] values = {deviceId};
					LogUtil.severe(this, deviceId + "DeviceDiscoverer.getDevices::Error resolving the deviceId." + LogUtil.getLog(names, values));
					
					throw ex;
				}
				
			}
			
			return result;
			
		}catch(Exception ex){
			throw new RTException(ex.getMessage());
		}
	}

	/**
	 * {@inheritDoc} 
	 *
	 */
	@Override
	public Device getDevice(String deviceId, boolean allProperties)
			throws RTException {

		DeviceRTD dev =  (DeviceRTD) getRTD(deviceId);
		return dev.devGetDevice(allProperties);
		
	}

	/**
	 * {@inheritDoc} 
	 *
	 */
	@Override
	public Device getDevice(String deviceId, List<String> propertyNames)
			throws RTException {
		
		DeviceRTD dev =  (DeviceRTD) getRTD(deviceId);
		return dev.getDevice(propertyNames);
	}

	/**
	 * {@inheritDoc} 
	 *
	 */
	@Override
	public RTDDeviceInterface getRTD(String deviceId) throws RTException {
		
		DeviceRTD genericRTD = null;
		boolean associatedDev = false;
		String[] parentDevName = null;
		
		String devType = null;		
		String devSubtype = null; 				
		
		try{
			
			//Is it in cache?
			RTDDeviceInterface cachedRtd = (RTDDeviceInterface)CacheManager.getCache(CACHE_NAME_RTDS).getObject(deviceId, null);
			if (cachedRtd != null){
				return cachedRtd;
			}
		
			//Check if the device is real device
			try{
				//real device: information provided through .xml
				devType = configDeviceManager.getDevice(deviceId).getType().toString();				
				devSubtype = configDeviceManager.getDevice(deviceId).getSubtype();		
				
				String[] names = {
						"devType",
						"devSubType"
				};
				
				String[] values = {
						devType,
						devSubtype
				};
				
				LogUtil.info(this, "DeviceDiscoverer. Resolving RTD class with this information::" + LogUtil.getLog(names, values));
				
			}catch (Exception e){
				//if (e.getMessage().contains("Server returned HTTP response code: 400")){	//Possible associated device
					associatedDev = true;
					
					parentDevName = deviceId.split("_");
					
					if (parentDevName.length>2){
						
						parentDevName[0]=parentDevName[0]+"_"+parentDevName[1];
					}
					
					devType = configDeviceManager.getDevice(parentDevName[0]).getType().toString();				
					devSubtype = configDeviceManager.getDevice(parentDevName[0]).getSubtype();
				//}
			}			
			
			HierarchicalINIConfiguration iniConfObj = null;
			try {
				iniConfObj = new HierarchicalINIConfiguration("rtd_rts2_discovery.ini");
			} catch (ConfigurationException e) {
				e.printStackTrace();
				throw new RTException("DeviceDiscoverer. Impossible to load the file: rtd_rts2_discovery.ini");
			}
			
			String className = (String) iniConfObj.getProperty(devType+"."+devSubtype);
			
			//Build the instance.
			try{
				
				LogUtil.info(this, "DeviceDiscoverer. Introspection class: " + className);
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
				throw new RTException("Error reflexing the RTD class"+e.getCause().getMessage());
			}		
			
			CacheManager.getCache(CACHE_NAME_RTDS).putObject(deviceId, genericRTD);
			
		}catch(Exception ex){
			throw new RTException(ex.getMessage());
		}		
		
		return genericRTD;
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
		
		LogUtil.info(this,"Resolving associated devices. BEGIN");

		DeviceRTD genericRTD = null;
		
		String devType = null;				
		String devSubtype = null;
		String className = null;
		
		HierarchicalINIConfiguration iniConfObj = null;
		try {
			iniConfObj = new HierarchicalINIConfiguration("rtd_rts2_discovery.ini");
		} catch (ConfigurationException e) {
			e.printStackTrace();
			throw new RTException("DeviceDiscoverer. Impossible to load the file: rtd_rts2_discovery.ini");
		}
		
		for (String deviceId: names){
			if (!deviceId.isEmpty()){
				
				devType = configDeviceManager.getDevice(deviceId).getType().toString();				
				devSubtype = configDeviceManager.getDevice(deviceId).getSubtype();
				
				
				className = (String) iniConfObj.getProperty(devType+"."+devSubtype);
				
				String[] namesLog = {
						"devType",
						"devSubType",
						"className"
				};
				
				String[] valuesLog = {
						devType,
						devSubtype,
						className
				};
				
				LogUtil.info(this, "DeviceDiscoverer. Resolving RTD class with this information::" + LogUtil.getLog(namesLog, valuesLog));
				
				try{
					Class<?> cls = Class.forName(className);
					Constructor<?> ct = cls.getConstructor();			
					genericRTD = (DeviceRTD) ct.newInstance();
					genericRTD.setDeviceId(deviceId);
					
					CacheManager.getCache(CACHE_NAME_RTDS).putObject(deviceId, genericRTD);
				}catch (Exception e){
					throw new RTException("Error reflexing the RTD class"+e.getMessage());
				}
				
				try{
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
				}catch (Exception e){
					if (!e.getMessage().contains("The device does not exist"))
						throw new RTException(e.getMessage());
						
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

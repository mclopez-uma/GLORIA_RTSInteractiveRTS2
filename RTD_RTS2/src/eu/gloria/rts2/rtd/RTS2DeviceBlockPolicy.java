package eu.gloria.rts2.rtd;

import java.util.ArrayList;
import java.util.List;
import java.util.MissingResourceException;
import java.util.StringTokenizer;

import eu.gloria.rt.entity.device.BlockState;
import eu.gloria.rt.entity.device.Device;
import eu.gloria.rt.entity.device.DeviceType;
import eu.gloria.rt.exception.RTException;
import eu.gloria.rtc.DeviceDiscoverer;
import eu.gloria.rts2.http.Rts2CommunicationException;
import eu.gloria.rts2.http.Rts2Exception;
import eu.gloria.rts2.http.Rts2GatewayDeviceManager;
import eu.gloria.rts2.http.Rts2GatewayDevicePropertiesRequest;
import eu.gloria.rts2.http.Rts2GatewayDevicePropertiesRequest.RequestType;
import eu.gloria.tools.cache.Cache;
import eu.gloria.tools.cache.CacheManager;
import eu.gloria.tools.configuration.Config;
import eu.gloria.tools.log.LogUtil;

/**
 * Block policy for RTS2. See: rtd_rts2_device_block_policy.properties
 * 
 * 
 * @author jcabello
 * 
 */
public class RTS2DeviceBlockPolicy {
	
	/**
	 * The block dependencies between devices is cached. This is the name into the cache repository.
	 */
	private static String CACHE_DEVICE_BLOCK_DEPS = "CACHE_DEVICE_BLOCK_DEPS";
	
	static{
		//Creates the cache
		CacheManager.createCache(CACHE_DEVICE_BLOCK_DEPS, -1, null);
	}
	
	/**
	 * Returns true if the device is blocked because of some dependency.
	 * @param deviceId Device identifier.
	 * @return Block state.
	 * @throws RTException In error case.
	 */
	public BlockState getDependencyBlockState(String deviceId) throws RTException {
		
		try {

			Rts2GatewayDeviceManager manager = new Rts2GatewayDeviceManager();
			
			
			//Dependencies, device ids...
			List<RTS2DeviceBlockPolicyDependency> deps = getDevBlockDependecies(deviceId, false);
			
			for (RTS2DeviceBlockPolicyDependency dep : deps) {
				Rts2GatewayDevicePropertiesRequest propReq = new Rts2GatewayDevicePropertiesRequest(RequestType.NO_PROPERTY, null);
				Device dev = manager.getDevice(dep.getDepDevId(), propReq);
				if (dep.containsDepBlockState(dev.getBlockState())){
					return dep.getTargetBlockState();
				}
			}
			
			//Dependencies, device types...
			deps = getDevBlockDependecies(deviceId, true);
			
			if (deps.size() > 0){
				
			
				for (RTS2DeviceBlockPolicyDependency dep : deps) {
					ArrayList<String> names = manager.getDevicesNames(dep.getDepDevType()); 
					for (String name : names) {
						Device dev = (Device) DeviceDiscoverer.devGetDevice(name, false);
						//if (dev.getType() == dep.getDepDevType() && !dev.getShortName().equals(deviceId)) {
							if (dep.containsDepBlockState(dev.getBlockState())){
								return dep.getTargetBlockState();
							}
						//}
					}
				}
			}

			return BlockState.UNBLOCK; //There is not any block dependency.

		} catch (Rts2CommunicationException e) {
			throw new RTException(e);
		} catch (Rts2Exception e) {
			throw new RTException(e);
		}
	}

	
	/**
	 * Recovers the configuration block policy dependencies.
	 * @param deviceId Device Identifier
	 * @param basedOnDevType true if the searching is based on Device type mode.
	 * @return the configuration block policy dependencies for the device.
	 * @throws RTException In error case.
	 */
	private List<RTS2DeviceBlockPolicyDependency> getDevBlockDependecies(String deviceId, boolean basedOnDevType) throws RTException {
		
		

		List<RTS2DeviceBlockPolicyDependency> result = null;

		try {

			String idDepsKey = deviceId + ".dev_id_deps";
			if (basedOnDevType){
				idDepsKey = deviceId + ".dev_type_deps";
			}
			
			Cache cache = CacheManager.getCache(CACHE_DEVICE_BLOCK_DEPS);
			result = (List<RTS2DeviceBlockPolicyDependency>) cache.getObject(idDepsKey, null);
			
			if (result == null){ //Not in cache
				
				result = new ArrayList<RTS2DeviceBlockPolicyDependency>();
				
				String depsList = Config.getProperty("rtd_rts2_device_block_policy", idDepsKey);
				StringTokenizer st = new StringTokenizer(depsList, Config.getProperty("rtd_rts2_device_block_policy","dev_dep_separator"));

				while (st.hasMoreTokens()) {
					
					result.add(new RTS2DeviceBlockPolicyDependency(basedOnDevType, st.nextToken(), 
							Config.getProperty("rtd_rts2_device_block_policy","dev_dep_components_separator"), 
							Config.getProperty("rtd_rts2_device_block_policy","dev_dep_state_separator")));
				}
				
				cache.putObject(idDepsKey, result);
			}
			

		} catch (Exception ex) {

			String[] names = { "deviceId", "basedOnDevType" };
			String[] values = { deviceId, String.valueOf(basedOnDevType) };

			String message = LogUtil.getLog(names, values) + "Error searching devices block dependencies.";
			LogUtil.severe(this, message);

		}

		return result;

	}

}

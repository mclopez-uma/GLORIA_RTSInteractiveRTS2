package eu.gloria.rts2.http;


import java.util.HashMap;

import java.util.Map;

import org.codehaus.jackson.map.ObjectMapper;

import eu.gloria.rt.entity.device.DeviceType;
import eu.gloria.rt.exception.RTException;
import eu.gloria.rtc.environment.config.device.ConfigDeviceManager;
import eu.gloria.rts2.rtd.DeviceDiscoverer;
import eu.gloria.tools.cache.ICacheRetriever;
import eu.gloria.tools.configuration.Config;
import eu.gloria.tools.log.LogUtil;

/**
 * Cache retriever for Decive types.
 * 
 * @author jcabello
 *
 */
public class Rts2GatewayDeviceTypeCacheRetriever implements ICacheRetriever {

	/**
	 * Device configuration list manager.
	 */
	protected static ConfigDeviceManager configDeviceManager;
	
	/**
	 * {@inheritDoc}
	 * <p>
	 * RETRIEVER PARAMS:
	 *  -DEV_ID (String): Device identifier.
	 *  
	 * RETURNS: DeviceType
	 */
	@Override
	public Object retrieve(Map<String, Object> params) throws RTException {
		
		DeviceType devType = null;
		String devId = (String)params.get("DEV_ID");
		
		String factoryClassName = Config.getProperty("rt_config", "device.discoverer.provider");
		
		if (factoryClassName.equals("eu.gloria.rts2.rtd.RTS2DeviceDiscovery")){
			//Auto

			LogUtil.info(this, ">>>>>>>>>>>>>>Resolving device type. BEGIN");
			Rts2Cmd cmd = Rts2Cmd.getNewCmd(Rts2CmdType.deviceinfo);
			cmd.getParameters().put("d", devId);
			String jsonContent = cmd.execute();
			LogUtil.info(this, ">>>>>>>>>>>>>>Resolving device type. END");
			
			try{
				ObjectMapper mapper = new ObjectMapper();
				HashMap<String, Object> data = (HashMap<String, Object>) mapper.readValue(jsonContent, Object.class);
				//HashMap data = (HashMap)JsonObj.fromJson(jsonContent);
				devType = Rts2GatewayTools.resolveDeviceType((Integer)data.get("type"));

			}catch(Exception ex){
				throw new Rts2Exception("Error recovering the device list. " + ex.getMessage());
			}
		}else{
			//XML file		
			
			LogUtil.info(this, ">>>>>>>>>>>>>>Resolving device type. BEGIN");
			
			try{
				
				configDeviceManager = new ConfigDeviceManager();
				
			}catch(Exception ex){
				LogUtil.severe(null, "Rts2GatewayDeviceTypeCacheRetriever. static initialization. Error loading the device list XML." + ex.getMessage());
				ex.printStackTrace();
			}
			
			if (configDeviceManager.getDeviceList() != null && configDeviceManager.getDeviceList().getDevice() != null){
				
				for (eu.gloria.rt.entity.environment.config.device.Device dev : configDeviceManager.getDeviceList().getDevice()) {
					if(dev.getShortName().equals(devId)){
						devType = DeviceType.fromValue(dev.getType().toString()) ;
						break;
					}
				}				
				
			}
			
			LogUtil.info(this, ">>>>>>>>>>>>>>Resolving device type. END");			
		}
		
		return devType;
		
		
	}

}

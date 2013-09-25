package eu.gloria.rts2.http;

import java.util.List;

import eu.gloria.rt.entity.device.DeviceType;
import eu.gloria.rt.exception.RTException;


/**
 * Tools for manipulating Json content.
 * 
 * @author jcabello
 * 
 */
public class Rts2GatewayTools {

	public static String PATH_SEPARATOR = "/";
	
	
	
	public static boolean existDeviceName (String name) throws RTException{
		try {

			Rts2GatewayDeviceManager gatewayDevManager = new Rts2GatewayDeviceManager();	
			List<String> devNames = gatewayDevManager.getDevicesNames();
			return devNames.contains(name);
			
		} catch (Exception ex) {
			RTException newEx = new RTException(ex);
			throw newEx;
		}
	}
	
	/**
	 * Resolves the Entity device type.
	 * @param type RTS2 device type.
	 * @return DeciveType
	 * @throws Rts2Exception In error case.
	 */
	public static DeviceType resolveDeviceType(int type) throws Rts2Exception{
		
		DeviceType result;
		
		switch(type){
		case 0:
			result = DeviceType.UNKNOWN;
			break;
		case 1:
			result = DeviceType.SERV_SERVERD;
			break;
		case 2:
			result = DeviceType.MOUNT;
			break;
		case 3:
			result = DeviceType.CCD;
			break;
		case 4:
			result = DeviceType.DOME;
			break;
		case 5:
			result = DeviceType.GENERIC_WEATHER_SENSOR;
			break;
		case 6:
			result = DeviceType.ROTATOR;
			break;
		case 7:
			result = DeviceType.PHOTOMETER;
			break;
		case 8:
			result = DeviceType.SERV_PLAN;
			break;
		case 9:
			result = DeviceType.SERV_GRB;
			break;
		case 10:
			result = DeviceType.FOCUS;
			break;
		case 11:
			result = DeviceType.MIRROR;
			break;
		case 12:
			result = DeviceType.CUPOLA;
			break;
		case 13:
			result = DeviceType.FW;
			break;
		case 14:
			result = DeviceType.SERV_AUGERSH;
			break;
		case 15:
			result = DeviceType.GENERIC_SENSOR;
			break;
		case 20:
			result = DeviceType.SERV_EXECUTOR;
			break;
		case 21:
			result = DeviceType.SERV_IMGPROC;
			break;
		case 22:
			result = DeviceType.SERV_SELECTOR;
			break;
		case 23:
			result = DeviceType.SERV_XMLRPC;
			break;
		case 24:
			result = DeviceType.SERV_INDI;
			break;
		case 25:
			result = DeviceType.SERV_LOGD;
			break;
		case 26:
			result = DeviceType.SERV_SCRIPTOR;
			break;
		default:
			throw new Rts2Exception("Impossible to resolve the device type. type=" + type);
		
		}
		
		return result;
		
	}
	
		
	
	/*public static ArrayList<Object> getVariable(String path, Object node)
			throws Rts2Exception {

		if (node == null)
			return null;

		StringTokenizer st = new StringTokenizer(path, PATH_SEPARATOR);
		while (st.hasMoreTokens()) {

			String token = st.nextToken();

			if (!(node instanceof Map<?, ?>)) {
				throw new Rts2Exception(
						"RTS2 impossible to reach the selected node. Path="
								+ path);
			}

			Map<String, Object> map = (Map<String, Object>) node;
			node = map.get(token);
		}

		if (!(node instanceof ArrayList<?>)) {
			throw new Rts2Exception("RTS2 node is not a Object[].");
		}

		return (ArrayList<Object>) node;

	}*/

	

	/*
	 * public static String getDevConfiguration(Object node){
	 * 
	 * StringBuilder sb = new StringBuilder(); generateDevConfiguration(node,
	 * sb); return sb.toString();
	 * 
	 * }
	 * 
	 * private static void generateDevConfiguration(Object node, StringBuilder
	 * sb){
	 * 
	 * if (node != null){
	 * 
	 * if (!(node instanceof Map<?, ?>)){
	 * 
	 * Map<String, Object> map = (Map<String, Object>) node;
	 * 
	 * for (String key : map.keySet()) { sb.append(key).append("=");
	 * 
	 * }
	 * 
	 * StringTokenizer st = new StringTokenizer(path, PATH_SEPARATOR); while
	 * (st.hasMoreTokens()){
	 * 
	 * String token = st.nextToken();
	 * 
	 * if (!(node instanceof Map<?, ?>)){ throw new
	 * Rts2Exception("RTS2 impossible to reach the selected node. Path=" +
	 * path); }
	 * 
	 * Map<String, Object> map = (Map<String, Object>) node; node =
	 * map.get(token); }
	 * 
	 * }
	 * 
	 * if (node instanceof ArrayList<?>){
	 * 
	 * }
	 * 
	 * } }
	 * 
	 * private static void generateDevParamValue(ArrayList<Object> node,
	 * StringBuilder sb){
	 * 
	 * 
	 * }
	 */

}

package eu.gloria.rts2.rtd;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import eu.gloria.tools.cache.ICacheRetriever;

/**
 * Class for caching device information.
 * @author jcabello
 *
 */
public class CachedDeviceInfo implements ICacheRetriever {
	
	/**
	 * Connected flag.
	 */
	private boolean connected;
	
	/**
	 * General repository information
	 */
	private static Map<Object, Object> attributes = Collections.synchronizedMap(new  HashMap<Object, Object>());

	
	/**
	 * Constructor.
	 */
	public CachedDeviceInfo(){
		connected = false;
	}

	
	@Override
	public Object retrieve(Map<String, Object> params) {
		
		return new CachedDeviceInfo();
	}

	/**
	 * Access method
	 * @return boolean
	 */
	public boolean isConnected() {
		return connected;
	}

	/**
	 * Access method
	 * @param connected value
	 */
	public void setConnected(boolean connected) {
		this.connected = connected;
	}
	
	/**
	 * Recover a device attribute
	 * @param key Key
	 * @return Object.
	 */
	public Object getAttribute(Object key){
		return attributes.get(key);
	}
	
	/**
	 * Stores a device attribute
	 * @param key Key
	 * @param value Attribute value.
	 */
	public void putAttribute(Object key, Object value){
		attributes.put(key, value);
	}

}

package eu.gloria.rts2.http;

import java.util.List;

/**
 * Request object for accessing to a RTS2 device.
 * 
 * @author jcabello
 *
 */
public class Rts2GatewayDevicePropertiesRequest {
	
	public enum RequestType{
		ALL_PROPERTIES,
		NO_PROPERTY,
		CUSTOM
	}
	
	private RequestType type;
	private List<String> propertyNames;
	
	/**
	 * Constructor.
	 * @param type Request type
	 * @param propertyNames Properties names
	 */
	public Rts2GatewayDevicePropertiesRequest(RequestType type, List<String> propertyNames){
		
		this.type = type;
		this.propertyNames = propertyNames;
	}

	public RequestType getType() {
		return type;
	}

	public void setType(RequestType type) {
		this.type = type;
	}

	public List<String> getPropertyNames() {
		return propertyNames;
	}

	public void setPropertyNames(List<String> propertyNames) {
		this.propertyNames = propertyNames;
	}
	

}

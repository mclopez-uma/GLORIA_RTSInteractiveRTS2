package eu.gloria.rts2.http;

import java.math.BigInteger;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Logger;

import org.codehaus.jackson.map.ObjectMapper;

import eu.gloria.rts2.http.Rts2GatewayDevicePropertiesRequest.RequestType;

/**
 * Wraps the Command GET response.
 * 
 * @author jcabello
 *
 */
public class Rts2CmdGetResponse {
	
	private static DecimalFormatSymbols dfs;
	
	static{
		
		//Sets the decimal format to . to make a string value
		dfs = new DecimalFormatSymbols();
		dfs.setDecimalSeparator('.');
	}
	
	/**
	 * Constructor.
	 * 
	 * @param jsonContent Json Content.
	 * @param propReq Properties name.
	 * @throws Rts2Exception In error case.
	 */
	public Rts2CmdGetResponse(String jsonContent, Rts2GatewayDevicePropertiesRequest propReq) throws Rts2Exception{
		
		try{
			vars = new ArrayList<JsonVariable>();
			minmax = new  ArrayList<JsonGenericVariable>(); 
		
			ObjectMapper mapper = new ObjectMapper();
			HashMap<String, Object> info = (HashMap<String, Object>) mapper.readValue(jsonContent, Object.class);
		
			Logger.getLogger(this.getClass().getName()).info("[JSON RESP::CMD GET]. JSON content converted to Objects.");
		
			buildJsonVariables(info, propReq);
			
			buildAdditionalInfo(info);
			
		}catch(Exception ex){
			throw new Rts2Exception("[JSON RESP::CMD GET]. Error analyzing the jsonContent. " + ex.getMessage());
		}
		
	}
	
	/**
	 * Json Variables
	 */
	private ArrayList<JsonVariable> vars;
	
	private ArrayList<JsonGenericVariable> minmax;
	
	private long state;
	private boolean idle;

	/**
	 * Access method.
	 * 
	 * @return List of variables
	 */
	public ArrayList<JsonVariable> getVars() {
		return vars;
	}

	/**
	 * Access method.
	 * 
	 * @param vars list of variables
	 */
	public void setVars(ArrayList<JsonVariable> vars) {
		this.vars = vars;
	}
	
	/**
	 * Returns the variables in the same path.
	 * @param path Considered path
	 * @return List of variables.
	 */
	public List<JsonVariable> getVariables(String path){
		
		List<JsonVariable> result = new ArrayList<JsonVariable>();
		
		for (JsonVariable jsonVariable : vars) {
			if (path.equals(jsonVariable.getPath())){
				result.add(jsonVariable);
			}
		}
		
		return result;
	}
	
	/**
	 * Returns the variables in the same path.
	 * @param path Considered path
	 * @return List of variables.
	 */
	public JsonVariable getVariable(String path, String name){
		
		JsonVariable result = null;
		
		for (JsonVariable jsonVariable : vars) {
			if (path.equals(jsonVariable.getPath()) && name.equals(jsonVariable.getName())){
				result = jsonVariable;
			}
		}
		
		return result;
	}
	
	/**
	 * Returns the minmax information.
	 * @return List of variables.
	 */
	public List<JsonGenericVariable> getMinmax(){
		
		return this.minmax;
	}
	
	/**
	 * Returns the minmax with a proper name.
	 * @param name minmax name
	 * @return JsonGenericVariable wrapper.
	 */
	public JsonGenericVariable getMinmax(String name){
		
		JsonGenericVariable result = null;
		
		for (JsonGenericVariable minmax : this.minmax) {
			if (minmax.getName().equals(name)){
				result = minmax;
				break;
			}
		}
		
		return result;
	}
	
	/**
	 * Private method for filling this object using the json content information
	 * @param node Json data
	 * @param propReq List of properties.
	 * @throws Rts2Exception In error case.
	 */
	private void buildJsonVariables(Object node, Rts2GatewayDevicePropertiesRequest propReq)
			throws Rts2Exception {
		
		buildJsonContainer(JsonVariable.PATH_SEPARATOR, "", node, propReq);

	}
	
	/**
	 * Private method for recovering additional information
	 * @param node Json data
	 * @throws Rts2Exception In error case.
	 */
	private void buildAdditionalInfo(Object node)
			throws Rts2Exception {
		
		if (node != null) {

			if ((node instanceof Map<?, ?>)) { // set of variables
				Map<String, Object> map = (Map<String, Object>) node;
				
				//DevState
				this.state = Long.parseLong(map.get("state").toString());
				
				//Dev idle
				long idle = Long.parseLong(map.get("state").toString());
				this.idle = (idle == 1);
				
				//MinMax
				Map<String, Object> minmaxMap = (Map<String, Object>) map.get("minmax");
				if (minmaxMap != null){
					
					for (String key : minmaxMap.keySet()) {
						ArrayList<Object> interval = (ArrayList<Object>) minmaxMap.get(key);
						
						JsonGenericVariable genericVar = new JsonGenericVariable();
						genericVar.setPath(JsonGenericVariable.PATH_SEPARATOR + "minmax");
						genericVar.setName(key);
						genericVar.setValue(interval);
						
						this.minmax.add(genericVar);
					}
					
				}
				
			}else{
				
				throw new Rts2Exception("There is not root json content.");
				
			}
		}else{
			
			throw new Rts2Exception("There is not root json content.");
		}

	}

	/**
	 * Private method for filling this object using the json content information. Recursive implementation.
	 * 
	 * @param String currentPath Path up to current node (separator:JsonVariable.PATH_SEPARATOR) 
	 * @param currentComponent name of the current component.
	 * @param node Json data.
	 * @param propReq List of properties.
	 * @throws Rts2Exception In error case.
	 */
	public void buildJsonContainer(String currentPath,
			String currentComponent, 
			Object node, Rts2GatewayDevicePropertiesRequest propReq) throws Rts2Exception {
		
		if (node != null) {

			if ((node instanceof Map<?, ?>)) { // set of variables

				Map<String, Object> map = (Map<String, Object>) node;

				for (String key : map.keySet()) {
					buildJsonContainer(currentPath + currentComponent, key,	map.get(key), propReq);
				}
			}

			if (node instanceof ArrayList<?>) { // Variable		
				
				
				if (!currentPath.equals("/d")) return; //only nodes from path "/d"
				
				if (propReq != null && (propReq.getType() == RequestType.ALL_PROPERTIES || (propReq.getType() == RequestType.CUSTOM && propReq.getPropertyNames().contains(currentComponent)))){
					
					//the property must be include.

					Logger.getLogger(Rts2GatewayTools.class.getName()).info(
							"JSON Processing.... variable="
								+ currentComponent);

					ArrayList<Object> arr = (ArrayList<Object>) node;
					JsonVariable var = new JsonVariable();
					var.setName(currentComponent);
					var.setPath(currentPath);
					
					if (arr.size() > 4) { // it has description
						var.setDesc(arr.get(4).toString());
					}
					
					int valuePositionInArray = 1;
					if (arr.size() == 1) { //Only it has values.
						valuePositionInArray = 0;
					}else{
						var.setFlags( new Long(arr.get(0).toString()));
					}
					
					// 	Value
					ArrayList<String> value = new ArrayList<String>();
					if (arr.get(valuePositionInArray) != null) {

						if (arr.get(valuePositionInArray) instanceof ArrayList<?>) { // The value
							// is an
							// array
							ArrayList<Object> tmpValArr = (ArrayList<Object>) arr
									.get(valuePositionInArray);
							if (tmpValArr != null) {
								for (int x = 0; x < tmpValArr.size(); x++) {
									if (tmpValArr.get(x) != null) {
										value.add(formatValue(tmpValArr.get(x)));
									}
								}
							}

						} else if (arr.get(valuePositionInArray) instanceof  Map<?, ?>){ //MAP -->Radec or ALTZ value
						
							Map<String, Object> tmpMap = (Map<String, Object>) arr.get(valuePositionInArray);
							Collection<Object> values = tmpMap.values();
							for (Object valueTmp : values){ 
								if (valueTmp == null){
									value.add(null);
								}else{
									value.add(valueTmp.toString());
								}
							}  
							
						} else { // The value is simple
							value.add(formatValue(arr.get(valuePositionInArray)));
						}
					}
					
					var.setValue(value);
				
					getVars().add(var);
				
				}
				
				//---------------
			}
		}

	}
	
	
	/**
	 * Format de value to String
	 * 
	 * JSON TYPE		JAVA TYPE
	 * ---------		---------
	 * string			String
	 * numbre no frac.	Integer, Long or BigInteger (smaller applicable)
	 * number frac.		Double (configurable BigDecimal)
	 * true|false		Boolean	
	 * null				null
	 * 
	 * @param value Json value
	 * @return String value
	 */
	private String formatValue(Object value){
		
		String result = null;
		
		if (value != null){
		
			if (value instanceof String){
				result = (String) value;
			}else if (value instanceof Integer){
				result = value.toString();
			}else if (value instanceof Long){
				result = value.toString();
			}else if (value instanceof BigInteger){
				result = value.toString();
			}else if (value instanceof Double){
				DecimalFormat df = new DecimalFormat("#0.0##############################");
				df.setDecimalFormatSymbols(dfs);
				result = df.format((Double)value);
			}else if (value instanceof Boolean){
				result = value.toString();
			}
		}
			
		
		return result;
	}

	public long getState() {
		return state;
	}


	public boolean isIdle() {
		return idle;
	}


}

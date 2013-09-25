package eu.gloria.rts2.http;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents a generic Json variable.
 * 
 * @author jcabello
 *
 */
public class JsonGenericVariable {
	
	public static String PATH_SEPARATOR = "/";

	private String path;
	private String name;
	private List<Object> value;
	
	/**
	 * Constructor
	 */
	public JsonGenericVariable(){
		path = "";
		name = "";
		value = new ArrayList();
	}
	
	/**
	 * Returns path + varname
	 * @return String
	 */
	public String getId(){
		return path + PATH_SEPARATOR + name;
	}
	
	/**
	 * Access method.
	 * @return Value.
	 */
	public String getPath() {
		return path;
	}
	
	/**
	 * Access method.
	 * @param path Value
	 */
	public void setPath(String path) {
		this.path = path;
	}
	
	/**
	 * Access method.
	 * @return Value.
	 */
	public String getName() {
		return name;
	}
	
	/**
	 * Access method.
	 * @param name Name
	 */
	public void setName(String name) {
		this.name = name;
	}
	
	/**
	 * Returns the list of values.
	 * @return Values.
	 */
	public List<Object> getValue() {
		return value;
	}
	
	/**
	 * Sets the list of values.
	 * @param value Values
	 */
	public void setValue(List<Object> value) {
		this.value = value;
	}

	
}
